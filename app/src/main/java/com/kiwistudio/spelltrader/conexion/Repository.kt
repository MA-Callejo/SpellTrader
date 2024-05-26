package com.kiwistudio.spelltrader.conexion

class Repository {
    private val api = RetrofitInstance.api

    suspend fun logIn(user: String, pass: String): Response {
        return api.logIn(user, pass)
    }
}