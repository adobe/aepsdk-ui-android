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

package com.adobe.marketing.mobile.notificationbuilder.internal.util

import com.adobe.marketing.mobile.util.DataReader

/**
 * Class responsible for extracting notification data from Remote Message Map.
 */
internal class MapData(private val data: Map<String, String>) : NotificationData {
    override fun getString(key: String): String? = DataReader.optString(data, key, null)
    override fun getInteger(key: String): Int? =
        DataReader.optString(data, key, null)?.toIntOrNull()

    override fun getBoolean(key: String): Boolean? =
        DataReader.optString(data, key, null)?.toBoolean()

    override fun getLong(key: String): Long? = DataReader.optString(data, key, null)?.toLongOrNull()
}
