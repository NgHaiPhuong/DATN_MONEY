package com.nmh.base.project.db.model

import java.util.Locale

data class LanguageModel(
    var name: String = "",
    var uri: String = "",
    var nativeName: String = "",
    var locale: Locale = Locale.ENGLISH,
    var isCheck: Boolean
)