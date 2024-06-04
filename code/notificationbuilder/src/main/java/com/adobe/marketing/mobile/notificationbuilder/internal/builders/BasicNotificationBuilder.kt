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
import android.view.View
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.adobe.marketing.mobile.notificationbuilder.NotificationConstructionFailedException
import com.adobe.marketing.mobile.notificationbuilder.PushTemplateConstants
import com.adobe.marketing.mobile.notificationbuilder.PushTemplateConstants.LOG_TAG
import com.adobe.marketing.mobile.notificationbuilder.PushTemplateConstants.PushPayloadKeys
import com.adobe.marketing.mobile.notificationbuilder.R
import com.adobe.marketing.mobile.notificationbuilder.internal.PushTemplateImageUtils
import com.adobe.marketing.mobile.notificationbuilder.internal.extensions.addActionButtons
import com.adobe.marketing.mobile.notificationbuilder.internal.extensions.createNotificationChannelIfRequired
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.BasicPushTemplate
import com.adobe.marketing.mobile.notificationbuilder.internal.util.NotificationData
import com.adobe.marketing.mobile.services.Log

/**
 * Object responsible for constructing a [NotificationCompat.Builder] object containing a basic push template notification.
 */
internal object BasicNotificationBuilder {
    private const val SELF_TAG = "BasicNotificationBuilder"

    @Throws(NotificationConstructionFailedException::class)
    fun construct(
        context: Context,
        pushTemplate: BasicPushTemplate,
        trackerActivityClass: Class<out Activity>?,
        broadcastReceiverClass: Class<out BroadcastReceiver>?
    ): NotificationCompat.Builder {
        Log.trace(LOG_TAG, SELF_TAG, "Building a basic template push notification.")
        val packageName = context.packageName
        val smallLayout = RemoteViews(packageName, R.layout.push_template_collapsed)
        val expandedLayout = RemoteViews(packageName, R.layout.push_template_expanded)

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelIdToUse: String =
            notificationManager.createNotificationChannelIfRequired(context, pushTemplate)

        // create the notification builder with the common settings applied
        val notificationBuilder = AEPPushNotificationBuilder.construct(
            context,
            pushTemplate,
            channelIdToUse,
            trackerActivityClass,
            smallLayout,
            expandedLayout,
            R.id.basic_expanded_layout
        )

        // set the image on the notification
        val imageUri = pushTemplate.imageUrl
        val downloadedImageCount = PushTemplateImageUtils.cacheImages(listOf(imageUri))

        if (downloadedImageCount == 0) {
            Log.trace(LOG_TAG, SELF_TAG, "No image found for basic push template.")
            expandedLayout.setViewVisibility(R.id.expanded_template_image, View.GONE)
        } else {
            expandedLayout.setImageViewBitmap(
                R.id.expanded_template_image,
                PushTemplateImageUtils.getCachedImage(imageUri)
            )
        }

        // add any action buttons defined for the notification
        notificationBuilder.addActionButtons(
            context,
            trackerActivityClass,
            pushTemplate.actionButtonsList,
            pushTemplate.tag,
            pushTemplate.isNotificationSticky ?: false
        )

        // add a remind later button if we have a label and an epoch or delay timestamp
        pushTemplate.remindLaterText?.let { remindLaterText ->
            if (pushTemplate.remindLaterTimestamp != null ||
                pushTemplate.remindLaterDuration != null
            ) {
                val remindIntent = createRemindPendingIntent(
                    context,
                    broadcastReceiverClass,
                    channelIdToUse,
                    pushTemplate
                )
                notificationBuilder.addAction(0, remindLaterText, remindIntent)
            }
        }

        return notificationBuilder
    }

    @Throws(NotificationConstructionFailedException::class)
    internal fun fallbackToBasicNotification(
        context: Context,
        trackerActivityClass: Class<out Activity>?,
        broadcastReceiverClass: Class<out BroadcastReceiver>?,
        data: NotificationData
    ): NotificationCompat.Builder {
        val basicPushTemplate = BasicPushTemplate(data)
        return construct(
            context,
            basicPushTemplate,
            trackerActivityClass,
            broadcastReceiverClass
        )
    }

    /**
     * Creates a pending intent for remind later button in a notification.
     *
     * @param context the application [Context]
     * @param broadcastReceiverClass the [Class] of the broadcast receiver to set in the created pending intent
     * @param channelId [String] containing the notification channel ID
     * @param pushTemplate the [BasicPushTemplate] object containing the basic push template data
     * @return the created remind later [PendingIntent]
     */
    private fun createRemindPendingIntent(
        context: Context,
        broadcastReceiverClass: Class<out BroadcastReceiver>?,
        channelId: String,
        pushTemplate: BasicPushTemplate
    ): PendingIntent? {
        if (broadcastReceiverClass == null) {
            return null
        }
        Log.trace(
            LOG_TAG,
            SELF_TAG,
            "Creating a remind later pending intent from a push template object."
        )

        val remindIntent = AEPPushNotificationBuilder.createIntent(
            PushTemplateConstants.IntentActions.REMIND_LATER_CLICKED,
            pushTemplate
        )
        remindIntent.putExtra(PushPayloadKeys.REMIND_LATER_TEXT, pushTemplate.remindLaterText)
        remindIntent.putExtra(
            PushPayloadKeys.REMIND_LATER_TIMESTAMP,
            pushTemplate.remindLaterTimestamp.toString()
        )
        remindIntent.putExtra(
            PushPayloadKeys.REMIND_LATER_DURATION,
            pushTemplate.remindLaterDuration.toString()
        )
        remindIntent.putExtra(PushPayloadKeys.ACTION_BUTTONS, pushTemplate.actionButtonsString)
        remindIntent.putExtra(PushPayloadKeys.CHANNEL_ID, channelId)

        broadcastReceiverClass.let {
            remindIntent.setClass(context.applicationContext, broadcastReceiverClass)
        }

        return PendingIntent.getBroadcast(
            context,
            0,
            remindIntent,
            PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }
}
