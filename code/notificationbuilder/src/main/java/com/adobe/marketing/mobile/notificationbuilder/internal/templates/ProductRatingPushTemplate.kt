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

import com.adobe.ui_utils.PushTemplateConstants
import com.adobe.marketing.mobile.notificationbuilder.internal.util.NotificationData
import com.adobe.marketing.mobile.services.Log
import org.json.JSONArray
import org.json.JSONObject

internal class ProductRatingPushTemplate(data: NotificationData) : AEPPushTemplate(data) {

    private val SELF_TAG = "ProductRatingPushTemplate"

    class RatingAction(val type: com.adobe.ui_utils.PushTemplateConstants.ActionType, val link: String?) {
        companion object {
            private const val SELF_TAG = "RatingAction"

            /**
             * Converts the json object representing the action on selecting the rating to a [RatingAction].
             *
             * @param jsonObject [JSONObject] containing the action details
             * @return an [RatingAction] or null if the conversion fails
             */
            fun from(jsonObject: JSONObject): RatingAction? {
                return try {
                    var uri: String? = null
                    val type = com.adobe.ui_utils.PushTemplateConstants.ActionType.valueOf(jsonObject.getString(
                        com.adobe.ui_utils.PushTemplateConstants.RatingAction.TYPE))
                    if (type == com.adobe.ui_utils.PushTemplateConstants.ActionType.WEBURL || type == com.adobe.ui_utils.PushTemplateConstants.ActionType.DEEPLINK) {
                        uri = jsonObject.getString(com.adobe.ui_utils.PushTemplateConstants.RatingAction.URI)
                    }
                    Log.trace(
                        com.adobe.ui_utils.PushTemplateConstants.LOG_TAG,
                        SELF_TAG,
                        "Creating a rating action with uri ($uri), and type ($type)."
                    )
                    RatingAction(type, uri)
                } catch (e: Exception) {
                    Log.warning(
                        com.adobe.ui_utils.PushTemplateConstants.LOG_TAG,
                        SELF_TAG,
                        "Exception in converting rating action json string to json object, Error : ${e.localizedMessage}."
                    )
                    null
                }
            }
        }
    }

    internal val ratingUnselectedIcon: String
    internal val ratingSelectedIcon: String
    internal val ratingActionString: String
    internal val ratingActionList: List<RatingAction>
    internal val ratingSelected: Int

    init {
        ratingUnselectedIcon = data.getRequiredString(com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.RATING_UNSELECTED_ICON)
        ratingSelectedIcon = data.getRequiredString(com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.RATING_SELECTED_ICON)
        ratingActionString = data.getRequiredString(com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.RATING_ACTIONS)
        ratingActionList = getRatingActionsFromString(ratingActionString)
            ?: throw IllegalArgumentException("Required field \"${com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.RATING_ACTIONS}\" is invalid.")
        if (ratingActionList.size < 3 || ratingActionList.size > 5) {
            throw IllegalArgumentException("\"${com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys.RATING_ACTIONS}\" field must have 3 to 5 rating actions")
        }
        ratingSelected = data.getInteger(com.adobe.ui_utils.PushTemplateConstants.IntentKeys.RATING_SELECTED)
            ?: com.adobe.ui_utils.PushTemplateConstants.ProductRatingKeys.RATING_UNSELECTED
    }

    private fun getRatingActionsFromString(ratingActionJsonString: String?): List<RatingAction>? {
        if (ratingActionJsonString.isNullOrEmpty()) {
            Log.debug(
                com.adobe.ui_utils.PushTemplateConstants.LOG_TAG,
                SELF_TAG,
                "Exception in converting rating uri json string to json array, Error :" +
                    " rating uris is null"
            )
            return null
        }
        val ratingActionList = mutableListOf<RatingAction>()
        try {
            val jsonArray = JSONArray(ratingActionJsonString)
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val ratingAction = RatingAction.from(jsonObject) ?: return null
                ratingActionList.add(ratingAction)
            }
        } catch (e: Exception) {
            Log.debug(
                com.adobe.ui_utils.PushTemplateConstants.LOG_TAG,
                SELF_TAG,
                "Exception in converting rating uri json string to json array, Error : ${e.localizedMessage}"
            )
            return null
        }
        return ratingActionList
    }
}
