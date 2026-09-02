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

package com.adobe.marketing.mobile.notificationbuilder.internal.ajo.builders

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.os.Bundle
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.adobe.marketing.mobile.notificationbuilder.PushTemplateConstants.PushPayloadKeys
import com.adobe.marketing.mobile.notificationbuilder.internal.PushTemplateImageUtils
import com.adobe.marketing.mobile.notificationbuilder.internal.PushTemplateType
import com.adobe.marketing.mobile.notificationbuilder.internal.builders.DummyActivity
import com.adobe.marketing.mobile.notificationbuilder.internal.builders.DummyBroadcastReceiver
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.AJOBasicPushTemplate
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.AJO_MOCKED_FLAT_BODY
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.AJO_MOCKED_FLAT_TITLE
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.AJO_MOCKED_TEMPLATE_PROPS_CENTER_CROP
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.AJO_MOCKED_TEMPLATE_PROPS_FIT_CENTER
import com.adobe.marketing.mobile.notificationbuilder.internal.util.IntentData
import com.adobe.marketing.mobile.notificationbuilder.internal.util.MapData
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockkConstructor
import io.mockk.mockkObject
import io.mockk.unmockkAll
import junit.framework.TestCase.assertNotNull
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [31])
class AJOBasicNotificationBuilderTest {

    private lateinit var context: Context
    private lateinit var trackerActivityClass: Class<out Activity>
    private lateinit var broadcastReceiverClass: Class<out BroadcastReceiver>

    @Before
    fun setUp() {
        context = RuntimeEnvironment.getApplication()
        trackerActivityClass = DummyActivity::class.java
        broadcastReceiverClass = DummyBroadcastReceiver::class.java
        mockkObject(PushTemplateImageUtils)
        mockkConstructor(RemoteViews::class)
        every { anyConstructed<RemoteViews>().setTextViewText(any(), any()) } just Runs
        every { anyConstructed<RemoteViews>().setImageViewBitmap(any(), any()) } just Runs
        every { anyConstructed<RemoteViews>().setViewVisibility(any(), any()) } just Runs
        every { anyConstructed<RemoteViews>().setOnClickPendingIntent(any(), any()) } just Runs
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `construct returns NotificationCompat Builder for center_crop template`() {
        val pushTemplate = AJOBasicPushTemplate(
            MapData(
                mutableMapOf(
                    PushPayloadKeys.TEMPLATE_TYPE to PushTemplateType.AJO_BASIC.value,
                    PushPayloadKeys.VERSION to "1",
                    PushPayloadKeys.TITLE to AJO_MOCKED_FLAT_TITLE,
                    PushPayloadKeys.BODY to AJO_MOCKED_FLAT_BODY,
                    PushPayloadKeys.AJO_TEMPLATE_PROPERTIES to AJO_MOCKED_TEMPLATE_PROPS_CENTER_CROP
                )
            )
        )

        val result = AJOBasicNotificationBuilder.construct(
            context, pushTemplate, trackerActivityClass, broadcastReceiverClass
        )

        assertNotNull(result)
        assert(result is NotificationCompat.Builder)
    }

    @Test
    fun `construct returns NotificationCompat Builder for fit_center template`() {
        val pushTemplate = AJOBasicPushTemplate(
            MapData(
                mutableMapOf(
                    PushPayloadKeys.TEMPLATE_TYPE to PushTemplateType.AJO_BASIC.value,
                    PushPayloadKeys.VERSION to "1",
                    PushPayloadKeys.TITLE to AJO_MOCKED_FLAT_TITLE,
                    PushPayloadKeys.BODY to AJO_MOCKED_FLAT_BODY,
                    PushPayloadKeys.AJO_TEMPLATE_PROPERTIES to AJO_MOCKED_TEMPLATE_PROPS_FIT_CENTER
                )
            )
        )

        val result = AJOBasicNotificationBuilder.construct(
            context, pushTemplate, trackerActivityClass, broadcastReceiverClass
        )

        assertNotNull(result)
        assert(result is NotificationCompat.Builder)
    }

    @Test
    fun `construct returns NotificationCompat Builder when no blob is present`() {
        val pushTemplate = AJOBasicPushTemplate(
            MapData(
                mutableMapOf(
                    PushPayloadKeys.TEMPLATE_TYPE to PushTemplateType.AJO_BASIC.value,
                    PushPayloadKeys.VERSION to "1",
                    PushPayloadKeys.TITLE to AJO_MOCKED_FLAT_TITLE,
                    PushPayloadKeys.BODY to AJO_MOCKED_FLAT_BODY
                )
            )
        )

        val result = AJOBasicNotificationBuilder.construct(
            context, pushTemplate, trackerActivityClass, broadcastReceiverClass
        )

        assertNotNull(result)
        assert(result is NotificationCompat.Builder)
    }

    @Test
    fun `construct uses silent channel when template is from intent`() {
        val bundle = Bundle().apply {
            putString(PushPayloadKeys.TEMPLATE_TYPE, PushTemplateType.AJO_BASIC.value)
            putString(PushPayloadKeys.VERSION, "1")
            putString(PushPayloadKeys.TITLE, AJO_MOCKED_FLAT_TITLE)
            putString(PushPayloadKeys.BODY, AJO_MOCKED_FLAT_BODY)
            putString(PushPayloadKeys.AJO_TEMPLATE_PROPERTIES, AJO_MOCKED_TEMPLATE_PROPS_CENTER_CROP)
        }
        val pushTemplate = AJOBasicPushTemplate(IntentData(bundle, null))

        val result = AJOBasicNotificationBuilder.construct(
            context, pushTemplate, trackerActivityClass, broadcastReceiverClass
        )

        assertNotNull(result)
        assert(result is NotificationCompat.Builder)
    }

    @Test
    @Config(sdk = [21])
    fun `construct returns builder on pre-Oreo device`() {
        val pushTemplate = AJOBasicPushTemplate(
            MapData(
                mutableMapOf(
                    PushPayloadKeys.TEMPLATE_TYPE to PushTemplateType.AJO_BASIC.value,
                    PushPayloadKeys.VERSION to "1",
                    PushPayloadKeys.TITLE to AJO_MOCKED_FLAT_TITLE,
                    PushPayloadKeys.BODY to AJO_MOCKED_FLAT_BODY,
                    PushPayloadKeys.AJO_TEMPLATE_PROPERTIES to AJO_MOCKED_TEMPLATE_PROPS_CENTER_CROP
                )
            )
        )

        val result = AJOBasicNotificationBuilder.construct(
            context, pushTemplate, trackerActivityClass, broadcastReceiverClass
        )

        assertNotNull(result)
        assert(result is NotificationCompat.Builder)
    }

    @Test
    fun `construct succeeds with custom sound`() {
        val pushTemplate = AJOBasicPushTemplate(
            MapData(
                mutableMapOf(
                    PushPayloadKeys.TEMPLATE_TYPE to PushTemplateType.AJO_BASIC.value,
                    PushPayloadKeys.VERSION to "1",
                    PushPayloadKeys.TITLE to AJO_MOCKED_FLAT_TITLE,
                    PushPayloadKeys.BODY to AJO_MOCKED_FLAT_BODY,
                    PushPayloadKeys.SOUND to "bells",
                    PushPayloadKeys.AJO_TEMPLATE_PROPERTIES to AJO_MOCKED_TEMPLATE_PROPS_CENTER_CROP
                )
            )
        )

        val result = AJOBasicNotificationBuilder.construct(
            context, pushTemplate, trackerActivityClass, broadcastReceiverClass
        )

        assertNotNull(result)
        assert(result is NotificationCompat.Builder)
    }
}
