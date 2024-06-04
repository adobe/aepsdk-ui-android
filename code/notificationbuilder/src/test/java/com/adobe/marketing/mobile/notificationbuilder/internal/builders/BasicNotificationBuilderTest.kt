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

package com.adobe.marketing.mobile.notificationbuilder.internal.builders

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.adobe.marketing.mobile.notificationbuilder.PushTemplateConstants
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.BasicPushTemplate
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.MOCKED_CHANNEL_ID
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.MOCKED_TAG
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.MOCKED_TICKER
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.MOCK_REMIND_LATER_DURATION
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.MOCK_REMIND_LATER_TEXT
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.MOCK_REMIND_LATER_TIME
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.MockAEPPushTemplateDataProvider
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.provideMockedBasicPushTemplateWithAllKeys
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.provideMockedBasicPushTemplateWithRequiredData
import com.adobe.marketing.mobile.notificationbuilder.internal.util.MapData
import junit.framework.TestCase.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import kotlin.test.assertNotNull

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [31])
class BasicNotificationBuilderTest {

    @Mock
    private lateinit var context: Context
    private lateinit var trackerActivityClass: Class<out Activity>
    private lateinit var broadcastReceiverClass: Class<out BroadcastReceiver>

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)

        context = RuntimeEnvironment.getApplication()
        trackerActivityClass = DummyActivity::class.java
        broadcastReceiverClass = DummyBroadcastReceiver::class.java
    }

    @Test
    fun `construct should return a NotificationCompat Builder`() {
        val pushTemplate = provideMockedBasicPushTemplateWithAllKeys()
        val notificationBuilder = BasicNotificationBuilder.construct(
            context,
            pushTemplate,
            trackerActivityClass,
            broadcastReceiverClass
        )

        assertEquals(NotificationCompat.Builder::class.java, notificationBuilder.javaClass)
    }

    @Test
    fun `construct should set parameters for notification builder properly`() {
        val pushTemplate = provideMockedBasicPushTemplateWithAllKeys()
        val notificationBuilder = BasicNotificationBuilder.construct(
            context,
            pushTemplate,
            trackerActivityClass,
            broadcastReceiverClass
        )

        val actions = notificationBuilder.mActions
        val listOfActionTitles = listOf("remind me", "Open the app", "Go to chess.com")

        assertNotNull(actions)
        assertEquals(listOfActionTitles.size, actions.size)

        for (eachActionTitle in listOfActionTitles) {
            val action = actions.find {
                it.title == eachActionTitle
            }
            assertNotNull(action)
        }

        val build = notificationBuilder.build()

        val pendingIntent = build.contentIntent
        val shadowPendingIntent = Shadows.shadowOf(pendingIntent)
        val intent = shadowPendingIntent.savedIntent

        assertEquals(pushTemplate.tag, intent.getStringExtra(PushTemplateConstants.PushPayloadKeys.TAG))
        assertEquals(pushTemplate.isNotificationSticky.toString(), intent.getStringExtra(PushTemplateConstants.PushPayloadKeys.STICKY))
        assertEquals(pushTemplate.tag, MOCKED_TAG)
        assertEquals(build.channelId, MOCKED_CHANNEL_ID)
        assertEquals(MOCKED_TICKER, build.tickerText)
    }

    @Test
    fun `construct should set STICKY flag to false when isNotificationSticky parameter is false`() {

        val dataMap = MockAEPPushTemplateDataProvider.getMockedDataMapWithRequiredData()
        dataMap[PushTemplateConstants.PushPayloadKeys.REMIND_LATER_TEXT] = MOCK_REMIND_LATER_TEXT
        dataMap[PushTemplateConstants.PushPayloadKeys.REMIND_LATER_DURATION] = MOCK_REMIND_LATER_DURATION
        dataMap[PushTemplateConstants.PushPayloadKeys.STICKY] = "false"

        val pushTemplate = BasicPushTemplate(MapData(dataMap))

        val notificationBuilder = BasicNotificationBuilder.construct(
            context,
            pushTemplate,
            trackerActivityClass,
            broadcastReceiverClass
        )

        val build = notificationBuilder.build()
        val pendingIntent = build.contentIntent
        val shadowPendingIntent = Shadows.shadowOf(pendingIntent)
        val intent = shadowPendingIntent.savedIntent

        assertEquals("false", intent.getStringExtra(PushTemplateConstants.PushPayloadKeys.STICKY))
    }

    @Test
    fun `fallbackToBasicNotification should return a NotificationCompat Builder`() {
        val notificationBuilder = BasicNotificationBuilder.fallbackToBasicNotification(
            context,
            trackerActivityClass,
            broadcastReceiverClass,
            MapData(MockAEPPushTemplateDataProvider.getMockedDataMapWithRequiredData())
        )

        assertEquals(NotificationCompat.Builder::class.java, notificationBuilder.javaClass)
    }

    @Test
    fun `createRemindPendingIntent should return null when broadcastReceiverClass is null`() {
        val pushTemplate = provideMockedBasicPushTemplateWithAllKeys()
        val pendingIntent = BasicNotificationBuilder.createRemindPendingIntent(
            context,
            null,
            MOCKED_CHANNEL_ID,
            pushTemplate
        )

        assertNull(pendingIntent)
    }

    @Test
    fun `remindLaterButton is added when remindLaterText is not null, remindLaterTimestamp is not null, remindLaterDuration is not null`() {
        val pushTemplate = provideMockedBasicPushTemplateWithAllKeys()
        val notificationBuilder = BasicNotificationBuilder.construct(
            context,
            pushTemplate,
            trackerActivityClass,
            broadcastReceiverClass
        )
        val actions = notificationBuilder.mActions
        val remindLaterAction = actions.find {
            it.title == MOCK_REMIND_LATER_TEXT
        }

        assertNotNull(remindLaterAction)
    }

    @Test
    fun `remindLaterButton is not added when remindLaterText is null`() {
        val pushTemplate = provideMockedBasicPushTemplateWithRequiredData()
        val notificationBuilder = BasicNotificationBuilder.construct(
            context,
            pushTemplate,
            trackerActivityClass,
            broadcastReceiverClass
        )
        val actions = notificationBuilder.mActions
        val remindLaterAction = actions.find {
            it.title == MOCK_REMIND_LATER_TEXT
        }

        assertNull(remindLaterAction)
    }

    @Test
    fun `remindLaterButton is not added when remindLaterText is not null, remindLaterTimestamp is null, remindLaterDuration is null`() {
        val dataMap = MockAEPPushTemplateDataProvider.getMockedDataMapWithRequiredData()
        dataMap[PushTemplateConstants.PushPayloadKeys.REMIND_LATER_TEXT] = MOCK_REMIND_LATER_TEXT

        val pushTemplate = BasicPushTemplate(MapData(dataMap))
        val notificationBuilder = BasicNotificationBuilder.construct(
            context,
            pushTemplate,
            trackerActivityClass,
            broadcastReceiverClass
        )
        val actions = notificationBuilder.mActions
        val remindLaterAction = actions.find {
            it.title == MOCK_REMIND_LATER_TEXT
        }

        assertNull(remindLaterAction)
    }

    @Test
    fun `remindLaterButton is added when remindLaterText is not null, remindLaterTimestamp is not null, remindLaterDuration is null`() {
        val dataMap = MockAEPPushTemplateDataProvider.getMockedDataMapWithRequiredData()
        dataMap[PushTemplateConstants.PushPayloadKeys.REMIND_LATER_TEXT] = MOCK_REMIND_LATER_TEXT
        dataMap[PushTemplateConstants.PushPayloadKeys.REMIND_LATER_TIMESTAMP] = MOCK_REMIND_LATER_TIME

        val pushTemplate = BasicPushTemplate(MapData(dataMap))
        val notificationBuilder = BasicNotificationBuilder.construct(
            context,
            pushTemplate,
            trackerActivityClass,
            broadcastReceiverClass
        )
        val actions = notificationBuilder.mActions
        val remindLaterAction = actions.find {
            it.title == MOCK_REMIND_LATER_TEXT
        }

        assertNotNull(remindLaterAction)
    }

    @Test
    fun `remindLaterButton is added when remindLaterText is not null, remindLaterTimestamp is null, remindLaterDuration is not null`() {
        val dataMap = MockAEPPushTemplateDataProvider.getMockedDataMapWithRequiredData()
        dataMap[PushTemplateConstants.PushPayloadKeys.REMIND_LATER_TEXT] = MOCK_REMIND_LATER_TEXT
        dataMap[PushTemplateConstants.PushPayloadKeys.REMIND_LATER_DURATION] = MOCK_REMIND_LATER_DURATION

        val pushTemplate = BasicPushTemplate(MapData(dataMap))
        val notificationBuilder = BasicNotificationBuilder.construct(
            context,
            pushTemplate,
            trackerActivityClass,
            broadcastReceiverClass
        )
        val actions = notificationBuilder.mActions
        val remindLaterAction = actions.find {
            it.title == MOCK_REMIND_LATER_TEXT
        }

        assertNotNull(remindLaterAction)
    }
}

class DummyBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        // empty class for testing
    }
}
