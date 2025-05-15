package com.nmh.base.project.activity.home

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.nmh.base.project.R
import com.nmh.base.project.activity.base.BaseFragment
import com.nmh.base.project.activity.base.UiState
import com.nmh.base.project.activity.search.SearchActivity
import com.nmh.base.project.activity.transactiondetail.DetailTransactionActivity
import com.nmh.base.project.callback.ICallBackItem
import com.nmh.base.project.callback.ICallBackString
import com.nmh.base.project.customView.CustomSelectMonth
import com.nmh.base.project.databinding.FragmentHomeBinding
import com.nmh.base.project.db.model.TransactionItem
import com.nmh.base.project.db.model.TransactionModel
import com.nmh.base.project.extensions.gone
import com.nmh.base.project.extensions.setOnUnDoubleClickListener
import com.nmh.base.project.extensions.visible
import com.nmh.base.project.helpers.SYMBOL_CURRENCY
import com.nmh.base.project.helpers.TRANSACTION_DETAIL
import com.nmh.base.project.helpers.TYPE_MODEL
import com.nmh.base.project.helpers.TypeLoan
import com.nmh.base.project.helpers.TypeModel
import com.nmh.base.project.sharepref.DataLocalManager
import com.nmh.base.project.utils.FormatNumber
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    @Inject
    lateinit var customSelectMonth: CustomSelectMonth

    @Inject
    lateinit var homeViewModel: HomeViewModel

    @Inject
    lateinit var transactionAdapter: TransactionAdapter

    private var typeModel = TypeModel.EXPENSES
    private var isEyeOpen = true
    private var dateFilter: String = ""

    private var currency : String = ""

    override fun setUp() {
        setupLayout()
        evenClick()
    }

    @SuppressLint("SetTextI18n")
    private fun setupLayout() {
        currency = DataLocalManager.getOption(SYMBOL_CURRENCY).toString()

        val date = SimpleDateFormat("MMMM, yyyy", Locale.ENGLISH)
        dateFilter = date.format(Date())
        homeViewModel.setDate(dateFilter)

        homeViewModel.type.observe(viewLifecycleOwner) {
            typeModel = it
            updateButton(it)
            homeViewModel.getTransactionByMonthAndType(dateFilter, typeModel)
        }

        homeViewModel.totalBalance.observe(viewLifecycleOwner) {
            binding.tvTotalTransaction.text =
                if (it >= 0) "$currency${FormatNumber.convertNumberToNumberDotString(it)}"
                else "-$currency${FormatNumber.convertNumberToNumberDotString(it).replace("-", "")}"
        }

        homeViewModel.date.observe(viewLifecycleOwner) {
            dateFilter = it
            binding.tvDate.text = it
            homeViewModel.getTransactionByMonthAndType(it, typeModel)
            homeViewModel.getAllListTransactionByMonth(it)
        }

        lifecycleScope.launch {
            homeViewModel.getTransactionByMonthAndType(dateFilter, typeModel)
            homeViewModel.getAllListTransactionByMonth(dateFilter)
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    homeViewModel.transactionByMonth.collect { state ->
                        handleGetAllTransaction(state)
                    }
                }

                launch {
                    homeViewModel.transactionByMonthAndType.collect { state ->
                        handleTransactionByDateAndType(state)
                    }
                }
            }
        }

        customSelectMonth.callback = object : ICallBackString {
            override fun callBack(ob: Any?, str: String) {
                val strDate = FormatNumber.convertMonthFormat(str)
                homeViewModel.setDate(strDate)
            }
        }

        transactionAdapter.callback = object : ICallBackItem {
            override fun callBack(ob: Any?, position: Int) {
                if (ob is TransactionItem.Transaction) {
                    startIntent(Intent(requireContext(), DetailTransactionActivity::class.java).apply {
                            putExtra(TRANSACTION_DETAIL, ob.model.id)
                        }, false
                    )
                }
            }
        }

        binding.rcvTransaction.apply {
            adapter = transactionAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun evenClick() {
        binding.rlDate.setOnUnDoubleClickListener {
            if (!customSelectMonth.isShowing)
                customSelectMonth.show()
        }

        binding.tvExpenses.setOnUnDoubleClickListener {
            homeViewModel.setType(TypeModel.EXPENSES)
        }

        binding.tvIncome.setOnUnDoubleClickListener {
            homeViewModel.setType(TypeModel.INCOME)
        }

        binding.tvLoan.setOnUnDoubleClickListener {
            homeViewModel.setType(TypeModel.LOAN)
        }

        binding.tvSearch.setOnUnDoubleClickListener {
            startIntent(Intent(requireContext(), SearchActivity::class.java).apply {
                putExtra(TYPE_MODEL, typeModel.name)
            }, false)
        }

        binding.ivEye.setOnUnDoubleClickListener {
            if (isEyeOpen) {
                binding.ivEye.setImageResource(R.drawable.ic_close_eye)
                binding.tvStar.gone()
                binding.tvTotalTransaction.visible()
                isEyeOpen = false
            } else {
                binding.ivEye.setImageResource(R.drawable.ic_open_eye)
                binding.tvStar.visible()
                binding.tvTotalTransaction.gone()
                isEyeOpen = true
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
                    isSameMonthYear(dateFilter, it.date)
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
                    homeViewModel.setTotalBalance(totalBalance)
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun handleTransactionByDateAndType(state: UiState<MutableList<TransactionItem>>) {
        when (state) {
            is UiState.Loading -> {}
            is UiState.Error -> {}
            is UiState.Success -> {
                val data = state.data
                if (data.isEmpty()) {
                    binding.rlEmpty.visible()
                    binding.clShowList.gone()
                } else {
                    binding.rlEmpty.gone()
                    binding.clShowList.visible()

                    val dateFormat = SimpleDateFormat("MMMM, dd yyyy", Locale.ENGLISH)
                    val targetFormat = SimpleDateFormat("MMMM, yyyy", Locale.ENGLISH)

                    val list = state.data.filterIsInstance<TransactionItem.Transaction>()
                        .filter {
                            val itemDate = dateFormat.parse(it.model.date)
                            val formattedItemDate = itemDate?.let { it1 -> targetFormat.format(it1) }
                            formattedItemDate == dateFilter
                        }

                    val type = list.firstOrNull()?.model?.typeModel
                    val filtered = list.filter {
                        isSameMonthYear(dateFilter, it.model.date)
                    }

                    if (type == typeModel && filtered.isNotEmpty()) {
                        when (typeModel) {
                            TypeModel.INCOME, TypeModel.EXPENSES -> {
                                val total = data.filterIsInstance<TransactionItem.Transaction>()
                                    .sumOf { it.model.amount }
                                binding.tvTotal.text = "$currency${FormatNumber.convertNumberToNumberDotString(total)}"
                            }

                            TypeModel.LOAN -> {
                                var totalLoan = 0L
                                var totalBorrow = 0L

                                data.filterIsInstance<TransactionItem.Transaction>().forEach {
                                    when (it.model.typeLoan) {
                                        TypeLoan.TYPE_LOAN -> totalLoan += it.model.amount
                                        TypeLoan.TYPE_BORROW -> totalBorrow += it.model.amount
                                        else -> Unit
                                    }
                                }

                                binding.tvTotalLoan.text = "$currency${FormatNumber.convertNumberToNumberDotString(totalLoan)}"
                                binding.tvTotalBorrow.text = "$currency${FormatNumber.convertNumberToNumberDotString(totalBorrow)}"
                            }
                        }
                        transactionAdapter.setData(data)
                    }
                }
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
        } else false
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
                binding.tvTotal.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_text_red))
                binding.tvEmpty.text = getString(R.string.add_your_first_expense_to_get_start)

                binding.clLoan.gone()
                binding.rlTotal.visible()
            }

            TypeModel.INCOME -> {
                binding.tvIncome.setBackgroundResource(R.drawable.bg_categories_enable)
                binding.tvIncome.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_F4F9FF))
                binding.tvTotal.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_text_green))
                binding.tvEmpty.text = getString(R.string.add_your_first_income_to_get_start)

                binding.clLoan.gone()
                binding.rlTotal.visible()
            }

            TypeModel.LOAN -> {
                binding.tvLoan.setBackgroundResource(R.drawable.bg_categories_enable)
                binding.tvLoan.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_F4F9FF))
                binding.tvEmpty.text = getString(R.string.add_your_first_loan_to_get_start)

                binding.clLoan.visible()
                binding.rlTotal.gone()
            }
        }
    }

    override fun onResume() {
        super.onResume()

        homeViewModel.getTransactionByMonthAndType(dateFilter, typeModel)
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
        fun newInstance(): HomeFragment {
            val args = Bundle()

            val fragment = HomeFragment()
            fragment.arguments = args
            return fragment
        }
    }
}