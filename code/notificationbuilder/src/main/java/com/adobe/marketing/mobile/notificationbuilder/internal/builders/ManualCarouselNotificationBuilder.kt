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
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.widget.RemoteViews
import androidx.annotation.VisibleForTesting
import androidx.core.app.NotificationCompat
import com.adobe.marketing.mobile.notificationbuilder.NotificationConstructionFailedException
import com.adobe.marketing.mobile.notificationbuilder.R
import com.adobe.marketing.mobile.notificationbuilder.internal.extensions.createNotificationChannelIfRequired
import com.adobe.marketing.mobile.notificationbuilder.internal.extensions.setRemoteViewClickAction
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.CarouselPushTemplate
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.ManualCarouselPushTemplate
import com.adobe.marketing.mobile.services.Log
import com.adobe.marketing.mobile.utils.PushTemplateConstants
import com.adobe.marketing.mobile.utils.PushTemplateConstants.LOG_TAG
import com.adobe.marketing.mobile.utils.PushTemplateConstants.PushPayloadKeys
import com.adobe.marketing.mobile.utils.PushTemplateImageUtils

/**
 * Object responsible for constructing a [NotificationCompat.Builder] object containing a manual or filmstrip carousel push template notification.
 */
internal object ManualCarouselNotificationBuilder {
    private const val SELF_TAG = "ManualCarouselNotificationBuilder"

    @Throws(NotificationConstructionFailedException::class)
    fun construct(
        context: Context,
        pushTemplate: ManualCarouselPushTemplate,
        trackerActivityClass: Class<out Activity>?,
        broadcastReceiverClass: Class<out BroadcastReceiver>?
    ): NotificationCompat.Builder {
        Log.trace(LOG_TAG, SELF_TAG, "Building a manual carousel template push notification.")

        // download carousel images
        val downloadedImagesCount = PushTemplateImageUtils.cacheImages(
            pushTemplate.carouselItems.map { it.imageUri }
        )

        // fallback to a basic push template notification builder if less than 3 images were able
        // to be downloaded
        if (downloadedImagesCount < PushTemplateConstants.DefaultValues.CAROUSEL_MINIMUM_IMAGE_COUNT) {
            Log.warning(LOG_TAG, SELF_TAG, "Less than 3 images are available for the manual carousel push template, falling back to a basic push template.")
            return BasicNotificationBuilder.fallbackToBasicNotification(
                context,
                trackerActivityClass,
                broadcastReceiverClass,
                pushTemplate.data
            )
        }

        // set the expanded layout depending on the carousel type
        val packageName = context.packageName
        val smallLayout = RemoteViews(packageName, R.layout.push_template_collapsed)
        val expandedLayout =
            if (pushTemplate.carouselLayout == PushTemplateConstants.DefaultValues.FILMSTRIP_CAROUSEL_MODE)
                RemoteViews(
                    packageName,
                    R.layout.push_template_filmstrip_carousel
                ) else RemoteViews(packageName, R.layout.push_template_manual_carousel)

        val validCarouselItems = downloadCarouselItems(pushTemplate.carouselItems)

        // get the indices for the carousel
        val carouselIndices = getCarouselIndices(pushTemplate, validCarouselItems.size)

        // store the updated center image index
        pushTemplate.centerImageIndex = carouselIndices.second

        // populate the images for the manual carousel
        setupCarouselImages(
            context,
            carouselIndices,
            pushTemplate,
            trackerActivityClass,
            expandedLayout,
            validCarouselItems,
            packageName,
        )

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // create the notification channel if needed
        val channelIdToUse =
            notificationManager.createNotificationChannelIfRequired(context, pushTemplate)

        // create the notification builder with the common settings applied
        val notificationBuilder = AEPPushNotificationBuilder.construct(
            context,
            pushTemplate,
            channelIdToUse,
            trackerActivityClass,
            smallLayout,
            expandedLayout,
            R.id.carousel_container_layout
        )

        // handle left and right navigation buttons
        setupNavigationButtons(
            context,
            pushTemplate,
            broadcastReceiverClass,
            expandedLayout,
            channelIdToUse
        )

        return notificationBuilder
    }

    /**
     * Downloads the images for a carousel push template.
     *
     * @param items the list of [CarouselPushTemplate.CarouselItem] objects to be displayed in the filmstrip carousel
     * @return a list of `CarouselPushTemplate.CarouselItem` objects that were successfully downloaded
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    internal fun downloadCarouselItems(
        items: List<CarouselPushTemplate.CarouselItem>
    ): List<CarouselPushTemplate.CarouselItem> {
        val validCarouselItems = mutableListOf<CarouselPushTemplate.CarouselItem>()
        for (item: CarouselPushTemplate.CarouselItem in items) {
            val imageUri: String = item.imageUri
            val pushImage: Bitmap? =
                PushTemplateImageUtils.getCachedImage(imageUri)
            if (pushImage == null) {
                Log.warning(
                    LOG_TAG,
                    SELF_TAG,
                    "Failed to retrieve an image from $imageUri, will not create a new carousel item."
                )
                continue
            }
            validCarouselItems.add(item)
        }
        return validCarouselItems
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    internal fun getCarouselIndices(
        pushTemplate: ManualCarouselPushTemplate,
        carouselSize: Int
    ): Triple<Int, Int, Int> {
        val carouselIndices: Triple<Int, Int, Int>
        if (pushTemplate.intentAction?.isNotEmpty() == true) {
            carouselIndices =
                if (pushTemplate.intentAction == PushTemplateConstants.IntentActions.MANUAL_CAROUSEL_LEFT_CLICKED || pushTemplate.intentAction == PushTemplateConstants.IntentActions.FILMSTRIP_LEFT_CLICKED) {
                    getNewIndicesForNavigateLeft(pushTemplate.centerImageIndex, carouselSize)
                } else {
                    getNewIndicesForNavigateRight(pushTemplate.centerImageIndex, carouselSize)
                }
        } else { // setup default indices if not building the notification from an intent
            carouselIndices =
                if (pushTemplate.carouselLayout == PushTemplateConstants.DefaultValues.FILMSTRIP_CAROUSEL_MODE) {
                    Triple(
                        PushTemplateConstants.DefaultValues.FILMSTRIP_CAROUSEL_CENTER_INDEX - 1,
                        PushTemplateConstants.DefaultValues.FILMSTRIP_CAROUSEL_CENTER_INDEX,
                        PushTemplateConstants.DefaultValues.FILMSTRIP_CAROUSEL_CENTER_INDEX + 1
                    )
                } else {
                    Triple(
                        carouselSize - 1,
                        PushTemplateConstants.DefaultValues.MANUAL_CAROUSEL_START_INDEX,
                        PushTemplateConstants.DefaultValues.MANUAL_CAROUSEL_START_INDEX + 1
                    )
                }
        }

        return carouselIndices
    }

    private fun setupCarouselImages(
        context: Context,
        newIndices: Triple<Int, Int, Int>,
        pushTemplate: ManualCarouselPushTemplate,
        trackerActivityClass: Class<out Activity>?,
        expandedLayout: RemoteViews,
        validCarouselItems: List<CarouselPushTemplate.CarouselItem>,
        packageName: String?,
    ) {
        if (pushTemplate.carouselLayout == PushTemplateConstants.DefaultValues.FILMSTRIP_CAROUSEL_MODE) {
            populateFilmstripCarouselImages(
                context,
                validCarouselItems,
                newIndices,
                pushTemplate,
                trackerActivityClass,
                expandedLayout
            )
        } else {
            populateManualCarouselImages(
                context,
                validCarouselItems,
                packageName,
                newIndices.second,
                pushTemplate,
                trackerActivityClass,
                expandedLayout
            )
        }
    }

    private fun setupNavigationButtons(
        context: Context,
        pushTemplate: ManualCarouselPushTemplate,
        broadcastReceiverClass: Class<out BroadcastReceiver>?,
        expandedLayout: RemoteViews,
        channelId: String
    ) {
        val clickPair =
            if (pushTemplate.carouselLayout == PushTemplateConstants.DefaultValues.DEFAULT_MANUAL_CAROUSEL_MODE) {
                Pair(
                    PushTemplateConstants.IntentActions.MANUAL_CAROUSEL_LEFT_CLICKED,
                    PushTemplateConstants.IntentActions.MANUAL_CAROUSEL_RIGHT_CLICKED
                )
            } else {
                Pair(
                    PushTemplateConstants.IntentActions.FILMSTRIP_LEFT_CLICKED,
                    PushTemplateConstants.IntentActions.FILMSTRIP_RIGHT_CLICKED
                )
            }

        val pendingIntentLeftButton = createCarouselNavigationClickPendingIntent(
            context,
            pushTemplate,
            clickPair.first,
            broadcastReceiverClass,
            channelId
        )

        val pendingIntentRightButton = createCarouselNavigationClickPendingIntent(
            context,
            pushTemplate,
            clickPair.second,
            broadcastReceiverClass,
            channelId
        )

        expandedLayout.setOnClickPendingIntent(R.id.leftImageButton, pendingIntentLeftButton)
        expandedLayout.setOnClickPendingIntent(R.id.rightImageButton, pendingIntentRightButton)
    }

    /**
     * Populates the images for a manual carousel push template.
     *
     * @param context the current [Context] of the application
     * @param items the list of [CarouselPushTemplate.CarouselItem] objects to be displayed in the carousel
     * @param packageName the [String] containing the package name of the application
     * @param centerIndex the `Int` index of the center image in the carousel
     * @param pushTemplate the [ManualCarouselPushTemplate] object containing the push template data
     * @param trackerActivityClass the [Class] of the activity that will be used for tracking interactions with the carousel item
     * @param expandedLayout the [RemoteViews] containing the expanded layout of the notification
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    internal fun populateManualCarouselImages(
        context: Context,
        items: List<CarouselPushTemplate.CarouselItem>,
        packageName: String?,
        centerIndex: Int,
        pushTemplate: ManualCarouselPushTemplate,
        trackerActivityClass: Class<out Activity>?,
        expandedLayout: RemoteViews
    ) {
        for (item: CarouselPushTemplate.CarouselItem in items) {
            val imageUri = item.imageUri
            val pushImage: Bitmap? =
                PushTemplateImageUtils.getCachedImage(imageUri)
            if (pushImage == null) {
                Log.warning(
                    LOG_TAG,
                    SELF_TAG,
                    "Failed to retrieve an image from $imageUri, will not create a new carousel item."
                )
                continue
            }
            val carouselItemRemoteView =
                RemoteViews(packageName, R.layout.push_template_carousel_item)
            carouselItemRemoteView.setImageViewBitmap(R.id.carousel_item_image_view, pushImage)
            carouselItemRemoteView.setTextViewText(R.id.carousel_item_caption, item.captionText)

            // assign a click action pending intent for each carousel item
            val interactionUri =
                if (item.interactionUri.isNullOrEmpty()) pushTemplate.actionUri else item.interactionUri
            interactionUri?.let {
                carouselItemRemoteView.setRemoteViewClickAction(
                    context,
                    trackerActivityClass,
                    R.id.carousel_item_image_view,
                    interactionUri,
                    null,
                    pushTemplate.data.getBundle()
                )
            }

            // add the carousel item to the view flipper
            expandedLayout.addView(R.id.manual_carousel_view_flipper, carouselItemRemoteView)

            // set the center image
            expandedLayout.setDisplayedChild(
                R.id.manual_carousel_view_flipper,
                centerIndex
            )
        }
    }

    /**
     * Populates the images for a manual filmstrip carousel push template.
     *
     * @param context the current [Context] of the application
     * @param imageCaptions the list of [String] captions for each filmstrip carousel image
     * @param imageClickActions the list of [String] click actions for each filmstrip carousel image
     * @param newIndices a [Triple] of [Int] indices for the new left, center, and right images
     * @param pushTemplate the [ManualCarouselPushTemplate] object containing the push template data
     * @param trackerActivityClass the [Class] of the activity that will be used for tracking interactions with the carousel item
     * @param expandedLayout the [RemoteViews] containing the expanded layout of the notification
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    internal fun populateFilmstripCarouselImages(
        context: Context,
        validCarouselItems: List<CarouselPushTemplate.CarouselItem>,
        newIndices: Triple<Int, Int, Int>,
        pushTemplate: ManualCarouselPushTemplate,
        trackerActivityClass: Class<out Activity>?,
        expandedLayout: RemoteViews
    ) {
        // get all captions present then set center caption text
        val centerCaptionText = validCarouselItems[newIndices.second].captionText
        expandedLayout.setTextViewText(
            R.id.manual_carousel_filmstrip_caption,
            centerCaptionText
        )

        // set the downloaded bitmaps in the filmstrip image views
        val assetCacheLocation = PushTemplateImageUtils.getAssetCacheLocation()
        if (assetCacheLocation.isNullOrEmpty()) {
            Log.warning(
                LOG_TAG,
                SELF_TAG,
                "Asset cache location is null or empty, unable to retrieve filmstrip carousel images."
            )
            return
        }

        val newLeftImage = PushTemplateImageUtils.getCachedImage(
            validCarouselItems[newIndices.first].imageUri
        )
        expandedLayout.setImageViewBitmap(
            R.id.manual_carousel_filmstrip_left, newLeftImage
        )

        val newCenterImage = PushTemplateImageUtils.getCachedImage(
            validCarouselItems[newIndices.second].imageUri
        )
        expandedLayout.setImageViewBitmap(
            R.id.manual_carousel_filmstrip_center, newCenterImage
        )

        val newRightImage = PushTemplateImageUtils.getCachedImage(
            validCarouselItems[newIndices.third].imageUri
        )
        expandedLayout.setImageViewBitmap(
            R.id.manual_carousel_filmstrip_right, newRightImage
        )

        // assign a click action pending intent to the center image view
        val interactionUri =
            if (!validCarouselItems[newIndices.second].interactionUri.isNullOrEmpty()) validCarouselItems[newIndices.second].interactionUri
            else pushTemplate.actionUri
        expandedLayout.setRemoteViewClickAction(
            context,
            trackerActivityClass,
            R.id.manual_carousel_filmstrip_center,
            interactionUri,
            null,
            pushTemplate.data.getBundle()
        )
    }

    /**
     * Calculates a new left, center, and right index for a carousel skip left press given the current center index and total number
     * of images
     *
     * @param centerIndex [Int] containing the current center image index
     * @param listSize `Int` containing the total number of images
     * @return [Triple] containing the calculated left, center, and right indices
     */
    private fun getNewIndicesForNavigateLeft(
        centerIndex: Int,
        listSize: Int
    ): Triple<Int, Int, Int> {
        val newCenterIndex = (centerIndex - 1 + listSize) % listSize
        val newLeftIndex = (newCenterIndex - 1 + listSize) % listSize
        Log.trace(
            LOG_TAG, SELF_TAG,
            "Calculated new indices. New center index is $newCenterIndex, new left index is $newLeftIndex, and new right index is $centerIndex."
        )
        return Triple(newLeftIndex, newCenterIndex, centerIndex)
    }

    /**
     * Calculates a new left, center, and right index for a carousel skip right press given the current center index and total number
     * of images
     *
     * @param centerIndex [Int] containing the current center image index
     * @param listSize `Int` containing the total number of images
     * @return [Triple] containing the calculated left, center, and right indices
     */
    private fun getNewIndicesForNavigateRight(
        centerIndex: Int,
        listSize: Int
    ): Triple<Int, Int, Int> {
        val newCenterIndex = (centerIndex + 1) % listSize
        val newRightIndex = (newCenterIndex + 1) % listSize
        Log.trace(
            LOG_TAG,
            SELF_TAG,
            "Calculated new indices. New center index is $newCenterIndex, new left index is $centerIndex, and new right index is $newRightIndex."
        )
        return Triple(centerIndex, newCenterIndex, newRightIndex)
    }

    /**
     * Creates a click intent for the specified [Intent] action. This intent is used to handle interactions
     * with the skip left and skip right buttons in a filmstrip or manual carousel push template notification.
     *
     * @param context the application [Context]
     * @param pushTemplate the [ManualCarouselPushTemplate] object containing the manual carousel push template data
     * @param intentAction [String] containing the intent action
     * @param broadcastReceiverClass the [Class] of the broadcast receiver to set in the created pending intent
     * @param channelId [String] containing the notification channel ID
     * @return the created click [Intent]
     */
    private fun createCarouselNavigationClickPendingIntent(
        context: Context,
        pushTemplate: ManualCarouselPushTemplate,
        intentAction: String,
        broadcastReceiverClass: Class<out BroadcastReceiver>?,
        channelId: String
    ): PendingIntent? {
        if (broadcastReceiverClass == null) {
            return null
        }
        val clickIntent = AEPPushNotificationBuilder.createIntent(intentAction, pushTemplate)
        clickIntent.putExtra(PushPayloadKeys.CHANNEL_ID, channelId)
        clickIntent.putExtra(
            PushTemplateConstants.IntentKeys.CENTER_IMAGE_INDEX,
            pushTemplate.centerImageIndex.toString()
        )
        broadcastReceiverClass.let {
            clickIntent.setClass(context, broadcastReceiverClass)
        }
        return PendingIntent.getBroadcast(
            context,
            0,
            clickIntent,
            PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }
}
