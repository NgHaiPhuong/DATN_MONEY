package com.nmh.base.project.db.local

import com.nmh.base.project.db.model.BudgetModel
import com.nmh.base.project.db.model.CategoryModel
import com.nmh.base.project.db.model.TransactionModel
import com.nmh.base.project.helpers.TypeModel
import kotlinx.coroutines.flow.Flow
import java.lang.reflect.Type

interface LocalRepository {

    fun getAllTransactionByMonth(monthYear: String): Flow<List<TransactionModel>>

    fun getTransactionByTypeModel(type: TypeModel): Flow<List<TransactionModel>>

    fun getTransactionByDate(date: String): Flow<List<TransactionModel>>

    fun getTransactionByMonthAndType(date: String, type: TypeModel): Flow<List<TransactionModel>>

    fun getTransactionById(id: Int): Flow<TransactionModel>

    fun getTransactionByMonthAndCategory(date: String, category: CategoryModel): Flow<List<TransactionModel>>

    fun getAllTransaction(): Flow<List<TransactionModel>>

    fun getTransactionByCategory(category: CategoryModel): Flow<List<TransactionModel>>

    fun getTransactionByBudget(budgetId: Int): Flow<List<TransactionModel>>

    suspend fun insertTransaction(transactionModel: TransactionModel)

    suspend fun deleteTransaction(transactionModel: TransactionModel)

    suspend fun updateTransaction(transactionModel: TransactionModel)

    suspend fun insertBudget(budgetModel: BudgetModel)

    suspend fun updateBudget(budgetModel: BudgetModel)

    suspend fun deleteBudget(budgetModel: BudgetModel)

    fun getAllBudget(): Flow<List<BudgetModel>>

    fun getBudgetByDate(date: String): Flow<List<BudgetModel>>

    fun getBudgetById(id: Int): Flow<BudgetModel>
}