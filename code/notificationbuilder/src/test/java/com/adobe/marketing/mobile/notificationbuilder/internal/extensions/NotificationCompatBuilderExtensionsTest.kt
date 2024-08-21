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
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import androidx.core.app.NotificationCompat
import com.adobe.marketing.mobile.MobileCore
import com.adobe.marketing.mobile.notificationbuilder.PushTemplateConstants
import com.adobe.marketing.mobile.notificationbuilder.internal.builders.DummyActivity
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.BasicPushTemplate
import com.adobe.marketing.mobile.utils.UiImageUtils
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.spyk
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config

// this class also has tests for PendingIntentUtils.createPendingIntentForTrackerActivity
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [31])
class NotificationCompatBuilderExtensionsTest {

    private lateinit var mockContext: Context
    private lateinit var trackerActivityClass: Class<out Activity>
    private lateinit var mockBitmap: Bitmap

    @Before
    fun setup() {
        mockContext = mockk<Context>(relaxed = true)
        mockkStatic(Context::getIconWithResourceName)
        mockkStatic(MobileCore::class)
        mockkObject(UiImageUtils)
        mockBitmap = mockk<Bitmap>(relaxed = true)
        trackerActivityClass = DummyActivity::class.java
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `setSmallIcon uses icon from payload when icon and color are valid`() {
        every { mockContext.getIconWithResourceName("valid_icon") } returns 1234
        every { MobileCore.getSmallIconResourceID() } returns 0

        val spyBuilder = spyk(NotificationCompat.Builder(mockContext, "mockChannelId"))

        spyBuilder.setSmallIcon(mockContext, "valid_icon", "000000")
        verify(exactly = 1) { spyBuilder.setSmallIcon(1234) }
        verify(exactly = 1) { spyBuilder.setColorized(true) }
        assertEquals(-16777216, spyBuilder.color)
    }

    @Test
    fun `setSmallIcon uses icon from mobile core when icon from payload is null`() {
        every { mockContext.getIconWithResourceName("invalid_icon") } returns 0
        every { MobileCore.getSmallIconResourceID() } returns 1234
        every { mockContext.getDefaultAppIcon() } returns 0

        val spyBuilder = spyk(NotificationCompat.Builder(mockContext, "mockChannelId"))

        spyBuilder.setSmallIcon(mockContext, "invalid_icon", null)
        verify(exactly = 1) { spyBuilder.setSmallIcon(1234) }
    }

    @Test
    fun `setSmallIcon uses default app icon when icon from payload is invalid`() {
        every { mockContext.getIconWithResourceName("invalid_icon") } returns 0
        every { MobileCore.getSmallIconResourceID() } returns 0
        every { mockContext.getDefaultAppIcon() } returns 1234

        val spyBuilder = spyk(NotificationCompat.Builder(mockContext, "mockChannelId"))

        spyBuilder.setSmallIcon(mockContext, "invalid_icon", null)
        verify(exactly = 1) { spyBuilder.setSmallIcon(1234) }
    }

    @Test
    fun `setSmallIcon does not set icon and color when all icon sources are invalid`() {
        every { mockContext.getIconWithResourceName("invalid_icon") } returns 0
        every { MobileCore.getSmallIconResourceID() } returns 0
        every { mockContext.getDefaultAppIcon() } returns 0

        val spyBuilder = spyk(NotificationCompat.Builder(mockContext, "mockChannelId"))

        spyBuilder.setSmallIcon(mockContext, "invalid_icon", "000000")
        verify(exactly = 0) { spyBuilder.setSmallIcon(any<Int>()) }
        verify(exactly = 0) { spyBuilder.setColorized(true) }
        assertEquals(0, spyBuilder.color)
    }

    @Test
    fun `setSmallIcon does not set color when icon is valid but color is null`() {
        every { mockContext.getIconWithResourceName("valid_icon") } returns 1234
        every { MobileCore.getSmallIconResourceID() } returns 0
        every { mockContext.getDefaultAppIcon() } returns 0

        val spyBuilder = spyk(NotificationCompat.Builder(mockContext, "mockChannelId"))

        spyBuilder.setSmallIcon(mockContext, "valid_icon", null)
        verify(exactly = 1) { spyBuilder.setSmallIcon(1234) }
        verify(exactly = 0) { spyBuilder.setColorized(true) }
        assertEquals(0, spyBuilder.color)
    }

    @Test
    fun `setSmallIcon does not set color when icon is valid but color is empty`() {
        every { mockContext.getIconWithResourceName("valid_icon") } returns 1234
        every { MobileCore.getSmallIconResourceID() } returns 0
        every { mockContext.getDefaultAppIcon() } returns 0

        val spyBuilder = spyk(NotificationCompat.Builder(mockContext, "mockChannelId"))

        spyBuilder.setSmallIcon(mockContext, "valid_icon", "")
        verify(exactly = 1) { spyBuilder.setSmallIcon(1234) }
        verify(exactly = 0) { spyBuilder.setColorized(true) }
        assertEquals(0, spyBuilder.color)
    }

    @Test
    fun `setSmallIcon does not set color when icon is valid but color is invalid`() {
        every { mockContext.getIconWithResourceName("valid_icon") } returns 1234
        every { MobileCore.getSmallIconResourceID() } returns 0
        every { mockContext.getDefaultAppIcon() } returns 0

        val spyBuilder = spyk(NotificationCompat.Builder(mockContext, "mockChannelId"))

        spyBuilder.setSmallIcon(mockContext, "valid_icon", "invalid_color")
        verify(exactly = 1) { spyBuilder.setSmallIcon(1234) }
        verify(exactly = 0) { spyBuilder.setColorized(true) }
        assertEquals(0, spyBuilder.color)
    }

    @Test
    fun `test sets default sound setSound with null customSound`() {
        val spyBuilder = spyk(NotificationCompat.Builder(mockContext, "mockChannelId"))

        spyBuilder.setSound(mockContext, null)
        verify(exactly = 1) {
            spyBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
        }
    }

    @Test
    fun `test sets default sound setSound with empty customSound`() {
        val spyBuilder = spyk(NotificationCompat.Builder(mockContext, "mockChannelId"))

        spyBuilder.setSound(mockContext, "")
        verify(exactly = 1) {
            spyBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
        }
    }

    @Test
    fun `test provided sound setSound with non null customSound`() {
        val testUri = Uri.parse("test_uri")
        every { mockContext.getSoundUriForResourceName("valid_sound") } returns testUri

        val spyBuilder = spyk(NotificationCompat.Builder(mockContext, "mockChannelId"))

        spyBuilder.setSound(mockContext, "valid_sound")
        verify(exactly = 1) {
            spyBuilder.setSound(testUri)
        }
    }

    @Test
    fun `setLargeIcon with valid imageUrl`() {
        every { UiImageUtils.cacheImages(listOf("valid_image_url")) } returns 1
        every { UiImageUtils.getCachedImage("valid_image_url") } returns mockBitmap

        val spyBuilder = spyk(NotificationCompat.Builder(mockContext, "mockChannelId"))

        spyBuilder.setLargeIcon("valid_image_url", "title", "bodyText")

        verify(exactly = 1) { spyBuilder.setLargeIcon(mockBitmap) }
        verify(exactly = 1) { spyBuilder.setStyle(any<NotificationCompat.BigPictureStyle>()) }
    }

    @Test
    fun `setLargeIcon with imageUrl that cannot be downloaded`() {
        every { UiImageUtils.cacheImages(listOf("invalid_image_url")) } returns 0

        val spyBuilder = spyk(NotificationCompat.Builder(mockContext, "mockChannelId"))

        spyBuilder.setLargeIcon("invalid_image_url", "title", "bodyText")

        verify(exactly = 0) { spyBuilder.setLargeIcon(any()) }
        verify(exactly = 0) { spyBuilder.setStyle(any<NotificationCompat.BigPictureStyle>()) }
    }

    @Test
    fun `setLargeIcon with null imageUrl`() {
        val spyBuilder = spyk(NotificationCompat.Builder(mockContext, "mockChannelId"))

        spyBuilder.setLargeIcon(null, "title", "bodyText")

        verify(exactly = 0) { spyBuilder.setLargeIcon(any()) }
        verify(exactly = 0) { spyBuilder.setStyle(any<NotificationCompat.BigPictureStyle>()) }
    }

    @Test
    fun `setLargeIcon with empty imageUrl`() {
        val spyBuilder = spyk(NotificationCompat.Builder(mockContext, "mockChannelId"))

        spyBuilder.setLargeIcon("", "title", "bodyText")

        verify(exactly = 0) { spyBuilder.setLargeIcon(any()) }
        verify(exactly = 0) { spyBuilder.setStyle(any<NotificationCompat.BigPictureStyle>()) }
    }

    @Test
    fun `setNotificationClickAction sets content intent when actionUri is not null`() {
        val testActionUri = "testActionUri"
        val testIntentExtras = Bundle()
        testIntentExtras.putString("testKey", "testValue")
        every { mockContext.applicationContext } returns mockContext
        val mockBuilder: NotificationCompat.Builder = mockk<NotificationCompat.Builder>(relaxed = true)
        every { mockBuilder.setContentIntent(any()) } returns mockBuilder

        mockBuilder.setNotificationClickAction(mockContext, trackerActivityClass, testActionUri, testIntentExtras)

        val pendingIntentCapture = slot<PendingIntent>()
        verify(exactly = 1) { mockBuilder.setContentIntent(capture(pendingIntentCapture)) }
        val pendingIntent = pendingIntentCapture.captured
        assertNotNull(pendingIntent)
        val shadowPendingIntent = Shadows.shadowOf(pendingIntent)
        assertTrue(shadowPendingIntent.isActivityIntent)
        assertEquals(mockContext, shadowPendingIntent.savedContext)
        assertEquals(PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE, shadowPendingIntent.flags)

        val intent = shadowPendingIntent.savedIntent
        assertNotNull(intent)
        assertEquals(PushTemplateConstants.NotificationAction.CLICKED, intent.action)
        assertEquals(trackerActivityClass.name, intent.component?.className)
        assertEquals(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP, intent.flags)
        assertEquals("testValue", intent.getStringExtra("testKey"))
        assertEquals(testActionUri, intent.getStringExtra(PushTemplateConstants.TrackingKeys.ACTION_URI))
        assertEquals(null, intent.getStringExtra(PushTemplateConstants.TrackingKeys.ACTION_ID))
    }

    @Test
    fun `setNotificationClickAction sets content intent when trackerActivityClass, actionUri and intentExtras are null`() {
        every { mockContext.applicationContext } returns mockContext
        val mockBuilder: NotificationCompat.Builder = mockk<NotificationCompat.Builder>(relaxed = true)
        every { mockBuilder.setContentIntent(any()) } returns mockBuilder

        mockBuilder.setNotificationClickAction(mockContext, null, null, null)

        val pendingIntentCapture = slot<PendingIntent>()
        verify(exactly = 1) { mockBuilder.setContentIntent(capture(pendingIntentCapture)) }
        val pendingIntent = pendingIntentCapture.captured
        assertNotNull(pendingIntent)
        val shadowPendingIntent = Shadows.shadowOf(pendingIntent)
        assertTrue(shadowPendingIntent.isActivityIntent)
        assertEquals(mockContext, shadowPendingIntent.savedContext)
        assertEquals(PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE, shadowPendingIntent.flags)

        val intent = shadowPendingIntent.savedIntent
        assertNotNull(intent)
        assertEquals(PushTemplateConstants.NotificationAction.CLICKED, intent.action)
        assertEquals(null, intent.component?.className)
        assertEquals(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP, intent.flags)
        assertEquals(null, intent.extras)
    }

    @Test
    fun `setNotificationDeleteAction sets delete intent when trackerActivityClass is not null`() {
        every { mockContext.applicationContext } returns mockContext
        val mockBuilder: NotificationCompat.Builder = mockk<NotificationCompat.Builder>(relaxed = true)
        every { mockBuilder.setDeleteIntent(any()) } returns mockBuilder

        mockBuilder.setNotificationDeleteAction(mockContext, trackerActivityClass)

        val pendingIntentCapture = slot<PendingIntent>()
        verify(exactly = 1) { mockBuilder.setDeleteIntent(capture(pendingIntentCapture)) }
        val pendingIntent = pendingIntentCapture.captured
        assertNotNull(pendingIntent)
        val shadowPendingIntent = Shadows.shadowOf(pendingIntent)
        assertTrue(shadowPendingIntent.isActivityIntent)
        assertEquals(mockContext, shadowPendingIntent.savedContext)
        assertEquals(PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE, shadowPendingIntent.flags)

        val intent = shadowPendingIntent.savedIntent
        assertNotNull(intent)
        assertEquals(PushTemplateConstants.NotificationAction.DISMISSED, intent.action)
        assertEquals(trackerActivityClass.name, intent.component?.className)
        assertEquals(Intent.FLAG_ACTIVITY_SINGLE_TOP, intent.flags)
        assertNull(intent.extras)
    }

    @Test
    fun `setNotificationDeleteAction sets delete intent when trackerActivityClass is null`() {
        every { mockContext.applicationContext } returns mockContext
        val mockBuilder: NotificationCompat.Builder = mockk<NotificationCompat.Builder>(relaxed = true)
        every { mockBuilder.setDeleteIntent(any()) } returns mockBuilder

        mockBuilder.setNotificationDeleteAction(mockContext, null)

        val pendingIntentCapture = slot<PendingIntent>()
        verify(exactly = 1) { mockBuilder.setDeleteIntent(capture(pendingIntentCapture)) }
        val pendingIntent = pendingIntentCapture.captured
        assertNotNull(pendingIntent)
        val shadowPendingIntent = Shadows.shadowOf(pendingIntent)
        assertTrue(shadowPendingIntent.isActivityIntent)
        assertEquals(mockContext, shadowPendingIntent.savedContext)
        assertEquals(PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE, shadowPendingIntent.flags)

        val intent = shadowPendingIntent.savedIntent
        assertNotNull(intent)
        assertEquals(PushTemplateConstants.NotificationAction.DISMISSED, intent.action)
        assertNull(intent.component?.className)
        assertEquals(Intent.FLAG_ACTIVITY_SINGLE_TOP, intent.flags)
        assertNull(intent.extras)
    }

    @Test
    fun `addActionButtons adds actions when actionButtons is valid and type is deeplink`() {
        val testIntentExtras = Bundle()
        testIntentExtras.putString("testKey", "testValue")
        every { mockContext.applicationContext } returns mockContext
        val mockBuilder: NotificationCompat.Builder = mockk<NotificationCompat.Builder>(relaxed = true)
        every { mockBuilder.addAction(any(), any(), any()) } returns mockBuilder

        mockBuilder.addActionButtons(
            mockContext, trackerActivityClass,
            listOf(
                BasicPushTemplate.ActionButton("testLabel", "testLink", PushTemplateConstants.ActionType.DEEPLINK.name)
            ),
            testIntentExtras
        )

        val iconCapture = slot<Int>()
        val labelCapture = slot<CharSequence>()
        val pendingIntentCapture = slot<PendingIntent>()
        verify(exactly = 1) { mockBuilder.addAction(capture(iconCapture), capture(labelCapture), capture(pendingIntentCapture)) }
        val pendingIntent = pendingIntentCapture.captured
        assertNotNull(pendingIntent)
        val shadowPendingIntent = Shadows.shadowOf(pendingIntent)
        assertTrue(shadowPendingIntent.isActivityIntent)
        assertEquals(mockContext, shadowPendingIntent.savedContext)
        assertEquals(PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE, shadowPendingIntent.flags)

        val intent = shadowPendingIntent.savedIntent
        assertNotNull(intent)
        assertEquals(PushTemplateConstants.NotificationAction.CLICKED, intent.action)
        assertEquals(trackerActivityClass.name, intent.component?.className)
        assertEquals(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP, intent.flags)
        assertEquals("testValue", intent.getStringExtra("testKey"))
        assertEquals("testLink", intent.getStringExtra(PushTemplateConstants.TrackingKeys.ACTION_URI))
        assertEquals("testLabel", intent.getStringExtra(PushTemplateConstants.TrackingKeys.ACTION_ID))
    }

    @Test
    fun `addActionButtons adds actions when actionButtons is valid and type is weburl`() {
        val testIntentExtras = Bundle()
        testIntentExtras.putString("testKey", "testValue")
        every { mockContext.applicationContext } returns mockContext
        val mockBuilder: NotificationCompat.Builder = mockk<NotificationCompat.Builder>(relaxed = true)
        every { mockBuilder.addAction(any(), any(), any()) } returns mockBuilder

        mockBuilder.addActionButtons(
            mockContext, trackerActivityClass,
            listOf(
                BasicPushTemplate.ActionButton("testLabel", "testLink", PushTemplateConstants.ActionType.WEBURL.name)
            ),
            testIntentExtras
        )

        val iconCapture = slot<Int>()
        val labelCapture = slot<CharSequence>()
        val pendingIntentCapture = slot<PendingIntent>()
        verify(exactly = 1) { mockBuilder.addAction(capture(iconCapture), capture(labelCapture), capture(pendingIntentCapture)) }
        val pendingIntent = pendingIntentCapture.captured
        assertNotNull(pendingIntent)
        val shadowPendingIntent = Shadows.shadowOf(pendingIntent)
        assertTrue(shadowPendingIntent.isActivityIntent)
        assertEquals(mockContext, shadowPendingIntent.savedContext)
        assertEquals(PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE, shadowPendingIntent.flags)

        val intent = shadowPendingIntent.savedIntent
        assertNotNull(intent)
        assertEquals(PushTemplateConstants.NotificationAction.CLICKED, intent.action)
        assertEquals(trackerActivityClass.name, intent.component?.className)
        assertEquals(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP, intent.flags)
        assertEquals("testValue", intent.getStringExtra("testKey"))
        assertEquals("testLink", intent.getStringExtra(PushTemplateConstants.TrackingKeys.ACTION_URI))
        assertEquals("testLabel", intent.getStringExtra(PushTemplateConstants.TrackingKeys.ACTION_ID))
    }

    @Test
    fun `addActionButtons adds actions when actionButtons is valid and type is openapp`() {
        val testIntentExtras = Bundle()
        testIntentExtras.putString("testKey", "testValue")
        every { mockContext.applicationContext } returns mockContext
        val mockBuilder: NotificationCompat.Builder = mockk<NotificationCompat.Builder>(relaxed = true)
        every { mockBuilder.addAction(any(), any(), any()) } returns mockBuilder

        mockBuilder.addActionButtons(
            mockContext, trackerActivityClass,
            listOf(
                BasicPushTemplate.ActionButton("testLabel", null, PushTemplateConstants.ActionType.OPENAPP.name)
            ),
            testIntentExtras
        )

        val iconCapture = slot<Int>()
        val labelCapture = slot<CharSequence>()
        val pendingIntentCapture = slot<PendingIntent>()
        verify(exactly = 1) { mockBuilder.addAction(capture(iconCapture), capture(labelCapture), capture(pendingIntentCapture)) }
        val pendingIntent = pendingIntentCapture.captured
        assertNotNull(pendingIntent)
        val shadowPendingIntent = Shadows.shadowOf(pendingIntent)
        assertTrue(shadowPendingIntent.isActivityIntent)
        assertEquals(mockContext, shadowPendingIntent.savedContext)
        assertEquals(PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE, shadowPendingIntent.flags)

        val intent = shadowPendingIntent.savedIntent
        assertNotNull(intent)
        assertEquals(PushTemplateConstants.NotificationAction.CLICKED, intent.action)
        assertEquals(trackerActivityClass.name, intent.component?.className)
        assertEquals(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP, intent.flags)
        assertEquals("testValue", intent.getStringExtra("testKey"))
        assertEquals(null, intent.getStringExtra(PushTemplateConstants.TrackingKeys.ACTION_URI))
        assertEquals("testLabel", intent.getStringExtra(PushTemplateConstants.TrackingKeys.ACTION_ID))
    }

    @Test
    fun `addActionButtons adds actions when actionButtons is valid and type is not valid`() {
        val testIntentExtras = Bundle()
        testIntentExtras.putString("testKey", "testValue")
        every { mockContext.applicationContext } returns mockContext
        val mockBuilder: NotificationCompat.Builder = mockk<NotificationCompat.Builder>(relaxed = true)
        every { mockBuilder.addAction(any(), any(), any()) } returns mockBuilder

        mockBuilder.addActionButtons(
            mockContext, trackerActivityClass,
            listOf(
                BasicPushTemplate.ActionButton("testLabel", "testLink", PushTemplateConstants.ActionType.NONE.name)
            ),
            testIntentExtras
        )

        val iconCapture = slot<Int>()
        val labelCapture = slot<CharSequence>()
        val pendingIntentCapture = slot<PendingIntent>()
        verify(exactly = 1) { mockBuilder.addAction(capture(iconCapture), capture(labelCapture), capture(pendingIntentCapture)) }
        val pendingIntent = pendingIntentCapture.captured
        assertNotNull(pendingIntent)
        val shadowPendingIntent = Shadows.shadowOf(pendingIntent)
        assertTrue(shadowPendingIntent.isActivityIntent)
        assertEquals(mockContext, shadowPendingIntent.savedContext)
        assertEquals(PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE, shadowPendingIntent.flags)

        val intent = shadowPendingIntent.savedIntent
        assertNotNull(intent)
        assertEquals(PushTemplateConstants.NotificationAction.CLICKED, intent.action)
        assertEquals(trackerActivityClass.name, intent.component?.className)
        assertEquals(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP, intent.flags)
        assertEquals("testValue", intent.getStringExtra("testKey"))
        assertEquals(null, intent.getStringExtra(PushTemplateConstants.TrackingKeys.ACTION_URI))
        assertEquals("testLabel", intent.getStringExtra(PushTemplateConstants.TrackingKeys.ACTION_ID))
    }

    @Test
    fun `addActionButtons adds actions when actionButtons is null`() {
        every { mockContext.applicationContext } returns mockContext
        val mockBuilder: NotificationCompat.Builder = mockk<NotificationCompat.Builder>(relaxed = true)
        every { mockBuilder.addAction(any(), any(), any()) } returns mockBuilder

        mockBuilder.addActionButtons(
            mockContext,
            trackerActivityClass,
            null,
            null
        )

        verify(exactly = 0) { mockBuilder.addAction(any(), any(), any()) }
    }

    @Test
    fun `addActionButtons adds actions when actionButtons is empty`() {
        every { mockContext.applicationContext } returns mockContext
        val mockBuilder: NotificationCompat.Builder = mockk<NotificationCompat.Builder>(relaxed = true)
        every { mockBuilder.addAction(any(), any(), any()) } returns mockBuilder

        mockBuilder.addActionButtons(
            mockContext,
            trackerActivityClass,
            emptyList(),
            null
        )

        verify(exactly = 0) { mockBuilder.addAction(any(), any(), any()) }
    }
}
