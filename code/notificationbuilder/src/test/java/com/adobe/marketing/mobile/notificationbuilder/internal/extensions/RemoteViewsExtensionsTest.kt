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

import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.RemoteViews
import com.adobe.marketing.mobile.notificationbuilder.PushTemplateConstants
import com.adobe.marketing.mobile.notificationbuilder.internal.builders.DummyActivity
import com.adobe.marketing.mobile.services.Logging
import com.adobe.marketing.mobile.services.ServiceProvider
import com.adobe.marketing.mobile.utils.PushTemplateImageUtils
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import kotlin.test.assertEquals

// this class also tests PendingIntentUtils.createPendingIntentForTrackerActivity
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [31])
class RemoteViewsExtensionsTest {

    private lateinit var remoteViews: RemoteViews
    private lateinit var mockBitmap: Bitmap
    private lateinit var trackerActivityClass: Class<out Activity>

    @Before
    fun setup() {
        remoteViews = mockk<RemoteViews>(relaxed = true)
        mockkObject(PushTemplateImageUtils)
        mockBitmap = mockk<Bitmap>(relaxed = true)
        trackerActivityClass = DummyActivity::class.java
    }

    @After
    fun teardown() {
        unmockkAll()
    }

    @Test
    fun `setElementColor applies color when valid hex string provided`() {
        val colorHex = "#FFFFFF"
        val elementIdCapture = slot<Int>()
        val methodNameCapture = slot<String>()
        val colorCapture = slot<Int>()
        every {
            remoteViews.setInt(
                capture(elementIdCapture),
                capture(methodNameCapture),
                capture(colorCapture)
            )
        } just Runs

        remoteViews.setElementColor(1, colorHex, "setBackgroundColor", "testFriendlyName")

        verify(exactly = 1) { remoteViews.setInt(any(), any(), any()) }
        assertEquals(1, elementIdCapture.captured)
        assertEquals("setBackgroundColor", methodNameCapture.captured)
        assertEquals(Color.parseColor(colorHex), colorCapture.captured)
    }

    @Test
    fun `setElementColor does not apply color when hex string is null`() {
        remoteViews.setElementColor(1, null, "setBackgroundColor", "testFriendlyName")

        verify(exactly = 0) { remoteViews.setInt(any(), any(), any()) }
    }

    @Test
    fun `setElementColor does not apply color when hex string is empty`() {

        remoteViews.setElementColor(1, "", "setBackgroundColor", "testFriendlyName")

        verify(exactly = 0) { remoteViews.setInt(any(), any(), any()) }
    }

    @Test
    fun `setElementColor does not apply color when hex string is invalid`() {
        val colorHex = "invalid"

        remoteViews.setElementColor(1, colorHex, "setBackgroundColor", "testFriendlyName")

        verify(exactly = 0) { remoteViews.setInt(any(), any(), any()) }
    }

    @Test
    fun `setNotificationBackgroundColor applies color when valid hex string provided`() {
        val colorHex = "FFFFFF"

        remoteViews.setNotificationBackgroundColor(colorHex, 1)

        verify {
            remoteViews.setElementColor(
                1,
                "#$colorHex",
                PushTemplateConstants.MethodNames.SET_BACKGROUND_COLOR,
                PushTemplateConstants.FriendlyViewNames.NOTIFICATION_BACKGROUND
            )
        }
    }

    @Test
    fun `setTimerTextColor applies color when valid hex string provided`() {
        val colorHex = "FFFFFF"

        remoteViews.setTimerTextColor(colorHex, 1)

        verify {
            remoteViews.setElementColor(
                1,
                "#$colorHex",
                PushTemplateConstants.MethodNames.SET_TEXT_COLOR,
                PushTemplateConstants.FriendlyViewNames.TIMER_TEXT
            )
        }
    }

    @Test
    fun `setNotificationTitleTextColor applies color when valid hex string provided`() {
        val colorHex = "FFFFFF"

        remoteViews.setNotificationTitleTextColor(colorHex, 1)

        verify {
            remoteViews.setElementColor(
                1,
                "#$colorHex",
                PushTemplateConstants.MethodNames.SET_TEXT_COLOR,
                PushTemplateConstants.FriendlyViewNames.NOTIFICATION_TITLE
            )
        }
    }

    @Test
    fun `setNotificationBodyTextColor applies color when valid hex string provided`() {
        val colorHex = "FFFFFF"

        remoteViews.setNotificationBodyTextColor(colorHex, 1)

        verify {
            remoteViews.setElementColor(
                1,
                "#$colorHex",
                PushTemplateConstants.MethodNames.SET_TEXT_COLOR,
                PushTemplateConstants.FriendlyViewNames.NOTIFICATION_BODY_TEXT
            )
        }
    }

    @Test
    fun `setRemoteViewImage applies image when valid URL provided`() {
        val imageUrl = "http://example.com/image.png"
        every { PushTemplateImageUtils.cacheImages(listOf(imageUrl)) } returns 1
        every { PushTemplateImageUtils.getCachedImage(imageUrl) } returns mockBitmap

        val result = remoteViews.setRemoteViewImage(imageUrl, 1)

        assertTrue(result)
        verify { remoteViews.setImageViewBitmap(1, mockBitmap) }
        verify(exactly = 0) { remoteViews.setViewVisibility(1, View.GONE) }
    }

    @Test
    fun `setRemoteViewImage applies image when valid resource name provided`() {
        val imageName = "valid_image"
        mockkStatic(ServiceProvider::class)
        mockkStatic(Context::getIconWithResourceName)
        every {
            ServiceProvider.getInstance().appContextService.applicationContext?.getIconWithResourceName(
                imageName
            )
        } returns 1234

        val result = remoteViews.setRemoteViewImage(imageName, 1)

        assertTrue(result)
        verify { remoteViews.setImageViewResource(1, 1234) }
        verify(exactly = 0) { remoteViews.setViewVisibility(1, View.GONE) }
    }

    @Test
    fun `setRemoteViewImage does not apply image when image string is null`() {
        every { remoteViews.setViewVisibility(any(), any()) } just Runs
        val result = remoteViews.setRemoteViewImage(null, 1)

        assertFalse(result)
        verify(exactly = 0) { remoteViews.setImageViewBitmap(any(), any()) }
        verify(exactly = 0) { remoteViews.setImageViewResource(any(), any()) }
        verify(exactly = 1) { remoteViews.setViewVisibility(1, View.GONE) }
    }

    @Test
    fun `setRemoteViewImage does not apply image when image string is empty`() {
        every { remoteViews.setViewVisibility(any(), any()) } just Runs
        val result = remoteViews.setRemoteViewImage("", 1)

        assertFalse(result)
        verify(exactly = 0) { remoteViews.setImageViewBitmap(any(), any()) }
        verify(exactly = 0) { remoteViews.setImageViewResource(any(), any()) }
        verify(exactly = 1) { remoteViews.setViewVisibility(1, View.GONE) }
    }

    @Test
    fun `setRemoteViewImage does not apply image when URL is invalid`() {
        val imageUrl = "invalid_url"
        every { remoteViews.setViewVisibility(any(), any()) } just Runs
        every { PushTemplateImageUtils.cacheImages(listOf(imageUrl)) } returns 0

        val result = remoteViews.setRemoteViewImage(imageUrl, 1)

        assertFalse(result)
        verify(exactly = 0) { remoteViews.setImageViewBitmap(any(), any()) }
        verify(exactly = 0) { remoteViews.setImageViewResource(any(), any()) }
        verify(exactly = 1) { remoteViews.setViewVisibility(1, View.GONE) }
    }

    @Test
    fun `setRemoteViewImage does not apply image URL could not be downloaded`() {
        val imageUrl = "http://example.com/image.png"
        every { remoteViews.setViewVisibility(any(), any()) } just Runs
        every { PushTemplateImageUtils.cacheImages(listOf(imageUrl)) } returns 0

        val result = remoteViews.setRemoteViewImage(imageUrl, 1)

        assertFalse(result)
        verify(exactly = 0) { remoteViews.setImageViewBitmap(any(), any()) }
        verify(exactly = 0) { remoteViews.setImageViewResource(any(), any()) }
        verify(atLeast = 1) { remoteViews.setViewVisibility(1, View.GONE) }
    }

    @Test
    fun `setRemoteViewImage does not apply image when resource name is invalid`() {
        val imageName = "invalid_image"
        every { remoteViews.setViewVisibility(any(), any()) } just Runs
        mockkStatic(ServiceProvider::class)
        mockkStatic(Context::getIconWithResourceName)
        every {
            ServiceProvider.getInstance().appContextService.applicationContext?.getIconWithResourceName(
                imageName
            )
        } returns 0
        every { ServiceProvider.getInstance().loggingService } returns mockk<Logging>(relaxed = true)

        val result = remoteViews.setRemoteViewImage(imageName, 1)

        assertFalse(result)
        verify(exactly = 0) { remoteViews.setImageViewResource(any(), any()) }
        verify(exactly = 0) { remoteViews.setImageViewBitmap(any(), any()) }
        verify(exactly = 1) { remoteViews.setViewVisibility(1, View.GONE) }
    }

    @Test
    fun `setRemoteViewClickAction sets click action when all parameter values are provided`() {
        val testActionUri = "testActionUri"
        val testActionID = "testActionID"
        val testIntentExtras = Bundle()
        testIntentExtras.putString("testKey", "testValue")
        val context = RuntimeEnvironment.getApplication()
        every { remoteViews.setOnClickPendingIntent(any(), any()) } just Runs

        remoteViews.setRemoteViewClickAction(context, trackerActivityClass, 1, testActionUri, testActionID, testIntentExtras)

        val pendingIntentCapture = slot<PendingIntent>()
        verify(exactly = 1) { remoteViews.setOnClickPendingIntent(1, capture(pendingIntentCapture)) }
        val pendingIntent = pendingIntentCapture.captured
        assertNotNull(pendingIntent)
        val shadowPendingIntent = Shadows.shadowOf(pendingIntent)
        assertTrue(shadowPendingIntent.isActivityIntent)
        assertEquals(context, shadowPendingIntent.savedContext)
        assertEquals(
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            shadowPendingIntent.flags
        )

        val intent = shadowPendingIntent.savedIntent
        assertNotNull(intent)
        assertEquals(PushTemplateConstants.NotificationAction.CLICKED, intent.action)
        assertEquals(trackerActivityClass.name, intent.component?.className)
        assertEquals(
            Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP,
            intent.flags
        )
        assertEquals("testValue", intent.getStringExtra("testKey"))
        assertEquals(
            testActionUri,
            intent.getStringExtra(PushTemplateConstants.TrackingKeys.ACTION_URI)
        )
        assertEquals(
            testActionID,
            intent.getStringExtra(PushTemplateConstants.TrackingKeys.ACTION_ID)
        )
    }

    @Test
    fun `setRemoteViewClickAction sets click action when when trackerActivityClass, actionUri, actionID and intentExtras are null`() {
        val context = RuntimeEnvironment.getApplication()

        remoteViews.setRemoteViewClickAction(context, null, 1, null, null, null)

        val pendingIntentCapture = slot<PendingIntent>()
        verify(exactly = 1) { remoteViews.setOnClickPendingIntent(1, capture(pendingIntentCapture)) }
        val pendingIntent = pendingIntentCapture.captured
        assertNotNull(pendingIntent)
        val shadowPendingIntent = Shadows.shadowOf(pendingIntent)
        assertTrue(shadowPendingIntent.isActivityIntent)
        assertEquals(context, shadowPendingIntent.savedContext)
        assertEquals(
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            shadowPendingIntent.flags
        )

        val intent = shadowPendingIntent.savedIntent
        assertNotNull(intent)
        assertEquals(PushTemplateConstants.NotificationAction.CLICKED, intent.action)
        assertEquals(null, intent.component?.className)
        assertEquals(
            Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP,
            intent.flags
        )
        assertEquals(null, intent.extras)
    }
}
