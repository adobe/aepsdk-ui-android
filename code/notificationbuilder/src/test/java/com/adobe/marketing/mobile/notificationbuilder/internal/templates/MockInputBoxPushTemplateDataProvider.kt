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

object MockInputBoxPushTemplateDataProvider {
    fun getMockedInputBoxDataMapWithRequiredData(): MutableMap<String, String> {
        return mutableMapOf(
            com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.TITLE to MOCKED_TITLE,
            com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.BODY to MOCKED_BODY,
            com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.VERSION to MOCKED_PAYLOAD_VERSION,
            com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.INPUT_BOX_RECEIVER_NAME to MOCKED_RECEIVER_NAME,
            com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.INPUT_BOX_HINT to MOCKED_HINT,
            com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.INPUT_BOX_FEEDBACK_TEXT to MOCKED_FEEDBACK_TEXT,
            com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.INPUT_BOX_FEEDBACK_IMAGE to MOCKED_FEEDBACK_IMAGE
        )
    }

    fun getMockedInputBoxBundleWithRequiredData(): Bundle {
        val mockBundle = Bundle()
        mockBundle.putString(com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.TITLE, MOCKED_TITLE)
        mockBundle.putString(com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.BODY, MOCKED_BODY)
        mockBundle.putString(com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.VERSION, MOCKED_PAYLOAD_VERSION)
        mockBundle.putString(com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.INPUT_BOX_RECEIVER_NAME, MOCKED_RECEIVER_NAME)
        mockBundle.putString(com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.INPUT_BOX_HINT, MOCKED_HINT)
        mockBundle.putString(com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.INPUT_BOX_FEEDBACK_TEXT, MOCKED_FEEDBACK_TEXT)
        mockBundle.putString(com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.INPUT_BOX_FEEDBACK_IMAGE, MOCKED_FEEDBACK_IMAGE)
        return mockBundle
    }

    fun getMockedInputBoxDataMapWithAllKeys(): MutableMap<String, String> {
        return mutableMapOf(
            com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.TAG to MOCKED_TAG,
            com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.TITLE to MOCKED_TITLE,
            com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.TEMPLATE_TYPE to PushTemplateType.BASIC.value,
            com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.BADGE_COUNT to "5",
            com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.BODY to MOCKED_BASIC_TEMPLATE_BODY,
            com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.CHANNEL_ID to MOCKED_CHANNEL_ID,
            com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.EXPANDED_BODY_TEXT to MOCKED_BASIC_TEMPLATE_BODY_EXPANDED,
            com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.BODY_TEXT_COLOR to "FFD966",
            com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.IMAGE_URL to MOCKED_IMAGE_URI,
            com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.LARGE_ICON to MOCKED_LARGE_ICON,
            com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.BACKGROUND_COLOR to "FFD966",
            com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.PRIORITY to MOCKED_PRIORITY,
            com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.VISIBILITY to MOCKED_VISIBILITY,
            com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.SOUND to "bell",
            com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.SMALL_ICON to MOCKED_SMALL_ICON,
            com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.TITLE_TEXT_COLOR to "FFD966",
            com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.TICKER to MOCKED_TICKER,
            com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.VERSION to MOCKED_PAYLOAD_VERSION,
            com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.STICKY to "true",
            com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.INPUT_BOX_RECEIVER_NAME to MOCKED_RECEIVER_NAME,
            com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.INPUT_BOX_HINT to MOCKED_HINT,
            com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.INPUT_BOX_FEEDBACK_TEXT to MOCKED_FEEDBACK_TEXT,
            com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.INPUT_BOX_FEEDBACK_IMAGE to MOCKED_FEEDBACK_IMAGE
        )
    }

    fun getMockedInputBoxBundleWithAllKeys(): Bundle {
        val mockBundle = Bundle()
        mockBundle.putString(com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.VERSION, MOCKED_PAYLOAD_VERSION)
        mockBundle.putString(com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.TITLE, MOCKED_TITLE)
        mockBundle.putString(com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.BODY, MOCKED_BODY)
        mockBundle.putString(com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.TAG, MOCKED_TAG)
        mockBundle.putString(com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.TITLE, MOCKED_TITLE)
        mockBundle.putString(com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.TEMPLATE_TYPE, PushTemplateType.BASIC.value)
        mockBundle.putString(com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.BADGE_COUNT, "5")
        mockBundle.putString(com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.BODY, MOCKED_BASIC_TEMPLATE_BODY)
        mockBundle.putString(com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.CHANNEL_ID, "2024")
        mockBundle.putString(com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.EXPANDED_BODY_TEXT, MOCKED_BASIC_TEMPLATE_BODY_EXPANDED)
        mockBundle.putString(com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.BODY_TEXT_COLOR, "FFD966")
        mockBundle.putString(com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.IMAGE_URL, MOCKED_IMAGE_URI)
        mockBundle.putString(com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.LARGE_ICON, MOCKED_LARGE_ICON)
        mockBundle.putString(com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.BACKGROUND_COLOR, "FFD966")
        mockBundle.putString(com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.PRIORITY, (MOCKED_PRIORITY))
        mockBundle.putString(com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.VISIBILITY, MOCKED_VISIBILITY)
        mockBundle.putString(com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.SOUND, "bell")
        mockBundle.putString(com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.SMALL_ICON, MOCKED_SMALL_ICON)
        mockBundle.putString(com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.TITLE_TEXT_COLOR, "FFD966")
        mockBundle.putString(com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.TICKER, MOCKED_TICKER)
        mockBundle.putString(com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.VERSION, MOCKED_PAYLOAD_VERSION)
        mockBundle.putString(com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.STICKY, "true")
        mockBundle.putString(com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.INPUT_BOX_RECEIVER_NAME, MOCKED_RECEIVER_NAME)
        mockBundle.putString(com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.INPUT_BOX_HINT, MOCKED_HINT)
        mockBundle.putString(com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.INPUT_BOX_FEEDBACK_TEXT, MOCKED_FEEDBACK_TEXT)
        mockBundle.putString(com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.INPUT_BOX_FEEDBACK_IMAGE, MOCKED_FEEDBACK_IMAGE)
        return mockBundle
    }
}
