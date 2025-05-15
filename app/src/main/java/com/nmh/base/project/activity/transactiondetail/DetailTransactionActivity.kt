package com.nmh.base.project.activity.transactiondetail

import android.annotation.SuppressLint
import android.content.Intent
import android.view.Gravity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.nmh.base.project.R
import com.nmh.base.project.activity.base.BaseActivity
import com.nmh.base.project.activity.base.UiState
import com.nmh.base.project.activity.create.CreateTransactionActivity
import com.nmh.base.project.databinding.ActivityDetailTransactionBinding
import com.nmh.base.project.db.model.TransactionModel
import com.nmh.base.project.extensions.setOnUnDoubleClickListener
import com.nmh.base.project.extensions.showToast
import com.nmh.base.project.helpers.EDIT_TRANSACTION
import com.nmh.base.project.helpers.IS_EDIT
import com.nmh.base.project.helpers.TRANSACTION_DETAIL
import com.nmh.base.project.helpers.TypeLoan
import com.nmh.base.project.helpers.TypeModel
import com.nmh.base.project.utils.FormatNumber
import com.nmh.base.project.callback.ICallBackCheck
import com.nmh.base.project.customView.CustomDeleteDialog
import com.nmh.base.project.helpers.SYMBOL_CURRENCY
import com.nmh.base.project.sharepref.DataLocalManager
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