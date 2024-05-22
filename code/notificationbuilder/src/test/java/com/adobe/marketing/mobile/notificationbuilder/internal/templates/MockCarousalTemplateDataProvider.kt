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