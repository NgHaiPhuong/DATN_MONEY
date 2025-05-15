package com.nmh.base.project.db.model

import com.google.gson.annotations.SerializedName

data class OnBoardingModel(
    @SerializedName("strTitle")
    var strTitle: String = "",
    @SerializedName("str")
    var str: String = "",
    @SerializedName("imgPage")
    var imgPage: Int = -1,
    @SerializedName("strIdAds")
    var strIdAds: String = "",
    @SerializedName("remote")
    var remote: Boolean = false,
    @SerializedName("isAds")
    var isAdsFullScr: Boolean = false
)