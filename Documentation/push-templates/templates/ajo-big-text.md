---
title: AJO Big text push template for Android
description: Payload fields and rendering behavior for the Android Adobe Journey Optimizer Big text push template.
keywords:
- Adobe Journey Optimizer
- Android
- Push
- Push Template
- Big text
---

# AJO Big text push template for Android

The AJO Big text template displays a title, a short body while collapsed, and the full body when expanded. It can also show an optional large side icon. On Android, the UI extension renders this template through `NotificationBuilderPlugin` when `adb_template_type` is `ajo_bigtext`.

## Payload fields

Push data values must be strings. Template-specific settings are JSON encoded in the `adb_template_properties` string.

| Field | Key | Description |
| :---- | :-- | :---------- |
| Template type | `adb_template_type` | Required. Set to `ajo_bigtext`. |
| Title | `adb_title` | Notification title. |
| Expanded body | `adb_body` | Full text shown when the notification is expanded. |
| Collapsed body | `adb_template_properties.adb_collapsed_text` | Optional short text shown while collapsed. Defaults to `adb_body` when omitted. |
| Large side icon | `adb_template_properties.adb_large_icon` | Optional image URL for the large side icon. |

Common notification fields such as `adb_sound`, `adb_channel_id`, and `adb_act` can also be supplied. See the [AJO Big text template guide](https://developer.adobe.com/client-sdks/edge/adobe-journey-optimizer/push-notification/push-templates/android/big-text) for the complete payload contract.

## Example

```json
{
  "adb_template_type": "ajo_bigtext",
  "adb_version": "1",
  "adb_title": "Your order has shipped",
  "adb_body": "Your order is on its way and should arrive by Friday. Track your package from the Orders tab.",
  "adb_template_properties": "{\"adb_collapsed_text\":\"Your order arrives Friday.\",\"adb_large_icon\":\"https://example.com/box.png\"}"
}
```

`adb_template_properties` is encoded JSON because FCM data messages accept string values. If the template type is unsupported or the plugin cannot build the notification, the host extension can display a basic notification instead.
