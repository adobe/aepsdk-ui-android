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

package com.adobe.marketing.mobile.notificationbuilder.internal.builders

import android.app.Activity
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.graphics.Bitmap
import android.widget.RemoteViews
import androidx.annotation.VisibleForTesting
import androidx.core.app.NotificationCompat
import com.adobe.marketing.mobile.notificationbuilder.PushTemplateConstants
import com.adobe.marketing.mobile.notificationbuilder.PushTemplateConstants.LOG_TAG
import com.adobe.marketing.mobile.notificationbuilder.R
import com.adobe.marketing.mobile.notificationbuilder.internal.extensions.createNotificationChannelIfRequired
import com.adobe.marketing.mobile.notificationbuilder.internal.extensions.setRemoteViewClickAction
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.AutoCarouselPushTemplate
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.CarouselPushTemplate
import com.adobe.marketing.mobile.services.Log
import com.adobe.marketing.mobile.utils.UiImageUtils

/**
 * Object responsible for constructing a [NotificationCompat.Builder] object containing a auto carousel push template notification.
 */
internal object AutoCarouselNotificationBuilder {
    private const val SELF_TAG = "AutoCarouselNotificationBuilder"

    fun construct(
        context: Context,
        pushTemplate: AutoCarouselPushTemplate,
        trackerActivityClass: Class<out Activity>?,
        broadcastReceiverClass: Class<out BroadcastReceiver>?,
    ): NotificationCompat.Builder {
        Log.trace(LOG_TAG, SELF_TAG, "Building an auto carousel template push notification.")

        val packageName = context.packageName
        val smallLayout = RemoteViews(packageName, R.layout.push_template_collapsed)
        val expandedLayout = RemoteViews(packageName, R.layout.push_template_auto_carousel)

        // load images into the carousel
        val downloadedImageCount = UiImageUtils.cacheImages(
            pushTemplate.carouselItems.map { it.imageUri }
        )

        // fallback to a basic push template notification builder if less than 3 images were able to be downloaded
        if (downloadedImageCount < PushTemplateConstants.DefaultValues.CAROUSEL_MINIMUM_IMAGE_COUNT) {
            Log.warning(LOG_TAG, SELF_TAG, "Less than 3 images are available for the auto carousel push template, falling back to a basic push template.")
            return BasicNotificationBuilder.fallbackToBasicNotification(
                context,
                trackerActivityClass,
                broadcastReceiverClass,
                pushTemplate.data,
            )
        }

        // load images into the carousel
        populateAutoCarouselImages(
            context,
            trackerActivityClass,
            expandedLayout,
            pushTemplate,
            pushTemplate.carouselItems,
            packageName
        )

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create the notification channel if needed
        val channelIdToUse =
            notificationManager.createNotificationChannelIfRequired(context, pushTemplate)

        // create the notification builder with the common settings applied
        return AEPPushNotificationBuilder.construct(
            context,
            pushTemplate,
            channelIdToUse,
            trackerActivityClass,
            smallLayout,
            expandedLayout,
            R.id.carousel_container_layout
        )
    }

    /**
     * Populates the images for a automatic carousel push template.
     *
     * @param context the current [Context] of the application
     * @param trackerActivityClass the [Class] of the activity that will be used for tracking interactions with the carousel item
     * @param expandedLayout the [RemoteViews] containing the expanded layout of the notification
     * @param pushTemplate the [CarouselPushTemplate] object containing the push template data
     * @param items the list of [CarouselPushTemplate.CarouselItem] objects to be displayed in the carousel
     * @param packageName the `String` name of the application package used to locate the layout resources
     * @return a [List] of downloaded image URIs
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    internal fun populateAutoCarouselImages(
        context: Context,
        trackerActivityClass: Class<out Activity>?,
        expandedLayout: RemoteViews,
        pushTemplate: CarouselPushTemplate,
        items: MutableList<CarouselPushTemplate.CarouselItem>,
        packageName: String?
    ): List<String> {
        val downloadedImageUris = mutableListOf<String>()
        for (item: CarouselPushTemplate.CarouselItem in items) {
            val imageUri: String = item.imageUri
            val pushImage: Bitmap? = UiImageUtils.getCachedImage(imageUri)
            if (pushImage == null) {
                Log.trace(
                    LOG_TAG,
                    SELF_TAG,
                    "Failed to retrieve an image from $imageUri, will not create a new carousel item."
                )
                continue
            }
            val carouselItem = RemoteViews(packageName, R.layout.push_template_carousel_item)
            downloadedImageUris.add(imageUri)
            carouselItem.setImageViewBitmap(R.id.carousel_item_image_view, pushImage)
            carouselItem.setTextViewText(R.id.carousel_item_caption, item.captionText)

            // assign a click action pending intent for each carousel item if we have a tracker activity
            trackerActivityClass?.let {
                val interactionUri = item.interactionUri ?: pushTemplate.actionUri
                carouselItem.setRemoteViewClickAction(
                    context,
                    trackerActivityClass,
                    R.id.carousel_item_image_view,
                    interactionUri,
                    null,
                    pushTemplate.data.getBundle()
                )
            }

            // add the carousel item to the view flipper
            expandedLayout.addView(R.id.auto_carousel_view_flipper, carouselItem)
        }

        return downloadedImageUris
    }
}
