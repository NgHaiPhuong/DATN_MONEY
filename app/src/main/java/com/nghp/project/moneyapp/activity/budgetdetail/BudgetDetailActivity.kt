package com.nghp.project.moneyapp.activity.budgetdetail

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.view.Gravity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.nghp.project.moneyapp.R
import com.nghp.project.moneyapp.activity.base.BaseActivity
import com.nghp.project.moneyapp.activity.base.UiState
import com.nghp.project.moneyapp.activity.budgedetaileach.BudgetDetailEachActivity
import com.nghp.project.moneyapp.callback.ICallBackItem
import com.nghp.project.moneyapp.callback.ICallBackItemCheck
import com.nghp.project.moneyapp.callback.ICallBackString
import com.nghp.project.moneyapp.customView.CustomAddBudget
import com.nghp.project.moneyapp.customView.CustomSelectMonth
import com.nghp.project.moneyapp.customView.CustomUpdateBudget
import com.nghp.project.moneyapp.databinding.ActivityBudgetDetailBinding
import com.nghp.project.moneyapp.db.model.BudgetModel
import com.nghp.project.moneyapp.db.model.TransactionModel
import com.nghp.project.moneyapp.extensions.setOnUnDoubleClickListener
import com.nghp.project.moneyapp.extensions.showToast
import com.nghp.project.moneyapp.helpers.BUDGET
import com.nghp.project.moneyapp.helpers.BUDGET_DETAIL
import com.nghp.project.moneyapp.helpers.DATE_BUDGET
import com.nghp.project.moneyapp.helpers.ID_BUDGET
import com.nghp.project.moneyapp.helpers.SYMBOL_CURRENCY
import com.nghp.project.moneyapp.helpers.TypeModel
import com.nghp.project.moneyapp.sharepref.DataLocalManager
import com.nghp.project.moneyapp.utils.FormatNumber
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.roundToInt

@AndroidEntryPoint
class BudgetDetailActivity :
    BaseActivity<ActivityBudgetDetailBinding>(ActivityBudgetDetailBinding::inflate) {

    @Inject lateinit var detailBudgetViewModel: DetailBudgetViewModel

    @Inject lateinit var customSelectMonth: CustomSelectMonth

    @Inject lateinit var customAddBudget: CustomAddBudget

    @Inject lateinit var budgetDetailAdapter: BudgetDetailAdapter

    @Inject lateinit var customUpdateBudget: CustomUpdateBudget

    private var totalExpenses = 0L
    private var totalBudget = 0L
    private var totalGapBudget = 0L
    private var selectMonth = ""
    private lateinit var budgetModel : BudgetModel
    private var currency : String = ""

    override fun setUp() {
        setupLayout()
        evenClick()
    }

    @SuppressLint("SetTextI18n")
    private fun setupLayout() {
        currency = DataLocalManager.getOption(SYMBOL_CURRENCY).toString()
        selectMonth = intent.getStringExtra(DATE_BUDGET) ?: ""

        detailBudgetViewModel.setTotalBudget(DataLocalManager.getLong(BUDGET))

        detailBudgetViewModel.date.observe(this) {
            selectMonth = it
            setUpPieChart(totalBudget, totalExpenses)
            detailBudgetViewModel.getExpensesTransactionByMonth(it, TypeModel.EXPENSES)
        }

        detailBudgetViewModel.totalExpenses.observe(this) {
            totalExpenses = it
            setUpPieChart(totalBudget, it)
        }

        detailBudgetViewModel.totalBudget.observe(this) {
            totalBudget = it
            setUpPieChart(it, totalExpenses)
            binding.tvBudget.text = getString(R.string.str_budget) + ": " + "$currency${FormatNumber.convertNumberToNumberDotString(totalBudget)}"
        }

        lifecycleScope.launch {
            detailBudgetViewModel.getListBudget()
            detailBudgetViewModel.getExpensesTransactionByMonth(selectMonth, TypeModel.EXPENSES)
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    detailBudgetViewModel.budget.collect { state ->
                        handleGetAllBudget(state)
                    }
                }

                launch {
                    detailBudgetViewModel.transactionExpenses.collect { state ->
                        handleGetListExpensesTransaction(state)
                    }
                }
            }
        }

        customSelectMonth.callback = object : ICallBackString {
            override fun callBack(ob: Any?, str: String) {
                val strDate = FormatNumber.convertMonthFormat(str)
                detailBudgetViewModel.setDate(strDate)
            }
        }

        customAddBudget.callBack = object : ICallBackItem {
            override fun callBack(ob: Any?, position: Int) {
                if (ob is BudgetModel) {
                    if(totalGapBudget + ob.amount > totalBudget) {
                        showToast(getString(R.string.over_budget), Gravity.CENTER)
                        return
                    } else {
                        detailBudgetViewModel.insertBudget(ob)
                        customAddBudget.dismiss()
                    }
                }
            }
        }

        customUpdateBudget.callback = object : ICallBackString {
            override fun callBack(ob: Any?, str: String) {
                budgetModel.amount = str.toLong()
                detailBudgetViewModel.updateBudget(budgetModel)
            }
        }

        budgetDetailAdapter.callBack = object : ICallBackItemCheck {
            override fun check(ob: Any?, isCheck: Boolean) {
                if (ob is BudgetModel) {
                    if(isCheck) {
                        startIntent(Intent(this@BudgetDetailActivity, BudgetDetailEachActivity::class.java).apply {
                            putExtra(ID_BUDGET, ob.id)
                            putExtra(BUDGET_DETAIL, ob)
                        }, false)
                    } else {
                        budgetModel = ob
                        if(!customUpdateBudget.isShowing) {
                            customUpdateBudget.showDialog(budgetModel.amount.toString())
                        }
                    }
                }
            }
        }

        binding.rcvBudgetList.apply {
            adapter = budgetDetailAdapter
            layoutManager = GridLayoutManager(this@BudgetDetailActivity, 3)
        }
    }

    private fun evenClick() {
        binding.ivBack.setOnUnDoubleClickListener { finish() }

        binding.ivCalendar.setOnUnDoubleClickListener {
            if (!customSelectMonth.isShowing)
                customSelectMonth.show()
        }

        binding.tvAddBudget.setOnUnDoubleClickListener {
            if (!customAddBudget.isShowing)
                customAddBudget.showDialog()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun handleGetAllBudget(state: UiState<MutableList<BudgetModel>>) {
        when (state) {
            is UiState.Loading -> {}
            is UiState.Error -> {}
            is UiState.Success -> {
                totalGapBudget = state.data.sumOf { it.amount }
                budgetDetailAdapter.setData(state.data)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun handleGetListExpensesTransaction(state: UiState<MutableList<TransactionModel>>) {
        when (state) {
            is UiState.Loading -> {}
            is UiState.Error -> {}
            is UiState.Success -> {
                totalExpenses = state.data.sumOf { it.amount }
                binding.tvExpenses.text = getString(R.string.str_expenses) + ": $currency${FormatNumber.convertNumberToNumberDotString(totalExpenses)}"

                detailBudgetViewModel.setTotalExpenses(totalExpenses)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setUpPieChart(totalBudget: Long, totalExpenses: Long) {

        val remain = totalBudget - totalExpenses
        if(remain < 0) binding.tvRemain.text = getString(R.string.str_remain) + ": -$currency${FormatNumber.convertNumberToNumberDotString(remain)}"
        else binding.tvRemain.text = getString(R.string.str_remain) + ": $currency${FormatNumber.convertNumberToNumberDotString(remain)}"

        var isShow = false
        if (totalBudget != 0L && totalExpenses != 0L) isShow = true

        val spend =
            if (isShow) (totalExpenses.toFloat() / totalBudget.toFloat()).coerceIn(0f, 1f) else 0f
        val remaining = 1f - spend
        val entries = listOf(PieEntry(spend * 100f), PieEntry(remaining * 100f))

        val dataSet = PieDataSet(entries, "")
        dataSet.apply {
            colors = listOf(
                ContextCompat.getColor(this@BudgetDetailActivity, R.color.main_color),
                ContextCompat.getColor(this@BudgetDetailActivity, R.color.color_F4F9FF)
            )
            valueTextSize = 11f
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
}