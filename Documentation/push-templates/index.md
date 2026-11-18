---
title: Adobe Experience Platform SDK push templates
description: Learn about Android push templates supported by the Adobe Experience Platform UI extension.
keywords:
- Adobe Journey Optimizer
- Push
- Push Template
- Push Templates
---

# Adobe Experience Platform push templates

<InlineAlert variant="info" slots="text"/>

On Android, the UI extension provides a Mobile Core plugin that renders Adobe Journey Optimizer (AJO) push templates. The plugin currently supports the **Basic** and **Big text** templates. The older Campaign Classic templates use a separate integration and are listed below.

## Getting started

* [Register the Notification Builder plugin for AJO](./android/plugin.md)
* [Campaign Classic setup](./android/index.md)

## Templates

* [AJO and Campaign Classic templates](./templates/index.md)
* [AJO Basic](./templates/ajo-basic.md)
* [AJO Big text](./templates/ajo-big-text.md)

## Image specifications

The following image recommendations apply to the existing Campaign Classic templates. For AJO template requirements, see the [AJO push templates documentation](https://developer.adobe.com/client-sdks/edge/adobe-journey-optimizer/push-notification/push-templates/).

**Android**

| Image type in notification payload |         Aspect Ratios         | Image Size | Supported File Types | File Source                          |
| :--------------------------------: | :---------------------------: | :--------: | :------------------: | ------------------------------------ |
|             adb_image              | 1:1, 3:2, 5:4, 4:3, 2:1, 16:9 |   < 1 MB   |    PNG, JPG, WebP    | Remote URI                           |
|           adb_small_icon           |              1:1              |   < 1 MB   |    PNG, JPG, WebP    | Bundled Drawable Asset               |
|           adb_large_icon           |              1:1              |   < 1 MB   |    PNG, JPG, WebP    | Remote URI or Bundled Drawable Asset |
|         Multi-icon images          |              1:1              |   < 1 MB   |    PNG, JPG, WebP    | Remote URI or Bundled Drawable Asset |
|         Rating icon images         |              1:1              |   < 1 MB   |    PNG, JPG, WebP    | Remote URI or Bundled Drawable Asset |
|          Carousel images           | 1:1, 3:2, 5:4, 4:3, 2:1, 16:9 |   < 1 MB   |    PNG, JPG, WebP    | Remote URI                           |
|     Filmstrip carousel images      |      1:1, 3:2, 5:4, 4:3       |   < 1 MB   |    PNG, JPG, WebP    | Remote URI                           |
| Horizontal product catalog images  |           2:1, 16:9           |   < 1 MB   |    PNG, JPG, WebP    | Remote URI                           |
|  Vertical product catalog images   |      1:1, 3:2, 5:4, 4:3       |   < 1 MB   |    PNG, JPG, WebP    | Remote URI                           |

`adb_image` is used as the main image in basic, input box, remind later, timer, and zero bezel notifications.
