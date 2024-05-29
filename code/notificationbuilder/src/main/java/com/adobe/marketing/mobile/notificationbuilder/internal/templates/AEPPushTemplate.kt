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

package com.adobe.marketing.mobile.notificationbuilder.internal.templates

import android.app.NotificationManager
import android.os.Build
import androidx.annotation.RequiresApi
import com.adobe.marketing.mobile.notificationbuilder.internal.NotificationPriority
import com.adobe.marketing.mobile.notificationbuilder.internal.NotificationVisibility
import com.adobe.marketing.mobile.notificationbuilder.internal.PushTemplateConstants.ActionType
import com.adobe.marketing.mobile.notificationbuilder.internal.PushTemplateConstants.PushPayloadKeys
import com.adobe.marketing.mobile.notificationbuilder.internal.PushTemplateType
import com.adobe.marketing.mobile.notificationbuilder.internal.util.IntentData
import com.adobe.marketing.mobile.notificationbuilder.internal.util.NotificationData

/**
 * This class is used to parse the push template data payload or an intent and provide the necessary information
 * to build a notification.
 */
internal sealed class AEPPushTemplate(data: NotificationData) {

    // Message data payload for the push template
    internal lateinit var messageData: MutableMap<String, String>
        private set

    // Required, title of the message shown in the collapsed and expanded push template layouts
    internal val title: String

    // Required, body of the message shown in the collapsed push template layout
    internal val body: String

    // Required, Version of the payload assigned by the authoring UI.
    internal val payloadVersion: String

    // begin optional values
    // Optional, sound to play when the notification is shown
    internal val sound: String?

    // Optional, number to show on the badge of the app
    internal val badgeCount: Int

    // Optional, priority of the notification
    internal val priorityString: String?

    // Optional, visibility of the notification
    internal val visibilityString: String?

    // Optional, notification channel to use when displaying the notification. Only used on Android O and above.
    internal val channelId: String?

    // Optional, small icon for the notification
    internal val smallIcon: String?

    // Optional, large icon for the notification
    internal val largeIcon: String?

    // Optional, image to show in the notification
    internal val imageUrl: String?

    // Optional, action type for the notification
    internal val actionType: ActionType?

    // Optional, action uri for the notification
    internal val actionUri: String?

    // Optional, Body of the message shown in the expanded message layout (setCustomBigContentView)
    internal val expandedBodyText: String?

    // Optional, Text color for adb_body and adb_body_ex. Represented as six character hex, e.g. 00FF00
    internal val bodyTextColor: String?

    // Optional, Text color for adb_title. Represented as six character hex, e.g. 00FF00
    internal val titleTextColor: String?

    // Optional, Color for the notification's small icon. Represented as six character hex, e.g.
    // 00FF00
    internal val smallIconColor: String?

    // Optional, Color for the notification's background. Represented as six character hex, e.g.
    // 00FF00
    internal val backgroundColor: String?

    // Optional, If present and a notification with the same tag is already being shown, the new
    // notification replaces the existing one in the notification drawer.
    internal val tag: String?

    // Optional, If present sets the "ticker" text, which is sent to accessibility services.
    internal val ticker: String?

    // Optional, the type of push template this payload contains
    internal val templateType: PushTemplateType?

    // Optional, when set to false or unset, the notification is automatically dismissed when the
    // user clicks it in the panel. When set to true, the notification persists even when the user
    // clicks it.
    internal val isNotificationSticky: Boolean?

    // flag to denote if the PushTemplate was built from an intent
    internal val isFromIntent: Boolean

    /**
     * Initializes the push template with the given NotificationData.
     *
     * @param data the data to initialize the push template with
     */
    init {
        // extract the notification payload version
        payloadVersion = data.getRequiredString(PushPayloadKeys.VERSION)

        // extract the text information
        title = data.getRequiredString(PushPayloadKeys.TITLE)
        body = data.getRequiredString(PushPayloadKeys.BODY)
        expandedBodyText = data.getString(PushPayloadKeys.EXPANDED_BODY_TEXT)
        ticker = data.getString(PushPayloadKeys.TICKER)

        // extract the template type
        templateType = PushTemplateType.fromString(data.getString(PushPayloadKeys.TEMPLATE_TYPE))
        isFromIntent = data is IntentData

        // extract the basic media information
        imageUrl = data.getString(PushPayloadKeys.IMAGE_URL)

        // extract the action information
        actionUri = data.getString(PushPayloadKeys.ACTION_URI)
        actionType =
            ActionType.valueOf(data.getString(PushPayloadKeys.ACTION_TYPE) ?: ActionType.NONE.name)

        // extract the icon information
        smallIcon = data.getString(PushPayloadKeys.SMALL_ICON)
            ?: data.getString(PushPayloadKeys.LEGACY_SMALL_ICON)
        largeIcon = data.getString(PushPayloadKeys.LARGE_ICON)

        // extract the color components
        titleTextColor = data.getString(PushPayloadKeys.TITLE_TEXT_COLOR)
        bodyTextColor = data.getString(PushPayloadKeys.BODY_TEXT_COLOR)
        backgroundColor = data.getString(PushPayloadKeys.BACKGROUND_COLOR)
        smallIconColor = data.getString(PushPayloadKeys.SMALL_ICON_COLOR)

        // extract the other notification properties
        tag = data.getString(PushPayloadKeys.TAG)
        sound = data.getString(PushPayloadKeys.SOUND)
        channelId = data.getString(PushPayloadKeys.CHANNEL_ID)
        badgeCount = data.getInteger(PushPayloadKeys.BADGE_COUNT) ?: 0
        isNotificationSticky = data.getBoolean(PushPayloadKeys.STICKY)

        // extract notification priority and visibility
        priorityString = data.getString(PushPayloadKeys.PRIORITY)
        visibilityString = data.getString(PushPayloadKeys.VISIBILITY)
    }

    fun getNotificationVisibility(): Int {
        return NotificationVisibility.getNotificationCompatVisibilityFromString(visibilityString)
    }

    fun getNotificationPriority(): Int {
        return NotificationPriority.getNotificationCompatPriorityFromString(priorityString)
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    fun getNotificationImportance(): Int {
        return if (priorityString.isNullOrEmpty()) {
            NotificationManager.IMPORTANCE_DEFAULT
        } else {
            notificationImportanceMap[priorityString] ?: NotificationManager.IMPORTANCE_DEFAULT
        }
    }

    companion object {
        private const val SELF_TAG = "AEPPushTemplate"

        @RequiresApi(api = Build.VERSION_CODES.N)
        internal val notificationImportanceMap: Map<String, Int> = mapOf(
            NotificationPriority.PRIORITY_MIN.toString() to NotificationManager.IMPORTANCE_MIN,
            NotificationPriority.PRIORITY_LOW.toString() to NotificationManager.IMPORTANCE_LOW,
            NotificationPriority.PRIORITY_DEFAULT.toString() to NotificationManager.IMPORTANCE_DEFAULT,
            NotificationPriority.PRIORITY_HIGH.toString() to NotificationManager.IMPORTANCE_HIGH,
            NotificationPriority.PRIORITY_MAX.toString() to NotificationManager.IMPORTANCE_MAX
        )
    }
}
