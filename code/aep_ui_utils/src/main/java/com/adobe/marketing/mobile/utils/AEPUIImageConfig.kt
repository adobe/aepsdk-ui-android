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

package com.adobe.marketing.mobile.utils

import android.graphics.Matrix
import com.adobe.marketing.mobile.utils.AEPUIImageConstants.DEFAULT_BITMAP_QUALITY
import com.adobe.marketing.mobile.utils.AEPUIImageConstants.DEFAULT_DOWNLOAD_TIMEOUT_SECS

class AEPUIImageConfig private constructor(builder: Builder) {
    val urlList: List<String?>
    val downloadTimeoutInSeconds: Int
    val bitmapQuality: Int
    val bitmapWidth: Float
    val bitmapHeight: Float
    val scaleToFit: Matrix.ScaleToFit

    init {
        this.urlList = builder.urlList
        this.downloadTimeoutInSeconds = builder.downloadTimeoutInSeconds
        this.bitmapQuality = builder.bitmapQuality
        this.bitmapWidth = builder.bitmapWidth
        this.bitmapHeight = builder.bitmapHeight
        this.scaleToFit = builder.scaleToFit
    }

    class Builder(val bitmapWidth: Float, val bitmapHeight: Float) {
        var urlList: List<String?> = listOf()
        var downloadTimeoutInSeconds: Int = DEFAULT_DOWNLOAD_TIMEOUT_SECS
        var bitmapQuality: Int = DEFAULT_BITMAP_QUALITY
        var scaleToFit: Matrix.ScaleToFit = Matrix.ScaleToFit.CENTER

        fun urlList(urlList: List<String?>) = apply { this.urlList = urlList }
        fun downloadTimeoutInSeconds(downloadTimeoutInSeconds: Int) =
            apply { this.downloadTimeoutInSeconds = downloadTimeoutInSeconds }

        fun bitmapQuality(bitmapQuality: Int) = apply { this.bitmapQuality = bitmapQuality }
        fun scaleToFit(scaleToFit: Matrix.ScaleToFit) = apply { this.scaleToFit = scaleToFit }

        fun build() = AEPUIImageConfig(this)
    }
}
