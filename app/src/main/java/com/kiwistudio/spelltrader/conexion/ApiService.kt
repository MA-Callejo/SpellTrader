package com.kiwistudio.spelltrader.conexion

import retrofit2.http.GET

interface ApiService {
    @GET("login.php")
    suspend fun logIn(user: String, pass: String): Response
}