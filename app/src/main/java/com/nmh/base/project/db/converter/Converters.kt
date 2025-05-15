package com.nmh.base.project.db.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.nmh.base.project.db.model.CategoryModel
import com.nmh.base.project.helpers.TypeLoan
import com.nmh.base.project.helpers.TypeModel

class Converters {

    @TypeConverter
    fun formTypeModel(value: TypeModel?): String? {
        return value?.name
    }

    @TypeConverter
    fun toTypeModel(value: String?): TypeModel? {
        return value?.let { TypeModel.valueOf(value) }
    }

    @TypeConverter
    fun formTypeLoan(value: TypeLoan?): String? {
        return value?.name
    }

    @TypeConverter
    fun toTypeLoan(value: String?): TypeLoan? {
        return value?.let { TypeLoan.valueOf(value) }
    }

    @TypeConverter
    fun fromCategoryModel(value: CategoryModel?): String? {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toCategoryModel(json: String?): CategoryModel? {
        return json?.let { Gson().fromJson(it, CategoryModel::class.java) }
    }
}