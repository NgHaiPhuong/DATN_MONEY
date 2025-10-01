package com.nghp.project.moneyapp.db.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "budget_table")
@Parcelize
data class BudgetModel (
    @PrimaryKey(autoGenerate = true)
    val id : Int = 0,
    val name: String = "",
    var amount : Long = 0L,
    val date : String = "",
    var isSelected : Boolean = false,
    var color: Int = -1
) : Parcelable