package com.kiwistudio.spelltrader

import android.app.Application
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

class LogInViewModel: ViewModel() {
    private lateinit var context: Context
    lateinit var securePreferenceHelper: SecurePreferenceHelper
    private val BASE_URL = "https://kiwiprojectstudio.com/SpellDeal/"
    fun init(context: Context) {
        Log.d("CONTEXT", "Iniciado")
        this.context = context
        securePreferenceHelper = SecurePreferenceHelper(context)
    }
    fun logIn(user: String, pass: String): LiveData<Response> {
        val result = MutableLiveData<Response>()
        val queue: RequestQueue = Volley.newRequestQueue(context)
        val url = BASE_URL + "login.php?user=" + user + "&pass=" + pass
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
    fun singIn(user: String, pass: String, email: String): LiveData<Response>  {
        val result = MutableLiveData<Response>()
        val queue: RequestQueue = Volley.newRequestQueue(context)
        val url = BASE_URL + "create_user.php?user="+user+"&pass="+pass+"&correo="+email
        val jsonObjectRequest =
            JsonObjectRequest(com.android.volley.Request.Method.GET, url, null,
                { response ->
                    if (response != null) {
                        val code = if(response.has("code")) response.getInt("code") else 599
                        val msg = if(response.has("msg")) response.getString("msg") else null
                        val body = if(response.has("body")) response.getJSONObject("body") else null
                        result.value = Response(code, msg, body)
                    }
                }
            ) { error -> error.message?.let { Log.d("ERROR", it) } }
        queue.add(jsonObjectRequest)

        return result
    }
}