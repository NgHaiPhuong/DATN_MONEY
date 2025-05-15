package com.nmh.base.project.activity.budgedetaileach

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nmh.base.project.activity.base.UiState
import com.nmh.base.project.db.local.LocalRepository
import com.nmh.base.project.db.model.BudgetModel
import com.nmh.base.project.db.model.TransactionModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

class BudgetDetailEachViewModel @Inject constructor(private val repository: LocalRepository) :
    ViewModel() {

    private var _transactionByBudget =
        MutableStateFlow<UiState<MutableList<TransactionModel>>>(UiState.Loading)
    val transactionByBudget: StateFlow<UiState<MutableList<TransactionModel>>> get() = _transactionByBudget

    fun getTransactionByBudget(budgetId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getTransactionByBudget(budgetId).catch {
                _transactionByBudget.value = UiState.Error(it.message.toString())
            }.collect {
                _transactionByBudget.value = UiState.Success(it.toMutableList())
            }
        }
    }

    fun deleteBudget(budgetModel: BudgetModel) {
        viewModelScope.launch {
            repository.deleteBudget(budgetModel)
        }
    }
}