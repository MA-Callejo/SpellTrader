package com.kiwistudio.spelltrader.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class Mensaje(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val propio: Int,
    val message: String,
    val timestamp: Long,
    val pedido: Int
)