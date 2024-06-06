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

import android.os.Bundle

/**
 * Class responsible for extracting notification data from an intent.
 */
internal class IntentData(private val extras: Bundle, val actionName: String?) : NotificationData {
    override fun getString(key: String): String? = extras.getString(key)
    override fun getInteger(key: String): Int? = extras.getString(key)?.toIntOrNull()
    override fun getBoolean(key: String): Boolean? = extras.getString(key)?.toBoolean()
    override fun getLong(key: String): Long? = extras.getString(key)?.toLongOrNull()
    override fun getBundle(): Bundle {
        return extras
    }
}
