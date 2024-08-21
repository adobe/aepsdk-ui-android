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
import android.view.View
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.adobe.marketing.mobile.notificationbuilder.NotificationConstructionFailedException
import com.adobe.marketing.mobile.notificationbuilder.PushTemplateConstants
import com.adobe.marketing.mobile.notificationbuilder.R
import com.adobe.marketing.mobile.notificationbuilder.internal.extensions.setRemoteViewClickAction
import com.adobe.marketing.mobile.notificationbuilder.internal.extensions.setRemoteViewImage
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.MockProductRatingTemplateDataProvider.getMockedDataMapForRatingTemplate
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.ProductRatingPushTemplate
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.provideMockedProductRatingTemplate
import com.adobe.marketing.mobile.notificationbuilder.internal.util.MapData
import com.adobe.marketing.mobile.utils.UiImageUtils
import com.adobe.marketing.mobile.utils.UiImageUtils.cacheImages
import com.adobe.marketing.mobile.utils.UiImageUtils.getCachedImage
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockkClass
import io.mockk.mockkConstructor
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [31])
class ProductRatingNotificationBuilderTest {
    private lateinit var context: Context
    private lateinit var trackerActivityClass: Class<out Activity>
    private lateinit var broadcastReceiverClass: Class<out BroadcastReceiver>

    @Before
    fun setUp() {
        context = RuntimeEnvironment.getApplication()
        trackerActivityClass = DummyActivity::class.java
        broadcastReceiverClass = DummyBroadcastReceiver::class.java
        mockkConstructor(RemoteViews::class)
        mockkStatic(RemoteViews::setRemoteViewImage)
        mockkStatic(RemoteViews::setRemoteViewClickAction)
        mockkObject(UiImageUtils)
    }

    @After
    fun teardown() {
        unmockkAll()
    }

    @Test
    fun `construct should return a NotificationCompat Builder`() {
        every { any<RemoteViews>().setRemoteViewImage(any(), any()) } returns true
        every { anyConstructed<RemoteViews>().setOnClickPendingIntent(any(), any()) } just Runs

        val pushTemplate = provideMockedProductRatingTemplate()
        val notificationBuilder = ProductRatingNotificationBuilder.construct(context, pushTemplate, trackerActivityClass, broadcastReceiverClass)

        // 4 rating icons + 1 setting view visibility for rating confirmation
        verify(exactly = 5) { anyConstructed<RemoteViews>().setOnClickPendingIntent(R.id.rating_icon_image, any()) }
        assertEquals(NotificationCompat.Builder::class.java, notificationBuilder::class.java)
    }

    @Test
    fun `construct should set visibility of expanded layout image view as GONE if no images are downloaded`() {
        every { cacheImages(any()) } answers { 0 }
        every { any<RemoteViews>().setRemoteViewImage(any(), any()) } returns true
        every { anyConstructed<RemoteViews>().setViewVisibility(any(), View.GONE) } just Runs

        val pushTemplate = provideMockedProductRatingTemplate()
        val notificationBuilder = ProductRatingNotificationBuilder.construct(context, pushTemplate, trackerActivityClass, broadcastReceiverClass)

        verify(exactly = 1) { anyConstructed<RemoteViews>().setViewVisibility(R.id.expanded_template_image, View.GONE) }
    }

    @Test
    fun `construct should set image for expanded layout if image is downloaded successfully`() {
        val cachedItem = mockkClass(Bitmap::class)

        every { cacheImages(any()) } answers { 1 }
        every { getCachedImage(any()) } answers { cachedItem }
        every { any<RemoteViews>().setRemoteViewImage(any(), any()) } returns true
        every { anyConstructed<RemoteViews>().setViewVisibility(any(), View.GONE) } just Runs

        val pushTemplate = provideMockedProductRatingTemplate()
        val notificationBuilder = ProductRatingNotificationBuilder.construct(context, pushTemplate, trackerActivityClass, broadcastReceiverClass)

        verify(exactly = 1) { anyConstructed<RemoteViews>().setImageViewBitmap(R.id.expanded_template_image, cachedItem) }
    }

    @Test
    fun `Rating Confirmation action should be hidden if no rating has been selected`() {
        every { any<RemoteViews>().setRemoteViewImage(any(), any()) } returns true
        every { anyConstructed<RemoteViews>().setViewVisibility(any(), any()) } just Runs

        val pushTemplate = provideMockedProductRatingTemplate()
        val notificationBuilder = ProductRatingNotificationBuilder.construct(context, pushTemplate, trackerActivityClass, broadcastReceiverClass)

        verify(exactly = 1) { anyConstructed<RemoteViews>().setViewVisibility(R.id.rating_confirm, View.INVISIBLE) }
    }

    @Test
    fun `Rating confirmation action should be visible when rating is selected`() {
        every { any<RemoteViews>().setRemoteViewImage(any(), any()) } returns true
        every { anyConstructed<RemoteViews>().setViewVisibility(any(), any()) } just Runs

        val dataMap = getMockedDataMapForRatingTemplate()
        dataMap[PushTemplateConstants.IntentKeys.RATING_SELECTED] = "3"
        val pushTemplate = ProductRatingPushTemplate(MapData(dataMap))
        val notificationBuilder = ProductRatingNotificationBuilder.construct(context, pushTemplate, trackerActivityClass, broadcastReceiverClass)

        verify(exactly = 1) { anyConstructed<RemoteViews>().setViewVisibility(R.id.rating_confirm, View.VISIBLE) }
        verify(exactly = 1) { any<RemoteViews>().setRemoteViewClickAction(any(), trackerActivityClass, R.id.rating_confirm, any(), "3", any()) }
    }

    @Test
    fun `construct should throw NotificationConstructionFailedException if image for unselected rating icon is invalid`() {
        every { any<RemoteViews>().setRemoteViewImage("rating_star_outline", any()) } returns false
        every { any<RemoteViews>().setRemoteViewImage("rating_star_filled", any()) } returns true

        val dataMap = getMockedDataMapForRatingTemplate()
        dataMap[PushTemplateConstants.IntentKeys.RATING_SELECTED] = "3"
        val pushTemplate = ProductRatingPushTemplate(MapData(dataMap))

        assertFailsWith(
            exceptionClass = NotificationConstructionFailedException::class,
            message = "Image for unselected rating icon is invalid.",
            block = {
                ProductRatingNotificationBuilder.construct(context, pushTemplate, trackerActivityClass, broadcastReceiverClass)
            }
        )
    }

    @Test
    fun `construct should throw NotificationConstructionFailedException if image for selected rating icon is invalid`() {
        every { any<RemoteViews>().setRemoteViewImage("rating_star_outline", any()) } returns true
        every { any<RemoteViews>().setRemoteViewImage("rating_star_filled", any()) } returns false

        val dataMap = getMockedDataMapForRatingTemplate()
        dataMap[PushTemplateConstants.IntentKeys.RATING_SELECTED] = "3"
        val pushTemplate = ProductRatingPushTemplate(MapData(dataMap))

        assertFailsWith(
            exceptionClass = NotificationConstructionFailedException::class,
            message = "Image for selected rating icon is invalid.",
            block = {
                ProductRatingNotificationBuilder.construct(context, pushTemplate, trackerActivityClass, broadcastReceiverClass)
            }
        )
    }

    @Test
    fun `construct should return a valid NotificationCompat Builder if Broadcast Receiver is not provided`() {
        every { any<RemoteViews>().setRemoteViewImage(any(), any()) } returns true

        val pushTemplate = provideMockedProductRatingTemplate()
        val notificationBuilder = ProductRatingNotificationBuilder.construct(context, pushTemplate, trackerActivityClass, null)

        assertEquals(NotificationCompat.Builder::class.java, notificationBuilder::class.java)
    }

    @Test
    fun `construct should return a NotificationCompat Builder if Tag is provided in Push Template`() {
        every { any<RemoteViews>().setRemoteViewImage(any(), any()) } returns true

        val dataMap = getMockedDataMapForRatingTemplate()
        dataMap[PushTemplateConstants.PushPayloadKeys.TAG] = "tag"
        val pushTemplate = ProductRatingPushTemplate(MapData(dataMap))
        val notificationBuilder = ProductRatingNotificationBuilder.construct(context, pushTemplate, trackerActivityClass, broadcastReceiverClass)

        assertEquals(NotificationCompat.Builder::class.java, notificationBuilder::class.java)
    }
}
