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

package com.adobe.marketing.mobile.notificationbuilder.testapp.notificationBuilder

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.Manifest
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.remember
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import com.adobe.marketing.mobile.notificationbuilder.NotificationBuilder
import com.adobe.marketing.mobile.notificationbuilder.testapp.AppConstants.NotificationBuilderConstants
import com.adobe.marketing.mobile.notificationbuilder.testapp.AppConstants.SharedPreferenceKeys
import com.adobe.marketing.mobile.notificationbuilder.testapp.ui.theme.AepsdkTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UINotificationBuilderActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AepsdkTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    NotificationPicker(this@UINotificationBuilderActivity)
                }
            }
        }
    }
}

@Composable
fun NotificationPicker(activity: Activity) {
    Column {
        Button(onClick = {
            val files = FileUtil.getFilesInPath(activity, "basic")
            println(files)
            activity.finish() })
        { Text(text = "Back",) }
        Text(
            text = "Select a template",
            modifier = Modifier.padding(16.dp),
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
        )
        NotificationJSONSelector(activity)
    }
}

@Composable
fun NotificationJSONSelector(activity: Activity) {
    val PLACEHOLDER_SELECT_FILE = "Select JSON ▼"
    val sharedPreferences = activity.getSharedPreferences(SharedPreferenceKeys.NAME, Context.MODE_PRIVATE)
    val selectedTemplate = remember { mutableStateOf(
        Template.valueOf(
        sharedPreferences.getString(SharedPreferenceKeys.SELECTED_TEMPLATE, Template.Timer.displayName) ?: Template.Timer.displayName
    )) }
    val expanded = remember { mutableStateOf(false) }
    val selectedFile = remember { mutableStateOf(sharedPreferences.getString(SharedPreferenceKeys.SELECTED_FILE, PLACEHOLDER_SELECT_FILE) ?: PLACEHOLDER_SELECT_FILE) }
    var files = FileUtil.getFilesInPath(activity, selectedTemplate.value.directoryName)

    Column {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            items(Template.values()) { template ->
                RadioButtonWithLabel(
                    selected = template == selectedTemplate.value,
                    onClick = {
                        selectedTemplate.value = template
                        with(sharedPreferences.edit()) {
                            putString(SharedPreferenceKeys.SELECTED_TEMPLATE, template.name)
                            apply()
                        }
                        selectedFile.value = PLACEHOLDER_SELECT_FILE
                        files = FileUtil.getFilesInPath(activity, template.directoryName)
                    },
                    text = " ${template.displayName}"
                )
            }
        }
        // Dropdown Menu for selecting JSON file
        Box(Modifier.fillMaxWidth().padding(16.dp).wrapContentSize(Alignment.Center)) {
            // Text element that shows selected file or prompt, styled to appear clickable
            Text(
                text = selectedFile.value,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = { expanded.value = true })
                    .padding(12.dp)
                    .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                    .padding(8.dp)

            )
            DropdownMenu(
                expanded = expanded.value,
                onDismissRequest = { expanded.value = false },
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
            ) {
                files.forEach { file ->
                    DropdownMenuItem(onClick = {
                        with(sharedPreferences.edit()) {
                            putString(SharedPreferenceKeys.SELECTED_FILE, file)
                            apply()
                        }
                        selectedFile.value = file
                        expanded.value = false
                    }) {
                        Text(text = file)
                    }
                }
            }
        }

        val isButtonEnabled = selectedFile.value != PLACEHOLDER_SELECT_FILE
        Button(
            onClick = { triggerNotification(activity, selectedTemplate.value, selectedFile.value) },
            enabled = isButtonEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Fire Notification")
        }
    }
}

@Composable
fun RadioButtonWithLabel(selected: Boolean, onClick: () -> Unit, text: String) {
    Row(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),verticalAlignment = Alignment.CenterVertically) {
        RadioButton(
            selected = selected,
            onClick = null
        )
        Text(text = text, modifier = Modifier.clickable(onClick = onClick))
    }
}

fun triggerNotification(activity: Activity, template: Template, selectedFile: String) {
    try {
        val notificationData = FileUtil.readNotificationData(activity,template,selectedFile)
        val builder = NotificationBuilder.constructNotificationBuilder(notificationData, NotificationTrackerActivity::class.java, NotificationBroadcastReceiver::class.java)
        with(NotificationManagerCompat.from(activity)) {
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                return
            }
            val tag = notificationData[NotificationBuilderConstants.TAG] ?: (System.currentTimeMillis() % Int.MAX_VALUE).toInt().toString()
            notify(tag.hashCode(), builder.build())
        }
    } catch (e: Exception) {
        CoroutineScope(Dispatchers.Main).launch {
            Toast.makeText(activity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
        e.printStackTrace()
    }
}
