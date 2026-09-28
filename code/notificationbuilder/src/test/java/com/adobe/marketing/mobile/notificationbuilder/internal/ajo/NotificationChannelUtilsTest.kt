/*
  Copyright 2026 Adobe. All rights reserved.
  This file is licensed to you under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License. You may obtain a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0
  Unless required by applicable law or agreed to in writing, software distributed under
  the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR REPRESENTATIONS
  OF ANY KIND, either express or implied. See the License for the specific language
  governing permissions and limitations under the License.
*/

package com.adobe.marketing.mobile.notificationbuilder.internal.ajo

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.RingtoneManager
import android.os.Bundle
import com.adobe.marketing.mobile.notificationbuilder.PushTemplateConstants.DefaultValues
import com.adobe.marketing.mobile.notificationbuilder.PushTemplateConstants.PushPayloadKeys
import com.adobe.marketing.mobile.notificationbuilder.internal.PushTemplateType
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.AJOBasicPushTemplate
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.AJOBigTextPushTemplate
import com.adobe.marketing.mobile.notificationbuilder.internal.util.IntentData
import com.adobe.marketing.mobile.notificationbuilder.internal.util.MapData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [31])
class NotificationChannelUtilsTest {

    private lateinit var context: Context
    private lateinit var notificationManager: NotificationManager

    @Before
    fun setUp() {
        context = RuntimeEnvironment.getApplication()
        notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    private fun basicTemplate(vararg extra: Pair<String, String>) = AJOBasicPushTemplate(
        MapData(
            mutableMapOf(
                PushPayloadKeys.TEMPLATE_TYPE to PushTemplateType.AJO_BASIC.value,
                PushPayloadKeys.VERSION to "1",
                PushPayloadKeys.TITLE to "Title",
                PushPayloadKeys.BODY to "Body",
                *extra
            )
        )
    )

    @Test
    fun `creates the default channel with the default sound when the payload has no channel id`() {
        val channelId = NotificationChannelUtils.createChannelIfRequired(
            context, notificationManager, basicTemplate()
        )

        assertEquals(DefaultValues.DEFAULT_CHANNEL_ID, channelId)
        val channel = notificationManager.getNotificationChannel(channelId)
        assertNotNull(channel)
        assertEquals(DefaultValues.DEFAULT_CHANNEL_NAME, channel.name)
        assertEquals(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), channel.sound)
    }

    @Test
    fun `creates the payload channel with the payload importance`() {
        val channelId = NotificationChannelUtils.createChannelIfRequired(
            context,
            notificationManager,
            basicTemplate(
                PushPayloadKeys.CHANNEL_ID to "custom_channel",
                PushPayloadKeys.PRIORITY to "PRIORITY_HIGH"
            )
        )

        assertEquals("custom_channel", channelId)
        assertEquals(
            NotificationManager.IMPORTANCE_HIGH,
            notificationManager.getNotificationChannel(channelId).importance
        )
    }

    @Test
    fun `uses a custom sound from the app resources`() {
        val channelId = NotificationChannelUtils.createChannelIfRequired(
            context,
            notificationManager,
            basicTemplate(PushPayloadKeys.CHANNEL_ID to "sound_channel", PushPayloadKeys.SOUND to "bells")
        )

        val sound = notificationManager.getNotificationChannel(channelId).sound
        assertEquals("android.resource://${context.packageName}/raw/bells", sound.toString())
    }

    @Test
    fun `reuses an existing channel without changing it`() {
        notificationManager.createNotificationChannel(
            NotificationChannel("existing", "Existing", NotificationManager.IMPORTANCE_LOW)
        )

        val channelId = NotificationChannelUtils.createChannelIfRequired(
            context,
            notificationManager,
            basicTemplate(PushPayloadKeys.CHANNEL_ID to "existing", PushPayloadKeys.PRIORITY to "PRIORITY_HIGH")
        )

        assertEquals("existing", channelId)
        val channel = notificationManager.getNotificationChannel(channelId)
        assertEquals("Existing", channel.name)
        assertEquals(NotificationManager.IMPORTANCE_LOW, channel.importance)
    }

    @Test
    fun `uses a silent channel without sound when the template is rebuilt from an intent`() {
        val bundle = Bundle().apply {
            putString(PushPayloadKeys.TEMPLATE_TYPE, PushTemplateType.AJO_BIG_TEXT.value)
            putString(PushPayloadKeys.VERSION, "1")
            putString(PushPayloadKeys.TITLE, "Title")
            putString(PushPayloadKeys.BODY, "Body")
            putString(PushPayloadKeys.CHANNEL_ID, "ignored_channel")
        }

        val channelId = NotificationChannelUtils.createChannelIfRequired(
            context, notificationManager, AJOBigTextPushTemplate(IntentData(bundle, null))
        )

        assertEquals(DefaultValues.SILENT_NOTIFICATION_CHANNEL_ID, channelId)
        val channel = notificationManager.getNotificationChannel(channelId)
        assertEquals(DefaultValues.SILENT_CHANNEL_NAME, channel.name)
        assertNull(channel.sound)
    }

    @Test
    @Config(sdk = [21])
    fun `returns the channel id without creating a channel below API 26`() {
        val channelId = NotificationChannelUtils.createChannelIfRequired(
            context, notificationManager, basicTemplate(PushPayloadKeys.CHANNEL_ID to "custom_channel")
        )

        assertEquals("custom_channel", channelId)
    }
}
