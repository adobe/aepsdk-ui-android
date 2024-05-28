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
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.adobe.marketing.mobile.notificationbuilder.internal.PushTemplateConstants
import com.adobe.marketing.mobile.notificationbuilder.internal.PushTemplateConstants.DEFAULT_CHANNEL_ID
import com.adobe.marketing.mobile.notificationbuilder.internal.PushTemplateConstants.SILENT_CHANNEL_NAME
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.BasicPushTemplate
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import org.junit.Assert.assertArrayEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import kotlin.test.assertNull

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [31])
class LegacyNotificationBuilderTest {

    @Mock
    private lateinit var pushTemplate: BasicPushTemplate
    private lateinit var trackerActivityClass: Class<out Activity>
    private lateinit var context: Context
    private val SUT = LegacyNotificationBuilder

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        context = RuntimeEnvironment.getApplication()
        trackerActivityClass = DummyActivity::class.java
    }

    @Test
    fun `construct should return a NotificationCompat Builder`() {
        val notificationBuilder = SUT.construct(context, pushTemplate, trackerActivityClass)
        assertEquals(NotificationCompat.Builder::class.java, notificationBuilder.javaClass)
    }

    @Test
    fun `construct should set ticker to pushTemplate ticker`() {
        `when`(pushTemplate.ticker).thenReturn("Test Ticker")
        val notificationBuilder = SUT.construct(context, pushTemplate, trackerActivityClass)
        assertEquals(pushTemplate.ticker, notificationBuilder.build().tickerText)
    }

    @Test
    fun `construct should set content title to pushTemplate title`() {
        `when`(pushTemplate.title).thenReturn("Test Title")
        val notificationBuilder =
            SUT.construct(context, pushTemplate, trackerActivityClass)
        assertEquals(
            pushTemplate.title,
            notificationBuilder.build().extras.getString(NotificationCompat.EXTRA_TITLE)
        )
    }

    @Test
    fun `construct should set content text to pushTemplate body`() {
        `when`(pushTemplate.body).thenReturn("Test Body")
        val notificationBuilder =
            SUT.construct(context, pushTemplate, trackerActivityClass)
        assertEquals(
            pushTemplate.body,
            notificationBuilder.build().extras.getString(NotificationCompat.EXTRA_TEXT)
        )
    }

    @Test
    fun `construct should set number to pushTemplate badgeCount`() {
        `when`(pushTemplate.badgeCount).thenReturn(5)
        val notificationBuilder =
            SUT.construct(context, pushTemplate, trackerActivityClass)
        assertEquals(pushTemplate.badgeCount, notificationBuilder.build().number)
    }

    @Test
    fun `construct should set channelId to pushTemplate channelId`() {
        `when`(pushTemplate.channelId).thenReturn("Test Channel Id")
        val notificationBuilder =
            SUT.construct(context, pushTemplate, trackerActivityClass)
        assertEquals(pushTemplate.channelId, notificationBuilder.build().channelId)
    }

    @Test
    fun `construct should set silent notification if isFromIntent is true`() {
        `when`(pushTemplate.channelId).thenReturn("Test Channel Id")
        `when`(pushTemplate.getNotificationImportance()).thenReturn(NotificationManager.IMPORTANCE_HIGH)
        `when`(pushTemplate.isFromIntent).thenReturn(true)
        val notificationBuilder =
            SUT.construct(context, pushTemplate, trackerActivityClass)
        assertEquals(SILENT_CHANNEL_NAME, notificationBuilder.build().channelId)
    }

    @Test
    fun `construct should not set silent notification if isFromIntent is false`() {
        `when`(pushTemplate.channelId).thenReturn("Test Channel Id")
        `when`(pushTemplate.getNotificationImportance()).thenReturn(NotificationManager.IMPORTANCE_HIGH)
        `when`(pushTemplate.isFromIntent).thenReturn(false)
        val notificationBuilder =
            SUT.construct(context, pushTemplate, trackerActivityClass)
        assertEquals(pushTemplate.channelId, notificationBuilder.build().channelId)
    }

    @Test
    fun `construct should set default channel ID if pushTemplate channelId is null`() {
        `when`(pushTemplate.channelId).thenReturn(null)
        `when`(pushTemplate.getNotificationImportance()).thenReturn(NotificationManager.IMPORTANCE_HIGH)
        `when`(pushTemplate.isFromIntent).thenReturn(false)
        val notificationBuilder =
            SUT.construct(context, pushTemplate, trackerActivityClass)
        assertEquals(DEFAULT_CHANNEL_ID, notificationBuilder.build().channelId)
    }

    @Test
    fun `construct should set notification visibility to public`() {
        `when`(pushTemplate.getNotificationVisibility()).thenReturn(NotificationCompat.VISIBILITY_PUBLIC)
        val notificationBuilder =
            SUT.construct(context, pushTemplate, trackerActivityClass)
        assertEquals(NotificationCompat.VISIBILITY_PUBLIC, notificationBuilder.build().visibility)
    }

    @Test
    fun `construct should set notification visibility to private`() {
        `when`(pushTemplate.getNotificationVisibility()).thenReturn(NotificationCompat.VISIBILITY_PRIVATE)
        val notificationBuilder =
            SUT.construct(context, pushTemplate, trackerActivityClass)
        assertEquals(NotificationCompat.VISIBILITY_PRIVATE, notificationBuilder.build().visibility)
    }

    @Test
    fun `construct should set priority to high for API level below 26`() {
        `when`(pushTemplate.getNotificationImportance()).thenReturn(NotificationManager.IMPORTANCE_HIGH)
        val notificationBuilder =
            SUT.construct(context, pushTemplate, trackerActivityClass)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            assertEquals(NotificationCompat.PRIORITY_HIGH, notificationBuilder.build().priority)
        }
    }

    @Test
    fun `construct should not set priority for API level 26 and above`() {
        `when`(pushTemplate.getNotificationImportance()).thenReturn(NotificationManager.IMPORTANCE_HIGH)
        val notificationBuilder =
            SUT.construct(context, pushTemplate, trackerActivityClass)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            assertEquals(0, notificationBuilder.build().priority)
        }
    }

    @Test
    fun `construct should set smallIcon to pushTemplate smallIcon`() {
        `when`(pushTemplate.smallIcon).thenReturn("skipleft")
        val notificationBuilder =
            SUT.construct(context, pushTemplate, trackerActivityClass)
        assertNotNull(notificationBuilder.build().smallIcon)
    }

    @Test
    fun `construct should not set smallIcon if pushTemplate smallIcon is invalid`() {
        `when`(pushTemplate.smallIcon).thenReturn("someRandomResourceName")
        val notificationBuilder =
            SUT.construct(context, pushTemplate, trackerActivityClass)
        assertNull(notificationBuilder.build().smallIcon)
    }

    @Test
    fun `construct should add action buttons to pushTemplate actionButtonsList`() {
        val actionButtonsList = listOf(
            BasicPushTemplate.ActionButton("label1", null, null),
            BasicPushTemplate.ActionButton("label2", null, null)
        )
        `when`(pushTemplate.actionButtonsList).thenReturn(actionButtonsList)
        val notificationBuilder = SUT.construct(context, pushTemplate, trackerActivityClass)
        assertEquals(
            actionButtonsList.map { it.label },
            notificationBuilder.build().actions.map { it.title }
        )
    }

    @Test
    fun `construct should set notification delete action`() {
        val notificationBuilder =
            SUT.construct(context, pushTemplate, trackerActivityClass)
        assertNotNull(notificationBuilder.build().deleteIntent)
    }

    @Test
    fun `construct should handle invalid pushTemplate sound`() {
        `when`(pushTemplate.sound).thenReturn("invalid_sound")
        val notificationBuilder =
            SUT.construct(context, pushTemplate, trackerActivityClass)
        assertNull(notificationBuilder.build().sound)
    }

    @Test
    fun `construct should set notification click action`() {
        `when`(pushTemplate.actionUri).thenReturn("test://actionUri")
        `when`(pushTemplate.tag).thenReturn("testTag")
        `when`(pushTemplate.isNotificationSticky).thenReturn(true)
        val notificationBuilder =
            SUT.construct(context, pushTemplate, trackerActivityClass)

        val pendingIntent = notificationBuilder.build().contentIntent
        val shadowPendingIntent = Shadows.shadowOf(pendingIntent)
        val intent = shadowPendingIntent.savedIntent

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

    @Config(sdk = [25])
    @Test
    fun `construct should set priority and vibration for API level below 26`() {
        val notificationBuilder =
            SUT.construct(context, pushTemplate, trackerActivityClass)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            assertEquals(NotificationCompat.PRIORITY_HIGH, notificationBuilder.build().priority)
            assertArrayEquals(LongArray(0), notificationBuilder.build().vibrate)
        }
    }
}

class DummyActivity : Activity() {
    // empty class for testing
}
