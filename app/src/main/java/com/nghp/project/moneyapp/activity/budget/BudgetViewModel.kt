package com.nghp.project.moneyapp.activity.budget

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nghp.project.moneyapp.activity.base.UiState
import com.nghp.project.moneyapp.db.local.LocalRepository
import com.nghp.project.moneyapp.db.model.BudgetModel
import com.nghp.project.moneyapp.db.model.TransactionModel
import com.nghp.project.moneyapp.helpers.TypeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

class BudgetViewModel @Inject constructor(private val repository: LocalRepository) : ViewModel() {

    private var _transactionExpenses =
        MutableStateFlow<UiState<MutableList<TransactionModel>>>(UiState.Loading)
    val transactionExpenses: StateFlow<UiState<MutableList<TransactionModel>>> get() = _transactionExpenses

    private var _totalBudget = MutableLiveData(0L)
    val totalBudget : LiveData<Long> get() = _totalBudget

    private var _totalExpenses = MutableLiveData(0L)
    val totalExpenses : LiveData<Long> get() = _totalExpenses

    private val _date = MutableLiveData<String>()
    val date: LiveData<String> get() = _date

    fun setDate(date: String) {
        _date.value = date
    }

    fun setTotalBudget(totalBudget: Long) {
        _totalBudget.value = totalBudget
    }

    fun setTotalExpenses(totalExpenses: Long) {
        _totalExpenses.value = totalExpenses
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