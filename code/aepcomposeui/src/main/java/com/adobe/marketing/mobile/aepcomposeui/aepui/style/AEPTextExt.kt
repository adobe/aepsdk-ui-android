package com.adobe.marketing.mobile.aepcomposeui.aepui.style

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign

// only needed when we support server side styling
fun AEPText.getComposeTextStyle(): TextStyle {
    var textStyle = TextStyle()
    if (color != null) {
        textStyle = textStyle.merge(Color(android.graphics.Color.parseColor(color)))
    }
    if (align != null) {
        textStyle = textStyle.merge(textAlign = when (align) {
            "center" -> TextAlign.Center
            "left" -> TextAlign.Left
            "right" -> TextAlign.Right
            "start" -> TextAlign.Start
            "end" -> TextAlign.End
            else -> TextAlign.Unspecified
        })
    }
    return textStyle
}