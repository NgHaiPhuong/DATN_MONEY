package com.nmh.base.project.activity.detalstatistic

import android.annotation.SuppressLint
import android.os.Build
import android.view.MotionEvent
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.nmh.base.project.R
import com.nmh.base.project.activity.base.BaseActivity
import com.nmh.base.project.activity.base.UiState
import com.nmh.base.project.customView.CustomBarChartRenderer
import com.nmh.base.project.databinding.ActivityDetailStatisticBinding
import com.nmh.base.project.db.model.StatisticModel
import com.nmh.base.project.db.model.TransactionByCategoryModel
import com.nmh.base.project.db.model.TransactionItem
import com.nmh.base.project.extensions.setOnUnDoubleClickListener
import com.nmh.base.project.helpers.CHART_DETAIL
import com.nmh.base.project.helpers.TRANSACTION_STATISTIC_MODEL
import com.nmh.base.project.sharepref.DataLocalManager
import com.nmh.base.project.utils.FormatNumber
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class DetailStatisticActivity :
    BaseActivity<ActivityDetailStatisticBinding>(ActivityDetailStatisticBinding::inflate) {

    @Inject
    lateinit var detailStatisticViewModel: DetailStatisticViewModel

    @Inject
    lateinit var detailStatisticAdapter: DetailStatisticAdapter

    private var color: Int = -1

    override fun setUp() {
        setupLayout()
        evenClick()
    }

    @SuppressLint("SetTextI18n")
    private fun setupLayout() {
//        val date = intent.getStringExtra(DATE_STATISTICS) ?: ""
        val category = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(
                TRANSACTION_STATISTIC_MODEL,
                TransactionByCategoryModel::class.java
            )
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(TRANSACTION_STATISTIC_MODEL)
        }

        category?.let {
            color = it.category.colorImg
            binding.tvTitle.text = category.category.name
            binding.tvTotalCategory.text =
                getString(R.string.str_total) + " " + category.category.name
        }

        lifecycleScope.launch {
            category?.let {
                detailStatisticViewModel.getListTransactionByCategory(category.category)
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    detailStatisticViewModel.transactionByCategory.collect { state ->
                        handleTransactionByMonthAndType(state)
                    }
                }
            }
        }

        binding.rcvTransaction.apply {
            adapter = detailStatisticAdapter
            layoutManager = LinearLayoutManager(this@DetailStatisticActivity)
        }
    }

    private fun evenClick() {
        binding.ivBack.setOnUnDoubleClickListener {
            DataLocalManager.setBoolean(CHART_DETAIL, false)
            finish()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun handleTransactionByMonthAndType(state: UiState<MutableList<TransactionItem>>) {
        when (state) {
            is UiState.Loading -> {}
            is UiState.Error -> {}
            is UiState.Success -> {
                setUpBarChart(state.data)
                detailStatisticAdapter.setData(state.data)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setUpBarChart(list: MutableList<TransactionItem>) {

        val listData = mutableListOf<StatisticModel>()

        val listTrans = list.asSequence()
            .filterIsInstance<TransactionItem.Transaction>()
            .toMutableList()

        val dateFormat = SimpleDateFormat("MMMM, yyyy", Locale.ENGLISH)
        val calendar = Calendar.getInstance()
        val thisMonth = dateFormat.format(calendar.time)
        calendar.add(Calendar.MONTH, -1)
        val lastMonth = dateFormat.format(calendar.time)

        val filteredItems = listTrans
            .mapNotNull {
                val month = FormatNumber.convertDateToMonthYear(it.model.date)
                if (month == thisMonth || month == lastMonth) {
                    month to it
                } else null
            }

        val stats = filteredItems
            .groupBy({ it.first }, { it.second })
            .map { (month, items) -> StatisticModel(month, items.sumOf { it.model.amount }) }
            .sortedBy { FormatNumber.monthNameToNumber(it.month) }
            .toMutableList()

        listData.addAll(stats)

        val monthsFound = filteredItems.map { it.first }.toSet()
        if (monthsFound.contains(thisMonth) && !monthsFound.contains(lastMonth)) {
            listData.add(0, StatisticModel("Last month", 0L))
        }

        val entries = listData.mapIndexed { index, items ->
            BarEntry(index.toFloat(), items.expenses.toFloat(), items.month)
        }

        val dataSet = BarDataSet(entries, "")
        dataSet.setDrawValues(true)
        dataSet.colors = list.map { ContextCompat.getColor(this, color) }.distinct()

        val barData = BarData(dataSet)
        barData.barWidth = 0.4f
        barData.setValueTextSize(14f)
        barData.setValueTextColor(ContextCompat.getColor(this, R.color.main_color))

        binding.barChart.apply {
            data = barData
            description.isEnabled = false
            setFitBars(true)
            animateY(1000)
            legend.isEnabled = false
            axisRight.isEnabled = false
            setPinchZoom(false)
            axisLeft.axisMinimum = 0f
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

        val yAxisLeft = binding.barChart.axisLeft
        yAxisLeft.apply {
            axisMinimum = 0f
            setDrawLabels(true)
            setDrawAxisLine(false)
            setDrawGridLines(false)
            granularity = 1f
            textSize = 12f
            textColor = ContextCompat.getColor(this@DetailStatisticActivity, R.color.main_color)
            setLabelCount(1, true)

            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return if (value == 0f) "0" else ""
                }
            }
        }

        val labels = listOf("Last month", "This month")
        binding.barChart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            valueFormatter = IndexAxisValueFormatter(labels)
            setDrawLabels(true)
            granularity = 1f
            labelCount = labels.size
            axisMinimum = -0.5f
            axisMaximum = labels.size - 0.5f
            setDrawAxisLine(true)
            setDrawGridLines(false)
            textSize = 12f
            textColor = ContextCompat.getColor(this@DetailStatisticActivity, R.color.black)
        }

        binding.barChart.setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> view.parent.requestDisallowInterceptTouchEvent(true)
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> view.parent.requestDisallowInterceptTouchEvent(
                    false
                )
            }
            false
        }

        binding.barChart.invalidate()
    }
}