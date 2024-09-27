package com.adobe.marketing.mobile.aepcomposeui.observers

import com.adobe.marketing.mobile.aepcomposeui.interactions.UIEvent

interface AepUiEventObserver {
    fun onEvent(event: UIEvent<*, *>)
}