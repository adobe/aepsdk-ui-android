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

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import com.adobe.marketing.mobile.notificationbuilder.PushTemplateConstants
import com.adobe.marketing.mobile.services.ServiceProvider

class NotificationTrackerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        when (intent?.action) {
            PushTemplateConstants.NotificationAction.CLICKED -> executePushAction(intent)
            else -> {}
        }

        finish()
        return
    }

    private fun executePushAction(intent: Intent) {
        val actionUri =
            intent.getStringExtra(PushTemplateConstants.TrackingKeys.ACTION_URI)
        if (actionUri.isNullOrEmpty()) {
            openApplication()
        } else {
            openUri(actionUri)
        }

        // remove the notification if sticky notifications are false
        val isStickyNotification = intent.getStringExtra(PushTemplateConstants.PushPayloadKeys.STICKY)?.toBoolean() ?: false
        if (isStickyNotification) {
            return
        }
        val tag = intent.getStringExtra(PushTemplateConstants.PushPayloadKeys.TAG)
        val context = ServiceProvider.getInstance().appContextService.applicationContext ?: return
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.cancel(tag.hashCode())
    }

    private fun openApplication() {
        val currentActivity = ServiceProvider.getInstance().appContextService.currentActivity
        val launchIntent = if (currentActivity != null) {
            Intent(currentActivity, currentActivity.javaClass)
        } else {
            packageManager.getLaunchIntentForPackage(packageName)
        }
        if (launchIntent != null) {
            launchIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            if (intent.extras != null) {
                launchIntent.putExtras(intent.extras!!)
            }
            startActivity(launchIntent)
        }
    }

    /**
     * Use this method to create an intent to open the the provided URI.
     *
     * @param uri the uri to open
     */
    private fun openUri(uri: String) {
        try {
            val deeplinkIntent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            deeplinkIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            if (intent.extras != null) {
                deeplinkIntent.putExtras(intent.extras!!)
            }
            startActivity(deeplinkIntent)
        } catch (e: ActivityNotFoundException) {

        }
    }
}