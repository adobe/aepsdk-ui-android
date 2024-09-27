package com.adobe.marketing.mobile.aepcomposeui.aepui.state

sealed interface AepUiState

data class SmallImageUIState(val title: String?, val dismissed: Boolean = false) : AepUiState
data class LargeImageUIState(val dismissed: Boolean = false) : AepUiState
