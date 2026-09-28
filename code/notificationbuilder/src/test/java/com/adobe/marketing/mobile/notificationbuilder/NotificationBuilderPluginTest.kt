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

package com.adobe.marketing.mobile.notificationbuilder

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.widget.RemoteViews
import com.adobe.marketing.mobile.notificationbuilder.PushTemplateConstants.PushPayloadKeys
import com.adobe.marketing.mobile.notificationbuilder.internal.PushTemplateImageUtils
import com.adobe.marketing.mobile.notificationbuilder.internal.PushTemplateType
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.AJO_MOCKED_BIGTEXT_PROPS_FULL
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.AJO_MOCKED_TEMPLATE_PROPS_FIT_CENTER
import com.adobe.marketing.mobile.plugin.IPushTemplateTrackingProvider
import com.adobe.marketing.mobile.plugin.PushInteraction
import com.adobe.marketing.mobile.services.AppContextService
import com.adobe.marketing.mobile.services.ServiceProvider
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkClass
import io.mockk.mockkConstructor
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

/**
 * End-to-end tests for the plugin entry point: an AJO payload goes in, a real [Notification] comes
 * out, and every tracked interaction is wired to the PendingIntent the host provider returned.
 * These mirror the on-device push-template E2E scenarios.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [31])
class NotificationBuilderPluginTest {

    private companion object {
        const val CONTENT_URI = "demoapp://secondpage"
        const val WEB_URI = "https://www.adobe.com"
        const val DEEPLINK_URI = "demoapp://thirdpage"
        const val BUTTONS =
            "[{\"label\":\"Web URL\",\"type\":\"WEBURL\",\"uri\":\"$WEB_URI\"}," +
                "{\"label\":\"Deeplink\",\"type\":\"DEEPLINK\",\"uri\":\"$DEEPLINK_URI\"}," +
                "{\"label\":\"Open App\",\"type\":\"OPENAPP\"}]"
    }

    private lateinit var application: Application
    private lateinit var notificationManager: NotificationManager
    private lateinit var trackingProvider: IPushTemplateTrackingProvider
    private val interactions = mutableListOf<PushInteraction>()
    private val pendingIntents = mutableListOf<PendingIntent>()
    private val plugin = NotificationBuilderPlugin()

    @Before
    fun setUp() {
        application = RuntimeEnvironment.getApplication()
        // mock the provider rather than calling setApplication: the app context is set-once and
        // would leak into other test classes sharing the Robolectric sandbox
        mockApplicationContext(application)
        notificationManager =
            application.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        mockkObject(PushTemplateImageUtils)
        every { PushTemplateImageUtils.getScaledBitmap(any(), any(), any(), any()) } returns null
        mockkConstructor(RemoteViews::class)
        every { anyConstructed<RemoteViews>().setTextViewText(any(), any()) } just Runs
        every { anyConstructed<RemoteViews>().setImageViewBitmap(any(), any()) } just Runs
        every { anyConstructed<RemoteViews>().setViewVisibility(any(), any()) } just Runs
        every { anyConstructed<RemoteViews>().setOnClickPendingIntent(any(), any()) } just Runs

        interactions.clear()
        pendingIntents.clear()
        trackingProvider = mockk()
        val captured = slot<PushInteraction>()
        every { trackingProvider.getPendingIntent(capture(captured)) } answers {
            interactions.add(captured.captured)
            mockk<PendingIntent>(relaxed = true).also { pendingIntents.add(it) }
        }
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    private fun mockApplicationContext(context: Context?) {
        val appContextService = mockk<AppContextService>()
        val serviceProvider = mockkClass(ServiceProvider::class, relaxed = true)
        mockkStatic(ServiceProvider::getInstance)
        every { ServiceProvider.getInstance() } returns serviceProvider
        every { serviceProvider.appContextService } returns appContextService
        every { appContextService.applicationContext } returns context
    }

    private fun ajoPayload(
        templateType: PushTemplateType,
        vararg overrides: Pair<String, String?>
    ): Map<String, String> {
        val payload = mutableMapOf<String, String?>(
            PushPayloadKeys.TEMPLATE_TYPE to templateType.value,
            PushPayloadKeys.VERSION to "1",
            PushPayloadKeys.TITLE to "AJO title",
            PushPayloadKeys.BODY to "AJO body",
            PushPayloadKeys.AJO_TEMPLATE_PROPERTIES to
                if (templateType == PushTemplateType.AJO_BIG_TEXT) {
                    AJO_MOCKED_BIGTEXT_PROPS_FULL
                } else {
                    AJO_MOCKED_TEMPLATE_PROPS_FIT_CENTER
                },
            PushPayloadKeys.ACTION_TYPE to "DEEPLINK",
            PushPayloadKeys.ACTION_URI to CONTENT_URI,
            PushPayloadKeys.ACTION_BUTTONS to BUTTONS
        )
        overrides.forEach { (key, value) -> payload[key] = value }
        @Suppress("UNCHECKED_CAST")
        return payload.filterValues { it != null } as Map<String, String>
    }

    private fun build(payload: Map<String, String>): Notification? =
        plugin.buildPushTemplateNotification(payload, trackingProvider)

    private fun pendingIntentFor(type: String): PendingIntent =
        pendingIntents[interactions.indexOfFirst { it.type == type }]

    private fun assertFullyWired(notification: Notification) {
        // one click, three buttons, one dismiss
        assertEquals(
            listOf("content_click", "dismiss", "button_click", "button_click", "button_click"),
            interactions.map { it.type }
        )

        val click = interactions.first { it.type == "content_click" }
        assertEquals(CONTENT_URI, click.actionUri)
        assertNull(click.actionId)
        assertSame(pendingIntentFor("content_click"), notification.contentIntent)
        assertSame(pendingIntentFor("dismiss"), notification.deleteIntent)

        val buttons = interactions.filter { it.type == "button_click" }
        assertEquals(listOf("Web URL", "Deeplink", "Open App"), buttons.map { it.actionId })
        assertEquals(listOf(WEB_URI, DEEPLINK_URI, null), buttons.map { it.actionUri })
        assertEquals(listOf("Web URL", "Deeplink", "Open App"), notification.actions.map { it.title.toString() })
        assertEquals(
            pendingIntents.filterIndexed { i, _ -> interactions[i].type == "button_click" },
            notification.actions.map { it.actionIntent }
        )
    }

    // --- happy path: AJO templates build and are fully wired to the host provider ---

    @Test
    fun `ajo_basic builds a notification wired to the host provider`() {
        val notification = build(ajoPayload(PushTemplateType.AJO_BASIC))

        assertNotNull(notification)
        assertFullyWired(notification!!)
    }

    @Test
    fun `ajo_bigtext builds a notification wired to the host provider`() {
        val notification = build(ajoPayload(PushTemplateType.AJO_BIG_TEXT))

        assertNotNull(notification)
        assertFullyWired(notification!!)
    }

    @Test
    fun `no action buttons means no button_click interactions`() {
        val notification = build(ajoPayload(PushTemplateType.AJO_BASIC, PushPayloadKeys.ACTION_BUTTONS to null))!!

        assertEquals(listOf("content_click", "dismiss"), interactions.map { it.type })
        assertNull(notification.actions)
    }

    // --- content click: only uri action types forward the uri ---

    @Test
    fun `content click forwards the uri only for WEBURL and DEEPLINK`() {
        val expected = mapOf(
            "WEBURL" to WEB_URI,
            "DEEPLINK" to WEB_URI,
            "OPENAPP" to null,
            "DISMISS" to null,
            "NONE" to null
        )
        expected.forEach { (actionType, expectedUri) ->
            interactions.clear()
            pendingIntents.clear()
            build(
                ajoPayload(
                    PushTemplateType.AJO_BASIC,
                    PushPayloadKeys.ACTION_TYPE to actionType,
                    PushPayloadKeys.ACTION_URI to WEB_URI
                )
            )
            val click = interactions.first { it.type == "content_click" }
            assertEquals("actionType=$actionType", expectedUri, click.actionUri)
        }
    }

    @Test
    fun `content click without an action type still opens the app with no uri`() {
        val notification = build(
            ajoPayload(PushTemplateType.AJO_BASIC, PushPayloadKeys.ACTION_TYPE to null)
        )!!

        assertNull(interactions.first { it.type == "content_click" }.actionUri)
        assertSame(pendingIntentFor("content_click"), notification.contentIntent)
    }

    // --- stickiness ---

    @Test
    fun `sticky notification is ongoing and does not auto cancel`() {
        listOf(PushTemplateType.AJO_BASIC, PushTemplateType.AJO_BIG_TEXT).forEach { type ->
            val notification = build(ajoPayload(type, PushPayloadKeys.STICKY to "true"))!!

            assertTrue("$type ongoing", notification.flags and Notification.FLAG_ONGOING_EVENT != 0)
            assertFalse("$type auto cancel", notification.flags and Notification.FLAG_AUTO_CANCEL != 0)
        }
    }

    @Test
    fun `non sticky notification auto cancels and is not ongoing`() {
        listOf(PushTemplateType.AJO_BASIC, PushTemplateType.AJO_BIG_TEXT).forEach { type ->
            val notification = build(ajoPayload(type))!!

            assertFalse("$type ongoing", notification.flags and Notification.FLAG_ONGOING_EVENT != 0)
            assertTrue("$type auto cancel", notification.flags and Notification.FLAG_AUTO_CANCEL != 0)
        }
    }

    // --- channel selection ---

    @Test
    fun `payload channel id is used and created with importance from priority`() {
        listOf(PushTemplateType.AJO_BASIC, PushTemplateType.AJO_BIG_TEXT).forEach { type ->
            val channelId = "ajo_channel_${type.value}"
            val notification = build(
                ajoPayload(
                    type,
                    PushPayloadKeys.CHANNEL_ID to channelId,
                    PushPayloadKeys.PRIORITY to "PRIORITY_HIGH"
                )
            )!!

            assertEquals(channelId, notification.channelId)
            assertEquals(
                NotificationManager.IMPORTANCE_HIGH,
                notificationManager.getNotificationChannel(channelId).importance
            )
        }
    }

    @Test
    fun `missing channel id falls back to the default SDK channel`() {
        listOf(PushTemplateType.AJO_BASIC, PushTemplateType.AJO_BIG_TEXT).forEach { type ->
            val notification = build(ajoPayload(type))!!

            assertEquals(PushTemplateConstants.DefaultValues.DEFAULT_CHANNEL_ID, notification.channelId)
            assertNotNull(
                notificationManager.getNotificationChannel(PushTemplateConstants.DefaultValues.DEFAULT_CHANNEL_ID)
            )
        }
    }

    @Test
    fun `existing channel is reused without changing its importance`() {
        val channelId = "existing_channel"
        notificationManager.createNotificationChannel(
            NotificationChannel(channelId, "Existing", NotificationManager.IMPORTANCE_LOW)
        )

        val notification = build(
            ajoPayload(
                PushTemplateType.AJO_BASIC,
                PushPayloadKeys.CHANNEL_ID to channelId,
                PushPayloadKeys.PRIORITY to "PRIORITY_HIGH"
            )
        )!!

        assertEquals(channelId, notification.channelId)
        assertEquals(
            NotificationManager.IMPORTANCE_LOW,
            notificationManager.getNotificationChannel(channelId).importance
        )
    }

    // --- host provider declines interactions ---

    @Test
    fun `provider returning null still builds an unwired notification`() {
        every { trackingProvider.getPendingIntent(any()) } returns null

        val notification = build(ajoPayload(PushTemplateType.AJO_BASIC))

        assertNotNull(notification)
        assertNull(notification!!.contentIntent)
        assertNull(notification.deleteIntent)
        assertNull(notification.actions)
        verify(exactly = 5) { trackingProvider.getPendingIntent(any()) }
    }

    // --- anything that is not a buildable AJO template returns null so the host falls back ---

    @Test
    fun `non AJO template types return null`() {
        listOf(
            PushTemplateType.BASIC.value,
            PushTemplateType.CAROUSEL.value,
            PushTemplateType.TIMER.value,
            "not_a_template"
        ).forEach { templateType ->
            val payload = ajoPayload(PushTemplateType.AJO_BASIC) +
                (PushPayloadKeys.TEMPLATE_TYPE to templateType)

            assertNull("type=$templateType", build(payload))
        }
        verify(exactly = 0) { trackingProvider.getPendingIntent(any()) }
    }

    @Test
    fun `missing template type returns null`() {
        assertNull(build(ajoPayload(PushTemplateType.AJO_BASIC, PushPayloadKeys.TEMPLATE_TYPE to null)))
    }

    @Test
    fun `missing required title or body returns null`() {
        listOf(PushPayloadKeys.TITLE, PushPayloadKeys.BODY).forEach { key ->
            listOf(PushTemplateType.AJO_BASIC, PushTemplateType.AJO_BIG_TEXT).forEach { type ->
                assertNull("$type without $key", build(ajoPayload(type, key to null)))
            }
        }
    }

    @Test
    fun `empty message data returns null`() {
        assertNull(build(emptyMap()))
    }

    @Test
    fun `null application context returns null`() {
        mockApplicationContext(null)

        assertNull(build(ajoPayload(PushTemplateType.AJO_BASIC)))
    }

    @Test
    fun `a throwing provider is contained and returns null`() {
        every { trackingProvider.getPendingIntent(any()) } throws IllegalStateException("boom")

        assertNull(build(ajoPayload(PushTemplateType.AJO_BASIC)))
    }

    @Test
    fun `malformed template properties still build with defaults`() {
        val notification = build(
            ajoPayload(PushTemplateType.AJO_BASIC, PushPayloadKeys.AJO_TEMPLATE_PROPERTIES to "{not json")
        )

        assertNotNull(notification)
    }
}
