package com.nmh.base.project.service.remote

import com.nmh.base.project.db.model.LanguageModel
import retrofit2.Call
import retrofit2.http.GET

interface GEMApi {
    @GET("config.json")
    fun dataFamous(): Call<MutableList<LanguageModel>>
}