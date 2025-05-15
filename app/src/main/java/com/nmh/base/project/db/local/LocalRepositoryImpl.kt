package com.nmh.base.project.db.local

import com.nmh.base.project.db.dao.BudgetDao
import com.nmh.base.project.db.dao.TransactionDao
import com.nmh.base.project.db.model.BudgetModel
import com.nmh.base.project.db.model.CategoryModel
import com.nmh.base.project.db.model.TransactionModel
import com.nmh.base.project.helpers.TypeModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalRepositoryImpl @Inject constructor(
    private val transactionDao: TransactionDao,
    private val budgetDao: BudgetDao
) :
    LocalRepository {

    override fun getAllTransactionByMonth(monthYear: String): Flow<List<TransactionModel>> {
        return transactionDao.getAllTransactionByMonth(monthYear)
    }

    override fun getTransactionByTypeModel(type: TypeModel): Flow<List<TransactionModel>> {
        return transactionDao.getTransactionByTypeModel(type)
    }

    override fun getTransactionByDate(date: String): Flow<List<TransactionModel>> {
        return transactionDao.getTransactionByDate(date)
    }

    override fun getTransactionByMonthAndType(
        date: String,
        type: TypeModel
    ): Flow<List<TransactionModel>> {
        return transactionDao.getTransactionByMonthAndType(date, type)
    }

    override fun getTransactionById(id: Int): Flow<TransactionModel> {
        return transactionDao.getTransactionById(id)
    }

    override fun getTransactionByMonthAndCategory(
        date: String,
        category: CategoryModel
    ): Flow<List<TransactionModel>> {
        return transactionDao.getTransactionByMonthAndCategory(date, category)
    }

    override fun getAllTransaction(): Flow<List<TransactionModel>> {
        return transactionDao.getAllTransaction()
    }

    override fun getTransactionByCategory(category: CategoryModel): Flow<List<TransactionModel>> {
        return transactionDao.getTransactionByCategory(category)
    }

    override fun getTransactionByBudget(budgetId: Int): Flow<List<TransactionModel>> {
        return transactionDao.getTransactionByBudget(budgetId)
    }

    override suspend fun insertTransaction(transactionModel: TransactionModel) {
        transactionDao.insertTransaction(transactionModel)
    }

    override suspend fun deleteTransaction(transactionModel: TransactionModel) {
        transactionDao.deleteTransaction(transactionModel)
    }

    override suspend fun updateTransaction(transactionModel: TransactionModel) {
        transactionDao.updateTransaction(transactionModel)
    }

    override suspend fun insertBudget(budgetModel: BudgetModel) {
        budgetDao.insertBudget(budgetModel)
    }

    override suspend fun updateBudget(budgetModel: BudgetModel) {
        budgetDao.updateBudget(budgetModel)
    }

    override suspend fun deleteBudget(budgetModel: BudgetModel) {
        budgetDao.deleteBudget(budgetModel)
    }

    override fun getAllBudget(): Flow<List<BudgetModel>> {
        return budgetDao.getAllBudget()
    }

    override fun getBudgetByDate(date: String): Flow<List<BudgetModel>> {
        return budgetDao.getBudgetByDate(date)
    }

    override fun getBudgetById(id: Int): Flow<BudgetModel> {
        return budgetDao.getBudgetByID(id)
    }
}