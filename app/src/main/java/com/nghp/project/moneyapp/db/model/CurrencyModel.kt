package com.nghp.project.moneyapp.db.model

data class CurrencyModel(
    val country: String,
    val code: String,
    val name: String,
    val uri: String = "",
    val symbol: String,
    var isSelected: Boolean = false
)