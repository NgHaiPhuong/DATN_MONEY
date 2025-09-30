package com.nghp.project.moneyapp.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.nghp.project.moneyapp.db.model.BudgetModel
import com.nghp.project.moneyapp.db.model.ChatMessageModel
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {
    @Insert
    suspend fun insertMessage(messageModel: ChatMessageModel)

    @Query("DELETE FROM message_table WHERE message = :msg")
    suspend fun deleteMessagesByText(msg: String)

    @Query("SELECT * FROM message_table")
    fun getAllMessage(): Flow<List<ChatMessageModel>>
}