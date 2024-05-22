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

object MockAEPPushTemplateDataProvider {
    fun getMockedDataMapWithBasicData(): HashMap<String, String> {
        return hashMapOf(
            Pair(PushTemplateConstants.PushPayloadKeys.TITLE, MOCKED_TITLE),
            Pair(PushTemplateConstants.PushPayloadKeys.BODY, MOCKED_BODY),
            Pair(PushTemplateConstants.PushPayloadKeys.VERSION, MOCKED_PAYLOAD_VERSION.toString())
        )
    }
    /**
     * Returns a mocked data bundle with basic data.
     */
    fun getMockedBundleWithMinimalData(): Bundle {
        val mockBundle = mock<Bundle>()
        Mockito.`when`(mockBundle.getString(PushTemplateIntentConstants.IntentKeys.TITLE_TEXT))
            .thenReturn(MOCKED_TITLE)
        Mockito.`when`(mockBundle.getString(PushTemplateIntentConstants.IntentKeys.BODY_TEXT))
            .thenReturn(MOCKED_BODY)
        Mockito.`when`(mockBundle.getInt(PushTemplateIntentConstants.IntentKeys.PAYLOAD_VERSION))
            .thenReturn(MOCKED_PAYLOAD_VERSION)
        return mockBundle
    }

    fun getMockedBundleWithoutTitle(): Bundle {
        val mockBundle = mock<Bundle>()
        Mockito.`when`(mockBundle.getString(PushTemplateIntentConstants.IntentKeys.BODY_TEXT))
            .thenReturn(MOCKED_BODY)
        Mockito.`when`(mockBundle.getInt(PushTemplateIntentConstants.IntentKeys.PAYLOAD_VERSION))
            .thenReturn(MOCKED_PAYLOAD_VERSION)
        return mockBundle
    }

    fun getMockedBundleWithoutBody(): Bundle {
        val mockBundle = mock<Bundle>()
        Mockito.`when`(mockBundle.getString(PushTemplateIntentConstants.IntentKeys.TITLE_TEXT))
            .thenReturn(MOCKED_TITLE)
        Mockito.`when`(mockBundle.getInt(PushTemplateIntentConstants.IntentKeys.PAYLOAD_VERSION))
            .thenReturn(MOCKED_PAYLOAD_VERSION)
        return mockBundle
    }
}
