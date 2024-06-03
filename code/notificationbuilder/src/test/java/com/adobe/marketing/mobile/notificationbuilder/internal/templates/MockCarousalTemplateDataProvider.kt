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
import com.adobe.marketing.mobile.notificationbuilder.internal.PushTemplateConstants
import com.adobe.marketing.mobile.notificationbuilder.internal.PushTemplateType
import org.mockito.Mockito
import org.mockito.kotlin.mock

object MockCarousalTemplateDataProvider {
    fun getMockedMapWithAutoCarouselData(): MutableMap<String, String> {
        val dataMap = getMockedMapWithCarousalData()
        dataMap[PushTemplateConstants.PushPayloadKeys.CAROUSEL_OPERATION_MODE] = "auto"
        return dataMap
    }

    fun getMockedMapWithManualCarouselData(): MutableMap<String, String> {
        val dataMap = getMockedMapWithCarousalData()
        dataMap[PushTemplateConstants.PushPayloadKeys.CAROUSEL_OPERATION_MODE] = "manual"
        return dataMap
    }

    private fun getMockedMapWithCarousalData(): MutableMap<String, String> {
        return mutableMapOf(
            PushTemplateConstants.PushPayloadKeys.TITLE to MOCKED_TITLE,
            PushTemplateConstants.PushPayloadKeys.BODY to MOCKED_BODY,
            PushTemplateConstants.PushPayloadKeys.VERSION to MOCKED_PAYLOAD_VERSION,
            PushTemplateConstants.PushPayloadKeys.TEMPLATE_TYPE to PushTemplateType.CAROUSEL.value,
            PushTemplateConstants.PushPayloadKeys.CAROUSEL_LAYOUT to MOCKED_CAROUSEL_LAYOUT,
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
            PushTemplateConstants.PushPayloadKeys.CAROUSEL_ITEMS to MOCKED_CAROUSEL_LAYOUT_DATA
        )
    }
    fun getMockedBundleWithManualCarouselData(): Bundle {
        val mockBundle = getMockedBundleWithCarousalData()
        Mockito.`when`(mockBundle.getString(PushTemplateConstants.PushPayloadKeys.CAROUSEL_OPERATION_MODE))
            .thenReturn("manual")
        return mockBundle
    }

    fun getMockedBundleWithAutoCarouselData(): Bundle {
        val mockBundle = getMockedBundleWithCarousalData()
        Mockito.`when`(mockBundle.getString(PushTemplateConstants.PushPayloadKeys.CAROUSEL_OPERATION_MODE))
            .thenReturn("auto")
        return mockBundle
    }

    private fun getMockedBundleWithCarousalData(): Bundle {
        val mockBundle = mock<Bundle>()
        Mockito.`when`(mockBundle.getString(PushTemplateConstants.PushPayloadKeys.TITLE))
            .thenReturn(MOCKED_TITLE)
        Mockito.`when`(mockBundle.getString(PushTemplateConstants.PushPayloadKeys.BODY))
            .thenReturn(MOCKED_BODY)
        Mockito.`when`(mockBundle.getString(PushTemplateConstants.PushPayloadKeys.VERSION))
            .thenReturn(MOCKED_PAYLOAD_VERSION)
        Mockito.`when`(mockBundle.getString(PushTemplateConstants.PushPayloadKeys.TEMPLATE_TYPE))
            .thenReturn(PushTemplateType.CAROUSEL.value)
        Mockito.`when`(mockBundle.getString(PushTemplateConstants.PushPayloadKeys.CAROUSEL_LAYOUT))
            .thenReturn(MOCKED_CAROUSEL_LAYOUT)
        Mockito.`when`(mockBundle.getString(PushTemplateConstants.PushPayloadKeys.BODY_TEXT_COLOR))
            .thenReturn(MOCKED_BODY_TEXT_COLOR)
        Mockito.`when`(mockBundle.getString(PushTemplateConstants.PushPayloadKeys.SMALL_ICON))
            .thenReturn(MOCKED_SMALL_ICON)
        Mockito.`when`(mockBundle.getString(PushTemplateConstants.PushPayloadKeys.LARGE_ICON))
            .thenReturn(MOCKED_LARGE_ICON)
        Mockito.`when`(mockBundle.getString(PushTemplateConstants.PushPayloadKeys.SMALL_ICON_COLOR))
            .thenReturn(MOCKED_SMALL_ICON_COLOR)
        Mockito.`when`(mockBundle.getString(PushTemplateConstants.PushPayloadKeys.VISIBILITY))
            .thenReturn(MOCKED_VISIBILITY)
        Mockito.`when`(mockBundle.getString(PushTemplateConstants.PushPayloadKeys.PRIORITY))
            .thenReturn(MOCKED_PRIORITY)
        Mockito.`when`(mockBundle.getString(PushTemplateConstants.PushPayloadKeys.TICKER))
            .thenReturn(MOCKED_TICKER)
        Mockito.`when`(mockBundle.getString(PushTemplateConstants.PushPayloadKeys.TAG))
            .thenReturn(MOCKED_TAG)
        Mockito.`when`(mockBundle.getString(PushTemplateConstants.PushPayloadKeys.STICKY))
            .thenReturn("true")
        Mockito.`when`(mockBundle.getString(PushTemplateConstants.PushPayloadKeys.ACTION_URI))
            .thenReturn(MOCKED_URI)
        Mockito.`when`(mockBundle.getString(PushTemplateConstants.PushPayloadKeys.CAROUSEL_ITEMS))
            .thenReturn(MOCKED_CAROUSEL_LAYOUT_DATA)
        return mockBundle
    }
}
