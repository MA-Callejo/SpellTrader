package com.kiwistudio.spelltrader.entities

data class Ubicacion(
    val nombre: String,
    val calle: String = "",
    val provincia: Int = 0,
    val poblacion: String = "",
    val cp: String = "",
    val altitud: Float? = null,
    val longitud: Float? = null,
    val numero: Int = 0,
    var id: Int = 0,
    )
