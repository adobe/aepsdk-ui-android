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
        mockBundle.putString(PushTemplateConstants.PushPayloadKeys.CAROUSEL_OPERATION_MODE, "manual")
        return mockBundle
    }

    fun getMockedBundleWithAutoCarouselData(): Bundle {
        val mockBundle = getMockedBundleWithCarousalData()
        mockBundle.putString(PushTemplateConstants.PushPayloadKeys.CAROUSEL_OPERATION_MODE, "auto")
        return mockBundle
    }

    private fun getMockedBundleWithCarousalData(): Bundle {
        val mockBundle = Bundle()
        mockBundle.putString(PushTemplateConstants.PushPayloadKeys.TITLE, MOCKED_TITLE)
        mockBundle.putString(PushTemplateConstants.PushPayloadKeys.BODY, MOCKED_BODY)
        mockBundle.putString(PushTemplateConstants.PushPayloadKeys.VERSION, MOCKED_PAYLOAD_VERSION)
        mockBundle.putString(PushTemplateConstants.PushPayloadKeys.TEMPLATE_TYPE, PushTemplateType.CAROUSEL.value)
        mockBundle.putString(PushTemplateConstants.PushPayloadKeys.CAROUSEL_LAYOUT, MOCKED_CAROUSEL_LAYOUT)
        mockBundle.putString(PushTemplateConstants.PushPayloadKeys.BODY_TEXT_COLOR, MOCKED_BODY_TEXT_COLOR)
        mockBundle.putString(PushTemplateConstants.PushPayloadKeys.SMALL_ICON, MOCKED_SMALL_ICON)
        mockBundle.putString(PushTemplateConstants.PushPayloadKeys.LARGE_ICON, MOCKED_LARGE_ICON)
        mockBundle.putString(PushTemplateConstants.PushPayloadKeys.SMALL_ICON_COLOR, MOCKED_SMALL_ICON_COLOR)
        mockBundle.putString(PushTemplateConstants.PushPayloadKeys.VISIBILITY, MOCKED_VISIBILITY)
        mockBundle.putString(PushTemplateConstants.PushPayloadKeys.PRIORITY, MOCKED_PRIORITY)
        mockBundle.putString(PushTemplateConstants.PushPayloadKeys.TICKER, MOCKED_TICKER)
        mockBundle.putString(PushTemplateConstants.PushPayloadKeys.TAG, MOCKED_TAG)
        mockBundle.putString(PushTemplateConstants.PushPayloadKeys.STICKY, "true")
        mockBundle.putString(PushTemplateConstants.PushPayloadKeys.ACTION_URI, MOCKED_URI)
        mockBundle.putString(PushTemplateConstants.PushPayloadKeys.CAROUSEL_ITEMS, MOCKED_CAROUSEL_LAYOUT_DATA)
        return mockBundle
    }
}
