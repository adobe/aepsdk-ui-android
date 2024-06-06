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

package com.adobe.marketing.mobile.notificationbuilder.internal

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import com.adobe.marketing.mobile.notificationbuilder.PushTemplateConstants
import com.adobe.marketing.mobile.services.Log
import java.util.Random

internal object PendingIntentUtils {

    private const val SELF_TAG = "PendingIntentUtils"

    internal fun scheduleNotification(
        context: Context,
        scheduledIntent: Intent,
        broadcastReceiverClass: Class<out BroadcastReceiver>?,
        triggerAtSeconds: Long,
    ) {
        broadcastReceiverClass?.let {
            scheduledIntent.setClass(context, broadcastReceiverClass)
        }

        val pendingIntent: PendingIntent = PendingIntent.getBroadcast(
            context,
            Random().nextInt(),
            scheduledIntent,
            PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager? ?: return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
            isExactAlarmsAllowed(alarmManager)
        ) {
            Log.trace(
                PushTemplateConstants.LOG_TAG,
                SELF_TAG,
                "Exact alarms are permitted, scheduling an exact alarm for the current notification."
            )
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP, triggerAtSeconds * 1000, pendingIntent
            )
        } else {
            // schedule an inexact alarm for the current notification
            Log.trace(
                PushTemplateConstants.LOG_TAG,
                SELF_TAG,
                "Exact alarms are not permitted, scheduling an inexact alarm for the current notification."
            )
            alarmManager[AlarmManager.RTC_WAKEUP, triggerAtSeconds * 1000] =
                pendingIntent
        }
    }

    /**
     * Checks if exact alarms are allowed on the device
     *
     * @param alarmManager [AlarmManager] instance
     * @return true if exact alarms are allowed, false otherwise
     */
    internal fun isExactAlarmsAllowed(alarmManager: AlarmManager?): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.S ||
            alarmManager?.canScheduleExactAlarms() ?: false
    }

    /**
     * Creates a pending intent for a notification.
     *
     * @param context the application [Context]
     * @param trackerActivityClass the [Class] of the activity to set in the created pending intent for tracking purposes
     * notification
     * @param actionUri the action uri
     * @param actionID the action ID
     * @param intentExtras the [Bundle] containing the extras to be added to the intent
     * @return the created [PendingIntent]
     */
    internal fun createPendingIntentForTrackerActivity(
        context: Context,
        trackerActivityClass: Class<out Activity>?,
        actionUri: String?,
        actionID: String?,
        intentExtras: Bundle?
    ): PendingIntent? {
        val intent = Intent(PushTemplateConstants.NotificationAction.CLICKED)
        trackerActivityClass?.let {
            intent.setClass(context.applicationContext, trackerActivityClass)
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        // todo revisit if all data is needed for click actions
        intentExtras?.let { intent.putExtras(intentExtras) }
        addActionDetailsToIntent(
            intent,
            actionUri,
            actionID
        )

        return PendingIntent.getActivity(
            context,
            Random().nextInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    /**
     * Adds action details to the provided [Intent].
     *
     * @param intent the intent
     * @param actionUri [String] containing the action uri
     * @param actionId `String` containing the action ID
     */
    private fun addActionDetailsToIntent(
        intent: Intent,
        actionUri: String?,
        actionId: String?
    ) {
        if (!actionUri.isNullOrEmpty()) {
            intent.putExtra(PushTemplateConstants.TrackingKeys.ACTION_URI, actionUri)
        }
        if (!actionId.isNullOrEmpty()) {
            intent.putExtra(PushTemplateConstants.TrackingKeys.ACTION_ID, actionId)
        }
    }
}
