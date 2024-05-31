
package com.kiwistudio.spelltrader.ui.anuncios

import android.app.AlertDialog
import android.content.Context
import android.content.res.Resources
import android.location.Geocoder
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.Space
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kiwistudio.spelltrader.MainViewModel
import com.kiwistudio.spelltrader.R
import com.kiwistudio.spelltrader.entities.Ubicacion
import com.kiwistudio.spelltrader.entities.UserDetail
import com.kiwistudio.spelltrader.entities.Valoraciones
import com.kiwistudio.spelltrader.ui.settings.DialogUbicacion
import com.kiwistudio.spelltrader.ui.settings.Ubicaciones
import java.util.Locale

class PerfilViwe : Fragment() {
    private lateinit var viewModel: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_perfil_viwe, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        recargar(view)
    }

    private fun recargar(view: View){
        viewModel.getUser().observeForever{
            if(it.body != null) {
                val ubicacionJS: MutableList<Ubicacion> = mutableListOf()
                val listaUbicaciones = it.body.getJSONArray("ubicaciones")
                for(i in 0 until listaUbicaciones.length()){
                    ubicacionJS.add(Ubicacion(
                        nombre = listaUbicaciones.getJSONObject(i).getString("nombre"),
                        id = listaUbicaciones.getJSONObject(i).getInt("id"),
                        altitud = listaUbicaciones.getJSONObject(i).getDouble("altitud"),
                        longitud = listaUbicaciones.getJSONObject(i).getDouble("longitud")
                    ))
                }
                val valoracionesJS: MutableList<Valoraciones> = mutableListOf()
                val listaValoraciones = it.body.getJSONArray("valoraciones")
                for(i in 0 until listaValoraciones.length()){
                    valoracionesJS.add(Valoraciones(
                        username = listaValoraciones.getJSONObject(i).getString("username"),
                        userid = listaValoraciones.getJSONObject(i).getInt("userid"),
                        valoracion = listaValoraciones.getJSONObject(i).getString("valoracion"),
                        score = listaValoraciones.getJSONObject(i).getDouble("score"),
                        fecha = listaValoraciones.getJSONObject(i).getString("fecha")
                    ))
                }
                val user = UserDetail(
                    id = it.body.getInt("id"),
                    nombre = it.body.getString("nombre"),
                    ventas = it.body.getInt("ventas"),
                    compras = it.body.getInt("compras"),
                    cambios = it.body.getInt("cambios"),
                    score = it.body.getDouble("score"),
                    ubicaciones = ubicacionJS,
                    valoraciones = valoracionesJS
                )
                view.findViewById<TextView>(R.id.ventas).text = "Ventas: " + user.ventas
                view.findViewById<TextView>(R.id.compas).text = "Compras: " + user.compras
                view.findViewById<TextView>(R.id.intercambios).text = "Cambios: " + user.cambios
                view.findViewById<TextView>(R.id.score).text = user.score.toString()
                view.findViewById<TextView>(R.id.username).text = user.nombre
                view.findViewById<TextView>(R.id.score).setTextColor(resources.getColor(if(user.score >= 4) R.color.nearMint else if (user.score >= 2.5) R.color.good else R.color.poor))
                val recyclerView: RecyclerView = view.findViewById(R.id.ubicaciones)
                recyclerView.layoutManager = LinearLayoutManager(requireContext())
                val adapter = UbicacionAdapter(user.ubicaciones,
                    onCardClick = { data ->
                        // Código a ejecutar cuando se pulse el CardView
                        val dialog = DialogVerUbicacion(data)
                        dialog.show(parentFragmentManager, "EditUbicacionDialog")
                    })
                recyclerView.adapter = adapter
                val recyclerViewVloraciones: RecyclerView = view.findViewById(R.id.valoraciones)
                recyclerViewVloraciones.layoutManager = LinearLayoutManager(requireContext())
                val adapterValoracion = ValoracionAdapter(user.valoraciones, resources,
                    onUserClick = { data ->
                        viewModel.userIdView = data
                        recargar(view)
                    })
                recyclerViewVloraciones.adapter = adapterValoracion
                view.findViewById<Button>(R.id.btnCatalogo).setOnClickListener{
                    viewModel.filtroUSer = user.id
                    viewModel.filtroUserName = user.nombre
                    findNavController().navigate(R.id.action_perfilViwe_to_main2)
                }
            }
        }
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.nombre)
        val calle: TextView = itemView.findViewById(R.id.calle)
        val context: Context
            get() = itemView.context
    }
    class UbicacionAdapter(private val dataList: List<Ubicacion>,
                           private val onCardClick: (Ubicacion) -> Unit): RecyclerView.Adapter<MyViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.adapter_ubicacion, parent, false)
            return MyViewHolder(itemView)
        }
        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val data = dataList[position]

            val geocoder = Geocoder(holder.context, Locale.getDefault())
            val addresses = data.altitud?.let { data.longitud?.let { it1 ->
                geocoder.getFromLocation(it,
                    it1, 1)
            } }
            if (addresses != null) {
                if (addresses.isNotEmpty()) {
                    val address = addresses[0]
                    val addressText = address.getAddressLine(0)
                    holder.calle.text = addressText
                } else {
                    holder.calle.text = "Address not found"
                }
            }
            holder.title.text = data.nombre
            holder.itemView.setOnClickListener {
                onCardClick(data)
            }
        }
        override fun getItemCount(): Int {
            return dataList.size
        }
    }

    class MyViewHolder2(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombre: TextView = itemView.findViewById(R.id.usuario)
        val fecha: TextView = itemView.findViewById(R.id.fecha)
        val puntuacion: TextView = itemView.findViewById(R.id.puntuacion)
        val valoracion: TextView = itemView.findViewById(R.id.valoracion)
        val context: Context
            get() = itemView.context
    }
    class ValoracionAdapter(private val dataList: List<Valoraciones>, private var resources: Resources,
                            private val onUserClick: (Int) -> Unit): RecyclerView.Adapter<MyViewHolder2>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder2 {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.adapter_valoracion, parent, false)
            return MyViewHolder2(itemView)
        }
        override fun onBindViewHolder(holder: MyViewHolder2, position: Int) {
            val data = dataList[position]
            holder.nombre.text = data.username
            holder.fecha.text = data.fecha
            holder.puntuacion.text = data.score.toInt().toString()
            holder.puntuacion.setTextColor(resources.getColor(if(data.score >= 4) R.color.nearMint else if (data.score >= 2.5) R.color.good else R.color.poor))
            holder.valoracion.text = data.valoracion
            holder.nombre.setOnClickListener {
                onUserClick(data.userid)
            }
        }
        override fun getItemCount(): Int {
            return dataList.size
        }
    }

}