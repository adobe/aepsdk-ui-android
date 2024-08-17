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

object MockProductRatingTemplateDataProvider {
    fun getMockedDataMapForRatingTemplate(): MutableMap<String, String> {
        return mutableMapOf(
            com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.VERSION to MOCKED_PAYLOAD_VERSION,
            com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.TEMPLATE_TYPE to PushTemplateType.PRODUCT_RATING.value,
            com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.TITLE to MOCKED_TITLE,
            com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.BODY to MOCKED_BODY,
            com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.EXPANDED_BODY_TEXT to MOCKED_BASIC_TEMPLATE_BODY_EXPANDED,
            com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.IMAGE_URL to MOCKED_IMAGE_URI,
            com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.ACTION_TYPE to "WEBURL",
            com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.ACTION_URI to MOCKED_ACTION_URI,
            com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.VERSION to MOCKED_PAYLOAD_VERSION,
            com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.RATING_UNSELECTED_ICON to "rating_star_outline",
            com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.RATING_SELECTED_ICON to "rating_star_filled",
            com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.RATING_ACTIONS to "[{\"uri\":\"https://www.adobe.com\", \"type\":\"WEBURL\"},{\"type\":\"OPENAPP\"},{\"uri\":\"https://www.adobe.com\", \"type\":\"WEBURL\"},{\"uri\": \"https://www.adobe.com\", \"type\":\"WEBURL\"},{\"uri\":\"https://www.adobe.com\", \"type\":\"WEBURL\"}]"
        )
    }

    fun getMockedBundleForRatingTemplate(): Bundle {
        val mockBundle = Bundle()
        mockBundle.putString(com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.VERSION, MOCKED_PAYLOAD_VERSION)
        mockBundle.putString(com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.TEMPLATE_TYPE, PushTemplateType.PRODUCT_RATING.value)
        mockBundle.putString(com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.TITLE, MOCKED_TITLE)
        mockBundle.putString(com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.BODY, MOCKED_BODY)
        mockBundle.putString(com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.EXPANDED_BODY_TEXT, MOCKED_BASIC_TEMPLATE_BODY_EXPANDED)
        mockBundle.putString(com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.IMAGE_URL, MOCKED_IMAGE_URI)
        mockBundle.putString(com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.ACTION_TYPE, "WEBURL")
        mockBundle.putString(com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.ACTION_URI, MOCKED_ACTION_URI)
        mockBundle.putString(com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.VERSION, MOCKED_PAYLOAD_VERSION)
        mockBundle.putString(com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.RATING_UNSELECTED_ICON, "rating_star_outline")
        mockBundle.putString(com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.RATING_SELECTED_ICON, "rating_star_filled")
        mockBundle.putString(com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.RATING_ACTIONS, "[{\"uri\":\"https://www.adobe.com\", \"type\":\"WEBURL\"},{\"type\":\"OPENAPP\"},{\"uri\":\"https://www.adobe.com\", \"type\":\"WEBURL\"},{\"uri\": \"https://www.adobe.com\", \"type\":\"WEBURL\"},{\"uri\":\"https://www.adobe.com\", \"type\":\"WEBURL\"}]")
        return mockBundle
    }
}
