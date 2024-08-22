package com.adobe.marketing.mobile.utils

import java.util.concurrent.TimeUnit

object UiImageConstants {
    const val DEFAULT_BITMAP_QUALITY = 100
    const val DEFAULT_DOWNLOAD_TIMEOUT_SECS = 10
    const val CAROUSEL_MAX_BITMAP_WIDTH = 300
    const val CAROUSEL_MAX_BITMAP_HEIGHT = 200
    const val PUSH_TEMPLATE_LOG_TAG = "PushTemplates"
    const val CACHE_BASE_DIR = "pushtemplates"
    const val PUSH_IMAGE_CACHE = "pushimagecache"
    val PUSH_NOTIFICATION_IMAGE_CACHE_EXPIRY_IN_MILLISECONDS: Long =
        TimeUnit.DAYS.toMillis(3) // 3 days

}