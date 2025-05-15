package com.nmh.base.project.activity.budgetdetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nmh.base.project.activity.base.UiState
import com.nmh.base.project.db.local.LocalRepository
import com.nmh.base.project.db.model.BudgetModel
import com.nmh.base.project.db.model.TransactionModel
import com.nmh.base.project.helpers.TypeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

class DetailBudgetViewModel @Inject constructor(private val repository: LocalRepository) :
    ViewModel() {

    private var _transactionExpenses =
        MutableStateFlow<UiState<MutableList<TransactionModel>>>(UiState.Loading)
    val transactionExpenses: StateFlow<UiState<MutableList<TransactionModel>>> get() = _transactionExpenses

    private var _budget =
        MutableStateFlow<UiState<MutableList<BudgetModel>>>(UiState.Loading)
    val budget: StateFlow<UiState<MutableList<BudgetModel>>> get() = _budget

    private val _date = MutableLiveData<String>()
    val date: LiveData<String> get() = _date

    fun setDate(date: String) {
        _date.value = date
    }

    private var _totalBudget = MutableLiveData(0L)
    val totalBudget: LiveData<Long> get() = _totalBudget

    private var _totalExpenses = MutableLiveData(0L)
    val totalExpenses: LiveData<Long> get() = _totalExpenses

    fun setTotalBudget(totalBudget: Long) {
        _totalBudget.value = totalBudget
    }

    fun setTotalExpenses(totalExpenses: Long) {
        _totalExpenses.value = totalExpenses
    }

    fun getListBudget() {
        viewModelScope.launch {
            repository.getAllBudget().catch {
                _budget.value = UiState.Error(it.message.toString())
            }.collect {
                _budget.value = UiState.Success(it.toMutableList())
            }
        }
    }

    fun insertBudget(budget: BudgetModel) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertBudget(budget)
        }
    }

    fun updateBudget(budget: BudgetModel) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateBudget(budget)
        }
    }

    fun getExpensesTransactionByMonth(date: String, type: TypeModel) {
        val query = date.replace(",", ", %")
        viewModelScope.launch(Dispatchers.IO) {
            repository.getTransactionByMonthAndType(query, type).catch {
                _transactionExpenses.value = UiState.Error(it.message.toString())
            }.collect {
                _transactionExpenses.value = UiState.Success(it.toMutableList())
            }
        }
    }
}