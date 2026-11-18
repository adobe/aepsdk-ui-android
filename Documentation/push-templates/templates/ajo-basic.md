---
title: AJO Basic push template for Android
description: Payload fields and rendering behavior for the Android Adobe Journey Optimizer Basic push template.
keywords:
- Adobe Journey Optimizer
- Android
- Push
- Push Template
- Basic
---

# AJO Basic push template for Android

The AJO Basic template displays a title, body, and an expanded hero image. On Android, the UI extension renders this template through `NotificationBuilderPlugin` when `adb_template_type` is `ajo_basic`.

## Payload fields

Push data values must be strings. The template-specific image scale setting is a JSON object encoded as a string in `adb_template_properties`.

| Field | Key | Description |
| :---- | :-- | :---------- |
| Template type | `adb_template_type` | Required. Set to `ajo_basic`. |
| Title | `adb_title` | Notification title. |
| Body | `adb_body` | Notification body. |
| Image | `adb_image` | Remote URL for the expanded hero image. |
| Image scale type | `adb_template_properties.adb_image_scale_type` | Optional. Set to `center_crop` or `fit_center`; defaults to `center_crop`. Encode the containing JSON object as a string. |

Common notification fields such as `adb_icon`, `adb_sound`, `adb_channel_id`, and `adb_act` can also be supplied. See the [AJO push template guide](https://developer.adobe.com/client-sdks/edge/adobe-journey-optimizer/push-notification/push-templates/android/basic) for the complete payload contract.

## Example

```json
{
  "adb_template_type": "ajo_basic",
  "adb_version": "1",
  "adb_title": "AJO Basic",
  "adb_body": "This image uses center crop.",
  "adb_image": "https://example.com/hero.png",
  "adb_icon": "ic_launcher",
  "adb_template_properties": "{\"adb_image_scale_type\":\"center_crop\"}"
}
```

`adb_template_properties` is encoded JSON because FCM data messages accept string values. If the template type is unsupported or the plugin cannot build the notification, the host extension can display a basic notification instead.
