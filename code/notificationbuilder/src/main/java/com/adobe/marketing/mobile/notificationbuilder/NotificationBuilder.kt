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
import android.content.BroadcastReceiver
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.adobe.marketing.mobile.notificationbuilder.internal.PushTemplateConstants
import com.adobe.marketing.mobile.notificationbuilder.internal.PushTemplateConstants.LOG_TAG
import com.adobe.marketing.mobile.notificationbuilder.internal.PushTemplateType
import com.adobe.marketing.mobile.notificationbuilder.internal.builders.AutoCarouselNotificationBuilder
import com.adobe.marketing.mobile.notificationbuilder.internal.builders.BasicNotificationBuilder
import com.adobe.marketing.mobile.notificationbuilder.internal.builders.InputBoxNotificationBuilder
import com.adobe.marketing.mobile.notificationbuilder.internal.builders.LegacyNotificationBuilder
import com.adobe.marketing.mobile.notificationbuilder.internal.builders.ManualCarouselNotificationBuilder
import com.adobe.marketing.mobile.notificationbuilder.internal.builders.ZeroBezelNotificationBuilder
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.AEPPushTemplate
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.AutoCarouselPushTemplate
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.BasicPushTemplate
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.CarouselPushTemplate
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.InputBoxPushTemplate
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.ManualCarouselPushTemplate
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.ZeroBezelPushTemplate
import com.adobe.marketing.mobile.notificationbuilder.internal.util.IntentData
import com.adobe.marketing.mobile.notificationbuilder.internal.util.MapData
import com.adobe.marketing.mobile.services.Log
import com.adobe.marketing.mobile.services.ServiceProvider

/**
 * Public facing object to construct a [NotificationCompat.Builder] object for the specified [PushTemplateType].
 * The [constructNotificationBuilder] methods will build the appropriate notification based on the provided
 * [AEPPushTemplate] or [Intent].
 */
object NotificationBuilder {
    private const val SELF_TAG = "NotificationBuilder"
    const val VERSION = "3.0.0"

    @JvmStatic
    fun version(): String {
        return VERSION
    }

    @Throws(NotificationConstructionFailedException::class)
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
        val pushTemplateType =
            PushTemplateType.fromString(messageData[PushTemplateConstants.PushPayloadKeys.TEMPLATE_TYPE])
        val notificationData = MapData(messageData)
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
                    CarouselPushTemplate.createCarouselPushTemplate(notificationData)

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
                        Log.trace(LOG_TAG, SELF_TAG, "Unknown carousel push template type, creating a legacy style notification.")
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

            PushTemplateType.UNKNOWN -> {
                return LegacyNotificationBuilder.construct(
                    context,
                    BasicPushTemplate(notificationData),
                    trackerActivityClass
                )
            }
        }
    }

    @Throws(NotificationConstructionFailedException::class)
    @JvmStatic
    fun constructNotificationBuilder(
        intent: Intent,
        trackerActivityClass: Class<out Activity>?,
        broadcastReceiverClass: Class<out BroadcastReceiver>?
    ): NotificationCompat.Builder {
        val context = ServiceProvider.getInstance().appContextService.applicationContext
            ?: throw NotificationConstructionFailedException("Application context is null, cannot build a notification.")
        val extras = intent.extras ?: throw NotificationConstructionFailedException("Intent extras are null, cannot re-build the notification.")
        val pushTemplateType =
            PushTemplateType.fromString(intent.getStringExtra(PushTemplateConstants.PushPayloadKeys.TEMPLATE_TYPE))
        val intentData = IntentData(extras, intent.action)

        when (pushTemplateType) {
            PushTemplateType.BASIC -> {
                Log.trace(LOG_TAG, SELF_TAG, "Building a basic style push notification.")
                return BasicNotificationBuilder.construct(
                    context,
                    BasicPushTemplate(intentData),
                    trackerActivityClass,
                    broadcastReceiverClass
                )
            }

            PushTemplateType.CAROUSEL -> {
                return ManualCarouselNotificationBuilder.construct(
                    context,
                    ManualCarouselPushTemplate(intentData),
                    trackerActivityClass,
                    broadcastReceiverClass
                )
            }

            PushTemplateType.INPUT_BOX -> {
                return InputBoxNotificationBuilder.construct(
                    context,
                    InputBoxPushTemplate(intentData),
                    trackerActivityClass,
                    broadcastReceiverClass
                )
            }

            PushTemplateType.UNKNOWN -> {
                return LegacyNotificationBuilder.construct(
                    context,
                    BasicPushTemplate(intentData),
                    trackerActivityClass
                )
            }

            else -> {
                // default to legacy notification
                return LegacyNotificationBuilder.construct(
                    context,
                    BasicPushTemplate(intentData),
                    trackerActivityClass
                )
            }
        }
    }
}
