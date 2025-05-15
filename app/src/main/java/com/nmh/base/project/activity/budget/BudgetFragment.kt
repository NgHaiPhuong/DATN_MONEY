package com.nmh.base.project.activity.budget

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.nmh.base.project.R
import com.nmh.base.project.activity.base.BaseFragment
import com.nmh.base.project.activity.base.UiState
import com.nmh.base.project.activity.budgetdetail.BudgetDetailActivity
import com.nmh.base.project.callback.ICallBackString
import com.nmh.base.project.customView.CustomSelectMonth
import com.nmh.base.project.customView.CustomUpdateBudget
import com.nmh.base.project.databinding.FragmentBudgetBinding
import com.nmh.base.project.db.model.BudgetModel
import com.nmh.base.project.db.model.TransactionModel
import com.nmh.base.project.extensions.setOnUnDoubleClickListener
import com.nmh.base.project.helpers.BUDGET
import com.nmh.base.project.helpers.DATE_BUDGET
import com.nmh.base.project.helpers.SYMBOL_CURRENCY
import com.nmh.base.project.helpers.TypeModel
import com.nmh.base.project.sharepref.DataLocalManager
import com.nmh.base.project.utils.FormatNumber
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlin.math.roundToInt

@AndroidEntryPoint
class BudgetFragment : BaseFragment<FragmentBudgetBinding>(FragmentBudgetBinding::inflate) {

    @Inject
    lateinit var budgetViewModel: BudgetViewModel

    @Inject
    lateinit var customUpdateBudget: CustomUpdateBudget

    @Inject
    lateinit var customSelectMonth: CustomSelectMonth

    private var totalExpenses = 0L
    private var totalBudget = 0L
    private var selectMonth = ""
    private var currency : String = ""

    override fun setUp() {
        setupLayout()
        evenClick()
    }

    @SuppressLint("SetTextI18n")
    private fun setupLayout() {
        currency = DataLocalManager.getOption(SYMBOL_CURRENCY).toString()

        val format = SimpleDateFormat("MMMM, yyyy", Locale.ENGLISH)
        selectMonth = format.format(Date())
        binding.tvDate.text = selectMonth

        budgetViewModel.setTotalBudget(DataLocalManager.getLong(BUDGET))

        budgetViewModel.totalExpenses.observe(this) {
            totalExpenses = it
            setUpPieChart(totalBudget, it)
        }

        budgetViewModel.totalBudget.observe(this) {
            totalBudget = it
            setUpPieChart(it, totalExpenses)
            DataLocalManager.setLong(it, BUDGET)
            binding.tvBudget.text = "$currency${FormatNumber.convertNumberToNumberDotString(totalBudget)}"
        }

        budgetViewModel.date.observe(this) {
            selectMonth = it
            binding.tvDate.text = it
            budgetViewModel.getExpensesTransactionByMonth(it, TypeModel.EXPENSES)
        }

        lifecycleScope.launch {
            budgetViewModel.getExpensesTransactionByMonth(selectMonth, TypeModel.EXPENSES)
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                budgetViewModel.transactionExpenses.collect { state ->
                    handleGetListTransByMonth(state)
                }
            }
        }

        customUpdateBudget.callback = object : ICallBackString {
            override fun callBack(ob: Any?, str: String) {
                if (str.isNotEmpty()) {
                    budgetViewModel.setTotalBudget(str.toLong())
                }
            }
        }

        customSelectMonth.callback = object : ICallBackString {
            override fun callBack(ob: Any?, str: String) {
                if (str.isNotEmpty()) {
                    val strDate = FormatNumber.convertMonthFormat(str)
                    budgetViewModel.setDate(strDate)
                }
            }
        }
    }

    private fun evenClick() {
        binding.tvBudgetDetail.setOnUnDoubleClickListener {
            startIntent(Intent(requireContext(), BudgetDetailActivity::class.java).apply {
                putExtra(DATE_BUDGET, selectMonth)
            }, false)
        }

        binding.ivEdit.setOnUnDoubleClickListener {
            val budget = binding.tvBudget.text.toString().replace(currency, "").replace(".", "")
            if(!customUpdateBudget.isShowing) customUpdateBudget.showDialog(budget)
        }

        binding.tvDate.setOnUnDoubleClickListener {
            if(!customSelectMonth.isShowing) customSelectMonth.show()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun handleGetListTransByMonth(state: UiState<MutableList<TransactionModel>>) {
        when (state) {
            is UiState.Loading -> {}
            is UiState.Error -> {}
            is UiState.Success -> {
                totalExpenses = state.data.sumOf { it.amount }
                binding.tvExpenses.text = getString(R.string.str_expenses) + ": $currency${FormatNumber.convertNumberToNumberDotString(totalExpenses)}"

                budgetViewModel.setTotalExpenses(totalExpenses)
            }
        }
    }

    private fun setUpPieChart(totalBudget: Long, totalExpenses: Long) {
        var isShow = false
        if (totalBudget != 0L && totalExpenses != 0L) isShow = true

        val spend = if (isShow) (totalExpenses.toFloat() / totalBudget.toFloat()).coerceIn(0f, 1f) else 0f
        val remaining = 1f - spend
        val entries = listOf(PieEntry(spend * 100f), PieEntry(remaining * 100f))

        val dataSet = PieDataSet(entries, "")
        dataSet.apply {
            colors = listOf(ContextCompat.getColor(requireContext(), R.color.main_color), ContextCompat.getColor(requireContext(), R.color.color_F4F9FF))
            valueTextSize = 12f
            sliceSpace = 1f
            setDrawIcons(false)
        }

        val pieData = PieData(dataSet)
        pieData.setDrawValues(false)

        binding.pieChart.apply {
            data = pieData
            description.isEnabled = false
            isRotationEnabled = false
            setDrawEntryLabels(false)
            holeRadius = 70f
            legend.isEnabled = false
            transparentCircleRadius = 0f
            setUsePercentValues(true)
            centerText = if (isShow) getString(R.string.remaining) + " ${(spend * 100f).roundToInt()}%" else ""
            setCenterTextColor(Color.BLACK)
            invalidate()
        }
    }

    override fun startIntent(intent: Intent, isFinish: Boolean) {
        startActivity(intent, null)
        if (isFinish) requireActivity().finish()
    }

    override fun startIntentForResult(
        startForResult: ActivityResultLauncher<Intent>,
        nameActivity: String,
        isFinish: Boolean
    ) {

    }

    override fun startIntentForResult(
        startForResult: ActivityResultLauncher<Intent>,
        intent: Intent,
        isFinish: Boolean
    ) {

    }

    companion object {
        fun newInstance(): BudgetFragment {
            val args = Bundle()

            val fragment = BudgetFragment()
            fragment.arguments = args
            return fragment
        }
    }
}