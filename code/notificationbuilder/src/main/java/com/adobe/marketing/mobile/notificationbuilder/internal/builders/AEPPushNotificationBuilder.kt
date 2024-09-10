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
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.adobe.marketing.mobile.notificationbuilder.NotificationConstructionFailedException
import com.adobe.marketing.mobile.notificationbuilder.R
import com.adobe.marketing.mobile.notificationbuilder.internal.extensions.setNotificationBackgroundColor
import com.adobe.marketing.mobile.notificationbuilder.internal.extensions.setNotificationBodyTextColor
import com.adobe.marketing.mobile.notificationbuilder.internal.extensions.setNotificationClickAction
import com.adobe.marketing.mobile.notificationbuilder.internal.extensions.setNotificationDeleteAction
import com.adobe.marketing.mobile.notificationbuilder.internal.extensions.setNotificationTitleTextColor
import com.adobe.marketing.mobile.notificationbuilder.internal.extensions.setRemoteViewImage
import com.adobe.marketing.mobile.notificationbuilder.internal.extensions.setSmallIcon
import com.adobe.marketing.mobile.notificationbuilder.internal.extensions.setSound
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.AEPPushTemplate

// TODO: The utilities provided by this builder assumes the id's for various common elements (R.id.basic_small_layout,
//  R.id.notification_title, R.id.notification_body_expanded) are the same across templates.
//  We will need to figure out a way to enforce this somehow either programmatically, structurally in the layout or via documentation.
internal object AEPPushNotificationBuilder {
    @Throws(NotificationConstructionFailedException::class)
    fun construct(
        context: Context,
        pushTemplate: AEPPushTemplate,
        channelIdToUse: String,
        trackerActivityClass: Class<out Activity>?,
        smallLayout: RemoteViews,
        expandedLayout: RemoteViews,
        containerLayoutViewId: Int
    ): NotificationCompat.Builder {

        // set the title and body text on the notification
        val titleText = pushTemplate.title
        val smallBodyText = pushTemplate.body
        val expandedBodyText = pushTemplate.expandedBodyText
        smallLayout.setTextViewText(R.id.notification_title, titleText)
        smallLayout.setTextViewText(R.id.notification_body, smallBodyText)
        expandedLayout.setTextViewText(R.id.notification_title, titleText)
        expandedLayout.setTextViewText(R.id.notification_body_expanded, expandedBodyText)

        // set custom colors on the notification background, title text, and body text
        smallLayout.setNotificationBackgroundColor(
            pushTemplate.backgroundColor,
            R.id.basic_small_layout
        )

        expandedLayout.setNotificationBackgroundColor(
            pushTemplate.backgroundColor,
            containerLayoutViewId
        )

        smallLayout.setNotificationTitleTextColor(
            pushTemplate.titleTextColor,
            R.id.notification_title
        )

        expandedLayout.setNotificationTitleTextColor(
            pushTemplate.titleTextColor,
            R.id.notification_title
        )

        smallLayout.setNotificationBodyTextColor(
            pushTemplate.bodyTextColor,
            R.id.notification_body
        )

        expandedLayout.setNotificationBodyTextColor(
            pushTemplate.bodyTextColor,
            R.id.notification_body_expanded
        )

        // set a large icon if one is present
        smallLayout.setRemoteViewImage(pushTemplate.largeIcon, R.id.large_icon)
        expandedLayout.setRemoteViewImage(pushTemplate.largeIcon, R.id.large_icon)

        val builder = NotificationCompat.Builder(context, channelIdToUse)
            .setTicker(pushTemplate.ticker)
            .setNumber(pushTemplate.badgeCount)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(smallLayout)
            .setCustomBigContentView(expandedLayout)
            // small icon must be present, otherwise the notification will not be displayed.
            .setSmallIcon(context, pushTemplate.smallIcon, pushTemplate.smallIconColor)
            // set notification visibility
            .setVisibility(pushTemplate.visibility.value)
            .setNotificationClickAction(
                context,
                trackerActivityClass,
                pushTemplate.actionUri,
                pushTemplate.actionType,
                pushTemplate.data.getBundle()
            )
            .setNotificationDeleteAction(context, trackerActivityClass)

        // API21 specific fixes
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
            // intent handling fix, see MOB-21261 for more info
            builder.setOnlyAlertOnce(true)
            // heads up display fix, see MOB-21447 for more info
            builder.setCustomHeadsUpContentView(expandedLayout)
        }

        // API22 and 23 heads up display fix, see MOB-21447 for more info
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M || Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP_MR1) {
            builder.setCustomHeadsUpContentView(smallLayout)
        }

        // if not from intent, set custom sound, note this applies to API 25 and lower only as
        // API 26 and up set the sound on the notification channel
        if (!pushTemplate.isFromIntent) {
            builder.setSound(context, pushTemplate.sound)
        }

        // if API level is below 26 (prior to notification channels) then notification priority is
        // set on the notification builder
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            builder.setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVibrate(LongArray(0)) // hack to enable heads up notifications as a HUD style
            // notification requires a tone or vibration
        }
        return builder
    }

    internal fun createIntent(action: String, template: AEPPushTemplate): Intent {
        val intent = Intent(action)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        intent.putExtras(template.data.getBundle())
        return intent
    }
}
