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
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AEPUIImageConfigTest {
    @Test
    fun `builder sets values correctly`() {
        val urlList = listOf("https://example.com/image1.png", "https://example.com/image2.png")
        val downloadTimeoutInSeconds = 10
        val bitmapQuality = 80
        val bitmapWidth = 300f
        val bitmapHeight = 200f
        val scaleToFit = Matrix.ScaleToFit.CENTER

        val config = AEPUIImageConfig.Builder(bitmapWidth, bitmapHeight)
            .urlList(urlList)
            .downloadTimeoutInSeconds(downloadTimeoutInSeconds)
            .bitmapQuality(bitmapQuality)
            .scaleToFit(scaleToFit)
            .build()

        assertEquals(urlList, config.urlList)
        assertEquals(downloadTimeoutInSeconds, config.downloadTimeoutInSeconds)
        assertEquals(bitmapQuality, config.bitmapQuality)
        assertEquals(bitmapWidth, config.bitmapWidth, 0.0f)
        assertEquals(bitmapHeight, config.bitmapHeight, 0.0f)
        assertEquals(scaleToFit, config.scaleToFit)
    }

    @Test
    fun `builder uses default values when not set`() {
        val bitmapWidth = 300f
        val bitmapHeight = 200f

        val config = AEPUIImageConfig.Builder(bitmapWidth, bitmapHeight).build()

        assertTrue(config.urlList.isEmpty())
        assertEquals(DEFAULT_DOWNLOAD_TIMEOUT_SECS, config.downloadTimeoutInSeconds)
        assertEquals(DEFAULT_BITMAP_QUALITY, config.bitmapQuality)
        assertEquals(Matrix.ScaleToFit.CENTER, config.scaleToFit)
    }

    @Test
    fun `builder handles null urlList correctly`() {
        val urlList = listOf(null, "https://example.com/image2.png")
        val config = AEPUIImageConfig.Builder(300f, 200f).urlList(urlList).build()
        assertEquals(urlList, config.urlList)
    }
}
