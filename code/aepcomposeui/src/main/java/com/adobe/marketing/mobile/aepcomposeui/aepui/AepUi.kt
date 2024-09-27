package com.adobe.marketing.mobile.aepcomposeui.aepui

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.adobe.marketing.mobile.aepuitemplates.AepUiTemplate
import com.adobe.marketing.mobile.aepuitemplates.SmallImageTemplate
import com.adobe.marketing.mobile.aepcomposeui.aepui.state.AepUiState
import com.adobe.marketing.mobile.aepcomposeui.aepui.state.SmallImageUIState


/**
 * Represents a UI component that can be rendered in the AepUI Engine. Binds a template data with a tracking observer.
 * This is a sealed interface that can be implemented by different UI components like [SmallImageAepUi], [LargeImageAepUi], etc.
 * This allows restricting the type of template and observer that can be associated with a UI component, which can be later
 * used in the app to render the UI component via UIComposeExtensions.
 * @param T The type of the template associated with the UI component.
 * @param S The type of the state associated with the UI component.
 */
sealed interface AepUI<T : AepUiTemplate, S : AepUiState> {
    fun getTemplate(): T
    fun getState(): S
    fun updateState(newState: S)
}


class SmallImageAepUi(
    private val template: SmallImageTemplate,
    state: SmallImageUIState
) : AepUI<SmallImageTemplate, SmallImageUIState> {
    private val _state = mutableStateOf(state)
    override fun updateState(newState: SmallImageUIState) {
        _state.value = newState
    }

    override fun getTemplate(): SmallImageTemplate {
        return template
    }

    override fun getState(): SmallImageUIState {
        return _state.value
    }
}