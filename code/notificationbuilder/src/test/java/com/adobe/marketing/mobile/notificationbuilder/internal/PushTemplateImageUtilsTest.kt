/*
  Copyright 2026 Adobe. All rights reserved.
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
import com.adobe.marketing.mobile.services.DeviceInforming
import com.adobe.marketing.mobile.services.HttpConnecting
import com.adobe.marketing.mobile.services.NetworkCallback
import com.adobe.marketing.mobile.services.NetworkRequest
import com.adobe.marketing.mobile.services.Networking
import com.adobe.marketing.mobile.services.ServiceProvider
import com.adobe.marketing.mobile.services.caching.CacheResult
import com.adobe.marketing.mobile.services.caching.CacheService
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.ByteArrayInputStream
import java.io.File
import java.net.HttpURLConnection

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [31])
class PushTemplateImageUtilsTest {

    private val validUrl = "http://example.com/image.png"

    @After
    fun teardown() {
        unmockkAll()
    }

    // ---- calculateInSampleSize ----

    @Test
    fun `calculateInSampleSize returns 1 when source already fits the target box`() {
        assertEquals(1, PushTemplateImageUtils.calculateInSampleSize(700, 500, 720, 720))
        assertEquals(1, PushTemplateImageUtils.calculateInSampleSize(720, 720, 720, 720))
    }

    @Test
    fun `calculateInSampleSize downsamples large landscape source (power of two, stays above box)`() {
        assertEquals(4, PushTemplateImageUtils.calculateInSampleSize(4000, 3000, 720, 720))
    }

    @Test
    fun `calculateInSampleSize downsamples square source just over the box`() {
        assertEquals(2, PushTemplateImageUtils.calculateInSampleSize(1440, 1440, 720, 720))
    }

    @Test
    fun `calculateInSampleSize handles rectangular device target (portrait source)`() {
        assertEquals(2, PushTemplateImageUtils.calculateInSampleSize(2160, 4320, 1080, 768))
    }

    @Test
    fun `calculateInSampleSize returns 1 for zero or negative dimensions`() {
        assertEquals(1, PushTemplateImageUtils.calculateInSampleSize(0, 0, 720, 720))
        assertEquals(1, PushTemplateImageUtils.calculateInSampleSize(1000, 1000, 0, 0))
        assertEquals(1, PushTemplateImageUtils.calculateInSampleSize(-10, -10, 720, 720))
    }

    @Test
    fun `calculateInSampleSize only downsamples the dimension that overflows`() {
        assertEquals(1, PushTemplateImageUtils.calculateInSampleSize(3000, 300, 720, 720))
    }

    // ---- scaledCacheKey ----

    @Test
    fun `scaledCacheKey encodes size and cover mode`() {
        assertEquals(
            "$validUrl#720x500#cover",
            PushTemplateImageUtils.scaledCacheKey(validUrl, 720, 500, true)
        )
    }

    @Test
    fun `scaledCacheKey encodes fit mode and differs from cover`() {
        val fit = PushTemplateImageUtils.scaledCacheKey(validUrl, 720, 500, false)
        val cover = PushTemplateImageUtils.scaledCacheKey(validUrl, 720, 500, true)
        assertEquals("$validUrl#720x500#fit", fit)
        assertTrue(fit != cover)
    }

    // ---- scaleToTarget ----

    @Test
    fun `scaleToTarget cover produces exact target dimensions and recycles source`() {
        val src = Bitmap.createBitmap(200, 100, Bitmap.Config.ARGB_8888)
        val out = PushTemplateImageUtils.scaleToTarget(src, 720, 500, true)
        assertEquals(720, out.width)
        assertEquals(500, out.height)
        assertTrue("source bitmap should be recycled after cover scale", src.isRecycled)
    }

    @Test
    fun `scaleToTarget fit keeps the result within the target box`() {
        val src = Bitmap.createBitmap(200, 100, Bitmap.Config.ARGB_8888)
        val out = PushTemplateImageUtils.scaleToTarget(src, 720, 500, false)
        assertTrue(out.width in 1..720)
        assertTrue(out.height in 1..500)
    }

    // ---- getScaledBitmap: guards ----

    @Test
    fun `getScaledBitmap returns null for null or blank url`() {
        assertNull(PushTemplateImageUtils.getScaledBitmap(null, 720, 500, true))
        assertNull(PushTemplateImageUtils.getScaledBitmap("", 720, 500, true))
    }

    @Test
    fun `getScaledBitmap returns null for invalid url`() {
        assertNull(PushTemplateImageUtils.getScaledBitmap("not a url", 720, 500, true))
    }

    @Test
    fun `getScaledBitmap returns null for non-positive dimensions`() {
        assertNull(PushTemplateImageUtils.getScaledBitmap(validUrl, 0, 500, true))
        assertNull(PushTemplateImageUtils.getScaledBitmap(validUrl, 720, 0, true))
    }

    // ---- getScaledBitmap: cache hit / download paths ----

    private fun mockServices(
        cache: CacheService,
        network: Networking? = null
    ) {
        val sp = mockk<ServiceProvider>()
        mockkStatic(ServiceProvider::class)
        every { ServiceProvider.getInstance() } returns sp
        val device = mockk<DeviceInforming>(relaxed = true)
        every { device.applicationCacheDir } returns File("build/tmp/pushimagecache-test")
        every { sp.deviceInfoService } returns device
        every { sp.cacheService } returns cache
        // SDK Log calls resolve the logging service off ServiceProvider; provide a relaxed one.
        every { sp.loggingService } returns mockk(relaxed = true)
        network?.let { every { sp.networkService } returns it }
    }

    @Test
    fun `getScaledBitmap returns cached bitmap on cache hit`() {
        val cache = mockk<CacheService>()
        val cacheResult = mockk<CacheResult>()
        every { cacheResult.data } returns ByteArrayInputStream(byteArrayOf(1, 2, 3))
        every { cache.get(any(), any()) } returns cacheResult
        mockServices(cache)
        val cached = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888)
        mockkStatic(BitmapFactory::class)
        every { BitmapFactory.decodeStream(any()) } returns cached

        val result = PushTemplateImageUtils.getScaledBitmap(validUrl, 720, 500, true)

        assertEquals(cached, result)
    }

    @Test
    fun `getScaledBitmap returns null when download fails`() {
        val cache = mockk<CacheService>(relaxed = true)
        every { cache.get(any(), any()) } returns null // cache miss
        val network = mockk<Networking>()
        every { network.connectAsync(any<NetworkRequest>(), any<NetworkCallback>()) } answers {
            secondArg<NetworkCallback>().call(null) // null connection -> failure
        }
        mockServices(cache, network)

        val result = PushTemplateImageUtils.getScaledBitmap(validUrl, 720, 500, true)

        assertNull(result)
    }

    @Test
    fun `getScaledBitmap downloads, scales and returns a bitmap on success`() {
        val cache = mockk<CacheService>(relaxed = true)
        every { cache.get(any(), any()) } returns null // cache miss -> download
        val connection = mockk<HttpConnecting>(relaxed = true)
        every { connection.responseCode } returns HttpURLConnection.HTTP_OK
        every { connection.inputStream } returns ByteArrayInputStream(byteArrayOf(9, 9, 9, 9))
        val network = mockk<Networking>()
        every { network.connectAsync(any<NetworkRequest>(), any<NetworkCallback>()) } answers {
            secondArg<NetworkCallback>().call(connection)
        }
        mockServices(cache, network)
        // decode returns a real bitmap so scaleToTarget can operate on it
        mockkStatic(BitmapFactory::class)
        every { BitmapFactory.decodeByteArray(any(), any(), any(), any()) } returns
            Bitmap.createBitmap(800, 600, Bitmap.Config.ARGB_8888)

        val result = PushTemplateImageUtils.getScaledBitmap(validUrl, 720, 500, true)

        // cover scaling produces exactly the requested target box
        assertEquals(720, result?.width)
        assertEquals(500, result?.height)
    }

    // ---- cacheImages (shared path: sampled decode + scaleBitmap recycle + finally) ----

    @Test
    fun `cacheImages downloads, decodes and caches on success`() {
        val cache = mockk<CacheService>(relaxed = true)
        every { cache.get(any(), any()) } returns null // cache miss
        val connection = mockk<HttpConnecting>(relaxed = true)
        every { connection.responseCode } returns HttpURLConnection.HTTP_OK
        every { connection.inputStream } returns ByteArrayInputStream(byteArrayOf(1, 2, 3, 4))
        val network = mockk<Networking>()
        every { network.connectAsync(any<NetworkRequest>(), any<NetworkCallback>()) } answers {
            secondArg<NetworkCallback>().call(connection)
        }
        mockServices(cache, network)
        mockkStatic(BitmapFactory::class)
        every { BitmapFactory.decodeByteArray(any(), any(), any(), any()) } returns
            Bitmap.createBitmap(800, 600, Bitmap.Config.ARGB_8888)

        val count = PushTemplateImageUtils.cacheImages(listOf(validUrl))

        assertEquals(1, count)
        verify { connection.close() }
    }

    @Test
    fun `cacheImages closes connection and counts zero when no image is decoded`() {
        val cache = mockk<CacheService>(relaxed = true)
        every { cache.get(any(), any()) } returns null // cache miss
        val connection = mockk<HttpConnecting>(relaxed = true)
        every { connection.responseCode } returns HttpURLConnection.HTTP_OK
        every { connection.inputStream } returns ByteArrayInputStream(byteArrayOf()) // empty -> null image
        val network = mockk<Networking>()
        every { network.connectAsync(any<NetworkRequest>(), any<NetworkCallback>()) } answers {
            secondArg<NetworkCallback>().call(connection)
        }
        mockServices(cache, network)

        val count = PushTemplateImageUtils.cacheImages(listOf(validUrl))

        assertEquals(0, count)
        verify { connection.close() }
    }
}
