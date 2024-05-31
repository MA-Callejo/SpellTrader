package com.kiwistudio.spelltrader.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MensajeDao {

    @Insert
    suspend fun insertMessage(message: Mensaje): Long

    @Query("SELECT * FROM messages WHERE pedido = :pedido ORDER BY timestamp ASC")
    suspend fun getAllMessages(pedido: Int): List<Mensaje>
}