package com.kiwistudio.spelltrader.ui.anuncios

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.kiwistudio.spelltrader.MainViewModel
import com.kiwistudio.spelltrader.R
import com.kiwistudio.spelltrader.entities.Anuncio
import com.kiwistudio.spelltrader.entities.AnuncioFull

class DialogAnuncio(val anuncio: AnuncioFull): DialogFragment() {
    private lateinit var viewModel: MainViewModel
    private lateinit var nombre: EditText
    private lateinit var descripcion: EditText
    private lateinit var btnOk: Button

    var onReserveListener: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_ubicacion, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        btnOk = view.findViewById(R.id.btnOk)
        nombre = view.findViewById(R.id.nombreUbicacion)
        nombre.setText(anuncio.nombre)
        descripcion.setText(anuncio.nombre)
    }
}