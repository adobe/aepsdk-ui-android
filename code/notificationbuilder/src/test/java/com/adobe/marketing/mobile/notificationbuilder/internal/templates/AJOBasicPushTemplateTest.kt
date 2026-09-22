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

import com.adobe.marketing.mobile.notificationbuilder.NotificationPriority
import com.adobe.marketing.mobile.notificationbuilder.PushTemplateConstants.AJOTemplatePropertyKeys
import com.adobe.marketing.mobile.notificationbuilder.PushTemplateConstants.ActionType
import com.adobe.marketing.mobile.notificationbuilder.PushTemplateConstants.PushPayloadKeys
import com.adobe.marketing.mobile.notificationbuilder.internal.PushTemplateType
import com.adobe.marketing.mobile.notificationbuilder.internal.util.MapData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class AJOBasicPushTemplateTest {

    // ── Inherited AEPPushTemplate parsing ─────────────────────────────────────

    @Test
    fun `parses title body and version from flat keys`() {
        val template = AJOBasicPushTemplate(
            MapData(
                mutableMapOf(
                    PushPayloadKeys.TEMPLATE_TYPE to PushTemplateType.AJO_BASIC.value,
                    PushPayloadKeys.VERSION to "1",
                    PushPayloadKeys.TITLE to AJO_MOCKED_FLAT_TITLE,
                    PushPayloadKeys.BODY to AJO_MOCKED_FLAT_BODY,
                    PushPayloadKeys.AJO_TEMPLATE_PROPERTIES to AJO_MOCKED_TEMPLATE_PROPS_FIT_CENTER
                )
            )
        )
        assertEquals(AJO_MOCKED_FLAT_TITLE, template.title)
        assertEquals(AJO_MOCKED_FLAT_BODY, template.body)
        assertEquals("1", template.payloadVersion)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `throws when required adb_version is absent`() {
        AJOBasicPushTemplate(
            MapData(
                mutableMapOf(
                    PushPayloadKeys.TEMPLATE_TYPE to PushTemplateType.AJO_BASIC.value,
                    PushPayloadKeys.TITLE to AJO_MOCKED_FLAT_TITLE,
                    PushPayloadKeys.BODY to AJO_MOCKED_FLAT_BODY
                )
            )
        )
    }

    @Test
    fun `parses imageUrl from flat adb_image key`() {
        val template = AJOBasicPushTemplate(
            MapData(
                mutableMapOf(
                    PushPayloadKeys.TEMPLATE_TYPE to PushTemplateType.AJO_BASIC.value,
                    PushPayloadKeys.VERSION to "1",
                    PushPayloadKeys.TITLE to AJO_MOCKED_FLAT_TITLE,
                    PushPayloadKeys.BODY to AJO_MOCKED_FLAT_BODY,
                    PushPayloadKeys.IMAGE_URL to AJO_MOCKED_IMAGE_URL,
                    PushPayloadKeys.AJO_TEMPLATE_PROPERTIES to AJO_MOCKED_TEMPLATE_PROPS_FIT_CENTER
                )
            )
        )
        assertEquals(AJO_MOCKED_IMAGE_URL, template.imageUrl)
    }

    @Test
    fun `imageUrl is null when flat adb_image is absent`() {
        val template = AJOBasicPushTemplate(
            MapData(
                mutableMapOf(
                    PushPayloadKeys.TEMPLATE_TYPE to PushTemplateType.AJO_BASIC.value,
                    PushPayloadKeys.VERSION to "1",
                    PushPayloadKeys.TITLE to AJO_MOCKED_FLAT_TITLE,
                    PushPayloadKeys.BODY to AJO_MOCKED_FLAT_BODY,
                    PushPayloadKeys.AJO_TEMPLATE_PROPERTIES to AJO_MOCKED_TEMPLATE_PROPS_FIT_CENTER
                )
            )
        )
        assertNull(template.imageUrl)
    }

    @Test
    fun `reads flat notification properties`() {
        val template = AJOBasicPushTemplate(
            MapData(
                mutableMapOf(
                    PushPayloadKeys.TEMPLATE_TYPE to PushTemplateType.AJO_BASIC.value,
                    PushPayloadKeys.VERSION to "1",
                    PushPayloadKeys.TITLE to AJO_MOCKED_FLAT_TITLE,
                    PushPayloadKeys.BODY to AJO_MOCKED_FLAT_BODY,
                    PushPayloadKeys.SOUND to "bells",
                    PushPayloadKeys.CHANNEL_ID to "ajo_channel",
                    PushPayloadKeys.PRIORITY to "PRIORITY_HIGH",
                    PushPayloadKeys.VISIBILITY to "PUBLIC",
                    PushPayloadKeys.BADGE_COUNT to "3",
                    PushPayloadKeys.STICKY to "true",
                    PushPayloadKeys.AJO_TEMPLATE_PROPERTIES to AJO_MOCKED_TEMPLATE_PROPS_FIT_CENTER
                )
            )
        )
        assertEquals("bells", template.sound)
        assertEquals("ajo_channel", template.channelId)
        assertEquals(3, template.badgeCount)
        assertEquals(true, template.isNotificationSticky)
        assertEquals(NotificationPriority.PRIORITY_HIGH, template.priority)
    }

    @Test
    fun `parses templateType as AJO_BASIC`() {
        val template = AJOBasicPushTemplate(
            MapData(
                mutableMapOf(
                    PushPayloadKeys.TEMPLATE_TYPE to PushTemplateType.AJO_BASIC.value,
                    PushPayloadKeys.VERSION to "1",
                    PushPayloadKeys.TITLE to AJO_MOCKED_FLAT_TITLE,
                    PushPayloadKeys.BODY to AJO_MOCKED_FLAT_BODY
                )
            )
        )
        assertEquals(PushTemplateType.AJO_BASIC, template.templateType)
    }

    @Test
    fun `uses legacy small icon when adb_small_icon is absent`() {
        val template = AJOBasicPushTemplate(
            MapData(
                mutableMapOf(
                    PushPayloadKeys.TEMPLATE_TYPE to PushTemplateType.AJO_BASIC.value,
                    PushPayloadKeys.VERSION to "1",
                    PushPayloadKeys.TITLE to AJO_MOCKED_FLAT_TITLE,
                    PushPayloadKeys.BODY to AJO_MOCKED_FLAT_BODY,
                    PushPayloadKeys.LEGACY_SMALL_ICON to "legacy_icon"
                )
            )
        )
        assertEquals("legacy_icon", template.smallIcon)
    }

    @Test
    fun `parses actionType from flat adb_a_type key`() {
        val template = AJOBasicPushTemplate(
            MapData(
                mutableMapOf(
                    PushPayloadKeys.TEMPLATE_TYPE to PushTemplateType.AJO_BASIC.value,
                    PushPayloadKeys.VERSION to "1",
                    PushPayloadKeys.TITLE to AJO_MOCKED_FLAT_TITLE,
                    PushPayloadKeys.BODY to AJO_MOCKED_FLAT_BODY,
                    PushPayloadKeys.ACTION_TYPE to ActionType.DEEPLINK.name
                )
            )
        )
        assertEquals(ActionType.DEEPLINK, template.actionType)
    }

    // ── AJOBasicPushTemplate specific (blob) ──────────────────────────────────

    @Test
    fun `sets imgScaleType to fit_center from blob`() {
        val template = AJOBasicPushTemplate(
            MapData(
                mutableMapOf(
                    PushPayloadKeys.TEMPLATE_TYPE to PushTemplateType.AJO_BASIC.value,
                    PushPayloadKeys.VERSION to "1",
                    PushPayloadKeys.TITLE to AJO_MOCKED_FLAT_TITLE,
                    PushPayloadKeys.BODY to AJO_MOCKED_FLAT_BODY,
                    PushPayloadKeys.AJO_TEMPLATE_PROPERTIES to AJO_MOCKED_TEMPLATE_PROPS_FIT_CENTER
                )
            )
        )
        assertEquals(AJOTemplatePropertyKeys.ScaleType.FIT_CENTER, template.imgScaleType)
    }

    @Test
    fun `sets imgScaleType to center_crop from blob`() {
        val template = AJOBasicPushTemplate(
            MapData(
                mutableMapOf(
                    PushPayloadKeys.TEMPLATE_TYPE to PushTemplateType.AJO_BASIC.value,
                    PushPayloadKeys.VERSION to "1",
                    PushPayloadKeys.TITLE to AJO_MOCKED_FLAT_TITLE,
                    PushPayloadKeys.BODY to AJO_MOCKED_FLAT_BODY,
                    PushPayloadKeys.AJO_TEMPLATE_PROPERTIES to AJO_MOCKED_TEMPLATE_PROPS_CENTER_CROP
                )
            )
        )
        assertEquals(AJOTemplatePropertyKeys.ScaleType.CENTER_CROP, template.imgScaleType)
    }

    @Test
    fun `defaults imgScaleType to center_crop when scale_type key is absent`() {
        val template = AJOBasicPushTemplate(
            MapData(
                mutableMapOf(
                    PushPayloadKeys.TEMPLATE_TYPE to PushTemplateType.AJO_BASIC.value,
                    PushPayloadKeys.VERSION to "1",
                    PushPayloadKeys.TITLE to AJO_MOCKED_FLAT_TITLE,
                    PushPayloadKeys.BODY to AJO_MOCKED_FLAT_BODY,
                    PushPayloadKeys.AJO_TEMPLATE_PROPERTIES to AJO_MOCKED_TEMPLATE_PROPS_NO_SCALE
                )
            )
        )
        assertEquals(AJOTemplatePropertyKeys.ScaleType.CENTER_CROP, template.imgScaleType)
    }

    @Test
    fun `defaults imgScaleType to center_crop when blob is absent`() {
        val template = AJOBasicPushTemplate(
            MapData(
                mutableMapOf(
                    PushPayloadKeys.TEMPLATE_TYPE to PushTemplateType.AJO_BASIC.value,
                    PushPayloadKeys.VERSION to "1",
                    PushPayloadKeys.TITLE to AJO_MOCKED_FLAT_TITLE,
                    PushPayloadKeys.BODY to AJO_MOCKED_FLAT_BODY
                )
            )
        )
        assertEquals(AJOTemplatePropertyKeys.ScaleType.CENTER_CROP, template.imgScaleType)
    }

    @Test
    fun `defaults imgScaleType to center_crop when blob is malformed`() {
        val template = AJOBasicPushTemplate(
            MapData(
                mutableMapOf(
                    PushPayloadKeys.TEMPLATE_TYPE to PushTemplateType.AJO_BASIC.value,
                    PushPayloadKeys.VERSION to "1",
                    PushPayloadKeys.TITLE to AJO_MOCKED_FLAT_TITLE,
                    PushPayloadKeys.BODY to AJO_MOCKED_FLAT_BODY,
                    PushPayloadKeys.AJO_TEMPLATE_PROPERTIES to "not valid json {{{"
                )
            )
        )
        assertEquals(AJOTemplatePropertyKeys.ScaleType.CENTER_CROP, template.imgScaleType)
    }

    @Test
    fun `parses action buttons from flat adb_act key`() {
        val template = AJOBasicPushTemplate(
            MapData(
                mutableMapOf(
                    PushPayloadKeys.TEMPLATE_TYPE to PushTemplateType.AJO_BASIC.value,
                    PushPayloadKeys.VERSION to "1",
                    PushPayloadKeys.TITLE to AJO_MOCKED_FLAT_TITLE,
                    PushPayloadKeys.BODY to AJO_MOCKED_FLAT_BODY,
                    PushPayloadKeys.ACTION_BUTTONS to MOCKED_ACTION_BUTTON_DATA,
                    PushPayloadKeys.AJO_TEMPLATE_PROPERTIES to AJO_MOCKED_TEMPLATE_PROPS_FIT_CENTER
                )
            )
        )
        assertEquals(2, template.actionButtonsList?.size)
        assertEquals("Go to chess.com", template.actionButtonsList?.get(0)?.label)
        assertEquals("Open the app", template.actionButtonsList?.get(1)?.label)
    }

    @Test
    fun `returns null action buttons when adb_act is absent`() {
        val template = AJOBasicPushTemplate(
            MapData(
                mutableMapOf(
                    PushPayloadKeys.TEMPLATE_TYPE to PushTemplateType.AJO_BASIC.value,
                    PushPayloadKeys.VERSION to "1",
                    PushPayloadKeys.TITLE to AJO_MOCKED_FLAT_TITLE,
                    PushPayloadKeys.BODY to AJO_MOCKED_FLAT_BODY
                )
            )
        )
        assertNull(template.actionButtonsList)
    }

    @Test
    fun `returns null action buttons when adb_act is invalid JSON`() {
        val template = AJOBasicPushTemplate(
            MapData(
                mutableMapOf(
                    PushPayloadKeys.TEMPLATE_TYPE to PushTemplateType.AJO_BASIC.value,
                    PushPayloadKeys.VERSION to "1",
                    PushPayloadKeys.TITLE to AJO_MOCKED_FLAT_TITLE,
                    PushPayloadKeys.BODY to AJO_MOCKED_FLAT_BODY,
                    PushPayloadKeys.ACTION_BUTTONS to "not json"
                )
            )
        )
        assertNull(template.actionButtonsList)
    }

    @Test
    fun `stores raw actionButtonsString`() {
        val template = AJOBasicPushTemplate(
            MapData(
                mutableMapOf(
                    PushPayloadKeys.TEMPLATE_TYPE to PushTemplateType.AJO_BASIC.value,
                    PushPayloadKeys.VERSION to "1",
                    PushPayloadKeys.TITLE to AJO_MOCKED_FLAT_TITLE,
                    PushPayloadKeys.BODY to AJO_MOCKED_FLAT_BODY,
                    PushPayloadKeys.ACTION_BUTTONS to MOCKED_ACTION_BUTTON_DATA
                )
            )
        )
        assertEquals(MOCKED_ACTION_BUTTON_DATA, template.actionButtonsString)
    }

    @Test
    fun `skips null action buttons and returns only valid ones`() {
        val template = AJOBasicPushTemplate(
            MapData(
                mutableMapOf(
                    PushPayloadKeys.TEMPLATE_TYPE to PushTemplateType.AJO_BASIC.value,
                    PushPayloadKeys.VERSION to "1",
                    PushPayloadKeys.TITLE to AJO_MOCKED_FLAT_TITLE,
                    PushPayloadKeys.BODY to AJO_MOCKED_FLAT_BODY,
                    PushPayloadKeys.ACTION_BUTTONS to MOCKED_MALFORMED_JSON_ACTION_BUTTON
                )
            )
        )
        assertNotNull(template.actionButtonsList)
    }
}
