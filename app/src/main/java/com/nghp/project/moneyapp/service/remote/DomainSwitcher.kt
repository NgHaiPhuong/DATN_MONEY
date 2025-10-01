package com.nghp.project.moneyapp.service.remote

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class DomainSwitcher {

    companion object {
        private val INSTANCE = DomainSwitcher()
        @JvmStatic
        fun getInstance(): DomainSwitcher {
            return INSTANCE
        }

        private const val BASE_URL_API: String = ""
    }

    fun <T> getApiClientWithUrl(apiClientClass: Class<T>, url: String): T {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(okHttpClient())
            .build()
        return retrofit.create(apiClientClass)
    }

    fun <T> getApiClient(apiClientClass: Class<T>): T {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL_API)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(okHttpClient())
            .build()
        return retrofit.create(apiClientClass)
    }

    private fun okHttpClient(): OkHttpClient {
        val clientBuilder: OkHttpClient.Builder = OkHttpClient.Builder()
            .retryOnConnectionFailure(false)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)
        return clientBuilder.build()
    }
}