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
import android.graphics.Bitmap
import android.view.View
import android.widget.RemoteViews
import com.adobe.marketing.mobile.notificationbuilder.PushTemplateConstants
import com.adobe.marketing.mobile.notificationbuilder.internal.PushTemplateImageUtils
import com.adobe.marketing.mobile.notificationbuilder.internal.PushTemplateImageUtils.cacheImages
import com.adobe.marketing.mobile.notificationbuilder.internal.PushTemplateImageUtils.getCachedImage
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.MockAEPPushTemplateDataProvider
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.ZeroBezelPushTemplate
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.replaceValueInMap
import com.adobe.marketing.mobile.notificationbuilder.internal.util.MapData
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkObject
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [31])
class ZeroBezelNotificationBuilderTest {

    private lateinit var context: Context
    private lateinit var dataMap: MutableMap<String, String>
    private lateinit var mockBitmap: Bitmap
    private lateinit var trackerActivityClass: Class<out Activity>

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        context = RuntimeEnvironment.getApplication()
        dataMap = MockAEPPushTemplateDataProvider.getMockedAEPDataMapWithAllKeys()
        mockkObject(PushTemplateImageUtils)
        mockkConstructor(RemoteViews::class)
        mockBitmap = mockk<Bitmap>()
        trackerActivityClass = DummyActivity::class.java
        every { getCachedImage(any()) } answers { mockBitmap }
    }

    @Test
    fun `verify construct with image downloaded and collapsedStyle is img`() {
        val pushTemplate = ZeroBezelPushTemplate(MapData(dataMap))

        every { cacheImages(any()) } answers { 2 }
        every { anyConstructed<RemoteViews>().setImageViewBitmap(any(), mockBitmap) } just Runs

        ZeroBezelNotificationBuilder.construct(
            context,
            pushTemplate,
            trackerActivityClass
        )

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

        ZeroBezelNotificationBuilder.construct(
            context,
            pushTemplate,
            trackerActivityClass
        )

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

        ZeroBezelNotificationBuilder.construct(
            context,
            pushTemplate,
            trackerActivityClass
        )

        verify { cacheImages(listOf(pushTemplate.imageUrl)) }
        verify { anyConstructed<RemoteViews>().setViewVisibility(any(), View.GONE) }
    }
}
