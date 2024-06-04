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
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.RemoteInput
import com.adobe.marketing.mobile.notificationbuilder.NotificationConstructionFailedException
import com.adobe.marketing.mobile.notificationbuilder.PushTemplateConstants
import com.adobe.marketing.mobile.notificationbuilder.PushTemplateConstants.LOG_TAG
import com.adobe.marketing.mobile.notificationbuilder.PushTemplateConstants.PushPayloadKeys
import com.adobe.marketing.mobile.notificationbuilder.R
import com.adobe.marketing.mobile.notificationbuilder.internal.PushTemplateImageUtils
import com.adobe.marketing.mobile.notificationbuilder.internal.extensions.createNotificationChannelIfRequired
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.InputBoxPushTemplate
import com.adobe.marketing.mobile.services.Log

/**
 * Object responsible for constructing a [NotificationCompat.Builder] object containing an input box push template notification.
 */
internal object InputBoxNotificationBuilder {
    private const val SELF_TAG = "InputBoxNotificationBuilder"

    @Throws(NotificationConstructionFailedException::class)
    fun construct(
        context: Context,
        pushTemplate: InputBoxPushTemplate,
        trackerActivityClass: Class<out Activity>?,
        broadcastReceiverClass: Class<out BroadcastReceiver>?
    ): NotificationCompat.Builder {
        Log.trace(LOG_TAG, SELF_TAG, "Building an input box template push notification.")
        val packageName = context.packageName
        val smallLayout = RemoteViews(packageName, R.layout.push_template_collapsed)
        val expandedLayout = RemoteViews(packageName, R.layout.push_template_expanded)

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelIdToUse: String =
            notificationManager.createNotificationChannelIfRequired(context, pushTemplate)

        // create the notification builder with the common settings applied
        val notificationBuilder = AEPPushNotificationBuilder.construct(
            context,
            pushTemplate,
            channelIdToUse,
            trackerActivityClass,
            smallLayout,
            expandedLayout,
            R.id.basic_expanded_layout
        )

        // get push payload data. if we are handling an intent then we know that we should be building a feedback received notification.
        val imageUri =
            if (pushTemplate.isFromIntent) pushTemplate.feedbackImage else pushTemplate.imageUrl
        val downloadedImageCount = PushTemplateImageUtils.cacheImages(listOf(imageUri))

        if (downloadedImageCount == 1) {
            val pushImage = PushTemplateImageUtils.getCachedImage(imageUri)
            expandedLayout.setImageViewBitmap(R.id.expanded_template_image, pushImage)
        } else {
            Log.trace(LOG_TAG, SELF_TAG, "No image found for input box push template.")
            expandedLayout.setViewVisibility(R.id.expanded_template_image, View.GONE)
        }

        val expandedBodyText =
            if (pushTemplate.isFromIntent) pushTemplate.feedbackText else pushTemplate.expandedBodyText
        val collapsedBodyText =
            if (pushTemplate.isFromIntent) pushTemplate.feedbackText else pushTemplate.body
        smallLayout.setTextViewText(R.id.notification_title, pushTemplate.title)
        smallLayout.setTextViewText(R.id.notification_body, collapsedBodyText)
        expandedLayout.setTextViewText(R.id.notification_title, pushTemplate.title)
        expandedLayout.setTextViewText(
            R.id.notification_body_expanded, expandedBodyText
        )

        // add an input box to capture user feedback if the push template is not from an intent
        // otherwise, we are done building the notification
        if (pushTemplate.isFromIntent) {
            return notificationBuilder
        }
        Log.trace(
            LOG_TAG, SELF_TAG,
            "Adding an input box to capture text input. The input box receiver name is ${pushTemplate.inputBoxReceiverName}."
        )
        addInputTextAction(
            context,
            broadcastReceiverClass,
            notificationBuilder,
            channelIdToUse,
            pushTemplate
        )

        return notificationBuilder
    }

    /**
     * Adds an input text action for the notification.
     *
     * @param context the application [Context]
     * @param broadcastReceiverClass the [BroadcastReceiver] class to use as the broadcast receiver
     * @param builder the [NotificationCompat.Builder] to attach the action buttons
     * @param channelId the [String] containing the channel ID to use for the notification
     * @param pushTemplate the [InputBoxPushTemplate] object containing the input box push template data
     * button is pressed
     */
    private fun addInputTextAction(
        context: Context,
        broadcastReceiverClass: Class<out BroadcastReceiver>?,
        builder: NotificationCompat.Builder,
        channelId: String,
        pushTemplate: InputBoxPushTemplate
    ) {
        val inputHint =
            if (pushTemplate.inputTextHint.isNullOrEmpty()) PushTemplateConstants.DefaultValues.INPUT_BOX_DEFAULT_REPLY_TEXT else pushTemplate.inputTextHint
        val remoteInput = RemoteInput.Builder(pushTemplate.inputBoxReceiverName)
            .setLabel(inputHint)
            .build()

        val inputReceivedIntent = createInputReceivedIntent(
            context,
            broadcastReceiverClass,
            channelId,
            pushTemplate
        )

        val replyPendingIntent =
            inputReceivedIntent?.let {
                PendingIntent.getBroadcast(
                    context,
                    pushTemplate.tag.hashCode(),
                    it,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
                )
            }

        val action =
            NotificationCompat.Action.Builder(null, inputHint, replyPendingIntent)
                .addRemoteInput(remoteInput)
                .build()

        builder.addAction(action)
    }

    private fun createInputReceivedIntent(
        context: Context,
        broadcastReceiverClass: Class<out BroadcastReceiver>?,
        channelId: String,
        pushTemplate: InputBoxPushTemplate
    ): Intent? {
        if (broadcastReceiverClass == null) {
            return null
        }
        Log.trace(
            LOG_TAG,
            SELF_TAG,
            "Creating a text input received intent from a push template object."
        )

        val inputReceivedIntent = AEPPushNotificationBuilder.createIntent(
            PushTemplateConstants.IntentActions.INPUT_RECEIVED,
            pushTemplate
        )
        inputReceivedIntent.putExtra(PushPayloadKeys.CHANNEL_ID, channelId)
        inputReceivedIntent.putExtra(
            PushPayloadKeys.INPUT_BOX_RECEIVER_NAME,
            pushTemplate.inputBoxReceiverName
        )
        inputReceivedIntent.putExtra(PushPayloadKeys.INPUT_BOX_HINT, pushTemplate.inputTextHint)
        inputReceivedIntent.putExtra(
            PushPayloadKeys.INPUT_BOX_FEEDBACK_TEXT,
            pushTemplate.feedbackText
        )
        inputReceivedIntent.putExtra(
            PushPayloadKeys.INPUT_BOX_FEEDBACK_IMAGE,
            pushTemplate.feedbackImage
        )
        broadcastReceiverClass.let {
            inputReceivedIntent.setClass(context.applicationContext, broadcastReceiverClass)
        }
        return inputReceivedIntent
    }
}
