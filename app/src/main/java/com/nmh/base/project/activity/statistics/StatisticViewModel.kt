package com.nmh.base.project.activity.statistics

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nmh.base.project.activity.base.UiState
import com.nmh.base.project.db.local.LocalRepository
import com.nmh.base.project.db.model.TransactionModel
import com.nmh.base.project.helpers.TypeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

class StatisticViewModel @Inject constructor(private val repository: LocalRepository) :
    ViewModel() {

    private var _transactionByMonth =
        MutableStateFlow<UiState<MutableList<TransactionModel>>>(UiState.Loading)
    val transactionByMonth: StateFlow<UiState<MutableList<TransactionModel>>> get() = _transactionByMonth

    private var _transactionByMonthAndType =
        MutableStateFlow<UiState<MutableList<TransactionModel>>>(UiState.Loading)
    val transactionByMonthAndType: StateFlow<UiState<MutableList<TransactionModel>>> get() = _transactionByMonthAndType

    private val _type = MutableLiveData(TypeModel.EXPENSES)
    val type: LiveData<TypeModel> get() = _type

    private val _date = MutableLiveData<String>()
    val date: LiveData<String> get() = _date

    fun setDate(date: String) {
        _date.value = date
    }

    fun setType(type: TypeModel) {
        _type.value = type
    }

    fun getTransactionByMonthAndType(date: String, type: TypeModel) {
        val query = date.replace(",", ", %")
        viewModelScope.launch(Dispatchers.IO) {
            repository.getTransactionByMonthAndType(query, type).catch {
                _transactionByMonthAndType.value = UiState.Error(it.message.toString())
            }.collect {
                _transactionByMonthAndType.value = UiState.Success(it.toMutableList())
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