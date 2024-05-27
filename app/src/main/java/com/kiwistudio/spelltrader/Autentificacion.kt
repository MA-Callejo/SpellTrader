package com.kiwistudio.spelltrader

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.kiwistudio.spelltrader.R

class Autentificacion : AppCompatActivity() {
    private lateinit var viewModel: LogInViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this)[LogInViewModel::class.java]
        viewModel.init(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_autentificacion)
    }
}