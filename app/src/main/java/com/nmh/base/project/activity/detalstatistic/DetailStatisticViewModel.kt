package com.nmh.base.project.activity.detalstatistic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nmh.base.project.activity.base.UiState
import com.nmh.base.project.db.local.LocalRepository
import com.nmh.base.project.db.model.CategoryModel
import com.nmh.base.project.db.model.TransactionItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

class DetailStatisticViewModel @Inject constructor(private val repository: LocalRepository) :
    ViewModel() {

    private var _transactionByCategory =
        MutableStateFlow<UiState<MutableList<TransactionItem>>>(UiState.Loading)
    val transactionByCategory: StateFlow<UiState<MutableList<TransactionItem>>> get() = _transactionByCategory

    fun getListTransactionByCategory(category: CategoryModel) {
        viewModelScope.launch {
            repository.getTransactionByCategory(category).catch {
                _transactionByCategory.value = UiState.Error(it.message.toString())
            }.collect { list ->

                val grouped = list.groupBy { it.date }
                    .flatMap { (date, transactions) ->
                        val header = TransactionItem.DateHeader(date)
                        val items = transactions.map { TransactionItem.Transaction(it) }
                        listOf(header) + items
                    }

                _transactionByCategory.value = UiState.Success(grouped.toMutableList())
            }
        }
    }
}