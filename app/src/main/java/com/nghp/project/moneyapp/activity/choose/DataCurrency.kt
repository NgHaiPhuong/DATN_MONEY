package com.nghp.project.moneyapp.activity.choose

import android.content.Context
import com.nghp.project.moneyapp.NMHApp
import com.nghp.project.moneyapp.db.model.CurrencyModel

object DataCurrency {

    fun getDataCurrency() : MutableList<CurrencyModel> {
        val listCurrencies = mutableListOf<CurrencyModel>()

        val f = NMHApp.ctx.assets.list("flag_currency")
        if (f != null) {
            for (s in f) {
                val country = s.replace(".webp", "")

                listCurrencies.add(CurrencyModel(country, getCode(country), getName(country), "flag_currency", getSymbol(country)))
            }
        }

        return listCurrencies
    }

    private fun getCode(country: String): String {
        return when(country.lowercase()) {
            "australian" -> "AUD"
            "chinese" -> "CNY"
            "euro" -> "EUR"
            "indian" -> "INR"
            "indonesian" -> "IDR"
            "thai" -> "THB"
            "uk" -> "GBP"
            "us" -> "USD"
            "vietnamese" -> "VND"
            else -> "VND"
        }
    }

    private fun getName(country: String): String {
        return when(country.lowercase()) {
            "australian" -> "- Australian Dollar"
            "chinese" -> "- Chinese Yuan"
            "euro" -> "- Euro"
            "indian" -> "- Indian Rupee"
            "indonesian" -> "- Indonesian Rupiah"
            "thai" -> "- Thai Baht"
            "uk" -> "- UK Pound"
            "us" -> "- US Dollar"
            "vietnamese" -> "- Vietnamese Dong"
            else -> "- Vietnamese Dong"
        }
    }

    private fun getSymbol(country: String): String {
        return when(country.lowercase()) {
            "australian" -> "$"
            "chinese" -> "¥"
            "euro" -> "€"
            "indian" -> "₹"
            "indonesian" -> "Rp"
            "thai" -> "฿"
            "uk" -> "£"
            "us" -> "$"
            "vietnamese" -> "₫"
            else -> "₫"
        }
    }
}