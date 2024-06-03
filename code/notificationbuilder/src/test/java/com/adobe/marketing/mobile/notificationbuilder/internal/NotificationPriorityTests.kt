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

package com.adobe.marketing.mobile.notificationbuilder.internal

import androidx.core.app.NotificationCompat
import org.junit.Assert.assertEquals
import org.junit.Test

class NotificationPriorityTests {

    @Test
    fun testEnumValues() {
        assertEquals(NotificationCompat.PRIORITY_DEFAULT, NotificationPriority.PRIORITY_DEFAULT.value)
        assertEquals("PRIORITY_DEFAULT", NotificationPriority.PRIORITY_DEFAULT.stringValue)

        assertEquals(NotificationCompat.PRIORITY_MIN, NotificationPriority.PRIORITY_MIN.value)
        assertEquals("PRIORITY_MIN", NotificationPriority.PRIORITY_MIN.stringValue)

        assertEquals(NotificationCompat.PRIORITY_LOW, NotificationPriority.PRIORITY_LOW.value)
        assertEquals("PRIORITY_LOW", NotificationPriority.PRIORITY_LOW.stringValue)

        assertEquals(NotificationCompat.PRIORITY_HIGH, NotificationPriority.PRIORITY_HIGH.value)
        assertEquals("PRIORITY_HIGH", NotificationPriority.PRIORITY_HIGH.stringValue)

        assertEquals(NotificationCompat.PRIORITY_MAX, NotificationPriority.PRIORITY_MAX.value)
        assertEquals("PRIORITY_MAX", NotificationPriority.PRIORITY_MAX.stringValue)
    }

    @Test
    fun testFromString() {

        assertEquals(NotificationPriority.PRIORITY_DEFAULT, NotificationPriority.fromString("PRIORITY_DEFAULT"))
        assertEquals(NotificationPriority.PRIORITY_MIN, NotificationPriority.fromString("PRIORITY_MIN"))
        assertEquals(NotificationPriority.PRIORITY_LOW, NotificationPriority.fromString("PRIORITY_LOW"))
        assertEquals(NotificationPriority.PRIORITY_HIGH, NotificationPriority.fromString("PRIORITY_HIGH"))
        assertEquals(NotificationPriority.PRIORITY_MAX, NotificationPriority.fromString("PRIORITY_MAX"))

        // verify invalid inputs
        assertEquals(NotificationPriority.PRIORITY_DEFAULT, NotificationPriority.fromString("INVALID"))
        assertEquals(NotificationPriority.PRIORITY_DEFAULT, NotificationPriority.fromString(""))
        assertEquals(NotificationPriority.PRIORITY_DEFAULT, NotificationPriority.fromString(null))
    }

    @Test
    fun testFromValue() {
        assertEquals(NotificationPriority.PRIORITY_DEFAULT, NotificationPriority.fromValue(NotificationCompat.PRIORITY_DEFAULT))
        assertEquals(NotificationPriority.PRIORITY_MIN, NotificationPriority.fromValue(NotificationCompat.PRIORITY_MIN))
        assertEquals(NotificationPriority.PRIORITY_LOW, NotificationPriority.fromValue(NotificationCompat.PRIORITY_LOW))
        assertEquals(NotificationPriority.PRIORITY_HIGH, NotificationPriority.fromValue(NotificationCompat.PRIORITY_HIGH))
        assertEquals(NotificationPriority.PRIORITY_MAX, NotificationPriority.fromValue(NotificationCompat.PRIORITY_MAX))

        // verify invalid inputs
        assertEquals(NotificationPriority.PRIORITY_DEFAULT, NotificationPriority.fromValue(999))
        assertEquals(NotificationPriority.PRIORITY_DEFAULT, NotificationPriority.fromValue(null))
        assertEquals(NotificationPriority.PRIORITY_DEFAULT, NotificationPriority.fromValue(-999))
    }
}
