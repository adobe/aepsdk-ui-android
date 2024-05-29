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

package com.adobe.marketing.mobile.notificationbuilder.internal.extensions

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.RingtoneManager
import android.os.Build
import com.adobe.marketing.mobile.notificationbuilder.internal.PushTemplateConstants
import com.adobe.marketing.mobile.notificationbuilder.internal.PushTemplateConstants.LOG_TAG
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.AEPPushTemplate
import com.adobe.marketing.mobile.services.Log

private const val SELF_TAG = "NotificationManagerExtensions"

/**
 * Creates a notification channel if the device is running on Android O or higher. If the channel
 * already exists, the same channel is used. A default channel ID will be used if no channel ID
 * is received from the payload.
 *
 * @param context the application [Context]
 * @param template the push template object
 * @return A [String] containing the created or existing channel ID
 */
internal fun NotificationManager.createNotificationChannelIfRequired(
    context: Context,
    template: AEPPushTemplate
): String {
    // create a silent notification channel if push is from intent
    // if not from intent and channel id is not provided, use the default channel id
    val channelIdToUse =
        if (template.isFromIntent) PushTemplateConstants.DefaultValues.SILENT_NOTIFICATION_CHANNEL_ID
        else template.channelId ?: PushTemplateConstants.DefaultValues.DEFAULT_CHANNEL_ID

    // No channel creation required.
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
        return channelIdToUse
    }

    // Don't create a channel if it already exists
    if (getNotificationChannel(channelIdToUse) != null) {
        Log.trace(
            LOG_TAG,
            SELF_TAG,
            "Using previously created notification channel: $channelIdToUse."
        )
        return channelIdToUse
    }

    // Create a channel
    val channel = NotificationChannel(
        channelIdToUse,
        if (template.isFromIntent) PushTemplateConstants.DefaultValues.SILENT_CHANNEL_NAME else PushTemplateConstants.DefaultValues.DEFAULT_CHANNEL_NAME,
        template.getNotificationImportance()
    )

    // Add a sound if required.
    if (template.isFromIntent) {
        channel.setSound(null, null)
    } else {
        val sound = if (template.sound.isNullOrEmpty()) {
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        } else context.getSoundUriForResourceName(template.sound)
        channel.setSound(sound, null)
    }

    Log.trace(
        LOG_TAG,
        SELF_TAG,
        "Creating a new notification channel with ID: ${template.channelId}. ${if (template.sound.isNullOrEmpty()) "and default sound." else "and custom sound: ${template.sound}."}"
    )
    createNotificationChannel(channel)
    return channelIdToUse
}
