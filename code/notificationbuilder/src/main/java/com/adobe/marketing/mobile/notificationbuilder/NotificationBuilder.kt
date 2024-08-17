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
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.adobe.marketing.mobile.notificationbuilder.NotificationBuilder.constructNotificationBuilder
import com.adobe.marketing.mobile.notificationbuilder.internal.PushTemplateType
import com.adobe.marketing.mobile.notificationbuilder.internal.builders.AutoCarouselNotificationBuilder
import com.adobe.marketing.mobile.notificationbuilder.internal.builders.BasicNotificationBuilder
import com.adobe.marketing.mobile.notificationbuilder.internal.builders.InputBoxNotificationBuilder
import com.adobe.marketing.mobile.notificationbuilder.internal.builders.LegacyNotificationBuilder
import com.adobe.marketing.mobile.notificationbuilder.internal.builders.ManualCarouselNotificationBuilder
import com.adobe.marketing.mobile.notificationbuilder.internal.builders.MultiIconNotificationBuilder
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
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.MultiIconPushTemplate
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.ProductCatalogPushTemplate
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.ProductRatingPushTemplate
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.TimerPushTemplate
import com.adobe.marketing.mobile.notificationbuilder.internal.templates.ZeroBezelPushTemplate
import com.adobe.marketing.mobile.notificationbuilder.internal.util.IntentData
import com.adobe.marketing.mobile.notificationbuilder.internal.util.MapData
import com.adobe.marketing.mobile.notificationbuilder.internal.util.NotificationData
import com.adobe.marketing.mobile.services.Log
import com.adobe.marketing.mobile.services.ServiceProvider
import com.adobe.ui_utils.PushTemplateConstants
import com.adobe.ui_utils.PushTemplateConstants.LOG_TAG

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
                        Log.warning(
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

            PushTemplateType.MULTI_ICON -> {
                return MultiIconNotificationBuilder.construct(
                    context,
                    MultiIconPushTemplate(notificationData),
                    trackerActivityClass,
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
