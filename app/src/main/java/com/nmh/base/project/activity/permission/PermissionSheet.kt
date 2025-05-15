package com.nmh.base.project.activity.permission

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import com.nlbn.ads.callback.NativeCallback
import com.nlbn.ads.util.Admob
import com.nlbn.ads.util.AppOpenManager
import com.nlbn.ads.util.ConsentHelper
import com.nmh.base.project.NMHApp
import com.nmh.base.project.R
import com.nmh.base.project.callback.ICallBackCheck
import com.nmh.base.project.databinding.AdsNativeBotHorizontalMediaLeftBinding
import com.nmh.base.project.databinding.BottomSheetDialogPermissionBinding
import com.nmh.base.project.extensions.checkAllPerGrand
import com.nmh.base.project.extensions.checkPer
import com.nmh.base.project.extensions.gone
import com.nmh.base.project.extensions.openSettingPermission
import com.nmh.base.project.extensions.setOnUnDoubleClickListener
import com.nmh.base.project.extensions.showToast
import com.nmh.base.project.extensions.visible
import com.nmh.base.project.utils.AdsConfig
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

class PermissionSheet @Inject constructor(@ActivityContext private val context: Context): BottomSheetDialog(context, R.style.SheetDialog) {

    private var binding = BottomSheetDialogPermissionBinding.inflate(LayoutInflater.from(context))

    var isDone: ICallBackCheck? = null
    var isDismiss: ICallBackCheck? = null

    private val notificationPer = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        arrayOf(Manifest.permission.POST_NOTIFICATIONS)
    else arrayOf("")

    private val storagePer = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        arrayOf(Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO, Manifest.permission.READ_MEDIA_AUDIO)
    else arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)

    private var nativeAds: NativeAd? = null

    init {
        setContentView(binding.root)
        setCancelable(true)

        binding.root.layoutParams.width = (NMHApp.w * 100).toInt()

        //show full size dialog bottom sheet

        initView()
        evenClick()

        setOnCancelListener {
            if (context.checkAllPerGrand()) isDone?.check(true)
            isDismiss?.check(true)
        }
    }

    fun showDialog() {
        show()
        window?.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        window?.navigationBarColor = Color.WHITE
        window?.statusBarColor = Color.TRANSPARENT
        window?.decorView?.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)

        checkPer()
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {
        binding.sc.apply {
            setCheck(true)
            isEnabled = false
        }

        loadNative()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun evenClick() {
        binding.tvNotify.setOnUnDoubleClickListener {
            if (binding.tvNotify.currentTextColor == Color.BLACK)
                return@setOnUnDoubleClickListener

            if (!context.checkPer(notificationPer)) requestNotificationPermission()
        }
        binding.tvRecord.setOnUnDoubleClickListener {
            if (binding.tvRecord.currentTextColor == Color.BLACK)
                return@setOnUnDoubleClickListener

            if (!context.checkPer(arrayOf(Manifest.permission.RECORD_AUDIO))) requestRecordPermission()
        }
        binding.tvCamera.setOnUnDoubleClickListener {
            if (binding.tvCamera.currentTextColor == Color.BLACK)
                return@setOnUnDoubleClickListener

            if (!context.checkPer(arrayOf(Manifest.permission.CAMERA))) requestCameraPermission()
        }
        binding.tvDraw.setOnUnDoubleClickListener {
            if (binding.tvDraw.currentTextColor == Color.BLACK)
                return@setOnUnDoubleClickListener

            if (!Settings.canDrawOverlays(context)) requestDrawOverlayPermission()
        }
        binding.tvStorage.setOnUnDoubleClickListener {
            if (binding.tvStorage.currentTextColor == Color.BLACK)
                return@setOnUnDoubleClickListener

            if (!context.checkPer(storagePer)) requestStoragePermission()
        }
        binding.rl.setOnUnDoubleClickListener {
            context.showToast(context.getString(R.string.click_to_step_by_step_for_require_permission), Gravity.CENTER)
        }
        binding.ivExit.setOnUnDoubleClickListener { cancel() }
    }

    @SuppressLint("SetTextI18n")
    fun checkPer(): Boolean {
        binding.tvNotify.apply {
            setBackgroundResource(R.drawable.bg_button_disable)
            setTextColor(Color.BLACK)

            clearAnimation()
            elevation = 0f
        }
        binding.tvRecord.apply {
            setBackgroundResource(R.drawable.bg_button_disable)
            setTextColor(Color.BLACK)

            clearAnimation()
            elevation = 0f
        }
        binding.tvCamera.apply {
            setBackgroundResource(R.drawable.bg_button_disable)
            setTextColor(Color.BLACK)

            clearAnimation()
            elevation = 0f
        }
        binding.tvDraw.apply {
            setBackgroundResource(R.drawable.bg_button_disable)
            setTextColor(Color.BLACK)

            clearAnimation()
            elevation = 0f
        }
        binding.tvStorage.apply {
            setBackgroundResource(R.drawable.bg_button_disable)
            setTextColor(Color.BLACK)

            clearAnimation()
            elevation = 0f
        }

        if (!context.checkPer(notificationPer)) {
            binding.tvPer.text = "${context.getString(R.string.you_can_select)} ${context.getString(R.string.str_notify)}"

            binding.tvNotify.apply {
                setBackgroundResource(R.drawable.bg_button_enable)
                setTextColor(Color.WHITE)

                startAnimationButton(this)
                elevation = 1f
            }
        } else if (!context.checkPer(arrayOf(Manifest.permission.RECORD_AUDIO))) {
            binding.tvPer.text = "${context.getString(R.string.you_can_select)} ${context.getString(R.string.str_record)}"

            binding.tvRecord.apply {
                setBackgroundResource(R.drawable.bg_button_enable)
                setTextColor(Color.WHITE)

                startAnimationButton(this)
                elevation = 1f
            }
        } else if (!context.checkPer(arrayOf(Manifest.permission.CAMERA))) {
            binding.tvPer.text = "${context.getString(R.string.you_can_select)} ${context.getString(R.string.str_camera)}"

            binding.tvCamera.apply {
                setBackgroundResource(R.drawable.bg_button_enable)
                setTextColor(Color.WHITE)

                startAnimationButton(this)
                elevation = 1f
            }
        } else if (!Settings.canDrawOverlays(context)) {
            binding.tvPer.text = "${context.getString(R.string.you_can_select)} ${context.getString(R.string.str_draw)}"

            binding.tvDraw.apply {
                setBackgroundResource(R.drawable.bg_button_enable)
                setTextColor(Color.WHITE)

                startAnimationButton(this)
                elevation = 1f
            }
        } else if (!context.checkPer(storagePer)) {
            binding.tvPer.text = "${context.getString(R.string.you_can_select)} ${context.getString(R.string.str_storage)}"

            binding.tvStorage.apply {
                setBackgroundResource(R.drawable.bg_button_enable)
                setTextColor(Color.WHITE)

                startAnimationButton(this)
                elevation = 1f
            }
        } else {
            cancel()
            return true
        }

        return false
    }

    private fun startAnimationButton(view: View) {
        Handler(Looper.getMainLooper()).postDelayed({
            view.startAnimation(AnimationUtils.loadAnimation(context, R.anim.heart_beat))
        }, 2000)
    }

    private fun requestDrawOverlayPermission() {
        AppOpenManager.getInstance().disableAppResumeWithActivity(context::class.java)
        context.startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).apply {
            data = Uri.parse("package:${context.packageName}")
        })
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            Dexter.withContext(context)
                .withPermissions(Manifest.permission.POST_NOTIFICATIONS)
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(p0: MultiplePermissionsReport) {
                        if (p0.isAnyPermissionPermanentlyDenied) {
                            AppOpenManager.getInstance().disableAppResumeWithActivity(context::class.java)
                            context.openSettingPermission(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        p0: MutableList<PermissionRequest>,
                        p1: PermissionToken
                    ) {
                        p1.continuePermissionRequest()
                    }

                }).check()
    }

    private fun requestRecordPermission() {
        Dexter.withContext(context)
            .withPermission(Manifest.permission.RECORD_AUDIO)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(p0: PermissionGrantedResponse) {
                    checkPer()
                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse) {
                    checkPer()
                    if (p0.isPermanentlyDenied) {
                        AppOpenManager.getInstance().disableAppResumeWithActivity(context::class.java)
                        context.openSettingPermission(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: PermissionRequest,
                    p1: PermissionToken
                ) {
                    p1.continuePermissionRequest()
                }
            }).check()
    }

    private fun requestCameraPermission() {
        Dexter.withContext(context)
            .withPermission(Manifest.permission.CAMERA)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(p0: PermissionGrantedResponse) {
                    checkPer()
                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse) {
                    checkPer()
                    if (p0.isPermanentlyDenied) {
                        AppOpenManager.getInstance().disableAppResumeWithActivity(context::class.java)
                        context.openSettingPermission(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: PermissionRequest,
                    p1: PermissionToken
                ) {
                    p1.continuePermissionRequest()
                }
            }).check()
    }

    private fun requestStoragePermission() {
        Dexter.withContext(context)
            .withPermissions(*storagePer)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(p0: MultiplePermissionsReport) {
                    if (p0.isAnyPermissionPermanentlyDenied) {
                        AppOpenManager.getInstance().disableAppResumeWithActivity(context::class.java)
                        context.openSettingPermission(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<PermissionRequest>,
                    p1: PermissionToken
                ) {
                    p1.continuePermissionRequest()
                }

            }).check()
    }

    fun checkPerDialog(): Boolean = context.checkAllPerGrand()

    fun loadNative() {
        try {
            if (AdsConfig.haveNetworkConnection(context) /*thêm điều kiện remote*/
                && ConsentHelper.getInstance(context).canRequestAds()) {
                binding.rlNative.visible()
                nativeAds?.let {
                    pushViewAds(it)
                } ?: run {
                    Admob.getInstance().loadNativeAd(
                        context,
                        context.getString(R.string.native_permission_in_app),
                        object : NativeCallback() {
                            override fun onNativeAdLoaded(nativeAd: NativeAd) {
                                nativeAds = nativeAd
                                pushViewAds(nativeAd)
                            }

                            override fun onAdFailedToLoad() {
                                binding.frNativeAds.removeAllViews()
                            }

                            override fun onAdImpression() {
                                super.onAdImpression()
                                nativeAds = null
                            }
                        }
                    )
                }
            } else binding.rlNative.gone()
        } catch (e: Exception) {
            binding.rlNative.gone()
            e.printStackTrace()
        }
    }

    private fun pushViewAds(nativeAd: NativeAd) {
        val adView = AdsNativeBotHorizontalMediaLeftBinding.inflate(layoutInflater)

        if (AdsConfig.isLoadFullAds())
            adView.adUnitContent.setBackgroundResource(R.drawable.bg_native_no_stroke)
        else adView.adUnitContent.setBackgroundResource(R.drawable.bg_native)

        binding.rlNative.visible()
        binding.frNativeAds.removeAllViews()
        binding.frNativeAds.addView(adView.root)
        Admob.getInstance().pushAdsToViewCustom(nativeAd, adView.root)
    }
}