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
import android.content.Intent
import android.graphics.Bitmap
import android.widget.RemoteViews
import com.adobe.marketing.mobile.notificationbuilder.PushTemplateConstants
import com.adobe.marketing.mobile.notificationbuilder.internal.PendingIntentUtils
import com.adobe.marketing.mobile.notificationbuilder.internal.PushTemplateImageUtils
import com.adobe.marketing.mobile.notificationbuilder.internal.PushTemplateImageUtils.cacheImages
import com.adobe.marketing.mobile.notificationbuilder.internal.PushTemplateImageUtils.getAssetCacheLocation
import com.adobe.marketing.mobile.notificationbuilder.internal.PushTemplateImageUtils.getCachedImage
import com.adobe.marketing.mobile.notificationbuilder.internal.builders.ManualCarouselNotificationBuilder.downloadCarouselItems
import com.adobe.marketing.mobile.notificationbuilder.internal.builders.ManualCarouselNotificationBuilder.getCarouselIndices
import com.adobe.marketing.mobile.notificationbuilder.internal.builders.ManualCarouselNotificationBuilder.populateFilmstripCarouselImages
import com.adobe.marketing.mobile.notificationbuilder.internal.builders.ManualCarouselNotificationBuilder.populateManualCarouselImages
import com.adobe.marketing.mobile.notificationbuilder.internal.builders.ManualCarouselNotificationBuilder.setupNavigationButtons
import com.adobe.marketing.mobile.notificationbuilder.internal.extensions.setRemoteViewClickAction
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.CarouselPushTemplate
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.ManualCarouselPushTemplate
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.MockCarousalTemplateDataProvider
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.provideMockedManualCarousalTemplate
import com.adobe.marketing.mobile.notificationbuilder.internal.util.IntentData
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockkClass
import io.mockk.mockkConstructor
import io.mockk.mockkObject
import io.mockk.runs
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.After
import org.junit.Assert.assertEquals
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
class ManualCarouselNotificationBuilderTest {

    private lateinit var context: Context
    private lateinit var pushTemplate: ManualCarouselPushTemplate
    private lateinit var expandedLayout: RemoteViews
    private lateinit var trackerActivityClass: Class<out Activity>
    private lateinit var broadcastReceiverClass: Class<out BroadcastReceiver>

    @Before
    fun setUp() {
        context = RuntimeEnvironment.getApplication()
        pushTemplate = provideMockedManualCarousalTemplate(false)
        trackerActivityClass = mockkClass(Activity::class, relaxed = true).javaClass
        broadcastReceiverClass = mockkClass(BroadcastReceiver::class, relaxed = true).javaClass
        mockkObject(PushTemplateImageUtils)
        mockkObject(BasicNotificationBuilder)
        mockkObject(PendingIntentUtils)
        mockkConstructor(RemoteViews::class)
        expandedLayout = mockkClass(RemoteViews::class)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `construct returns NotificationCompat Builder for valid inputs`() {
        val result = ManualCarouselNotificationBuilder.construct(
            context,
            pushTemplate,
            trackerActivityClass,
            broadcastReceiverClass
        )
        assertNotNull(result)
    }

    @Test
    fun `construct returns BasicNotificationBuilder if download image count is less than 3`() {
        every { cacheImages(any()) } answers { 2 }
        ManualCarouselNotificationBuilder.construct(
            context,
            pushTemplate,
            trackerActivityClass,
            broadcastReceiverClass
        )
        verify {
            BasicNotificationBuilder.fallbackToBasicNotification(
                context,
                trackerActivityClass,
                broadcastReceiverClass,
                pushTemplate.data
            )
        }
    }

    @Test
    fun `construct returns ManualCarouselNotificationBuilder if download image count is equal to 3`() {
        every { cacheImages(any()) } answers { 3 }
        ManualCarouselNotificationBuilder.construct(
            context,
            pushTemplate,
            trackerActivityClass,
            broadcastReceiverClass
        )
        verify(exactly = 0) {
            BasicNotificationBuilder.fallbackToBasicNotification(
                context,
                trackerActivityClass,
                broadcastReceiverClass,
                pushTemplate.data
            )
        }
    }

    @Test
    fun `construct returns ManualCarouselNotificationBuilder if download image count is greater than 3`() {
        every { cacheImages(any()) } answers { 5 }
        ManualCarouselNotificationBuilder.construct(
            context,
            pushTemplate,
            trackerActivityClass,
            broadcastReceiverClass
        )
        verify(exactly = 0) {
            BasicNotificationBuilder.fallbackToBasicNotification(
                context,
                trackerActivityClass,
                broadcastReceiverClass,
                pushTemplate.data
            )
        }
    }

    @Test
    fun `construct ManualCarouselNotificationBuilder shouldn't be called if downloadCarouselItems failed to download some of the images`() {
        var callCount = 0
        every { cacheImages(any()) } answers { 5 }
        every { getCachedImage(any()) } answers { if (callCount++ % 2 == 0) mockkClass(Bitmap::class) else null }
        verify(exactly = 0) {
            ManualCarouselNotificationBuilder.construct(
                context,
                pushTemplate,
                trackerActivityClass,
                broadcastReceiverClass
            )
        }
    }

    @Test
    fun `test downloadCarouselItems returns non empty list when image bitmap is present in the cache`() {
        every { getCachedImage(any()) } answers { mockkClass(Bitmap::class) }
        val imagesList = downloadCarouselItems(pushTemplate.carouselItems)
        assertTrue(imagesList == pushTemplate.carouselItems)
    }

    @Test
    fun `test getCarouselIndices with left click intent action`() {
        val mockBundle = MockCarousalTemplateDataProvider.getMockedBundleWithManualCarouselData()
        val data =
            IntentData(mockBundle, PushTemplateConstants.IntentActions.MANUAL_CAROUSEL_LEFT_CLICKED)
        val mcPushTemplate = CarouselPushTemplate(data) as ManualCarouselPushTemplate
        val result = getCarouselIndices(mcPushTemplate, 3)
        assertEquals(Triple(1, 2, 0), result)
    }

    @Test
    fun `test getCarouselIndices with filmstrip left click intent action`() {
        val mockBundle = MockCarousalTemplateDataProvider.getMockedBundleWithManualCarouselData()
        val data =
            IntentData(mockBundle, PushTemplateConstants.IntentActions.FILMSTRIP_LEFT_CLICKED)
        val mcPushTemplate = CarouselPushTemplate(data) as ManualCarouselPushTemplate
        val result = getCarouselIndices(mcPushTemplate, 3)
        assertEquals(Triple(1, 2, 0), result)
    }

    @Test
    fun `test getCarouselIndices with no intent action and filmstrip layout`() {
        val mockBundle = MockCarousalTemplateDataProvider.getMockedBundleWithManualCarouselData()
        val data =
            IntentData(mockBundle, PushTemplateConstants.DefaultValues.FILMSTRIP_CAROUSEL_MODE)
        val mcPushTemplate = CarouselPushTemplate(data) as ManualCarouselPushTemplate
        val result = getCarouselIndices(mcPushTemplate, 3)
        assertEquals(
            Triple(
                PushTemplateConstants.DefaultValues.FILMSTRIP_CAROUSEL_CENTER_INDEX - 1,
                PushTemplateConstants.DefaultValues.FILMSTRIP_CAROUSEL_CENTER_INDEX,
                PushTemplateConstants.DefaultValues.FILMSTRIP_CAROUSEL_CENTER_INDEX + 1
            ),
            result
        )
    }

    @Test
    fun `test getCarouselIndices with no intent action and manual layout`() {
        val result = getCarouselIndices(pushTemplate, pushTemplate.carouselItems.size)
        assertEquals(
            Triple(
                pushTemplate.carouselItems.size - 1,
                PushTemplateConstants.DefaultValues.MANUAL_CAROUSEL_START_INDEX,
                PushTemplateConstants.DefaultValues.MANUAL_CAROUSEL_START_INDEX + 1
            ),
            result
        )
    }

    @Test
    fun `test to verify notification with action name MANUAL_CAROUSEL_LEFT_CLICKED is having correct center image index`() {
        every { cacheImages(any()) } answers { 5 }
        every { getCachedImage(any()) } answers { mockkClass(Bitmap::class) }
        val mockBundle = MockCarousalTemplateDataProvider.getMockedBundleWithManualCarouselData()
        val data = IntentData(mockBundle, PushTemplateConstants.IntentActions.MANUAL_CAROUSEL_LEFT_CLICKED)
        val mcPushTemplate = CarouselPushTemplate(data) as ManualCarouselPushTemplate
        ManualCarouselNotificationBuilder.construct(
            context,
            mcPushTemplate,
            trackerActivityClass,
            broadcastReceiverClass
        )
        assertEquals(4, mcPushTemplate.centerImageIndex)
    }

    @Test
    fun `test to verify notification with action name MANUAL_CAROUSEL_RIGHT_CLICKED is having correct center image index`() {
        every { cacheImages(any()) } answers { 5 }
        every { getCachedImage(any()) } answers { mockkClass(Bitmap::class) }
        val mockBundle = MockCarousalTemplateDataProvider.getMockedBundleWithManualCarouselData()
        val data = IntentData(mockBundle, PushTemplateConstants.IntentActions.MANUAL_CAROUSEL_RIGHT_CLICKED)
        val mcPushTemplate = CarouselPushTemplate(data) as ManualCarouselPushTemplate
        ManualCarouselNotificationBuilder.construct(
            context,
            mcPushTemplate,
            trackerActivityClass,
            broadcastReceiverClass
        )
        assertEquals(1, mcPushTemplate.centerImageIndex)
    }

    @Test
    fun `test to verify notification with action name FILMSTRIP_LEFT_CLICKED is having correct center image index`() {
        every { cacheImages(any()) } answers { 5 }
        every { getCachedImage(any()) } answers { mockkClass(Bitmap::class) }
        val mockBundle = MockCarousalTemplateDataProvider.getMockedBundleWithManualCarouselData()
        mockBundle.putString(PushTemplateConstants.PushPayloadKeys.CAROUSEL_LAYOUT, PushTemplateConstants.DefaultValues.FILMSTRIP_CAROUSEL_MODE)
        val data = IntentData(mockBundle, PushTemplateConstants.IntentActions.FILMSTRIP_LEFT_CLICKED)
        val mcPushTemplate = CarouselPushTemplate(data) as ManualCarouselPushTemplate
        mcPushTemplate.centerImageIndex
        ManualCarouselNotificationBuilder.construct(
            context,
            mcPushTemplate,
            trackerActivityClass,
            broadcastReceiverClass
        )
        assertEquals(0, mcPushTemplate.centerImageIndex)
    }

    @Test
    fun `test to verify notification with action name FILMSTRIP_RIGHT_CLICKED is having correct center image index`() {
        every { cacheImages(any()) } answers { 5 }
        every { getCachedImage(any()) } answers { mockkClass(Bitmap::class) }
        val mockBundle = MockCarousalTemplateDataProvider.getMockedBundleWithManualCarouselData()
        mockBundle.putString(PushTemplateConstants.PushPayloadKeys.CAROUSEL_LAYOUT, PushTemplateConstants.DefaultValues.FILMSTRIP_CAROUSEL_MODE)
        val data = IntentData(mockBundle, PushTemplateConstants.IntentActions.FILMSTRIP_RIGHT_CLICKED)
        val mcPushTemplate = CarouselPushTemplate(data) as ManualCarouselPushTemplate
        mcPushTemplate.centerImageIndex
        ManualCarouselNotificationBuilder.construct(
            context,
            mcPushTemplate,
            trackerActivityClass,
            broadcastReceiverClass
        )
        assertEquals(2, mcPushTemplate.centerImageIndex)
    }

    @Test
    fun `test populateManualCarouselImages with valid images`() {
        val packageName = context.packageName
        val centerIndex = 1
        every { getCachedImage(any()) } answers { mockkClass(Bitmap::class) }
        every { anyConstructed<RemoteViews>().setTextViewText(any(), any()) } just Runs
        every { anyConstructed<RemoteViews>().setImageViewBitmap(any(), any()) } just Runs
        every {
            anyConstructed<RemoteViews>().setRemoteViewClickAction(
                context,
                trackerActivityClass,
                any(),
                any(),
                null,
                any()
            )
        } just Runs
        every { expandedLayout.addView(any(), any<RemoteViews>()) } just Runs
        every { expandedLayout.setDisplayedChild(any(), any()) } just Runs
        populateManualCarouselImages(
            context,
            pushTemplate.carouselItems,
            packageName,
            centerIndex,
            pushTemplate,
            trackerActivityClass,
            expandedLayout
        )
        val carouselItemsCount = pushTemplate.carouselItems.size
        verify(exactly = carouselItemsCount) {
            anyConstructed<RemoteViews>().setTextViewText(
                any(),
                any()
            )
        }
        verify(exactly = carouselItemsCount) {
            anyConstructed<RemoteViews>().setImageViewBitmap(
                any(),
                any()
            )
        }
        verify(exactly = carouselItemsCount) {
            anyConstructed<RemoteViews>().setRemoteViewClickAction(
                context,
                trackerActivityClass,
                any(),
                any(),
                null,
                any()
            )
        }
        verify(exactly = carouselItemsCount) { expandedLayout.addView(any(), any<RemoteViews>()) }
        verify(exactly = carouselItemsCount) {
            expandedLayout.setDisplayedChild(
                any(),
                centerIndex
            )
        }
    }

    @Test
    fun `test populateManualCarouselImages with null images`() {
        val packageName = context.packageName
        val centerIndex = 1
        populateManualCarouselImages(
            context,
            pushTemplate.carouselItems,
            packageName,
            centerIndex,
            pushTemplate,
            trackerActivityClass,
            expandedLayout
        )

        verify(exactly = 0) { anyConstructed<RemoteViews>().setTextViewText(any(), any()) }
        verify(exactly = 0) { anyConstructed<RemoteViews>().setImageViewBitmap(any(), any()) }
        verify(exactly = 0) {
            anyConstructed<RemoteViews>().setRemoteViewClickAction(
                context,
                trackerActivityClass,
                any(),
                any(),
                null,
                any()
            )
        }
        verify(exactly = 0) { expandedLayout.addView(any(), any<RemoteViews>()) }
        verify(exactly = 0) { expandedLayout.setDisplayedChild(any(), centerIndex) }
    }

    @Test
    fun `test populateFilmstripCarouselImages with valid images`() {
        val mockBitmap = mockkClass(Bitmap::class)
        val newIndices = Triple(0, 1, 2)
        every { getAssetCacheLocation() } answers { "assetCacheLocation" }
        every { getCachedImage(any()) } answers { mockBitmap }
        every { expandedLayout.setTextViewText(any(), any()) } returns Unit
        every { expandedLayout.setImageViewBitmap(any(), any()) } just Runs
        every {
            expandedLayout.setRemoteViewClickAction(
                context,
                trackerActivityClass,
                any(),
                any(),
                null,
                any()
            )
        } returns Unit
        populateFilmstripCarouselImages(
            context,
            pushTemplate.carouselItems,
            newIndices,
            pushTemplate,
            trackerActivityClass,
            expandedLayout
        )

        verify(exactly = 1) { expandedLayout.setTextViewText(any(), any()) }
        verify(exactly = 3) { expandedLayout.setImageViewBitmap(any(), any()) }
        verify(exactly = 1) {
            expandedLayout.setRemoteViewClickAction(
                context,
                trackerActivityClass,
                any(),
                any(),
                null,
                any()
            )
        }
    }

    @Test
    fun `test populateFilmstripCarouselImages with null asset cached location`() {
        val mockBitmap = mockkClass(Bitmap::class)
        val newIndices = Triple(0, 1, 2)
        every { getAssetCacheLocation() } answers { null }
        every { getCachedImage(any()) } answers { mockBitmap }
        every { expandedLayout.setTextViewText(any(), any()) } returns Unit
        every { expandedLayout.setImageViewBitmap(any(), mockBitmap) } returns Unit
        every {
            expandedLayout.setRemoteViewClickAction(
                context,
                trackerActivityClass,
                any(),
                any(),
                null,
                any()
            )
        } returns Unit
        populateFilmstripCarouselImages(
            context,
            pushTemplate.carouselItems,
            newIndices,
            pushTemplate,
            trackerActivityClass,
            expandedLayout
        )
        verify(exactly = 0) {
            expandedLayout.setRemoteViewClickAction(
                context,
                trackerActivityClass,
                any(),
                any(),
                null,
                any()
            )
        }
    }

    @Test
    fun `test setupNavigationButtons for DEFAULT_MANUAL_CAROUSEL_MODE`() {
        val mockBundle = MockCarousalTemplateDataProvider.getMockedBundleWithManualCarouselData()
        mockBundle.putString(PushTemplateConstants.PushPayloadKeys.CAROUSEL_LAYOUT, PushTemplateConstants.DefaultValues.DEFAULT_MANUAL_CAROUSEL_MODE)
        val data = IntentData(mockBundle, PushTemplateConstants.IntentActions.MANUAL_CAROUSEL_RIGHT_CLICKED)
        val mcPushTemplate = CarouselPushTemplate(data) as ManualCarouselPushTemplate

        mockkConstructor(AEPPushNotificationBuilder::class)

        every {
            anyConstructed<AEPPushNotificationBuilder>().createIntent(any(), mcPushTemplate)
        } returns Intent()

        every { expandedLayout.setOnClickPendingIntent(any(), any()) } just runs

        setupNavigationButtons(context, mcPushTemplate, broadcastReceiverClass, expandedLayout, "channelId")

        verify(exactly = 2) { expandedLayout.setOnClickPendingIntent(any(), any()) }
    }
}
