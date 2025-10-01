package com.nghp.project.moneyapp.activity.transactiondetail

import android.annotation.SuppressLint
import android.content.Intent
import android.view.Gravity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.nghp.project.moneyapp.R
import com.nghp.project.moneyapp.activity.base.BaseActivity
import com.nghp.project.moneyapp.activity.base.UiState
import com.nghp.project.moneyapp.activity.create.CreateTransactionActivity
import com.nghp.project.moneyapp.databinding.ActivityDetailTransactionBinding
import com.nghp.project.moneyapp.db.model.TransactionModel
import com.nghp.project.moneyapp.extensions.setOnUnDoubleClickListener
import com.nghp.project.moneyapp.extensions.showToast
import com.nghp.project.moneyapp.helpers.EDIT_TRANSACTION
import com.nghp.project.moneyapp.helpers.IS_EDIT
import com.nghp.project.moneyapp.helpers.TRANSACTION_DETAIL
import com.nghp.project.moneyapp.helpers.TypeLoan
import com.nghp.project.moneyapp.helpers.TypeModel
import com.nghp.project.moneyapp.utils.FormatNumber
import com.nghp.project.moneyapp.callback.ICallBackCheck
import com.nghp.project.moneyapp.customView.CustomDeleteDialog
import com.nghp.project.moneyapp.helpers.SYMBOL_CURRENCY
import com.nghp.project.moneyapp.sharepref.DataLocalManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class DetailTransactionActivity : BaseActivity<ActivityDetailTransactionBinding>(ActivityDetailTransactionBinding::inflate) {

    @Inject
    lateinit var detailViewModel: DetailTransactionViewModel

    @Inject
    lateinit var deleteDialog: CustomDeleteDialog

    @Inject
    lateinit var detailTransactionViewModel: DetailTransactionViewModel

    private var transactionModel: TransactionModel? = null
    private var currency : String = ""

    override fun setUp() {
        setupLayout()
        evenClick()
    }

    private fun setupLayout() {
        currency = DataLocalManager.getOption(SYMBOL_CURRENCY).toString()
        val id = intent.getIntExtra(TRANSACTION_DETAIL, 0)

        lifecycleScope.launch {
            detailTransactionViewModel.getTransactionById(id)
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                detailTransactionViewModel.transactionById.collect {
                    when (it) {
                        is UiState.Loading -> {}
                        is UiState.Error -> {}
                        is UiState.Success -> {
                            updateData(it.data)
                            transactionModel = it.data
                        }
                    }
                }
            }
        }

        deleteDialog.callback = object : ICallBackCheck {
            override fun check(isCheck: Boolean) {
                if (isCheck) {
                    transactionModel?.let {
                        detailViewModel.deleteTransaction(it)
                        showToast(getString(R.string.delete_succesfull), Gravity.CENTER)
                        finish()
                    } ?: run {
                        showToast(getString(R.string.delete_fail), Gravity.CENTER)
                    }
                }
            }
        }
    }

    private fun evenClick() {
        binding.tvBack.setOnUnDoubleClickListener { onBackPressedDispatcher.onBackPressed() }

        binding.tvEdit.setOnUnDoubleClickListener {
            startIntent(Intent(this@DetailTransactionActivity, CreateTransactionActivity::class.java).apply {
                putExtra(EDIT_TRANSACTION, transactionModel?.id)
                putExtra(IS_EDIT, true)
            }, false)
        }

        binding.tvDelete.setOnUnDoubleClickListener {
            if(!deleteDialog.isShowing) deleteDialog.show()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateData(item: TransactionModel?) {
        item?.let {
            binding.tvDate.text = item.date

            val strAmount = FormatNumber.convertNumberToNumberDotString(item.amount)

            when(item.typeModel) {
                TypeModel.EXPENSES -> {
                    binding.tvAmount.text = "-$currency$strAmount"
                    binding.tvAmount.setTextColor(ContextCompat.getColor(this, R.color.color_text_red))
                }

                TypeModel.INCOME-> {
                    binding.tvAmount.text = "$currency$strAmount"
                    binding.tvAmount.setTextColor(ContextCompat.getColor(this, R.color.color_text_green))
                }

                else -> Unit
            }

            when(item.typeLoan) {
                TypeLoan.TYPE_BORROW -> {
                    binding.tvAmount.text = "-$currency$strAmount"
                    binding.tvAmount.setTextColor(ContextCompat.getColor(this, R.color.color_text_red))
                }

                TypeLoan.TYPE_LOAN -> {
                    binding.tvAmount.text = "$currency$strAmount"
                    binding.tvAmount.setTextColor(ContextCompat.getColor(this, R.color.color_text_green))
                }

                else -> Unit
            }

            Glide.with(this)
                .load(item.category.img)
                .into(binding.imvCategory)

            binding.tvNameCategory.text = item.category.name
            binding.tvDesNote.text = item.note
        }
    }
}