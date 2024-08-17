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
import android.os.Build
import android.os.Bundle
import androidx.core.app.NotificationCompat
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.BasicPushTemplate
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.MockAEPPushTemplateDataProvider
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.removeKeysFromMap
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.replaceValueInMap
import com.adobe.marketing.mobile.notificationbuilder.internal.util.IntentData
import com.adobe.marketing.mobile.notificationbuilder.internal.util.MapData
import com.adobe.ui_utils.PushTemplateConstants
import com.adobe.ui_utils.PushTemplateConstants.DefaultValues.DEFAULT_CHANNEL_ID
import com.adobe.ui_utils.PushTemplateConstants.DefaultValues.SILENT_NOTIFICATION_CHANNEL_ID
import io.mockk.unmockkAll
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import org.junit.After
import org.junit.Assert.assertArrayEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [31])
class LegacyNotificationBuilderTest {

    private lateinit var trackerActivityClass: Class<out Activity>
    private lateinit var context: Context
    private lateinit var dataMap: MutableMap<String, String>
    private lateinit var mockBundle: Bundle

    @Before
    fun setUp() {
        context = RuntimeEnvironment.getApplication()
        trackerActivityClass = DummyActivity::class.java
        dataMap = MockAEPPushTemplateDataProvider.getMockedAEPDataMapWithAllKeys()
        mockBundle = MockAEPPushTemplateDataProvider.getMockedAEPBundleWithAllKeys()
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `verify construct should map valid BasicPushTemplate data to notification data`() {
        val pushTemplate = BasicPushTemplate(MapData(dataMap))
        val notification =
            LegacyNotificationBuilder.construct(context, pushTemplate, trackerActivityClass)
                .build()
        val pendingIntent = notification.contentIntent
        val shadowPendingIntent = Shadows.shadowOf(pendingIntent)
        val intent = shadowPendingIntent.savedIntent

        assertEquals(Notification::class.java, notification.javaClass)
        assertEquals(pushTemplate.ticker, notification.tickerText)
        assertEquals(
            pushTemplate.title,
            notification.extras.getString(NotificationCompat.EXTRA_TITLE)
        )
        assertEquals(
            pushTemplate.body,
            notification.extras.getString(NotificationCompat.EXTRA_TEXT)
        )
        assertEquals(pushTemplate.channelId, notification.channelId)
        assertNotNull(notification.smallIcon)
        assertEquals(
            pushTemplate.actionButtonsList?.map { it.label },
            notification.actions.map { it.title }
        )
        assertNotNull(notification.deleteIntent)
        assertEquals(
            pushTemplate.actionUri,
            intent.getStringExtra(PushTemplateConstants.TrackingKeys.ACTION_URI)
        )
        assertEquals(
            pushTemplate.tag,
            intent.getStringExtra(PushTemplateConstants.PushPayloadKeys.TAG)
        )
        assertEquals(
            pushTemplate.isNotificationSticky.toString(),
            intent.getStringExtra(PushTemplateConstants.PushPayloadKeys.STICKY)
        )
    }

    @Test
    fun `construct should set silent notification if isFromIntent is true`() {
        val pushTemplate = BasicPushTemplate(IntentData(mockBundle, null))
        val notification =
            LegacyNotificationBuilder.construct(context, pushTemplate, trackerActivityClass)
                .build()
        assertEquals(SILENT_NOTIFICATION_CHANNEL_ID, notification.channelId)
    }

    @Test
    fun `construct should set default channel ID if pushTemplate channelId is null`() {
        dataMap.removeKeysFromMap(PushTemplateConstants.PushPayloadKeys.CHANNEL_ID)
        val pushTemplate = BasicPushTemplate(MapData(dataMap))
        val notification =
            LegacyNotificationBuilder.construct(context, pushTemplate, trackerActivityClass)
                .build()
        val pendingIntent = notification.contentIntent
        val shadowPendingIntent = Shadows.shadowOf(pendingIntent)
        val intent = shadowPendingIntent.savedIntent

        assertEquals(DEFAULT_CHANNEL_ID, notification.channelId)

        // verify that rest of the notification attributes are same as the ones derived from template
        assertEquals(Notification::class.java, notification.javaClass)
        assertEquals(pushTemplate.ticker, notification.tickerText)
        assertEquals(
            pushTemplate.title,
            notification.extras.getString(NotificationCompat.EXTRA_TITLE)
        )
        assertEquals(
            pushTemplate.body,
            notification.extras.getString(NotificationCompat.EXTRA_TEXT)
        )
        assertEquals(
            pushTemplate.body,
            notification.extras.getString(NotificationCompat.EXTRA_TEXT)
        )
        assertNotNull(notification.smallIcon)
        assertEquals(
            pushTemplate.actionButtonsList?.map { it.label },
            notification.actions.map { it.title }
        )
        assertNotNull(notification.deleteIntent)
        assertEquals(
            pushTemplate.actionUri,
            intent.getStringExtra(PushTemplateConstants.TrackingKeys.ACTION_URI)
        )
        assertEquals(
            pushTemplate.tag,
            intent.getStringExtra(PushTemplateConstants.PushPayloadKeys.TAG)
        )
        assertEquals(
            pushTemplate.isNotificationSticky.toString(),
            intent.getStringExtra(PushTemplateConstants.PushPayloadKeys.STICKY)
        )
    }

    @Test
    fun `construct should not set smallIcon if pushTemplate smallIcon is invalid`() {
        dataMap.replaceValueInMap(
            Pair(
                PushTemplateConstants.PushPayloadKeys.SMALL_ICON,
                "invalid_small_icon"
            )
        )
        val pushTemplate = BasicPushTemplate(MapData(dataMap))
        val notification =
            LegacyNotificationBuilder.construct(context, pushTemplate, trackerActivityClass)
                .build()
        assertNull(notification.smallIcon)
    }

    @Test
    fun `construct should not set notification sound if pushTemplate sound is invalid`() {
        dataMap.replaceValueInMap(
            Pair(
                PushTemplateConstants.PushPayloadKeys.SOUND,
                "invalid_sound"
            )
        )
        val pushTemplate = BasicPushTemplate(MapData(dataMap))
        val notification =
            LegacyNotificationBuilder.construct(context, pushTemplate, trackerActivityClass)
                .build()
        assertNull(notification.sound)
    }

    @Test
    fun `construct should set sticky flag to false when isNotificationSticky is false`() {
        dataMap.replaceValueInMap(
            Pair(
                PushTemplateConstants.PushPayloadKeys.STICKY,
                "false"
            )
        )
        val pushTemplate = BasicPushTemplate(MapData(dataMap))
        val notification =
            LegacyNotificationBuilder.construct(context, pushTemplate, trackerActivityClass)
                .build()
        val pendingIntent = notification.contentIntent
        val shadowPendingIntent = Shadows.shadowOf(pendingIntent)
        val intent = shadowPendingIntent.savedIntent
        // Assert that the sticky flag is false
        assertEquals(
            "false", intent.getStringExtra(PushTemplateConstants.PushPayloadKeys.STICKY)
        )
    }

    @Config(sdk = [25])
    @Test
    fun `construct should set priority and vibration for API level below 26`() {
        val pushTemplate = BasicPushTemplate(MapData(dataMap))
        val notification =
            LegacyNotificationBuilder.construct(context, pushTemplate, trackerActivityClass)
                .build()
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            assertEquals(NotificationCompat.PRIORITY_HIGH, notification.priority)
            assertArrayEquals(LongArray(0), notification.vibrate)
        }
    }
}

class DummyActivity : Activity() {
    // empty class for testing
}
