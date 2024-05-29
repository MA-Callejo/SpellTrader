package com.kiwistudio.spelltrader.entities

data class Anuncio(
    var id: Int = 0,
    var nombre: String,
    var descripcion: String = "",
    var carta: String = "",
    var estado: Int = 0,
    var fkPropietario: Int,
    var modoPrecio: Int = 0,
    var precio: Double = 0.0,
    var porcentaje: Double = 0.0,
    var comision: Double = 0.0,
    var idioma: Int = 0,
    var foil: Boolean = false
)
