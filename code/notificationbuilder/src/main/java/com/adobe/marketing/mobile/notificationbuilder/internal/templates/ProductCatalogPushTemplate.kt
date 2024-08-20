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

import com.adobe.marketing.mobile.notificationbuilder.internal.util.NotificationData
import com.adobe.marketing.mobile.services.Log
import com.adobe.marketing.mobile.utils.PushTemplateConstants
import com.adobe.marketing.mobile.utils.PushTemplateConstants.CatalogItemKeys
import com.adobe.marketing.mobile.utils.PushTemplateConstants.DefaultValues
import com.adobe.marketing.mobile.utils.PushTemplateConstants.LOG_TAG
import com.adobe.marketing.mobile.utils.PushTemplateConstants.PushPayloadKeys
import org.json.JSONArray
import org.json.JSONException

internal class ProductCatalogPushTemplate(data: NotificationData) : AEPPushTemplate(data) {
    // Required, Text to be shown on the CTA button
    internal val ctaButtonText: String

    // Required, Color for the CTA button. Represented as six character hex, e.g. 00FF00
    internal val ctaButtonColor: String

    // Required, Color for the CTA button text. Represented as six character hex, e.g. 00FF00
    internal val ctaButtonTextColor: String

    // Required, URI to be handled when the user clicks the CTA button
    internal val ctaButtonUri: String

    // Required, Determines if the layout of the catalog goes left-to-right or top-to-bottom.
    // Value will either be "horizontal" (left-to-right) or "vertical" (top-to-bottom).
    internal val displayLayout: String

    // Required, Three entries describing the items in the product catalog.
    // The value is an encoded JSON string.
    internal val rawCatalogItems: String

    // Required, One or more items in the product catalog defined by the CatalogItem class
    internal val catalogItems: MutableList<CatalogItem>

    internal var currentIndex: Int

    data class CatalogItem(
        // Required, Text to use in the title if this product is selected
        val title: String,

        // Required, Text to use in the body if this product is selected
        val body: String,

        // Required, URI to an image to use in notification when this product is selected
        val img: String,

        // Required, Price of this product to display when the notification is selected
        val price: String,

        // Required, URI to be handled when the user clicks the large image of the selected item
        val uri: String
    )

    /**
     * Constructs a Product Catalog push template with the given NotificationData.
     */
    init {
        ctaButtonText = data.getRequiredString(PushPayloadKeys.CATALOG_CTA_BUTTON_TEXT)
        ctaButtonColor = data.getRequiredString(PushPayloadKeys.CATALOG_CTA_BUTTON_COLOR)
        ctaButtonTextColor = data.getRequiredString(PushPayloadKeys.CATALOG_CTA_BUTTON_TEXT_COLOR)
        ctaButtonUri = data.getRequiredString(PushPayloadKeys.CATALOG_CTA_BUTTON_URI)
        displayLayout = data.getRequiredString(PushPayloadKeys.CATALOG_LAYOUT)
        rawCatalogItems = data.getRequiredString(PushPayloadKeys.CATALOG_ITEMS)
        catalogItems = parseCatalogItemsFromString(rawCatalogItems)
        currentIndex = data.getInteger(PushTemplateConstants.IntentKeys.CATALOG_ITEM_INDEX)
            ?: DefaultValues.PRODUCT_CATALOG_START_INDEX
    }

    companion object {
        private const val SELF_TAG = "ProductCatalogPushTemplate"

        private fun parseCatalogItemsFromString(catalogItemsString: String?): MutableList<CatalogItem> {
            val catalogItems = mutableListOf<CatalogItem>()
            val jsonArray: JSONArray?
            try {
                jsonArray = JSONArray(catalogItemsString)
            } catch (e: JSONException) {
                Log.error(
                    LOG_TAG, SELF_TAG,
                    "Exception occurred when creating json array from the catalog items string: ${e.localizedMessage}"
                )
                throw IllegalArgumentException("Catalog items string containing a valid json array was not found.")
            }

            // fast fail if the array is not the expected size
            if (jsonArray.length() != 3) {
                throw IllegalArgumentException("3 catalog items are required for a Product Catalog notification.")
            }

            for (i in 0 until jsonArray.length()) {
                try {
                    val item = jsonArray.getJSONObject(i)
                    // all values are required for a catalog item. if any are missing we have an invalid catalog item and we know the notification as a whole is invalid
                    // as three catalog items are required.
                    val title = item.getString(CatalogItemKeys.TITLE)
                    val body = item.getString(CatalogItemKeys.BODY)
                    val image = item.getString(CatalogItemKeys.IMAGE)
                    val price = item.getString(CatalogItemKeys.PRICE)
                    val uri = item.getString(CatalogItemKeys.URI)

                    catalogItems.add(
                        CatalogItem(
                            title,
                            body,
                            image,
                            price,
                            uri
                        )
                    )
                } catch (e: JSONException) {
                    Log.error(
                        LOG_TAG,
                        SELF_TAG,
                        "Failed to parse catalog item at index $i: ${e.localizedMessage}"
                    )
                    throw IllegalArgumentException("3 catalog items are required for a Product Catalog notification.")
                }
            }
            return catalogItems
        }
    }
}
