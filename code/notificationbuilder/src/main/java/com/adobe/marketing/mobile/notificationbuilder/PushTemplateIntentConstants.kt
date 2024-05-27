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

object PushTemplateIntentConstants {
    object IntentActions {
        const val FILMSTRIP_LEFT_CLICKED = "filmstrip_left"
        const val FILMSTRIP_RIGHT_CLICKED = "filmstrip_right"
        const val REMIND_LATER_CLICKED = "remind_clicked"
        const val MANUAL_CAROUSEL_LEFT_CLICKED = "manual_left"
        const val MANUAL_CAROUSEL_RIGHT_CLICKED = "manual_right"
        const val INPUT_RECEIVED = "input_received"
    }

    object IntentKeys {
        const val CENTER_IMAGE_INDEX = "centerImageIndex"
        const val IMAGE_URLS = "imageUrls"
        const val IMAGE_CAPTIONS = "imageCaptions"
        const val IMAGE_CLICK_ACTIONS = "imageClickActions"
    }
}
