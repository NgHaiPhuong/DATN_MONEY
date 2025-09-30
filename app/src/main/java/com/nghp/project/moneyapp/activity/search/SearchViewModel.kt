package com.nghp.project.moneyapp.activity.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nghp.project.moneyapp.activity.base.UiState
import com.nghp.project.moneyapp.db.local.LocalRepository
import com.nghp.project.moneyapp.db.model.TransactionModel
import com.nghp.project.moneyapp.helpers.TypeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

class SearchViewModel @Inject constructor(private val repository: LocalRepository) : ViewModel() {

    private var _transactionByType =
        MutableStateFlow<UiState<MutableList<TransactionModel>>>(UiState.Loading)
    val transactionByType: StateFlow<UiState<MutableList<TransactionModel>>> get() = _transactionByType

    fun getTransactionByType(type: TypeModel) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getTransactionByTypeModel(type).catch {
                _transactionByType.value = UiState.Error(it.message.toString())
            }.collect {
                _transactionByType.value = UiState.Success(it.toMutableList())
            }
        }
    }
}