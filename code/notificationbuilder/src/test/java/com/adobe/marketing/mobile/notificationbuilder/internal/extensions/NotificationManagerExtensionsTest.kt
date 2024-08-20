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

package com.adobe.marketing.mobile.notificationbuilder.internal.extensions

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.RingtoneManager
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.BasicPushTemplate
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.MOCKED_CHANNEL_ID
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.MockAEPPushTemplateDataProvider
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.provideMockedBasicPushTemplateWithAllKeys
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.provideMockedBasicPushTemplateWithRequiredData
import com.adobe.marketing.mobile.notificationbuilder.internal.util.MapData
import com.adobe.marketing.mobile.utils.PushTemplateConstants
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [31])
class NotificationManagerExtensionsTest {
    @Mock
    private lateinit var context: Context

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        context = RuntimeEnvironment.getApplication()
    }

    @Test
    fun `createNotificationChannelIfRequired should create a new channel if it does not exist`() {
        val template = provideMockedBasicPushTemplateWithRequiredData()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationChannelId = notificationManager.createNotificationChannelIfRequired(context, template)

        assertEquals(PushTemplateConstants.DefaultValues.DEFAULT_CHANNEL_ID, notificationChannelId)
    }

    @Test
    fun `createNotificationChannelIfRequired should not create a new channel if it already exists`() {
        val template = provideMockedBasicPushTemplateWithAllKeys()
        val notificationManager = Mockito.mock(NotificationManager::class.java)
        Mockito.`when`(notificationManager.getNotificationChannel(anyString())).thenReturn(NotificationChannel(MOCKED_CHANNEL_ID, "default_channel_name", NotificationManager.IMPORTANCE_DEFAULT))
        val notificationChannelId = notificationManager.createNotificationChannelIfRequired(context, template)

        assertEquals(MOCKED_CHANNEL_ID, notificationChannelId)
    }

    @Test
    fun `createNotificationChannelIfRequired should set a default sound if template sound is null`() {
        val template = provideMockedBasicPushTemplateWithRequiredData()
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationChannelId = notificationManager.createNotificationChannelIfRequired(context, template)
        val notificationChannel = notificationManager.getNotificationChannel(notificationChannelId)

        assertEquals(notificationChannel.sound, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
    }

    @Test
    fun `createNotificationChannelIfRequired should set a default sound if template sound is empty`() {
        val dataMap = MockAEPPushTemplateDataProvider.getMockedAEPDataMapWithAllKeys()
        dataMap[PushTemplateConstants.PushPayloadKeys.SOUND] = ""
        val template = BasicPushTemplate(MapData(dataMap))
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationChannelId = notificationManager.createNotificationChannelIfRequired(context, template)
        val notificationChannel = notificationManager.getNotificationChannel(notificationChannelId)

        assertEquals(notificationChannel.sound, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
    }

    @Test
    fun `createNotificationChannelIfRequired should create a silent notification channel when template is created from intent`() {
        val template = provideMockedBasicPushTemplateWithRequiredData(true)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationChannelId = notificationManager.createNotificationChannelIfRequired(context, template)

        assertEquals(PushTemplateConstants.DefaultValues.SILENT_NOTIFICATION_CHANNEL_ID, notificationChannelId)
    }
}
