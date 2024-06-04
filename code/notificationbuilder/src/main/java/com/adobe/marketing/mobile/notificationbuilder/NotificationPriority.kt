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

package com.adobe.marketing.mobile.notificationbuilder

import androidx.core.app.NotificationCompat

enum class NotificationPriority(val value: Int, val stringValue: String) {
    PRIORITY_DEFAULT(NotificationCompat.PRIORITY_DEFAULT, "PRIORITY_DEFAULT"),
    PRIORITY_MIN(NotificationCompat.PRIORITY_MIN, "PRIORITY_MIN"),
    PRIORITY_LOW(NotificationCompat.PRIORITY_LOW, "PRIORITY_LOW"),
    PRIORITY_HIGH(NotificationCompat.PRIORITY_HIGH, "PRIORITY_HIGH"),
    PRIORITY_MAX(NotificationCompat.PRIORITY_MAX, "PRIORITY_MAX");

    companion object {
        private val mapByString = values().associateBy { it.stringValue }
        private val mapByValue = values().associateBy { it.value }

        /**
         * Returns the [NotificationPriority] enum for the given [priorityString].
         * If the [priorityString] is null or not found, returns [PRIORITY_DEFAULT].
         */
        @JvmStatic
        fun fromString(priorityString: String?): NotificationPriority =
            priorityString?.let { mapByString[it] } ?: PRIORITY_DEFAULT

        /**
         * Returns the [NotificationPriority] enum for the given [value].
         * If the [value] is null or not found, returns [PRIORITY_DEFAULT].
         */
        @JvmStatic
        fun fromValue(value: Int?): NotificationPriority =
            value?.let { mapByValue[it] } ?: PRIORITY_DEFAULT
    }
}
