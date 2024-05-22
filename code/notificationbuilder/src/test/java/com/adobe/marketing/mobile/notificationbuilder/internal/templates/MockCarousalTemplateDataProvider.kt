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
import com.adobe.marketing.mobile.notificationbuilder.PushTemplateIntentConstants
import com.adobe.marketing.mobile.notificationbuilder.internal.PushTemplateConstants
import org.mockito.Mockito
import org.mockito.kotlin.mock

object MockCarousalTemplateDataProvider {
    fun getMockedMapWithAutoCarouselData(): Map<String, String> {
        return mapOf(
            PushTemplateConstants.PushPayloadKeys.TITLE to MOCKED_TITLE,
            PushTemplateConstants.PushPayloadKeys.BODY to MOCKED_BODY,
            PushTemplateConstants.PushPayloadKeys.VERSION to MOCKED_PAYLOAD_VERSION.toString(),
            PushTemplateConstants.PushPayloadKeys.CAROUSEL_LAYOUT to "auto",
            PushTemplateConstants.PushPayloadKeys.EXPANDED_BODY_TEXT_COLOR to "#FFFFFF",
            PushTemplateConstants.PushPayloadKeys.SMALL_ICON to "small_icon",
            PushTemplateConstants.PushPayloadKeys.LARGE_ICON to "large_icon",
            PushTemplateConstants.PushPayloadKeys.SMALL_ICON_COLOR to "#000000",
            PushTemplateConstants.PushPayloadKeys.TICKER to "ticker",
            PushTemplateConstants.PushPayloadKeys.TAG to "tag",
            PushTemplateConstants.PushPayloadKeys.STICKY to "true",
            PushTemplateConstants.PushPayloadKeys.ACTION_URI to "action_uri",
            PushTemplateConstants.PushPayloadKeys.CAROUSEL_ITEMS to MOCKED_CAROUSEL_LAYOUT_DATA
        )
    }

    fun getMockedMapWithManualCarouselData(): Map<String, String> {
        return mapOf(
            PushTemplateConstants.PushPayloadKeys.TITLE to MOCKED_TITLE,
            PushTemplateConstants.PushPayloadKeys.BODY to MOCKED_BODY,
            PushTemplateConstants.PushPayloadKeys.VERSION to MOCKED_PAYLOAD_VERSION.toString(),
            PushTemplateConstants.PushPayloadKeys.CAROUSEL_LAYOUT to "manual",
            PushTemplateConstants.PushPayloadKeys.EXPANDED_BODY_TEXT_COLOR to "#FFFFFF",
            PushTemplateConstants.PushPayloadKeys.SMALL_ICON to "small_icon",
            PushTemplateConstants.PushPayloadKeys.LARGE_ICON to "large_icon",
            PushTemplateConstants.PushPayloadKeys.SMALL_ICON_COLOR to "#000000",
            PushTemplateConstants.PushPayloadKeys.TICKER to "ticker",
            PushTemplateConstants.PushPayloadKeys.TAG to "tag",
            PushTemplateConstants.PushPayloadKeys.STICKY to "true",
            PushTemplateConstants.PushPayloadKeys.ACTION_URI to "action_uri",
            PushTemplateConstants.PushPayloadKeys.CAROUSEL_ITEMS to MOCKED_CAROUSEL_LAYOUT_DATA
        )
    }
    fun getMockedBundleWithManualCarouselData(): Bundle {
        val mockBundle = mock<Bundle>()
        Mockito.`when`(mockBundle.getString(PushTemplateIntentConstants.IntentKeys.TITLE_TEXT))
            .thenReturn(MOCKED_TITLE)
        Mockito.`when`(mockBundle.getString(PushTemplateIntentConstants.IntentKeys.BODY_TEXT))
            .thenReturn(MOCKED_BODY)
        Mockito.`when`(mockBundle.getInt(PushTemplateIntentConstants.IntentKeys.PAYLOAD_VERSION))
            .thenReturn(MOCKED_PAYLOAD_VERSION)
        Mockito.`when`(mockBundle.getString(PushTemplateIntentConstants.IntentKeys.CAROUSEL_LAYOUT_TYPE))
            .thenReturn("manual")
        Mockito.`when`(mockBundle.getString(PushTemplateIntentConstants.IntentKeys.EXPANDED_BODY_TEXT_COLOR))
            .thenReturn("#FFFFFF")
        Mockito.`when`(mockBundle.getString(PushTemplateIntentConstants.IntentKeys.SMALL_ICON))
            .thenReturn("small_icon")
        Mockito.`when`(mockBundle.getString(PushTemplateIntentConstants.IntentKeys.LARGE_ICON))
            .thenReturn("large_icon")
        Mockito.`when`(mockBundle.getString(PushTemplateIntentConstants.IntentKeys.SMALL_ICON_COLOR))
            .thenReturn("#000000")
        Mockito.`when`(mockBundle.getInt(PushTemplateIntentConstants.IntentKeys.VISIBILITY))
            .thenReturn(1)
        Mockito.`when`(mockBundle.getInt(PushTemplateIntentConstants.IntentKeys.IMPORTANCE))
            .thenReturn(1)
        Mockito.`when`(mockBundle.getString(PushTemplateIntentConstants.IntentKeys.TICKER))
            .thenReturn("ticker")
        Mockito.`when`(mockBundle.getString(PushTemplateIntentConstants.IntentKeys.TAG))
            .thenReturn("tag")
        Mockito.`when`(mockBundle.getBoolean(PushTemplateIntentConstants.IntentKeys.STICKY))
            .thenReturn(true)
        Mockito.`when`(mockBundle.getString(PushTemplateIntentConstants.IntentKeys.ACTION_URI))
            .thenReturn("action_uri")
        Mockito.`when`(mockBundle.getString(PushTemplateIntentConstants.IntentKeys.CAROUSEL_ITEMS))
            .thenReturn(MOCKED_CAROUSEL_LAYOUT_DATA)
        Mockito.`when`(mockBundle.getString(PushTemplateIntentConstants.IntentKeys.CAROUSEL_OPERATION_MODE))
            .thenReturn("manual")
        return mockBundle
    }

    fun getMockedBundleWithAutoCarouselData(): Bundle {
        val mockBundle = mock<Bundle>()
        Mockito.`when`(mockBundle.getString(PushTemplateIntentConstants.IntentKeys.TITLE_TEXT))
            .thenReturn(MOCKED_TITLE)
        Mockito.`when`(mockBundle.getString(PushTemplateIntentConstants.IntentKeys.BODY_TEXT))
            .thenReturn(MOCKED_BODY)
        Mockito.`when`(mockBundle.getInt(PushTemplateIntentConstants.IntentKeys.PAYLOAD_VERSION))
            .thenReturn(MOCKED_PAYLOAD_VERSION)
        Mockito.`when`(mockBundle.getString(PushTemplateIntentConstants.IntentKeys.CAROUSEL_LAYOUT_TYPE))
            .thenReturn("auto")
        Mockito.`when`(mockBundle.getString(PushTemplateIntentConstants.IntentKeys.EXPANDED_BODY_TEXT_COLOR))
            .thenReturn("#FFFFFF")
        Mockito.`when`(mockBundle.getString(PushTemplateIntentConstants.IntentKeys.SMALL_ICON))
            .thenReturn("small_icon")
        Mockito.`when`(mockBundle.getString(PushTemplateIntentConstants.IntentKeys.LARGE_ICON))
            .thenReturn("large_icon")
        Mockito.`when`(mockBundle.getString(PushTemplateIntentConstants.IntentKeys.SMALL_ICON_COLOR))
            .thenReturn("#000000")
        Mockito.`when`(mockBundle.getInt(PushTemplateIntentConstants.IntentKeys.VISIBILITY))
            .thenReturn(1)
        Mockito.`when`(mockBundle.getInt(PushTemplateIntentConstants.IntentKeys.IMPORTANCE))
            .thenReturn(1)
        Mockito.`when`(mockBundle.getString(PushTemplateIntentConstants.IntentKeys.TICKER))
            .thenReturn("ticker")
        Mockito.`when`(mockBundle.getString(PushTemplateIntentConstants.IntentKeys.TAG))
            .thenReturn("tag")
        Mockito.`when`(mockBundle.getBoolean(PushTemplateIntentConstants.IntentKeys.STICKY))
            .thenReturn(true)
        Mockito.`when`(mockBundle.getString(PushTemplateIntentConstants.IntentKeys.ACTION_URI))
            .thenReturn("action_uri")
        Mockito.`when`(mockBundle.getString(PushTemplateIntentConstants.IntentKeys.CAROUSEL_ITEMS))
            .thenReturn(MOCKED_CAROUSEL_LAYOUT_DATA)
        return mockBundle
    }
}
