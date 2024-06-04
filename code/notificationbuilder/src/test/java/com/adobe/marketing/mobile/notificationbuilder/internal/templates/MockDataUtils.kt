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
const val MOCKED_SMALL_ICON = "skipleft"
const val MOCKED_LARGE_ICON = "https://cdn-icons-png.flaticon.com/128/864/864639.png"
const val MOCKED_SMALL_ICON_COLOR = "#000000"
const val MOCKED_VISIBILITY = "PUBLIC"
const val MOCKED_PRIORITY = "PRIORITY_HIGH"
const val MOCKED_TICKER = "ticker"
const val MOCKED_TAG = "tag"
const val MOCKED_URI = "https://www.adobe.com"
const val MOCKED_CAROUSEL_LAYOUT_DATA = "[{\"img\":\"https://i.imgur.com/7ZolaOv.jpeg\",\"txt\":\"Basketball Shoes\"},{\"img\":\"https://i.imgur.com/mZvLuzU.jpg\",\"txt\":\"Red Jersey\",\"uri\":\"https://firefly.adobe.com/red_jersey\"},{\"img\":\"https://i.imgur.com/X5yjy09.jpg\",\"txt\":\"Volleyball\", \"uri\":\"https://firefly.adobe.com/volleyball\"},{\"img\":\"https://i.imgur.com/35B0mkh.jpg\",\"txt\":\"Basketball\",\"uri\":\"https://firefly.adobe.com/basketball\"},{\"img\":\"https://i.imgur.com/Cs5hmfb.jpg\",\"txt\":\"Black Batting Helmet\",\"uri\":\"https://firefly.adobe.com/black_helmet\"}]"
const val MOCKED_IMAGE_URI =
    "https://images.pexels.com/photos/260024/pexels-photo-260024.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"
const val MOCKED_ACTION_URI = "https://chess.com/games"
const val MOCKED_BASIC_TEMPLATE_BODY_EXPANDED = "Basic push template with action buttons."
const val MOCKED_ACTION_BUTTON_DATA =
    "[{\"label\":\"Go to chess.com\",\"uri\":\"https://chess.com/games/552\",\"type\":\"DEEPLINK\"},{\"label\":\"Open the app\",\"uri\":\"\",\"type\":\"OPENAPP\"}]"
const val MOCKED_BASIC_TEMPLATE_BODY = "Shall we play a game?"
const val MOCK_REMIND_LATER_TEXT = "remind me"
const val MOCK_REMIND_LATER_TIME = "1234567890"
const val MOCK_REMIND_LATER_DURATION = "6000"
const val MOCKED_MALFORMED_JSON_ACTION_BUTTON = "[" +
    "{\"label\":\"\",\"uri\":\"https://chess.com/games/552\",\"type\":\"DEEPLINK\"}," +
    "{}," +
    "{\"label\":\"Open the app\",\"uri\":\"\",\"type\":\"GO_TO_WEB_PAGE\"}," +
    "{\"label\":\"Go to chess.com\",\"uri\":\"https://chess.com/games/552\",\"type\":\"DEEPLINK\"}]"

fun <K, V> MutableMap<K, V>.removeKeysFromMap(listOfKeys: List<K>) {
    for (key in listOfKeys) {
        this.remove(key)
    }
}

fun <K, V> MutableMap<K, V>.removeKeysFromMap(vararg keys: K) {
    for (key in keys) {
        this.remove(key)
    }
}
fun <K, V> MutableMap<K, V>.replaceValueInMap(mapOfNewKeySet: Map<K, V>) {
    for ((key, value) in mapOfNewKeySet) {
        this[key] = value
    }
}

fun <K, V> MutableMap<K, V>.replaceValueInMap(vararg keyValues: Pair<K, V>) {
    for ((key, value) in keyValues) {
        this[key] = value
    }
}

internal fun provideMockedBasicPushTemplateWithRequiredData(isFromIntent: Boolean = false): BasicPushTemplate {
    val data: NotificationData
    if (isFromIntent) {
        val mockBundle = MockAEPPushTemplateDataProvider.getMockedBundleWithRequiredData()
        data = IntentData(mockBundle, null)
    } else {
        val dataMap = MockAEPPushTemplateDataProvider.getMockedDataMapWithRequiredData()
        data = MapData(dataMap)
    }
    return BasicPushTemplate(data)
}

internal fun provideMockedBasicPushTemplateWithAllKeys(isFromIntent: Boolean = false): BasicPushTemplate {
    val data: NotificationData
    if (isFromIntent) {
        val mockBundle = MockAEPPushTemplateDataProvider.getMockedAEPBundleWithAllKeys()
        data = IntentData(mockBundle, null)
    } else {
        val dataMap = MockAEPPushTemplateDataProvider.getMockedAEPDataMapWithAllKeys()
        data = MapData(dataMap)
    }
    return BasicPushTemplate(data)
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
    return CarouselPushTemplate(data) as ManualCarouselPushTemplate
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
    return CarouselPushTemplate(data) as AutoCarouselPushTemplate
}
