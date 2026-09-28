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

package com.adobe.marketing.mobile.notificationbuilder.internal.ajo

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.RingtoneManager
import android.os.Build
import com.adobe.marketing.mobile.notificationbuilder.PushTemplateConstants
import com.adobe.marketing.mobile.notificationbuilder.PushTemplateConstants.LOG_TAG
import com.adobe.marketing.mobile.notificationbuilder.internal.extensions.getSoundUriForResourceName
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.AEPPushTemplate
import com.adobe.marketing.mobile.services.Log

/**
 * Notification channel handling shared by the AJO template builders (AJO Basic / AJO Big Text).
 *
 * The logic mirrors the legacy `NotificationManager.createNotificationChannelIfRequired` extension
 * but is kept separate so the AJO builders do not depend on or change the legacy flow.
 */
internal object NotificationChannelUtils {
    private const val SELF_TAG = "AJONotificationChannelUtils"

    /**
     * Returns the channel id for [pushTemplate], creating the channel first if it does not exist.
     *
     * A notification rebuilt from an intent uses the silent channel; otherwise the payload's
     * channel id is used, or the default channel when none is set. Below API 26 there are no
     * channels, so only the id is returned.
     *
     * @param context the application [Context], used to resolve a custom channel sound
     * @param notificationManager the [NotificationManager] used to look up and create channels
     * @param pushTemplate the AJO template providing the channel id, sound and importance
     * @return the channel id to post the notification on
     */
    fun createChannelIfRequired(
        context: Context,
        notificationManager: NotificationManager,
        pushTemplate: AEPPushTemplate
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
