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

package com.adobe.marketing.mobile.notificationbuilder.testapp.notificationBuilder

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException

object FileUtil {
    fun getFilesInPath(context: Context, path: String): List<String> {
        return try {
            context.assets.list(path)?.toList() ?: emptyList()
        } catch (e: IOException) {
            emptyList()
        }
    }

    fun readNotificationData(context: Context, template: Template, fileName: String): Map<String, String> {
        val jsonFilePath = "${template.directoryName}/$fileName"
        val jsonContent = context.assets.open(jsonFilePath).bufferedReader().use { it.readText() }
        val type = object : TypeToken<Map<String, String>>() {}.type
        return Gson().fromJson(jsonContent, type)
    }
}