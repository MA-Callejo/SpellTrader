package com.kiwistudio.spelltrader.ui.anuncios

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.kiwistudio.spelltrader.MainViewModel
import com.kiwistudio.spelltrader.R
import com.kiwistudio.spelltrader.entities.Anuncio
import com.kiwistudio.spelltrader.entities.AnuncioFull
import com.squareup.picasso.Picasso

class DialogAnuncio(val anuncio: AnuncioFull): DialogFragment() {
    private lateinit var viewModel: MainViewModel
    private lateinit var nombre: TextView

    var onReserve: (() -> Unit)? = null
    var onUser: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_anuncio, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        nombre = view.findViewById(R.id.nombreCarta)
        val precioTV: TextView = view.findViewById(R.id.precio)
        val edicion: TextView = view.findViewById(R.id.edicion)
        val imagen: ImageView = view.findViewById(R.id.imagen)
        val idioma: ImageView = view.findViewById(R.id.idioma)
        val foil: ImageView = view.findViewById(R.id.foil)
        val distance: TextView = view.findViewById(R.id.distance)
        val user: TextView = view.findViewById(R.id.user)


        nombre.setText(anuncio.nombre)
        viewModel.getCard(anuncio.carta).observeForever{
            nombre.setText(it.getString("name"))
            edicion.text = it.getString("set_name")
            val precio: Double
            if(anuncio.modoPrecio == 1){
                precio = anuncio.precio
            }else {
                val precios = it.getJSONObject("prices")
                val precioBase =
                    (if (anuncio.foil) precios.getString("eur_foil") else precios.getString("eur")).toDouble()
                precio = precioBase*(1+(anuncio.porcentaje/100)) + anuncio.comision
            }
            precioTV.text = String.format("%.2f", precio)+"€"
            val uri = it.getJSONObject("image_uris").getString("normal")
            Picasso.get()
                .load(uri) // Reemplaza con el campo adecuado de tu clase Anuncio
                .into(imagen)
        }
        view.backgroundTintList =
            when(anuncio.estado){
                1 -> ColorStateList.valueOf(resources.getColor(R.color.mint))
                2 -> ColorStateList.valueOf(resources.getColor(R.color.nearMint))
                3 -> ColorStateList.valueOf(resources.getColor(R.color.excellent))
                4 -> ColorStateList.valueOf(resources.getColor(R.color.good))
                5 -> ColorStateList.valueOf(resources.getColor(R.color.lightplayed))
                6 -> ColorStateList.valueOf(resources.getColor(R.color.played))
                7 -> ColorStateList.valueOf(resources.getColor(R.color.poor))
                else -> ColorStateList.valueOf(resources.getColor(R.color.grey))
            }
        idioma.setImageResource(
            when(anuncio.idioma){
                1 -> R.drawable.ingles
                2 -> R.drawable.espana
                3 -> R.drawable.portugal
                4 -> R.drawable.francia
                5 -> R.drawable.italia
                6 -> R.drawable.china
                7 -> R.drawable.japon
                8 -> R.drawable.corea
                9 -> R.drawable.rusia
                else -> R.drawable.ingles
            }
        )
        distance.text = "${anuncio.distance}Km"
        user.text = anuncio.userName
        user.setTextColor(resources.getColor(if(anuncio.userScore >= 4) R.color.nearMint else if (anuncio.userScore >= 2.5) R.color.good else R.color.poor))
        user.setOnClickListener{
            onUser?.let { it1 -> it1() }
        }
        foil.visibility = if(anuncio.foil) View.VISIBLE else View.GONE
    }
}