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

import androidx.core.app.NotificationCompat
import com.adobe.marketing.mobile.notificationbuilder.PushTemplateConstants
import com.adobe.marketing.mobile.notificationbuilder.PushTemplateConstants.PushInteractionType
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.BasicPushTemplate
import com.adobe.marketing.mobile.plugin.IPushTemplateTrackingProvider
import com.adobe.marketing.mobile.plugin.PushInteraction
import com.adobe.marketing.mobile.services.Log

/**
 * AJO-only `NotificationCompat.Builder` extensions that obtain the interaction
 * [android.app.PendingIntent]s (content tap, action buttons, dismiss) from the host-supplied
 * [IPushTemplateTrackingProvider] instead of building them locally.
 *
 * These are separate from the shared (AEP/ACC) click/delete/button extensions so the host owns
 * tracking and intent handling for AJO templates while this add-on owns rendering. AJO Basic / Big
 * Text carry no template state, so no `templateExtras` are sent.
 *
 * If the host returns `null` (an interaction type it does not handle), the action is left unwired.
 */

private const val SELF_TAG = "AJONotificationCompatBuilderExtensions"

/**
 * Sets the content-tap action for an AJO notification using the host [trackingProvider].
 *
 * @param trackingProvider the host's [IPushTemplateTrackingProvider]
 * @param actionUri the content action uri, forwarded only for [PushTemplateConstants.ActionType.DEEPLINK]
 *     / [PushTemplateConstants.ActionType.WEBURL]; `null` otherwise so the host opens the app
 * @param actionType the [PushTemplateConstants.ActionType] of the content action
 */
internal fun NotificationCompat.Builder.setAJONotificationClickAction(
    trackingProvider: IPushTemplateTrackingProvider,
    actionUri: String?,
    actionType: PushTemplateConstants.ActionType?
): NotificationCompat.Builder {
    val uri = actionUri.takeIf { isUriAction(actionType) }
    val pendingIntent = trackingProvider.getPendingIntent(
        PushInteraction(PushInteractionType.CONTENT_CLICK, actionUri = uri)
    )
    pendingIntent?.let { setContentIntent(it) } ?: logUnwired(PushInteractionType.CONTENT_CLICK)
    return this
}

/**
 * Sets the dismiss (delete) action for an AJO notification using the host [trackingProvider].
 *
 * @param trackingProvider the host's [IPushTemplateTrackingProvider]
 */
internal fun NotificationCompat.Builder.setAJONotificationDeleteAction(
    trackingProvider: IPushTemplateTrackingProvider
): NotificationCompat.Builder {
    val pendingIntent = trackingProvider.getPendingIntent(PushInteraction(PushInteractionType.DISMISS))
    pendingIntent?.let { setDeleteIntent(it) } ?: logUnwired(PushInteractionType.DISMISS)
    return this
}

/**
 * Adds the AJO action buttons, obtaining each button's [android.app.PendingIntent] from the host
 * [trackingProvider]. A button the host returns no PendingIntent for is not added.
 *
 * @param trackingProvider the host's [IPushTemplateTrackingProvider]
 * @param actionButtons the list of [BasicPushTemplate.ActionButton] to attach
 */
internal fun NotificationCompat.Builder.addAJOActionButtons(
    trackingProvider: IPushTemplateTrackingProvider,
    actionButtons: List<BasicPushTemplate.ActionButton>?
): NotificationCompat.Builder {
    if (actionButtons.isNullOrEmpty()) {
        return this
    }
    for (eachButton in actionButtons) {
        val uri = eachButton.link.takeIf { isUriAction(eachButton.type) }
        val pendingIntent = trackingProvider.getPendingIntent(
            PushInteraction(PushInteractionType.BUTTON_CLICK, actionUri = uri, actionId = eachButton.label)
        )
        if (pendingIntent == null) {
            logUnwired(PushInteractionType.BUTTON_CLICK)
            continue
        }
        addAction(0, eachButton.label, pendingIntent)
    }
    return this
}

/**
 * Whether the [actionType] carries an action uri (deeplink or web url) that should be forwarded to
 * the host; [PushTemplateConstants.ActionType.OPENAPP] / [PushTemplateConstants.ActionType.NONE] /
 * [PushTemplateConstants.ActionType.DISMISS] carry none (a `null` uri opens the app).
 */
private fun isUriAction(actionType: PushTemplateConstants.ActionType?): Boolean =
    actionType == PushTemplateConstants.ActionType.DEEPLINK ||
        actionType == PushTemplateConstants.ActionType.WEBURL

private fun logUnwired(type: String) {
    Log.debug(
        PushTemplateConstants.LOG_TAG,
        SELF_TAG,
        "Host returned no PendingIntent for interaction '$type'; leaving it unwired."
    )
}
