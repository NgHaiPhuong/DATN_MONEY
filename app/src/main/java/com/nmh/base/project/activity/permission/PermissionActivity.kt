package com.nmh.base.project.activity.permission

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import com.google.android.gms.ads.nativead.NativeAd
import com.nlbn.ads.callback.NativeCallback
import com.nlbn.ads.util.Admob
import com.nlbn.ads.util.AppOpenManager
import com.nlbn.ads.util.ConsentHelper
import com.nmh.base.project.R
import com.nmh.base.project.activity.WelcomeActivity
import com.nmh.base.project.activity.base.BaseActivity
import com.nmh.base_lib.callback.StatusResultSwitch
import com.nmh.base.project.databinding.ActivityPermissionBinding
import com.nmh.base.project.databinding.AdsNativeTopFullAdsBinding
import com.nmh.base.project.extensions.gone
import com.nmh.base.project.extensions.openSettingPermission
import com.nmh.base.project.extensions.setOnUnDoubleClickListener
import com.nmh.base.project.extensions.visible
import com.nmh.base.project.helpers.FIRST_INSTALL
import com.nmh.base.project.sharepref.DataLocalManager
import com.nmh.base.project.utils.AdsConfig

class PermissionActivity : BaseActivity<ActivityPermissionBinding>(ActivityPermissionBinding::inflate) {

    override fun isHideNavigation(): Boolean = true

    val storagePer = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        arrayOf(
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.READ_MEDIA_AUDIO
        ) /*tương ứng với tên mỗi quyền (không phải lúc nào cx xin đủ cả 3 quyền)*/
    else arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)

    private var type = ""
    private var countStorage = 0
    private var countCamera = 0
    private var countRecord = 0

    override fun setUp() {
        setUpLayout()
        evenClick()
    }

    override fun onResume() {
        super.onResume()
        AppOpenManager.getInstance().enableAppResumeWithActivity(PermissionActivity::class.java)

        val storagePer = checkPer(storagePer)
        val overlayPer = Settings.canDrawOverlays(this)
        val cameraPer = checkPer(arrayOf(Manifest.permission.CAMERA))
        val recordPer = checkPer(arrayOf(Manifest.permission.RECORD_AUDIO))

        if (storagePer || overlayPer || cameraPer || recordPer)
            binding.tvGo.text = getString(R.string.str_continue)
        else binding.tvGo.text = getString(R.string.skip)

        binding.scCamera.setCheck(cameraPer)
        binding.scRecord.setCheck(recordPer)
        binding.scStorage.setCheck(storagePer)
        binding.scDraw.setCheck(overlayPer)
    }

    private fun evenClick() {
        binding.scRecord.onResult = object : StatusResultSwitch {
            override fun onResult(isChecked: Boolean) {
                if (checkPer(arrayOf(Manifest.permission.RECORD_AUDIO))) {
                    binding.scRecord.setCheck(true)
                    return
                }
                if (isChecked) {
                    AppOpenManager.getInstance().disableAppResumeWithActivity(PermissionActivity::class.java)
                    type = "record"
                    checkPermission.launch(arrayOf(Manifest.permission.RECORD_AUDIO))
                }
            }
        }
        binding.scCamera.onResult = object : StatusResultSwitch {
            override fun onResult(isChecked: Boolean) {
                if (checkPer(arrayOf(Manifest.permission.CAMERA))) {
                    binding.scCamera.setCheck(true)
                    return
                }
                if (isChecked) {
                    AppOpenManager.getInstance().disableAppResumeWithActivity(PermissionActivity::class.java)
                    type = "camera"
                    checkPermission.launch(arrayOf(Manifest.permission.CAMERA))
                }
            }
        }
        binding.scStorage.onResult = object : StatusResultSwitch {
            override fun onResult(isChecked: Boolean) {
                if (checkPer(storagePer)) {
                    binding.scStorage.setCheck(true)
                    return
                }
                if (isChecked) {
                    AppOpenManager.getInstance().disableAppResumeWithActivity(PermissionActivity::class.java)
                    type = "storage"
                    checkPermission.launch(storagePer)
                }
            }
        }
        binding.scDraw.onResult = object : StatusResultSwitch {
            override fun onResult(isChecked: Boolean) {
                if (Settings.canDrawOverlays(this@PermissionActivity)) {
                    binding.scDraw.setCheck(true)
                    return
                }
                if (isChecked) {
                    AppOpenManager.getInstance().disableAppResumeWithActivity(PermissionActivity::class.java)
                    type = "overlay"
                    requestPermission.launch(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).apply {
                        data = Uri.parse("package:$packageName")
                    })
                }
            }
        }
        binding.tvGo.setOnUnDoubleClickListener {
            DataLocalManager.setBoolean(FIRST_INSTALL, false)
            startIntent(WelcomeActivity::class.java.name, true)
        }
    }

    private fun setUpLayout() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) binding.rlNotify.visible()
        else binding.rlNotify.gone()

        if (true /*chỗ này check key remote*/) {
            if (AdsConfig.isLoadFullAds())
                loadNative(getString(R.string.native_permission))
            else binding.layoutNative.gone()
        } else binding.layoutNative.gone()
    }

    private var requestPermission =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            when(type) {
                "overlay" -> {
                    if (true /*thêm key remote*/)
                        loadNative(getString(R.string.native_permission_overlay))
                    else binding.layoutNative.gone()
                }
            }
        }


    private var checkPermission =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            when(type) {
                "record" -> {
                    countRecord++
                    if (countRecord >= 3) {
                        AppOpenManager.getInstance().disableAppResumeWithActivity(PermissionActivity::class.java)
                        openSettingPermission(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    }

                    if (true /*thêm key remote*/)
                        loadNative(getString(R.string.native_permission_record))
                    else binding.layoutNative.gone()
                }
                "camera" -> {
                    countCamera++
                    if (countCamera >= 3) {
                        AppOpenManager.getInstance().disableAppResumeWithActivity(PermissionActivity::class.java)
                        openSettingPermission(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    }

                    if (true /*thêm key remote*/)
                        loadNative(getString(R.string.native_permission_camera))
                    else binding.layoutNative.gone()
                }
                "storage" -> {
                    countStorage++
                    if (countStorage >= 3) {
                        AppOpenManager.getInstance().disableAppResumeWithActivity(PermissionActivity::class.java)
                        openSettingPermission(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    }

                    if (true /*thêm key remote*/)
                        loadNative(getString(R.string.native_permission_storage))
                    else binding.layoutNative.gone()
                }
            }
        }

    private fun loadNative(strId: String) {
        if(haveNetworkConnection() && AdsConfig.isLoadFullAds()
            && ConsentHelper.getInstance(this).canRequestAds()) {
            binding.layoutNative.visible()
            AdsConfig.nativePermission?.let {
                pushViewNative(it)
            } ?: run {
                Admob.getInstance().loadNativeAd(this@PermissionActivity, strId, object : NativeCallback() {
                    override fun onNativeAdLoaded(nativeAd: NativeAd) {
                        super.onNativeAdLoaded(nativeAd)
                        pushViewNative(nativeAd)
                    }

                    override fun onAdFailedToLoad() {
                        super.onAdFailedToLoad()
                        binding.frNativeAds.removeAllViews()
                    }
                })
            }
        } else binding.layoutNative.gone()
    }

    private fun pushViewNative(nativeAd: NativeAd) {
        val adView = AdsNativeTopFullAdsBinding.inflate(layoutInflater)
        binding.layoutNative.visible()
        binding.frNativeAds.removeAllViews()
        binding.frNativeAds.addView(adView.root)
        Admob.getInstance().pushAdsToViewCustom(nativeAd, adView.root)
    }
}