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

package com.adobe.marketing.mobile.notificationbuilder.internal.extensions

import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Bundle
import androidx.core.app.NotificationCompat
import com.adobe.marketing.mobile.MobileCore
import com.adobe.marketing.mobile.notificationbuilder.PushTemplateConstants
import com.adobe.marketing.mobile.notificationbuilder.PushTemplateConstants.DefaultValues.CAROUSEL_MAX_BITMAP_HEIGHT
import com.adobe.marketing.mobile.notificationbuilder.PushTemplateConstants.DefaultValues.CAROUSEL_MAX_BITMAP_WIDTH
import com.adobe.marketing.mobile.notificationbuilder.PushTemplateConstants.LOG_TAG
import com.adobe.marketing.mobile.notificationbuilder.internal.PendingIntentUtils
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.BasicPushTemplate
import com.adobe.marketing.mobile.services.Log
import com.adobe.marketing.mobile.utils.AEPUIImageConfig
import com.adobe.marketing.mobile.utils.AEPUIImageUtils
import java.util.Random

private const val SELF_TAG = "RemoteViewExtensions"

/**
 * Sets the small icon for the notification. If a small icon is received from the payload, the
 * same is used. If a small icon is not received from the payload, we use the icon set using
 * MobileCore.setSmallIcon(). If a small icon is not set using MobileCore.setSmallIcon(), we use
 * the default small icon of the application.
 *
 * @param context the application [Context]
 * @param smallIcon `String` containing the small icon to use
 * @param iconColor `String` containing the small icon color code to use
 */
internal fun NotificationCompat.Builder.setSmallIcon(
    context: Context,
    smallIcon: String?,
    iconColor: String?
): NotificationCompat.Builder {
    val iconFromPayload = context.getIconWithResourceName(smallIcon)
    val iconFromMobileCore = MobileCore.getSmallIconResourceID()
    val iconResourceId: Int
    if (isValidIcon(iconFromPayload)) {
        iconResourceId = iconFromPayload
    } else if (isValidIcon(iconFromMobileCore)) {
        iconResourceId = iconFromMobileCore
    } else {
        val iconFromApp = context.getDefaultAppIcon()
        if (isValidIcon(iconFromApp)) {
            iconResourceId = iconFromApp
        } else {
            Log.warning(
                LOG_TAG,
                SELF_TAG,
                "No valid small icon found. Notification will not be displayed."
            )
            return this
        }
    }
    setSmallIcon(iconResourceId)
    setSmallIconColor(iconColor)
    return this
}

/**
 * Sets a custom color to the notification's small icon.
 *
 * @param iconColorHex `String` containing a color code to be used in customizing the
 * small icon color
 */
private fun NotificationCompat.Builder.setSmallIconColor(
    iconColorHex: String?
) {
    if (iconColorHex.isNullOrEmpty()) {
        Log.trace(
            LOG_TAG,
            SELF_TAG,
            "Empty icon color hex string found, custom color will not be applied to the notification icon."
        )
        return
    }

    try {
        // sets the icon color if provided
        val color = Color.parseColor("#$iconColorHex")
        setColorized(true).color = color
    } catch (exception: IllegalArgumentException) {
        Log.warning(
            LOG_TAG,
            SELF_TAG,
            "Unrecognized hex string passed to Color.parseColor(), custom color will not be applied to the notification icon."
        )
    }
    return
}

/**
 * Sets the sound for the legacy style notification or notification on a device less than API 25.
 * If a sound is received from the payload, the same is used.
 * If a sound is not received from the payload, the default sound is used.
 *
 * @param context the application [Context]
 * @param customSound [String] containing the custom sound file name to load from the
 * bundled assets
 */
internal fun NotificationCompat.Builder.setSound(
    context: Context,
    customSound: String?
): NotificationCompat.Builder {
    if (customSound.isNullOrEmpty()) {
        Log.trace(
            LOG_TAG,
            SELF_TAG,
            "No custom sound found in the push template, using the default notification sound."
        )
        setSound(
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        )
        return this
    }
    Log.trace(
        LOG_TAG,
        SELF_TAG,
        "Setting sound from bundle named $customSound."
    )
    setSound(
        context.getSoundUriForResourceName(
            customSound
        )
    )
    return this
}

/**
 * Sets the provided image url as the large icon for the legacy style notification. If a large icon url is received
 * from the payload, the image is downloaded and the notification style is set to
 * BigPictureStyle. If large icon url is not received from the payload, default style is used
 * for the notification.
 *
 * @param imageUrl [String] containing the image url
 * @param title `String` containing the title
 * @param bodyText `String` containing the body text
 */
internal fun NotificationCompat.Builder.setLargeIcon(
    imageUrl: String?,
    title: String?,
    bodyText: String?
): NotificationCompat.Builder {
    // Quick bail out if there is no image url
    if (imageUrl.isNullOrEmpty()) return this
    val config = AEPUIImageConfig.Builder(
        CAROUSEL_MAX_BITMAP_WIDTH.toFloat(),
        CAROUSEL_MAX_BITMAP_HEIGHT.toFloat()
    )
        .urlList(listOf(imageUrl))
        .build()

    val downloadedIconCount = AEPUIImageUtils.cacheImages(config)

    // Bail out if the download fails
    if (downloadedIconCount == 0) {
        return this
    }

    val bitmap = AEPUIImageUtils.getCachedImage(imageUrl)
    setLargeIcon(bitmap)
    val bigPictureStyle = NotificationCompat.BigPictureStyle()
    bigPictureStyle.bigPicture(bitmap)
    bigPictureStyle.bigLargeIcon(null)
    bigPictureStyle.setBigContentTitle(title)
    bigPictureStyle.setSummaryText(bodyText)
    setStyle(bigPictureStyle)
    return this
}

/**
 * Sets the click action for the notification.
 *
 * @param context the application [Context]
 * @param trackerActivityClass the [Class] of the activity to set in the created pending intent for tracking purposes
 * @param actionUri `String` containing the action uri
 * @param intentExtras the [Bundle] containing the extras to be added to the intent
 */
internal fun NotificationCompat.Builder.setNotificationClickAction(
    context: Context,
    trackerActivityClass: Class<out Activity>?,
    actionUri: String?,
    intentExtras: Bundle?
): NotificationCompat.Builder {
    val pendingIntent: PendingIntent? = PendingIntentUtils.createPendingIntentForTrackerActivity(
        context,
        trackerActivityClass,
        actionUri,
        null,
        intentExtras
    )
    setContentIntent(pendingIntent)
    return this
}

/**
 * Sets the delete action for the notification.
 *
 * @param context the application [Context]
 * @param trackerActivityClass the [Class] of the activity to set in the created pending intent for tracking purposes
 * notification
 */
internal fun NotificationCompat.Builder.setNotificationDeleteAction(
    context: Context,
    trackerActivityClass: Class<out Activity>?
): NotificationCompat.Builder {
    val deleteIntent = Intent(PushTemplateConstants.NotificationAction.DISMISSED)
    trackerActivityClass?.let {
        deleteIntent.setClass(context.applicationContext, trackerActivityClass)
    }
    deleteIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
    val intent = PendingIntent.getActivity(
        context,
        Random().nextInt(),
        deleteIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    setDeleteIntent(intent)
    return this
}

/**
 * Adds action buttons for the notification.
 *
 * @param context the application [Context]
 * @param trackerActivityClass the [Activity] class to use as the tracker activity
 * @param actionButtons list of [BasicPushTemplate.ActionButton] containing action buttons to attach
 * to the notification
 * @param intentExtras the [Bundle] containing the extras to be added to the intent
 */
internal fun NotificationCompat.Builder.addActionButtons(
    context: Context,
    trackerActivityClass: Class<out Activity>?,
    actionButtons: List<BasicPushTemplate.ActionButton>?,
    intentExtras: Bundle?
): NotificationCompat.Builder {
    if (actionButtons.isNullOrEmpty()) {
        return this
    }
    for (eachButton in actionButtons) {
        val pendingIntent: PendingIntent? =
            if (eachButton.type === PushTemplateConstants.ActionType.DEEPLINK ||
                eachButton.type === PushTemplateConstants.ActionType.WEBURL
            ) {
                PendingIntentUtils.createPendingIntentForTrackerActivity(
                    context,
                    trackerActivityClass,
                    eachButton.link,
                    eachButton.label,
                    intentExtras
                )
            } else {
                PendingIntentUtils.createPendingIntentForTrackerActivity(
                    context,
                    trackerActivityClass,
                    null,
                    eachButton.label,
                    intentExtras
                )
            }
        addAction(0, eachButton.label, pendingIntent)
    }
    return this
}

/**
 * Checks if the icon is valid.
 *
 * @param icon the icon to be checked
 * @return true if the icon is valid, false otherwise
 */
private fun isValidIcon(icon: Int): Boolean {
    return icon > 0
}
