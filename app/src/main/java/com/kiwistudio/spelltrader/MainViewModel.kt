package com.kiwistudio.spelltrader

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kiwistudio.spelltrader.conexion.Repository
import com.kiwistudio.spelltrader.conexion.Response
import kotlinx.coroutines.launch

class MainViewModel: ViewModel() {
    private lateinit var context: Context
    lateinit var securePreferenceHelper: SecurePreferenceHelper
    private val repository = Repository()
    private val _response = MutableLiveData<Response>()
    val response: LiveData<Response> get() = _response
    private var token = ""
    private var userId = 0

    fun init(context: Context){
        this.context = context
        securePreferenceHelper = SecurePreferenceHelper(context)
    }

    fun logIn(user: String, pass: String) {
        viewModelScope.launch {
            try {
                val userList = repository.logIn(user, pass)
                _response.postValue(userList)
            } catch (e: Exception) {
                // Manejar la excepción
            }
        }
    }

    fun setUser(userId: Int, token: String){
        this.userId = userId
        this.token = token
    }
}