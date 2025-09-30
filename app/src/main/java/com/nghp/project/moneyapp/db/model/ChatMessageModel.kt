package com.nghp.project.moneyapp.db.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "message_table")
@Parcelize
data class ChatMessageModel(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var message: String = "",
    var isUser: Boolean
) : Parcelable