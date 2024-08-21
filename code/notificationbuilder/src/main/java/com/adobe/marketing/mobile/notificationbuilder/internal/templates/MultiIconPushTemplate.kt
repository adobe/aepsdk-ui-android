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

import com.adobe.marketing.mobile.notificationbuilder.PushTemplateConstants
import com.adobe.marketing.mobile.notificationbuilder.PushTemplateConstants.DEFAULT_DELETE_ICON_NAME
import com.adobe.marketing.mobile.notificationbuilder.internal.util.NotificationData
import com.adobe.marketing.mobile.services.Log
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

internal class MultiIconPushTemplate(data: NotificationData) : AEPPushTemplate(data) {

    private val SELF_TAG = "MultiIconNotificationTemplate"
    data class MultiIconTemplateItem(
        val iconUrl: String,
        val actionType: PushTemplateConstants.ActionType,
        val actionUri: String?
    )

    internal val templateItemList: MutableList<MultiIconTemplateItem>
    internal var cancelIcon: String? = null

    init {
        val itemsJson = data.getRequiredString(PushTemplateConstants.PushPayloadKeys.MULTI_ICON_ITEMS)
        templateItemList = getTemplateItemList(itemsJson)
            ?: throw IllegalArgumentException("Required field \"${PushTemplateConstants.PushPayloadKeys.MULTI_ICON_ITEMS}\" is invalid.")

        if (templateItemList.size < PushTemplateConstants.DefaultValues.ICON_TEMPLATE_MIN_IMAGE_COUNT ||
            templateItemList.size > PushTemplateConstants.DefaultValues.ICON_TEMPLATE_MAX_IMAGE_COUNT
        ) {
            throw IllegalArgumentException("\"${PushTemplateConstants.PushPayloadKeys.MULTI_ICON_ITEMS}\" field must have 3 to 5 valid items")
        }

        cancelIcon = data.getString(PushTemplateConstants.PushPayloadKeys.MULTI_ICON_CLOSE_BUTTON)
        if (cancelIcon.isNullOrEmpty()) {
            cancelIcon = DEFAULT_DELETE_ICON_NAME
        }
    }

    private fun getTemplateItemList(templateIconListJsonString: String?): MutableList<MultiIconTemplateItem>? {
        if (templateIconListJsonString.isNullOrEmpty()) {
            Log.warning(
                PushTemplateConstants.LOG_TAG,
                SELF_TAG,
                "Exception in converting rating uri json string to json array, Error :" +
                    " templateIconList Json String is null or empty"
            )
            return null
        }
        val iconItemsList = mutableListOf<MultiIconTemplateItem>()
        try {
            val jsonArray = JSONArray(templateIconListJsonString)
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val multiIconAction = getIconItemFromJsonObject(jsonObject)
                multiIconAction?.let { iconItemsList.add(it) }
            }
        } catch (e: JSONException) {
            Log.debug(
                PushTemplateConstants.LOG_TAG,
                SELF_TAG,
                "Exception in converting template action json string to json array, Error : ${e.localizedMessage}"
            )
            return null
        }
        return iconItemsList
    }

    private fun getIconItemFromJsonObject(jsonObject: JSONObject): MultiIconTemplateItem? {
        return try {
            val imageUri = jsonObject.getString(PushTemplateConstants.MultiIconTemplateKeys.IMG)
            // In case of invalid image URI, return null as icon is mandatory
            if (imageUri.isNullOrEmpty()) {
                Log.debug(
                    PushTemplateConstants.LOG_TAG,
                    SELF_TAG,
                    "Image uri is empty, cannot create icon item."
                )
                return null
            }
            var uri: String? = null
            val actionTypeString = jsonObject.getString(PushTemplateConstants.MultiIconTemplateKeys.TYPE)
            var actionType = if (actionTypeString.isNullOrEmpty()) {
                PushTemplateConstants.ActionType.NONE
            } else {
                PushTemplateConstants.ActionType.valueOf(actionTypeString)
            }
            if (actionType == PushTemplateConstants.ActionType.WEBURL || actionType == PushTemplateConstants.ActionType.DEEPLINK) {
                uri = jsonObject.getString(PushTemplateConstants.MultiIconTemplateKeys.URI)
                if (uri.isNullOrEmpty()) {
                    Log.debug(
                        PushTemplateConstants.LOG_TAG,
                        SELF_TAG,
                        "Uri is empty for action type $actionType, cannot create icon item."
                    )
                    return null
                }
            }

            MultiIconTemplateItem(imageUri, actionType, uri)
        } catch (e: Exception) {
            null
        }
    }
}
