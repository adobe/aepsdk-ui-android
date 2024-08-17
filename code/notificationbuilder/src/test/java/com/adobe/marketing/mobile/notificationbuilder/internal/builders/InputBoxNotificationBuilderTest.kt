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
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.adobe.marketing.mobile.notificationbuilder.R
import com.adobe.marketing.mobile.notificationbuilder.internal.extensions.setRemoteImage
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.InputBoxPushTemplate
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.MOCKED_BASIC_TEMPLATE_BODY
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.MOCKED_BASIC_TEMPLATE_BODY_EXPANDED
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.MOCKED_BODY
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.MOCKED_FEEDBACK_IMAGE
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.MOCKED_FEEDBACK_TEXT
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.MOCKED_HINT
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.MOCKED_IMAGE_URI
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.MOCKED_RECEIVER_NAME
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.MOCKED_TITLE
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.MockInputBoxPushTemplateDataProvider
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.provideMockedInputBoxPushTemplateWithAllKeys
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.provideMockedInputBoxPushTemplateWithRequiredData
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.removeKeysFromMap
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.replaceValueInMap
import com.adobe.marketing.mobile.notificationbuilder.internal.util.MapData
import com.adobe.ui_utils.PushTemplateConstants
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockkConstructor
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.After
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
        mockkConstructor(RemoteViews::class)
    }

    @After
    fun tearDown() {
        unmockkAll()
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

        assertNotNull(notificationBuilder)
        assertEquals(NotificationCompat.Builder::class.java, notificationBuilder.javaClass)
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

        assertNotNull(notificationBuilder)
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

        assertNotNull(notificationBuilder)
        assertEquals(1, notificationBuilder.mActions.size)
        assertNotNull(notificationBuilder.mActions.find { it.title == MOCKED_HINT })
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
    fun `createInputReceivedPendingIntent should set class when trackerActivityClass parameter is not null`() {
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
    fun `createInputReceivedPendingIntent should set intent extras correctly`() {
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
        assertEquals(MOCKED_TITLE, intent.getStringExtra(PushTemplateConstants.PushPayloadKeys.TITLE))
        assertEquals(MOCKED_BODY, intent.getStringExtra(PushTemplateConstants.PushPayloadKeys.BODY))
        assertEquals(MOCKED_RECEIVER_NAME, intent.getStringExtra(PushTemplateConstants.PushPayloadKeys.INPUT_BOX_RECEIVER_NAME))
        assertEquals(MOCKED_FEEDBACK_IMAGE, intent.getStringExtra(PushTemplateConstants.PushPayloadKeys.INPUT_BOX_FEEDBACK_IMAGE))
        assertEquals(MOCKED_FEEDBACK_TEXT, intent.getStringExtra(PushTemplateConstants.PushPayloadKeys.INPUT_BOX_FEEDBACK_TEXT))
        assertEquals(MOCKED_HINT, intent.getStringExtra(PushTemplateConstants.PushPayloadKeys.INPUT_BOX_HINT))
    }

    @Test
    fun `Action with default hint text should be created when inputTextHint field is empty`() {
        val dataMap = MockInputBoxPushTemplateDataProvider.getMockedInputBoxDataMapWithRequiredData()
        dataMap.replaceValueInMap(PushTemplateConstants.PushPayloadKeys.INPUT_BOX_HINT, "")

        val pushTemplate = InputBoxPushTemplate(MapData(dataMap))
        val notificationBuilder = InputBoxNotificationBuilder.construct(
            context,
            pushTemplate,
            trackerActivityClass,
            broadcastReceiverClass
        )

        assertNotNull(notificationBuilder)

        val actions = notificationBuilder.mActions
        assertEquals(PushTemplateConstants.DefaultValues.INPUT_BOX_DEFAULT_REPLY_TEXT, actions[0].title)
    }

    @Test
    fun `Action with default hint text should be created when inputTextHint field is null`() {
        val dataMap = MockInputBoxPushTemplateDataProvider.getMockedInputBoxDataMapWithRequiredData()
        dataMap.removeKeysFromMap(PushTemplateConstants.PushPayloadKeys.INPUT_BOX_HINT)

        val pushTemplate = InputBoxPushTemplate(MapData(dataMap))
        val notificationBuilder = InputBoxNotificationBuilder.construct(
            context,
            pushTemplate,
            trackerActivityClass,
            broadcastReceiverClass
        )

        assertNotNull(notificationBuilder)

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

        assertNotNull(notificationBuilder)

        val actions = notificationBuilder.mActions
        assertEquals(MOCKED_HINT, actions[0].title)
    }

    @Test
    fun `Validate notification content when push template is created from intent`() {
        val pushTemplate = provideMockedInputBoxPushTemplateWithAllKeys(true)
        every { anyConstructed<RemoteViews>().setTextViewText(any(), any()) } just Runs

        val notificationBuilder = InputBoxNotificationBuilder.construct(
            context,
            pushTemplate,
            trackerActivityClass,
            broadcastReceiverClass
        )

        verify { anyConstructed<RemoteViews>().setRemoteImage(MOCKED_FEEDBACK_IMAGE, R.id.expanded_template_image) }
        verify { anyConstructed<RemoteViews>().setTextViewText(R.id.notification_body, MOCKED_FEEDBACK_TEXT) }
        verify { anyConstructed<RemoteViews>().setTextViewText(R.id.notification_body_expanded, MOCKED_FEEDBACK_TEXT) }
    }

    @Test
    fun `Validate notification content when push template is not created from intent`() {
        val pushTemplate = provideMockedInputBoxPushTemplateWithAllKeys()
        every { anyConstructed<RemoteViews>().setTextViewText(any(), any()) } just Runs

        val notificationBuilder = InputBoxNotificationBuilder.construct(
            context,
            pushTemplate,
            trackerActivityClass,
            broadcastReceiverClass
        )

        verify { anyConstructed<RemoteViews>().setRemoteImage(MOCKED_IMAGE_URI, R.id.expanded_template_image) }
        verify { anyConstructed<RemoteViews>().setTextViewText(R.id.notification_body, MOCKED_BASIC_TEMPLATE_BODY) }
        verify { anyConstructed<RemoteViews>().setTextViewText(R.id.notification_body_expanded, MOCKED_BASIC_TEMPLATE_BODY_EXPANDED) }
    }
}
