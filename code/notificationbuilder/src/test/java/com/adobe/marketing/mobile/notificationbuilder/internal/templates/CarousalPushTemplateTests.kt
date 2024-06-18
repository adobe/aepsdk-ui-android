package com.adobe.marketing.mobile.notificationbuilder.internal.templates

import android.os.Bundle
import com.adobe.marketing.mobile.notificationbuilder.PushTemplateConstants
import com.adobe.marketing.mobile.notificationbuilder.internal.PushTemplateType
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.MockCarousalTemplateDataProvider.getMockedBundleWithAutoCarouselData
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.MockCarousalTemplateDataProvider.getMockedBundleWithManualCarouselData
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.MockCarousalTemplateDataProvider.getMockedMapWithAutoCarouselData
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.MockCarousalTemplateDataProvider.getMockedMapWithManualCarouselData
import com.adobe.marketing.mobile.notificationbuilder.internal.util.IntentData
import com.adobe.marketing.mobile.notificationbuilder.internal.util.MapData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.Test
import kotlin.test.assertFailsWith

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [31])
class CarousalPushTemplateTests {
    @Test
    fun `Test AutoCarouselPushTemplate initialization with Intent`() {
        val mockBundle = getMockedBundleWithAutoCarouselData()
        val template = CarouselPushTemplate(IntentData(mockBundle, null))
        assertEquals(PushTemplateType.CAROUSEL, template.templateType)
        assertTrue(template is AutoCarouselPushTemplate)
        assertEquals(MOCKED_CAROUSEL_LAYOUT_DATA, template.rawCarouselItems)
    }

   @Test
    fun `Test ManualCarouselPushTemplate initialization with Intent`() {
        val mockBundle = getMockedBundleWithManualCarouselData()
        val template = CarouselPushTemplate(IntentData(mockBundle, null))
        assertEquals(PushTemplateType.CAROUSEL, template.templateType)
        assertTrue(template is ManualCarouselPushTemplate)
        assertEquals(MOCKED_CAROUSEL_LAYOUT_DATA, template.rawCarouselItems)
    }

    @Test
    fun `Test AutoCarouselPushTemplate initialization with MapData`() {
        val mockedMap = getMockedMapWithAutoCarouselData()
        val template = CarouselPushTemplate(MapData(mockedMap))
        assertEquals(PushTemplateType.CAROUSEL, template.templateType)
        assertTrue(template is AutoCarouselPushTemplate)
        assertEquals(MOCKED_CAROUSEL_LAYOUT_DATA, template.rawCarouselItems)
    }

    @Test
    fun `Test ManualCarouselPushTemplate initialization with MapData`() {
        val mockedMap = getMockedMapWithManualCarouselData()
        val template =  CarouselPushTemplate(MapData(mockedMap))
        assertEquals(PushTemplateType.CAROUSEL, template.templateType)
        assertTrue(template is ManualCarouselPushTemplate)
        assertEquals(MOCKED_CAROUSEL_LAYOUT_DATA, template.rawCarouselItems)
    }
    @Test
    fun `Test CarouselPushTemplate initialization with missing carouselLayout`() {
        val mockedMap = getMockedMapWithAutoCarouselData().apply {
            remove(PushTemplateConstants.PushPayloadKeys.CAROUSEL_LAYOUT)
        }
        val exception = assertFailsWith<IllegalArgumentException> {
            CarouselPushTemplate(MapData(mockedMap))
        }
        assertEquals("Required push template key ${PushTemplateConstants.PushPayloadKeys.CAROUSEL_LAYOUT} not found or null", exception.message)
    }

    @Test
    fun `Test CarouselPushTemplate initialization with missing rawCarouselItems`() {
        val mockedMap = getMockedMapWithAutoCarouselData().apply {
            remove(PushTemplateConstants.PushPayloadKeys.CAROUSEL_ITEMS)
        }
        val exception = assertFailsWith<IllegalArgumentException> {
            CarouselPushTemplate(MapData(mockedMap))
        }
        assertEquals("Required push template key ${PushTemplateConstants.PushPayloadKeys.CAROUSEL_ITEMS} not found or null", exception.message)
    }

    @Test
    fun `Test CarouselPushTemplate initialization with empty rawCarouselItems`() {
        val mockedMap = getMockedMapWithAutoCarouselData().apply {
            put(PushTemplateConstants.PushPayloadKeys.CAROUSEL_ITEMS, "")
        }
        val template = CarouselPushTemplate(MapData(mockedMap))
        assertEquals(PushTemplateType.CAROUSEL, template.templateType)
        assertTrue(template.carouselItems.isEmpty())
    }

    @Test
    fun `Test CarouselPushTemplate initialization with malformed rawCarouselItems`() {
        val mockedMap = getMockedMapWithAutoCarouselData().apply {
            put(PushTemplateConstants.PushPayloadKeys.CAROUSEL_ITEMS, "malformed_json_string")
        }
        val template = CarouselPushTemplate(MapData(mockedMap))
        assertEquals(PushTemplateType.CAROUSEL, template.templateType)
        assertTrue(template.carouselItems.isEmpty())
    }
}