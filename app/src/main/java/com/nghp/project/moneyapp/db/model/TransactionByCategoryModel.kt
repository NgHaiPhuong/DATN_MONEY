package com.nghp.project.moneyapp.db.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TransactionByCategoryModel(
    var category: CategoryModel,
    var totalAmount: Long = 0L
) : Parcelable