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
import com.adobe.marketing.mobile.notificationbuilder.internal.NotificationPriority
import com.adobe.marketing.mobile.notificationbuilder.internal.PushTemplateConstants
import com.adobe.marketing.mobile.notificationbuilder.internal.PushTemplateType
import org.mockito.Mockito
import org.mockito.kotlin.mock

object MockAEPPushTemplateDataProvider {
    fun getMockedDataMapWithRequiredData(): MutableMap<String, String> {
        return mutableMapOf(
            PushTemplateConstants.PushPayloadKeys.TITLE to MOCKED_TITLE,
            PushTemplateConstants.PushPayloadKeys.BODY to MOCKED_BODY,
            PushTemplateConstants.PushPayloadKeys.VERSION to MOCKED_PAYLOAD_VERSION
        )
    }
    /**
     * Returns a mocked data bundle with basic data.
     */
    fun getMockedBundleWithRequiredData(): Bundle {
        val mockBundle = mock<Bundle>()
        Mockito.`when`(mockBundle.getString(PushTemplateConstants.PushPayloadKeys.TITLE))
            .thenReturn(MOCKED_TITLE)
        Mockito.`when`(mockBundle.getString(PushTemplateConstants.PushPayloadKeys.BODY))
            .thenReturn(MOCKED_BODY)
        Mockito.`when`(mockBundle.getString(PushTemplateConstants.PushPayloadKeys.VERSION))
            .thenReturn(MOCKED_PAYLOAD_VERSION)
        return mockBundle
    }

    fun getMockedAEPDataMapWithAllKeys(): MutableMap<String, String> {
        return mutableMapOf(
            PushTemplateConstants.PushPayloadKeys.TAG to MOCKED_TAG,
            PushTemplateConstants.PushPayloadKeys.TITLE to MOCKED_TITLE,
            PushTemplateConstants.PushPayloadKeys.TEMPLATE_TYPE to PushTemplateType.BASIC.value,
            PushTemplateConstants.PushPayloadKeys.ACTION_URI to MOCKED_ACTION_URI,
            PushTemplateConstants.PushPayloadKeys.ACTION_TYPE to PushTemplateConstants.ActionType.NONE.name,
            PushTemplateConstants.PushPayloadKeys.ACTION_BUTTONS to MOCKED_ACTION_BUTTON_DATA,
            PushTemplateConstants.PushPayloadKeys.BADGE_COUNT to "5",
            PushTemplateConstants.PushPayloadKeys.BODY to MOCKED_BASIC_TEMPLATE_BODY,
            PushTemplateConstants.PushPayloadKeys.CHANNEL_ID to "2024",
            PushTemplateConstants.PushPayloadKeys.EXPANDED_BODY_TEXT to MOCKED_BASIC_TEMPLATE_BODY_EXPANDED,
            PushTemplateConstants.PushPayloadKeys.BODY_TEXT_COLOR to "FFD966",
            PushTemplateConstants.PushPayloadKeys.IMAGE_URL to MOCKED_IMAGE_URI,
            PushTemplateConstants.PushPayloadKeys.LARGE_ICON to MOCKED_LARGE_ICON,
            PushTemplateConstants.PushPayloadKeys.BACKGROUND_COLOR to "FFD966",
            PushTemplateConstants.PushPayloadKeys.PRIORITY to NotificationPriority.getNotificationPriority(MOCKED_PRIORITY),
            PushTemplateConstants.PushPayloadKeys.VISIBILITY to MOCKED_VISIBILITY,
            PushTemplateConstants.PushPayloadKeys.REMIND_LATER_TEXT to "remind me",
            PushTemplateConstants.PushPayloadKeys.REMIND_LATER_TIMESTAMP to "1234567890",
            PushTemplateConstants.PushPayloadKeys.SOUND to "bell",
            PushTemplateConstants.PushPayloadKeys.SMALL_ICON to MOCKED_SMALL_ICON,
            PushTemplateConstants.PushPayloadKeys.TITLE_TEXT_COLOR to "FFD966",
            PushTemplateConstants.PushPayloadKeys.TICKER to MOCKED_TICKER,
            PushTemplateConstants.PushPayloadKeys.VERSION to MOCKED_PAYLOAD_VERSION,
            PushTemplateConstants.PushPayloadKeys.STICKY to "true"
        )
    }

    fun getMockedAEPBundleWithAllKeys(): Bundle {
        val mockBundle = mock<Bundle>()
        Mockito.`when`(mockBundle.getString(PushTemplateConstants.PushPayloadKeys.TAG))
            .thenReturn(MOCKED_TAG)
        Mockito.`when`(mockBundle.getString(PushTemplateConstants.PushPayloadKeys.TITLE)).thenReturn(
            MOCKED_TITLE
        )
        Mockito.`when`(mockBundle.getString(PushTemplateConstants.PushPayloadKeys.TEMPLATE_TYPE))
            .thenReturn(PushTemplateType.BASIC.value)
        Mockito.`when`(mockBundle.getString(PushTemplateConstants.PushPayloadKeys.ACTION_URI))
            .thenReturn(MOCKED_ACTION_URI)
        Mockito.`when`(mockBundle.getString(PushTemplateConstants.PushPayloadKeys.ACTION_TYPE))
            .thenReturn(PushTemplateConstants.ActionType.NONE.name)
        Mockito.`when`(mockBundle.getString(PushTemplateConstants.PushPayloadKeys.ACTION_BUTTONS))
            .thenReturn(MOCKED_ACTION_BUTTON_DATA)
        Mockito.`when`(mockBundle.getString(PushTemplateConstants.PushPayloadKeys.BADGE_COUNT))
            .thenReturn("5")
        Mockito.`when`(mockBundle.getString(PushTemplateConstants.PushPayloadKeys.BODY))
            .thenReturn(MOCKED_BASIC_TEMPLATE_BODY)
        Mockito.`when`(mockBundle.getString(PushTemplateConstants.PushPayloadKeys.CHANNEL_ID))
            .thenReturn("2024")
        Mockito.`when`(mockBundle.getString(PushTemplateConstants.PushPayloadKeys.EXPANDED_BODY_TEXT))
            .thenReturn(MOCKED_BASIC_TEMPLATE_BODY_EXPANDED)
        Mockito.`when`(mockBundle.getString(PushTemplateConstants.PushPayloadKeys.BODY_TEXT_COLOR))
            .thenReturn("FFD966")
        Mockito.`when`(mockBundle.getString(PushTemplateConstants.PushPayloadKeys.IMAGE_URL))
            .thenReturn(MOCKED_IMAGE_URI)
        Mockito.`when`(mockBundle.getString(PushTemplateConstants.PushPayloadKeys.LARGE_ICON))
            .thenReturn(MOCKED_LARGE_ICON)
        Mockito.`when`(mockBundle.getString(PushTemplateConstants.PushPayloadKeys.BACKGROUND_COLOR))
            .thenReturn("FFD966")
        Mockito.`when`(mockBundle.getString(PushTemplateConstants.PushPayloadKeys.PRIORITY))
            .thenReturn(NotificationPriority.getNotificationPriority(MOCKED_PRIORITY))
        Mockito.`when`(mockBundle.getString(PushTemplateConstants.PushPayloadKeys.VISIBILITY))
            .thenReturn(MOCKED_VISIBILITY)
        Mockito.`when`(mockBundle.getString(PushTemplateConstants.PushPayloadKeys.REMIND_LATER_TEXT))
            .thenReturn("remind me")
        Mockito.`when`(mockBundle.getString(PushTemplateConstants.PushPayloadKeys.REMIND_LATER_TIMESTAMP))
            .thenReturn("1234567890")
        Mockito.`when`(mockBundle.getString(PushTemplateConstants.PushPayloadKeys.SOUND))
            .thenReturn("bell")
        Mockito.`when`(mockBundle.getString(PushTemplateConstants.PushPayloadKeys.SMALL_ICON))
            .thenReturn(MOCKED_SMALL_ICON)
        Mockito.`when`(mockBundle.getString(PushTemplateConstants.PushPayloadKeys.TITLE_TEXT_COLOR))
            .thenReturn("FFD966")
        Mockito.`when`(mockBundle.getString(PushTemplateConstants.PushPayloadKeys.TICKER))
            .thenReturn(MOCKED_TICKER)
        Mockito.`when`(mockBundle.getString(PushTemplateConstants.PushPayloadKeys.VERSION))
            .thenReturn(MOCKED_PAYLOAD_VERSION)
        Mockito.`when`(mockBundle.getString(PushTemplateConstants.PushPayloadKeys.STICKY))
            .thenReturn("true")
        return mockBundle
    }
}
