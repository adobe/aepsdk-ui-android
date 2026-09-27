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

import android.app.Notification
import com.adobe.marketing.mobile.plugin.IPushTemplateTrackingProvider
import com.adobe.marketing.mobile.plugin.IUiTemplatePlugin
import com.adobe.marketing.mobile.services.Log

/**
 * [IUiTemplatePlugin] implementation for AJO push templates. Register it once at app startup with
 * `MobileCore.addPlugins(NotificationBuilderPlugin())`; a host SDK (for example Messaging) then
 * resolves it via `MobileCore.getPlugin(IUiTemplatePlugin::class.java)` and asks it to build the
 * template notification.
 *
 * This add-on depends only on Core - it does not depend on the host SDK. It **renders** the
 * [Notification] and obtains every tracking [android.app.PendingIntent] from the host-supplied
 * [IPushTemplateTrackingProvider], so the host keeps ownership of tracking and intent handling.
 */
class NotificationBuilderPlugin : IUiTemplatePlugin {

    private companion object {
        const val SELF_TAG = "NotificationBuilderPlugin"
    }

    /**
     * Builds an AJO push-template notification (AJO Basic / AJO Big Text). Any other template type
     * returns `null` so the host falls back to a basic notification.
     *
     * The host also calls this for re-render with the template state merged into [messageData]. AJO
     * Basic / Big Text never request a re-render, so they are only ever built for the first render.
     */
    override fun buildPushTemplateNotification(
        messageData: Map<String, String>,
        trackingProvider: IPushTemplateTrackingProvider
    ): Notification? {
        return try {
            NotificationBuilder.buildAJOTemplateNotification(messageData, trackingProvider)?.build()
        } catch (t: Throwable) {
            // Failure isolation: never crash the host's FCM callback. Returning null lets the host
            // fall back to a basic notification.
            Log.warning(
                PushTemplateConstants.LOG_TAG,
                SELF_TAG,
                "Failed to build push-template notification: ${t.message}. Host will fall back."
            )
            null
        }
    }
}
