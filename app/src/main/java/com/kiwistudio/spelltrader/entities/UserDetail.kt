package com.kiwistudio.spelltrader.entities

data class UserDetail(
    var id: Int = 0,
    var nombre: String = "",
    var ventas: Int = 0,
    var compras: Int = 0,
    var cambios: Int = 0,
    var score: Double = 0.0,
    var ubicaciones: List<Ubicacion> = listOf(),
    var valoraciones: List<Valoraciones> = listOf()
)
