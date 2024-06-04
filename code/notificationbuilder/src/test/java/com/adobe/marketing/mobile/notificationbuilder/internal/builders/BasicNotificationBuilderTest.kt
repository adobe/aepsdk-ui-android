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
import com.adobe.marketing.mobile.notificationbuilder.NotificationConstructionFailedException
import com.adobe.marketing.mobile.notificationbuilder.internal.PushTemplateConstants
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.BasicPushTemplate
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.MOCKED_CHANNEL_ID
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
import org.robolectric.annotation.Config
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [31])
class BasicNotificationBuilderTest {

    @Mock
    private lateinit var basicPushTemplate: BasicPushTemplate
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
    fun `construct should not return null`() {
        val notificationBuilder = BasicNotificationBuilder.construct(context, basicPushTemplate, trackerActivityClass, broadcastReceiverClass)
        assertNotNull(notificationBuilder)
    }

    @Test
    fun `construct should return a NotificationCompat Builder`() {
        val pushTemplate = provideMockedBasicPushTemplateWithAllKeys()
        val notificationBuilder = BasicNotificationBuilder.construct(context, pushTemplate, trackerActivityClass, broadcastReceiverClass)

        assertEquals(NotificationCompat.Builder::class.java, notificationBuilder.javaClass)
        assertNotNull(notificationBuilder)
        // mChannelId
        // mActions title "remind me" "Open the app"
        // mActions size = 3; actionsbutton list size?
        // STICKY notification
        // tag

        /*
        * 1. context
        * 2. pushTemplate
        * 3. channelIdToUse
        * 4. trackerActivityClass
        * 5. smallLayout
        * 6. expandedLayout
        * 7. ContainerLayoutViewId
        * 8. imageUri
        * 9. downloadedImageCount
        *
        * 1. expandedLayout.setViewVisibility
        * 2. expandedLayout.setImageViewBitmap
        *
        * addActionButtons
        *   1. context
        *   2. trackerActivityClass
        *   3. pushTemplate.actionButtonsList
        *   4. pushTemplate.tag
        *   5. pushTemplate.isNotificationSticky
        *
        * write tests for scenarios where it can throw exception -> NotificationConstructionFailedException
        * */
    }

    @Test
    fun `Actions list should be set properly`() {
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

        val build = notificationBuilder.build()
        build.

        for (eachActionTitle in listOfActionTitles) {
            val action = actions.find {
                it.title == eachActionTitle
            }
            assertNotNull(action)
        }
    }

//    @Test
//    fun `channelIdToUse parameter should be set properly`() {
//        val pushTemplate = provideMockedBasicPushTemplateWithAllKeys()
//        val notificationBuilder = BasicNotificationBuilder.construct(context, pushTemplate, trackerActivityClass, broadcastReceiverClass)
//
//        assertEquals(MOCKED_CHANNEL_ID, notificationBuilder.mChannelId)
//    }

    @Test
    fun `construct should throw NotificationConstructionFailedException when invalid push template is provided`() {

        assertFailsWith (
            exceptionClass = NotificationConstructionFailedException::class,
            block = { BasicNotificationBuilder.construct(context, context, trackerActivityClass, broadcastReceiverClass) }
        )

//        {
//            BasicNotificationBuilder.construct(context, null, trackerActivityClass, broadcastReceiverClass)
//        }
    }

    @Test
    fun `fallbackToBasicNotification should return a NotificationCompat Builder`() {
        val notificationBuilder = BasicNotificationBuilder.fallbackToBasicNotification(
            context,
            trackerActivityClass,
            broadcastReceiverClass,
            MockAEPPushTemplateDataProvider.getMockedDataMapWithRequiredData()
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

class DummyActivity : Activity() {
    // empty class for testing
}

class DummyBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        // empty class for testing
    }
}
