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
import com.adobe.ui_utils.PushTemplateConstants
import com.adobe.marketing.mobile.notificationbuilder.internal.PushTemplateType

object MockTimerTemplateDataProvider {
    internal fun getMockedMapWithTimerData(
        isUsingDuration: Boolean,
        duration: String
    ): MutableMap<String, String> {
        val map = mutableMapOf(
            com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.TITLE to MOCKED_TITLE,
            com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.BODY to MOCKED_BODY,
            com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.EXPANDED_BODY_TEXT to MOCKED_EXPANDED_BODY,
            com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.IMAGE_URL to MOCKED_IMAGE_URI,
            com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.VERSION to MOCKED_PAYLOAD_VERSION,
            com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.TEMPLATE_TYPE to PushTemplateType.TIMER.value,
            com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.BODY_TEXT_COLOR to MOCKED_BODY_TEXT_COLOR,
            com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.SMALL_ICON to MOCKED_SMALL_ICON,
            com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.LARGE_ICON to MOCKED_LARGE_ICON,
            com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.SMALL_ICON_COLOR to MOCKED_SMALL_ICON_COLOR,
            com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.VISIBILITY to MOCKED_VISIBILITY,
            com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.PRIORITY to MOCKED_PRIORITY,
            com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.TICKER to MOCKED_TICKER,
            com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.STICKY to "true",
            com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.TAG to MOCKED_TAG,
            com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.ACTION_URI to MOCKED_URI,
            com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.TimerKeys.ALTERNATE_EXPANDED_BODY to MOCKED_ALT_EXPANDED_BODY,
            com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.TimerKeys.ALTERNATE_TITLE to MOCKED_ALT_TITLE,
            com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.TimerKeys.ALTERNATE_BODY to MOCKED_ALT_BODY,
            com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.TimerKeys.ALTERNATE_IMAGE to MOCKED_ALT_IMAGE_URI,
            com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.TimerKeys.TIMER_COLOR to MOCKED_TIMER_COLOR,
        )
        if (isUsingDuration) {
            map[com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.TimerKeys.TIMER_DURATION] = duration
        } else {
            map[com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.TimerKeys.TIMER_END_TIME] =
                MOCKED_TIMER_EXPIRY_TIME
        }
        return map
    }

    internal fun getMockedBundleWithTimerData(isUsingDuration: Boolean, duration: String): Bundle {
        val mockBundle = Bundle()
        mockBundle.putString(com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.TITLE, MOCKED_TITLE)
        mockBundle.putString(com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.BODY, MOCKED_EXPANDED_BODY)
        mockBundle.putString(com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.EXPANDED_BODY_TEXT, MOCKED_BODY)
        mockBundle.putString(com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.IMAGE_URL, MOCKED_IMAGE_URI)
        mockBundle.putString(com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.VERSION, MOCKED_PAYLOAD_VERSION)
        mockBundle.putString(
            com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.TEMPLATE_TYPE,
            PushTemplateType.TIMER.value
        )
        mockBundle.putString(
            com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.BODY_TEXT_COLOR,
            MOCKED_BODY_TEXT_COLOR
        )
        mockBundle.putString(com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.SMALL_ICON, MOCKED_SMALL_ICON)
        mockBundle.putString(com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.LARGE_ICON, MOCKED_LARGE_ICON)
        mockBundle.putString(
            com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.SMALL_ICON_COLOR,
            MOCKED_SMALL_ICON_COLOR
        )
        mockBundle.putString(com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.VISIBILITY, MOCKED_VISIBILITY)
        mockBundle.putString(com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.PRIORITY, MOCKED_PRIORITY)
        mockBundle.putString(com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.TICKER, MOCKED_TICKER)
        mockBundle.putString(com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.TAG, MOCKED_TAG)
        mockBundle.putString(com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.STICKY, "true")
        mockBundle.putString(com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.ACTION_URI, MOCKED_URI)
        mockBundle.putString(
            com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.TimerKeys.ALTERNATE_EXPANDED_BODY,
            MOCKED_ALT_EXPANDED_BODY
        )
        mockBundle.putString(
            com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.TimerKeys.ALTERNATE_TITLE,
            MOCKED_ALT_TITLE
        )
        mockBundle.putString(
            com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.TimerKeys.ALTERNATE_BODY,
            MOCKED_ALT_BODY
        )
        mockBundle.putString(
            com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.TimerKeys.ALTERNATE_IMAGE,
            MOCKED_ALT_IMAGE_URI
        )
        mockBundle.putString(
            com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.TimerKeys.TIMER_COLOR,
            MOCKED_TIMER_COLOR
        )
        if (isUsingDuration) {
            mockBundle.putString(
                com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.TimerKeys.TIMER_DURATION,
                duration
            )
        } else {
            mockBundle.putString(
                com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.TimerKeys.TIMER_END_TIME,
                MOCKED_TIMER_EXPIRY_TIME
            )
        }
        return mockBundle
    }
}
