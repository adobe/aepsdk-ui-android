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

package com.adobe.marketing.mobile.notificationbuilder

import android.app.Activity
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.adobe.marketing.mobile.notificationbuilder.NotificationBuilder.constructNotificationBuilder
import com.adobe.marketing.mobile.notificationbuilder.PushTemplateConstants.LOG_TAG
import com.adobe.marketing.mobile.notificationbuilder.internal.PendingIntentUtils
import com.adobe.marketing.mobile.notificationbuilder.internal.PushTemplateType
import com.adobe.marketing.mobile.notificationbuilder.internal.builders.AutoCarouselNotificationBuilder
import com.adobe.marketing.mobile.notificationbuilder.internal.builders.BasicNotificationBuilder
import com.adobe.marketing.mobile.notificationbuilder.internal.builders.InputBoxNotificationBuilder
import com.adobe.marketing.mobile.notificationbuilder.internal.builders.LegacyNotificationBuilder
import com.adobe.marketing.mobile.notificationbuilder.internal.builders.ManualCarouselNotificationBuilder
import com.adobe.marketing.mobile.notificationbuilder.internal.builders.ProductCatalogNotificationBuilder
import com.adobe.marketing.mobile.notificationbuilder.internal.builders.ProductRatingNotificationBuilder
import com.adobe.marketing.mobile.notificationbuilder.internal.builders.TimerNotificationBuilder
import com.adobe.marketing.mobile.notificationbuilder.internal.builders.ZeroBezelNotificationBuilder
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.AEPPushTemplate
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.AutoCarouselPushTemplate
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.BasicPushTemplate
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.CarouselPushTemplate
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.InputBoxPushTemplate
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.ManualCarouselPushTemplate
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.ProductCatalogPushTemplate
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.ProductRatingPushTemplate
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.TimerPushTemplate
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.ZeroBezelPushTemplate
import com.adobe.marketing.mobile.notificationbuilder.internal.util.IntentData
import com.adobe.marketing.mobile.notificationbuilder.internal.util.MapData
import com.adobe.marketing.mobile.notificationbuilder.internal.util.NotificationData
import com.adobe.marketing.mobile.services.Log
import com.adobe.marketing.mobile.services.ServiceProvider
import com.adobe.marketing.mobile.util.TimeUtils

/**
 * Public facing object to construct a [NotificationCompat.Builder] object for the specified [PushTemplateType].
 * The [constructNotificationBuilder] methods will build the appropriate notification based on the provided
 * [AEPPushTemplate] or [Intent].
 */
object NotificationBuilder {
    private const val SELF_TAG = "NotificationBuilder"
    private const val VERSION = "3.0.0"

    @JvmStatic
    fun version(): String {
        return VERSION
    }

    /**
     * Constructs a [NotificationCompat.Builder] object from the provided [messageData]
     *
     * @param messageData [Map] containing the data needed for the notification construction
     * @param trackerActivityClass [Class] of the [Activity] to be launched when the notification is clicked
     * @param broadcastReceiverClass [Class] of the [BroadcastReceiver] to be used for handling notification actions
     * @return [NotificationCompat.Builder] object
     * @throws [NotificationConstructionFailedException] if the notification construction fails due to missing data
     * @throws [IllegalArgumentException] if the provided message data has invalid data
     */
    @Throws(NotificationConstructionFailedException::class, IllegalArgumentException::class)
    @JvmStatic
    fun constructNotificationBuilder(
        messageData: Map<String, String>,
        trackerActivityClass: Class<out Activity>?,
        broadcastReceiverClass: Class<out BroadcastReceiver>?
    ): NotificationCompat.Builder {
        val context = ServiceProvider.getInstance().appContextService.applicationContext
            ?: throw NotificationConstructionFailedException("Application context is null, cannot build a notification.")
        if (messageData.isEmpty()) {
            throw NotificationConstructionFailedException("Message data is empty, cannot build a notification.")
        }
        val notificationData = MapData(messageData)
        return createNotificationBuilder(context, notificationData, trackerActivityClass, broadcastReceiverClass)
    }

    /**
     * Constructs a [NotificationCompat.Builder] object from the provided [intent]
     *
     * @param intent [Intent] containing the data needed for the notification construction
     * @param trackerActivityClass [Class] of the [Activity] to be launched when the notification is clicked
     * @param broadcastReceiverClass [Class] of the [BroadcastReceiver] to be used for handling notification actions
     * @return [NotificationCompat.Builder] object
     * @throws [NotificationConstructionFailedException] if the notification construction fails due to missing data
     * @throws [IllegalArgumentException] if the provided message data has invalid data
     */
    @Throws(NotificationConstructionFailedException::class, IllegalArgumentException::class)
    @JvmStatic
    fun constructNotificationBuilder(
        intent: Intent,
        trackerActivityClass: Class<out Activity>?,
        broadcastReceiverClass: Class<out BroadcastReceiver>?
    ): NotificationCompat.Builder {
        val context = ServiceProvider.getInstance().appContextService.applicationContext
            ?: throw NotificationConstructionFailedException("Application context is null, cannot build a notification.")
        val extras = intent.extras ?: throw NotificationConstructionFailedException("Intent extras are null, cannot re-build the notification.")
        val intentData = IntentData(extras, intent.action)
        return createNotificationBuilder(context, intentData, trackerActivityClass, broadcastReceiverClass)
    }

    /**
     * Handles the remind later intent by scheduling a [PendingIntent] to the [broadcastReceiverClass]
     * which will be fired at a time specified in the [remindLaterIntent].
     *
     * Once the PendingIntent is fired, the [broadcastReceiverClass] is responsible for
     * reconstructing the notification and displaying it.
     *
     * @param remindLaterIntent [Intent] containing the data needed to schedule and recreate the notification
     * @param broadcastReceiverClass [Class] of the [BroadcastReceiver] that will be fired when the [PendingIntent] resolves at a later time
     */
    @Throws(NotificationConstructionFailedException::class, IllegalArgumentException::class)
    @JvmStatic
    fun handleRemindIntent(
        remindLaterIntent: Intent,
        broadcastReceiverClass: Class<out BroadcastReceiver>?
    ) {
        val context = ServiceProvider.getInstance().appContextService.applicationContext
            ?: throw NotificationConstructionFailedException("Application context is null, cannot schedule notification for later.")

        // get the time for remind later from the intent extras
        val intentExtras = remindLaterIntent.extras
            ?: throw NotificationConstructionFailedException("Intent extras are null, cannot schedule notification for later.")
        val remindLaterTimestamp =
            intentExtras.getString(PushTemplateConstants.PushPayloadKeys.REMIND_LATER_TIMESTAMP)?.toLongOrNull() ?: 0
        val remindLaterDuration =
            intentExtras.getString(PushTemplateConstants.PushPayloadKeys.REMIND_LATER_DURATION)?.toLongOrNull() ?: 0

        // calculate difference in fire date from the current date if timestamp is provided
        val secondsUntilFireDate: Long = if (remindLaterDuration > 0) remindLaterDuration
        else remindLaterTimestamp - TimeUtils.getUnixTimeInSeconds()

        val notificationManager = NotificationManagerCompat.from(context)
        val tag = intentExtras.getString(PushTemplateConstants.PushPayloadKeys.TAG)

        // if fire date is greater than 0 then we want to schedule a reminder notification.
        if (secondsUntilFireDate <= 0) {
            tag?.let { notificationManager.cancel(tag.hashCode()) }
            throw IllegalArgumentException("Remind later timestamp or duration is less than or equal to current timestamp, cannot schedule notification for later.")
        }
        Log.trace(LOG_TAG, SELF_TAG, "Remind later pressed, will reschedule the notification to be displayed $secondsUntilFireDate seconds from now")

        // calculate the trigger time
        val triggerTimeInSeconds: Long = if (remindLaterDuration > 0) remindLaterDuration + TimeUtils.getUnixTimeInSeconds()
        else remindLaterTimestamp

        // schedule a pending intent to be broadcast at the specified timestamp
        if (broadcastReceiverClass == null) {
            Log.trace(
                LOG_TAG,
                SELF_TAG,
                "Broadcast receiver class is null, will not schedule a notification from the received" +
                    " intent with action ${remindLaterIntent.action}"
            )
            tag?.let { notificationManager.cancel(tag.hashCode()) }
            return
        }
        val scheduledIntent = Intent(PushTemplateConstants.IntentActions.SCHEDULED_NOTIFICATION_BROADCAST)
        scheduledIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        scheduledIntent.putExtras(intentExtras)
        PendingIntentUtils.createPendingIntentForScheduledNotifications(context, scheduledIntent, broadcastReceiverClass, triggerTimeInSeconds)

        // cancel the displayed notification
        tag?.let { notificationManager.cancel(tag.hashCode()) }
    }

    private fun createNotificationBuilder(
        context: Context,
        notificationData: NotificationData,
        trackerActivityClass: Class<out Activity>?,
        broadcastReceiverClass: Class<out BroadcastReceiver>?
    ): NotificationCompat.Builder {

        val pushTemplateType =
            PushTemplateType.fromString(notificationData.getString(PushTemplateConstants.PushPayloadKeys.TEMPLATE_TYPE))

        when (pushTemplateType) {
            PushTemplateType.BASIC -> {
                val basicPushTemplate = BasicPushTemplate(notificationData)
                return BasicNotificationBuilder.construct(
                    context,
                    basicPushTemplate,
                    trackerActivityClass,
                    broadcastReceiverClass
                )
            }

            PushTemplateType.CAROUSEL -> {
                val carouselPushTemplate =
                    CarouselPushTemplate(notificationData)

                when (carouselPushTemplate) {
                    is AutoCarouselPushTemplate -> {
                        return AutoCarouselNotificationBuilder.construct(
                            context,
                            carouselPushTemplate,
                            trackerActivityClass,
                            broadcastReceiverClass
                        )
                    }

                    is ManualCarouselPushTemplate -> {
                        return ManualCarouselNotificationBuilder.construct(
                            context,
                            carouselPushTemplate,
                            trackerActivityClass,
                            broadcastReceiverClass
                        )
                    }

                    else -> {
                        Log.trace(
                            LOG_TAG,
                            SELF_TAG,
                            "Unknown carousel push template type, creating a legacy style notification."
                        )
                        return LegacyNotificationBuilder.construct(
                            context,
                            BasicPushTemplate(notificationData),
                            trackerActivityClass
                        )
                    }
                }
            }

            PushTemplateType.ZERO_BEZEL -> {
                val zeroBezelPushTemplate = ZeroBezelPushTemplate(notificationData)
                return ZeroBezelNotificationBuilder.construct(
                    context,
                    zeroBezelPushTemplate,
                    trackerActivityClass
                )
            }

            PushTemplateType.INPUT_BOX -> {
                return InputBoxNotificationBuilder.construct(
                    context,
                    InputBoxPushTemplate(notificationData),
                    trackerActivityClass,
                    broadcastReceiverClass
                )
            }

            PushTemplateType.PRODUCT_CATALOG -> {
                return ProductCatalogNotificationBuilder.construct(
                    context,
                    ProductCatalogPushTemplate(notificationData),
                    trackerActivityClass,
                    broadcastReceiverClass
                )
            }

            PushTemplateType.PRODUCT_RATING -> {
                return ProductRatingNotificationBuilder.construct(
                    context,
                    ProductRatingPushTemplate(notificationData),
                    trackerActivityClass,
                    broadcastReceiverClass
                )
            }

            PushTemplateType.TIMER -> {
                return TimerNotificationBuilder.construct(
                    context,
                    TimerPushTemplate(notificationData),
                    trackerActivityClass,
                    broadcastReceiverClass
                )
            }

            PushTemplateType.UNKNOWN -> {
                return LegacyNotificationBuilder.construct(
                    context,
                    BasicPushTemplate(notificationData),
                    trackerActivityClass
                )
            }
        }
    }
}
