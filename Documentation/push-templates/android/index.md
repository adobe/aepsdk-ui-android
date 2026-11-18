---
title: Adobe Experience Platform SDK push templates
description: Set up Android push templates for the Adobe Campaign Classic Mobile SDK extension.
keywords:
- Adobe Campaign Classic
- Push
- Push Template
- Push Templates
---

# Push templates setup - Android

This page describes the Campaign Classic integration. For Adobe Journey Optimizer (AJO) push templates, see [Register the Notification Builder plugin](./plugin.md).

<InlineAlert variant="info" slots="text"/>

Campaign Classic template handling is available through the **Adobe Campaign Classic** extension. This setup is supported by **Android SDK version 3.1.0+**.

## Setup

### Prerequisite

Add the Campaign Classic extension dependency to your app's gradle file:

```groovy
dependencies {
  implementation("com.adobe.marketing.mobile:campaignclassic:3.1.+")
}
```

### Implementation

In your application, call `AEPMessagingService.handleRemoteMessage` from `onMessageReceived` in the class implementing `FirebaseMessagingService`.

Below is an example of where to call the API:

```java
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (AEPMessagingService.handleRemoteMessage(this, remoteMessage)) {
            // Campaign extension has handled the notification
        } else {
            // Handle notification from other sources
        }
    }
}
```
