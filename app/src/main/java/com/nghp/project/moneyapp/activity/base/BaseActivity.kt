package com.nghp.project.moneyapp.activity.base

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.google.firebase.remoteconfig.remoteConfig
import com.nghp.project.moneyapp.R
import com.nghp.project.moneyapp.callback.ICallBackCheck
import com.nghp.project.moneyapp.databinding.DialogBackToHomeBinding
import com.nghp.project.moneyapp.databinding.DialogExitAppBinding
import com.nghp.project.moneyapp.databinding.DialogLoadingBinding
import com.nghp.project.moneyapp.databinding.DialogUpdateAppBinding
import com.nghp.project.moneyapp.extensions.changeLanguage
import com.nghp.project.moneyapp.extensions.createBackground
import com.nghp.project.moneyapp.extensions.setAnimExit
import com.nghp.project.moneyapp.extensions.setOnUnDoubleClickListener
import com.nghp.project.moneyapp.extensions.setStatusBarTransparent
import com.nghp.project.moneyapp.extensions.setUpDialog
import com.nghp.project.moneyapp.helpers.CURRENT_LANGUAGE
import com.nghp.project.moneyapp.helpers.TAG
import com.nghp.project.moneyapp.sharepref.DataLocalManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import java.util.Timer
import java.util.TimerTask
import kotlin.coroutines.CoroutineContext

abstract class BaseActivity<B : ViewBinding>(
    val bindingFactory: (LayoutInflater) -> B
) : AppCompatActivity(), BaseView, CoroutineScope {

    lateinit var job: Job
    var w = 0F
    override val coroutineContext: CoroutineContext
        get() = getDispatchers() + job

    val binding: B by lazy { bindingFactory(layoutInflater) }
    private lateinit var decorView: View

    private var finish = 0
    private var mIsShowLoading = false
    private var isShowNetwork = false
    private var isShowDialogBack = false

    private var loadingDialog: AlertDialog? = null
    private var networkDialog: AlertDialog? = null

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        decorView = window.decorView
        if (!isHideNavigation())
            setStatusBarTransparent(
                this@BaseActivity,
                isVisible(),
                getColorState()[0],
                getColorState()[1]
            )
        else {
            window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
            window.navigationBarColor = Color.parseColor("#01ffffff")
            window.statusBarColor = Color.TRANSPARENT
            decorView.systemUiVisibility = hideSystemBars()
        }

        job = Job()
        setContentView(binding.root)
        w = resources.displayMetrics.widthPixels / 100F
        setUp()
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    override fun onResume() {
        super.onResume()

        // get remote config
        val remoteConfig = Firebase.remoteConfig
        remoteConfig.setConfigSettingsAsync(remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
        })
        var remoteUpdate = false
        remoteConfig.fetchAndActivate().addOnCompleteListener(this) { task ->
            if (task.isSuccessful) remoteUpdate = remoteConfig.getBoolean("force_update")
        }

        if (remoteUpdate) checkUpdate()
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(
            changeLanguage(
                newBase,
                DataLocalManager.getLanguage(CURRENT_LANGUAGE)
            )
        )
    }

    protected open fun getDispatchers(): CoroutineContext = Dispatchers.IO + job

    protected open fun getColorState(): IntArray = intArrayOf(Color.TRANSPARENT, Color.WHITE)

    protected open fun isVisible(): Boolean = true

    protected open fun isHideNavigation(): Boolean = true

    protected abstract fun setUp()

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)

        if (isHideNavigation()) {
            window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
            window.navigationBarColor = Color.parseColor("#01ffffff")
            window.statusBarColor = Color.TRANSPARENT
            decorView.systemUiVisibility = hideSystemBars()
        }
    }

    private fun hideSystemBars(): Int = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)

    private fun checkUpdate() {
        //check update is UPDATE_AVAILABLE
        AppUpdateManagerFactory.create(this).appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
            ) {
                showDialogUpdate()
            } else if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                //CHECK THIS if AppUpdateType.FLEXIBLE, otherwise you can skip
            } else Log.d(TAG, "checkUpdate: ehe")
        }
    }

    private fun showDialogUpdate() {
        val bindingDialog =
            DialogUpdateAppBinding.inflate(LayoutInflater.from(this@BaseActivity), null, false)

        val dialog = AlertDialog.Builder(this@BaseActivity, R.style.SheetDialog).create()
        dialog.setUpDialog(bindingDialog.root, false)

        bindingDialog.root.layoutParams.width = (69.96f * w).toInt()
        bindingDialog.root.layoutParams.height = (84.889f * w).toInt()

        bindingDialog.tvUpdate.setOnUnDoubleClickListener {
            try {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=$packageName")
                    )
                )
            } catch (anfe: android.content.ActivityNotFoundException) {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                    )
                )
            }
        }
    }

    override fun startIntent(nameActivity: String, isFinish: Boolean) {
        val intent = Intent().apply {
            component = ComponentName(this@BaseActivity, nameActivity)
        }
        startActivity(intent, null)
        if (isFinish) this.finish()
    }

    override fun startIntent(intent: Intent, isFinish: Boolean) {
        startActivity(intent, null)
        if (isFinish) this.finish()
    }

    override fun startIntentForResult(
        startForResult: ActivityResultLauncher<Intent>,
        nameActivity: String,
        isFinish: Boolean
    ) {
        startForResult.launch(
            Intent().apply {
                component = ComponentName(this@BaseActivity, nameActivity)
            }, null
        )
        if (isFinish) this.finish()
    }

    override fun startIntentForResult(
        startForResult: ActivityResultLauncher<Intent>,
        intent: Intent,
        isFinish: Boolean
    ) {
        startForResult.launch(intent, null)
        if (isFinish) this.finish()
    }

    fun checkPer(str: Array<String>): Boolean {
        if (str[0] == "") return true
        var isCheck = true
        for (i in str) {
            if (ContextCompat.checkSelfPermission(this, i) != PackageManager.PERMISSION_GRANTED)
                isCheck = false
        }

        return isCheck
    }

    fun haveNetworkConnection(): Boolean {
        var haveConnectedWifi = false
        var haveConnectedMobile = false
        val cm =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.allNetworkInfo
        for (ni in netInfo) {
            if (ni.typeName.equals("WIFI", ignoreCase = true))
                if (ni.isConnected) haveConnectedWifi = true
            if (ni.typeName.equals("MOBILE", ignoreCase = true))
                if (ni.isConnected) haveConnectedMobile = true
        }
        return haveConnectedWifi || haveConnectedMobile
    }

    protected fun onBackPressed(isAsk: Boolean) {
        if (isAsk) {
            if (finish != 0) {
                finish = 0
                finish()
                setAnimExit()
            } else {
                Toast.makeText(this, R.string.finish, Toast.LENGTH_SHORT).show()
                finish++
                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        finish = 0
                    }
                }, 1000)
            }
        } else finish()
    }

    override fun showLoading() {
        showLoading(true)
    }

    private fun initDialog(isCancel: Boolean) {
        val bindingDialog = DialogLoadingBinding.inflate(LayoutInflater.from(this@BaseActivity))
        bindingDialog.root.createBackground(intArrayOf(Color.WHITE), 3.5f * w, -1, -1)

        loadingDialog = AlertDialog.Builder(this@BaseActivity, R.style.SheetDialog).create()
        loadingDialog?.setUpDialog(bindingDialog.root, isCancel)

        bindingDialog.root.layoutParams.width = (73.889f * w).toInt()
        bindingDialog.root.layoutParams.height = (34.556f * w).toInt()
        mIsShowLoading = true
    }

    override fun showLoading(cancelable: Boolean) {
        Handler(Looper.getMainLooper()).post {
            if (loadingDialog != null && mIsShowLoading) {
                loadingDialog?.cancel()
                loadingDialog = null
            }
            initDialog(cancelable)
        }
    }

    override fun hideLoading() {
        //cho chắc :(
        Handler(Looper.getMainLooper()).post {
            if (loadingDialog != null && mIsShowLoading && !isFinishing) {
                loadingDialog?.cancel()
                loadingDialog = null
            }
            mIsShowLoading = false
        }
    }

    fun showDialogBack(isDismiss: ICallBackCheck) {
        val bindingDialog = DialogBackToHomeBinding.inflate(LayoutInflater.from(this))
        val dialog = AlertDialog.Builder(this, R.style.SheetDialog).create()
        dialog.setUpDialog(bindingDialog.root, false)
        isShowDialogBack = true

        bindingDialog.root.layoutParams.width = (91.111f * w).toInt()
//            bindingDialog.root.layoutParams.height = (50.55f * w).toInt()

        bindingDialog.tvCancel.setOnUnDoubleClickListener {
            dialog.cancel()
            isDismiss.check(true)
        }
        bindingDialog.tvContinue.setOnUnDoubleClickListener { finish() }
    }

    fun showDialogExit(isCancel: ICallBackCheck) {
        val bindingDialog = DialogExitAppBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(this, R.style.SheetDialog).create()
        dialog.setUpDialog(bindingDialog.root, false)
        isShowDialogBack = true

        bindingDialog.root.layoutParams.width = (95.55f * w).toInt()
//            bindingDialog.root.layoutParams.height = (50.55f * w).toInt()

        bindingDialog.tvStay.setOnUnDoubleClickListener {
            dialog.cancel()
            isCancel.check(true)
        }
        bindingDialog.tvExit.setOnUnDoubleClickListener { finishAffinity() }
    }
}