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

import com.adobe.marketing.mobile.notificationbuilder.internal.templates.MockMultiIconTemplateDataProvider.getMockedDataMapWithForMultiIcon
import com.adobe.marketing.mobile.notificationbuilder.internal.util.MapData
import com.adobe.ui_utils.PushTemplateConstants
import com.adobe.ui_utils.PushTemplateConstants.DEFAULT_DELETE_ICON_NAME
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import kotlin.test.Test
import kotlin.test.assertFailsWith

@RunWith(MockitoJUnitRunner::class)
class MultiIconPushTemplateTests {

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
    }
    @Test
    fun testMultiIconNotificationWithAllKeys() {
        val multiIconPushTemplate = provideMockedMultiIconTemplateWithAllKeys()
        assertEquals(5, multiIconPushTemplate.templateItemList.size)
        assertEquals("delete", multiIconPushTemplate.cancelIcon)
    }

    @Test
    fun testMultiIconPushTemplateWithNoCrossButtonIconKey() {
        // Arrange
        val dataMap = getMockedDataMapWithForMultiIcon()
        dataMap.remove(PushTemplateConstants.PushPayloadKeys.MULTI_ICON_CLOSE_BUTTON)
        val data = MapData(dataMap)
        val multiIconPushTemplate = MultiIconPushTemplate(data)
        assertEquals(5, multiIconPushTemplate.templateItemList.size)
        assertEquals(DEFAULT_DELETE_ICON_NAME, multiIconPushTemplate.cancelIcon)
    }

    @Test
    fun testMultiIconPushTemplateWithInvalidUrisAndEmptyAction() {
        // Arrange
        val dataMap = getMockedDataMapWithForMultiIcon()
        dataMap.replaceValueInMap(
            PushTemplateConstants.PushPayloadKeys.MULTI_ICON_ITEMS,
            MOCK_MULTI_ICON_ITEM_PAYLOAD_INVALID_IMAGE
        )
        val data = MapData(dataMap)
        val multiIconPushTemplate = MultiIconPushTemplate(data)
        assertEquals(3, multiIconPushTemplate.templateItemList.size)
        assertEquals(multiIconPushTemplate.templateItemList[0].actionType, PushTemplateConstants.ActionType.NONE)
    }

    @Test
    fun testMultiIconPushTemplateNoJson() {
        val dataMap = getMockedDataMapWithForMultiIcon()
        dataMap.replaceValueInMap(
            PushTemplateConstants.PushPayloadKeys.MULTI_ICON_ITEMS,
            MOCK_MULTI_ICON_ITEM_PAYLOAD_INCOMPLETE_JSON
        )
        val multiIconPushTemplate = MultiIconPushTemplate(MapData(dataMap))
        assertEquals(3, multiIconPushTemplate.templateItemList.size)
    }

    @Test
    fun testMultiIconPushTemplateEmptyJson() {
        val dataMap = getMockedDataMapWithForMultiIcon()
        dataMap.replaceValueInMap(PushTemplateConstants.PushPayloadKeys.MULTI_ICON_ITEMS, "")
        val data = MapData(dataMap)
        val exception = assertFailsWith<IllegalArgumentException> {
            MultiIconPushTemplate(data)
        }
        assertEquals(
            "Required field \"${PushTemplateConstants.PushPayloadKeys.MULTI_ICON_ITEMS}\" is invalid.",
            exception.message
        )
    }

    @Test
    fun testMultiIconPushTemplateIncompleteJson() {
        val dataMap = getMockedDataMapWithForMultiIcon()
        dataMap.replaceValueInMap(
            PushTemplateConstants.PushPayloadKeys.MULTI_ICON_ITEMS,
            MOCK_MULTI_ICON_ITEM_PAYLOAD_INCOMPLETE_JSON
        )
        val multiIconPushTemplate = MultiIconPushTemplate(MapData(dataMap))
        assertEquals(3, multiIconPushTemplate.templateItemList.size)
    }

    @Test
    fun testAMultiIconPushTemplateInvalidJson() {
        val dataMap = getMockedDataMapWithForMultiIcon()
        dataMap.replaceValueInMap(PushTemplateConstants.PushPayloadKeys.MULTI_ICON_ITEMS, MOCKED_MALFORMED_JSON_ACTION_BUTTON)
        val data = MapData(dataMap)
        val exception = assertFailsWith<IllegalArgumentException> {
            MultiIconPushTemplate(data)
        }
        assertEquals(
            "\"${PushTemplateConstants.PushPayloadKeys.MULTI_ICON_ITEMS}\" field must have 3 to 5 valid items",
            exception.message
        )
    }

    @Test
    fun testBMultiIconPushTemplateEmptyJson2() {
        val dataMap = getMockedDataMapWithForMultiIcon()
        dataMap.replaceValueInMap(PushTemplateConstants.PushPayloadKeys.MULTI_ICON_ITEMS, "{}")
        val data = MapData(dataMap)
        val exception = assertFailsWith<IllegalArgumentException> {
            MultiIconPushTemplate(data)
        }
        assertEquals(
            "Required field \"${PushTemplateConstants.PushPayloadKeys.MULTI_ICON_ITEMS}\" is invalid.",
            exception.message
        )
    }
}
