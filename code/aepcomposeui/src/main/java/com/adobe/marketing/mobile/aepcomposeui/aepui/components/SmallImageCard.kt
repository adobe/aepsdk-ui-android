package com.adobe.marketing.mobile.aepcomposeui.aepui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.adobe.marketing.mobile.aepcomposeui.aepui.SmallImageAepUi
import com.adobe.marketing.mobile.aepcomposeui.aepui.style.SmallImageAepUiStyle
import com.adobe.marketing.mobile.aepcomposeui.interactions.UIEvent
import com.adobe.marketing.mobile.aepcomposeui.observers.AepUiEventObserver

/**
 * Composable for rendering a Small Image Card
 */
@Composable
internal fun SmallImageCard(
    ui: SmallImageAepUi,
    style: SmallImageAepUiStyle,
    observer: AepUiEventObserver?,
) {

    LaunchedEffect(key1 = Unit) {
        observer?.onEvent(UIEvent.Display(ui))
    }

    DisposableEffect(key1 = Unit) {
        onDispose {
            observer?.onEvent(UIEvent.Dismiss(ui))
        }
    }

    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable {
                observer?.onEvent(UIEvent.Click(ui))
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // TODO - Add image support
            Spacer(modifier = Modifier.width(16.dp))

            Column(
                verticalArrangement = Arrangement.Center
            ) {
                ui.getState().title?.let {
                    Text(
                        text = it,
                        style = style.getTitleTextStyle(ui.getTemplate()),
                    )
                }
                Text(
                    text = ui.getTemplate().description,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}