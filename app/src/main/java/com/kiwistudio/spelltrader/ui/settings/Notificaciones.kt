package com.kiwistudio.spelltrader.ui.settings

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import androidx.lifecycle.ViewModelProvider
import com.kiwistudio.spelltrader.MainViewModel
import com.kiwistudio.spelltrader.R
class Notificaciones : Fragment() {
    private lateinit var viewModel: MainViewModel
    private lateinit var mensajes: Switch
    private lateinit var picos: Switch
    private lateinit var ofertas: Switch
    private lateinit var caducidad: Switch
    private lateinit var valoraciones: Switch
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notificaciones, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        mensajes = view.findViewById(R.id.mensajes)
        mensajes.isChecked = viewModel.notificaciones[0]
        picos = view.findViewById(R.id.picos)
        picos.isChecked = viewModel.notificaciones[1]
        ofertas = view.findViewById(R.id.ofertas)
        ofertas.isChecked = viewModel.notificaciones[2]
        caducidad = view.findViewById(R.id.caducidad)
        caducidad.isChecked = viewModel.notificaciones[3]
        valoraciones = view.findViewById(R.id.valoraciones)
        valoraciones.isChecked = viewModel.notificaciones[4]
        mensajes.setOnClickListener{
            viewModel.securePreferenceHelper.savePrefernece("mensajes", (mensajes.isChecked))
        }
        picos.setOnClickListener{
            viewModel.securePreferenceHelper.savePrefernece("picos", (picos.isChecked))
        }
        ofertas.setOnClickListener{
            viewModel.securePreferenceHelper.savePrefernece("ofertas", (ofertas.isChecked))
        }
        caducidad.setOnClickListener{
            viewModel.securePreferenceHelper.savePrefernece("caducidad", (caducidad.isChecked))
        }
        valoraciones.setOnClickListener{
            viewModel.securePreferenceHelper.savePrefernece("valoraciones", (valoraciones.isChecked))
        }
    }
}