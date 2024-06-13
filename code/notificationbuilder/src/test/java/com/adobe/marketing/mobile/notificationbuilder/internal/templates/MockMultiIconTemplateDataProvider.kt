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

import com.adobe.marketing.mobile.notificationbuilder.PushTemplateConstants
import com.adobe.marketing.mobile.notificationbuilder.internal.PushTemplateType

object MockMultiIconTemplateDataProvider {

    fun getMockedDataMapWithForMultiIcon(): MutableMap<String, String> {
        return mutableMapOf(
            PushTemplateConstants.PushPayloadKeys.TAG to MOCKED_TAG,
            PushTemplateConstants.PushPayloadKeys.TITLE to MOCKED_TITLE,
            PushTemplateConstants.PushPayloadKeys.TEMPLATE_TYPE to PushTemplateType.MULTI_ICON.value,
            PushTemplateConstants.PushPayloadKeys.BODY to "",
            PushTemplateConstants.PushPayloadKeys.CHANNEL_ID to MOCKED_CHANNEL_ID,
            PushTemplateConstants.PushPayloadKeys.PRIORITY to MOCKED_PRIORITY,
            PushTemplateConstants.PushPayloadKeys.VISIBILITY to MOCKED_VISIBILITY,
            PushTemplateConstants.PushPayloadKeys.SOUND to "bell",
            PushTemplateConstants.PushPayloadKeys.SMALL_ICON to MOCKED_SMALL_ICON,
            PushTemplateConstants.PushPayloadKeys.VERSION to MOCKED_PAYLOAD_VERSION,
            PushTemplateConstants.PushPayloadKeys.STICKY to "true",
            PushTemplateConstants.PushPayloadKeys.MULTI_ICON_CLOSE_BUTTON to "delete",
            PushTemplateConstants.PushPayloadKeys.MULTI_ICON_ITEMS to MOCK_MULTI_ICON_ITEM_PAYLOAD,
        )
    }
}
