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

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import com.adobe.marketing.mobile.notificationbuilder.internal.PendingIntentUtils
import com.adobe.marketing.mobile.services.Log
import com.adobe.marketing.mobile.services.ServiceProvider
import com.adobe.marketing.mobile.util.TimeUtils

/**
 * Public facing object to handle the remind later intent.
 */
object RemindLaterHandler {
    private const val SELF_TAG = "RemindLaterHandler"

    /**
     * Handles the remind later intent by scheduling a [PendingIntent] to the [broadcastReceiverClass]
     * which will be fired at a time specified in the [remindLaterIntent].
     *
     * Once the PendingIntent is fired, the [broadcastReceiverClass] is responsible for
     * reconstructing the notification and displaying it.
     *
     * @param remindLaterIntent [Intent] containing the data needed to schedule and recreate the notification
     * @param broadcastReceiverClass [Class] of the [BroadcastReceiver] that will be fired when the [PendingIntent] resolves at a later time
     */
    @Throws(NotificationConstructionFailedException::class, IllegalArgumentException::class)
    @JvmStatic
    fun handleRemindIntent(
        remindLaterIntent: Intent,
        broadcastReceiverClass: Class<out BroadcastReceiver>?
    ) {
        val context = ServiceProvider.getInstance().appContextService.applicationContext
            ?: throw NotificationConstructionFailedException("Application context is null, cannot schedule notification for later.")

        // get the time for remind later from the intent extras
        val intentExtras = remindLaterIntent.extras
            ?: throw NotificationConstructionFailedException("Intent extras are null, cannot schedule notification for later.")
        val remindLaterTimestamp =
            intentExtras.getString(com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.REMIND_LATER_TIMESTAMP)?.toLongOrNull() ?: 0
        val remindLaterDuration =
            intentExtras.getString(com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.REMIND_LATER_DURATION)?.toLongOrNull() ?: 0

        // calculate difference in fire date from the current date if timestamp is provided
        val secondsUntilFireDate: Long = if (remindLaterDuration > 0) remindLaterDuration
        else remindLaterTimestamp - TimeUtils.getUnixTimeInSeconds()

        val notificationManager = NotificationManagerCompat.from(context)
        val tag = intentExtras.getString(com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.TAG)

        // if fire date is greater than 0 then we want to schedule a reminder notification.
        if (secondsUntilFireDate <= 0) {
            tag?.let { notificationManager.cancel(tag.hashCode()) }
            throw IllegalArgumentException("Remind later timestamp or duration is less than or equal to current timestamp, cannot schedule notification for later.")
        }
        Log.trace(com.adobe.ui_utils.PushTemplateConstants.LOG_TAG, SELF_TAG, "Remind later pressed, will reschedule the notification to be displayed $secondsUntilFireDate seconds from now")

        // calculate the trigger time
        val triggerTimeInSeconds: Long = if (remindLaterDuration > 0) remindLaterDuration + TimeUtils.getUnixTimeInSeconds()
        else remindLaterTimestamp

        // schedule a pending intent to be broadcast at the specified timestamp
        if (broadcastReceiverClass == null) {
            Log.warning(
                com.adobe.ui_utils.PushTemplateConstants.LOG_TAG,
                SELF_TAG,
                "Broadcast receiver class is null, cannot schedule notification for later."
            )
            tag?.let { notificationManager.cancel(tag.hashCode()) }
            return
        }
        val scheduledIntent = Intent(com.adobe.ui_utils.PushTemplateConstants.IntentActions.SCHEDULED_NOTIFICATION_BROADCAST)
        scheduledIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        scheduledIntent.putExtras(intentExtras)
        PendingIntentUtils.scheduleNotification(context, scheduledIntent, broadcastReceiverClass, triggerTimeInSeconds)

        // cancel the displayed notification
        tag?.let { notificationManager.cancel(tag.hashCode()) }
    }
}
