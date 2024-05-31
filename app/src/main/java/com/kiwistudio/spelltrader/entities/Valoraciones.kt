package com.kiwistudio.spelltrader.entities

data class Valoraciones(
    var username: String,
    var userid: Int,
    var valoracion: String,
    var score: Double,
    var fecha: String,
    var valor: Double = 0.0,
    var tipo: Int = 0)
