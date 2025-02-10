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

package com.adobe.marketing.mobile.notificationbuilder

import android.app.Activity
import android.app.AlarmManager
import android.app.Application
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import com.adobe.marketing.mobile.notificationbuilder.internal.PendingIntentUtils
import com.adobe.marketing.mobile.notificationbuilder.internal.PushTemplateImageUtils
import com.adobe.marketing.mobile.notificationbuilder.internal.PushTemplateType
import com.adobe.marketing.mobile.notificationbuilder.internal.builders.AutoCarouselNotificationBuilder
import com.adobe.marketing.mobile.notificationbuilder.internal.builders.BasicNotificationBuilder
import com.adobe.marketing.mobile.notificationbuilder.internal.builders.InputBoxNotificationBuilder
import com.adobe.marketing.mobile.notificationbuilder.internal.builders.LegacyNotificationBuilder
import com.adobe.marketing.mobile.notificationbuilder.internal.builders.ManualCarouselNotificationBuilder
import com.adobe.marketing.mobile.notificationbuilder.internal.builders.MultiIconNotificationBuilder
import com.adobe.marketing.mobile.notificationbuilder.internal.builders.ProductCatalogNotificationBuilder
import com.adobe.marketing.mobile.notificationbuilder.internal.builders.ProductRatingNotificationBuilder
import com.adobe.marketing.mobile.notificationbuilder.internal.builders.TimerNotificationBuilder
import com.adobe.marketing.mobile.notificationbuilder.internal.builders.ZeroBezelNotificationBuilder
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.MockAEPPushTemplateDataProvider
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.MockCarousalTemplateDataProvider
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.MockProductCatalogTemplateDataProvider
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.MockTimerTemplateDataProvider
import com.adobe.marketing.mobile.services.AppContextService
import com.adobe.marketing.mobile.services.ServiceProvider
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkClass
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [31])
class NotificationBuilderTests {

    @Mock
    private lateinit var application: Application
    private lateinit var context: Context
    private lateinit var alarmManager: AlarmManager
    private lateinit var notificationManager: NotificationManager
    private lateinit var trackerActivityClass: Class<out Activity>
    private lateinit var broadcastReceiverClass: Class<out BroadcastReceiver>
    private lateinit var notificationManagerCompat: NotificationManagerCompat

    @Before
    fun setUp() {
        setupNotificationBuilderMocks()
        setupApplicationMocks()
        trackerActivityClass = mockkClass(Activity::class, relaxed = true).javaClass
        broadcastReceiverClass = mockkClass(BroadcastReceiver::class, relaxed = true).javaClass
        mockkObject(PendingIntentUtils)
        mockkObject(PushTemplateImageUtils)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    private fun setupNotificationBuilderMocks() {
        mockkObject(BasicNotificationBuilder)
        mockkObject(ManualCarouselNotificationBuilder)
        mockkObject(AutoCarouselNotificationBuilder)
        mockkObject(InputBoxNotificationBuilder)
        mockkObject(ZeroBezelNotificationBuilder)
        mockkObject(ProductRatingNotificationBuilder)
        mockkObject(ProductCatalogNotificationBuilder)
        mockkObject(MultiIconNotificationBuilder)
        mockkObject(TimerNotificationBuilder)
        mockkObject(LegacyNotificationBuilder)
    }

    private fun setupApplicationMocks() {
        mockkStatic(NotificationManagerCompat::class)
        application = mockk<Application>(relaxed = true)
        context = mockk<Context>(relaxed = true)
        alarmManager = mockk<AlarmManager>(relaxed = true)
        notificationManager = mockk<NotificationManager>(relaxed = true)
        notificationManagerCompat = mockkClass(NotificationManagerCompat::class)
        every { NotificationManagerCompat.from(any()) } returns notificationManagerCompat
        every { notificationManagerCompat.cancel(any()) } returns Unit
        every { alarmManager.canScheduleExactAlarms() } returns true
        every { context.getSystemService(Context.NOTIFICATION_SERVICE) } returns notificationManager
        every { context.getSystemService(Context.ALARM_SERVICE) } returns alarmManager
        every { context.packageName } answers { callOriginal() }
        every { application.applicationContext } returns context
        ServiceProvider.getInstance().appContextService.setApplication(application)
    }

    @Test
    fun `NotificationBuilder version values matches the expected version`() {
        val version = NotificationBuilder.version()

        assertEquals("3.0.3", version)
    }

    @Test
    fun `constructNotificationBuilder given a map with required data should return a NotificationCompat Builder`() {
        val mapData = MockAEPPushTemplateDataProvider.getMockedDataMapWithRequiredData()
        val notificationBuilder = NotificationBuilder.constructNotificationBuilder(
            mapData,
            trackerActivityClass,
            broadcastReceiverClass
        )

        assertNotNull(notificationBuilder)
    }

    @Test(expected = NotificationConstructionFailedException::class)
    fun `constructNotificationBuilder given a map with required data but no context is available should throw an exception`() {
        setNullContext()
        val mapData = MockAEPPushTemplateDataProvider.getMockedDataMapWithRequiredData()
        val notificationBuilder = NotificationBuilder.constructNotificationBuilder(
            mapData,
            trackerActivityClass,
            broadcastReceiverClass
        )

        assertNull(notificationBuilder)
    }

    @Test(expected = NotificationConstructionFailedException::class)
    fun `constructNotificationBuilder given a map with empty data should thrown an exception`() {
        val mapData = emptyMap<String, String>()
        val notificationBuilder = NotificationBuilder.constructNotificationBuilder(
            mapData,
            trackerActivityClass,
            broadcastReceiverClass
        )

        assertNull(notificationBuilder)
    }

    @Test
    fun `constructNotificationBuilder given an intent with required data should return a NotificationCompat Builder`() {
        val bundle = MockAEPPushTemplateDataProvider.getMockedBundleWithRequiredData()
        val intent = Intent()
        intent.action = "mockAction"
        intent.putExtras(bundle)
        val notificationBuilder = NotificationBuilder.constructNotificationBuilder(
            intent,
            trackerActivityClass,
            broadcastReceiverClass
        )

        assertNotNull(notificationBuilder)
    }

    @Test(expected = NotificationConstructionFailedException::class)
    fun `constructNotificationBuilder given an intent with required data but no context is available should throw an exception`() {
        setNullContext()
        val bundle = MockAEPPushTemplateDataProvider.getMockedBundleWithRequiredData()
        val intent = Intent()
        intent.action = "mockAction"
        intent.putExtras(bundle)
        val notificationBuilder = NotificationBuilder.constructNotificationBuilder(
            intent,
            trackerActivityClass,
            broadcastReceiverClass
        )

        assertNull(notificationBuilder)
    }

    @Test(expected = NotificationConstructionFailedException::class)
    fun `constructNotificationBuilder given an intent with empty data should thrown an exception`() {
        val intent = Intent()
        intent.action = "mockAction"
        val notificationBuilder = NotificationBuilder.constructNotificationBuilder(
            intent,
            trackerActivityClass,
            broadcastReceiverClass
        )

        assertNull(notificationBuilder)
    }

    @Test
    fun `verify private createNotificationBuilder calls LegacyNotificationBuilder construct`() {
        val mapData = MockAEPPushTemplateDataProvider.getMockedAEPDataMapWithAllKeys()
        mapData[PushTemplateConstants.PushPayloadKeys.TEMPLATE_TYPE] = "some unknown type"
        NotificationBuilder.constructNotificationBuilder(mapData, trackerActivityClass, broadcastReceiverClass)
        verify(exactly = 1) { LegacyNotificationBuilder.construct(any(Context::class), any(), trackerActivityClass) }
    }

    @Test
    fun `verify private createNotificationBuilder calls BasicNotificationBuilder construct`() {
        val mapData = MockAEPPushTemplateDataProvider.getMockedAEPDataMapWithAllKeys()
        NotificationBuilder.constructNotificationBuilder(mapData, trackerActivityClass, broadcastReceiverClass)
        verify(exactly = 1) { BasicNotificationBuilder.construct(any(Context::class), any(), trackerActivityClass, broadcastReceiverClass) }
    }

    @Test
    fun `verify private createNotificationBuilder calls ManualCarouselNotificationBuilder construct`() {
        val mapData = MockCarousalTemplateDataProvider.getMockedMapWithManualCarouselData()
        NotificationBuilder.constructNotificationBuilder(mapData, trackerActivityClass, broadcastReceiverClass)
        verify(exactly = 1) { ManualCarouselNotificationBuilder.construct(any(Context::class), any(), trackerActivityClass, broadcastReceiverClass) }
    }

    @Test
    fun `verify private createNotificationBuilder calls AutoCarouselNotificationBuilder construct`() {
        val mapData = MockCarousalTemplateDataProvider.getMockedMapWithAutoCarouselData()
        NotificationBuilder.constructNotificationBuilder(mapData, trackerActivityClass, broadcastReceiverClass)
        verify(exactly = 1) { AutoCarouselNotificationBuilder.construct(any(Context::class), any(), trackerActivityClass, broadcastReceiverClass) }
    }

    @Test
    fun `verify private createNotificationBuilder calls InputBoxNotificationBuilder construct`() {
        val mapData = MockAEPPushTemplateDataProvider.getMockedAEPDataMapWithAllKeys()
        mapData[PushTemplateConstants.PushPayloadKeys.TEMPLATE_TYPE] = PushTemplateType.INPUT_BOX.value
        mapData[PushTemplateConstants.PushPayloadKeys.INPUT_BOX_RECEIVER_NAME] = "receiverName"
        NotificationBuilder.constructNotificationBuilder(mapData, trackerActivityClass, broadcastReceiverClass)
        verify(exactly = 1) { InputBoxNotificationBuilder.construct(any(Context::class), any(), trackerActivityClass, broadcastReceiverClass) }
    }

    @Test
    fun `verify private createNotificationBuilder calls ZeroBezelNotificationBuilder construct`() {
        val mapData = MockAEPPushTemplateDataProvider.getMockedAEPDataMapWithAllKeys()
        mapData[PushTemplateConstants.PushPayloadKeys.TEMPLATE_TYPE] = PushTemplateType.ZERO_BEZEL.value
        NotificationBuilder.constructNotificationBuilder(mapData, trackerActivityClass, broadcastReceiverClass)
        verify(exactly = 1) { ZeroBezelNotificationBuilder.construct(any(Context::class), any(), trackerActivityClass) }
    }

    @Test
    fun `verify private createNotificationBuilder calls ProductRatingNotificationBuilder construct`() {
        every { PushTemplateImageUtils.cacheImages(any()) } answers { 1 }
        val mapData = MockAEPPushTemplateDataProvider.getMockedAEPDataMapWithAllKeys()
        mapData[PushTemplateConstants.PushPayloadKeys.TEMPLATE_TYPE] = PushTemplateType.PRODUCT_RATING.value
        mapData[PushTemplateConstants.PushPayloadKeys.RATING_UNSELECTED_ICON] = "https://i.imgur.com/unselected.png"
        mapData[PushTemplateConstants.PushPayloadKeys.RATING_SELECTED_ICON] = "https://i.imgur.com/selected.png"
        mapData[PushTemplateConstants.PushPayloadKeys.RATING_ACTIONS] = "[{\"uri\":\"https://www.adobe.com\", \"type\":\"WEBURL\"},{\"type\":\"OPENAPP\"},{\"type\":\"DISMISS\"},{\"uri\": \"https://www.adobe.com\", \"type\":\"WEBURL\"},{\"uri\":\"instabiz://opensecond\", \"type\":\"DEEPLINK\"}]"
        NotificationBuilder.constructNotificationBuilder(mapData, trackerActivityClass, broadcastReceiverClass)
        verify(exactly = 1) { ProductRatingNotificationBuilder.construct(any(Context::class), any(), trackerActivityClass, broadcastReceiverClass) }
    }

    @Test
    fun `verify private createNotificationBuilder calls ProductCatalogNotificationBuilder construct`() {
        every { PushTemplateImageUtils.cacheImages(any()) } answers { 3 }
        every { PushTemplateImageUtils.getCachedImage(any()) } answers { mockk() }
        val mapData = MockProductCatalogTemplateDataProvider.getMockedMapWithProductCatalogData()
        mapData[PushTemplateConstants.PushPayloadKeys.TEMPLATE_TYPE] = PushTemplateType.PRODUCT_CATALOG.value
        NotificationBuilder.constructNotificationBuilder(mapData, trackerActivityClass, broadcastReceiverClass)
        verify(exactly = 1) { ProductCatalogNotificationBuilder.construct(any(Context::class), any(), trackerActivityClass, broadcastReceiverClass) }
    }

    @Test
    fun `verify private createNotificationBuilder calls MultiIconNotificationBuilder construct`() {
        every { PushTemplateImageUtils.cacheImages(any()) } answers { 3 }
        every { PushTemplateImageUtils.getCachedImage(any()) } answers { mockk() }
        val mapData = MockAEPPushTemplateDataProvider.getMockedAEPDataMapWithAllKeys()
        mapData[PushTemplateConstants.PushPayloadKeys.TEMPLATE_TYPE] = PushTemplateType.MULTI_ICON.value
        mapData[PushTemplateConstants.PushPayloadKeys.MULTI_ICON_ITEMS] = "[{\"img\":\"https://sneakerland.com/products/assets/shoe1.png\",\"uri\":\"myapp://chooseShoeType/shoe1\",\"type\":\"DEEPLINK\"},{\"img\":\"https://sneakerland.com/products/assets/shoe2.png\",\"uri\":\"myapp://chooseShoeType/shoe2\",\"type\":\"DEEPLINK\"},{\"img\":\"https://sneakerland.com/products/assets/shoe3.png\",\"uri\":\"myapp://chooseShoeType/shoe3\",\"type\":\"DEEPLINK\"},{\"img\":\"https://sneakerland.com/products/assets/shoe4.png\",\"uri\":\"myapp://chooseShoeType/shoe4\",\"type\":\"DEEPLINK\"},{\"img\":\"https://sneakerland.com/products/assets/shoe5.png\",\"uri\":\"myapp://chooseShoeType/shoe5\",\"type\":\"DEEPLINK\"}]"
        NotificationBuilder.constructNotificationBuilder(mapData, trackerActivityClass, broadcastReceiverClass)
        verify(exactly = 1) { MultiIconNotificationBuilder.construct(any(Context::class), any(), trackerActivityClass) }
    }

    @Test
    fun `verify private createNotificationBuilder calls TimerNotificationBuilder construct`() {
        val mapData = MockTimerTemplateDataProvider.getMockedMapWithTimerData(true, "10")
        mapData[PushTemplateConstants.PushPayloadKeys.TEMPLATE_TYPE] = PushTemplateType.TIMER.value
        NotificationBuilder.constructNotificationBuilder(mapData, trackerActivityClass, broadcastReceiverClass)
        verify(exactly = 1) { TimerNotificationBuilder.construct(any(Context::class), any(), trackerActivityClass, broadcastReceiverClass) }
    }

    private fun setNullContext() {
        val mockAppContextService = mockk<AppContextService>()
        val mockServiceProvider = mockkClass(ServiceProvider::class)
        mockkStatic(ServiceProvider::getInstance)
        every { ServiceProvider.getInstance() } returns mockServiceProvider
        every { mockServiceProvider.appContextService } returns mockAppContextService
        every { mockAppContextService.applicationContext } returns null
    }
}
