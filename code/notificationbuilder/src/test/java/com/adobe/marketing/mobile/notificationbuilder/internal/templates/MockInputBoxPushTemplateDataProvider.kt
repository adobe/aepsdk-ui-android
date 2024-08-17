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
import com.adobe.ui_utils.PushTemplateConstants

object MockInputBoxPushTemplateDataProvider {
    fun getMockedInputBoxDataMapWithRequiredData(): MutableMap<String, String> {
        return mutableMapOf(
            PushTemplateConstants.PushPayloadKeys.TITLE to MOCKED_TITLE,
            PushTemplateConstants.PushPayloadKeys.BODY to MOCKED_BODY,
            PushTemplateConstants.PushPayloadKeys.VERSION to MOCKED_PAYLOAD_VERSION,
            PushTemplateConstants.PushPayloadKeys.INPUT_BOX_RECEIVER_NAME to MOCKED_RECEIVER_NAME,
            PushTemplateConstants.PushPayloadKeys.INPUT_BOX_HINT to MOCKED_HINT,
            PushTemplateConstants.PushPayloadKeys.INPUT_BOX_FEEDBACK_TEXT to MOCKED_FEEDBACK_TEXT,
            PushTemplateConstants.PushPayloadKeys.INPUT_BOX_FEEDBACK_IMAGE to MOCKED_FEEDBACK_IMAGE
        )
    }

    fun getMockedInputBoxBundleWithRequiredData(): Bundle {
        val mockBundle = Bundle()
        mockBundle.putString(PushTemplateConstants.PushPayloadKeys.TITLE, MOCKED_TITLE)
        mockBundle.putString(PushTemplateConstants.PushPayloadKeys.BODY, MOCKED_BODY)
        mockBundle.putString(PushTemplateConstants.PushPayloadKeys.VERSION, MOCKED_PAYLOAD_VERSION)
        mockBundle.putString(PushTemplateConstants.PushPayloadKeys.INPUT_BOX_RECEIVER_NAME, MOCKED_RECEIVER_NAME)
        mockBundle.putString(PushTemplateConstants.PushPayloadKeys.INPUT_BOX_HINT, MOCKED_HINT)
        mockBundle.putString(PushTemplateConstants.PushPayloadKeys.INPUT_BOX_FEEDBACK_TEXT, MOCKED_FEEDBACK_TEXT)
        mockBundle.putString(PushTemplateConstants.PushPayloadKeys.INPUT_BOX_FEEDBACK_IMAGE, MOCKED_FEEDBACK_IMAGE)
        return mockBundle
    }

    fun getMockedInputBoxDataMapWithAllKeys(): MutableMap<String, String> {
        return mutableMapOf(
            PushTemplateConstants.PushPayloadKeys.TAG to MOCKED_TAG,
            PushTemplateConstants.PushPayloadKeys.TITLE to MOCKED_TITLE,
            PushTemplateConstants.PushPayloadKeys.TEMPLATE_TYPE to PushTemplateType.BASIC.value,
            PushTemplateConstants.PushPayloadKeys.BADGE_COUNT to "5",
            PushTemplateConstants.PushPayloadKeys.BODY to MOCKED_BASIC_TEMPLATE_BODY,
            PushTemplateConstants.PushPayloadKeys.CHANNEL_ID to MOCKED_CHANNEL_ID,
            PushTemplateConstants.PushPayloadKeys.EXPANDED_BODY_TEXT to MOCKED_BASIC_TEMPLATE_BODY_EXPANDED,
            PushTemplateConstants.PushPayloadKeys.BODY_TEXT_COLOR to "FFD966",
            PushTemplateConstants.PushPayloadKeys.IMAGE_URL to MOCKED_IMAGE_URI,
            PushTemplateConstants.PushPayloadKeys.LARGE_ICON to MOCKED_LARGE_ICON,
            PushTemplateConstants.PushPayloadKeys.BACKGROUND_COLOR to "FFD966",
            PushTemplateConstants.PushPayloadKeys.PRIORITY to MOCKED_PRIORITY,
            PushTemplateConstants.PushPayloadKeys.VISIBILITY to MOCKED_VISIBILITY,
            PushTemplateConstants.PushPayloadKeys.SOUND to "bell",
            PushTemplateConstants.PushPayloadKeys.SMALL_ICON to MOCKED_SMALL_ICON,
            PushTemplateConstants.PushPayloadKeys.TITLE_TEXT_COLOR to "FFD966",
            PushTemplateConstants.PushPayloadKeys.TICKER to MOCKED_TICKER,
            PushTemplateConstants.PushPayloadKeys.VERSION to MOCKED_PAYLOAD_VERSION,
            PushTemplateConstants.PushPayloadKeys.STICKY to "true",
            PushTemplateConstants.PushPayloadKeys.INPUT_BOX_RECEIVER_NAME to MOCKED_RECEIVER_NAME,
            PushTemplateConstants.PushPayloadKeys.INPUT_BOX_HINT to MOCKED_HINT,
            PushTemplateConstants.PushPayloadKeys.INPUT_BOX_FEEDBACK_TEXT to MOCKED_FEEDBACK_TEXT,
            PushTemplateConstants.PushPayloadKeys.INPUT_BOX_FEEDBACK_IMAGE to MOCKED_FEEDBACK_IMAGE
        )
    }

    fun getMockedInputBoxBundleWithAllKeys(): Bundle {
        val mockBundle = Bundle()
        mockBundle.putString(PushTemplateConstants.PushPayloadKeys.VERSION, MOCKED_PAYLOAD_VERSION)
        mockBundle.putString(PushTemplateConstants.PushPayloadKeys.TITLE, MOCKED_TITLE)
        mockBundle.putString(PushTemplateConstants.PushPayloadKeys.BODY, MOCKED_BODY)
        mockBundle.putString(PushTemplateConstants.PushPayloadKeys.TAG, MOCKED_TAG)
        mockBundle.putString(PushTemplateConstants.PushPayloadKeys.TITLE, MOCKED_TITLE)
        mockBundle.putString(PushTemplateConstants.PushPayloadKeys.TEMPLATE_TYPE, PushTemplateType.BASIC.value)
        mockBundle.putString(PushTemplateConstants.PushPayloadKeys.BADGE_COUNT, "5")
        mockBundle.putString(PushTemplateConstants.PushPayloadKeys.BODY, MOCKED_BASIC_TEMPLATE_BODY)
        mockBundle.putString(PushTemplateConstants.PushPayloadKeys.CHANNEL_ID, "2024")
        mockBundle.putString(PushTemplateConstants.PushPayloadKeys.EXPANDED_BODY_TEXT, MOCKED_BASIC_TEMPLATE_BODY_EXPANDED)
        mockBundle.putString(PushTemplateConstants.PushPayloadKeys.BODY_TEXT_COLOR, "FFD966")
        mockBundle.putString(PushTemplateConstants.PushPayloadKeys.IMAGE_URL, MOCKED_IMAGE_URI)
        mockBundle.putString(PushTemplateConstants.PushPayloadKeys.LARGE_ICON, MOCKED_LARGE_ICON)
        mockBundle.putString(PushTemplateConstants.PushPayloadKeys.BACKGROUND_COLOR, "FFD966")
        mockBundle.putString(PushTemplateConstants.PushPayloadKeys.PRIORITY, (MOCKED_PRIORITY))
        mockBundle.putString(PushTemplateConstants.PushPayloadKeys.VISIBILITY, MOCKED_VISIBILITY)
        mockBundle.putString(PushTemplateConstants.PushPayloadKeys.SOUND, "bell")
        mockBundle.putString(PushTemplateConstants.PushPayloadKeys.SMALL_ICON, MOCKED_SMALL_ICON)
        mockBundle.putString(PushTemplateConstants.PushPayloadKeys.TITLE_TEXT_COLOR, "FFD966")
        mockBundle.putString(PushTemplateConstants.PushPayloadKeys.TICKER, MOCKED_TICKER)
        mockBundle.putString(PushTemplateConstants.PushPayloadKeys.VERSION, MOCKED_PAYLOAD_VERSION)
        mockBundle.putString(PushTemplateConstants.PushPayloadKeys.STICKY, "true")
        mockBundle.putString(PushTemplateConstants.PushPayloadKeys.INPUT_BOX_RECEIVER_NAME, MOCKED_RECEIVER_NAME)
        mockBundle.putString(PushTemplateConstants.PushPayloadKeys.INPUT_BOX_HINT, MOCKED_HINT)
        mockBundle.putString(PushTemplateConstants.PushPayloadKeys.INPUT_BOX_FEEDBACK_TEXT, MOCKED_FEEDBACK_TEXT)
        mockBundle.putString(PushTemplateConstants.PushPayloadKeys.INPUT_BOX_FEEDBACK_IMAGE, MOCKED_FEEDBACK_IMAGE)
        return mockBundle
    }
}
