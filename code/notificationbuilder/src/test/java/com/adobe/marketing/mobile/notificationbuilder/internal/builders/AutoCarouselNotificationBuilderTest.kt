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
import android.graphics.Bitmap
import android.widget.RemoteViews
import com.adobe.marketing.mobile.notificationbuilder.internal.PendingIntentUtils
import com.adobe.marketing.mobile.notificationbuilder.internal.PushTemplateImageUtils
import com.adobe.marketing.mobile.notificationbuilder.internal.PushTemplateImageUtils.cacheImages
import com.adobe.marketing.mobile.notificationbuilder.internal.PushTemplateImageUtils.getCachedImage
import com.adobe.marketing.mobile.notificationbuilder.internal.extensions.setRemoteViewClickAction
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.AutoCarouselPushTemplate
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.provideMockedAutoCarousalTemplate
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockkClass
import io.mockk.mockkConstructor
import io.mockk.mockkObject
import io.mockk.verify
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [31])
class AutoCarouselNotificationBuilderTest {

    private lateinit var context: Context
    private lateinit var expandedLayout: RemoteViews
    private lateinit var trackerActivityClass: Class<out Activity>
    private lateinit var broadcastReceiverClass: Class<out BroadcastReceiver>
    private lateinit var autoCarouselPushTemplate: AutoCarouselPushTemplate

    @Before
    fun setUp() {
        context = RuntimeEnvironment.getApplication()
        expandedLayout = mockkClass(RemoteViews::class)
        trackerActivityClass = mockkClass(Activity::class, relaxed = true).javaClass
        broadcastReceiverClass = mockkClass(BroadcastReceiver::class, relaxed = true).javaClass
        autoCarouselPushTemplate = provideMockedAutoCarousalTemplate(false)
        mockkObject(BasicNotificationBuilder)
        mockkObject(PendingIntentUtils)
        mockkObject(PushTemplateImageUtils)
        mockkConstructor(RemoteViews::class)
    }

    @Test
    fun `construct returns NotificationCompat Builder for valid inputs`() {
        val result = AutoCarouselNotificationBuilder.construct(
            context,
            autoCarouselPushTemplate,
            trackerActivityClass,
            broadcastReceiverClass
        )
        assertNotNull(result)
    }

    @Test
    fun `construct returns BasicNotificationBuilder if less than 3 images were downloaded`() {
        every { cacheImages(any()) } answers { 2 }
        AutoCarouselNotificationBuilder.construct(
            context,
            autoCarouselPushTemplate,
            trackerActivityClass,
            broadcastReceiverClass
        )
        verify(exactly = 1) {
            BasicNotificationBuilder.fallbackToBasicNotification(
                context,
                trackerActivityClass,
                broadcastReceiverClass,
                autoCarouselPushTemplate.data
            )
        }
    }

    @Test
    fun `construct returns BasicNotificationBuilder if greater than equal to 3 images were downloaded`() {
        every { cacheImages(any()) } answers { 3 }
        AutoCarouselNotificationBuilder.construct(
            context,
            autoCarouselPushTemplate,
            trackerActivityClass,
            broadcastReceiverClass
        )
        verify(exactly = 0) {
            BasicNotificationBuilder.fallbackToBasicNotification(
                context,
                trackerActivityClass,
                broadcastReceiverClass,
                autoCarouselPushTemplate.data
            )
        }
    }

    @Test
    fun `populateAutoCarouselImages returns list with only cached images with tracker activity`() {
        val cachedItem = mockkClass(Bitmap::class)
        every { getCachedImage(any()) } answers { cachedItem }
        every { anyConstructed<RemoteViews>().setImageViewBitmap(any(), any()) } just Runs
        every { anyConstructed<RemoteViews>().setTextViewText(any(), any()) } just Runs
        every { anyConstructed<RemoteViews>().setRemoteViewClickAction(context, trackerActivityClass, any(), any(), null, any()) } just Runs
        every { expandedLayout.addView(any(), any<RemoteViews>()) } just Runs
        val imagesList = AutoCarouselNotificationBuilder.populateAutoCarouselImages(
            context,
            trackerActivityClass,
            expandedLayout,
            autoCarouselPushTemplate,
            autoCarouselPushTemplate.carouselItems,
            context.packageName
        )
        assertFalse(imagesList.isEmpty())
        verify(exactly = imagesList.size) { getCachedImage(any()) }
        verify(exactly = imagesList.size) { anyConstructed<RemoteViews>().setImageViewBitmap(any(), any()) }
        verify(exactly = imagesList.size) { anyConstructed<RemoteViews>().setRemoteViewClickAction(context, trackerActivityClass, any(), any(), null, any()) }
        verify(exactly = imagesList.size) { anyConstructed<RemoteViews>().setTextViewText(any(), any()) }
    }

    @Test
    fun `populateAutoCarouselImages returns list with only cached images with no tracker activity`() {
        val cachedItem = mockkClass(Bitmap::class)
        every { getCachedImage(any()) } answers { cachedItem }
        every { anyConstructed<RemoteViews>().setImageViewBitmap(any(), any()) } just Runs
        every { anyConstructed<RemoteViews>().setTextViewText(any(), any()) } just Runs
        every { anyConstructed<RemoteViews>().setRemoteViewClickAction(context, trackerActivityClass, any(), any(), null, any()) } just Runs
        every { expandedLayout.addView(any(), any<RemoteViews>()) } just Runs
        val imagesList = AutoCarouselNotificationBuilder.populateAutoCarouselImages(
            context,
            null,
            expandedLayout,
            autoCarouselPushTemplate,
            autoCarouselPushTemplate.carouselItems,
            context.packageName
        )
        assertFalse(imagesList.isEmpty())
        verify(exactly = imagesList.size) { getCachedImage(any()) }
        verify(exactly = imagesList.size) { anyConstructed<RemoteViews>().setImageViewBitmap(any(), any()) }
        verify(exactly = 0) { anyConstructed<RemoteViews>().setRemoteViewClickAction(context, trackerActivityClass, any(), any(), null, any()) }
        verify(exactly = imagesList.size) { anyConstructed<RemoteViews>().setTextViewText(any(), any()) }
    }

    @Test
    fun `populateAutoCarouselImages returns empty list when no images are cached`() {
        every { getCachedImage(any()) } answers { null }
        val imagesList = AutoCarouselNotificationBuilder.populateAutoCarouselImages(
            context,
            trackerActivityClass,
            expandedLayout,
            autoCarouselPushTemplate,
            autoCarouselPushTemplate.carouselItems,
            context.packageName
        )
        assertTrue(imagesList.isEmpty())
        verify(exactly = 0) { anyConstructed<RemoteViews>().setImageViewBitmap(any(), any()) }
        verify(exactly = 0) { anyConstructed<RemoteViews>().setRemoteViewClickAction(context, trackerActivityClass, any(), any(), null, any()) }
        verify(exactly = 0) { anyConstructed<RemoteViews>().setTextViewText(any(), any()) }
    }
}
