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
import kotlinx.coroutines.launch
import org.json.JSONObject

class MainViewModel: ViewModel() {
    private lateinit var context: Context
    lateinit var securePreferenceHelper: SecurePreferenceHelper
    private val BASE_URL = "https://kiwiprojectstudio.com/SpellDeal/"
    var token = ""
    private var userId = 0

    fun init(context: Context) {
        Log.d("CONTEXT", "Iniciado")
        this.context = context
        securePreferenceHelper = SecurePreferenceHelper(context)
        val credentials = securePreferenceHelper.getUserId()
        userId = credentials.first
        token = credentials.second.toString()
    }
    fun getUbicaciones(user: Int): LiveData<Response> {
        val result = MutableLiveData<Response>()
        val queue: RequestQueue = Volley.newRequestQueue(context)
        val url = BASE_URL + "getUbicaciones.php?user=" + user + "&token=" + token
        val jsonObjectRequest = JsonObjectRequest(
            com.android.volley.Request.Method.GET, url, null,
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
}