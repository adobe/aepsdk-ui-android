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

import android.os.Bundle
import com.adobe.marketing.mobile.notificationbuilder.internal.PushTemplateType
import com.adobe.marketing.mobile.utils.PushTemplateConstants

object MockTimerTemplateDataProvider {
    internal fun getMockedMapWithTimerData(
        isUsingDuration: Boolean,
        duration: String
    ): MutableMap<String, String> {
        val map = mutableMapOf(
            PushTemplateConstants.PushPayloadKeys.TITLE to MOCKED_TITLE,
            PushTemplateConstants.PushPayloadKeys.BODY to MOCKED_BODY,
            PushTemplateConstants.PushPayloadKeys.EXPANDED_BODY_TEXT to MOCKED_EXPANDED_BODY,
            PushTemplateConstants.PushPayloadKeys.IMAGE_URL to MOCKED_IMAGE_URI,
            PushTemplateConstants.PushPayloadKeys.VERSION to MOCKED_PAYLOAD_VERSION,
            PushTemplateConstants.PushPayloadKeys.TEMPLATE_TYPE to PushTemplateType.TIMER.value,
            PushTemplateConstants.PushPayloadKeys.BODY_TEXT_COLOR to MOCKED_BODY_TEXT_COLOR,
            PushTemplateConstants.PushPayloadKeys.SMALL_ICON to MOCKED_SMALL_ICON,
            PushTemplateConstants.PushPayloadKeys.LARGE_ICON to MOCKED_LARGE_ICON,
            PushTemplateConstants.PushPayloadKeys.SMALL_ICON_COLOR to MOCKED_SMALL_ICON_COLOR,
            PushTemplateConstants.PushPayloadKeys.VISIBILITY to MOCKED_VISIBILITY,
            PushTemplateConstants.PushPayloadKeys.PRIORITY to MOCKED_PRIORITY,
            PushTemplateConstants.PushPayloadKeys.TICKER to MOCKED_TICKER,
            PushTemplateConstants.PushPayloadKeys.STICKY to "true",
            PushTemplateConstants.PushPayloadKeys.TAG to MOCKED_TAG,
            PushTemplateConstants.PushPayloadKeys.ACTION_URI to MOCKED_URI,
            PushTemplateConstants.PushPayloadKeys.TimerKeys.ALTERNATE_EXPANDED_BODY to MOCKED_ALT_EXPANDED_BODY,
            PushTemplateConstants.PushPayloadKeys.TimerKeys.ALTERNATE_TITLE to MOCKED_ALT_TITLE,
            PushTemplateConstants.PushPayloadKeys.TimerKeys.ALTERNATE_BODY to MOCKED_ALT_BODY,
            PushTemplateConstants.PushPayloadKeys.TimerKeys.ALTERNATE_IMAGE to MOCKED_ALT_IMAGE_URI,
            PushTemplateConstants.PushPayloadKeys.TimerKeys.TIMER_COLOR to MOCKED_TIMER_COLOR,
        )
        if (isUsingDuration) {
            map[PushTemplateConstants.PushPayloadKeys.TimerKeys.TIMER_DURATION] = duration
        } else {
            map[PushTemplateConstants.PushPayloadKeys.TimerKeys.TIMER_END_TIME] =
                MOCKED_TIMER_EXPIRY_TIME
        }
        return map
    }

    internal fun getMockedBundleWithTimerData(isUsingDuration: Boolean, duration: String): Bundle {
        val mockBundle = Bundle()
        mockBundle.putString(PushTemplateConstants.PushPayloadKeys.TITLE, MOCKED_TITLE)
        mockBundle.putString(PushTemplateConstants.PushPayloadKeys.BODY, MOCKED_EXPANDED_BODY)
        mockBundle.putString(PushTemplateConstants.PushPayloadKeys.EXPANDED_BODY_TEXT, MOCKED_BODY)
        mockBundle.putString(PushTemplateConstants.PushPayloadKeys.IMAGE_URL, MOCKED_IMAGE_URI)
        mockBundle.putString(PushTemplateConstants.PushPayloadKeys.VERSION, MOCKED_PAYLOAD_VERSION)
        mockBundle.putString(
            PushTemplateConstants.PushPayloadKeys.TEMPLATE_TYPE,
            PushTemplateType.TIMER.value
        )
        mockBundle.putString(
            PushTemplateConstants.PushPayloadKeys.BODY_TEXT_COLOR,
            MOCKED_BODY_TEXT_COLOR
        )
        mockBundle.putString(PushTemplateConstants.PushPayloadKeys.SMALL_ICON, MOCKED_SMALL_ICON)
        mockBundle.putString(PushTemplateConstants.PushPayloadKeys.LARGE_ICON, MOCKED_LARGE_ICON)
        mockBundle.putString(
            PushTemplateConstants.PushPayloadKeys.SMALL_ICON_COLOR,
            MOCKED_SMALL_ICON_COLOR
        )
        mockBundle.putString(PushTemplateConstants.PushPayloadKeys.VISIBILITY, MOCKED_VISIBILITY)
        mockBundle.putString(PushTemplateConstants.PushPayloadKeys.PRIORITY, MOCKED_PRIORITY)
        mockBundle.putString(PushTemplateConstants.PushPayloadKeys.TICKER, MOCKED_TICKER)
        mockBundle.putString(PushTemplateConstants.PushPayloadKeys.TAG, MOCKED_TAG)
        mockBundle.putString(PushTemplateConstants.PushPayloadKeys.STICKY, "true")
        mockBundle.putString(PushTemplateConstants.PushPayloadKeys.ACTION_URI, MOCKED_URI)
        mockBundle.putString(
            PushTemplateConstants.PushPayloadKeys.TimerKeys.ALTERNATE_EXPANDED_BODY,
            MOCKED_ALT_EXPANDED_BODY
        )
        mockBundle.putString(
            PushTemplateConstants.PushPayloadKeys.TimerKeys.ALTERNATE_TITLE,
            MOCKED_ALT_TITLE
        )
        mockBundle.putString(
            PushTemplateConstants.PushPayloadKeys.TimerKeys.ALTERNATE_BODY,
            MOCKED_ALT_BODY
        )
        mockBundle.putString(
            PushTemplateConstants.PushPayloadKeys.TimerKeys.ALTERNATE_IMAGE,
            MOCKED_ALT_IMAGE_URI
        )
        mockBundle.putString(
            PushTemplateConstants.PushPayloadKeys.TimerKeys.TIMER_COLOR,
            MOCKED_TIMER_COLOR
        )
        if (isUsingDuration) {
            mockBundle.putString(
                PushTemplateConstants.PushPayloadKeys.TimerKeys.TIMER_DURATION,
                duration
            )
        } else {
            mockBundle.putString(
                PushTemplateConstants.PushPayloadKeys.TimerKeys.TIMER_END_TIME,
                MOCKED_TIMER_EXPIRY_TIME
            )
        }
        return mockBundle
    }
}
