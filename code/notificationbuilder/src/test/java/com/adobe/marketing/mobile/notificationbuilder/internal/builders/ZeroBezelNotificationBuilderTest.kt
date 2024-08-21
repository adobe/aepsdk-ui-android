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
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.RemoteViews
import com.adobe.marketing.mobile.notificationbuilder.PushTemplateConstants
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.MockAEPPushTemplateDataProvider
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.ZeroBezelPushTemplate
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.replaceValueInMap
import com.adobe.marketing.mobile.notificationbuilder.internal.util.IntentData
import com.adobe.marketing.mobile.notificationbuilder.internal.util.MapData
import com.adobe.marketing.mobile.utils.PushTemplateImageUtils
import com.adobe.marketing.mobile.utils.PushTemplateImageUtils.cacheImages
import com.adobe.marketing.mobile.utils.PushTemplateImageUtils.getCachedImage
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkObject
import io.mockk.unmockkAll
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [31])
class ZeroBezelNotificationBuilderTest {

    private lateinit var context: Context
    private lateinit var dataMap: MutableMap<String, String>
    private lateinit var mockBitmap: Bitmap
    private lateinit var trackerActivityClass: Class<out Activity>
    private lateinit var mockBundle: Bundle

    @Before
    fun setup() {
        context = RuntimeEnvironment.getApplication()
        dataMap = MockAEPPushTemplateDataProvider.getMockedAEPDataMapWithAllKeys()
        mockkObject(PushTemplateImageUtils)
        mockkConstructor(RemoteViews::class)
        mockBitmap = mockk<Bitmap>(relaxed = true)
        trackerActivityClass = DummyActivity::class.java
        mockBundle = MockAEPPushTemplateDataProvider.getMockedAEPBundleWithAllKeys()
        every { getCachedImage(any()) } answers { mockBitmap }
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `verify construct with image downloaded and collapsedStyle is img`() {
        val pushTemplate = ZeroBezelPushTemplate(MapData(dataMap))
        every { cacheImages(any()) } answers { 2 }
        every { anyConstructed<RemoteViews>().setImageViewBitmap(any(), mockBitmap) } just Runs

        val notification = ZeroBezelNotificationBuilder.construct(
            context,
            pushTemplate,
            trackerActivityClass
        ).build()

        assertEquals(pushTemplate.channelId, notification.channelId)
        verifyNotificationDataFields(notification, pushTemplate)
        verify { cacheImages(listOf(pushTemplate.imageUrl)) }
        verify { getCachedImage(pushTemplate.imageUrl) }
        verify { anyConstructed<RemoteViews>().setImageViewBitmap(any(), mockBitmap) }
    }

    @Test
    fun `verify construct with image downloaded and collapsedStyle is txt`() {
        dataMap.replaceValueInMap(
            Pair(
                PushTemplateConstants.PushPayloadKeys.ZERO_BEZEL_COLLAPSED_STYLE,
                "txt"
            )
        )
        val pushTemplate = ZeroBezelPushTemplate(MapData(dataMap))

        every { cacheImages(any()) } answers { 2 }
        every { anyConstructed<RemoteViews>().setImageViewBitmap(any(), mockBitmap) } just Runs
        every { anyConstructed<RemoteViews>().setViewVisibility(any(), View.GONE) } just Runs

        val notification = ZeroBezelNotificationBuilder.construct(
            context,
            pushTemplate,
            trackerActivityClass
        ).build()

        assertEquals(pushTemplate.channelId, notification.channelId)
        verifyNotificationDataFields(notification, pushTemplate)
        verify { cacheImages(listOf(pushTemplate.imageUrl)) }
        verify { getCachedImage(pushTemplate.imageUrl) }
        verify { anyConstructed<RemoteViews>().setImageViewBitmap(any(), mockBitmap) }
        verify { anyConstructed<RemoteViews>().setViewVisibility(any(), View.GONE) }
    }

    @Test
    fun `verify construct with no image downloaded`() {
        val pushTemplate = ZeroBezelPushTemplate(MapData(dataMap))

        every { cacheImages(listOf(pushTemplate.imageUrl)) } returns 0
        every { anyConstructed<RemoteViews>().setViewVisibility(any(), View.GONE) } just Runs

        val notification = ZeroBezelNotificationBuilder.construct(
            context,
            pushTemplate,
            trackerActivityClass
        ).build()

        assertEquals(pushTemplate.channelId, notification.channelId)
        verifyNotificationDataFields(notification, pushTemplate)
        verify { cacheImages(listOf(pushTemplate.imageUrl)) }
        verify { anyConstructed<RemoteViews>().setViewVisibility(any(), View.GONE) }
    }

    @Test
    fun `verify construct with image downloaded and collapsedStyle is img for IntentData`() {
        val pushTemplate = ZeroBezelPushTemplate(IntentData(mockBundle, null))
        every { cacheImages(any()) } answers { 2 }
        every { anyConstructed<RemoteViews>().setImageViewBitmap(any(), mockBitmap) } just Runs

        val notification = ZeroBezelNotificationBuilder.construct(
            context,
            pushTemplate,
            trackerActivityClass
        ).build()

        assertEquals(
            PushTemplateConstants.DefaultValues.SILENT_NOTIFICATION_CHANNEL_ID,
            notification.channelId
        )
        verifyNotificationDataFields(notification, pushTemplate)
        verify { cacheImages(listOf(pushTemplate.imageUrl)) }
        verify { getCachedImage(pushTemplate.imageUrl) }
        verify { anyConstructed<RemoteViews>().setImageViewBitmap(any(), mockBitmap) }
    }

    @Test
    fun `verify construct with image downloaded and collapsedStyle is txt for IntentData`() {
        mockBundle.putString(
            PushTemplateConstants.PushPayloadKeys.ZERO_BEZEL_COLLAPSED_STYLE,
            "txt"
        )

        val pushTemplate = ZeroBezelPushTemplate(IntentData(mockBundle, null))
        every { cacheImages(any()) } answers { 2 }
        every { anyConstructed<RemoteViews>().setImageViewBitmap(any(), mockBitmap) } just Runs
        every { anyConstructed<RemoteViews>().setViewVisibility(any(), View.GONE) } just Runs

        val notification = ZeroBezelNotificationBuilder.construct(
            context,
            pushTemplate,
            trackerActivityClass
        ).build()

        assertEquals(
            PushTemplateConstants.DefaultValues.SILENT_NOTIFICATION_CHANNEL_ID,
            notification.channelId
        )
        verifyNotificationDataFields(notification, pushTemplate)
        verify { cacheImages(listOf(pushTemplate.imageUrl)) }
        verify { getCachedImage(pushTemplate.imageUrl) }
        verify { anyConstructed<RemoteViews>().setImageViewBitmap(any(), mockBitmap) }
        verify { anyConstructed<RemoteViews>().setViewVisibility(any(), View.GONE) }
    }

    @Test
    fun `verify construct with no image downloaded for IntentData`() {
        val pushTemplate = ZeroBezelPushTemplate(IntentData(mockBundle, null))
        every { cacheImages(listOf(pushTemplate.imageUrl)) } returns 0
        every { anyConstructed<RemoteViews>().setViewVisibility(any(), View.GONE) } just Runs

        val notification = ZeroBezelNotificationBuilder.construct(
            context,
            pushTemplate,
            trackerActivityClass
        ).build()

        assertEquals(
            PushTemplateConstants.DefaultValues.SILENT_NOTIFICATION_CHANNEL_ID,
            notification.channelId
        )
        verifyNotificationDataFields(notification, pushTemplate)
        verify { cacheImages(listOf(pushTemplate.imageUrl)) }
        verify { anyConstructed<RemoteViews>().setViewVisibility(any(), View.GONE) }
    }

    private fun verifyNotificationDataFields(
        notification: Notification,
        pushTemplate: ZeroBezelPushTemplate
    ) {
        val pendingIntent = notification.contentIntent
        val shadowPendingIntent = Shadows.shadowOf(pendingIntent)
        val intent = shadowPendingIntent.savedIntent

        assertEquals(Notification::class.java, notification.javaClass)
        assertEquals(pushTemplate.ticker, notification.tickerText)
        assertEquals(pushTemplate.badgeCount, notification.number)
        assertEquals(pushTemplate.visibility.value, notification.visibility)
        assertNotNull(notification.smallIcon)
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
}
