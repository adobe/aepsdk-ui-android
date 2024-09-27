package com.adobe.marketing.mobile.aepcomposeui.aepui.style

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import com.adobe.marketing.mobile.aepuitemplates.SmallImageTemplate

// class containing style for all UI types supported by the UI Engine
class AepUiStyle(
    val smallImageAepUiStyle: SmallImageAepUiStyle = SmallImageAepUiStyle()
)

class SmallImageAepUiStyle(
    private val titleTextStyle: TextStyle? = null
) {
    private var defaultTitleColor = Color.Red

    // order of merging is important here
    // App style > Server style > Default style
    @Composable
    fun getTitleTextStyle(template: SmallImageTemplate): TextStyle {
        return MaterialTheme.typography.h5.copy(defaultTitleColor)
            .merge(template.title?.getComposeTextStyle())
            .merge(titleTextStyle)
    }
}