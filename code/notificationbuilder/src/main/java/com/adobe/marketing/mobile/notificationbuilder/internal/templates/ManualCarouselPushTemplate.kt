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

import com.adobe.marketing.mobile.notificationbuilder.PushTemplateIntentConstants
import com.adobe.marketing.mobile.notificationbuilder.internal.PushTemplateConstants
import com.adobe.marketing.mobile.notificationbuilder.internal.util.IntentData
import com.adobe.marketing.mobile.notificationbuilder.internal.util.NotificationData

internal class ManualCarouselPushTemplate(data: NotificationData) : CarouselPushTemplate(data) {
    internal var intentAction: String? = null
        private set
    internal var centerImageIndex: Int = PushTemplateConstants.DefaultValues.NO_CENTER_INDEX_SET

    /**
     * Constructs a Manual Carousel Push Template from the provided data.
     * If the intent action is not null, then the data is from an intent.
     *
     * @param data Notification data
     */
    init {
        centerImageIndex = getDefaultCarouselIndex(carouselLayout)
        if (data is IntentData && data.actionName != null) {
            this.intentAction = data.actionName
            centerImageIndex = data.getInteger(PushTemplateIntentConstants.IntentKeys.CENTER_IMAGE_INDEX) ?: getDefaultCarouselIndex(carouselLayout)
        }
    }

    companion object {
        private fun getDefaultCarouselIndex(carouselLayoutType: String): Int {
            return if (carouselLayoutType == PushTemplateConstants.DefaultValues.FILMSTRIP_CAROUSEL_MODE) {
                PushTemplateConstants.DefaultValues.FILMSTRIP_CAROUSEL_CENTER_INDEX
            } else {
                PushTemplateConstants.DefaultValues.MANUAL_CAROUSEL_START_INDEX
            }
        }
    }
}
