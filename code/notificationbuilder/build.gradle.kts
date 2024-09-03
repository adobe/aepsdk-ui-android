/*
 * Copyright 2024 Adobe. All rights reserved.
 * This file is licensed to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License. You may obtain a copy
 * of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR REPRESENTATIONS
 * OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
plugins {
    id("aep-library")
}

val mavenCoreVersion: String by project
val notificationbuilderModuleName: String by project
val notificationbuilderVersion: String by project
val notificationbuilderMavenRepoName: String by project
val notificationbuilderMavenRepoDescription: String by project


aepLibrary {
    namespace = "com.adobe.marketing.mobile.notificationbuilder"
    moduleName = notificationbuilderModuleName
    moduleVersion = notificationbuilderVersion
    enableSpotless = true
    enableCheckStyle = true
    enableDokkaDoc = true
    
    publishing {
        mavenRepoName = notificationbuilderMavenRepoName
        mavenRepoDescription = notificationbuilderMavenRepoDescription
        gitRepoName = "aepsdk-ui-android"
        addCoreDependency(mavenCoreVersion)
    }
}

dependencies {
    implementation("com.adobe.marketing.mobile:core:$mavenCoreVersion")
    implementation(project(":aep_ui_utils"))
    testImplementation("org.robolectric:robolectric:4.7")
    testImplementation("io.mockk:mockk:1.13.11")
}
