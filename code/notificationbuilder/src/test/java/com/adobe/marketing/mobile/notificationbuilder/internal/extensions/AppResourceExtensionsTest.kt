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

package com.adobe.marketing.mobile.notificationbuilder.internal.extensions

import android.content.Context
import android.net.Uri
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [31])
class AppResourceExtensionsTest {

    private lateinit var mockContext: Context

    @Before
    fun setup() {
        mockContext = RuntimeEnvironment.getApplication()
    }

    @Test
    fun `test getIconWithResourceName for valid icon name`() {
        val iconName = "skipleft"
        val expectedIconId =
            mockContext.resources.getIdentifier(iconName, "drawable", mockContext.packageName)
        val iconId = mockContext.getIconWithResourceName(iconName)
        assertEquals(expectedIconId, iconId)
    }

    @Test
    fun `test getIconWithResourceName for invalid icon name`() {
        val emptyIconId = mockContext.getIconWithResourceName("")
        val nullIconId = mockContext.getIconWithResourceName(null)
        val invalidIconId = mockContext.getIconWithResourceName("invalid_icon")
        assertEquals(0, emptyIconId)
        assertEquals(0, nullIconId)
        assertEquals(0, invalidIconId)
    }

    @Test
    fun `test getDefaultAppIcon`() {
        val defaultAppIcon = mockContext.getDefaultAppIcon()
        val expectedIcon = mockContext.applicationInfo.icon
        assertEquals(expectedIcon, defaultAppIcon)
    }

    @Test
    fun `test getSoundUriForResourceName`() {
        val testSound = "test_sound"
        val expectedUri =
            Uri.parse("android.resource://com.adobe.marketing.mobile.notificationbuilder.test/raw/$testSound")
        val resultUri = mockContext.getSoundUriForResourceName(testSound)
        assertEquals(expectedUri, resultUri)
    }

    @Test
    fun `test getSoundUriForResourceName for null sound name`() {
        val expectedUri =
            Uri.parse("android.resource://com.adobe.marketing.mobile.notificationbuilder.test/raw/")
        val resultUri = mockContext.getSoundUriForResourceName(null)
        assertEquals(expectedUri, resultUri)
    }
}