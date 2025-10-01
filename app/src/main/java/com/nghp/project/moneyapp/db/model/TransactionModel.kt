package com.nghp.project.moneyapp.db.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nghp.project.moneyapp.helpers.TypeLoan
import com.nghp.project.moneyapp.helpers.TypeModel
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "transaction_table")
@Parcelize
data class TransactionModel(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var category: CategoryModel,
    var amount: Long = 0L,
    var note: String,
    var date: String,
    var time: String,
    var budgetInt: Int? = -1,
    var noteLender: String = "",
    var noteBorrower: String = "",
    var typeModel: TypeModel? = TypeModel.EXPENSES,
    var typeLoan: TypeLoan? = TypeLoan.NULL
) : Parcelable