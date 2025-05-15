package com.nmh.base.project.activity.onboarding

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.nlbn.ads.callback.NativeCallback
import com.nlbn.ads.util.Admob
import com.nlbn.ads.util.ConsentHelper
import com.nmh.base.project.R
import com.nmh.base.project.databinding.AdsNativeBotBinding
import com.nmh.base.project.databinding.AdsNativeFullScrBinding
import com.nmh.base.project.databinding.AdsNativeTopNewBinding
import com.nmh.base.project.databinding.ItemOnBoardingNewBinding
import com.nmh.base.project.databinding.NativeBotMediaTopLoadingBinding
import com.nmh.base.project.databinding.NativeTopLoadingBinding
import com.nmh.base.project.extensions.gone
import com.nmh.base.project.extensions.invisible
import com.nmh.base.project.extensions.setOnUnDoubleClickListener
import com.nmh.base.project.extensions.visible
import com.nmh.base.project.db.model.OnBoardingModel
import com.nmh.base.project.utils.AdsConfig
import com.nmh.base.project.utils.AdsConfig.haveNetworkConnection
import com.nmh.base.project.callback.ICallBackItem
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

class PagerOnBoardingNewAdapter @Inject constructor(@ActivityContext private val context: Context) : RecyclerView.Adapter<PagerOnBoardingNewAdapter.PagerHolder>() {

    private lateinit var lstPage: MutableList<OnBoardingModel>
    var actionNext: ICallBackItem? = null
    var posDot = 0

    fun setData(lstTip: MutableList<OnBoardingModel>) {
        this.lstPage = lstTip

        notifyChange()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerHolder =
        PagerHolder(ItemOnBoardingNewBinding.inflate(LayoutInflater.from(context), parent, false))

    override fun getItemCount(): Int = if (lstPage.isNotEmpty()) lstPage.size else 0

    override fun onBindViewHolder(holder: PagerHolder, position: Int) {
        holder.onBind(position)
    }

    inner class PagerHolder(private val binding: ItemOnBoardingNewBinding) : ViewHolder(binding.root) {

        init {
            if (!AdsConfig.isLoadFullAds()) {
                val vLoad = NativeBotMediaTopLoadingBinding.inflate(LayoutInflater.from(context))
                binding.nativeAds.addView(vLoad.root)
            } else {
                val vLoad = NativeTopLoadingBinding.inflate(LayoutInflater.from(context))
                binding.nativeAds.addView(vLoad.root)
            }
        }

        fun onBind(position: Int) {
            val item = lstPage[position]

            if (item.isAdsFullScr) {
                binding.ctl.gone()
                binding.frAdsNative.visible()

                loadNativeFullScr(item.strIdAds)
            } else {
                binding.ctl.visible()
                binding.frAdsNative.gone()

                if (item.strIdAds != "") {
                    binding.vAnim.invisible()
                    loadNative(item.strIdAds, item.remote)
                } else {
                    binding.rlNative.invisible()
                    binding.vAnim.visible()
                }

                setDot()

                binding.tvTitle.text = item.strTitle
                binding.tvDes.text = item.str

                if (item.imgPage != -1)
                    Glide.with(context)
                        .asDrawable()
                        .load(item.imgPage)
                        .into(binding.iv)

                binding.tvAction.setOnUnDoubleClickListener { actionNext?.callBack(item, position) }
            }
        }

        private fun setDot() {
            when(posDot) {
                0 -> binding.ivDot.setImageResource(R.drawable.ic_dot_onboarding_1)
                1 -> binding.ivDot.setImageResource(R.drawable.ic_dot_onboarding_2)
                2 -> binding.ivDot.setImageResource(R.drawable.ic_dot_onboarding_3)
                3 -> binding.ivDot.setImageResource(R.drawable.ic_dot_onboarding_4)
            }
            posDot++
        }

        private fun loadNative(strId: String, remote: Boolean) {
            try {
                if (ConsentHelper.getInstance(context).canRequestAds() && haveNetworkConnection(context) && remote) {
                    binding.rlNative.visible()
                    Admob.getInstance().loadNativeAd(context, strId, object : NativeCallback() {
                        override fun onNativeAdLoaded(nativeAd: NativeAd) {
                            initViewNative(nativeAd)
                        }

                        override fun onAdFailedToLoad() {
                            super.onAdFailedToLoad()
                            binding.nativeAds.removeAllViews()
                        }
                    })
                } else binding.nativeAds.gone()
            } catch (e: Exception) {
                e.printStackTrace()
                binding.nativeAds.gone()
            }
        }

        private fun initViewNative(nativeAd: NativeAd) {
            val adView: NativeAdView
            if (!AdsConfig.isLoadFullAds())
                adView = AdsNativeBotBinding.inflate(LayoutInflater.from(context)).root
            else adView = AdsNativeTopNewBinding.inflate(LayoutInflater.from(context)).root

            binding.nativeAds.removeAllViews()
            binding.nativeAds.addView(adView)
            Admob.getInstance().pushAdsToViewCustom(nativeAd, adView)
        }

        private fun loadNativeFullScr(strId: String) {
            try {
                if (ConsentHelper.getInstance(context).canRequestAds() && haveNetworkConnection(context)) {
                    binding.frAdsNative.visible()
                    Admob.getInstance().loadNativeAd(context, strId, object : NativeCallback() {
                        override fun onNativeAdLoaded(nativeAd: NativeAd) {
                            initViewNativeFullScr(nativeAd)
                        }
                    })
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        private fun initViewNativeFullScr(nativeAd: NativeAd) {
            val adView = AdsNativeFullScrBinding.inflate(LayoutInflater.from(context))

            if (!AdsConfig.isLoadFullAds())
                adView.ctl.setBackgroundResource(R.drawable.bg_native)
            else adView.ctl.setBackgroundResource(R.drawable.bg_native_no_stroke)

            binding.frAds.removeAllViews()
            binding.frAds.addView(adView.root)
            Admob.getInstance().pushAdsToViewCustom(nativeAd, adView.root)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun notifyChange() {
        notifyDataSetChanged()
    }
}