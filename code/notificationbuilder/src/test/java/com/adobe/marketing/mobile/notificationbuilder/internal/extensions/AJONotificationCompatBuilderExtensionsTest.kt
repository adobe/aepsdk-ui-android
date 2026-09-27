/*
  Copyright 2026 Adobe. All rights reserved.
  This file is licensed to you under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License. You may obtain a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0
  Unless required by applicable law or agreed to in writing, software distributed under
  the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR REPRESENTATIONS
  OF ANY KIND, either express or implied. See the License for the specific language
  governing permissions and limitations under the License.
*/

package com.adobe.marketing.mobile.notificationbuilder.internal.extensions

import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat
import com.adobe.marketing.mobile.notificationbuilder.PushTemplateConstants
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.BasicPushTemplate
import com.adobe.marketing.mobile.plugin.IPushTemplateTrackingProvider
import com.adobe.marketing.mobile.plugin.PushInteraction
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [31])
class AJONotificationCompatBuilderExtensionsTest {

    private lateinit var context: Context
    private lateinit var trackingProvider: IPushTemplateTrackingProvider
    private lateinit var pendingIntent: PendingIntent
    private val interactions = mutableListOf<PushInteraction>()

    @Before
    fun setup() {
        context = RuntimeEnvironment.getApplication()
        pendingIntent = mockk(relaxed = true)
        trackingProvider = mockk()
        interactions.clear()
        val captured = slot<PushInteraction>()
        every { trackingProvider.getPendingIntent(capture(captured)) } answers {
            interactions.add(captured.captured)
            pendingIntent
        }
    }

    @Test
    fun `click action requests content_click and forwards a deeplink uri`() {
        val builder = NotificationCompat.Builder(context, "channel")
            .setAJONotificationClickAction(
                trackingProvider, "myapp://home", PushTemplateConstants.ActionType.DEEPLINK
            )

        val interaction = interactions.single()
        assertEquals("content_click", interaction.type)
        assertEquals("myapp://home", interaction.actionUri)
        assertNull(interaction.actionId)
        assertNull(interaction.templateExtras)
        assertSame(pendingIntent, builder.build().contentIntent)
    }

    @Test
    fun `click action drops the uri for open-app actions`() {
        NotificationCompat.Builder(context, "channel")
            .setAJONotificationClickAction(
                trackingProvider, "myapp://home", PushTemplateConstants.ActionType.OPENAPP
            )

        assertNull(interactions.single().actionUri)
    }

    @Test
    fun `click action forwards a web url uri`() {
        NotificationCompat.Builder(context, "channel")
            .setAJONotificationClickAction(
                trackingProvider, "https://adobe.com", PushTemplateConstants.ActionType.WEBURL
            )

        assertEquals("https://adobe.com", interactions.single().actionUri)
    }

    @Test
    fun `click action drops the uri for none, dismiss and missing action types`() {
        listOf(
            PushTemplateConstants.ActionType.NONE,
            PushTemplateConstants.ActionType.DISMISS,
            null
        ).forEach { actionType ->
            NotificationCompat.Builder(context, "channel")
                .setAJONotificationClickAction(trackingProvider, "myapp://home", actionType)
        }

        assertEquals(listOf("content_click", "content_click", "content_click"), interactions.map { it.type })
        assertEquals(listOf(null, null, null), interactions.map { it.actionUri })
    }

    @Test
    fun `action buttons forward the uri only for web url and deeplink buttons`() {
        val buttons = listOf(
            BasicPushTemplate.ActionButton("Web", "https://adobe.com", "WEBURL"),
            BasicPushTemplate.ActionButton("Deep", "myapp://page", "DEEPLINK"),
            BasicPushTemplate.ActionButton("App", "myapp://ignored", "OPENAPP")
        )

        NotificationCompat.Builder(context, "channel")
            .addAJOActionButtons(trackingProvider, buttons)

        assertEquals(listOf("https://adobe.com", "myapp://page", null), interactions.map { it.actionUri })
    }

    @Test
    fun `null or empty button list adds no actions`() {
        listOf(null, emptyList<BasicPushTemplate.ActionButton>()).forEach { buttons ->
            val notification = NotificationCompat.Builder(context, "channel")
                .addAJOActionButtons(trackingProvider, buttons)
                .build()
            assertNull(notification.actions)
        }
        assertEquals(0, interactions.size)
    }

    @Test
    fun `delete action requests dismiss`() {
        val builder = NotificationCompat.Builder(context, "channel")
            .setAJONotificationDeleteAction(trackingProvider)

        assertEquals("dismiss", interactions.single().type)
        assertSame(pendingIntent, builder.build().deleteIntent)
    }

    @Test
    fun `action buttons request button_click with label as actionId`() {
        val buttons = listOf(
            BasicPushTemplate.ActionButton("Open", "https://adobe.com", "WEBURL"),
            BasicPushTemplate.ActionButton("Later", null, "DISMISS")
        )

        val builder = NotificationCompat.Builder(context, "channel")
            .addAJOActionButtons(trackingProvider, buttons)

        assertEquals(listOf("button_click", "button_click"), interactions.map { it.type })
        assertEquals(listOf("Open", "Later"), interactions.map { it.actionId })
        assertEquals(listOf("https://adobe.com", null), interactions.map { it.actionUri })
        assertEquals(2, builder.build().actions.size)
    }

    @Test
    fun `interactions the host does not handle are left unwired`() {
        every { trackingProvider.getPendingIntent(any()) } returns null
        val buttons = listOf(BasicPushTemplate.ActionButton("Open", "https://adobe.com", "WEBURL"))

        val notification = NotificationCompat.Builder(context, "channel")
            .setAJONotificationClickAction(trackingProvider, null, null)
            .setAJONotificationDeleteAction(trackingProvider)
            .addAJOActionButtons(trackingProvider, buttons)
            .build()

        assertNull(notification.contentIntent)
        assertNull(notification.deleteIntent)
        assertNull(notification.actions)
        verify(exactly = 3) { trackingProvider.getPendingIntent(any()) }
    }
}
