package com.adobe.marketing.mobile.aepuitemplates

import com.adobe.marketing.mobile.aepuitemplates.utils.AepUiTemplateType

class SmallImageTemplate() : AepUiTemplate {
    val imageUrl: String = ""
    val title: String = ""
    val description: String = ""
    override fun getType() = AepUiTemplateType.SMALL_IMAGE.typeName
}
