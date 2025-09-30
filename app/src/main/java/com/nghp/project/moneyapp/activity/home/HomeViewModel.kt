package com.nghp.project.moneyapp.activity.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nghp.project.moneyapp.activity.base.UiState
import com.nghp.project.moneyapp.db.local.LocalRepository
import com.nghp.project.moneyapp.db.model.TransactionItem
import com.nghp.project.moneyapp.db.model.TransactionModel
import com.nghp.project.moneyapp.helpers.TypeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

class HomeViewModel @Inject constructor(private val repository: LocalRepository) : ViewModel() {

    private var _transactionByMonth =
        MutableStateFlow<UiState<MutableList<TransactionModel>>>(UiState.Loading)
    val transactionByMonth: StateFlow<UiState<MutableList<TransactionModel>>> get() = _transactionByMonth

    private var _transactionByMonthAndType =
        MutableStateFlow<UiState<MutableList<TransactionItem>>>(UiState.Loading)
    val transactionByMonthAndType: StateFlow<UiState<MutableList<TransactionItem>>> get() = _transactionByMonthAndType

    private val _type = MutableLiveData(TypeModel.EXPENSES)
    val type: LiveData<TypeModel> get() = _type

    private val _totalBalance = MutableLiveData(0L)
    val totalBalance: LiveData<Long> get() = _totalBalance

    private val _date = MutableLiveData<String>()
    val date: LiveData<String> get() = _date

    fun setDate(date: String) {
        _date.value = date
    }

    fun setTotalBalance(total: Long) {
        _totalBalance.value = total
    }

    fun setType(type: TypeModel) {
        _type.value = type
    }

    fun getTransactionByMonthAndType(date: String, type: TypeModel) {
        val query = date.replace(",", ", %")
        viewModelScope.launch(Dispatchers.IO) {
            repository.getTransactionByMonthAndType(query, type).catch {
                _transactionByMonthAndType.value = UiState.Error(it.message.toString())
            }.collect { list ->

                val grouped = list.groupBy { it.date }
                    .flatMap { (date, transactions) ->
                        val header = TransactionItem.DateHeader(date)
                        val items = transactions.map { TransactionItem.Transaction(it) }
                        listOf(header) + items
                    }

                _transactionByMonthAndType.value = UiState.Success(grouped.toMutableList())
            }
        }
    }

    fun getAllListTransactionByMonth(monthYear: String) {
        val query = monthYear.replace(",", ", %")
        viewModelScope.launch(Dispatchers.IO) {
            repository.getAllTransactionByMonth(query).catch {
                _transactionByMonth.value = UiState.Error(it.message.toString())
            }.collect {
                _transactionByMonth.value = UiState.Success(it.toMutableList())
            }
        }
    }
}