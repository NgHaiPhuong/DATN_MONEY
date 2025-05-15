package com.nmh.base.project.activity.budgedetaileach

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.view.Gravity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.nmh.base.project.R
import com.nmh.base.project.activity.base.BaseActivity
import com.nmh.base.project.activity.base.UiState
import com.nmh.base.project.activity.detalstatistic.DetailStatisticActivity
import com.nmh.base.project.activity.statistics.StatisticAdapter
import com.nmh.base.project.callback.ICallBackCheck
import com.nmh.base.project.callback.ICallBackItem
import com.nmh.base.project.customView.CustomDeleteBudgetDialog
import com.nmh.base.project.databinding.ActivityBudgetDetailEachBinding
import com.nmh.base.project.db.model.BudgetModel
import com.nmh.base.project.db.model.TransactionByCategoryModel
import com.nmh.base.project.db.model.TransactionModel
import com.nmh.base.project.extensions.gone
import com.nmh.base.project.extensions.setOnUnDoubleClickListener
import com.nmh.base.project.extensions.showToast
import com.nmh.base.project.extensions.visible
import com.nmh.base.project.helpers.BUDGET_DETAIL
import com.nmh.base.project.helpers.CHART_DETAIL
import com.nmh.base.project.helpers.SYMBOL_CURRENCY
import com.nmh.base.project.helpers.TRANSACTION_STATISTIC_MODEL
import com.nmh.base.project.sharepref.DataLocalManager
import com.nmh.base.project.utils.FormatNumber
import com.nmh.base.project.utils.UtilsBitmap
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BudgetDetailEachActivity : BaseActivity<ActivityBudgetDetailEachBinding>(ActivityBudgetDetailEachBinding::inflate) {

    @Inject lateinit var viewModel: BudgetDetailEachViewModel

    @Inject lateinit var deleteDialog: CustomDeleteBudgetDialog

    @Inject
    lateinit var statisticAdapter: StatisticAdapter

    private var budgetModel : BudgetModel? = null

    override fun setUp() {
        setupLayout()
        evenClick()
    }

    private fun setupLayout() {
        budgetModel = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(BUDGET_DETAIL, BudgetModel::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(BUDGET_DETAIL)
        }

        budgetModel?.let { item ->
            updateData(item, 0L)

            lifecycleScope.launch {
                viewModel.getTransactionByBudget(item.id)
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.transactionByBudget.collect {
                        when (it) {
                            is UiState.Loading -> {}
                            is UiState.Error -> {}
                            is UiState.Success -> {
                                handleTransactionByBudget(item, it)
                            }
                        }
                    }
                }
            }
        }

        deleteDialog.callback = object : ICallBackCheck {
            override fun check(isCheck: Boolean) {
                budgetModel?.let {
                    viewModel.deleteBudget(it)
                    showToast(getString(R.string.delete_succesfull), Gravity.CENTER)
                    finish()
                } ?: run {
                    showToast(getString(R.string.delete_fail), Gravity.CENTER)
                }
            }
        }

        statisticAdapter.callback = object : ICallBackItem {
            override fun callBack(ob: Any?, position: Int) {
                if(ob is TransactionByCategoryModel) {
                    startIntent(Intent(this@BudgetDetailEachActivity, DetailStatisticActivity::class.java).apply {
                        putExtra(TRANSACTION_STATISTIC_MODEL, ob)
                    }, false)
                    DataLocalManager.setBoolean(CHART_DETAIL, true)
                }
            }
        }

        binding.rcvBudgetList.apply {
            adapter = statisticAdapter
            layoutManager = LinearLayoutManager(this@BudgetDetailEachActivity)
        }

        binding.pillProgressView.setDarkColor(Color.WHITE)
        val color = UtilsBitmap.getRandomPastelColor()
        binding.pillProgressView.setLightColor(color)
    }

    private fun evenClick() {
        binding.ivBack.setOnUnDoubleClickListener { finish() }
        binding.deleteButton.setOnUnDoubleClickListener {
            if(!deleteDialog.isShowing) deleteDialog.showDialog()
        }
    }

    private fun updateData(item: BudgetModel, total : Long = 0L) {
        item?.let {
            binding.tvName.text = item.name
            binding.pillProgressView.setProgress((item.amount - total).toInt())
            binding.pillProgressView.setFormPercentText(false)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun handleTransactionByBudget(item: BudgetModel, state: UiState<MutableList<TransactionModel>>) {
        when (state) {
            is UiState.Loading -> {}
            is UiState.Error -> {}
            is UiState.Success -> {
                if(state.data.isEmpty()) {
                    binding.rlEmpty.visible()
                    binding.rlList.gone()
                } else {
                    binding.rlEmpty.gone()
                    binding.rlList.visible()

                    val totalAmount = state.data.sumOf { it.amount }
                    val currency = DataLocalManager.getOption(SYMBOL_CURRENCY).toString()
                    binding.totalExpenditureValue.text = "$currency${FormatNumber.convertNumberToNumberDotString(totalAmount)}"

                    val listStatisticByName =
                        state.data.groupBy { it.category }
                            .map { (category, item) ->
                                val total = item.sumOf { it.amount }
                                TransactionByCategoryModel(category, total)
                            }
                            .toMutableList()

                    updateData(item, totalAmount)
                    statisticAdapter.setData(listStatisticByName, item.amount)
                }
            }
        }
    }
}