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
import android.os.Bundle
import android.view.View
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.adobe.marketing.mobile.notificationbuilder.NotificationConstructionFailedException
import com.adobe.marketing.mobile.notificationbuilder.PushTemplateConstants
import com.adobe.marketing.mobile.notificationbuilder.PushTemplateConstants.LOG_TAG
import com.adobe.marketing.mobile.notificationbuilder.R
import com.adobe.marketing.mobile.notificationbuilder.internal.PushTemplateImageUtils
import com.adobe.marketing.mobile.notificationbuilder.internal.extensions.createNotificationChannelIfRequired
import com.adobe.marketing.mobile.notificationbuilder.internal.extensions.setNotificationTitleTextColor
import com.adobe.marketing.mobile.notificationbuilder.internal.extensions.setRemoteViewClickAction
import com.adobe.marketing.mobile.notificationbuilder.internal.extensions.setRemoteViewImage
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.ProductRatingPushTemplate
import com.adobe.marketing.mobile.services.Log

internal object ProductRatingNotificationBuilder {
    private const val SELF_TAG = "ProductRatingNotificationBuilder"

    @Throws(NotificationConstructionFailedException::class)
    fun construct(
        context: Context,
        pushTemplate: ProductRatingPushTemplate,
        trackerActivityClass: Class<out Activity>?,
        broadcastReceiverClass: Class<out BroadcastReceiver>?
    ): NotificationCompat.Builder {
        Log.trace(
            LOG_TAG,
            SELF_TAG,
            "Building a rating template push notification."
        )
        val packageName = context.packageName
        val smallLayout = RemoteViews(packageName, R.layout.push_template_collapsed)
        val expandedLayout = RemoteViews(packageName, R.layout.push_template_product_rating_expanded)

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelIdToUse: String = notificationManager.createNotificationChannelIfRequired(
            context,
            pushTemplate
        )

        // create the notification builder with the common settings applied
        val notificationBuilder = AEPPushNotificationBuilder.construct(
            context,
            pushTemplate,
            channelIdToUse,
            trackerActivityClass,
            smallLayout,
            expandedLayout,
            R.id.rating_expanded_layout
        )

        // set the image on the notification
        val imageUri = pushTemplate.imageUrl
        val downloadedImageCount = PushTemplateImageUtils.cacheImages(listOf(imageUri))

        if (downloadedImageCount == 0) {
            Log.trace(
                LOG_TAG,
                SELF_TAG,
                "No image found for rating push template."
            )
            expandedLayout.setViewVisibility(R.id.expanded_template_image, View.GONE)
        } else {
            expandedLayout.setImageViewBitmap(
                R.id.expanded_template_image,
                PushTemplateImageUtils.getCachedImage(imageUri)
            )
        }

        // populate the rating icons
        populateRatingIcons(
            context,
            broadcastReceiverClass,
            expandedLayout,
            pushTemplate,
            packageName,
            channelIdToUse
        )

        // check if confirm button needs to be shown
        if (pushTemplate.ratingSelected > PushTemplateConstants.ProductRatingKeys.RATING_UNSELECTED) {
            expandedLayout.setViewVisibility(R.id.rating_confirm, View.VISIBLE)
            expandedLayout.setNotificationTitleTextColor(
                pushTemplate.titleTextColor,
                R.id.rating_confirm
            )

            // add pending intent for confirm click
            // sticky is set to false as the notification will be dismissed after confirm click
            val ratingConfirmedIntentExtras = Bundle(pushTemplate.data.getBundle()) // copy the bundle
            ratingConfirmedIntentExtras.putString(PushTemplateConstants.PushPayloadKeys.STICKY, "false")
            val selectedRatingAction = pushTemplate.ratingActionList[pushTemplate.ratingSelected]
            expandedLayout.setRemoteViewClickAction(
                context,
                trackerActivityClass,
                R.id.rating_confirm,
                selectedRatingAction.link,
                pushTemplate.ratingSelected.toString(),
                pushTemplate.ratingActionList[pushTemplate.ratingSelected].type,
                ratingConfirmedIntentExtras
            )
        } else {
            // hide confirm if no rating is selected
            expandedLayout.setViewVisibility(R.id.rating_confirm, View.INVISIBLE)
        }

        return notificationBuilder
    }

    /**
     * Populates the rating icons in the notification.
     *
     * @param context the current [Context] of the application
     * @param broadcastReceiverClass the [Class] of the broadcast receiver to set in the created pending intent
     * @param expandedLayout the [RemoteViews] containing the expanded layout of the notification
     * @param pushTemplate the [ProductRatingPushTemplate] object containing the product rating push template data
     * @param packageName the `String` name of the application package used to locate the layout resources
     * @param channelIdToUse the `String` containing the channel ID to use for the notification
     */
    private fun populateRatingIcons(
        context: Context,
        broadcastReceiverClass: Class<out BroadcastReceiver>?,
        expandedLayout: RemoteViews,
        pushTemplate: ProductRatingPushTemplate,
        packageName: String?,
        channelIdToUse: String
    ) {
        // set the rating icons in the notification based on the rating selected
        for (i in 0 until pushTemplate.ratingActionList.size) {
            val ratingIconLayout = RemoteViews(packageName, R.layout.push_template_product_rating_icon_layout)
            val ratingIconImageView = R.id.rating_icon_image
            if (i <= pushTemplate.ratingSelected) {
                if (!ratingIconLayout.setRemoteViewImage(pushTemplate.ratingSelectedIcon, ratingIconImageView)) {
                    throw NotificationConstructionFailedException("Image for selected rating icon is invalid.")
                }
            } else if (!ratingIconLayout.setRemoteViewImage(pushTemplate.ratingUnselectedIcon, ratingIconImageView)) {
                throw NotificationConstructionFailedException("Image for unselected rating icon is invalid.")
            }
            expandedLayout.addView(R.id.rating_icons_container, ratingIconLayout)

            // add pending intent for rating icon click
            val ratingButtonPendingIntent = createRatingButtonPendingIntent(
                context,
                broadcastReceiverClass,
                channelIdToUse,
                pushTemplate,
                i
            )
            ratingIconLayout.setOnClickPendingIntent(ratingIconImageView, ratingButtonPendingIntent)
        }
    }

    /**
     * Creates a pending intent for rating icon click in a notification.
     *
     * @param context the application [Context]
     * @param broadcastReceiverClass the [Class] of the broadcast receiver to set in the created pending intent
     * @param channelId [String] containing the notification channel ID
     * @param pushTemplate the [ProductRatingPushTemplate] object containing the basic push template data
     * @return the created remind later [PendingIntent]
     */
    private fun createRatingButtonPendingIntent(
        context: Context,
        broadcastReceiverClass: Class<out BroadcastReceiver>?,
        channelId: String,
        pushTemplate: ProductRatingPushTemplate,
        ratingButtonSelection: Int,
    ): PendingIntent? {
        if (broadcastReceiverClass == null) {
            return null
        }
        Log.trace(LOG_TAG, SELF_TAG, "Creating a rating click pending intent from a push template object.")

        val ratingButtonClickIntent = AEPPushNotificationBuilder.createIntent(PushTemplateConstants.IntentActions.RATING_ICON_CLICKED, pushTemplate)
        broadcastReceiverClass.let {
            ratingButtonClickIntent.setClass(context.applicationContext, broadcastReceiverClass)
        }
        ratingButtonClickIntent.putExtra(
            PushTemplateConstants.IntentKeys.RATING_SELECTED,
            ratingButtonSelection.toString()
        )
        ratingButtonClickIntent.putExtra(
            PushTemplateConstants.PushPayloadKeys.CHANNEL_ID,
            channelId
        )

        return PendingIntent.getBroadcast(
            context,
            pushTemplate.tag.hashCode() + ratingButtonSelection,
            ratingButtonClickIntent,
            PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }
}
