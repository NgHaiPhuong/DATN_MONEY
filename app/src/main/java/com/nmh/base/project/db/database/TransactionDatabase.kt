package com.nmh.base.project.db.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.nmh.base.project.db.converter.Converters
import com.nmh.base.project.db.dao.BudgetDao
import com.nmh.base.project.db.dao.TransactionDao
import com.nmh.base.project.db.model.BudgetModel
import com.nmh.base.project.db.model.TransactionModel

@Database(version = 1, entities = [TransactionModel::class, BudgetModel::class], exportSchema = false)
@TypeConverters(Converters::class)
abstract class TransactionDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao

    abstract fun budgetDao(): BudgetDao
}