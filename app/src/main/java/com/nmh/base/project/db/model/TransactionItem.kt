package com.nmh.base.project.db.model

sealed class TransactionItem {
    data class DateHeader(val date: String) : TransactionItem()
    data class Transaction(val model: TransactionModel) : TransactionItem()
}