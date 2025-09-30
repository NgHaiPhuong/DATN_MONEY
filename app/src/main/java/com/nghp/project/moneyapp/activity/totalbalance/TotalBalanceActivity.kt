package com.nghp.project.moneyapp.activity.totalbalance

import android.annotation.SuppressLint
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.nghp.project.moneyapp.R
import com.nghp.project.moneyapp.activity.base.BaseActivity
import com.nghp.project.moneyapp.activity.base.UiState
import com.nghp.project.moneyapp.databinding.ActivityTotalBalanceBinding
import com.nghp.project.moneyapp.db.model.StatisticModel
import com.nghp.project.moneyapp.extensions.setOnUnDoubleClickListener
import com.nghp.project.moneyapp.helpers.SYMBOL_CURRENCY
import com.nghp.project.moneyapp.sharepref.DataLocalManager
import com.nghp.project.moneyapp.utils.FormatNumber
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class TotalBalanceActivity :
    BaseActivity<ActivityTotalBalanceBinding>(ActivityTotalBalanceBinding::inflate) {

    @Inject
    lateinit var totalBalanceViewModel: TotalBalanceViewModel

    @Inject
    lateinit var totalBalanceAdapter: TotalBalanceAdapter

    override fun setUp() {
        setupLayout()
        evenClick()
    }

    private fun setupLayout() {
        val format = SimpleDateFormat("yyyy", Locale.ENGLISH)
        binding.tvYear.text = format.format(Date())

        lifecycleScope.launch {
            totalBalanceViewModel.getListTransactionFilterByMonth()
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                totalBalanceViewModel.transaction.collect { state ->
                    handleTransactionByMonthAndType(state)
                }
            }
        }

        binding.rcvTotal.apply {
            adapter = totalBalanceAdapter
            layoutManager = LinearLayoutManager(this@TotalBalanceActivity)
        }
    }

    private fun evenClick() {
        binding.ivBack.setOnUnDoubleClickListener { finish() }
    }

    @SuppressLint("SetTextI18n")
    private fun handleTransactionByMonthAndType(state: UiState<MutableList<StatisticModel>>) {
        when (state) {
            is UiState.Loading -> {}
            is UiState.Error -> {}
            is UiState.Success -> {
                val list = state.data

                var totalBalance = 0L
                var totalExpenses = 0L
                var totalIncome = 0L

                list.forEach {
                    totalExpenses += it.expenses
                    totalIncome += it.income
                    totalBalance += it.balance
                }

                val currency = DataLocalManager.getOption(SYMBOL_CURRENCY)

                binding.tvTotalBalance.text = "$currency${FormatNumber.convertNumberToCommaString(totalBalance)}"
                binding.tvExpeneses.text = getString(R.string.str_expensed) + ": $currency${FormatNumber.convertNumberToCommaString(totalExpenses)}"
                binding.tvIncome.text = getString(R.string.str_income) + ": $currency${FormatNumber.convertNumberToCommaString(totalIncome)}"

                totalBalanceAdapter.setData(list)
            }
        }
    }
}