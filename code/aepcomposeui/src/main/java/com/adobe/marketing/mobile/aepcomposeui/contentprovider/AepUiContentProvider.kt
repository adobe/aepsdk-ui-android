package com.adobe.marketing.mobile.aepcomposeui.contentprovider

import com.adobe.marketing.mobile.aepuitemplates.AepUiTemplate
import kotlinx.coroutines.flow.Flow

/**
 * Interface to provide content for the UI and a backing tracker for analytics.
 * This can be treated as a strategy to provide content and tracking for the UI.
 * Allows components like extensions which provide content to expose an implementation
 * for the app to use.
 */
interface AepUiContentProvider {
    /**
     * Retrieves the content for the UI.
     * @return The content for the UI as a flow of [AepTemplate]s.
     */
    suspend fun getContent(): Flow<List<AepUiTemplate>>

    suspend fun refreshContent()
}

