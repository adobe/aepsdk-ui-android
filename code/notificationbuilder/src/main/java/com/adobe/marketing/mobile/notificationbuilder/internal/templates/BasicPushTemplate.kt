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

import androidx.annotation.VisibleForTesting
import com.adobe.marketing.mobile.notificationbuilder.internal.util.NotificationData
import com.adobe.marketing.mobile.services.Log
import com.adobe.ui_utils.PushTemplateConstants.ActionButtons
import com.adobe.ui_utils.PushTemplateConstants.ActionType
import com.adobe.ui_utils.PushTemplateConstants.LOG_TAG
import com.adobe.ui_utils.PushTemplateConstants.PushPayloadKeys
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

internal class BasicPushTemplate(data: NotificationData) : AEPPushTemplate(data) {

    private val SELF_TAG = "BasicPushTemplate"

    /** Class representing the action button with label, link and type  */
    class ActionButton(val label: String, val link: String?, type: String?) {
        val type: ActionType

        init {
            this.type = try {
                ActionType.valueOf(
                    type ?: ActionType.NONE.name
                )
            } catch (e: IllegalArgumentException) {
                Log.warning(
                    LOG_TAG, SELF_TAG,
                    "Invalid action button type provided, defaulting to NONE. Error : ${e.localizedMessage}"
                )
                ActionType.NONE
            }
        }

        companion object {
            private const val SELF_TAG = "ActionButton"

            /**
             * Converts the json object representing an action button to an [ActionButton].
             * Action button must have a non-empty label, type and uri
             *
             * @param jsonObject [JSONObject] containing the action button details
             * @return an [ActionButton] or null if the conversion fails
             */
            fun getActionButtonFromJSONObject(jsonObject: JSONObject): ActionButton? {
                return try {
                    val label = jsonObject.getString(ActionButtons.LABEL)
                    if (label.isEmpty()) {
                        Log.debug(
                            LOG_TAG, SELF_TAG, "Label is empty"
                        )
                        return null
                    }
                    var uri: String? = null
                    val type = jsonObject.getString(ActionButtons.TYPE)
                    if (type == ActionType.WEBURL.name || type == ActionType.DEEPLINK.name) {
                        uri = jsonObject.optString(ActionButtons.URI)
                    }
                    Log.trace(
                        LOG_TAG, SELF_TAG,
                        "Creating an ActionButton with label ($label), uri ($uri), and type ($type)."
                    )
                    ActionButton(label, uri, type)
                } catch (e: JSONException) {
                    Log.warning(
                        LOG_TAG, SELF_TAG,
                        "Exception in converting actionButtons json string to json object, Error : ${e.localizedMessage}."
                    )
                    null
                }
            }
        }
    }

    // Optional, action buttons for the notification
    internal val actionButtonsString: String?

    // Optional, list of ActionButton for the notification
    internal val actionButtonsList: List<ActionButton>?

    // Optional, If present, show a "remind later" button using the value provided as its label
    internal val remindLaterText: String?

    // Optional, If present, schedule this notification to be re-delivered at this epoch timestamp (in seconds) provided.
    internal val remindLaterTimestamp: Long?

    // Optional, If present, schedule this notification to be re-delivered after this provided time (in seconds).
    internal val remindLaterDuration: Int?

    /**
     * Initializes the push template with the given NotificationData.
     */
    init {
        actionButtonsString = data.getString(PushPayloadKeys.ACTION_BUTTONS)
        actionButtonsList = getActionButtonsFromString(actionButtonsString)
        remindLaterText = data.getString(PushPayloadKeys.REMIND_LATER_TEXT)
        remindLaterTimestamp = data.getLong(PushPayloadKeys.REMIND_LATER_TIMESTAMP)
        remindLaterDuration = data.getInteger(PushPayloadKeys.REMIND_LATER_DURATION)
    }

    /**
     * Converts the string containing json array of actionButtons to a list of [ActionButton].
     *
     * @param actionButtons [String] containing the action buttons json string
     * @return a list of [ActionButton] or null if the conversion fails
     */
    @VisibleForTesting
    internal fun getActionButtonsFromString(actionButtons: String?): List<ActionButton>? {
        if (actionButtons == null) {
            Log.debug(
                LOG_TAG, SELF_TAG,
                "Exception in converting actionButtons json string to json object, Error :" +
                    " actionButtons is null"
            )
            return null
        }
        val actionButtonList = mutableListOf<ActionButton>()
        try {
            val jsonArray = JSONArray(actionButtons)
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val button = ActionButton.getActionButtonFromJSONObject(jsonObject) ?: continue
                actionButtonList.add(button)
            }
        } catch (e: JSONException) {
            Log.warning(
                LOG_TAG, SELF_TAG,
                "Exception in converting actionButtons json string to json object, Error : ${e.localizedMessage}"
            )
            return null
        }
        return actionButtonList
    }
}
