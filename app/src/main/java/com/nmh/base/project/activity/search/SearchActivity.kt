package com.nmh.base.project.activity.search

import android.content.Intent
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.nmh.base.project.activity.base.BaseActivity
import com.nmh.base.project.activity.base.UiState
import com.nmh.base.project.activity.transactiondetail.DetailTransactionActivity
import com.nmh.base.project.callback.ICallBackItem
import com.nmh.base.project.databinding.ActivitySearchBinding
import com.nmh.base.project.db.model.TransactionModel
import com.nmh.base.project.extensions.gone
import com.nmh.base.project.extensions.setOnUnDoubleClickListener
import com.nmh.base.project.extensions.visible
import com.nmh.base.project.helpers.TRANSACTION_DETAIL
import com.nmh.base.project.helpers.TYPE_MODEL
import com.nmh.base.project.helpers.TypeModel
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