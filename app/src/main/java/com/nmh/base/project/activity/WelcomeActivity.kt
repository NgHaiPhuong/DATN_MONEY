package com.nmh.base.project.activity

import com.nmh.base.project.activity.base.BaseActivity
import com.nmh.base.project.databinding.ActivityWelcomeBinding
import com.nmh.base.project.extensions.setOnUnDoubleClickListener
import com.nmh.base.project.utils.AdsConfig

class WelcomeActivity: BaseActivity<ActivityWelcomeBinding>(ActivityWelcomeBinding::inflate) {

    override fun isHideNavigation(): Boolean = true

    override fun setUp() {
        //nếu app không có màn welcome, có thể load trước ở các màn trước đó gần với màn home(trừ màn permission)
        if (AdsConfig.nativeHome == null /*thêm điều kiện remote(nếu có)*/)
            AdsConfig.loadNativeHome(this@WelcomeActivity)

        binding.tvGetStart.setOnUnDoubleClickListener {
            startIntent(MainActivity::class.java.name, true)
        }
    }
}