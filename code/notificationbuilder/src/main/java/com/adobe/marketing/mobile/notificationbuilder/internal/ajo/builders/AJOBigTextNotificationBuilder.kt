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

package com.adobe.marketing.mobile.notificationbuilder.internal.ajo.builders

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.media.RingtoneManager
import android.os.Build
import android.view.View
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.adobe.marketing.mobile.notificationbuilder.NotificationConstructionFailedException
import com.adobe.marketing.mobile.notificationbuilder.PushTemplateConstants
import com.adobe.marketing.mobile.notificationbuilder.PushTemplateConstants.LOG_TAG
import com.adobe.marketing.mobile.notificationbuilder.R
import com.adobe.marketing.mobile.notificationbuilder.internal.extensions.addActionButtons
import com.adobe.marketing.mobile.notificationbuilder.internal.extensions.getSoundUriForResourceName
import com.adobe.marketing.mobile.notificationbuilder.internal.extensions.setNotificationClickAction
import com.adobe.marketing.mobile.notificationbuilder.internal.extensions.setNotificationDeleteAction
import com.adobe.marketing.mobile.notificationbuilder.internal.extensions.setRemoteViewImage
import com.adobe.marketing.mobile.notificationbuilder.internal.extensions.setSmallIcon
import com.adobe.marketing.mobile.notificationbuilder.internal.extensions.setSound
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.AJOBigTextPushTemplate
import com.adobe.marketing.mobile.services.Log

/**
 * Object responsible for constructing a [NotificationCompat.Builder] object containing an
 * AJO big text ("ajo_bigtext") push template notification.
 *
 * Renders a BigText-style layout: collapsed shows title + short body + large icon; expanded shows
 * title + full long body ([AJOBigTextPushTemplate.expandedBodyText]) + large icon. There is no
 * hero image. The large icon scale type is applied by choosing between two pre-defined expanded
 * layouts, mirroring the pattern used by [AJOBasicNotificationBuilder] for the hero image.
 */
internal object AJOBigTextNotificationBuilder {
    private const val SELF_TAG = "AJOBigTextNotificationBuilder"

    @Throws(NotificationConstructionFailedException::class)
    fun construct(
        context: Context,
        pushTemplate: AJOBigTextPushTemplate,
        trackerActivityClass: Class<out Activity>?,
        broadcastReceiverClass: Class<out BroadcastReceiver>?
    ): NotificationCompat.Builder {
        Log.trace(LOG_TAG, SELF_TAG, "Building an AJO big text template push notification.")
        val packageName = context.packageName
        val smallLayout = RemoteViews(packageName, R.layout.ajo_push_template_collapsed)
        val expandedLayout = RemoteViews(packageName, R.layout.ajo_bigtext_push_template_expanded)

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelIdToUse = createChannelIfRequired(context, notificationManager, pushTemplate)

        // set the title and body text. The collapsed state shows the short collapsed text while
        // the expanded state shows the full long body text (the flat adb_body key).
        smallLayout.setTextViewText(R.id.notification_title, pushTemplate.title)
        smallLayout.setTextViewText(R.id.notification_body, pushTemplate.collapsedText)
        expandedLayout.setTextViewText(R.id.notification_title, pushTemplate.title)
        expandedLayout.setTextViewText(R.id.notification_body_expanded, pushTemplate.body)

        // the large side icon has no scale type option — always render with center crop and
        // hide the fit center view
        smallLayout.setViewVisibility(R.id.large_icon_fit_center, View.GONE)
        smallLayout.setViewVisibility(R.id.large_icon_center_crop, View.VISIBLE)
        smallLayout.setRemoteViewImage(pushTemplate.largeIconUrl, R.id.large_icon_center_crop)
        expandedLayout.setViewVisibility(R.id.large_icon_fit_center, View.GONE)
        expandedLayout.setViewVisibility(R.id.large_icon_center_crop, View.VISIBLE)
        expandedLayout.setRemoteViewImage(pushTemplate.largeIconUrl, R.id.large_icon_center_crop)

        val builder = NotificationCompat.Builder(context, channelIdToUse)
            .setTicker(pushTemplate.ticker)
            .setNumber(pushTemplate.badgeCount)
            .setAutoCancel(!pushTemplate.isNotificationSticky)
            .setOngoing(pushTemplate.isNotificationSticky)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(smallLayout)
            .setCustomBigContentView(expandedLayout)
            // small icon must be present, otherwise the notification will not be displayed.
            .setSmallIcon(context, pushTemplate.smallIcon, null)
            .setVisibility(pushTemplate.visibility.value)
            .setNotificationClickAction(
                context,
                trackerActivityClass,
                pushTemplate.actionUri,
                pushTemplate.actionType,
                pushTemplate.data.getBundle()
            )
            .setNotificationDeleteAction(context, trackerActivityClass)

        // if not from intent, set custom sound. applies to API 25 and lower only as
        // API 26 and up set the sound on the notification channel.
        if (!pushTemplate.isFromIntent) {
            builder.setSound(context, pushTemplate.sound)
        }

        // below API 26 (no notification channels) priority is set on the builder
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            builder.setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVibrate(LongArray(0))
        }

        // add any action buttons defined for the notification
        builder.addActionButtons(
            context,
            trackerActivityClass,
            pushTemplate.actionButtonsList,
            pushTemplate.data.getBundle()
        )

        return builder
    }

    /**
     * Creates a notification channel if required. Logic mirrors the shared
     * `NotificationManager.createNotificationChannelIfRequired` extension but is kept local so
     * the AJO builder does not depend on or modify the AEPPushTemplate-typed shared extension.
     *
     * @param context the application [Context]
     * @param notificationManager the [NotificationManager] used to create / look up channels
     * @param pushTemplate the [AJOBigTextPushTemplate] providing channel id, sound and importance
     * @return the channel ID to use for the notification
     */
    private fun createChannelIfRequired(
        context: Context,
        notificationManager: NotificationManager,
        pushTemplate: AJOBigTextPushTemplate
    ): String {
        val channelIdToUse =
            if (pushTemplate.isFromIntent) {
                PushTemplateConstants.DefaultValues.SILENT_NOTIFICATION_CHANNEL_ID
            } else {
                pushTemplate.channelId ?: PushTemplateConstants.DefaultValues.DEFAULT_CHANNEL_ID
            }

        // no channel creation required below API 26
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return channelIdToUse
        }

        // don't create a channel if it already exists
        if (notificationManager.getNotificationChannel(channelIdToUse) != null) {
            Log.trace(
                LOG_TAG,
                SELF_TAG,
                "Using previously created notification channel: $channelIdToUse."
            )
            return channelIdToUse
        }

        val channel = NotificationChannel(
            channelIdToUse,
            if (pushTemplate.isFromIntent) {
                PushTemplateConstants.DefaultValues.SILENT_CHANNEL_NAME
            } else {
                PushTemplateConstants.DefaultValues.DEFAULT_CHANNEL_NAME
            },
            pushTemplate.getNotificationImportance()
        )

        if (pushTemplate.isFromIntent) {
            channel.setSound(null, null)
        } else {
            val soundUri = if (pushTemplate.sound.isNullOrEmpty()) {
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            } else {
                context.getSoundUriForResourceName(pushTemplate.sound)
            }
            channel.setSound(soundUri, null)
        }

        Log.trace(
            LOG_TAG,
            SELF_TAG,
            "Creating a new notification channel with ID: $channelIdToUse."
        )
        notificationManager.createNotificationChannel(channel)
        return channelIdToUse
    }
}
