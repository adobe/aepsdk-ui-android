/*
  Copyright 2024 Adobe. All rights reserved.
  This file is licensed to you under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License. You may obtain a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0
  Unless required by applicable law or agreed to in writing, software distributed under
  the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR REPRESENTATIONS
  OF ANY KIND, either express or implied. See the License for the specific language
  governing permissions and limitations under the License.
*/

package com.adobe.marketing.mobile.notificationbuilder.internal.extensions

import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.adobe.marketing.mobile.notificationbuilder.internal.PendingIntentUtils
import com.adobe.marketing.mobile.notificationbuilder.internal.PushTemplateConstants
import com.adobe.marketing.mobile.notificationbuilder.internal.PushTemplateConstants.LOG_TAG
import com.adobe.marketing.mobile.notificationbuilder.internal.PushTemplateImageUtils
import com.adobe.marketing.mobile.services.Log
import com.adobe.marketing.mobile.services.ServiceProvider
import com.adobe.marketing.mobile.util.UrlUtils

private const val SELF_TAG = "RemoteViewExtensions"

/**
 * Sets a provided color hex string to a UI element contained in a specified [RemoteViews]
 * view.
 *
 * @param elementId [Int] containing the resource id of the UI element
 * @param colorHex [String] containing the color hex string
 * @param methodName `String` containing the method to be called on the UI element to
 * update the color
 * @param viewFriendlyName `String` containing the friendly name of the view to be used
 * for logging purposes
 */
internal fun RemoteViews.setElementColor(
    elementId: Int,
    colorHex: String?,
    methodName: String,
    viewFriendlyName: String
) {
    if (colorHex.isNullOrEmpty()) {
        Log.trace(
            LOG_TAG,
            SELF_TAG,
            "Empty color hex string found, custom color will not be applied to $viewFriendlyName."
        )
        return
    }

    try {
        setInt(elementId, methodName, Color.parseColor(colorHex))
    } catch (exception: IllegalArgumentException) {
        Log.trace(
            LOG_TAG,
            SELF_TAG,
            "Unrecognized hex string passed to Color.parseColor(), custom color will not be applied to $viewFriendlyName."
        )
    }
}

/**
 * Sets custom colors to the notification background.
 *
 * @param backgroundColor [String] containing the hex color code for the notification background
 * @param containerViewId [Int] containing the resource id of the push template notification RemoteViews
 */
internal fun RemoteViews.setNotificationBackgroundColor(
    backgroundColor: String?,
    containerViewId: Int
) {
    // get custom color from hex string and set it the notification background
    setElementColor(
        containerViewId,
        "#$backgroundColor",
        PushTemplateConstants.MethodNames.SET_BACKGROUND_COLOR,
        PushTemplateConstants.FriendlyViewNames.NOTIFICATION_BACKGROUND
    )
}

/**
 * Sets custom colors to the notification title text.
 *
 * @param titleTextColor [String] containing the hex color code for the notification title text
 * @param containerViewId [Int] containing the resource id of the push template notification RemoteViews
 */
internal fun RemoteViews.setNotificationTitleTextColor(
    titleTextColor: String?,
    containerViewId: Int
) {
    // get custom color from hex string and set it the notification title
    setElementColor(
        containerViewId,
        "#$titleTextColor",
        PushTemplateConstants.MethodNames.SET_TEXT_COLOR,
        PushTemplateConstants.FriendlyViewNames.NOTIFICATION_TITLE
    )
}

/**
 * Sets custom colors to the notification body text.
 *
 * @param expandedBodyTextColor [String] containing the hex color code for the expanded
 * notification body text
 * @param containerViewId [Int] containing the resource id of the push template notification RemoteViews
 */
internal fun RemoteViews.setNotificationBodyTextColor(
    expandedBodyTextColor: String?,
    containerViewId: Int
) {
    // get custom color from hex string and set it the notification body text
    setElementColor(
        containerViewId,
        "#$expandedBodyTextColor",
        PushTemplateConstants.MethodNames.SET_TEXT_COLOR,
        PushTemplateConstants.FriendlyViewNames.NOTIFICATION_BODY_TEXT
    )
}

/**
 * Sets the image for the provided [RemoteViews]. If a image contains a filename
 * only then the image is set from a bundle image resource. If the image contains a URL,
 * the image is downloaded then set.
 *
 * @param image `String` containing the image to use
 *
 * @return `Boolean` true if the image was set, false otherwise
 */
internal fun RemoteViews.setRemoteViewImage(
    image: String?,
    containerViewId: Int
): Boolean {
    if (image.isNullOrEmpty()) {
        Log.trace(
            LOG_TAG,
            SELF_TAG,
            "Null or empty image string found, image will not be applied."
        )
        setViewVisibility(containerViewId, View.GONE)
        return false
    }
    // logical OR is used here for short circuiting the second condition
    // first check if image represents a valid URL
    // only if it is not, check for bundled image
    return setRemoteImage(image, containerViewId) || setBundledImage(image, containerViewId)
}

/**
 * Sets the click action for the specified view in the custom push template [RemoteViews].
 *
 * @param context the application [Context]
 * @param trackerActivityClass the [Class] of the activity to set in the created pending intent for tracking purposes
 * template notification
 * @param targetViewResourceId [Int] containing the resource id of the view to attach the click action
 * @param actionUri `String` containing the action uri defined for the push template image
 * @param actionType the [PushTemplateConstants.ActionType] of the action to be performed
 * @param tag `String` containing the tag to use when scheduling the notification
 * @param stickyNotification [Boolean] if false, remove the [NotificationCompat] after the `RemoteViews` is pressed
 */
internal fun RemoteViews.setRemoteViewClickAction(
    context: Context,
    trackerActivityClass: Class<out Activity>?,
    targetViewResourceId: Int,
    actionUri: String?,
    actionId: String?,
    tag: String?,
    stickyNotification: Boolean
) {
    Log.trace(
        LOG_TAG,
        SELF_TAG,
        "Setting remote view click action uri: $actionUri."
    )

    val pendingIntent: PendingIntent? =
        PendingIntentUtils.createPendingIntent(
            context,
            trackerActivityClass,
            actionUri,
            actionId,
            tag,
            stickyNotification
        )
    setOnClickPendingIntent(targetViewResourceId, pendingIntent)
}

/**
 * Sets the image for the provided [RemoteViews] by downloading the image from the provided URL.
 * If the image cannot be downloaded, the image visibility is set to [View.GONE].
 *
 * @param imageUrl `String` containing the image URL to download and use
 * @param containerViewId [Int] containing the resource id of the view to attach the image to
 *
 * @return `Boolean` true if the image was set, false otherwise
 */
internal fun RemoteViews.setRemoteImage(
    imageUrl: String?,
    containerViewId: Int
): Boolean {
    if (!UrlUtils.isValidUrl(imageUrl)) {
        return false
    }
    val downloadedIconCount = PushTemplateImageUtils.cacheImages(listOf(imageUrl))
    if (downloadedIconCount == 0) {
        Log.warning(
            LOG_TAG,
            SELF_TAG,
            "Unable to download an image from URL $imageUrl, image will not be applied."
        )
        setViewVisibility(containerViewId, View.GONE)
        return false
    }
    setImageViewBitmap(
        containerViewId,
        PushTemplateImageUtils.getCachedImage(imageUrl)
    )
    return true
}

/**
 * Sets the image resource bundled with the app for the provided [RemoteViews].
 * If the resource does not exist, the [RemoteViews] visibility is set to [View.GONE].
 *
 * @param image `String` containing the image to use
 * @param containerViewId [Int] containing the resource id of the view to attach the image to
 *
 * @return `Boolean` true if the image was set, false otherwise
 */
internal fun RemoteViews.setBundledImage(
    image: String?,
    containerViewId: Int
): Boolean {
    val bundledIconId: Int? = ServiceProvider.getInstance()
        .appContextService.applicationContext?.getIconWithResourceName(image)
    if (bundledIconId == null || bundledIconId == 0) {
        Log.warning(
            LOG_TAG,
            SELF_TAG,
            "Unable to find a bundled image with name $image, image will not be applied."
        )
        setViewVisibility(containerViewId, View.GONE)
        return false
    }
    setImageViewResource(containerViewId, bundledIconId)
    return true
}
