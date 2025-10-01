package com.nghp.project.moneyapp.activity.search

import android.content.Intent
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.nghp.project.moneyapp.activity.base.BaseActivity
import com.nghp.project.moneyapp.activity.base.UiState
import com.nghp.project.moneyapp.activity.transactiondetail.DetailTransactionActivity
import com.nghp.project.moneyapp.callback.ICallBackItem
import com.nghp.project.moneyapp.databinding.ActivitySearchBinding
import com.nghp.project.moneyapp.db.model.TransactionModel
import com.nghp.project.moneyapp.extensions.gone
import com.nghp.project.moneyapp.extensions.setOnUnDoubleClickListener
import com.nghp.project.moneyapp.extensions.visible
import com.nghp.project.moneyapp.helpers.TRANSACTION_DETAIL
import com.nghp.project.moneyapp.helpers.TYPE_MODEL
import com.nghp.project.moneyapp.helpers.TypeModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SearchActivity : BaseActivity<ActivitySearchBinding>(ActivitySearchBinding::inflate) {

    @Inject
    lateinit var searchAdapter: SearchAdapter

    @Inject
    lateinit var searchViewModel: SearchViewModel

    private var typeModel = TypeModel.EXPENSES

    override fun setUp() {
        setupLayout()
        evenClick()
    }

    private fun setupLayout() {
        typeModel = intent.getStringExtra(TYPE_MODEL)?.let {
            TypeModel.valueOf(it)
        } ?: TypeModel.EXPENSES

        lifecycleScope.launch {
            searchViewModel.getTransactionByType(typeModel)
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                searchViewModel.transactionByType.collect {
                    when (it) {
                        is UiState.Loading -> {}
                        is UiState.Error -> {}
                        is UiState.Success -> {
                            if (it.data.isEmpty()) {
                                binding.rcvSearch.gone()
                                binding.rlEmpty.visible()
                            } else {
                                binding.rcvSearch.visible()
                                binding.rlEmpty.gone()

                                searchAdapter.setData(it.data)
                            }
                        }
                    }
                }
            }
        }

        searchAdapter.callback = object : ICallBackItem {
            override fun callBack(ob: Any?, position: Int) {
                if (ob is TransactionModel) {
                    startIntent(Intent(this@SearchActivity, DetailTransactionActivity::class.java).apply {
                        putExtra(TRANSACTION_DETAIL, ob.id)
                    }, false)
                }
            }
        }

        binding.rcvSearch.apply {
            adapter = searchAdapter
            layoutManager = LinearLayoutManager(this@SearchActivity)
        }
    }

    private fun evenClick() {
        binding.ivBack.setOnUnDoubleClickListener { finish() }

        binding.edtSearch.addTextChangedListener {
            searchAdapter.filter(it.toString())
        }
    }
}