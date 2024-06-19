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
import androidx.core.app.NotificationCompat
import com.adobe.marketing.mobile.notificationbuilder.NotificationConstructionFailedException
import com.adobe.marketing.mobile.notificationbuilder.PushTemplateConstants
import com.adobe.marketing.mobile.notificationbuilder.PushTemplateConstants.DefaultValues.PRODUCT_CATALOG_VERTICAL_LAYOUT
import com.adobe.marketing.mobile.notificationbuilder.R
import com.adobe.marketing.mobile.notificationbuilder.internal.PushTemplateImageUtils
import com.adobe.marketing.mobile.notificationbuilder.internal.PushTemplateImageUtils.cacheImages
import com.adobe.marketing.mobile.notificationbuilder.internal.PushTemplateImageUtils.getCachedImage
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.MockProductCatalogTemplateDataProvider.getMockedMapWithProductCatalogData
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.ProductCatalogPushTemplate
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.provideMockedProductCatalogTemplate
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.replaceValueInMap
import com.adobe.marketing.mobile.notificationbuilder.internal.util.MapData
import io.mockk.every
import io.mockk.mockkClass
import io.mockk.mockkObject
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [31])
class ProductCatalogNotificationBuilderTest {
    @Mock
    private lateinit var context: Context
    private lateinit var trackerActivityClass: Class<out Activity>
    private lateinit var broadcastReceiverClass: Class<out BroadcastReceiver>

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)

        context = RuntimeEnvironment.getApplication()
        trackerActivityClass = DummyActivity::class.java
        broadcastReceiverClass = DummyBroadcastReceiver::class.java
        mockkObject(PushTemplateImageUtils)
    }

    @Test
    fun `construct should throw NotificationConstructionFailedException if downloaded image count does not match catalog image count`() {
        val pushTemplate = provideMockedProductCatalogTemplate()

        assertFailsWith(
            exceptionClass = NotificationConstructionFailedException::class,
            message = "Failed to download all images for the product catalog notification.",
            block = {
                ProductCatalogNotificationBuilder.construct(context, pushTemplate, trackerActivityClass, broadcastReceiverClass)
            }
        )
    }

    @Test
    fun `construct should return a NotificationCompat Builder if download image count matches catalog image count`() {
        val pushTemplate = provideMockedProductCatalogTemplate()
        val cachedItem = mockkClass(Bitmap::class)

        // product catalog template requires 3 catalog items
        every { cacheImages(any()) } answers { 3 }
        every { getCachedImage(any()) } answers { cachedItem }
        val notificationBuilder = ProductCatalogNotificationBuilder.construct(context, pushTemplate, trackerActivityClass, broadcastReceiverClass)

        assertEquals(R.layout.push_template_horizontal_catalog, notificationBuilder.bigContentView.layoutId)
        assertEquals(NotificationCompat.Builder::class.java, notificationBuilder::class.java)
    }

    @Test
    fun `construct should build a notification with a vertical layout if vertical layout is provided`() {
        val dataMap = getMockedMapWithProductCatalogData()
        dataMap.replaceValueInMap(PushTemplateConstants.PushPayloadKeys.CATALOG_LAYOUT, PRODUCT_CATALOG_VERTICAL_LAYOUT)
        val pushTemplate = ProductCatalogPushTemplate(MapData(dataMap))
        val cachedItem = mockkClass(Bitmap::class)

        // product catalog template requires 3 catalog items
        every { cacheImages(any()) } answers { 3 }
        every { getCachedImage(any()) } answers { cachedItem }

        val notificationBuilder = ProductCatalogNotificationBuilder.construct(context, pushTemplate, trackerActivityClass, broadcastReceiverClass)
        assertEquals(R.layout.push_tempate_vertical_catalog, notificationBuilder.bigContentView.layoutId)
        assertEquals(NotificationCompat.Builder::class.java, notificationBuilder::class.java)
    }

    @Test
    fun `construct should return a valid NotificationCompat Builder Broadcast Receiver is not provided`() {
        val pushTemplate = provideMockedProductCatalogTemplate()
        val cachedItem = mockkClass(Bitmap::class)

        // product catalog template requires 3 catalog items
        every { cacheImages(any()) } answers { 3 }
        every { getCachedImage(any()) } answers { cachedItem }

        val notificationBuilder = ProductCatalogNotificationBuilder.construct(context, pushTemplate, trackerActivityClass, null)
        assertEquals(NotificationCompat.Builder::class.java, notificationBuilder::class.java)
    }

    @Test
    fun `construct should throw NotificationConstructionFailedException if catalog thumbnail is not found`() {
        val pushTemplate = provideMockedProductCatalogTemplate()
        every { cacheImages(any()) } answers { 3 }

        assertFailsWith(
            exceptionClass = NotificationConstructionFailedException::class,
            message = "No image found for catalog item thumbnail.",
            block = {
                ProductCatalogNotificationBuilder.construct(context, pushTemplate, trackerActivityClass, broadcastReceiverClass)
            }
        )
    }

    @Test
    fun `construct should return a NotificationCompat Builder if Tag is provided in Push Template`() {
        val dataMap = getMockedMapWithProductCatalogData()
        dataMap[PushTemplateConstants.PushPayloadKeys.TAG] = "tag"
        val pushTemplate = ProductCatalogPushTemplate(MapData(dataMap))
        val cachedItem = mockkClass(Bitmap::class)

        // product catalog template requires 3 catalog items
        every { cacheImages(any()) } answers { 3 }
        every { getCachedImage(any()) } answers { cachedItem }

        val notificationBuilder = ProductCatalogNotificationBuilder.construct(context, pushTemplate, trackerActivityClass, broadcastReceiverClass)
        assertEquals(NotificationCompat.Builder::class.java, notificationBuilder::class.java)
    }
}
