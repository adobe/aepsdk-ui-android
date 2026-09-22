/*
  Copyright 2026 Adobe. All rights reserved.
  This file is licensed to you under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License. You may obtain a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0
  Unless required by applicable law or agreed to in writing, software distributed under
  the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR REPRESENTATIONS
  OF ANY KIND, either express or implied. See the License for the specific language
  governing permissions and limitations under the License.
*/

package com.adobe.marketing.mobile.notificationbuilder.internal

import org.junit.Assert.assertEquals
import org.junit.Test

class PushTemplateImageUtilsTest {

    // ---- calculateInSampleSize ----

    @Test
    fun `calculateInSampleSize returns 1 when source already fits the target box`() {
        assertEquals(1, PushTemplateImageUtils.calculateInSampleSize(700, 500, 720, 720))
        assertEquals(1, PushTemplateImageUtils.calculateInSampleSize(720, 720, 720, 720))
    }

    @Test
    fun `calculateInSampleSize downsamples large landscape source (power of two, stays above box)`() {
        // 4000x3000 into 720x720 -> 4 (decoded ~1000x750, still >= box)
        assertEquals(4, PushTemplateImageUtils.calculateInSampleSize(4000, 3000, 720, 720))
    }

    @Test
    fun `calculateInSampleSize downsamples square source just over the box`() {
        // 1440x1440 into 720x720 -> 2 (decoded 720x720)
        assertEquals(2, PushTemplateImageUtils.calculateInSampleSize(1440, 1440, 720, 720))
    }

    @Test
    fun `calculateInSampleSize handles rectangular device target (portrait source)`() {
        // portrait 2160x4320 into a 1080x768 box -> keeps both dims >= box
        // halfW=1080, halfH=2160: i=1 (1080>=1080 && 2160>=768) -> i=2 (540>=1080? no) => 2
        assertEquals(2, PushTemplateImageUtils.calculateInSampleSize(2160, 4320, 1080, 768))
    }

    @Test
    fun `calculateInSampleSize returns 1 for zero or negative dimensions`() {
        assertEquals(1, PushTemplateImageUtils.calculateInSampleSize(0, 0, 720, 720))
        assertEquals(1, PushTemplateImageUtils.calculateInSampleSize(1000, 1000, 0, 0))
        assertEquals(1, PushTemplateImageUtils.calculateInSampleSize(-10, -10, 720, 720))
    }

    @Test
    fun `calculateInSampleSize only downsamples the dimension that overflows`() {
        // wide banner 3000x300 into 720x720: width overflows, height doesn't.
        // halfW=1500, halfH=150: i=1 (1500>=720 && 150>=720? no) => stays 1
        assertEquals(1, PushTemplateImageUtils.calculateInSampleSize(3000, 300, 720, 720))
    }
}
