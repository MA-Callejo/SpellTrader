package com.kiwistudio.spelltrader.ui.catalogo

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

class DialogMiAnuncio(val anuncio: Anuncio?): DialogFragment() {
    private lateinit var viewModel: MainViewModel
    private lateinit var nombre: EditText
    private lateinit var descripcion: EditText
    private lateinit var btnOk: Button

    var onConfirmListener: (() -> Unit)? = null

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
        btnOk.setOnClickListener {
            enviar()
        }
        if(anuncio != null){
            nombre.setText(anuncio.nombre)
            descripcion.setText(anuncio.nombre)
        }
    }
    private fun enviar(){
        val ann = Anuncio(
            nombre = nombre.text.toString(),
            descripcion = nombre.text.toString()
        )
        if(anuncio != null){
            ann.id = anuncio.id
            viewModel.updateAnuncios(ann).observeForever{
                if(it != null){
                    onConfirmListener?.invoke()
                    dismiss()
                }
            }
        }else{
            viewModel.createAnuncio(ann).observeForever{
                if(it != null){
                    onConfirmListener?.invoke()
                    dismiss()
                }
            }
        }
    }
}