package com.nghp.project.moneyapp.activity.choose

import android.content.Intent
import androidx.recyclerview.widget.LinearLayoutManager
import com.nghp.project.moneyapp.R
import com.nghp.project.moneyapp.activity.MainActivity
import com.nghp.project.moneyapp.activity.base.BaseActivity
import com.nghp.project.moneyapp.activity.setbudget.SetBudgetActivity
import com.nghp.project.moneyapp.callback.ICallBackItem
import com.nghp.project.moneyapp.databinding.ActivityChooseCurrencyBinding
import com.nghp.project.moneyapp.db.model.CurrencyModel
import com.nghp.project.moneyapp.extensions.setOnUnDoubleClickListener
import com.nghp.project.moneyapp.helpers.CHOOSE_CURRENCY
import com.nghp.project.moneyapp.helpers.SET_START_BUDGET
import com.nghp.project.moneyapp.helpers.SYMBOL_CURRENCY
import com.nghp.project.moneyapp.sharepref.DataLocalManager
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