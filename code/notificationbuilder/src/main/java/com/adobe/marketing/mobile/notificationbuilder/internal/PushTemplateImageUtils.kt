/*
  Copyright 2024 Adobe. All rights reserved.
  This file is licensed to you under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License. You may obtain a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0
  Unless required by applicable law or agreed to in writing, software distributed under
  the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR REPRESENTATIONS
  OF ANY KIND, either express or implied. See the License for the specific language
  governing permissions and limitations under the License.
*/

package com.adobe.marketing.mobile.notificationbuilder.internal

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.RectF
import androidx.core.graphics.createBitmap
import com.adobe.marketing.mobile.notificationbuilder.PushTemplateConstants
import com.adobe.marketing.mobile.notificationbuilder.PushTemplateConstants.LOG_TAG
import com.adobe.marketing.mobile.services.HttpConnecting
import com.adobe.marketing.mobile.services.HttpMethod
import com.adobe.marketing.mobile.services.Log
import com.adobe.marketing.mobile.services.NetworkCallback
import com.adobe.marketing.mobile.services.NetworkRequest
import com.adobe.marketing.mobile.services.ServiceProvider
import com.adobe.marketing.mobile.services.caching.CacheEntry
import com.adobe.marketing.mobile.services.caching.CacheExpiry
import com.adobe.marketing.mobile.services.caching.CacheService
import com.adobe.marketing.mobile.util.UrlUtils
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference
import kotlin.math.max

/**
 * Utility functions to assist in downloading and caching images for push template notifications.
 */

internal object PushTemplateImageUtils {
    private const val SELF_TAG = "PushTemplateImageUtil"
    private const val FULL_BITMAP_QUALITY = 100
    private const val DOWNLOAD_TIMEOUT_SECS = 10

    /**
     * Downloads and caches images provided in the [urlList]. Prior to downloading, the image url
     * is used to retrieve a previously cached image using [CacheService].
     * If a valid cache result is returned then no image is downloaded.
     * If no cache result is returned, a call to [downloadImage] is made to download then cache the image.
     *
     * This is a blocking method that returns only after the download for all images
     * have finished either by failing or successfully downloading, or the timeout has been reached.
     *
     * @param urlList [String] containing an image asset url
     * @return [Int] number of images that were found in cache or successfully downloaded
     */
    internal fun cacheImages(
        urlList: List<String?>
    ): Int {
        val assetCacheLocation = getAssetCacheLocation()
        if (urlList.isEmpty() || assetCacheLocation.isNullOrEmpty()) {
            return 0
        }

        val cacheService = ServiceProvider.getInstance().cacheService
        val downloadedImageCount = AtomicInteger(0)
        val latchAborted = AtomicBoolean(false)
        val latch = CountDownLatch(urlList.size)
        for (url in urlList) {
            if (url == null || !UrlUtils.isValidUrl(url)) {
                latch.countDown()
                continue
            }

            val cacheResult = cacheService[assetCacheLocation, url]
            if (cacheResult != null) {
                Log.trace(
                    LOG_TAG,
                    SELF_TAG,
                    "Found cached image for $url"
                )
                downloadedImageCount.incrementAndGet()
                latch.countDown()
                continue
            }

            downloadImage(url) { connection ->
                try {
                    if (!latchAborted.get()) {
                        val image = handleDownloadResponse(url, connection)
                        // scale the decoded bitmap into the bounding box (see scaleBitmap) as we
                        // don't want to keep a full size image in memory
                        image?.let {
                            val pushImage = scaleBitmap(it)
                            // write bitmap to cache
                            try {
                                bitmapToInputStream(pushImage).use { bitmapInputStream ->
                                    cacheBitmapInputStream(
                                        cacheService,
                                        bitmapInputStream,
                                        url
                                    )
                                }
                                downloadedImageCount.incrementAndGet()
                            } catch (exception: IOException) {
                                Log.warning(
                                    LOG_TAG,
                                    SELF_TAG,
                                    "Exception occurred creating an input stream from a bitmap for {$url}: ${exception.localizedMessage}."
                                )
                            }
                        }
                    }
                } catch (throwable: Throwable) {
                    // Guard against any unexpected failure (including OutOfMemoryError from image
                    // decoding/scaling). This callback runs on a network thread, so an uncaught
                    // error here would otherwise skip the latch countdown (blocking the caller
                    // until timeout) and leak the connection.
                    Log.warning(
                        LOG_TAG,
                        SELF_TAG,
                        "Unexpected error while processing the downloaded image for {$url}: ${throwable.localizedMessage}."
                    )
                } finally {
                    latch.countDown()
                    connection?.close()
                }
            }
        }
        try {
            if (latch.await(DOWNLOAD_TIMEOUT_SECS.toLong(), TimeUnit.SECONDS)) {
                Log.trace(
                    LOG_TAG,
                    SELF_TAG,
                    "All image downloads have completed."
                )
            } else {
                Log.warning(
                    LOG_TAG,
                    SELF_TAG,
                    "Timed out waiting for image downloads to complete."
                )
                latchAborted.set(true)
            }
        } catch (e: InterruptedException) {
            Log.warning(
                LOG_TAG,
                SELF_TAG,
                "Interrupted while waiting for image downloads to complete: ${e.localizedMessage}"
            )
            latchAborted.set(true)
        }
        return downloadedImageCount.get()
    }

    /**
     * Initiates a network request to download the image provided by the url `String`.
     *
     * @param url [String] containing the image url to download
     * @param completionCallback callback to be invoked with the [HttpConnecting] object
     * when download is complete
     */
    private fun downloadImage(
        url: String,
        completionCallback: (HttpConnecting?) -> Unit
    ) {
        val networkRequest = NetworkRequest(
            url,
            HttpMethod.GET,
            null,
            null,
            DOWNLOAD_TIMEOUT_SECS,
            DOWNLOAD_TIMEOUT_SECS
        )

        val networkCallback = NetworkCallback { connection: HttpConnecting? ->
            completionCallback.invoke(connection)
        }

        ServiceProvider.getInstance()
            .networkService
            .connectAsync(networkRequest, networkCallback)
    }

    /**
     * Retrieves an image from the cache using the provided url `String`.
     *
     * @param url [String] containing the image url to retrieve from cache
     * @return [Bitmap] containing the image retrieved from cache, or `null` if no image is found
     */
    internal fun getCachedImage(url: String?): Bitmap? {
        val assetCacheLocation = getAssetCacheLocation()
        if (url == null || !UrlUtils.isValidUrl(url) || assetCacheLocation.isNullOrEmpty()) {
            return null
        }
        val cacheResult = ServiceProvider.getInstance().cacheService[assetCacheLocation, url]
        if (cacheResult == null) {
            Log.warning(LOG_TAG, SELF_TAG, "Image not found in cache for $url")
            return null
        }
        Log.trace(LOG_TAG, SELF_TAG, "Found cached image for $url")
        return BitmapFactory.decodeStream(cacheResult.data)
    }

    private fun handleDownloadResponse(url: String?, connection: HttpConnecting?): Bitmap? {
        if (connection == null) {
            Log.warning(
                LOG_TAG,
                SELF_TAG,
                "Failed to download push notification image from url ($url), received a null connection."
            )
            return null
        }
        if ((connection.responseCode != HttpURLConnection.HTTP_OK)) {
            Log.debug(
                LOG_TAG,
                SELF_TAG,
                "Failed to download push notification image from url ($url). Response code was: ${connection.responseCode}."
            )
            return null
        }
        return try {
            // Read the compressed bytes once, then decode with a sample size so a very large
            // source image is never fully decoded into memory (avoids OutOfMemoryError).
            val imageBytes = connection.inputStream?.readBytes()
            if (imageBytes == null || imageBytes.isEmpty()) {
                Log.warning(
                    LOG_TAG,
                    SELF_TAG,
                    "Failed to read image bytes from url ($url)."
                )
                return null
            }
            decodeSampledBitmap(
                imageBytes,
                PushTemplateConstants.DefaultValues.CAROUSEL_MAX_BITMAP_WIDTH,
                PushTemplateConstants.DefaultValues.CAROUSEL_MAX_BITMAP_HEIGHT
            )?.also {
                Log.trace(
                    LOG_TAG,
                    SELF_TAG,
                    "Downloaded push notification image from url ($url)"
                )
            }
        } catch (throwable: Throwable) {
            Log.warning(
                LOG_TAG,
                SELF_TAG,
                "Failed to decode push notification image from url ($url): ${throwable.localizedMessage}."
            )
            null
        }
    }

    /**
     * Decodes the provided encoded image bytes into a [Bitmap], downsampling large source images so
     * the fully decoded bitmap stays at least as large as the requested [reqWidth] x [reqHeight]
     * bounding box. This keeps peak decode memory bounded regardless of the source resolution.
     *
     * @param imageBytes [ByteArray] containing the encoded image
     * @param reqWidth the target bounding-box width in pixels
     * @param reqHeight the target bounding-box height in pixels
     * @return the decoded [Bitmap], or `null` if the bytes could not be decoded
     */
    private fun decodeSampledBitmap(imageBytes: ByteArray, reqWidth: Int, reqHeight: Int): Bitmap? {
        // First pass: read only the image bounds without allocating pixel memory.
        val boundsOptions = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size, boundsOptions)

        // Second pass: decode the pixels using the computed sample size.
        val decodeOptions = BitmapFactory.Options().apply {
            inSampleSize = calculateInSampleSize(
                boundsOptions.outWidth,
                boundsOptions.outHeight,
                reqWidth,
                reqHeight
            )
        }
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size, decodeOptions)
    }

    /**
     * Computes the largest power-of-two `inSampleSize` that keeps the decoded image at least as
     * large as the requested bounding box, so the subsequent exact scaling in [scaleBitmap] has
     * enough resolution to stay sharp while peak decode memory stays bounded.
     *
     * @param srcWidth the source image width in pixels
     * @param srcHeight the source image height in pixels
     * @param reqWidth the target bounding-box width in pixels
     * @param reqHeight the target bounding-box height in pixels
     * @return the sample size (a power of two, minimum 1)
     */
    internal fun calculateInSampleSize(
        srcWidth: Int,
        srcHeight: Int,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        if (srcWidth <= 0 || srcHeight <= 0 || reqWidth <= 0 || reqHeight <= 0) {
            return 1
        }
        var inSampleSize = 1
        if (srcHeight > reqHeight || srcWidth > reqWidth) {
            val halfHeight = srcHeight / 2
            val halfWidth = srcWidth / 2
            // keep halving while both dimensions stay at or above the requested box
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

    /**
     * Converts a [Bitmap] into an [InputStream] to be used in caching images.
     *
     * @param bitmap [Bitmap] to be converted into an [InputStream]
     * @return an `InputStream` created from the provided bitmap
     */
    private fun bitmapToInputStream(bitmap: Bitmap): InputStream {
        ByteArrayOutputStream().use {
            bitmap.compress(Bitmap.CompressFormat.PNG, FULL_BITMAP_QUALITY, it)
            val bitmapData = it.toByteArray()
            return ByteArrayInputStream(bitmapData)
        }
    }

    /**
     * Writes the provided [InputStream] to the downloaded push template image [assetCacheLocation].
     *
     * @param cacheService [CacheService] the AEPSDK cache service
     * @param bitmapInputStream [InputStream] created from a download [Bitmap]
     * @param imageUrl [String] containing the image url to be used a cache key
     */
    private fun cacheBitmapInputStream(
        cacheService: CacheService,
        bitmapInputStream: InputStream,
        imageUrl: String
    ) {
        Log.trace(
            LOG_TAG,
            SELF_TAG,
            "Caching image downloaded from $imageUrl."
        )
        getAssetCacheLocation()?.let {
            // cache push notification images for 3 days
            val cacheEntry = CacheEntry(
                bitmapInputStream,
                CacheExpiry.after(
                    PushTemplateConstants.DefaultValues.PUSH_NOTIFICATION_IMAGE_CACHE_EXPIRY_IN_MILLISECONDS
                ),
                null
            )
            cacheService[it, imageUrl] = cacheEntry
        }
    }

    /**
     * Scales a downloaded [Bitmap] to fit within the
     * [PushTemplateConstants.DefaultValues.CAROUSEL_MAX_BITMAP_WIDTH] x
     * [PushTemplateConstants.DefaultValues.CAROUSEL_MAX_BITMAP_HEIGHT] bounding box.
     * The scaling is done using a [Matrix] object to maintain the aspect ratio of the original
     * image. The source bitmap is recycled when a new, scaled bitmap is produced so the larger
     * intermediate is released promptly.
     *
     * @param downloadedBitmap [Bitmap] to be scaled
     * @return [Bitmap] containing the scaled image
     */
    private fun scaleBitmap(downloadedBitmap: Bitmap): Bitmap {
        val matrix = Matrix()
        matrix.setRectToRect(
            RectF(0f, 0f, downloadedBitmap.width.toFloat(), downloadedBitmap.height.toFloat()),
            RectF(
                0f,
                0f,
                PushTemplateConstants.DefaultValues.CAROUSEL_MAX_BITMAP_WIDTH.toFloat(),
                PushTemplateConstants.DefaultValues.CAROUSEL_MAX_BITMAP_HEIGHT.toFloat()
            ),
            Matrix.ScaleToFit.CENTER
        )
        val scaledBitmap = Bitmap.createBitmap(
            downloadedBitmap,
            0,
            0,
            downloadedBitmap.width,
            downloadedBitmap.height,
            matrix,
            true
        )
        // createBitmap may return the same instance when no scaling is needed; only recycle the
        // source when a distinct scaled bitmap was actually allocated.
        if (scaledBitmap != downloadedBitmap) {
            downloadedBitmap.recycle()
        }
        return scaledBitmap
    }

    /**
     * Retrieves the asset cache location to use for downloaded push template images.
     *
     * @return [String] containing the asset cache location to use for storing downloaded push template images.
     */
    internal fun getAssetCacheLocation(): String? {
        val deviceInfoService = ServiceProvider.getInstance().deviceInfoService
            ?: return null
        val applicationCacheDir = deviceInfoService.applicationCacheDir
        return if ((applicationCacheDir == null)) null else (
            (
                applicationCacheDir
                    .toString() + File.separator +
                    PushTemplateConstants.CACHE_BASE_DIR
                ) + File.separator +
                PushTemplateConstants.PUSH_IMAGE_CACHE
            )
    }

    /**
     * Returns a device/target-aware scaled [Bitmap] for the given [url], suitable for setting on a
     * notification `RemoteViews` image. The bitmap is downsampled at decode time and scaled to the
     * requested [reqWidth] x [reqHeight] box (center-cropped to cover when [coverCrop] is true,
     * otherwise aspect-fit), keeping both peak decode memory and the marshaled bitmap size bounded
     * to what the notification actually displays.
     *
     * Results are cached keyed by url + target size + scale mode, so the same url requested at a
     * different display size does not collide. This path is AJO-scoped and independent of the
     * carousel [cacheImages] / [getCachedImage] flow.
     *
     * @param url the image url
     * @param reqWidth the target width in pixels
     * @param reqHeight the target height in pixels
     * @param coverCrop true to center-crop to cover the box, false to aspect-fit within it
     * @return the scaled [Bitmap], or `null` if it could not be produced
     */
    internal fun getScaledBitmap(
        url: String?,
        reqWidth: Int,
        reqHeight: Int,
        coverCrop: Boolean
    ): Bitmap? {
        if (url.isNullOrEmpty() || !UrlUtils.isValidUrl(url) || reqWidth <= 0 || reqHeight <= 0) {
            return null
        }
        val assetCacheLocation = getAssetCacheLocation()
        val cacheKey = scaledCacheKey(url, reqWidth, reqHeight, coverCrop)
        val cacheService = ServiceProvider.getInstance().cacheService

        // return a previously scaled + cached bitmap if present
        if (!assetCacheLocation.isNullOrEmpty()) {
            cacheService[assetCacheLocation, cacheKey]?.let {
                Log.trace(
                    LOG_TAG,
                    SELF_TAG,
                    "Found cached scaled image for $url ($reqWidth x $reqHeight)."
                )
                return try {
                    BitmapFactory.decodeStream(it.data)
                } catch (throwable: Throwable) {
                    Log.warning(
                        LOG_TAG,
                        SELF_TAG,
                        "Failed to decode cached scaled image for $url: ${throwable.localizedMessage}."
                    )
                    null
                }
            }
        }

        val imageBytes = downloadImageBytes(url) ?: return null
        val scaled = try {
            val decoded = decodeSampledBitmap(imageBytes, reqWidth, reqHeight) ?: return null
            scaleToTarget(decoded, reqWidth, reqHeight, coverCrop)
        } catch (throwable: Throwable) {
            Log.warning(
                LOG_TAG,
                SELF_TAG,
                "Failed to scale image for $url: ${throwable.localizedMessage}."
            )
            return null
        }

        // cache the scaled bitmap for reuse on re-posts
        if (!assetCacheLocation.isNullOrEmpty()) {
            try {
                bitmapToInputStream(scaled).use { stream ->
                    cacheService[assetCacheLocation, cacheKey] = CacheEntry(
                        stream,
                        CacheExpiry.after(
                            PushTemplateConstants.DefaultValues.PUSH_NOTIFICATION_IMAGE_CACHE_EXPIRY_IN_MILLISECONDS
                        ),
                        null
                    )
                }
            } catch (exception: IOException) {
                Log.warning(
                    LOG_TAG,
                    SELF_TAG,
                    "Failed to cache scaled image for $url: ${exception.localizedMessage}."
                )
            }
        }
        return scaled
    }

    /**
     * Builds a cache key qualified by the requested target size and scale mode so the same source
     * url cached at different display sizes does not collide.
     */
    internal fun scaledCacheKey(
        url: String,
        reqWidth: Int,
        reqHeight: Int,
        coverCrop: Boolean
    ): String = "$url#${reqWidth}x$reqHeight#${if (coverCrop) "cover" else "fit"}"

    /**
     * Synchronously downloads the raw (encoded) image bytes for [url], blocking up to
     * [DOWNLOAD_TIMEOUT_SECS] seconds. The connection is always closed and the latch always
     * released, even if the callback throws.
     *
     * @param url the image url
     * @return the encoded image bytes, or `null` on failure/timeout
     */
    private fun downloadImageBytes(url: String): ByteArray? {
        val latch = CountDownLatch(1)
        val holder = AtomicReference<ByteArray?>(null)
        downloadImage(url) { connection ->
            try {
                if (connection != null && connection.responseCode == HttpURLConnection.HTTP_OK) {
                    holder.set(connection.inputStream?.readBytes())
                } else {
                    Log.debug(
                        LOG_TAG,
                        SELF_TAG,
                        "Failed to download image from url ($url). Response code was: ${connection?.responseCode}."
                    )
                }
            } catch (throwable: Throwable) {
                Log.warning(
                    LOG_TAG,
                    SELF_TAG,
                    "Error reading image bytes from url ($url): ${throwable.localizedMessage}."
                )
            } finally {
                latch.countDown()
                connection?.close()
            }
        }
        return try {
            if (latch.await(DOWNLOAD_TIMEOUT_SECS.toLong(), TimeUnit.SECONDS)) {
                holder.get()
            } else {
                Log.warning(
                    LOG_TAG,
                    SELF_TAG,
                    "Timed out downloading image from url ($url)."
                )
                null
            }
        } catch (e: InterruptedException) {
            Log.warning(
                LOG_TAG,
                SELF_TAG,
                "Interrupted while downloading image from url ($url): ${e.localizedMessage}"
            )
            null
        }
    }

    /**
     * Scales [src] into the [reqWidth] x [reqHeight] box. When [coverCrop] is true the image is
     * scaled to cover the box and center-cropped (matching an ImageView `centerCrop`); otherwise it
     * is aspect-fit within the box. Intermediate bitmaps are recycled.
     *
     * @param src the decoded source [Bitmap]
     * @param reqWidth target width in pixels
     * @param reqHeight target height in pixels
     * @param coverCrop true to cover + crop, false to aspect-fit
     * @return the scaled [Bitmap]
     */
    internal fun scaleToTarget(
        src: Bitmap,
        reqWidth: Int,
        reqHeight: Int,
        coverCrop: Boolean
    ): Bitmap {
        if (!coverCrop) {
            val matrix = Matrix()
            matrix.setRectToRect(
                RectF(0f, 0f, src.width.toFloat(), src.height.toFloat()),
                RectF(0f, 0f, reqWidth.toFloat(), reqHeight.toFloat()),
                Matrix.ScaleToFit.CENTER
            )
            val fitted = Bitmap.createBitmap(src, 0, 0, src.width, src.height, matrix, true)
            if (fitted != src) src.recycle()
            return fitted
        }

        // cover: draw the source scaled-to-cover and centered directly into a reqWidth x reqHeight
        // bitmap. Drawing into the final-sized canvas avoids allocating a large intermediate when
        // the source aspect differs a lot from the box (e.g. a wide banner into a tall box).
        val output = createBitmap(reqWidth, reqHeight, src.config ?: Bitmap.Config.ARGB_8888)
        val scale = max(reqWidth.toFloat() / src.width, reqHeight.toFloat() / src.height)
        val matrix = Matrix().apply {
            setScale(scale, scale)
            // center the scaled source within the box
            postTranslate(
                (reqWidth - src.width * scale) / 2f,
                (reqHeight - src.height * scale) / 2f
            )
        }
        Canvas(output).drawBitmap(src, matrix, Paint(Paint.FILTER_BITMAP_FLAG))
        src.recycle()
        return output
    }
}
