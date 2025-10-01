package com.nghp.project.moneyapp.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.nghp.project.moneyapp.db.model.BudgetModel
import com.nghp.project.moneyapp.db.model.CategoryModel
import com.nghp.project.moneyapp.db.model.TransactionModel
import com.nghp.project.moneyapp.helpers.TypeModel
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Insert
    suspend fun insertTransaction(transactionModel: TransactionModel)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateTransaction(transactionModel: TransactionModel)

    @Delete
    suspend fun deleteTransaction(transactionModel: TransactionModel)

    @Query("SELECT * FROM transaction_table WHERE date LIKE '%' || :monthYear || '%' ORDER BY date DESC, time DESC")
    fun getAllTransactionByMonth(monthYear: String): Flow<List<TransactionModel>>

    @Query("SELECT * FROM TRANSACTION_TABLE WHERE typeModel = :type ORDER BY date DESC, time DESC")
    fun getTransactionByTypeModel(type: TypeModel): Flow<List<TransactionModel>>

    @Query("SELECT * FROM TRANSACTION_TABLE WHERE date = :date ORDER BY date DESC, time DESC")
    fun getTransactionByDate(date: String): Flow<List<TransactionModel>>

    @Query("SELECT * FROM TRANSACTION_TABLE WHERE date LIKE '%' || :monthYear || '%' AND typeModel = :type ORDER BY date DESC, time DESC")
    fun getTransactionByMonthAndType(monthYear: String, type: TypeModel): Flow<List<TransactionModel>>

    @Query("SELECT * FROM TRANSACTION_TABLE WHERE date LIKE '%' || :monthYear || '%' AND category = :category ORDER BY date DESC, time DESC")
    fun getTransactionByMonthAndCategory(monthYear: String, category: CategoryModel): Flow<List<TransactionModel>>

    @Query("SELECT * FROM TRANSACTION_TABLE WHERE id = :id")
    fun getTransactionById(id: Int): Flow<TransactionModel>

    @Query("SELECT * FROM TRANSACTION_TABLE WHERE category = :category")
    fun getTransactionByCategory(category: CategoryModel): Flow<List<TransactionModel>>

    @Query("SELECT * FROM TRANSACTION_TABLE WHERE budgetInt = :budgetId ORDER BY date DESC, time DESC")
    fun getTransactionByBudget(budgetId: Int): Flow<List<TransactionModel>>

    @Query("SELECT * FROM TRANSACTION_TABLE ORDER BY date DESC, time DESC")
    fun getAllTransaction(): Flow<List<TransactionModel>>
}