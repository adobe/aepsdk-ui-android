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

import com.adobe.marketing.mobile.notificationbuilder.internal.util.NotificationData
import com.adobe.marketing.mobile.util.TimeUtils
import com.adobe.marketing.mobile.utils.PushTemplateConstants.PushPayloadKeys.TimerKeys

/**
 * Class for parsing the data required to display a Timer notification.
 */
internal class TimerPushTemplate(data: NotificationData) : AEPPushTemplate(data) {

    data class TimerContent(
        val title: String,
        val body: String?,
        val expandedBody: String?,
        val imageUrl: String?
    )

    internal val alternateTitle: String
    internal val alternateBody: String?
    internal val alternateExpandedBody: String?
    internal val alternateImage: String?
    internal val timerColor: String?
    internal val expiryTime: Long
    internal val timerContent: TimerContent

    /**
     * Initialize the TimerPushTemplate with the provided [NotificationData].
     */
    init {
        alternateTitle = data.getRequiredString(TimerKeys.ALTERNATE_TITLE)
        expiryTime = extractExpiryTime(data) ?: 0
        alternateBody = data.getString(TimerKeys.ALTERNATE_BODY)
        alternateExpandedBody = data.getString(TimerKeys.ALTERNATE_EXPANDED_BODY)
        alternateImage = data.getString(TimerKeys.ALTERNATE_IMAGE)
        timerColor = data.getString(TimerKeys.TIMER_COLOR)

        timerContent = if (isExpired()) {
            TimerContent(
                title = alternateTitle,
                body = alternateBody,
                expandedBody = alternateExpandedBody,
                imageUrl = alternateImage
            )
        } else {
            TimerContent(
                title = title,
                body = body,
                expandedBody = expandedBodyText,
                imageUrl = imageUrl
            )
        }
    }

    /**
     * Returns true if the timer has expired, false otherwise.
     */
    internal fun isExpired(): Boolean {
        return expiryTime.let { TimeUtils.getUnixTimeInSeconds() > it }
    }

    /**
     * Returns the timestamp when the timer will expire.
     *
     * @return the expiry time in seconds
     */
    private fun extractExpiryTime(data: NotificationData): Long? {
        val duration = data.getString(TimerKeys.TIMER_DURATION)?.toLongOrNull()
        val endTimestamp = data.getString(TimerKeys.TIMER_END_TIME)?.toLongOrNull()
        // If duration is provided, calculate the expiry time based on the current time.
        // If endTimestamp is provided, use it as the expiry time.
        // duration takes precedence over endTimestamp, if both are provided.
        return when {
            duration != null -> TimeUtils.getUnixTimeInSeconds() + duration
            endTimestamp != null -> endTimestamp
            else -> null
        }
    }
}
