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

import android.content.BroadcastReceiver
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import com.adobe.marketing.mobile.notificationbuilder.internal.PendingIntentUtils
import com.adobe.marketing.mobile.services.ServiceProvider
import io.mockk.every
import io.mockk.mockk
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

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [31])
class RemindLaterHandlerTests {

    private lateinit var mockBroadcastReceiverClass: Class<out BroadcastReceiver>
    private lateinit var mockedNotificationManagerCompat: NotificationManagerCompat

    @Before
    fun setup() {
        mockBroadcastReceiverClass = BroadcastReceiver::class.java
        mockkStatic(ServiceProvider::class)
        mockkObject(PendingIntentUtils)
        val context = RuntimeEnvironment.getApplication()
        every { ServiceProvider.getInstance().appContextService.applicationContext } returns context
        every { ServiceProvider.getInstance().loggingService } returns mockk(relaxed = true)
        mockedNotificationManagerCompat = mockk(relaxed = true)
        mockkStatic(NotificationManagerCompat::class)
        every { NotificationManagerCompat.from(context) } returns mockedNotificationManagerCompat
        every { PendingIntentUtils.scheduleNotification(any(), any(), any(), any()) } returns Unit
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `handleRemindIntent should schedule notification when valid remindLaterDuration and broadcastReceiverClass are provided`() {
        RemindLaterHandler.handleRemindIntent(getRemindLaterIntentDuration(), mockBroadcastReceiverClass)

        verify { PendingIntentUtils.scheduleNotification(any(), any(), any(), any()) }
        verify(atLeast = 1) { mockedNotificationManagerCompat.cancel(any()) }
    }

    @Test
    fun `handleRemindIntent should schedule notification when valid remindLaterTimestamp and broadcastReceiverClass are provided`() {
        RemindLaterHandler.handleRemindIntent(getRemindLaterIntentTimestamp(), mockBroadcastReceiverClass)

        verify { PendingIntentUtils.scheduleNotification(any(), any(), any(), any()) }
        verify(atLeast = 1) { mockedNotificationManagerCompat.cancel(any()) }
    }

    @Test(expected = NotificationConstructionFailedException::class)
    fun `handleRemindIntent should throw NotificationConstructionFailedException when applicationContext is null`() {
        every { ServiceProvider.getInstance().appContextService.applicationContext } returns null

        RemindLaterHandler.handleRemindIntent(getRemindLaterIntentDuration(), mockBroadcastReceiverClass)

        verify(exactly = 0) { PendingIntentUtils.scheduleNotification(any(), any(), any(), any()) }
        verify(exactly = 0) { mockedNotificationManagerCompat.cancel(any()) }
    }

    @Test(expected = NotificationConstructionFailedException::class)
    fun `handleRemindIntent should throw NotificationConstructionFailedException when intent extras are null`() {

        RemindLaterHandler.handleRemindIntent(Intent(), mockBroadcastReceiverClass)

        verify(exactly = 0) { PendingIntentUtils.scheduleNotification(any(), any(), any(), any()) }
        verify(exactly = 0) { mockedNotificationManagerCompat.cancel(any()) }
    }

    @Test(expected = IllegalArgumentException::class)
    fun `handleRemindIntent should throw IllegalArgumentException when remindLaterDuration is less than or equal to current timestamp`() {
        val remindLaterIntent = getRemindLaterIntentDuration()
        remindLaterIntent.putExtra(PushTemplateConstants.PushPayloadKeys.REMIND_LATER_DURATION, "-20")

        RemindLaterHandler.handleRemindIntent(remindLaterIntent, mockBroadcastReceiverClass)

        verify(exactly = 0) { PendingIntentUtils.scheduleNotification(any(), any(), any(), any()) }
        verify(atLeast = 1) { mockedNotificationManagerCompat.cancel(any()) }
    }

    @Test(expected = IllegalArgumentException::class)
    fun `handleRemindIntent should throw IllegalArgumentException when remindLaterDuration is invalid`() {
        val remindLaterIntent = getRemindLaterIntentDuration()
        remindLaterIntent.putExtra(PushTemplateConstants.PushPayloadKeys.REMIND_LATER_DURATION, "invalid")

        RemindLaterHandler.handleRemindIntent(remindLaterIntent, mockBroadcastReceiverClass)

        verify(exactly = 0) { PendingIntentUtils.scheduleNotification(any(), any(), any(), any()) }
        verify(atLeast = 1) { mockedNotificationManagerCompat.cancel(any()) }
    }

    @Test(expected = IllegalArgumentException::class)
    fun `handleRemindIntent should schedule notification when valid remindLaterTimestamp is less than or equal to current timestamp`() {
        val remindLaterIntent = getRemindLaterIntentTimestamp()
        remindLaterIntent.putExtra(PushTemplateConstants.PushPayloadKeys.REMIND_LATER_TIMESTAMP, (System.currentTimeMillis() / 1000L - 10).toString())

        RemindLaterHandler.handleRemindIntent(remindLaterIntent, mockBroadcastReceiverClass)

        verify(exactly = 0) { PendingIntentUtils.scheduleNotification(any(), any(), any(), any()) }
        verify(atLeast = 1) { mockedNotificationManagerCompat.cancel(any()) }
    }

    @Test(expected = IllegalArgumentException::class)
    fun `handleRemindIntent should schedule notification when remindLaterTimestamp is invalid`() {
        val remindLaterIntent = getRemindLaterIntentTimestamp()
        remindLaterIntent.putExtra(PushTemplateConstants.PushPayloadKeys.REMIND_LATER_TIMESTAMP, "invalid")

        RemindLaterHandler.handleRemindIntent(remindLaterIntent, mockBroadcastReceiverClass)

        verify(exactly = 0) { PendingIntentUtils.scheduleNotification(any(), any(), any(), any()) }
        verify(atLeast = 1) { mockedNotificationManagerCompat.cancel(any()) }
    }

    @Test
    fun `handleRemindIntent should not schedule notification when broadcastReceiverClass is null`() {
        RemindLaterHandler.handleRemindIntent(getRemindLaterIntentDuration(), null)

        verify(exactly = 0) { PendingIntentUtils.scheduleNotification(any(), any(), any(), any()) }
        verify(atLeast = 1) { mockedNotificationManagerCompat.cancel(any()) }
    }

    @Test
    fun `handleRemindIntent should not schedule notification when tag is null`() {
        val remindLaterIntent = getRemindLaterIntentDuration()
        remindLaterIntent.removeExtra(PushTemplateConstants.PushPayloadKeys.TAG)
        RemindLaterHandler.handleRemindIntent(remindLaterIntent, mockBroadcastReceiverClass)

        verify(atLeast = 1) { PendingIntentUtils.scheduleNotification(any(), any(), any(), any()) }
        verify(exactly = 0) { mockedNotificationManagerCompat.cancel(any()) }
    }

    private fun getRemindLaterIntentDuration(): Intent {
        val remindLaterIntent = Intent()
        remindLaterIntent.putExtra(PushTemplateConstants.PushPayloadKeys.REMIND_LATER_DURATION, "20")
        remindLaterIntent.putExtra(PushTemplateConstants.PushPayloadKeys.TAG, "testTag")
        return remindLaterIntent
    }

    private fun getRemindLaterIntentTimestamp(): Intent {
        val remindLaterIntent = Intent()
        remindLaterIntent.putExtra(PushTemplateConstants.PushPayloadKeys.REMIND_LATER_TIMESTAMP, (System.currentTimeMillis() / 1000L + 10).toString())
        remindLaterIntent.putExtra(PushTemplateConstants.PushPayloadKeys.TAG, "testTag")
        return remindLaterIntent
    }
}
