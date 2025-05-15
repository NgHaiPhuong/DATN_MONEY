package com.nmh.base.project.utils

import android.content.Context
import com.nmh.base.project.R
import com.nmh.base.project.db.model.CategoryModel
import com.nmh.base.project.db.model.MonthModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DataHelper {

    fun getMonth(context: Context): MutableList<MonthModel> {
        val list: MutableList<MonthModel> = ArrayList()

        list.add(MonthModel(context.getString(R.string.jan)))
        list.add(MonthModel(context.getString(R.string.feb)))
        list.add(MonthModel(context.getString(R.string.mar)))
        list.add(MonthModel(context.getString(R.string.apr)))
        list.add(MonthModel(context.getString(R.string.may)))
        list.add(MonthModel(context.getString(R.string.jun)))
        list.add(MonthModel(context.getString(R.string.jul)))
        list.add(MonthModel(context.getString(R.string.aug)))
        list.add(MonthModel(context.getString(R.string.sep)))
        list.add(MonthModel(context.getString(R.string.oct)))
        list.add(MonthModel(context.getString(R.string.nov)))
        list.add(MonthModel(context.getString(R.string.dec)))

        return list
    }


    fun getDataExpenses(context: Context): MutableList<CategoryModel> {
        val list: MutableList<CategoryModel> = ArrayList()

        list.add(CategoryModel(R.drawable.ic_food, context.getString(R.string.food), R.color.color_food, true))
        list.add(CategoryModel(R.drawable.ic_social, context.getString(R.string.social), R.color.color_social))
        list.add(CategoryModel(R.drawable.ic_traffic, context.getString(R.string.traffic), R.color.color_traffic))
        list.add(CategoryModel(R.drawable.ic_shopping, context.getString(R.string.shopping), R.color.color_shopping))
        list.add(CategoryModel(R.drawable.ic_grocery, context.getString(R.string.grocery), R.color.color_grocery))
        list.add(CategoryModel(R.drawable.ic_education, context.getString(R.string.education), R.color.color_education))
        list.add(CategoryModel(R.drawable.ic_bills, context.getString(R.string.bills), R.color.color_bills))
        list.add(CategoryModel(R.drawable.ic_rentals, context.getString(R.string.rentals), R.color.color_rentals))
        list.add(CategoryModel(R.drawable.ic_medical, context.getString(R.string.medical), R.color.color_medical))
        list.add(CategoryModel(R.drawable.ic_investment, context.getString(R.string.investment), R.color.color_investment))
        list.add(CategoryModel(R.drawable.ic_gift, context.getString(R.string.gift), R.color.color_gift))
        list.add(CategoryModel(R.drawable.ic_other, context.getString(R.string.other), R.color.color_other))
        return list
    }

    fun getDataIncome(context: Context): MutableList<CategoryModel> {
        val list: MutableList<CategoryModel> = ArrayList()

        list.add(CategoryModel(R.drawable.ic_salary, context.getString(R.string.salary), R.color.color_salary, true))
        list.add(CategoryModel(R.drawable.ic_invest, context.getString(R.string.invest), R.color.color_invest))
        list.add(CategoryModel(R.drawable.ic_business, context.getString(R.string.business), R.color.color_business))
        list.add(CategoryModel(R.drawable.ic_interest, context.getString(R.string.interest), R.color.color_interest))
        list.add(CategoryModel(R.drawable.ic_extra, context.getString(R.string.extra_income), R.color.color_extra))
        list.add(CategoryModel(R.drawable.ic_other, context.getString(R.string.other), R.color.color_other))
        return list
    }

    fun getDataLoan(context: Context): MutableList<CategoryModel> {
        val list: MutableList<CategoryModel> = ArrayList()

        list.add(CategoryModel(R.drawable.ic_loan, context.getString(R.string.loan), R.color.color_loan, true))
        list.add(CategoryModel(R.drawable.ic_borrow, context.getString(R.string.borrow), R.color.color_borrow))
        return list
    }
}