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

import com.adobe.marketing.mobile.notificationbuilder.internal.PushTemplateConstants.PushPayloadKeys
import com.adobe.marketing.mobile.notificationbuilder.internal.util.NotificationData

internal class ZeroBezelPushTemplate(data: NotificationData) : AEPPushTemplate(data) {

    internal enum class ZeroBezelStyle(val collapsedStyle: String) {
        IMAGE("img"),
        TEXT("txt");

        companion object {
            private val zeroBezelStyleMap = values().associateBy { it.collapsedStyle }
            internal fun getCollapsedStyleFromString(style: String): ZeroBezelStyle {
                return zeroBezelStyleMap[style] ?: TEXT
            }
        }
    }

    internal var collapsedStyle: ZeroBezelStyle
        private set

    /**
     * Constructs a Zero Bezel Push Template from the provided data.
     *
     * @param data Notification data
     */
    init {
        collapsedStyle = ZeroBezelStyle
            .getCollapsedStyleFromString(
                data.getString(PushPayloadKeys.ZERO_BEZEL_COLLAPSED_STYLE) ?: "txt"
            )
    }
}
