package com.nmh.base.project.activity.choose

import android.content.Intent
import androidx.recyclerview.widget.LinearLayoutManager
import com.nmh.base.project.R
import com.nmh.base.project.activity.MainActivity
import com.nmh.base.project.activity.base.BaseActivity
import com.nmh.base.project.activity.setbudget.SetBudgetActivity
import com.nmh.base.project.callback.ICallBackItem
import com.nmh.base.project.databinding.ActivityChooseCurrencyBinding
import com.nmh.base.project.db.model.CurrencyModel
import com.nmh.base.project.extensions.setOnUnDoubleClickListener
import com.nmh.base.project.helpers.CHOOSE_CURRENCY
import com.nmh.base.project.helpers.SET_START_BUDGET
import com.nmh.base.project.helpers.SYMBOL_CURRENCY
import com.nmh.base.project.sharepref.DataLocalManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ChooseCurrencyActivity :
    BaseActivity<ActivityChooseCurrencyBinding>(ActivityChooseCurrencyBinding::inflate) {

    @Inject
    lateinit var currencyAdapter: CurrencyAdapter

    override fun setUp() {
        currencyAdapter.setData(DataCurrency.getDataCurrency())
        currencyAdapter.callBack = object : ICallBackItem {
            override fun callBack(ob: Any?, position: Int) {
                if (ob is CurrencyModel) {
                    binding.ivCheck.setImageResource(R.drawable.ic_tick_green)
                    DataLocalManager.setOption(ob.symbol, SYMBOL_CURRENCY)
                }
            }
        }

        binding.rcvCurrencies.apply {
            adapter = currencyAdapter
            layoutManager = LinearLayoutManager(this@ChooseCurrencyActivity)
        }

        binding.ivCheck.setOnUnDoubleClickListener {
            DataLocalManager.setBoolean(CHOOSE_CURRENCY, true)
            if(!DataLocalManager.getBoolean(SET_START_BUDGET, false)){
                startIntent(Intent(this@ChooseCurrencyActivity, SetBudgetActivity::class.java), false)
            } else startIntent(Intent(this@ChooseCurrencyActivity, MainActivity::class.java), false)
        }
    }
}