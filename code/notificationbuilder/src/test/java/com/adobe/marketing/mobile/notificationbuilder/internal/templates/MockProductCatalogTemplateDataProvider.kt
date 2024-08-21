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
import com.adobe.marketing.mobile.notificationbuilder.PushTemplateConstants

object MockProductCatalogTemplateDataProvider {
    fun getMockedMapWithProductCatalogData(): MutableMap<String, String> {
        return mutableMapOf(
            PushTemplateConstants.PushPayloadKeys.TITLE to MOCKED_TITLE,
            PushTemplateConstants.PushPayloadKeys.BODY to MOCKED_BODY,
            PushTemplateConstants.PushPayloadKeys.VERSION to MOCKED_PAYLOAD_VERSION,
            PushTemplateConstants.PushPayloadKeys.CATALOG_CTA_BUTTON_TEXT to "ctaButtonText",
            PushTemplateConstants.PushPayloadKeys.CATALOG_CTA_BUTTON_COLOR to "ctaButtonColor",
            PushTemplateConstants.PushPayloadKeys.CATALOG_CTA_BUTTON_TEXT_COLOR to "ctaButtonTextColor",
            PushTemplateConstants.PushPayloadKeys.CATALOG_CTA_BUTTON_URI to "ctaButtonUri",
            PushTemplateConstants.PushPayloadKeys.CATALOG_LAYOUT to "horizontal",
            PushTemplateConstants.PushPayloadKeys.CATALOG_ITEMS to "[" +
                "{\"title\":\"title1\",\"body\":\"body1\",\"img\":\"https://images.pexels.com/photos/260024/pexels-photo-260024.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2\",\"price\":\"price1\",\"uri\":\"uri1\"}," +
                "{\"title\":\"title2\",\"body\":\"body2\",\"img\":\"https://images.pexels.com/photos/260024/pexels-photo-260024.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2\",\"price\":\"price2\",\"uri\":\"uri2\"}," +
                "{\"title\":\"title3\",\"body\":\"body3\",\"img\":\"https://images.pexels.com/photos/260024/pexels-photo-260024.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2\",\"price\":\"price3\",\"uri\":\"uri3\"}" +
                "]"
        )
    }

    fun getMockedBundleWithProductCatalogData(): Bundle {
        val mockBundle = Bundle()
        mockBundle.putString(PushTemplateConstants.PushPayloadKeys.TITLE, MOCKED_TITLE)
        mockBundle.putString(PushTemplateConstants.PushPayloadKeys.BODY, MOCKED_BODY)
        mockBundle.putString(PushTemplateConstants.PushPayloadKeys.VERSION, MOCKED_PAYLOAD_VERSION)
        mockBundle.putString(
            PushTemplateConstants.PushPayloadKeys.CATALOG_CTA_BUTTON_TEXT,
            "ctaButtonText"
        )
        mockBundle.putString(
            PushTemplateConstants.PushPayloadKeys.CATALOG_CTA_BUTTON_COLOR,
            "ctaButtonColor"
        )
        mockBundle.putString(
            PushTemplateConstants.PushPayloadKeys.CATALOG_CTA_BUTTON_TEXT_COLOR,
            "ctaButtonTextColor"
        )
        mockBundle.putString(
            PushTemplateConstants.PushPayloadKeys.CATALOG_CTA_BUTTON_URI,
            "ctaButtonUri"
        )
        mockBundle.putString(PushTemplateConstants.PushPayloadKeys.CATALOG_LAYOUT, "horizontal")
        mockBundle.putString(
            PushTemplateConstants.PushPayloadKeys.CATALOG_ITEMS,
            "[" +
                "{\"title\":\"title1\",\"body\":\"body1\",\"img\":\"https://images.pexels.com/photos/260024/pexels-photo-260024.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2\",\"price\":\"price1\",\"uri\":\"uri1\"}," +
                "{\"title\":\"title2\",\"body\":\"body2\",\"img\":\"https://images.pexels.com/photos/260024/pexels-photo-260024.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2\",\"price\":\"price2\",\"uri\":\"uri2\"}," +
                "{\"title\":\"title3\",\"body\":\"body3\",\"img\":\"https://images.pexels.com/photos/260024/pexels-photo-260024.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2\",\"price\":\"price3\",\"uri\":\"uri3\"}" +
                "]"
        )
        return mockBundle
    }
}
