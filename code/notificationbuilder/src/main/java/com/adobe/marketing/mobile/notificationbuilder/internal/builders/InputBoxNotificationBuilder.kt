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
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.RemoteInput
import com.adobe.marketing.mobile.notificationbuilder.NotificationConstructionFailedException
import com.adobe.marketing.mobile.notificationbuilder.PushTemplateConstants
import com.adobe.marketing.mobile.notificationbuilder.PushTemplateConstants.LOG_TAG
import com.adobe.marketing.mobile.notificationbuilder.PushTemplateConstants.PushPayloadKeys
import com.adobe.marketing.mobile.notificationbuilder.R
import com.adobe.marketing.mobile.notificationbuilder.internal.extensions.createNotificationChannelIfRequired
import com.adobe.marketing.mobile.notificationbuilder.internal.extensions.setRemoteViewImage
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.InputBoxPushTemplate
import com.adobe.marketing.mobile.services.Log
import java.util.Random

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

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            throw NotificationConstructionFailedException("Input box push notification on devices below Android N is not supported.")
        }

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

        // set the image on the notification
        expandedLayout.setRemoteViewImage(
            imageUri,
            R.id.expanded_template_image,
        )

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
            trackerActivityClass,
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
     * @param trackerActivityClass the [Activity] class to launch when the input text is submitted
     * @param builder the [NotificationCompat.Builder] to attach the action buttons
     * @param channelId the [String] containing the channel ID to use for the notification
     * @param pushTemplate the [InputBoxPushTemplate] object containing the input box push template data
     * button is pressed
     */
    private fun addInputTextAction(
        context: Context,
        trackerActivityClass: Class<out Activity>?,
        builder: NotificationCompat.Builder,
        channelId: String,
        pushTemplate: InputBoxPushTemplate
    ) {
        val inputHint =
            if (pushTemplate.inputTextHint.isNullOrEmpty()) PushTemplateConstants.DefaultValues.INPUT_BOX_DEFAULT_REPLY_TEXT else pushTemplate.inputTextHint
        val remoteInput = RemoteInput.Builder(pushTemplate.inputBoxReceiverName)
            .setLabel(inputHint)
            .build()

        val replyPendingIntent = createInputReceivedPendingIntent(
            context,
            trackerActivityClass,
            channelId,
            pushTemplate
        )

        val action =
            NotificationCompat.Action.Builder(null, inputHint, replyPendingIntent)
                .addRemoteInput(remoteInput)
                .build()

        builder.addAction(action)
    }

    /**
     * Creates a pending intent which resolves to the [trackerActivityClass] for the input submit action.
     *
     * @param context the application [Context]
     * @param trackerActivityClass the [Activity] class to launch when the input text is submitted
     * @param channelId the [String] containing the channel ID to use for the notification
     * @param pushTemplate the [InputBoxPushTemplate] object containing the input box push template data
     * @return the created [PendingIntent]
     */
    private fun createInputReceivedPendingIntent(
        context: Context,
        trackerActivityClass: Class<out Activity>?,
        channelId: String,
        pushTemplate: InputBoxPushTemplate
    ): PendingIntent {
        val inputReceivedIntentExtras = pushTemplate.data.getBundle()
        inputReceivedIntentExtras.putString(PushPayloadKeys.CHANNEL_ID, channelId)
        val intent = Intent(PushTemplateConstants.NotificationAction.INPUT_RECEIVED)
        trackerActivityClass?.let {
            intent.setClass(context.applicationContext, trackerActivityClass)
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtras(inputReceivedIntentExtras)

        // Remote input requires a pending intent to be created with the FLAG_MUTABLE flag
        return PendingIntent.getActivity(
            context,
            Random().nextInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
    }
}
