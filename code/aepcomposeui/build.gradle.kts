plugins {
    id("aep-library")
}

val mavenCoreVersion: String by project
val aepComposeUiModuleName: String by project
val aepComposeUiVersion: String by project
val aepComposeUiMavenRepoName: String by project
val aepComposeUiMavenRepoDescription: String by project

aepLibrary {
    namespace = "com.adobe.marketing.mobile.aepcomposeui"

    moduleName = aepComposeUiModuleName
    moduleVersion = aepComposeUiVersion
    enableSpotless = true
    enableCheckStyle = true
    enableDokkaDoc = true

    publishing {
        mavenRepoName = aepComposeUiMavenRepoName
        mavenRepoDescription = aepComposeUiMavenRepoDescription
        gitRepoName = "aepsdk-ui-android"
        addCoreDependency(mavenCoreVersion)
    }
}

dependencies {
    implementation("com.adobe.marketing.mobile:core:$mavenCoreVersion")
    testImplementation("org.robolectric:robolectric:4.7")
    testImplementation("io.mockk:mockk:1.13.11")
    api(project(":aepuitemplates"))
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
}


