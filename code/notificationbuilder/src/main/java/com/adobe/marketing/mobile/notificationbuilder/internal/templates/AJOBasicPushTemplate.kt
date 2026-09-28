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
 * Parses the raw [adb_template_properties] blob into a [JSONObject], returning null when the blob
 * is missing or malformed. Shared by the AJO template subclasses.
 */
internal fun parseAjoTemplateProperties(raw: String?): JSONObject? {
    if (raw.isNullOrEmpty()) return null
    return try {
        JSONObject(raw)
    } catch (e: JSONException) {
        Log.warning(
            LOG_TAG, "AJOTemplateProperties",
            "Failed to parse adb_template_properties: ${e.localizedMessage}"
        )
        null
    }
}

/**
 * Represents the AJO "ajo_basic" push template.
 *
 * All general fields (title, body, version, image url, icons, sound, action, etc.) are parsed by
 * [AEPPushTemplate] from the flat top-level FCM keys. The only template-specific field lives in the
 * [adb_template_properties] blob as the flat [adb_image_scale_type] key. This template has no large
 * side icon. Action buttons are read from the flat [adb_act] key.
 */
internal class AJOBasicPushTemplate(data: NotificationData) : AEPPushTemplate(data) {

    private val SELF_TAG = "AJOBasicPushTemplate"

    // Scale type for the main expanded image. Defaults to CENTER_CROP.
    internal val imgScaleType: String

    // Optional, action buttons for the notification as a raw string
    internal val actionButtonsString: String?

    // Optional, parsed list of action buttons
    internal val actionButtonsList: List<BasicPushTemplate.ActionButton>?

    init {
        val props: JSONObject? = parseAjoTemplateProperties(
            data.getString(PushPayloadKeys.AJO_TEMPLATE_PROPERTIES)
        )

        imgScaleType = props?.optString(AJOTemplatePropertyKeys.IMAGE_SCALE_TYPE)
            ?.takeIf { it.isNotEmpty() }
            ?: AJOTemplatePropertyKeys.ScaleType.CENTER_CROP

        // Action buttons come from the flat adb_act key, same as ACC/AJO today
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
