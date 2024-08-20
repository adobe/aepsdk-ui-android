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

import android.app.NotificationManager
import com.adobe.marketing.mobile.notificationbuilder.NotificationPriority
import com.adobe.marketing.mobile.notificationbuilder.NotificationVisibility
import com.adobe.marketing.mobile.notificationbuilder.internal.PushTemplateType
import com.adobe.marketing.mobile.notificationbuilder.internal.util.MapData
import com.adobe.marketing.mobile.utils.PushTemplateConstants
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import kotlin.test.Test
import kotlin.test.assertFailsWith

@RunWith(MockitoJUnitRunner::class)
class AEPPushTemplateTest {
    @Test
    fun `Test BasicPushTemplate initialization with Map`() {
        val aepPushTemplate = provideMockedBasicPushTemplateWithRequiredData()
        assertEquals(MOCKED_TITLE, aepPushTemplate.title)
        assertEquals(MOCKED_BODY, aepPushTemplate.body)
        assertEquals(MOCKED_PAYLOAD_VERSION, aepPushTemplate.payloadVersion)
    }

    @Test
    fun `Test exception with missing data adb_title`() {
        val aepPushBasicData = MockAEPPushTemplateDataProvider.getMockedDataMapWithRequiredData()
        aepPushBasicData.remove(PushTemplateConstants.PushPayloadKeys.TITLE)
        val exception = assertFailsWith<IllegalArgumentException> {
            BasicPushTemplate(MapData(aepPushBasicData))
        }
        assertEquals(
            "Required push template key ${PushTemplateConstants.PushPayloadKeys.TITLE} not found or null",
            exception.message
        )
    }

    @Test
    fun `Test AEPPushTemplate initialization with missing body`() {
        val aepPushData = MockAEPPushTemplateDataProvider.getMockedDataMapWithRequiredData()
        aepPushData.remove(PushTemplateConstants.PushPayloadKeys.BODY)
        val exception = assertFailsWith<IllegalArgumentException> {
            BasicPushTemplate(MapData(aepPushData))
        }
        assertEquals(
            "Required push template key ${PushTemplateConstants.PushPayloadKeys.BODY} not found or null",
            exception.message
        )
    }

    @Test
    fun `Test AEPPushTemplate initialization with missing version`() {
        val aepPushData = MockAEPPushTemplateDataProvider.getMockedDataMapWithRequiredData()
        aepPushData.remove(PushTemplateConstants.PushPayloadKeys.VERSION)
        val exception = assertFailsWith<IllegalArgumentException> {
            BasicPushTemplate(MapData(aepPushData))
        }
        assertEquals(
            "Required push template key ${PushTemplateConstants.PushPayloadKeys.VERSION} not found or null",
            exception.message
        )
    }

    @Test
    fun `Test AEPPushTemplate initialization with all data`() {
        val aepPushTemplate = provideMockedBasicPushTemplateWithAllKeys()
        assertEquals(PushTemplateType.BASIC, aepPushTemplate.templateType)
        assertEquals(MOCKED_TAG, aepPushTemplate.tag)
        assertFalse(aepPushTemplate.isFromIntent)
        assertEquals(MOCKED_ACTION_URI, aepPushTemplate.actionUri)
        assertEquals(PushTemplateConstants.ActionType.NONE, aepPushTemplate.actionType)
        assertEquals(5, aepPushTemplate.badgeCount)
        assertEquals(MOCKED_BASIC_TEMPLATE_BODY, aepPushTemplate.body)
        assertEquals(MOCKED_CHANNEL_ID, aepPushTemplate.channelId)
        assertEquals(MOCKED_BASIC_TEMPLATE_BODY_EXPANDED, aepPushTemplate.expandedBodyText)
        assertEquals("FFD966", aepPushTemplate.bodyTextColor)
        assertEquals(MOCKED_IMAGE_URI, aepPushTemplate.imageUrl)
        assertEquals(MOCKED_LARGE_ICON, aepPushTemplate.largeIcon)
        assertEquals("FFD966", aepPushTemplate.backgroundColor)
        assertEquals(NotificationPriority.fromString(MOCKED_PRIORITY), aepPushTemplate.priority)
        assertEquals(NotificationVisibility.fromString(MOCKED_VISIBILITY), aepPushTemplate.visibility)
        assertEquals("PRIORITY_HIGH", aepPushTemplate.priority.stringValue)
        assertEquals("PUBLIC", aepPushTemplate.visibility.stringValue)
        assertEquals("bell", aepPushTemplate.sound)
        assertEquals(MOCKED_SMALL_ICON, aepPushTemplate.smallIcon)
        assertEquals("FFD966", aepPushTemplate.titleTextColor)
        assertEquals(MOCKED_TICKER, aepPushTemplate.ticker)
        assertEquals(MOCKED_PAYLOAD_VERSION, aepPushTemplate.payloadVersion)
        assertEquals(true, aepPushTemplate.isNotificationSticky)
        assertEquals(aepPushTemplate.smallIconColor, null)
        assertEquals(
            aepPushTemplate.getNotificationImportance(),
            NotificationManager.IMPORTANCE_HIGH
        )
    }

    @Test
    fun `Test AEPPushTemplate initialization with null priority`() {
        val data = MockAEPPushTemplateDataProvider.getMockedAEPDataMapWithAllKeys()
        data.remove(PushTemplateConstants.PushPayloadKeys.PRIORITY)
        val aepPushTemplate = BasicPushTemplate(MapData(data))
        assertEquals("PRIORITY_DEFAULT", aepPushTemplate.priority.stringValue)
    }
}
