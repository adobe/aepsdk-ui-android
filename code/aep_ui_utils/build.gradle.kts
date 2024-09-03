plugins {
    id("aep-library")
}

val mavenCoreVersion: String by project
val aepUIUtilsModuleName: String by project
val aepUIUtilsVersion: String by project
val aepUIUtilsMavenRepoName: String by project
val aepUIUtilsMavenRepoDescription: String by project


aepLibrary {
    namespace = "com.adobe.marketing.mobile.aep_ui_utils"
    moduleName = aepUIUtilsModuleName
    moduleVersion = aepUIUtilsVersion
    enableSpotless = true
    enableCheckStyle = true
    enableDokkaDoc = true

    publishing {
        mavenRepoName = aepUIUtilsMavenRepoName
        mavenRepoDescription = aepUIUtilsMavenRepoDescription
        gitRepoName = "aepsdk-ui-android"
        addCoreDependency(mavenCoreVersion)
    }
}

dependencies {
    implementation("com.adobe.marketing.mobile:core:$mavenCoreVersion")
}
