package com.nmh.base.project.activity.create

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nmh.base.project.activity.base.UiState
import com.nmh.base.project.db.local.LocalRepository
import com.nmh.base.project.db.model.BudgetModel
import com.nmh.base.project.db.model.TransactionModel
import com.nmh.base.project.helpers.TypeModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

class CreateTransactionViewModel @Inject constructor(private val repository: LocalRepository) :
    ViewModel() {

    private var _type = MutableLiveData(TypeModel.EXPENSES)
    val type: LiveData<TypeModel> get() = _type

    private val _transactionById =
        MutableStateFlow<UiState<TransactionModel>>(UiState.Loading)
    val transactionById: StateFlow<UiState<TransactionModel>> = _transactionById

    private var _budget =
        MutableStateFlow<UiState<MutableList<BudgetModel>>>(UiState.Loading)
    val budget: StateFlow<UiState<MutableList<BudgetModel>>> get() = _budget

    private val _budgetById =
        MutableStateFlow<UiState<BudgetModel>>(UiState.Loading)
    val budgetById: StateFlow<UiState<BudgetModel>> = _budgetById

    fun setType(typeModel: TypeModel) {
        _type.value = typeModel
    }

    fun getBudgetById(id: Int?) {
        id?.let {
            viewModelScope.launch {
                repository.getBudgetById(it).catch {
                    _budgetById.value = UiState.Error(it.message.toString())
                }.collect {
                    _budgetById.value = UiState.Success(it)
                }
            }
        }
    }

    fun insertTransaction(transactionModel: TransactionModel) {
        viewModelScope.launch {
            repository.insertTransaction(transactionModel)
        }
    }

    fun updateTransaction(transactionModel: TransactionModel) {
        viewModelScope.launch {
            repository.updateTransaction(transactionModel)
        }
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

    fun getTransactionById(id: Int) {
        viewModelScope.launch {
            repository.getTransactionById(id).catch {
                _transactionById.value = UiState.Error(it.message.toString())
            }.collect {
                _transactionById.value = UiState.Success(it)
            }
        }
    }
}