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

package com.adobe.marketing.mobile.notificationbuilder.internal.templates

import com.adobe.marketing.mobile.notificationbuilder.internal.PushTemplateConstants.CarouselItemKeys
import com.adobe.marketing.mobile.notificationbuilder.internal.PushTemplateConstants.DefaultValues
import com.adobe.marketing.mobile.notificationbuilder.internal.PushTemplateConstants.LOG_TAG
import com.adobe.marketing.mobile.notificationbuilder.internal.PushTemplateConstants.PushPayloadKeys
import com.adobe.marketing.mobile.notificationbuilder.internal.util.NotificationData
import com.adobe.marketing.mobile.services.Log
import org.json.JSONArray
import org.json.JSONException

internal open class CarouselPushTemplate protected constructor(data: NotificationData) : AEPPushTemplate(data) {
    // Optional, Determines how the carousel will be operated. Valid values are "auto" or "manual".
    // Default is "auto".
    internal val carouselMode: String

    // Required, One or more Items in the carousel defined by the CarouselItem class
    internal val carouselItems: MutableList<CarouselItem>

    // Required, "default" or "filmstrip"
    internal val carouselLayout: String

    // Contains the carousel items as a string
    internal val rawCarouselItems: String

    data class CarouselItem(
        // Required, URI to an image to be shown for the carousel item
        val imageUri: String,
        // Optional, caption to show when the carousel item is visible
        val captionText: String?,
        // Optional, URI to handle when the item is touched by the user. If no uri is provided for the item, adb_uri will be handled instead.
        val interactionUri: String?
    )

    /**
     * Initializes the push template with the given NotificationData.
     *
     * @param data the data to initialize the push template with
     */
    init {
        carouselLayout = data.getRequiredString(PushPayloadKeys.CAROUSEL_LAYOUT)
        rawCarouselItems = data.getRequiredString(PushPayloadKeys.CAROUSEL_ITEMS)
        carouselMode = data.getString(PushPayloadKeys.CAROUSEL_OPERATION_MODE)
            ?: DefaultValues.AUTO_CAROUSEL_MODE
        carouselItems = parseCarouselItemsFromString(rawCarouselItems)
    }

    companion object {
        private const val SELF_TAG = "CarouselPushTemplate"

        fun createCarouselPushTemplate(data: NotificationData): CarouselPushTemplate {
            val carouselMode = data.getString(PushPayloadKeys.CAROUSEL_OPERATION_MODE) ?: DefaultValues.AUTO_CAROUSEL_MODE
            return if (carouselMode == DefaultValues.AUTO_CAROUSEL_MODE) {
                AutoCarouselPushTemplate(data)
            } else
                ManualCarouselPushTemplate(data)
        }

        private fun parseCarouselItemsFromString(carouselItemsString: String?): MutableList<CarouselItem> {
            val carouselItems = mutableListOf<CarouselItem>()
            if (carouselItemsString.isNullOrEmpty()) {
                Log.debug(
                    LOG_TAG, SELF_TAG,
                    "No carousel items found in the push template."
                )
                return carouselItems
            }
            try {
                val jsonArray = JSONArray(carouselItemsString)
                for (i in 0 until jsonArray.length()) {
                    val item = jsonArray.getJSONObject(i)
                    val imageUri = item.getString(CarouselItemKeys.IMAGE)
                    val captionText = item.optString(CarouselItemKeys.TEXT, "")
                    val interactionUri = item.optString(CarouselItemKeys.URI, "")
                    carouselItems.add(
                        CarouselItem(imageUri, captionText, interactionUri)
                    )
                }
            } catch (e: JSONException) {
                Log.debug(
                    LOG_TAG, SELF_TAG,
                    "Failed to parse carousel items from the push template: ${e.localizedMessage}"
                )
            }
            return carouselItems
        }
    }
}
