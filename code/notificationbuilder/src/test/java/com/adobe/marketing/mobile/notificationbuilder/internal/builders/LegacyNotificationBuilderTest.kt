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
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.core.app.NotificationCompat
import com.adobe.marketing.mobile.notificationbuilder.internal.PushTemplateConstants
import com.adobe.marketing.mobile.notificationbuilder.internal.PushTemplateConstants.DefaultValues.DEFAULT_CHANNEL_ID
import com.adobe.marketing.mobile.notificationbuilder.internal.PushTemplateConstants.DefaultValues.SILENT_NOTIFICATION_CHANNEL_ID
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.BasicPushTemplate
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.MockAEPPushTemplateDataProvider
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.removeKeysFromMap
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.replaceValueInMap
import com.adobe.marketing.mobile.notificationbuilder.internal.util.IntentData
import com.adobe.marketing.mobile.notificationbuilder.internal.util.MapData
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
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

    @Test
    fun `verify construct should map valid BasicPushTemplate data to notification data`() {
        val pushTemplate = BasicPushTemplate(MapData(dataMap))
        val notificationBuilder =
            LegacyNotificationBuilder.construct(context, pushTemplate, trackerActivityClass)
        val pendingIntent = notificationBuilder.build().contentIntent
        val shadowPendingIntent = Shadows.shadowOf(pendingIntent)
        val intent = shadowPendingIntent.savedIntent

        assertEquals(NotificationCompat.Builder::class.java, notificationBuilder.javaClass)
        assertEquals(pushTemplate.ticker, notificationBuilder.build().tickerText)
        assertEquals(
            pushTemplate.title,
            notificationBuilder.build().extras.getString(NotificationCompat.EXTRA_TITLE)
        )
        assertEquals(
            pushTemplate.body,
            notificationBuilder.build().extras.getString(NotificationCompat.EXTRA_TEXT)
        )
        assertEquals(
            pushTemplate.body,
            notificationBuilder.build().extras.getString(NotificationCompat.EXTRA_TEXT)
        )
        assertEquals(pushTemplate.channelId, notificationBuilder.build().channelId)
        assertNotNull(notificationBuilder.build().smallIcon)
        assertEquals(
            pushTemplate.actionButtonsList?.map { it.label },
            notificationBuilder.build().actions.map { it.title }
        )
        assertNotNull(notificationBuilder.build().deleteIntent)
        assertEquals(
            pushTemplate.actionUri,
            intent.getStringExtra(PushTemplateConstants.Tracking.TrackingKeys.ACTION_URI)
        )
        assertEquals(
            pushTemplate.tag,
            intent.getStringExtra(PushTemplateConstants.PushPayloadKeys.TAG)
        )
        assertEquals(
            pushTemplate.isNotificationSticky,
            intent.getBooleanExtra(PushTemplateConstants.PushPayloadKeys.STICKY, false)
        )
    }

    @Test
    fun `construct should set silent notification if isFromIntent is true`() {
        val pushTemplate = BasicPushTemplate(IntentData(mockBundle, null))
        val notificationBuilder =
            LegacyNotificationBuilder.construct(context, pushTemplate, trackerActivityClass)
        assertEquals(SILENT_NOTIFICATION_CHANNEL_ID, notificationBuilder.build().channelId)
    }

    @Test
    fun `construct should set default channel ID if pushTemplate channelId is null`() {
        dataMap.removeKeysFromMap(PushTemplateConstants.PushPayloadKeys.CHANNEL_ID)
        val pushTemplate = BasicPushTemplate(MapData(dataMap))
        val notificationBuilder =
            LegacyNotificationBuilder.construct(context, pushTemplate, trackerActivityClass)
        assertEquals(DEFAULT_CHANNEL_ID, notificationBuilder.build().channelId)
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
        val notificationBuilder =
            LegacyNotificationBuilder.construct(context, pushTemplate, trackerActivityClass)
        assertNull(notificationBuilder.build().smallIcon)
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
        val notificationBuilder =
            LegacyNotificationBuilder.construct(context, pushTemplate, trackerActivityClass)
        assertNull(notificationBuilder.build().sound)
    }

    @Config(sdk = [25])
    @Test
    fun `construct should set priority and vibration for API level below 26`() {
        val pushTemplate = BasicPushTemplate(MapData(dataMap))
        val notificationBuilder =
            LegacyNotificationBuilder.construct(context, pushTemplate, trackerActivityClass)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            assertEquals(NotificationCompat.PRIORITY_HIGH, notificationBuilder.build().priority)
            assertArrayEquals(LongArray(0), notificationBuilder.build().vibrate)
        }
    }
}

class DummyActivity : Activity() {
    // empty class for testing
}
