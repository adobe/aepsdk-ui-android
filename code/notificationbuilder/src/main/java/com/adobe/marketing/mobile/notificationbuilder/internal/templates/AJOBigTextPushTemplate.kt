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
import com.adobe.marketing.mobile.notificationbuilder.PushTemplateConstants.AJOTemplatePropertyKeys
import com.adobe.marketing.mobile.notificationbuilder.PushTemplateConstants.LOG_TAG
import com.adobe.marketing.mobile.notificationbuilder.PushTemplateConstants.PushPayloadKeys
import com.adobe.marketing.mobile.notificationbuilder.internal.util.NotificationData
import com.adobe.marketing.mobile.services.Log
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/**
 * Represents the AJO "ajo_bigtext" push template.
 *
 * Renders a BigText-style notification with optional large-icon support in both collapsed and
 * expanded states. There is no hero image. General fields are parsed by [AEPPushTemplate].
 *
 * Body semantics:
 *  - The flat [adb_body] key holds the full long text shown in the expanded state — exposed as the
 *    inherited [body].
 *  - The blob [adb_collapsed_text] key holds the short text shown in the collapsed state
 *    ([collapsedText]); it falls back to [body] when absent.
 *
 * The large side icon url comes from the blob [adb_large_icon] key ([largeIconUrl], no scale type).
 * Note: the inherited [largeIcon] reads the flat key and is unused by this template.
 */
internal class AJOBigTextPushTemplate(data: NotificationData) : AEPPushTemplate(data) {

    private val SELF_TAG = "AJOBigTextPushTemplate"

    // Short text shown in the collapsed state. Falls back to `body` when absent.
    internal val collapsedText: String

    // Optional, large side icon url sourced from the blob adb_large_icon key.
    internal val largeIconUrl: String?

    // Optional, action buttons for the notification as a raw string
    internal val actionButtonsString: String?

    // Optional, parsed list of action buttons
    internal val actionButtonsList: List<BasicPushTemplate.ActionButton>?

    init {
        val props: JSONObject? = parseAjoTemplateProperties(
            data.getString(PushPayloadKeys.AJO_TEMPLATE_PROPERTIES)
        )

        collapsedText = props?.optString(AJOTemplatePropertyKeys.COLLAPSED_TEXT)
            ?.takeIf { it.isNotEmpty() }
            ?: body

        largeIconUrl = props?.optString(AJOTemplatePropertyKeys.LARGE_ICON)
            ?.takeIf { it.isNotEmpty() }

        actionButtonsString = data.getString(PushPayloadKeys.ACTION_BUTTONS)
        actionButtonsList = getActionButtonsFromString(actionButtonsString)
    }

    @VisibleForTesting
    internal fun getActionButtonsFromString(actionButtons: String?): List<BasicPushTemplate.ActionButton>? {
        if (actionButtons == null) {
            Log.debug(
                LOG_TAG, SELF_TAG,
                "Exception in converting actionButtons json string to json object, Error : actionButtons is null"
            )
            return null
        }
        val actionButtonList = mutableListOf<BasicPushTemplate.ActionButton>()
        try {
            val jsonArray = JSONArray(actionButtons)
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val button = BasicPushTemplate.ActionButton.getActionButtonFromJSONObject(jsonObject)
                    ?: continue
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
