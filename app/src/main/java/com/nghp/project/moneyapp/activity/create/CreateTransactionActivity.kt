package com.nghp.project.moneyapp.activity.create

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.nghp.project.moneyapp.R
import com.nghp.project.moneyapp.activity.base.BaseActivity
import com.nghp.project.moneyapp.activity.base.UiState
import com.nghp.project.moneyapp.callback.ICallBackItem
import com.nghp.project.moneyapp.callback.ICallBackString
import com.nghp.project.moneyapp.customView.CustomChooseBudget
import com.nghp.project.moneyapp.customView.CustomDateDialog
import com.nghp.project.moneyapp.customView.CustomTimeDialog
import com.nghp.project.moneyapp.databinding.ActivityCreateTransactionBinding
import com.nghp.project.moneyapp.db.model.BudgetModel
import com.nghp.project.moneyapp.db.model.CategoryModel
import com.nghp.project.moneyapp.db.model.TransactionModel
import com.nghp.project.moneyapp.extensions.gone
import com.nghp.project.moneyapp.extensions.setOnUnDoubleClickListener
import com.nghp.project.moneyapp.extensions.showToast
import com.nghp.project.moneyapp.extensions.visible
import com.nghp.project.moneyapp.helpers.EDIT_TRANSACTION
import com.nghp.project.moneyapp.helpers.IS_EDIT
import com.nghp.project.moneyapp.helpers.TypeLoan
import com.nghp.project.moneyapp.helpers.TypeModel
import com.nghp.project.moneyapp.utils.DataHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class CreateTransactionActivity :
    BaseActivity<ActivityCreateTransactionBinding>(ActivityCreateTransactionBinding::inflate) {

    @Inject
    lateinit var categoryAdapter: CategoryAdapter

    @Inject
    lateinit var customDateDialog: CustomDateDialog

    @Inject
    lateinit var customTimeDialog: CustomTimeDialog

    @Inject
    lateinit var createViewModel: CreateTransactionViewModel

    @Inject
    lateinit var customChooseDialog: CustomChooseBudget

    private lateinit var category: CategoryModel
    private var isEdit: Boolean = false
    private var transaction: TransactionModel? = null
    private var budgetModel: BudgetModel? = null
    private var typeLoan: TypeLoan? = null
    private var listBudget = mutableListOf<BudgetModel>()
    private var typeModel = TypeModel.EXPENSES

    override fun setUp() {
        setupLayout()
        clickListener()
    }

    @SuppressLint("SimpleDateFormat", "SetTextI18n", "DefaultLocale")
    private fun setupLayout() {
        isEdit = intent.getBooleanExtra(IS_EDIT, false)
        val transactionId = intent.getIntExtra(EDIT_TRANSACTION, -1)

        val date = SimpleDateFormat("MMMM, dd yyyy", Locale.ENGLISH)
        binding.tvDate.text = date.format(Date())

        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        binding.tvHour.text = String.format("%02d:%02d", hour, minute)

        if(transactionId != -1) {
            lifecycleScope.launch {
                createViewModel.getTransactionById(transactionId)
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    launch {
                        createViewModel.transactionById.collect {
                            when (it) {
                                is UiState.Loading -> {}
                                is UiState.Error -> {}
                                is UiState.Success -> {
                                    transaction = it.data
                                    transaction?.let { item ->
                                        createViewModel.getBudgetById(item.budgetInt)
                                        updateData(item)
                                    }
                                }
                            }
                        }
                    }

                    launch {
                        createViewModel.budgetById.collect {
                            when (it) {
                                is UiState.Loading -> {}
                                is UiState.Error -> {}
                                is UiState.Success -> {
                                    budgetModel = it.data
                                    budgetModel?.let { budget ->
                                        binding.tvChooseBudget.text = budget.name
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            createViewModel.getListBudget()
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                createViewModel.budget.collect {
                    when (it) {
                        is UiState.Loading -> {}
                        is UiState.Error -> {}
                        is UiState.Success -> {
                            listBudget = it.data
                        }
                    }
                }
            }
        }

        reloadData(TypeModel.EXPENSES)

        createViewModel.type.observe(this) {
            typeModel = it
            updateButton(it)
            reloadData(it)
        }

        categoryAdapter.callback = object : ICallBackItem {
            override fun callBack(ob: Any?, position: Int) {
                category = ob as CategoryModel
                when (category.name) {
                    getString(R.string.loan) -> {
                        typeLoan = TypeLoan.TYPE_LOAN
                        binding.rlLender.visible()
                        binding.rlBorrower.gone()
                    }
                    getString(R.string.borrow) -> {
                        typeLoan = TypeLoan.TYPE_BORROW
                        binding.rlLender.gone()
                        binding.rlBorrower.visible()
                    }
                }
            }
        }

        binding.rcvCategory.apply {
            adapter = categoryAdapter
            layoutManager = GridLayoutManager(context, 4)
        }

        customDateDialog.callback = object : ICallBackString {
            override fun callBack(ob: Any?, str: String) {
                binding.tvDate.text = str
            }
        }

        customTimeDialog.callback = object : ICallBackString {
            override fun callBack(ob: Any?, str: String) {
                binding.tvHour.text = str
            }
        }

        customChooseDialog.callback = object : ICallBackItem {
            override fun callBack(ob: Any?, position: Int) {
                if (ob is BudgetModel) {
                    budgetModel = ob
                    budgetModel?.let{
                        binding.tvChooseBudget.text = ob.name
                    }
                } else binding.tvChooseBudget.text = ""
            }
        }
    }

    private fun clickListener() {
        binding.tvExpenses.setOnUnDoubleClickListener {
            createViewModel.setType(TypeModel.EXPENSES)
        }

        binding.tvIncome.setOnUnDoubleClickListener {
            createViewModel.setType(TypeModel.INCOME)
        }

        binding.tvLoan.setOnUnDoubleClickListener {
            createViewModel.setType(TypeModel.LOAN)
            typeLoan = TypeLoan.TYPE_LOAN
        }

        binding.tvDate.setOnUnDoubleClickListener {
            if (!customDateDialog.isShowing)
                customDateDialog.show()
        }

        binding.tvHour.setOnUnDoubleClickListener {
            if (!customTimeDialog.isShowing)
                customTimeDialog.show()
        }

        binding.rlBudget.setOnUnDoubleClickListener {
            if (!customChooseDialog.isShowing) {
                customChooseDialog.showDialog(listBudget)
            }
        }

        binding.ivBack.setOnUnDoubleClickListener { finish() }

        binding.edtAmount.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                // Ẩn bàn phím
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.edtAmount.windowToken, 0)
                true
            } else {
                false
            }
        }

        binding.tvSave.setOnUnDoubleClickListener {
            val amount = binding.edtAmount.text.toString().toLongOrNull()
            val note = binding.edtNote.text.toString()
            val dateString = binding.tvDate.text.toString()
            val time = binding.tvHour.text.toString()
            val strLender = binding.edtLender.text.toString()
            val strBorrower = binding.edtBorrower.text.toString()

            if (amount == null || amount <= 0) {
                showToast(getString(R.string.str_please_fill), Gravity.CENTER)
                return@setOnUnDoubleClickListener
            }

            if (!isEdit) {
                transaction = TransactionModel(
                    category = category,
                    amount = amount,
                    note = note,
                    date = dateString,
                    time = time,
                    budgetInt = budgetModel?.id,
                    noteLender = strLender,
                    noteBorrower = strBorrower,
                    typeModel = typeModel,
                    typeLoan = typeLoan
                )

                transaction?.let { createViewModel.insertTransaction(it) }
                showToast(getString(R.string.save_succesfull), Gravity.CENTER)
            } else {
                transaction = TransactionModel(
                    id = transaction!!.id,
                    category = category,
                    amount = amount,
                    note = note,
                    date = dateString,
                    time = time,
                    budgetInt = budgetModel?.id,
                    noteLender = strLender,
                    noteBorrower = strBorrower,
                    typeModel = typeModel,
                    typeLoan = typeLoan
                )

                transaction?.let { createViewModel.updateTransaction(it) }
                showToast(getString(R.string.edit_succesfull), Gravity.CENTER)
            }
            finish()
        }
    }

    private fun updateData(transactionModel: TransactionModel?) {
        transactionModel?.let { item ->
            category = item.category
            typeLoan = item.typeLoan
            item.typeModel?.let {
                updateButton(it)
                showCategoryEdit(it, item.category.name)
                typeModel = it
            }

            binding.tvDate.text = item.date
            binding.tvHour.text = item.time

            binding.edtAmount.setText(item.amount.toString())
            binding.edtNote.setText(item.note)
            binding.edtLender.setText(item.noteLender)
            binding.edtBorrower.setText(item.noteBorrower)
        }
    }

    private fun updateButton(typeModel: TypeModel) {
        binding.tvExpenses.setBackgroundResource(R.drawable.bg_categories_disenable)
        binding.tvIncome.setBackgroundResource(R.drawable.bg_categories_disenable)
        binding.tvLoan.setBackgroundResource(R.drawable.bg_categories_disenable)

        binding.tvExpenses.setTextColor(ContextCompat.getColor(this@CreateTransactionActivity, R.color.color_001C41))
        binding.tvIncome.setTextColor(ContextCompat.getColor(this@CreateTransactionActivity, R.color.color_001C41))
        binding.tvLoan.setTextColor(ContextCompat.getColor(this@CreateTransactionActivity, R.color.color_001C41))

        when (typeModel) {
            TypeModel.EXPENSES -> {
                binding.tvExpenses.setBackgroundResource(R.drawable.bg_categories_enable)
                binding.tvExpenses.setTextColor(ContextCompat.getColor(this@CreateTransactionActivity, R.color.color_F4F9FF))
                binding.clLoan.gone()
                binding.rlBudget.visible()
            }

            TypeModel.INCOME -> {
                binding.tvIncome.setBackgroundResource(R.drawable.bg_categories_enable)
                binding.tvIncome.setTextColor(ContextCompat.getColor(this@CreateTransactionActivity, R.color.color_F4F9FF))
                binding.clLoan.gone()
                binding.rlBudget.gone()
            }

            TypeModel.LOAN -> {
                binding.tvLoan.setBackgroundResource(R.drawable.bg_categories_enable)
                binding.tvLoan.setTextColor(ContextCompat.getColor(this@CreateTransactionActivity, R.color.color_F4F9FF))
                binding.clLoan.visible()
                binding.rlBudget.gone()
            }
        }
    }

    private fun reloadData(typeModel: TypeModel) {
        when (typeModel) {
            TypeModel.EXPENSES -> {
                category = CategoryModel(R.drawable.ic_food, getString(R.string.food), R.color.color_food)
                categoryAdapter.setData(DataHelper.getDataExpenses(this))
            }

            TypeModel.INCOME -> {
                category = CategoryModel(R.drawable.ic_salary, getString(R.string.salary), R.color.color_salary)
                categoryAdapter.setData(DataHelper.getDataIncome(this))
            }

            TypeModel.LOAN -> {
                category = CategoryModel(R.drawable.ic_loan, getString(R.string.loan), R.color.color_loan)
                categoryAdapter.setData(DataHelper.getDataLoan(this))
            }
        }
    }

    private fun showCategoryEdit(typeModel: TypeModel, nameCategoryEdit: String) {
        val list = when (typeModel) {
            TypeModel.EXPENSES -> {
                DataHelper.getDataExpenses(this).onEach {
                    it.isSelected = it.name == nameCategoryEdit
                }
            }

            TypeModel.INCOME -> {
                DataHelper.getDataIncome(this).onEach {
                    it.isSelected = it.name == nameCategoryEdit
                }
            }

            TypeModel.LOAN-> {
                DataHelper.getDataLoan(this).onEach {
                    it.isSelected = it.name == nameCategoryEdit
                }
            }
        }
        categoryAdapter.setData(list)
    }
}