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
