package com.nghp.project.moneyapp.db.model

data class StatisticModel(
    var month: String,
    var expenses: Long = 0L,
    var income: Long = 0L,
    var loan: Long = 0L,
    var borrow: Long = 0L,
    var balance: Long = 0L
)