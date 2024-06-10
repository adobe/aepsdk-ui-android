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
import androidx.core.app.NotificationCompat
import com.adobe.marketing.mobile.notificationbuilder.PushTemplateConstants
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.InputBoxPushTemplate
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.MOCKED_HINT
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.MockInputBoxPushTemplateDataProvider
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.provideMockedInputBoxPushTemplateWithRequiredData
import com.adobe.marketing.mobile.notificationbuilder.internal.util.MapData
import junit.framework.TestCase
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [31])
class InputBoxNotificationBuilderTest {
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
        val pushTemplate = provideMockedInputBoxPushTemplateWithRequiredData()
        val notificationBuilder = InputBoxNotificationBuilder.construct(
            context,
            pushTemplate,
            trackerActivityClass,
            broadcastReceiverClass
        )
        TestCase.assertEquals(NotificationCompat.Builder::class.java, notificationBuilder.javaClass)
        assertNotNull(notificationBuilder)
    }

    @Test
    fun `construct should not have any inputText action if the template is created from intent`() {
        val pushTemplate = provideMockedInputBoxPushTemplateWithRequiredData(true)
        val notificationBuilder = InputBoxNotificationBuilder.construct(
            context,
            pushTemplate,
            trackerActivityClass,
            broadcastReceiverClass
        )

        assertEquals(0, notificationBuilder.mActions.size)
    }

    @Test
    fun `construct should have an InputText action if the template is not created from intent`() {
        val pushTemplate = provideMockedInputBoxPushTemplateWithRequiredData()
        val notificationBuilder = InputBoxNotificationBuilder.construct(
            context,
            pushTemplate,
            trackerActivityClass,
            broadcastReceiverClass
        )

        assertEquals(1, notificationBuilder.mActions.size)
        assertNotNull(notificationBuilder)
    }

    @Test
    fun `createInputReceivedPendingIntent should not set class when trackerActivityClass parameter is null`() {
        val pushTemplate = provideMockedInputBoxPushTemplateWithRequiredData()
        val notificationBuilder = InputBoxNotificationBuilder.construct(
            context,
            pushTemplate,
            null,
            broadcastReceiverClass
        )

        val notification = notificationBuilder.build()
        val pendingIntent = notification.contentIntent
        val shadowPendingIntent = Shadows.shadowOf(pendingIntent)
        val intent = shadowPendingIntent.savedIntent

        assertNotNull(intent)
        assertNull(intent.resolveActivity(context.packageManager))
    }

    @Test
    fun `createInputReceivedPendingIntent should set class when trackerActivityClass parameter is null`() {
        val pushTemplate = provideMockedInputBoxPushTemplateWithRequiredData()
        val notificationBuilder = InputBoxNotificationBuilder.construct(
            context,
            pushTemplate,
            trackerActivityClass,
            broadcastReceiverClass
        )

        val notification = notificationBuilder.build()
        val pendingIntent = notification.contentIntent
        val shadowPendingIntent = Shadows.shadowOf(pendingIntent)
        val intent = shadowPendingIntent.savedIntent

        assertNotNull(intent)
        assertEquals("com.adobe.marketing.mobile.notificationbuilder.internal.builders.DummyActivity", intent.resolveActivity(context.packageManager).className)
    }

    @Test
    fun `Action with default hint text should be created when inputTextHint field is empty`() {
        val dataMap = MockInputBoxPushTemplateDataProvider.getMockedInputBoxDataMapWithRequiredData()
        dataMap[PushTemplateConstants.PushPayloadKeys.INPUT_BOX_HINT] = ""

        val pushTemplate = InputBoxPushTemplate(MapData(dataMap))
        val notificationBuilder = InputBoxNotificationBuilder.construct(
            context,
            pushTemplate,
            trackerActivityClass,
            broadcastReceiverClass
        )

        val actions = notificationBuilder.mActions
        assertEquals(PushTemplateConstants.DefaultValues.INPUT_BOX_DEFAULT_REPLY_TEXT, actions[0].title)
    }

    @Test
    fun `Action with default hint text should be created when inputTextHint field is null`() {
        val dataMap = MockInputBoxPushTemplateDataProvider.getMockedInputBoxDataMapWithRequiredData()
        dataMap.remove(PushTemplateConstants.PushPayloadKeys.INPUT_BOX_HINT)

        val pushTemplate = InputBoxPushTemplate(MapData(dataMap))
        val notificationBuilder = InputBoxNotificationBuilder.construct(
            context,
            pushTemplate,
            trackerActivityClass,
            broadcastReceiverClass
        )

        val actions = notificationBuilder.mActions
        assertEquals(PushTemplateConstants.DefaultValues.INPUT_BOX_DEFAULT_REPLY_TEXT, actions[0].title)
    }

    @Test
    fun `Action with provided hint text should be created when inputTextHint field is present`() {
        val pushTemplate = provideMockedInputBoxPushTemplateWithRequiredData()
        val notificationBuilder = InputBoxNotificationBuilder.construct(
            context,
            pushTemplate,
            trackerActivityClass,
            broadcastReceiverClass
        )

        val actions = notificationBuilder.mActions
        assertEquals(MOCKED_HINT, actions[0].title)
    }
}
