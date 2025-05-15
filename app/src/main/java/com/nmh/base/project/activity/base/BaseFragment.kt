package com.nmh.base.project.activity.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import java.lang.ref.WeakReference

abstract class BaseFragment<B: ViewBinding>(val bindingFactory: (LayoutInflater) -> B): Fragment(),
    BaseView {

    val binding: B by lazy { bindingFactory(layoutInflater) }

    private var baseActivity: WeakReference<BaseActivity<*>>? = null
    var w = 0f

    protected abstract fun setUp()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.isClickable = true
        w = requireContext().resources.displayMetrics.widthPixels / 100f
        setUp()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BaseActivity<*>) baseActivity = WeakReference(context)
    }

    override fun startIntent(nameActivity: String, isFinish: Boolean) {
        baseActivity?.get()?.startIntent(nameActivity, isFinish)
    }

    override fun showLoading() {
        baseActivity?.get()?.showLoading()
    }

    override fun showLoading(cancelable: Boolean) {
        baseActivity?.get()?.showLoading(cancelable)
    }

    override fun hideLoading() {
        baseActivity?.get()?.hideLoading()
    }

    companion object {
        //animation
        const val res = android.R.id.content
    }
}