package com.nmh.base.project.activity.totalbalance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nmh.base.project.activity.base.UiState
import com.nmh.base.project.db.local.LocalRepository
import com.nmh.base.project.db.model.StatisticModel
import com.nmh.base.project.helpers.TypeLoan
import com.nmh.base.project.helpers.TypeModel
import com.nmh.base.project.utils.FormatNumber
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

class TotalBalanceViewModel @Inject constructor(private var repository: LocalRepository) :
    ViewModel() {

    private var _transaction =
        MutableStateFlow<UiState<MutableList<StatisticModel>>>(UiState.Loading)
    val transaction: StateFlow<UiState<MutableList<StatisticModel>>> get() = _transaction

    fun getListTransactionFilterByMonth() {
        viewModelScope.launch {
            repository.getAllTransaction().catch {
                _transaction.value = UiState.Error(it.message.toString())
            }.collect { list ->

                val group = list.groupBy { extractMonthYear(it.date) }
                    .map {(date, transactions) ->
                        var totalExpenses = 0L
                        var totalIncome = 0L
                        var totalLoan = 0L
                        var totalBorrow = 0L
                        val totalBalance = transactions.sumOf { it.amount }

                        transactions.forEach {
                            when(it.typeModel) {
                                TypeModel.EXPENSES -> totalExpenses += it.amount
                                TypeModel.INCOME -> totalIncome += it.amount
                                TypeModel.LOAN -> {
                                    when(it.typeLoan) {
                                        TypeLoan.TYPE_LOAN -> totalLoan += it.amount
                                        TypeLoan.TYPE_BORROW -> totalBorrow += it.amount
                                        else -> Unit
                                    }
                                }
                                else -> Unit
                            }
                        }
                        val month = FormatNumber.convertDateToMonth(date)
                        StatisticModel(month, totalExpenses, totalIncome, totalLoan, totalBorrow, totalBalance)
                    }

                _transaction.value = UiState.Success(group.toMutableList())
            }
        }
    }

    private fun extractMonthYear(fullDate: String): String {
        val parts = fullDate.split(" ")
        return if (parts.size == 3) "${parts[0]} ${parts[2]}" else fullDate
    }
}