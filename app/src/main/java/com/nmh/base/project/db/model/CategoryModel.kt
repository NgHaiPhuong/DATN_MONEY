package com.nmh.base.project.db.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CategoryModel (
    val img : Int,
    val name : String,
    val colorImg : Int,
    var isSelected: Boolean = false
) : Parcelable