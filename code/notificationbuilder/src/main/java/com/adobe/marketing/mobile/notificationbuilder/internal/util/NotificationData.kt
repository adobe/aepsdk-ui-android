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

// Interface for abstracting the source of notification properties
interface NotificationData {

    /**
     * Returns the string value for the given key, or throws an exception if the key is not found or the value is null.
     *
     * @param key the key to retrieve the string value
     * @return the string value for the given key
     * @throws IllegalArgumentException if the key is not found or the value is null
     */
    fun getRequiredString(key: String): String {
        return getString(key)
            ?: throw IllegalArgumentException("Required push template key $key not found or null")
    }

    /**
     * Returns the string value for the given key.
     *
     * @param key the key to retrieve the string value
     * @return the string value for the given key, or null if the key is not found
     */
    fun getString(key: String): String?

    /**
     * Returns the integer value for the given key.
     *
     * @param key the key to retrieve the integer value
     * @return the integer value for the given key, or null if the key is not found or the value is not an integer
     */
    fun getInteger(key: String): Int?

    /**
     * Returns the boolean value for the given key.
     *
     * @param key the key to retrieve the boolean value
     * @return the boolean value for the given key, or null if the key is not found or the value is not a boolean
     */
    fun getBoolean(key: String): Boolean?

    /**
     * Returns the long value for the given key.
     *
     * @param key the key to retrieve the long value
     * @return the long value for the given key, or null if the key is not found or the value is not a long
     */
    fun getLong(key: String): Long?
}
