package com.nghp.project.moneyapp.db.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.nghp.project.moneyapp.db.converter.Converters
import com.nghp.project.moneyapp.db.dao.BudgetDao
import com.nghp.project.moneyapp.db.dao.MessageDao
import com.nghp.project.moneyapp.db.dao.TransactionDao
import com.nghp.project.moneyapp.db.model.BudgetModel
import com.nghp.project.moneyapp.db.model.ChatMessageModel
import com.nghp.project.moneyapp.db.model.TransactionModel

@Database(version = 1, entities = [TransactionModel::class, BudgetModel::class, ChatMessageModel::class], exportSchema = false)
@TypeConverters(Converters::class)
abstract class TransactionDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao

    abstract fun budgetDao(): BudgetDao

    abstract fun messageDao(): MessageDao
}