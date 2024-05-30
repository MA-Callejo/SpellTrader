package com.kiwistudio.spelltrader

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

class SecurePreferenceHelper(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_USERNAME = "username"
        private const val KEY_PASSWORD = "password"
        private const val KEY_ID = "id"
        private const val KEY_TOKEN = "token"
        private const val NOTIFY_MENSAJES = "mensajes"
        private const val NOTIFY_PICOS = "picos"
        private const val NOTIFY_OFERTAS = "ofertas"
        private const val NOTIFY_CADUCIDAD = "caducidad"
        private const val NOTIFY_VALORACIONES = "valoraciones"
    }

    fun savePrefernece(key: String, valor: Boolean){
        with(sharedPreferences.edit()) {
            putBoolean(key, valor)
            apply()
        }
    }
    fun getNotificaciones(): MutableList<Boolean> {
        val notificaciones = mutableListOf(false, false, false, false, false)
        notificaciones[0] = sharedPreferences.getBoolean(NOTIFY_MENSAJES, false)
        notificaciones[1] = sharedPreferences.getBoolean(NOTIFY_PICOS, false)
        notificaciones[2] = sharedPreferences.getBoolean(NOTIFY_OFERTAS, false)
        notificaciones[3] = sharedPreferences.getBoolean(NOTIFY_CADUCIDAD, false)
        notificaciones[4] = sharedPreferences.getBoolean(NOTIFY_VALORACIONES, false)
        return notificaciones
    }

    fun saveUserCredentials(username: String, password: String) {
        with(sharedPreferences.edit()) {
            putString(KEY_USERNAME, username)
            putString(KEY_PASSWORD, password)
            apply()
        }
    }
    fun saveUserData(id: Int, token: String) {
        with(sharedPreferences.edit()) {
            putInt(KEY_ID, id)
            putString(KEY_TOKEN, token)
            apply()
        }
    }

    fun getUserCredentials(): Pair<String?, String?> {
        val username = sharedPreferences.getString(KEY_USERNAME, null)
        val password = sharedPreferences.getString(KEY_PASSWORD, null)
        return Pair(username, password)
    }
    fun getUserId(): Pair<Int, String?>{
        val id = sharedPreferences.getInt(KEY_ID, 0)
        val token = sharedPreferences.getString(KEY_TOKEN, null)
        return Pair(id, token)
    }

    fun clearUserCredentials() {
        with(sharedPreferences.edit()) {
            remove(KEY_USERNAME)
            remove(KEY_PASSWORD)
            apply()
        }
    }
}