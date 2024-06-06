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
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.res.Resources
import android.net.Uri
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockedStatic
import org.mockito.Mockito.mock
import org.mockito.Mockito.mockStatic
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any

class AppResourceExtensionsTest {

    @Mock
    private lateinit var mockContext: Context

    @Mock
    private lateinit var mockPackageManager: PackageManager

    @Mock
    private lateinit var mockResources: Resources

    @Mock
    private lateinit var mockUri: Uri
    private lateinit var mockedStaticUri: MockedStatic<Uri>

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        mockedStaticUri = mockStatic(Uri::class.java)
        mockedStaticUri.`when`<Any> { Uri.parse(any()) }.thenReturn(mockUri)
        `when`(mockContext.packageManager).thenReturn(mockPackageManager)
        `when`(mockContext.resources).thenReturn(mockResources)
    }

    @After
    fun teardown() {
        mockedStaticUri.close()
    }

    @Test
    fun `test getIconWithResourceName for valid icon name`() {
        val iconName = "skipleft"
        `when`(
            mockContext.resources.getIdentifier(
                iconName,
                "drawable",
                mockContext.packageName
            )
        ).thenReturn(1234)

        val iconId = mockContext.getIconWithResourceName(iconName)
        assertEquals(1234, iconId)
    }

    @Test
    fun `test getIconWithResourceName for invalid icon name`() {
        `when`(
            mockContext.resources.getIdentifier(
                "",
                "drawable",
                mockContext.packageName
            )
        ).thenReturn(0)
        `when`(
            mockContext.resources.getIdentifier(
                null,
                "drawable",
                mockContext.packageName
            )
        ).thenReturn(0)
        `when`(
            mockContext.resources.getIdentifier(
                "invalid_icon",
                "drawable",
                mockContext.packageName
            )
        ).thenReturn(0)

        val emptyIconId = mockContext.getIconWithResourceName("")
        val nullIconId = mockContext.getIconWithResourceName(null)
        val invalidIconId = mockContext.getIconWithResourceName("invalid_icon")
        assertEquals(0, emptyIconId)
        assertEquals(0, nullIconId)
        assertEquals(0, invalidIconId)
    }

    @Test
    fun `test getDefaultAppIcon`() {
        `when`(mockPackageManager.getApplicationInfo(mockContext.packageName, 0)).thenReturn(
            mock(
                ApplicationInfo::class.java
            ).apply { icon = 1234 }
        )

        val defaultAppIcon = mockContext.getDefaultAppIcon()
        assertEquals(1234, defaultAppIcon)
    }

    @Test
    fun `test getDefaultAppIcon when PackageManager throws NameNotFoundException`() {
        val packageName = "com.adobe.marketing.mobile.notificationbuilder"
        `when`(mockContext.packageName).thenReturn(packageName)
        `when`(
            mockPackageManager.getApplicationInfo(
                packageName,
                0
            )
        ).thenThrow(PackageManager.NameNotFoundException())

        val iconId = mockContext.getDefaultAppIcon()
        assertEquals(-1, iconId)
    }

    @Test
    fun `test getSoundUriForResourceName for valid sound resource`() {
        val expectedUri =
            Uri.parse("android.resource://com.adobe.marketing.mobile.notificationbuilder.test/raw/test_sound")
        val resultUri = mockContext.getSoundUriForResourceName("test_sound")
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
