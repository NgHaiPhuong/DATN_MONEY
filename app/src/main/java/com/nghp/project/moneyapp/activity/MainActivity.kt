package com.nghp.project.moneyapp.activity

import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.nghp.project.moneyapp.R
import com.nghp.project.moneyapp.activity.base.BaseActivity
import com.nghp.project.moneyapp.activity.create.CreateTransactionActivity
import com.nghp.project.moneyapp.activity.budget.BudgetFragment
import com.nghp.project.moneyapp.activity.home.HomeFragment
import com.nghp.project.moneyapp.activity.setting.SettingFragment
import com.nghp.project.moneyapp.activity.statistics.StatisticsFragment
import com.nghp.project.moneyapp.adapter.ViewPagerAddFragmentAdapter
import com.nghp.project.moneyapp.callback.ICallBackCheck
import com.nghp.project.moneyapp.databinding.ActivityMainBinding
import com.nghp.project.moneyapp.extensions.setOnUnDoubleClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {

    override fun isHideNavigation(): Boolean = true

    private val homeFragment: HomeFragment by lazy { HomeFragment.newInstance() }
    private val budgetFragment: BudgetFragment by lazy { BudgetFragment.newInstance() }
    private val statisticsFragment: StatisticsFragment by lazy { StatisticsFragment.newInstance() }
    private val settingFragment: SettingFragment by lazy { SettingFragment.newInstance() }

    override fun setUp() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                when (binding.vpHome.currentItem) {
                    0 -> {
                        showDialogExit(object : ICallBackCheck {
                            override fun check(isCheck: Boolean) {

                            }
                        })
                    }

                    1, 2, 3 -> binding.vpHome.currentItem = 0
                }
            }
        })

        setUpLayout()
        evenClick()
    }

    private fun setUpLayout() {
        showLoading()
        Handler(Looper.getMainLooper()).postDelayed({ hideLoading() }, 1500)

        binding.vpHome.apply {
            adapter = ViewPagerAddFragmentAdapter(supportFragmentManager, lifecycle).apply {
                addFrag(homeFragment)
                addFrag(statisticsFragment)
                addFrag(budgetFragment)
                addFrag(settingFragment)
            }
            offscreenPageLimit = 4
            isUserInputEnabled = false

            registerOnPageChangeCallback(object : OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    setupBottomView(position)
                }
            })
        }
    }

    private fun evenClick() {
        binding.rlHome.setOnUnDoubleClickListener {
            binding.vpHome.setCurrentItem(0, true)
        }

        binding.rlStatistics.setOnUnDoubleClickListener {
            binding.vpHome.setCurrentItem(1, true)
        }

        binding.rlBudget.setOnUnDoubleClickListener {
            binding.vpHome.setCurrentItem(2, true)
        }

        binding.rlSettings.setOnUnDoubleClickListener {
            binding.vpHome.setCurrentItem(3, true)
        }

        binding.ivCreate.setOnUnDoubleClickListener {
            startIntent(Intent(this@MainActivity, CreateTransactionActivity::class.java), false)
        }
    }

    private fun setupBottomView(position: Int) {
        binding.ivHome.setImageResource(R.drawable.ic_home)
        binding.tvHome.setTextColor(ContextCompat.getColor(this, R.color.color_BFBFBF))

        binding.ivStatistics.setImageResource(R.drawable.ic_statistics)
        binding.tvStatistics.setTextColor(ContextCompat.getColor(this, R.color.color_BFBFBF))

        binding.ivBudget.setImageResource(R.drawable.ic_budget)
        binding.tvBudget.setTextColor(ContextCompat.getColor(this, R.color.color_BFBFBF))

        binding.ivSettings.setImageResource(R.drawable.ic_settings)
        binding.tvSettings.setTextColor(ContextCompat.getColor(this, R.color.color_BFBFBF))

        when (position) {
            0 -> {
                binding.ivHome.setImageResource(R.drawable.ic_home_enable)
                binding.tvHome.setTextColor(ContextCompat.getColor(this, R.color.main_color))
            }

            1 -> {
                binding.ivStatistics.setImageResource(R.drawable.ic_statistics_enable)
                binding.tvStatistics.setTextColor(ContextCompat.getColor(this, R.color.main_color))
            }

            2 -> {
                binding.ivBudget.setImageResource(R.drawable.ic_budget_enable)
                binding.tvBudget.setTextColor(ContextCompat.getColor(this, R.color.main_color))
            }

            3 -> {
                binding.ivSettings.setImageResource(R.drawable.ic_settings_enable)
                binding.tvSettings.setTextColor(ContextCompat.getColor(this, R.color.main_color))
            }
        }
    }
}