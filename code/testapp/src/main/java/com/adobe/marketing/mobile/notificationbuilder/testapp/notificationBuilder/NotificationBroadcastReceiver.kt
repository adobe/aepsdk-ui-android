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

package com.adobe.marketing.mobile.notificationbuilder.testapp.notificationBuilder

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import com.adobe.marketing.mobile.notificationbuilder.NotificationBuilder
import com.adobe.marketing.mobile.notificationbuilder.NotificationConstructionFailedException
import com.adobe.marketing.mobile.notificationbuilder.testapp.AppConstants.NotificationBuilderConstants
import com.adobe.marketing.mobile.util.StringUtils

class NotificationBroadcastReceiver : BroadcastReceiver() {
    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (StringUtils.isNullOrEmpty(action)) {
            return
        }

        val notificationManager = NotificationManagerCompat.from(context)
        val tag = intent.getStringExtra(NotificationBuilderConstants.TAG)
        try {

            when (action) {
                "remind_clicked" -> {}
                "scheduled_notification_broadcast" -> {
                    // TODO: Implement logic for scheduled notification
                }
                else -> {
                    val builder = NotificationBuilder.constructNotificationBuilder(intent, NotificationTrackerActivity::class.java ,
                        NotificationBroadcastReceiver::class.java)
                    notificationManager.notify(tag.hashCode(), builder.build())
                }
            }


        } catch (e: NotificationConstructionFailedException) {
            e.printStackTrace()
        }
    }
}