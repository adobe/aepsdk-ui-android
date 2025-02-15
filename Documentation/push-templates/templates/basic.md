---
title: Adobe Experience Platform SDK basic push template
description: Basic push template supported by Adobe Campaign Classic mobile extension.
keywords:
- Adobe Campaign Classic
- Push
- Push Template
- Basic
---

import Tabs from './tabs/basic.md'

# Push template - basic

A standard notification including a title, subtitle, message body, optional image, icons, link, buttons.

An alternate title and body can be optionally specified when the notification is expanded by the user.

## Configuration

If using "remind later" functionality in a basic notification, you **must** declare use of the `USE_EXACT_ALARM` permission in your `AndroidManifest.xml`:

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.mycompany.myapp">
    ...
    <uses-permission android:name="android.permission.USE_EXACT_ALARM" />
    ...
</manifest>
```

## Properties

The properties below define the payload sent to FCM:

| **Field**          | **Required** | **Key**             | **Type** | **Description**                                              |
| :----------------- | :----------- | :------------------ | :------- | :----------------------------------------------------------- |
| Payload Version    | ✅            | `adb_version`       | string   | Version of the payload assigned by the ACC authoring UI.     |
| Template Type      | ✅            | `adb_template_type` | string   | Informs the reader which properties may exist in the template object.<br />Basic template uses a value of "basic". |
| Title              | ✅            | `adb_title`         | string   | Text shown in the notification's title.                      |
| Color - Title      | ⛔️            | `adb_clr_title`     | string   | Text color for `adb_title`.<br />Represented as six character hex, e.g. `00FF00`. |
| Body               | ✅            | `adb_body`          | string   | Text shown in message body when notification is collapsed.   |
| Expanded Body      | ⛔️            | `adb_body_ex`       | string   | Body of the message when the message is expanded.            |
| Color - Body       | ⛔️            | `adb_clr_body`      | string   | Text color for `adb_body`, `adb_body_ex`.<br />Represented as six character hex, e.g. `00FF00`. |
| Sound              | ⛔️            | `adb_sound`         | string   | Sound played when notification is delivered.<br />Value should be the name of the sound file without its extension.<br />Sound file should be located in the `assets/raw` directory of your app. |
| Image              | ⛔️            | `adb_image`         | string   | URI of an image to be shown when notification is expanded.   |
| Link URI           | ⛔️            | `adb_uri`           | string   | URI to be handled when user clicks the notification.         |
| Link Type          | ⛔️            | `adb_a_type`        | string   | Type of link represented in `adb_uri` - one of "WEBURL", "DEEPLINK", "DISMISS", or "OPENAPP".<br />Required if `adb_uri` is specified. |
| Button(s)          | ⛔️            | `adb_act`           | string   | **The value is an encoded JSON string.**<br />One to three buttons to create for the notification. If using "remind later" functionality, only two buttons may be used in this field.<br />When decoded, the string contains an array of the following objects:<br /><ul><li>`label` (required) - Text shown on the button's label.</li><li>`uri` (required) - URI to be handled when user clicks the button.</li><li>`type` (required) - Type of link represented in `uri` - one of "WEBURL", "DEEPLINK", "DISMISS", or "OPENAPP".</li></ul> |
| Icon               | ⛔️            | `adb_icon`          | string   | Name of a small icon to use in the notification. <br />**Note** - The value referenced by this key is not used if a valid `adb_small_icon` key value pair is present in the payload. |
| Small Icon         | ⛔️            | `adb_small_icon`    | string   | Name of a small icon to use in the notification. The app's drawable resources are checked for an image file with the provided name. |
| Color - Small Icon | ⛔️            | `adb_clr_icon`      | string   | Color for the notification's small icon.<br />Represented as six character hex, e.g. `00FF00`. |
| Large Icon         | ⛔️            | `adb_large_icon`    | string   | URI or name for a large icon to use in the notification. If the URI is valid, the remote image is downloaded and cached on the device to be used as the large icon. If a name is provided, the app's drawable resources are checked for an image file with the same name. If no valid URI or name is provided then no large icon is set on the notification. |
| Remind Later Text  | ⛔️            | `adb_rem_txt`       | string   | If present, show a "remind later" button using the value provided as its label.<br />**Note** - both `remTxt` and `remTs` must be present in order to support "remind me later" functionality. |
| Remind Later Date  | ⛔️            | `adb_rem_ts`        | string   | If present, schedule this notification to be re-delivered at this epoch timestamp (in seconds) provided.<br />**Note** - both `remTxt` and `remTs` must be present in order to support "remind me later" functionality. |
| Channel ID         | ⛔️            | `adb_channel_id`    | string   | The [notification's channel id](https://developer.android.com/guide/topics/ui/notifiers/notifications#ManageChannels) (new in Android O). The app must create a channel with this channel ID before any notification with this channel ID is received. If you don't send this channel ID in the request, or if the channel ID provided has not yet been created by the app, FCM uses the channel ID specified in the app manifest.<br />If not provided in payload, SDK uses a "default" channel ID of value "General Notifications".<br />If < API 26 (Android O), this value is ignored. |
| Badge Count        | ⛔️            | `adb_n_count`       | string   | Value to be used on app's badge.                             |
| Priority           | ⛔️            | `adb_n_priority`    | string   | Designates the notification priority for Android - [importance](https://developer.android.com/reference/android/app/NotificationChannel#setImportance(int)) for API >= 26 (Android 8.0) or [priority](https://developer.android.com/reference/androidx/core/app/NotificationCompat.Builder#setPriority(int)) for API < 26. |
| Tag                | ⛔️            | `adb_tag`           | string   | Identifier used to replace existing notifications in the notification drawer. If not specified, each request creates a new notification. If specified and a notification with the same tag is already being shown, the new notification replaces the existing one in the notification drawer. |
| Sticky             | ⛔️            | `adb_sticky`        | string   | Boolean value represented as a string "true" or "false".<br />When set to false or unset, the notification is automatically dismissed when the user clicks it in the panel. When set to true, the notification persists even when the user clicks it. |
| Ticker             | ⛔️            | `adb_ticker`        | string   | Sets the "ticker" text, which is sent to accessibility services. |
| Color - Background | ⛔️            | `adb_clr_bg`        | string   | Color for notification's background.<br />Represented as six character hex, e.g. `00FF00`. |

## Example

<img src="./assets/android_basic.gif" width="50%" height="50%"/>

Below is a sample of what a payload might look like for a basic notification:

```json
{
  "message": {
    "token": "FCM_TOKEN",
    "android": {
      "collapse_key": "new message"
    },    
    "data": {
      "adb_version": "1",
      "adb_template_type": "basic",
      "adb_title": "game request",
      "adb_body": "shall we play a game?",
      "adb_sound": "bingBong",
      "adb_small_icon": "ic_knight",
      "adb_large_icon": "https://pictureofchess.com/logo.png",
      "adb_n_count": "1",
      "adb_n_priority": "PRIORITY_LOW",
      "adb_channel_id": "a3b80ef",
      "adb_image": "https://pictureofchess.com/board.png",
      "adb_uri": "https://chess.com/games",
      "adb_a_type": "WEBURL",
      "adb_act": "[{\"label\":\"accept\",\"uri\":\"https://chess.com/games/552\",\"type\":\"DEEPLINK\"{\"label\":\"decline\",\"uri\":\"\",\"type\":\"OPENAPP\"}]",
      "adb_tag": "24",
      "adb_sticky": "true",
      "adb_ticker": "Play a game?",
      "adb_body_ex": "Bob wants to play a game of chess with you. Click 'accept' to start!",
      "adb_clr_body": "00EE00",
      "adb_clr_title": "AABBCC",
      "adb_clr_icon": "123456",
      "adb_clr_bg": "000000",
      "adb_rem_txt": "Remind me",
      "adb_rem_ts": "1703462400"
    }
  }
}
```

## Usage recommendations

The tables below contain guidelines for your push notification content. **These recommendations help your text and images display reliably across multiple devices.** These values are guidelines only - you should still test a notification prior to sending it.

### Audio specifications

Custom sound files must be placed within the app's bundle in the `res/raw` directory. Supported formats are `mp3`, `wav`, or `ogg`.

### Image specifications

| **Aspect ratios**             | **Image size range** | **Supported file types** |
| :---------------------------- | -------------------: | -----------------------: |
| 1:1, 3:2, 5:4, 4:3, 2:1, 16:9 |               < 1 MB |           PNG, JPG, WebP |

### Text guidelines

| **Type**    |                   **Recommended max # of characters** | **Supports multiple lines?** |
| :---------- | ----------------------------------------------------: | ---------------------------: |
| Title       |  ~35 (depends on screen size and device font setting) |                           No |
| Description | ~110 (depends on screen size and device font setting) |                          Yes |
