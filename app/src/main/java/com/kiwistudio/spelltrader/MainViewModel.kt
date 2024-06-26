package com.kiwistudio.spelltrader

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.volley.RequestQueue
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.kiwistudio.spelltrader.conexion.Repository
import com.kiwistudio.spelltrader.conexion.Response
import com.kiwistudio.spelltrader.db.DataBase
import com.kiwistudio.spelltrader.db.Mensaje
import com.kiwistudio.spelltrader.db.MensajeDao
import com.kiwistudio.spelltrader.entities.Anuncio
import com.kiwistudio.spelltrader.entities.Filtros
import com.kiwistudio.spelltrader.entities.Ubicacion
import com.kiwistudio.spelltrader.entities.UserDetail
import kotlinx.coroutines.launch
import org.json.JSONObject

class MainViewModel: ViewModel() {
    private lateinit var context: Context
    lateinit var securePreferenceHelper: SecurePreferenceHelper
    private lateinit var database: DataBase
    private lateinit var messageDao: MensajeDao
    private val BASE_URL = "https://kiwiprojectstudio.com/SpellDeal/"
    private val ORACLE_URL = "https://api.scryfall.com/"
    var token = ""
    var userId = 0
    var notificaciones: MutableList<Boolean> = mutableListOf()
    var userVer: UserDetail? = null
    var userIdView = 0
    var filtroUSer: Int? = null
    var filtroUserName = ""
    var pedidoID: Int = 0

    fun init(context: Context) {
        Log.d("CONTEXT", "Iniciado")
        this.context = context
        securePreferenceHelper = SecurePreferenceHelper(context)
        val credentials = securePreferenceHelper.getUserId()
        userId = credentials.first
        token = credentials.second.toString()
        notificaciones = securePreferenceHelper.getNotificaciones()
        database = DataBase.getDatabase(context)
        messageDao = database.messageDao()
    }
    suspend fun getMensajes(): List<Mensaje> {
        return messageDao.getAllMessages(pedidoID)
    }
    suspend fun insetMessage(mensaje: Mensaje): Long {
        return messageDao.insertMessage(mensaje)
    }
    fun connexionKiwi(url: String, body: JSONObject?): LiveData<Response>{
        val result = MutableLiveData<Response>()
        val queue: RequestQueue = Volley.newRequestQueue(context)
        Log.d("SENDING", "$url: ${body.toString()}")
        val jsonObjectRequest = JsonObjectRequest(
            com.android.volley.Request.Method.POST, BASE_URL + url, body,
            { response ->
                if (response != null) {
                    val code = if(response.has("code")) response.getInt("code") else 599
                    val msg = if(response.has("msg")) response.getString("msg") else null
                    val body = if(response.has("body")) response.getJSONObject("body") else null
                    result.value = Response(code, msg, body)
                }
            },
            { error -> error.message?.let { Log.d("ERROR", it) } })
        queue.add(jsonObjectRequest)
        return result
    }
    fun connexionOracle(url: String, body: JSONObject?): LiveData<JSONObject>{
        val result = MutableLiveData<JSONObject>()
        val queue: RequestQueue = Volley.newRequestQueue(context)
        val jsonObjectRequest = JsonObjectRequest(
            com.android.volley.Request.Method.GET, ORACLE_URL + url, body,
            { response ->
                if (response != null) {
                    result.value = response
                }
            },
            { error -> error.message?.let { Log.d("ERROR", it) } })
        queue.add(jsonObjectRequest)
        return result
    }
    fun getMisAnuncios(): LiveData<Response> {
        val url = "getMisAnuncios.php?token=" + token
        val result = connexionKiwi(url, null)
        return result
    }
    fun getMensajesServer(pedido: Int): LiveData<Response> {
        val url = "readMsg.php?token=" + token+"&pedido="+pedido
        val result = connexionKiwi(url, null)
        return result
    }
    fun getUser(): LiveData<Response> {
        val url = "getUser.php?token=" + token+"&id="+userIdView
        val result = connexionKiwi(url, null)
        return result
    }
    fun sendMensaje(mensaje: Mensaje): LiveData<Response> {
        val jsonBody = JSONObject().apply {
            put("cuerpo", mensaje.message)
            put("pedido", mensaje.pedido)
            put("time", mensaje.timestamp)
        }
        val url = "sendMsg.php?token=" + token
        val result = connexionKiwi(url, jsonBody)
        return result
    }
    fun getUserMe(): LiveData<Response> {
        val url = "getUser.php?token=" + token+"&id="+userId
        val result = connexionKiwi(url, null)
        return result
    }
    fun reservar(anuncio: Int, valor: Double): LiveData<Response> {
        val url = "reservar.php?token=" + token+"&anuncio="+anuncio+"&valor="+valor
        val result = connexionKiwi(url, null)
        return result
    }
    fun getHistorico(): LiveData<Response> {
        val url = "getHistorico.php?token=" + token
        val result = connexionKiwi(url, null)
        return result
    }
    fun getAnuncios(filtros: Filtros): LiveData<Response> {
        val jsonBody = JSONObject().apply {
            put("altitud", filtros.altitud)
            put("latitud", filtros.latitud)
            put("nombre", filtros.nombre)
            put("user", filtros.user)
        }
        val url = "getAnuncios.php?token=" + token
        val result = connexionKiwi(url, jsonBody)
        return result
    }
    fun getPendientes(): LiveData<Response> {
        val url = "getPendientes.php?token=" + token
        val result = connexionKiwi(url, null)
        return result
    }
    fun deleteAnuncios(id: Int): LiveData<Response>{
        val url = "deleteAnuncios.php?id=" + id + "&token=" + token
        val result = connexionKiwi(url, null)
        return result
    }
    fun updateAnuncios(anuncio: Anuncio): LiveData<Response>{
        val jsonBody = JSONObject().apply {
            put("nombre", anuncio.nombre)
            put("descripcion", anuncio.descripcion)
        }
        val url = "updateAnuncios.php?id="+anuncio.id+"&token=" + token
        val result = connexionKiwi(url, jsonBody)
        return result
    }
    fun createAnuncio(anuncio: Anuncio): LiveData<Response>{
        val jsonBody = JSONObject().apply {
            put("nombre", anuncio.nombre)
            put("descripcion", anuncio.descripcion)
        }
        val url = "createAnuncio.php?token=" + token
        val result = connexionKiwi(url, jsonBody)
        return result
    }
    fun getUbicaciones(user: Int): LiveData<Response> {
        val url = "getUbicaciones.php?user=" + user + "&token=" + token
        val result = connexionKiwi(url, null)
        return result
    }
    fun deleteUbicacion(id: Int): LiveData<Response>{
        val url = "deleteUbicaciones.php?id=" + id + "&token=" + token
        val result = connexionKiwi(url, null)
        return result
    }
    fun updateUbicacion(ubicacion: Ubicacion): LiveData<Response>{
        val jsonBody = JSONObject().apply {
            put("nombre", ubicacion.nombre)
            put("altitud", ubicacion.altitud)
            put("longitud", ubicacion.longitud)
        }
        val url = "updateUbicacion.php?id="+ubicacion.id+"&token=" + token
        val result = connexionKiwi(url, jsonBody)
        return result
    }
    fun createUbicacion(ubicacion: Ubicacion): LiveData<Response>{
        val jsonBody = JSONObject().apply {
            put("nombre", ubicacion.nombre)
            put("altitud", ubicacion.altitud)
            put("longitud", ubicacion.longitud)
        }
        val url = "createUbicacion.php?token=" + token
        val result = connexionKiwi(url, jsonBody)
        return result
    }

    fun getCard(id: String): LiveData<JSONObject> {
        val url = "cards/"+id
        return connexionOracle(url, null)
    }
}