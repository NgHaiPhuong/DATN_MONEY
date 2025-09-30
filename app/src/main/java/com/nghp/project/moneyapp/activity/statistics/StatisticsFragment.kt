package com.nghp.project.moneyapp.activity.statistics

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.MotionEvent
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.MPPointF
import com.nghp.project.moneyapp.R
import com.nghp.project.moneyapp.activity.base.BaseFragment
import com.nghp.project.moneyapp.activity.base.UiState
import com.nghp.project.moneyapp.activity.chatbot.ChatBotActivity
import com.nghp.project.moneyapp.activity.detalstatistic.DetailStatisticActivity
import com.nghp.project.moneyapp.activity.totalbalance.TotalBalanceActivity
import com.nghp.project.moneyapp.callback.ICallBackItem
import com.nghp.project.moneyapp.callback.ICallBackString
import com.nghp.project.moneyapp.customView.CustomBarChartRenderer
import com.nghp.project.moneyapp.customView.CustomPieChartRenderer
import com.nghp.project.moneyapp.customView.CustomSelectMonth
import com.nghp.project.moneyapp.databinding.FragmentStatisticsBinding
import com.nghp.project.moneyapp.db.model.TransactionByCategoryModel
import com.nghp.project.moneyapp.db.model.TransactionModel
import com.nghp.project.moneyapp.extensions.gone
import com.nghp.project.moneyapp.extensions.setOnUnDoubleClickListener
import com.nghp.project.moneyapp.extensions.visible
import com.nghp.project.moneyapp.helpers.CHART_DETAIL
import com.nghp.project.moneyapp.helpers.SYMBOL_CURRENCY
import com.nghp.project.moneyapp.helpers.TRANSACTION_STATISTIC_MODEL
import com.nghp.project.moneyapp.helpers.TypeLoan
import com.nghp.project.moneyapp.helpers.TypeModel
import com.nghp.project.moneyapp.sharepref.DataLocalManager
import com.nghp.project.moneyapp.utils.FormatNumber
import com.nghp.project.moneyapp.utils.UtilsBitmap
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlin.math.roundToInt

@AndroidEntryPoint
class StatisticsFragment : BaseFragment<FragmentStatisticsBinding>(FragmentStatisticsBinding::inflate) {

    @Inject
    lateinit var customSelectMonth: CustomSelectMonth

    @Inject
    lateinit var statisticViewModel: StatisticViewModel

    @Inject
    lateinit var statisticAdapter:  StatisticAdapter

    private lateinit var selectDate: String
    private var typeModel = TypeModel.EXPENSES
    private var isShowPieChart = true
    private var currency = ""

    override fun setUp() {
        setupLayout()
        evenClick()
    }

    private fun setupLayout() {
        DataLocalManager.setBoolean(CHART_DETAIL, false)

        currency = DataLocalManager.getOption(SYMBOL_CURRENCY).toString()

        val date = SimpleDateFormat("MMMM, yyyy", Locale.ENGLISH)
        selectDate = date.format(Date())
        statisticViewModel.setDate(selectDate)

        statisticViewModel.type.observe(viewLifecycleOwner) {
            updateButton(it)
            typeModel = it
            statisticViewModel.getTransactionByMonthAndType(selectDate, it)
        }

        statisticViewModel.date.observe(viewLifecycleOwner) {
            selectDate = it
            binding.tvMonth.text = FormatNumber.convertDateToMonth(selectDate)
            binding.tvDateList.text = selectDate
            statisticViewModel.getTransactionByMonthAndType(it, typeModel)
        }

        lifecycleScope.launch {
            statisticViewModel.getTransactionByMonthAndType(selectDate, typeModel)
            statisticViewModel.getAllListTransactionByMonth(selectDate)
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    statisticViewModel.transactionByMonthAndType.collect { state ->
                        handleTransactionByMonthAndType(state)
                    }
                }

                launch {
                    statisticViewModel.transactionByMonth.collect { state ->
                        handleGetAllTransaction(state)
                    }
                }
            }
        }

        statisticAdapter.callback = object : ICallBackItem {
            override fun callBack(ob: Any?, position: Int) {
                if(ob is TransactionByCategoryModel) {
                    startIntent(Intent(requireContext(), DetailStatisticActivity::class.java).apply {
                        putExtra(TRANSACTION_STATISTIC_MODEL, ob)
                    }, false)
                    DataLocalManager.setBoolean(CHART_DETAIL, true)
                }
            }
        }

        binding.rcvListByMonth.apply {
            adapter = statisticAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        customSelectMonth.callback = object : ICallBackString {
            override fun callBack(ob: Any?, str: String) {
                val strDate = FormatNumber.convertMonthFormat(str)
                statisticViewModel.setDate(strDate)
            }
        }
    }

    private fun evenClick() {
        binding.ivShowCalender.setOnUnDoubleClickListener {
            if (!customSelectMonth.isShowing)
                customSelectMonth.show()
        }

        binding.tvExpenses.setOnUnDoubleClickListener {
            statisticViewModel.setType(TypeModel.EXPENSES)
        }

        binding.tvIncome.setOnUnDoubleClickListener {
            statisticViewModel.setType(TypeModel.INCOME)
        }

        binding.tvLoan.setOnUnDoubleClickListener {
            statisticViewModel.setType(TypeModel.LOAN)
        }

        binding.clTotalBalance.setOnUnDoubleClickListener {
            startIntent(Intent(requireContext(), TotalBalanceActivity::class.java), false)
        }

        binding.ivChatbot.setOnUnDoubleClickListener {
            startIntent(Intent(requireContext(), ChatBotActivity::class.java), false)
        }

        binding.ivChart.setOnUnDoubleClickListener {
            if(isShowPieChart) {
                binding.pieChart.visible()
                binding.barChart.gone()
                isShowPieChart = false
            } else {
                binding.pieChart.gone()
                binding.barChart.visible()
                isShowPieChart = true
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun handleGetAllTransaction(state: UiState<MutableList<TransactionModel>>) {
        when (state) {
            is UiState.Loading -> {}
            is UiState.Error -> {}
            is UiState.Success -> {
                val transactions = state.data

                var totalExpenses = 0L
                var totalIncome = 0L
                var totalLoan = 0L
                var totalBorrow = 0L

                val filtered = transactions.filter {
                    isSameMonthYear(selectDate, it.date)
                }
                if(filtered.isNotEmpty()) {
                    transactions.forEach {
                        when (it.typeModel) {
                            TypeModel.EXPENSES -> totalExpenses += it.amount
                            TypeModel.INCOME -> totalIncome += it.amount
                            TypeModel.LOAN -> {
                                when (it.typeLoan) {
                                    TypeLoan.TYPE_LOAN -> totalLoan += it.amount
                                    TypeLoan.TYPE_BORROW -> totalBorrow += it.amount
                                    else -> Unit
                                }
                            }
                            else -> Unit
                        }
                    }
                    val totalBalance = totalIncome + (totalLoan - totalBorrow) - totalExpenses
                    binding.tvTotalTransaction.text =
                        if (totalBalance >= 0) "$currency${FormatNumber.convertNumberToNumberDotString(totalBalance)}"
                        else "-$currency${FormatNumber.convertNumberToNumberDotString(totalBalance).replace("-", "")}"
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun handleTransactionByMonthAndType(state: UiState<MutableList<TransactionModel>>) {
        when (state) {
            is UiState.Loading -> {}
            is UiState.Error -> {}
            is UiState.Success -> {
                val type = state.data.firstOrNull()?.typeModel

                val monthYearList = state.data.mapNotNull { item ->
                    val parts = item.date.split(" ")
                    if (parts.size == 3) {
                        "${parts[0]} ${parts[2]}"
                    } else null
                }.distinct()

                val monthList = monthYearList.firstOrNull()
                var listStatisticByName = mutableListOf<TransactionByCategoryModel>()
                var totalBalance = 0L

                monthList?.let { item ->
                    if (type == typeModel && item.contains(selectDate)) {
                        totalBalance = state.data.sumOf { it.amount }

                        listStatisticByName =
                            state.data.groupBy { it.category }
                                .map { (category, item) ->
                                    val total = item.sumOf { it.amount }
                                    TransactionByCategoryModel(category, total)
                                }
                                .toMutableList()
                    }
                }

                setUpBarChart(listStatisticByName)
                setUpPieChart(listStatisticByName)
                statisticAdapter.setData(listStatisticByName, totalBalance)
            }
        }
    }

    private fun isSameMonthYear(input: String, date: String): Boolean {
        val monthYear = input.trim().split(",").map { it.trim() }
        val dateParts = date.trim().split(" ")

        return if (monthYear.size == 2 && dateParts.size >= 3) {
            val month = dateParts[0].replace(",", "")
            val year = dateParts.last()
            month.equals(monthYear[0], ignoreCase = true) && year == monthYear[1]
        } else {
            false
        }
    }

    @SuppressLint("ClickableViewAccessibility", "UseKtx")
    private fun setUpBarChart(list: MutableList<TransactionByCategoryModel>) {
        val entries = if (list.isEmpty())
            listOf(BarEntry(0f, 0f))
        else {
            list.mapIndexed { index, items ->
                BarEntry(index.toFloat(), items.totalAmount.toFloat())
            }
        }

        val dataSet = BarDataSet(entries, "")
        dataSet.setDrawValues(false)
        dataSet.colors = if (list.isEmpty()) listOf(Color.TRANSPARENT)
        else list.map { ContextCompat.getColor(requireContext(), it.category.colorImg) }.distinct()

        val barData = BarData(dataSet)
        barData.barWidth = 0.3f

        binding.barChart.apply {
            data = barData
            description.isEnabled = false
            setFitBars(true)
            animateY(1000)
            legend.isEnabled = false
            axisRight.isEnabled = false
            setPinchZoom(false)
            setScaleEnabled(false)
            isDoubleTapToZoomEnabled = false

            renderer = CustomBarChartRenderer(
                binding.barChart,
                binding.barChart.animator,
                binding.barChart.viewPortHandler
            )
            isHighlightPerTapEnabled = false
            isHighlightPerDragEnabled = false
        }

        val xAxis = binding.barChart.xAxis
        xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            setDrawLabels(false)
            setDrawAxisLine(true)
            setDrawGridLines(false)
            gridColor = Color.parseColor("#EEEEEE")
            axisLineColor = Color.TRANSPARENT
            setCenterAxisLabels(false)
            axisLineColor = Color.BLACK
        }

        val currency = DataLocalManager.getOption(SYMBOL_CURRENCY)

        val yAxisLeft = binding.barChart.axisLeft
        yAxisLeft.apply {
            axisMinimum = 0f
            setDrawGridLines(true)
            gridColor = Color.parseColor("#EEEEEE")
            setDrawAxisLine(false)
            axisMaximum = if(list.isEmpty()) 450f else binding.barChart.data.yMax + 20f
            valueFormatter = object : ValueFormatter() {
                @SuppressLint("DefaultLocale")
                override fun getFormattedValue(value: Float): String {
                    return "$currency" + value.toInt().toString()
                }
            }
        }

        // Chặn ScrollView (hoặc NestedScrollView) không cuộn khi người dùng tương tác với biểu đồ
        binding.barChart.setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> view.parent.requestDisallowInterceptTouchEvent(true)
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> view.parent.requestDisallowInterceptTouchEvent(false)
            }
            false
        }

        binding.barChart.invalidate()
    }

    private fun setUpPieChart(list: MutableList<TransactionByCategoryModel>) {

        val entries = if(list.isNotEmpty()) {
            list.map { item ->
                val icon = UtilsBitmap.getIconWithCircleBackground(requireContext(), item.category.img)
                PieEntry(item.totalAmount.toFloat(), icon)
            }
        } else listOf(PieEntry(1f))

        val dataSet = PieDataSet(entries, "")
        dataSet.apply {
            colors = if(list.isNotEmpty()) {
                list.map { ContextCompat.getColor(requireContext(), it.category.colorImg) }.distinct()
            } else listOf(ContextCompat.getColor(requireContext(), R.color.main_color))
            valueTextSize = 12f
            sliceSpace = 1f
            setDrawIcons(false)
            iconsOffset = MPPointF(0f, -40f)
        }

        val pieData = PieData(dataSet)
        pieData.setDrawValues(false)

        binding.pieChart.apply {
            data = pieData
            description.isEnabled = false
            isRotationEnabled = false
            setDrawEntryLabels(false)
            holeRadius = 60f
            legend.isEnabled = false
            transparentCircleRadius = 0f
            setUsePercentValues(true)
            setExtraOffsets(12f, 12f, 12f, 12f)

            renderer = CustomPieChartRenderer(
                binding.pieChart,
                binding.pieChart.animator,
                binding.pieChart.viewPortHandler
            )

            setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                override fun onValueSelected(e: Entry?, h: Highlight?) {
                    if (e !is PieEntry) return

                    val dataSetPie = binding.pieChart.data.getDataSetByIndex(0) as PieDataSet
                    binding.pieChart.data.setDrawValues(true)
                    dataSetPie.selectionShift = 2f
                    dataSetPie.valueFormatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            return value.roundToInt().toString() + "%"
                        }
                    }

                    val index = dataSetPie.getEntryIndex(e)
                    var textColors = mutableListOf<Int>()
                    if (list.isNotEmpty()) {
                        for (i in 0 until dataSetPie.entryCount) {
                            if (i == index) textColors.add(Color.WHITE)
                            else textColors.add(Color.TRANSPARENT)
                        }
                    } else textColors = mutableListOf(Color.TRANSPARENT)

                    dataSetPie.setValueTextColors(textColors)
                    binding.pieChart.invalidate()
                }

                override fun onNothingSelected() {
                    dataSet.setDrawValues(false)
                    dataSet.selectionShift = 0f
                    binding.pieChart.invalidate()
                }
            })

            invalidate()
        }
    }

    private fun updateButton(typeModel: TypeModel) {
        binding.tvExpenses.setBackgroundResource(R.drawable.bg_categories_disenable)
        binding.tvIncome.setBackgroundResource(R.drawable.bg_categories_disenable)
        binding.tvLoan.setBackgroundResource(R.drawable.bg_categories_disenable)

        binding.tvExpenses.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_001C41))
        binding.tvIncome.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_001C41))
        binding.tvLoan.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_001C41))

        when (typeModel) {
            TypeModel.EXPENSES -> {
                binding.tvExpenses.setBackgroundResource(R.drawable.bg_categories_enable)
                binding.tvExpenses.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_F4F9FF))
            }

            TypeModel.INCOME -> {
                binding.tvIncome.setBackgroundResource(R.drawable.bg_categories_enable)
                binding.tvIncome.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_F4F9FF))
            }

            TypeModel.LOAN -> {
                binding.tvLoan.setBackgroundResource(R.drawable.bg_categories_enable)
                binding.tvLoan.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_F4F9FF))
            }
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
        fun newInstance(): StatisticsFragment {
            val args = Bundle()

            val fragment = StatisticsFragment()
            fragment.arguments = args
            return fragment
        }
    }
}