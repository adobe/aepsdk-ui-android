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
import com.adobe.marketing.mobile.notificationbuilder.internal.PushTemplateConstants
import com.adobe.marketing.mobile.notificationbuilder.internal.extensions.setNotificationBackgroundColor
import com.adobe.marketing.mobile.notificationbuilder.internal.extensions.setNotificationBodyTextColor
import com.adobe.marketing.mobile.notificationbuilder.internal.extensions.setNotificationClickAction
import com.adobe.marketing.mobile.notificationbuilder.internal.extensions.setNotificationDeleteAction
import com.adobe.marketing.mobile.notificationbuilder.internal.extensions.setNotificationTitleTextColor
import com.adobe.marketing.mobile.notificationbuilder.internal.extensions.setRemoteViewLargeIcon
import com.adobe.marketing.mobile.notificationbuilder.internal.extensions.setSmallIcon
import com.adobe.marketing.mobile.notificationbuilder.internal.extensions.setSound
import com.adobe.marketing.mobile.notificationbuilder.internal.extensions.setVisibility
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
        smallLayout.setRemoteViewLargeIcon(pushTemplate.largeIcon)
        expandedLayout.setRemoteViewLargeIcon(pushTemplate.largeIcon)

        val builder = NotificationCompat.Builder(context, channelIdToUse)
            .setTicker(pushTemplate.ticker)
            .setNumber(pushTemplate.badgeCount)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(smallLayout)
            .setCustomBigContentView(expandedLayout)
            // small icon must be present, otherwise the notification will not be displayed.
            .setSmallIcon(context, pushTemplate.smallIcon, pushTemplate.smallIconColor)
            // set notification visibility
            .setVisibility(pushTemplate)
            // set custom sound, note this applies to API 25 and lower only as API 26 and up set the
            // sound on the notification channel
            .setSound(context, pushTemplate.sound)
            .setNotificationClickAction(
                context,
                trackerActivityClass,
                pushTemplate.actionUri,
                pushTemplate.tag,
                pushTemplate.isNotificationSticky ?: false
            )
            .setNotificationDeleteAction(context, trackerActivityClass)

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

        // add the notification payload version
        intent.putExtra(PushTemplateConstants.PushPayloadKeys.VERSION, template.payloadVersion)

        // add the notification text information
        intent.putExtra(PushTemplateConstants.PushPayloadKeys.TITLE, template.title)
        intent.putExtra(PushTemplateConstants.PushPayloadKeys.BODY, template.body)
        intent.putExtra(PushTemplateConstants.PushPayloadKeys.EXPANDED_BODY_TEXT, template.expandedBodyText)
        intent.putExtra(PushTemplateConstants.PushPayloadKeys.TICKER, template.ticker)

        // add the template type
        intent.putExtra(PushTemplateConstants.PushPayloadKeys.TEMPLATE_TYPE, template.templateType?.value)

        // add the basic media information
        intent.putExtra(PushTemplateConstants.PushPayloadKeys.IMAGE_URL, template.imageUrl)

        // add the notification action information
        intent.putExtra(PushTemplateConstants.PushPayloadKeys.ACTION_URI, template.actionUri)
        intent.putExtra(PushTemplateConstants.PushPayloadKeys.ACTION_TYPE, template.actionType?.name)

        // add the notification icon information
        intent.putExtra(PushTemplateConstants.PushPayloadKeys.SMALL_ICON, template.smallIcon)
        intent.putExtra(PushTemplateConstants.PushPayloadKeys.LARGE_ICON, template.largeIcon)

        // add the notification color data
        intent.putExtra(PushTemplateConstants.PushPayloadKeys.TITLE_TEXT_COLOR, template.titleTextColor)
        intent.putExtra(PushTemplateConstants.PushPayloadKeys.BODY_TEXT_COLOR, template.bodyTextColor)
        intent.putExtra(PushTemplateConstants.PushPayloadKeys.BACKGROUND_COLOR, template.backgroundColor)
        intent.putExtra(PushTemplateConstants.PushPayloadKeys.SMALL_ICON_COLOR, template.smallIconColor)

        // add other notification properties
        intent.putExtra(PushTemplateConstants.PushPayloadKeys.TAG, template.tag)
        intent.putExtra(PushTemplateConstants.PushPayloadKeys.SOUND, template.sound)
        intent.putExtra(PushTemplateConstants.PushPayloadKeys.BADGE_COUNT, template.badgeCount.toString())
        intent.putExtra(PushTemplateConstants.PushPayloadKeys.STICKY, template.isNotificationSticky.toString())

        // add notification priority and visibility
        intent.putExtra(PushTemplateConstants.PushPayloadKeys.PRIORITY, template.priorityString)
        intent.putExtra(PushTemplateConstants.PushPayloadKeys.VISIBILITY, template.visibilityString)
        return intent
    }
}
