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
import android.app.Notification
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.adobe.ui_utils.PushTemplateConstants
import com.adobe.ui_utils.PushTemplateConstants.IntentActions.TIMER_EXPIRED
import com.adobe.marketing.mobile.notificationbuilder.R
import com.adobe.marketing.mobile.notificationbuilder.internal.extensions.setNotificationBackgroundColor
import com.adobe.marketing.mobile.notificationbuilder.internal.extensions.setNotificationBodyTextColor
import com.adobe.marketing.mobile.notificationbuilder.internal.extensions.setNotificationTitleTextColor
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.AEPPushTemplate
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.BasicPushTemplate
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.MockAEPPushTemplateDataProvider
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.replaceValueInMap
import com.adobe.marketing.mobile.notificationbuilder.internal.util.IntentData
import com.adobe.marketing.mobile.notificationbuilder.internal.util.MapData
import io.mockk.unmockkAll
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import org.junit.After
import org.junit.Assert.assertArrayEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [31])
class AEPPushNotificationBuilderTest {

    @Mock
    private lateinit var smallLayout: RemoteViews

    @Mock
    private lateinit var expandedLayout: RemoteViews

    private lateinit var context: Context
    private lateinit var dataMap: MutableMap<String, String>
    private lateinit var trackerActivityClass: Class<out Activity>
    private lateinit var mockBundle: Bundle

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        context = RuntimeEnvironment.getApplication()
        dataMap = MockAEPPushTemplateDataProvider.getMockedAEPDataMapWithAllKeys()
        trackerActivityClass = DummyActivity::class.java
        mockBundle = MockAEPPushTemplateDataProvider.getMockedAEPBundleWithAllKeys()
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `verify construct should map valid data fields to notification data`() {
        val pushTemplate = BasicPushTemplate(MapData(dataMap))
        val notification = AEPPushNotificationBuilder.construct(
            context,
            pushTemplate,
            CHANNEL_ID_TO_USE,
            trackerActivityClass,
            smallLayout,
            expandedLayout,
            CONTAINER_LAYOUT_VIEW_ID
        ).build()

        verifyNotificationDataFields(notification, pushTemplate)
    }

    @Test
    fun `verify construct should map valid data fields and colors to notification view`() {
        val pushTemplate = BasicPushTemplate(MapData(dataMap))
        AEPPushNotificationBuilder.construct(
            context,
            pushTemplate,
            CHANNEL_ID_TO_USE,
            trackerActivityClass,
            smallLayout,
            expandedLayout,
            CONTAINER_LAYOUT_VIEW_ID
        )

        verifyNotificationViewDataAndColors(pushTemplate)
    }

    @Test
    fun `construct should map valid data to notification data and view for Intent Data`() {
        val pushTemplate = BasicPushTemplate(IntentData(mockBundle, null))
        val notification = AEPPushNotificationBuilder.construct(
            context,
            pushTemplate,
            CHANNEL_ID_TO_USE,
            trackerActivityClass,
            smallLayout,
            expandedLayout,
            CONTAINER_LAYOUT_VIEW_ID
        ).build()

        verifyNotificationDataFields(notification, pushTemplate)
        verifyNotificationViewDataAndColors(pushTemplate)
    }

    @Test
    fun `construct should not set notification sound if pushTemplate sound is invalid`() {
        dataMap.replaceValueInMap(
            Pair(
                com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.SOUND,
                "invalid_sound"
            )
        )
        val pushTemplate = BasicPushTemplate(MapData(dataMap))
        val notification =
            AEPPushNotificationBuilder.construct(
                context,
                pushTemplate,
                CHANNEL_ID_TO_USE,
                trackerActivityClass,
                smallLayout,
                expandedLayout,
                CONTAINER_LAYOUT_VIEW_ID
            ).build()

        assertNull(notification.sound)
    }

    @Test
    fun `test createIntent returns intent with mapped properties`() {
        val pushTemplate = BasicPushTemplate(MapData(dataMap))
        val action = TIMER_EXPIRED
        val intent = AEPPushNotificationBuilder.createIntent(action, pushTemplate)

        assertEquals(action, intent.action)
        assertEquals(Intent.FLAG_ACTIVITY_SINGLE_TOP, intent.flags)
        assertNotNull(intent.extras)
    }

    @Config(sdk = [25])
    @Test
    fun `construct should set priority and vibration for API level below 26`() {
        val pushTemplate = BasicPushTemplate(MapData(dataMap))
        val notification =
            AEPPushNotificationBuilder.construct(
                context,
                pushTemplate,
                CHANNEL_ID_TO_USE,
                trackerActivityClass,
                smallLayout,
                expandedLayout,
                CONTAINER_LAYOUT_VIEW_ID
            ).build()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            assertEquals(NotificationCompat.PRIORITY_HIGH, notification.priority)
            assertArrayEquals(LongArray(0), notification.vibrate)
        }
    }

    @Test
    @Config(sdk = [25])
    fun `construct should set sound on Notification Builder for API level 25 and below`() {
        val pushTemplate = BasicPushTemplate(MapData(dataMap))
        val notification =
            AEPPushNotificationBuilder.construct(
                context,
                pushTemplate,
                CHANNEL_ID_TO_USE,
                trackerActivityClass,
                smallLayout,
                expandedLayout,
                CONTAINER_LAYOUT_VIEW_ID
            ).build()
        val soundUri =
            "android.resource://com.adobe.marketing.mobile.notificationbuilder.test/raw/${pushTemplate.sound}"
        assertEquals(soundUri, notification.sound.toString())
    }

    private fun verifyNotificationDataFields(
        notification: Notification,
        pushTemplate: AEPPushTemplate
    ) {
        val pendingIntent = notification.contentIntent
        val shadowPendingIntent = Shadows.shadowOf(pendingIntent)
        val intent = shadowPendingIntent.savedIntent

        assertEquals(Notification::class.java, notification.javaClass)
        assertEquals(pushTemplate.ticker, notification.tickerText)
        assertEquals(notification.channelId, CHANNEL_ID_TO_USE)
        assertEquals(pushTemplate.badgeCount, notification.number)
        assertEquals(pushTemplate.visibility.value, notification.visibility)
        assertNotNull(notification.smallIcon)
        assertNotNull(notification.deleteIntent)
        assertEquals(
            pushTemplate.actionUri,
            intent.getStringExtra(com.adobe.ui_utils.PushTemplateConstants.TrackingKeys.ACTION_URI)
        )
        assertEquals(
            pushTemplate.tag,
            intent.getStringExtra(com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.TAG)
        )
        assertEquals(
            pushTemplate.isNotificationSticky.toString(),
            intent.getStringExtra(com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.STICKY)
        )
    }

    private fun verifyNotificationViewDataAndColors(pushTemplate: AEPPushTemplate) {
        verify(smallLayout).setTextViewText(R.id.notification_title, pushTemplate.title)
        verify(smallLayout).setTextViewText(R.id.notification_body, pushTemplate.body)
        verify(expandedLayout).setTextViewText(R.id.notification_title, pushTemplate.title)
        verify(expandedLayout).setTextViewText(
            R.id.notification_body_expanded,
            pushTemplate.expandedBodyText
        )
        verify(smallLayout).setNotificationBackgroundColor(
            pushTemplate.backgroundColor,
            R.id.basic_small_layout
        )
        verify(expandedLayout).setNotificationBackgroundColor(
            pushTemplate.backgroundColor,
            CONTAINER_LAYOUT_VIEW_ID
        )
        verify(smallLayout).setNotificationTitleTextColor(
            pushTemplate.titleTextColor,
            R.id.notification_title
        )
        verify(expandedLayout).setNotificationTitleTextColor(
            pushTemplate.titleTextColor,
            R.id.notification_title
        )
        verify(smallLayout).setNotificationBodyTextColor(
            pushTemplate.bodyTextColor,
            R.id.notification_body
        )
        verify(expandedLayout).setNotificationBodyTextColor(
            pushTemplate.bodyTextColor,
            R.id.notification_body_expanded
        )
    }

    companion object {
        private const val CHANNEL_ID_TO_USE = "channel_id"
        private const val CONTAINER_LAYOUT_VIEW_ID = 123
    }
}
