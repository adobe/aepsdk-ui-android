plugins {
    id("aep-library")
}

val mavenCoreVersion: String by project
val aepUiTemplatesModuleName: String by project
val aepUiTemplatesVersion: String by project
val aepUiTemplatesMavenRepoName: String by project
val aepUiTemplatesMavenRepoDescription: String by project

aepLibrary {
    namespace = "com.adobe.marketing.mobile.aepcomposeui"

    moduleName = aepUiTemplatesModuleName
    moduleVersion = aepUiTemplatesVersion
    enableSpotless = true
    enableCheckStyle = true
    enableDokkaDoc = true

    publishing {
        mavenRepoName = aepUiTemplatesMavenRepoName
        mavenRepoDescription = aepUiTemplatesMavenRepoDescription
        gitRepoName = "aepsdk-ui-android"
        addCoreDependency(mavenCoreVersion)
    }
}

dependencies {
    implementation("com.adobe.marketing.mobile:core:$mavenCoreVersion")
    testImplementation("org.robolectric:robolectric:4.7")
    testImplementation("io.mockk:mockk:1.13.11")
}