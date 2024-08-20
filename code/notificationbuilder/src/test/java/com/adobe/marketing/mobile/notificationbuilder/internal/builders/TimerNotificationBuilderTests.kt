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
import android.app.AlarmManager
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.os.Build
import android.service.notification.StatusBarNotification
import android.widget.RemoteViews
import com.adobe.marketing.mobile.notificationbuilder.NotificationConstructionFailedException
import com.adobe.marketing.mobile.notificationbuilder.internal.PendingIntentUtils
import com.adobe.marketing.mobile.notificationbuilder.internal.extensions.setRemoteViewImage
import com.adobe.marketing.mobile.notificationbuilder.internal.extensions.setTimerTextColor
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.MOCKED_ALT_BODY
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.MOCKED_ALT_EXPANDED_BODY
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.MOCKED_ALT_IMAGE_URI
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.MOCKED_ALT_TITLE
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.MOCKED_BODY
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.MOCKED_EXPANDED_BODY
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.MOCKED_IMAGE_URI
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.MOCKED_TIMER_COLOR
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.MOCKED_TITLE
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.TimerPushTemplate
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.provideMockedTimerTemplate
import com.adobe.marketing.mobile.utils.PushTemplateImageUtils
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkClass
import io.mockk.mockkConstructor
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.verify
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.util.ReflectionHelpers
import kotlin.test.assertNull

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [31])
class TimerNotificationBuilderTests {

    private lateinit var context: Context
    private lateinit var pushTemplate: TimerPushTemplate
    private lateinit var trackerActivityClass: Class<out Activity>
    private lateinit var broadcastReceiverClass: Class<out BroadcastReceiver>
    private lateinit var alarmManager: AlarmManager
    private lateinit var notificationManager: NotificationManager

    @Before
    fun setUp() {
        context = mockk<Context>(relaxed = true)
        alarmManager = mockk<AlarmManager>(relaxed = true)
        notificationManager = mockk<NotificationManager>(relaxed = true)
        every { alarmManager.canScheduleExactAlarms() } returns true
        every { context.getSystemService(Context.NOTIFICATION_SERVICE) } returns notificationManager
        every { context.getSystemService(Context.ALARM_SERVICE) } returns alarmManager
        every { context.packageName } answers { callOriginal() }
        pushTemplate = provideMockedTimerTemplate(false, true)
        trackerActivityClass = mockkClass(Activity::class, relaxed = true).javaClass
        broadcastReceiverClass = mockkClass(BroadcastReceiver::class, relaxed = true).javaClass
        mockkObject(PushTemplateImageUtils)
        mockkObject(PendingIntentUtils)
        mockkConstructor(RemoteViews::class)
        mockkStatic(RemoteViews::setTimerTextColor)
        mockkStatic(RemoteViews::setRemoteViewImage)
        every { anyConstructed<RemoteViews>().setTextViewText(any(), any()) } just Runs
        every { anyConstructed<RemoteViews>().setImageViewBitmap(any(), any()) } just Runs
    }

    @Test(expected = NotificationConstructionFailedException::class)
    fun `construct throws exception when schedule exact alarm permission not given`() {
        every { alarmManager.canScheduleExactAlarms() } returns false
        val result = TimerNotificationBuilder.construct(
            context,
            pushTemplate,
            trackerActivityClass,
            broadcastReceiverClass
        )
        assertNull(result)
    }

    @Test(expected = NotificationConstructionFailedException::class)
    fun `construct throws exception when API is less than 24`() {
        ReflectionHelpers.setStaticField(Build.VERSION::class.java, "SDK_INT", 23)
        val result = TimerNotificationBuilder.construct(
            context,
            pushTemplate,
            trackerActivityClass,
            broadcastReceiverClass
        )
        assertNull(result)
    }

    @Test
    fun `construct returns NotificationCompat Builder for valid inputs`() {
        val result = TimerNotificationBuilder.construct(
            context,
            pushTemplate,
            trackerActivityClass,
            broadcastReceiverClass
        )
        assertNotNull(result)
        verify(exactly = 4) { // 4 calls expected, 2 from TimerNotificationBuilder and 2 from AEPPushNotificationBuilder
            anyConstructed<RemoteViews>().setTextViewText(
                any(),
                MOCKED_TITLE
            )
        }
        verify(exactly = 2) { // 2 calls expected, 1 from TimerNotificationBuilder and 1 from AEPPushNotificationBuilder
            anyConstructed<RemoteViews>().setTextViewText(
                any(),
                MOCKED_BODY
            )
        }
        verify(exactly = 2) { // 2 calls expected, 1 from TimerNotificationBuilder and 1 from AEPPushNotificationBuilder
            anyConstructed<RemoteViews>().setTextViewText(
                any(),
                MOCKED_EXPANDED_BODY
            )
        }
        verify(exactly = 1) {
            (RemoteViews::setRemoteViewImage)(
                any(),
                MOCKED_IMAGE_URI,
                any()
            )
        }
        val expectedTime: Long = (60 * 1000) // using the duration present in the TimerTemplate
        verify(exactly = 2) {
            anyConstructed<RemoteViews>().setChronometer(
                any(),
                range(expectedTime - 5000, expectedTime + 5000, true, true),
                any(),
                true
            )
        }
        verify(exactly = 2) {
            (RemoteViews::setTimerTextColor)(
                any(),
                MOCKED_TIMER_COLOR,
                any()
            )
        }
    }

    @Test
    fun `construct returns NotificationCompat Builder containing alternate message content when timer is expired`() {
        // using a negative duration to guarantee that the timer is expired
        pushTemplate = provideMockedTimerTemplate(false, true, "-10")
        val notification: StatusBarNotification = mockkClass(StatusBarNotification::class)
        every { notification.id } returns pushTemplate.tag.hashCode()
        val notifications = arrayOf(notification)
        every { notificationManager.activeNotifications } returns notifications
        val result = TimerNotificationBuilder.construct(
            context,
            pushTemplate,
            trackerActivityClass,
            broadcastReceiverClass
        )
        assertNotNull(result)
        verify(exactly = 2) {
            anyConstructed<RemoteViews>().setTextViewText(
                any(),
                MOCKED_ALT_TITLE
            )
        }
        verify(exactly = 1) {
            anyConstructed<RemoteViews>().setTextViewText(
                any(),
                MOCKED_ALT_BODY
            )
        }
        verify(exactly = 1) {
            anyConstructed<RemoteViews>().setTextViewText(
                any(),
                MOCKED_ALT_EXPANDED_BODY
            )
        }
        verify(exactly = 1) {
            (RemoteViews::setRemoteViewImage)(
                any(),
                MOCKED_ALT_IMAGE_URI,
                any()
            )
        }
        verify(exactly = 0) {
            anyConstructed<RemoteViews>().setChronometer(
                any(),
                any(),
                any(),
                any()
            )
        }
        verify(exactly = 0) {
            (RemoteViews::setTimerTextColor)(
                any(),
                any(),
                any()
            )
        }
    }

    @Test(expected = NotificationConstructionFailedException::class)
    fun `construct throws exception when timer is expired but notification with a matching tag is not found`() {
        // using a negative duration to guarantee that the timer is expired
        pushTemplate = provideMockedTimerTemplate(false, true, "-10")
        val notification: StatusBarNotification = mockkClass(StatusBarNotification::class)
        every { notification.id } returns -1111
        val notifications = arrayOf(notification)
        every { notificationManager.activeNotifications } returns notifications
        val result = TimerNotificationBuilder.construct(
            context,
            pushTemplate,
            trackerActivityClass,
            broadcastReceiverClass
        )
        assertNull(result)
    }

    @Test(expected = NotificationConstructionFailedException::class)
    fun `construct throws exception when timer is expired but no notification is currently displayed`() {
        // using a negative duration to guarantee that the timer is expired
        pushTemplate = provideMockedTimerTemplate(false, true, "-10")
        val notifications = emptyArray<StatusBarNotification>()
        every { notificationManager.activeNotifications } returns notifications
        val result = TimerNotificationBuilder.construct(
            context,
            pushTemplate,
            trackerActivityClass,
            broadcastReceiverClass
        )
        assertNull(result)
    }

    @Test
    fun `construct returns NotificationCompat Builder and an alarm is set if duration present in TimerTemplate`() {
        val result = TimerNotificationBuilder.construct(
            context,
            pushTemplate,
            trackerActivityClass,
            broadcastReceiverClass
        )
        assertNotNull(result)
        val expectedTime =
            System.currentTimeMillis() + (60 * 1000) // current time + 60 seconds in milliseconds
        verify(exactly = 1) {
            alarmManager.setExactAndAllowWhileIdle(
                any(),
                range(System.currentTimeMillis(), expectedTime, true, true),
                any()
            )
        }
    }

    @Test
    fun `construct returns NotificationCompat Builder and an alarm is set using future timestamp if no duration in TimerTemplate`() {
        pushTemplate = provideMockedTimerTemplate(true, false)
        val result = TimerNotificationBuilder.construct(
            context,
            pushTemplate,
            trackerActivityClass,
            broadcastReceiverClass
        )
        assertNotNull(result)
        val expectedTime =
            2665428926 * 1000 // Thursday, June 18, 2054 8:55:26 PM GMT in milliseconds
        verify(exactly = 1) { alarmManager.setExactAndAllowWhileIdle(any(), expectedTime, any()) }
    }
}
