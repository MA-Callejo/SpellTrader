package com.kiwistudio.spelltrader.entities

data class ChatPreview(
    var username: String = "",
    var valor: Double = 0.0,
    var mensaje: String = "",
    var saliente: Boolean = false,
    var pedidoId: Int = 0,
    var tipo: Int = 0)
