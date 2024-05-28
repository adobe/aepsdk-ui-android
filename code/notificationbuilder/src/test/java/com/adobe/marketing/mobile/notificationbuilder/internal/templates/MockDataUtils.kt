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

import com.adobe.marketing.mobile.notificationbuilder.internal.util.IntentData
import com.adobe.marketing.mobile.notificationbuilder.internal.util.MapData
import com.adobe.marketing.mobile.notificationbuilder.internal.util.NotificationData

const val MOCKED_TITLE = "Mocked Title"
const val MOCKED_BODY = "Mocked Body"
const val MOCKED_PAYLOAD_VERSION = "1"
const val MOCKED_CAROUSEL_LAYOUT = "default"
const val MOCKED_BODY_TEXT_COLOR = "#FFFFFF"
const val MOCKED_SMALL_ICON = "chat_bubble"
const val MOCKED_LARGE_ICON = "https://cdn-icons-png.flaticon.com/128/864/864639.png"
const val MOCKED_SMALL_ICON_COLOR = "#000000"
const val MOCKED_VISIBILITY = "PUBLIC"
const val MOCKED_PRIORITY = 0
const val MOCKED_TICKER = "ticker"
const val MOCKED_TAG = "tag"
const val MOCKED_URI = "https://www.adobe.com"
const val MOCKED_CAROUSEL_LAYOUT_DATA = "[{\"img\":\"https://i.imgur.com/7ZolaOv.jpeg\",\"txt\":\"Basketball Shoes\"},{\"img\":\"https://i.imgur.com/mZvLuzU.jpg\",\"txt\":\"Red Jersey\",\"uri\":\"https://firefly.adobe.com/red_jersey\"},{\"img\":\"https://i.imgur.com/X5yjy09.jpg\",\"txt\":\"Volleyball\", \"uri\":\"https://firefly.adobe.com/volleyball\"},{\"img\":\"https://i.imgur.com/35B0mkh.jpg\",\"txt\":\"Basketball\",\"uri\":\"https://firefly.adobe.com/basketball\"},{\"img\":\"https://i.imgur.com/Cs5hmfb.jpg\",\"txt\":\"Black Batting Helmet\",\"uri\":\"https://firefly.adobe.com/black_helmet\"}]"

fun removeKeysFromMap(listOfKeys: List<String>, dataMap: MutableMap<String, Any>) {
    for (key in listOfKeys) {
        dataMap.remove(key)
    }
}

fun <K, V> replaceValueInMap(mapOfNewKeySet: Map<K, V>, dataMap: MutableMap<K, V>) {
    for ((key, value) in mapOfNewKeySet) {
        dataMap[key] = value
    }
}

internal fun provideMockedManualCarousalTemplate(isFromIntent: Boolean = false): ManualCarouselPushTemplate {
    val data: NotificationData
    if (isFromIntent) {
        val mockBundle = MockCarousalTemplateDataProvider.getMockedBundleWithManualCarouselData()
        data = IntentData(mockBundle, null)
    } else {
        val dataMap = MockCarousalTemplateDataProvider.getMockedMapWithManualCarouselData()
        data = MapData(dataMap)
    }
    return CarouselPushTemplate.createCarouselPushTemplate(data) as ManualCarouselPushTemplate
}

internal fun provideMockedAutoCarousalTemplate(isFromIntent: Boolean = false): AutoCarouselPushTemplate {
    val data: NotificationData
    if (isFromIntent) {
        val mockBundle = MockCarousalTemplateDataProvider.getMockedBundleWithAutoCarouselData()
        data = IntentData(mockBundle, null)
    } else {
        val dataMap = MockCarousalTemplateDataProvider.getMockedMapWithAutoCarouselData()
        data = MapData(dataMap)
    }
    return CarouselPushTemplate.createCarouselPushTemplate(data) as AutoCarouselPushTemplate
}
