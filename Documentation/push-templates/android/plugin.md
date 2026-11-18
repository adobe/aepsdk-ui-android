---
title: Set up AJO push templates on Android
description: Register the UI extension's Notification Builder plugin with Mobile Core to render Adobe Journey Optimizer push templates.
keywords:
- Adobe Journey Optimizer
- Android
- Push
- Push Template
- NotificationBuilderPlugin
---

# Set up AJO push templates on Android

The UI extension provides `NotificationBuilderPlugin`, which implements the Mobile Core `IUiTemplatePlugin` contract. The Adobe Journey Optimizer extension uses this plugin to render supported push templates. The app registers the plugin with Mobile Core; the app does not call the plugin to build each notification.

The plugin is Android-only and currently supports the [AJO Basic and Big text templates](../templates/index.md#adobe-journey-optimizer-templates). It requires Mobile Core 3.10.0 or later.

## Add the dependency

Add the Notification Builder artifact to your app's Gradle dependencies:

```groovy
dependencies {
    implementation("com.adobe.marketing.mobile:notificationbuilder:3.1.0")
}
```

## Register the plugin

Register `NotificationBuilderPlugin` in your `Application` class before initializing Mobile Core and registering extensions:

```kotlin
import android.app.Application
import com.adobe.marketing.mobile.MobileCore
import com.adobe.marketing.mobile.notificationbuilder.NotificationBuilderPlugin

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()

        MobileCore.addPlugins(NotificationBuilderPlugin())
        MobileCore.initialize(this, "ENVIRONMENT_ID")
    }
}
```

Replace `ENVIRONMENT_ID` with your Mobile Core environment ID. Register the Adobe Journey Optimizer extension as described in its [getting started guide](https://developer.adobe.com/client-sdks/edge/adobe-journey-optimizer/).

Add plugins before Mobile Core initialization so the plugin is available when extensions start processing, including when your app is launched by a push notification. See [Mobile Core plugins](https://developer.adobe.com/client-sdks/home/base/mobile-core/plugins/) for details.

## How template rendering works

When the Adobe Journey Optimizer extension receives a push with a supported `adb_template_type`, it asks Mobile Core for the registered `IUiTemplatePlugin`. The Notification Builder plugin creates the notification UI and uses the tracking provider supplied by the host extension for notification actions. The host extension remains responsible for tracking and intent handling.

If the payload uses a template type that the plugin does not support, or notification construction fails, the plugin returns no custom notification and the host can use its basic-notification fallback. For payload fields and examples, see [AJO Basic](../templates/ajo-basic.md) and [AJO Big text](../templates/ajo-big-text.md).
