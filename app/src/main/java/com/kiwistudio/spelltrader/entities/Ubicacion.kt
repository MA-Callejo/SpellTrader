package com.kiwistudio.spelltrader.entities

data class Ubicacion(
    val nombre: String,
    val calle: String = "",
    val provincia: Int = 0,
    val poblacion: String = "",
    val cp: String = "",
    val altitud: Double? = null,
    val longitud: Double? = null,
    val numero: Int = 0,
    var id: Int = 0,
    )
