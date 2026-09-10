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

// NOTE: This IUiTemplatePlugin implementation is temporarily commented out because the plugin API
// (com.adobe.marketing.mobile.plugin.IUiTemplatePlugin) is not present in the currently resolved
// Core dependency, so it would not compile. Restore it once Core exposes the plugin API.
/*
import android.app.Activity
import android.app.Notification
import android.content.BroadcastReceiver
import android.content.Context
import com.adobe.marketing.mobile.plugin.IUiTemplatePlugin
import com.adobe.marketing.mobile.services.Log

/**
 * [IUiTemplatePlugin] implementation for AJO push templates. Register it once at app startup with
 * `MobileCore.addPlugins(NotificationBuilderPlugin())`; a host SDK (for example Messaging) then
 * resolves it via `MobileCore.getPlugin(IUiTemplatePlugin::class.java)` and asks it to build the
 * template notification.
 *
 * This add-on depends only on Core - it does not depend on the host SDK. It builds and returns the
 * [Notification]; the host owns posting and tracking.
 */
class NotificationBuilderPlugin : IUiTemplatePlugin {

    override fun buildPushTemplateNotification(
        context: Context,
        messageData: Map<String, String>,
        trackerActivityClass: Class<out Activity>?,
        broadcastReceiverClass: Class<out BroadcastReceiver>?
    ): Notification? {
        return try {
            NotificationBuilder.constructNotificationBuilder(
                messageData,
                trackerActivityClass,
                broadcastReceiverClass
            ).build()
        } catch (t: Throwable) {
            // Failure isolation: never crash the host's FCM callback. Returning null lets the host
            // fall back to a basic notification.
            Log.warning(
                "NotificationBuilder",
                "NotificationBuilderPlugin",
                "Failed to build push-template notification: ${t.message}. Host will fall back."
            )
            null
        }
    }
}
*/
