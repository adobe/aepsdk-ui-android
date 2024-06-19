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
        val template = CarouselPushTemplate(MapData(mockedMap))
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
