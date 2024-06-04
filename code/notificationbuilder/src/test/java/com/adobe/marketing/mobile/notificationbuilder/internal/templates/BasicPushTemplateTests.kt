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
import com.adobe.marketing.mobile.notificationbuilder.internal.util.MapData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import kotlin.test.Test

@RunWith(MockitoJUnitRunner::class)
class BasicPushTemplateTests {
    @Test
    fun `Test BasicPushTemplate initialization with all keys present`() {
        val basicPushTemplate = provideMockedBasicPushTemplateWithAllKeys()

        // Assert the number of action buttons
        assertEquals(2, basicPushTemplate.actionButtonsList?.size)

        val actionButton1 = basicPushTemplate.actionButtonsList?.get(0)
        assertEquals("Go to chess.com", actionButton1?.label)
        assertEquals("https://chess.com/games/552", actionButton1?.link)
        assertEquals(PushTemplateConstants.ActionType.DEEPLINK, actionButton1?.type)

        val actionButton2 = basicPushTemplate.actionButtonsList?.get(1)
        assertEquals("Open the app", actionButton2?.label)
        assertNull(actionButton2?.link)
        assertEquals(PushTemplateConstants.ActionType.OPENAPP, actionButton2?.type)

        assertEquals(MOCK_REMIND_LATER_TIME, basicPushTemplate.remindLaterTimestamp.toString())
        assertEquals(MOCK_REMIND_LATER_DURATION, basicPushTemplate.remindLaterDuration.toString())
        assertEquals(MOCK_REMIND_LATER_TEXT, basicPushTemplate.remindLaterText)
        assertEquals(MOCKED_ACTION_BUTTON_DATA, basicPushTemplate.actionButtonsString)
    }

    @Test
    fun `Test BasicPushTemplate initialization with invalid JSON`() {
        val dataMap = MockAEPPushTemplateDataProvider.getMockedAEPDataMapWithAllKeys()
        dataMap[PushTemplateConstants.PushPayloadKeys.ACTION_BUTTONS] = ""
        val basicPushTemplate = BasicPushTemplate(MapData(dataMap))
        assertEquals(null, basicPushTemplate.actionButtonsList)
    }

    @Test
    fun `Test BasicPushTemplate initialization with malformed JSON`() {
        val dataMap = MockAEPPushTemplateDataProvider.getMockedAEPDataMapWithAllKeys()
        dataMap[PushTemplateConstants.PushPayloadKeys.ACTION_BUTTONS] = MOCKED_MALFORMED_JSON_ACTION_BUTTON
        val basicPushTemplate = BasicPushTemplate(MapData(dataMap))
        // Two wrong JSON objects are ignored out of 4
        assertEquals(2, basicPushTemplate.actionButtonsList?.size)

        val actionButton1 = basicPushTemplate.actionButtonsList?.get(0)
        // Wrong Action type in JSON is converted to NONE
        assertEquals(PushTemplateConstants.ActionType.NONE, actionButton1?.type)
    }
}
