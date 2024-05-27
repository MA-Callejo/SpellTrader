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
    private val repository = Repository()
    private val _response = MutableLiveData<Response>()
    private val BASE_URL = "https://kiwiprojectstudio.com/SpellDeal/"
    val response: LiveData<Response> get() = _response
    var token = ""
    private var userId = 0

    fun init(context: Context) {
        Log.d("CONTEXT", "Iniciado")
        this.context = context
        securePreferenceHelper = SecurePreferenceHelper(context)
    }

    fun logIn(user: String, pass: String) {
        val queue: RequestQueue = Volley.newRequestQueue(context)
        val url = BASE_URL + "login.php?user="+user+"&pass="+pass
        val jsonObjectRequest =
            JsonObjectRequest(com.android.volley.Request.Method.GET, url, null,
                object : com.android.volley.Response.Listener<JSONObject?> {
                    override fun onResponse(response: JSONObject?) {
                        if (response != null) {
                            var code = if(response.has("code")) response.getInt("code") else 599
                            var msg = if(response.has("msg")) response.getString("msg") else null
                            var body = if(response.has("body")) response.getJSONObject("body") else null
                            _response.value = Response(code, msg, body)
                        }
                    }
                },
                object : com.android.volley.Response.ErrorListener {
                    override fun onErrorResponse(error: VolleyError) {
                        error.message?.let { Log.d("ERROR", it) }
                    }
                })
        queue.add(jsonObjectRequest)
    }
    fun singIn(user: String, pass: String, email: String) {
        val queue: RequestQueue = Volley.newRequestQueue(context)
        val url = BASE_URL + "create_user.php?user="+user+"&pass="+pass+"&correo="+email
        val jsonObjectRequest =
            JsonObjectRequest(com.android.volley.Request.Method.GET, url, null,
                { response ->
                    if (response != null) {
                        val code = if(response.has("code")) response.getInt("code") else 599
                        val msg = if(response.has("msg")) response.getString("msg") else null
                        val body = if(response.has("body")) response.getJSONObject("body") else null
                        _response.value = Response(code, msg, body)
                    }
                }
            ) { error -> error.message?.let { Log.d("ERROR", it) } }
        queue.add(jsonObjectRequest)
    }

    fun setUser(userId: Int, token: String){
        this.userId = userId
        this.token = token
    }
}