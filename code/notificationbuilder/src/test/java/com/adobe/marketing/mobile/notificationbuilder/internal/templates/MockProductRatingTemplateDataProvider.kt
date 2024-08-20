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

object MockProductRatingTemplateDataProvider {
    fun getMockedDataMapForRatingTemplate(): MutableMap<String, String> {
        return mutableMapOf(
            PushTemplateConstants.PushPayloadKeys.VERSION to MOCKED_PAYLOAD_VERSION,
            PushTemplateConstants.PushPayloadKeys.TEMPLATE_TYPE to PushTemplateType.PRODUCT_RATING.value,
            PushTemplateConstants.PushPayloadKeys.TITLE to MOCKED_TITLE,
            PushTemplateConstants.PushPayloadKeys.BODY to MOCKED_BODY,
            PushTemplateConstants.PushPayloadKeys.EXPANDED_BODY_TEXT to MOCKED_BASIC_TEMPLATE_BODY_EXPANDED,
            PushTemplateConstants.PushPayloadKeys.IMAGE_URL to MOCKED_IMAGE_URI,
            PushTemplateConstants.PushPayloadKeys.ACTION_TYPE to "WEBURL",
            PushTemplateConstants.PushPayloadKeys.ACTION_URI to MOCKED_ACTION_URI,
            PushTemplateConstants.PushPayloadKeys.VERSION to MOCKED_PAYLOAD_VERSION,
            PushTemplateConstants.PushPayloadKeys.RATING_UNSELECTED_ICON to "rating_star_outline",
            PushTemplateConstants.PushPayloadKeys.RATING_SELECTED_ICON to "rating_star_filled",
            PushTemplateConstants.PushPayloadKeys.RATING_ACTIONS to "[{\"uri\":\"https://www.adobe.com\", \"type\":\"WEBURL\"},{\"type\":\"OPENAPP\"},{\"uri\":\"https://www.adobe.com\", \"type\":\"WEBURL\"},{\"uri\": \"https://www.adobe.com\", \"type\":\"WEBURL\"},{\"uri\":\"https://www.adobe.com\", \"type\":\"WEBURL\"}]"
        )
    }

    fun getMockedBundleForRatingTemplate(): Bundle {
        val mockBundle = Bundle()
        mockBundle.putString(PushTemplateConstants.PushPayloadKeys.VERSION, MOCKED_PAYLOAD_VERSION)
        mockBundle.putString(PushTemplateConstants.PushPayloadKeys.TEMPLATE_TYPE, PushTemplateType.PRODUCT_RATING.value)
        mockBundle.putString(PushTemplateConstants.PushPayloadKeys.TITLE, MOCKED_TITLE)
        mockBundle.putString(PushTemplateConstants.PushPayloadKeys.BODY, MOCKED_BODY)
        mockBundle.putString(PushTemplateConstants.PushPayloadKeys.EXPANDED_BODY_TEXT, MOCKED_BASIC_TEMPLATE_BODY_EXPANDED)
        mockBundle.putString(PushTemplateConstants.PushPayloadKeys.IMAGE_URL, MOCKED_IMAGE_URI)
        mockBundle.putString(PushTemplateConstants.PushPayloadKeys.ACTION_TYPE, "WEBURL")
        mockBundle.putString(PushTemplateConstants.PushPayloadKeys.ACTION_URI, MOCKED_ACTION_URI)
        mockBundle.putString(PushTemplateConstants.PushPayloadKeys.VERSION, MOCKED_PAYLOAD_VERSION)
        mockBundle.putString(PushTemplateConstants.PushPayloadKeys.RATING_UNSELECTED_ICON, "rating_star_outline")
        mockBundle.putString(PushTemplateConstants.PushPayloadKeys.RATING_SELECTED_ICON, "rating_star_filled")
        mockBundle.putString(PushTemplateConstants.PushPayloadKeys.RATING_ACTIONS, "[{\"uri\":\"https://www.adobe.com\", \"type\":\"WEBURL\"},{\"type\":\"OPENAPP\"},{\"uri\":\"https://www.adobe.com\", \"type\":\"WEBURL\"},{\"uri\": \"https://www.adobe.com\", \"type\":\"WEBURL\"},{\"uri\":\"https://www.adobe.com\", \"type\":\"WEBURL\"}]")
        return mockBundle
    }
}
