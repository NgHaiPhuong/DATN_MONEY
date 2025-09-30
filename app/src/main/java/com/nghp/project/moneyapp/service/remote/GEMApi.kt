package com.nghp.project.moneyapp.service.remote

import com.nghp.project.moneyapp.db.model.LanguageModel
import retrofit2.Call
import retrofit2.http.GET

interface GEMApi {
    @GET("config.json")
    fun dataFamous(): Call<MutableList<LanguageModel>>
}