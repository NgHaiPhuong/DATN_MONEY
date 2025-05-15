package com.nmh.base.project.activity.transactiondetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nmh.base.project.activity.base.UiState
import com.nmh.base.project.db.local.LocalRepository
import com.nmh.base.project.db.model.BudgetModel
import com.nmh.base.project.db.model.TransactionModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

class DetailTransactionViewModel @Inject constructor(private val repository: LocalRepository) :
    ViewModel() {

    private val _transactionById =
        MutableStateFlow<UiState<TransactionModel>>(UiState.Loading)
    val transactionById: StateFlow<UiState<TransactionModel>> = _transactionById

    fun getTransactionById(id: Int) {
        viewModelScope.launch {
            repository.getTransactionById(id).catch {
                _transactionById.value = UiState.Error(it.message.toString())
            }.collect {
                _transactionById.value = UiState.Success(it)
            }
        }
    }

    fun deleteTransaction(transactionModel: TransactionModel) {
        viewModelScope.launch {
            repository.deleteTransaction(transactionModel)
        }
    }
}