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
import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.adobe.marketing.mobile.notificationbuilder.NotificationConstructionFailedException
import com.adobe.marketing.mobile.notificationbuilder.PushTemplateConstants
import com.adobe.marketing.mobile.notificationbuilder.R
import com.adobe.marketing.mobile.notificationbuilder.internal.extensions.createNotificationChannelIfRequired
import com.adobe.marketing.mobile.notificationbuilder.internal.extensions.setRemoteViewClickAction
import com.adobe.marketing.mobile.notificationbuilder.internal.extensions.setRemoteViewImage
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.MultiIconPushTemplate
import com.adobe.marketing.mobile.services.Log

internal object MultiIconNotificationBuilder {
    const val SELF_TAG = "MultiIconNotificationBuilder"

    fun construct(
        context: Context,
        pushTemplate: MultiIconPushTemplate,
        trackerActivityClass: Class<out Activity>?
    ): NotificationCompat.Builder {

        Log.trace(
            PushTemplateConstants.LOG_TAG,
            SELF_TAG,
            "Building an icon template push notification."
        )

        val packageName = context.packageName
        val notificationLayout = RemoteViews(packageName, R.layout.push_template_multi_icon)

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelIdToUse: String = notificationManager.createNotificationChannelIfRequired(
            context,
            pushTemplate
        )

        populateIconsForMultiIconTemplate(
            context,
            trackerActivityClass,
            notificationLayout,
            pushTemplate,
            pushTemplate.templateItemList,
            packageName
        )

        setCancelIcon(
            notificationLayout,
            pushTemplate
        )

        val closeButtonIntentExtra = Bundle(pushTemplate.data.getBundle()) // copy the bundle
        closeButtonIntentExtra.putString(PushTemplateConstants.PushPayloadKeys.STICKY, "false")
        notificationLayout.setRemoteViewClickAction(
            context,
            trackerActivityClass,
            R.id.five_icon_close_button,
            null,
            null,
            closeButtonIntentExtra
        )

        return AEPPushNotificationBuilder.construct(
            context,
            pushTemplate,
            channelIdToUse,
            trackerActivityClass,
            notificationLayout,
            notificationLayout,
            R.id.carousel_container_layout
        )
    }

    private fun setCancelIcon(
        notificationLayout: RemoteViews,
        pushTemplate: MultiIconPushTemplate,
    ) {
        val iconString = pushTemplate.cancelIcon
        notificationLayout.setRemoteViewImage(iconString, R.id.five_icon_close_button)
    }

    private fun populateIconsForMultiIconTemplate(
        context: Context,
        trackerActivityClass: Class<out Activity>?,
        notificationLayout: RemoteViews,
        pushTemplate: MultiIconPushTemplate,
        items: MutableList<MultiIconPushTemplate.MultiIconTemplateItem>,
        packageName: String?
    ) {
        var validImagesAddedCount = 0
        for (item in items) {
            val iconItem = RemoteViews(packageName, R.layout.multi_icon_template_item)
            if (iconItem.setRemoteViewImage(item.iconUrl, R.id.icon_item_image_view)) {
                validImagesAddedCount++
            }

            trackerActivityClass?.let {
                val interactionUri = item.actionUri ?: pushTemplate.actionUri
                iconItem.setRemoteViewClickAction(
                    context,
                    trackerActivityClass,
                    R.id.icon_item_image_view,
                    interactionUri,
                    null,
                    pushTemplate.data.getBundle()
                )
            }
            notificationLayout.addView(R.id.icons_layout_linear, iconItem)
        }
        if (validImagesAddedCount < PushTemplateConstants.DefaultValues.ICON_TEMPLATE_MIN_IMAGE_COUNT) {
            throw NotificationConstructionFailedException("Valid icons are less then 3, cannot build a notification.")
        }
    }
}
