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

import com.adobe.marketing.mobile.notificationbuilder.PushTemplateConstants.PushPayloadKeys
import com.adobe.marketing.mobile.notificationbuilder.internal.PushTemplateType
import com.adobe.marketing.mobile.notificationbuilder.internal.util.MapData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class AJOBigTextPushTemplateTest {

    // ── Inherited AEPPushTemplate parsing ─────────────────────────────────────

    @Test
    fun `parses title body and version from flat keys`() {
        val template = AJOBigTextPushTemplate(
            MapData(
                mutableMapOf(
                    PushPayloadKeys.TEMPLATE_TYPE to PushTemplateType.AJO_BIG_TEXT.value,
                    PushPayloadKeys.VERSION to "1",
                    PushPayloadKeys.TITLE to AJO_MOCKED_FLAT_TITLE,
                    PushPayloadKeys.BODY to AJO_MOCKED_FLAT_BODY,
                    PushPayloadKeys.AJO_TEMPLATE_PROPERTIES to AJO_MOCKED_BIGTEXT_PROPS_FULL
                )
            )
        )
        assertEquals(AJO_MOCKED_FLAT_TITLE, template.title)
        assertEquals(AJO_MOCKED_FLAT_BODY, template.body)
        assertEquals("1", template.payloadVersion)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `throws when required adb_version is absent`() {
        AJOBigTextPushTemplate(
            MapData(
                mutableMapOf(
                    PushPayloadKeys.TEMPLATE_TYPE to PushTemplateType.AJO_BIG_TEXT.value,
                    PushPayloadKeys.TITLE to AJO_MOCKED_FLAT_TITLE,
                    PushPayloadKeys.BODY to AJO_MOCKED_FLAT_BODY
                )
            )
        )
    }

    @Test
    fun `reads flat notification properties`() {
        val template = AJOBigTextPushTemplate(
            MapData(
                mutableMapOf(
                    PushPayloadKeys.TEMPLATE_TYPE to PushTemplateType.AJO_BIG_TEXT.value,
                    PushPayloadKeys.VERSION to "1",
                    PushPayloadKeys.TITLE to AJO_MOCKED_FLAT_TITLE,
                    PushPayloadKeys.BODY to AJO_MOCKED_FLAT_BODY,
                    PushPayloadKeys.SOUND to "bells",
                    PushPayloadKeys.CHANNEL_ID to "ajo_bigtext_channel",
                    PushPayloadKeys.PRIORITY to "PRIORITY_HIGH",
                    PushPayloadKeys.VISIBILITY to "PUBLIC",
                    PushPayloadKeys.BADGE_COUNT to "3",
                    PushPayloadKeys.STICKY to "true",
                    PushPayloadKeys.AJO_TEMPLATE_PROPERTIES to AJO_MOCKED_BIGTEXT_PROPS_FULL
                )
            )
        )
        assertEquals("bells", template.sound)
        assertEquals("ajo_bigtext_channel", template.channelId)
        assertEquals(3, template.badgeCount)
        assertEquals(true, template.isNotificationSticky)
    }

    // ── AJOBigTextPushTemplate specific (blob) ────────────────────────────────

    @Test
    fun `expanded text is the flat adb_body`() {
        val template = AJOBigTextPushTemplate(
            MapData(
                mutableMapOf(
                    PushPayloadKeys.TEMPLATE_TYPE to PushTemplateType.AJO_BIG_TEXT.value,
                    PushPayloadKeys.VERSION to "1",
                    PushPayloadKeys.TITLE to AJO_MOCKED_FLAT_TITLE,
                    PushPayloadKeys.BODY to AJO_MOCKED_FLAT_BODY,
                    PushPayloadKeys.AJO_TEMPLATE_PROPERTIES to AJO_MOCKED_BIGTEXT_PROPS_FULL
                )
            )
        )
        assertEquals(AJO_MOCKED_FLAT_BODY, template.body)
    }

    @Test
    fun `parses largeIconUrl from blob`() {
        val template = AJOBigTextPushTemplate(
            MapData(
                mutableMapOf(
                    PushPayloadKeys.TEMPLATE_TYPE to PushTemplateType.AJO_BIG_TEXT.value,
                    PushPayloadKeys.VERSION to "1",
                    PushPayloadKeys.TITLE to AJO_MOCKED_FLAT_TITLE,
                    PushPayloadKeys.BODY to AJO_MOCKED_FLAT_BODY,
                    PushPayloadKeys.AJO_TEMPLATE_PROPERTIES to AJO_MOCKED_BIGTEXT_PROPS_FULL
                )
            )
        )
        assertEquals(AJO_MOCKED_LARGE_ICON_URL, template.largeIconUrl)
    }

    @Test
    fun `largeIconUrl is null when blob has no adb_large_icon key`() {
        val template = AJOBigTextPushTemplate(
            MapData(
                mutableMapOf(
                    PushPayloadKeys.TEMPLATE_TYPE to PushTemplateType.AJO_BIG_TEXT.value,
                    PushPayloadKeys.VERSION to "1",
                    PushPayloadKeys.TITLE to AJO_MOCKED_FLAT_TITLE,
                    PushPayloadKeys.BODY to AJO_MOCKED_FLAT_BODY,
                    PushPayloadKeys.AJO_TEMPLATE_PROPERTIES to "{\"adb_collapsed_text\":\"AJO Collapsed Body\"}"
                )
            )
        )
        assertNull(template.largeIconUrl)
    }

    @Test
    fun `largeIconUrl is null when blob is absent`() {
        val template = AJOBigTextPushTemplate(
            MapData(
                mutableMapOf(
                    PushPayloadKeys.TEMPLATE_TYPE to PushTemplateType.AJO_BIG_TEXT.value,
                    PushPayloadKeys.VERSION to "1",
                    PushPayloadKeys.TITLE to AJO_MOCKED_FLAT_TITLE,
                    PushPayloadKeys.BODY to AJO_MOCKED_FLAT_BODY
                )
            )
        )
        assertNull(template.largeIconUrl)
    }

    @Test
    fun `collapsedText parsed from adb_collapsed_text in blob`() {
        val template = AJOBigTextPushTemplate(
            MapData(
                mutableMapOf(
                    PushPayloadKeys.TEMPLATE_TYPE to PushTemplateType.AJO_BIG_TEXT.value,
                    PushPayloadKeys.VERSION to "1",
                    PushPayloadKeys.TITLE to AJO_MOCKED_FLAT_TITLE,
                    PushPayloadKeys.BODY to AJO_MOCKED_FLAT_BODY,
                    PushPayloadKeys.AJO_TEMPLATE_PROPERTIES to AJO_MOCKED_BIGTEXT_PROPS_FULL
                )
            )
        )
        assertEquals(AJO_MOCKED_COLLAPSED_TEXT, template.collapsedText)
    }

    @Test
    fun `collapsedText falls back to flat adb_body when adb_collapsed_text is absent`() {
        val template = AJOBigTextPushTemplate(
            MapData(
                mutableMapOf(
                    PushPayloadKeys.TEMPLATE_TYPE to PushTemplateType.AJO_BIG_TEXT.value,
                    PushPayloadKeys.VERSION to "1",
                    PushPayloadKeys.TITLE to AJO_MOCKED_FLAT_TITLE,
                    PushPayloadKeys.BODY to AJO_MOCKED_FLAT_BODY,
                    PushPayloadKeys.AJO_TEMPLATE_PROPERTIES to AJO_MOCKED_BIGTEXT_PROPS_NO_COLLAPSED
                )
            )
        )
        assertEquals(AJO_MOCKED_FLAT_BODY, template.collapsedText)
    }

    @Test
    fun `collapsedText falls back to flat adb_body when blob is absent`() {
        val template = AJOBigTextPushTemplate(
            MapData(
                mutableMapOf(
                    PushPayloadKeys.TEMPLATE_TYPE to PushTemplateType.AJO_BIG_TEXT.value,
                    PushPayloadKeys.VERSION to "1",
                    PushPayloadKeys.TITLE to AJO_MOCKED_FLAT_TITLE,
                    PushPayloadKeys.BODY to AJO_MOCKED_FLAT_BODY
                )
            )
        )
        assertEquals(AJO_MOCKED_FLAT_BODY, template.collapsedText)
    }

    @Test
    fun `collapsedText falls back to flat adb_body when blob is malformed`() {
        val template = AJOBigTextPushTemplate(
            MapData(
                mutableMapOf(
                    PushPayloadKeys.TEMPLATE_TYPE to PushTemplateType.AJO_BIG_TEXT.value,
                    PushPayloadKeys.VERSION to "1",
                    PushPayloadKeys.TITLE to AJO_MOCKED_FLAT_TITLE,
                    PushPayloadKeys.BODY to AJO_MOCKED_FLAT_BODY,
                    PushPayloadKeys.AJO_TEMPLATE_PROPERTIES to "not valid json {{{"
                )
            )
        )
        assertEquals(AJO_MOCKED_FLAT_BODY, template.collapsedText)
    }

    @Test
    fun `collapsedText falls back to flat adb_body when adb_collapsed_text is empty string`() {
        val template = AJOBigTextPushTemplate(
            MapData(
                mutableMapOf(
                    PushPayloadKeys.TEMPLATE_TYPE to PushTemplateType.AJO_BIG_TEXT.value,
                    PushPayloadKeys.VERSION to "1",
                    PushPayloadKeys.TITLE to AJO_MOCKED_FLAT_TITLE,
                    PushPayloadKeys.BODY to AJO_MOCKED_FLAT_BODY,
                    PushPayloadKeys.AJO_TEMPLATE_PROPERTIES to "{\"adb_collapsed_text\":\"\"}"
                )
            )
        )
        assertEquals(AJO_MOCKED_FLAT_BODY, template.collapsedText)
    }

    @Test
    fun `parses action buttons from flat adb_act key`() {
        val template = AJOBigTextPushTemplate(
            MapData(
                mutableMapOf(
                    PushPayloadKeys.TEMPLATE_TYPE to PushTemplateType.AJO_BIG_TEXT.value,
                    PushPayloadKeys.VERSION to "1",
                    PushPayloadKeys.TITLE to AJO_MOCKED_FLAT_TITLE,
                    PushPayloadKeys.BODY to AJO_MOCKED_FLAT_BODY,
                    PushPayloadKeys.ACTION_BUTTONS to MOCKED_ACTION_BUTTON_DATA,
                    PushPayloadKeys.AJO_TEMPLATE_PROPERTIES to AJO_MOCKED_BIGTEXT_PROPS_FULL
                )
            )
        )
        assertEquals(2, template.actionButtonsList?.size)
        assertEquals("Go to chess.com", template.actionButtonsList?.get(0)?.label)
        assertEquals("Open the app", template.actionButtonsList?.get(1)?.label)
    }

    @Test
    fun `returns null action buttons when adb_act is absent`() {
        val template = AJOBigTextPushTemplate(
            MapData(
                mutableMapOf(
                    PushPayloadKeys.TEMPLATE_TYPE to PushTemplateType.AJO_BIG_TEXT.value,
                    PushPayloadKeys.VERSION to "1",
                    PushPayloadKeys.TITLE to AJO_MOCKED_FLAT_TITLE,
                    PushPayloadKeys.BODY to AJO_MOCKED_FLAT_BODY,
                    PushPayloadKeys.AJO_TEMPLATE_PROPERTIES to AJO_MOCKED_BIGTEXT_PROPS_FULL
                )
            )
        )
        assertNull(template.actionButtonsList)
    }

    @Test
    fun `returns null action buttons when adb_act is invalid JSON`() {
        val template = AJOBigTextPushTemplate(
            MapData(
                mutableMapOf(
                    PushPayloadKeys.TEMPLATE_TYPE to PushTemplateType.AJO_BIG_TEXT.value,
                    PushPayloadKeys.VERSION to "1",
                    PushPayloadKeys.TITLE to AJO_MOCKED_FLAT_TITLE,
                    PushPayloadKeys.BODY to AJO_MOCKED_FLAT_BODY,
                    PushPayloadKeys.ACTION_BUTTONS to "not json",
                    PushPayloadKeys.AJO_TEMPLATE_PROPERTIES to AJO_MOCKED_BIGTEXT_PROPS_FULL
                )
            )
        )
        assertNull(template.actionButtonsList)
    }

    @Test
    fun `stores raw actionButtonsString`() {
        val template = AJOBigTextPushTemplate(
            MapData(
                mutableMapOf(
                    PushPayloadKeys.TEMPLATE_TYPE to PushTemplateType.AJO_BIG_TEXT.value,
                    PushPayloadKeys.VERSION to "1",
                    PushPayloadKeys.TITLE to AJO_MOCKED_FLAT_TITLE,
                    PushPayloadKeys.BODY to AJO_MOCKED_FLAT_BODY,
                    PushPayloadKeys.ACTION_BUTTONS to MOCKED_ACTION_BUTTON_DATA,
                    PushPayloadKeys.AJO_TEMPLATE_PROPERTIES to AJO_MOCKED_BIGTEXT_PROPS_FULL
                )
            )
        )
        assertEquals(MOCKED_ACTION_BUTTON_DATA, template.actionButtonsString)
    }

    @Test
    fun `action buttons list skips entries where getActionButtonFromJSONObject returns null`() {
        val template = AJOBigTextPushTemplate(
            MapData(
                mutableMapOf(
                    PushPayloadKeys.TEMPLATE_TYPE to PushTemplateType.AJO_BIG_TEXT.value,
                    PushPayloadKeys.VERSION to "1",
                    PushPayloadKeys.TITLE to AJO_MOCKED_FLAT_TITLE,
                    PushPayloadKeys.BODY to AJO_MOCKED_FLAT_BODY,
                    PushPayloadKeys.ACTION_BUTTONS to MOCKED_MALFORMED_JSON_ACTION_BUTTON,
                    PushPayloadKeys.AJO_TEMPLATE_PROPERTIES to AJO_MOCKED_BIGTEXT_PROPS_FULL
                )
            )
        )
        assertEquals(2, template.actionButtonsList?.size)
    }
}
