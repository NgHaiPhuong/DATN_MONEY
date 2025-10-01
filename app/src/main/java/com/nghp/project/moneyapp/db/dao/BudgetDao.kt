package com.nghp.project.moneyapp.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.nghp.project.moneyapp.db.model.BudgetModel
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {
    @Insert
    suspend fun insertBudget(budgetModel: BudgetModel)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateBudget(budgetModel: BudgetModel)

    @Delete
    suspend fun deleteBudget(budgetModel: BudgetModel)

    @Query("SELECT * FROM BUDGET_TABLE")
    fun getAllBudget(): Flow<List<BudgetModel>>

    @Query("SELECT * FROM BUDGET_TABLE WHERE date = :date")
    fun getBudgetByDate(date: String): Flow<List<BudgetModel>>

    @Query("SELECT * FROM BUDGET_TABLE WHERE id = :id")
    fun getBudgetByID(id: Int): Flow<BudgetModel>
}