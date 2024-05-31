package com.kiwistudio.spelltrader.ui.settings

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Space
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.view.marginStart
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kiwistudio.spelltrader.MainViewModel
import com.kiwistudio.spelltrader.R
import com.kiwistudio.spelltrader.entities.Ubicacion
import com.kiwistudio.spelltrader.entities.UserDetail
import com.kiwistudio.spelltrader.entities.Valoraciones
import com.kiwistudio.spelltrader.ui.anuncios.DialogVerUbicacion

class Historial : Fragment() {
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_historial, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        viewModel.getHistorico().observeForever{
            if(it.body != null) {
                val valoracionesJS: MutableList<Valoraciones> = mutableListOf()
                val listaValoraciones = it.body.getJSONArray("valoraciones")
                for(i in 0 until listaValoraciones.length()){
                    valoracionesJS.add(
                        Valoraciones(
                            username = listaValoraciones.getJSONObject(i).getString("username"),
                            userid = listaValoraciones.getJSONObject(i).getInt("userid"),
                            valoracion = listaValoraciones.getJSONObject(i).getString("valoracion"),
                            score = listaValoraciones.getJSONObject(i).getDouble("score"),
                            fecha = listaValoraciones.getJSONObject(i).getString("fecha"),
                            tipo = listaValoraciones.getJSONObject(i).getInt("tipo"),
                            valor = listaValoraciones.getJSONObject(i).getDouble("valor")
                        )
                    )
                }
                val recyclerViewVloraciones: RecyclerView = view.findViewById(R.id.valoraciones)
                recyclerViewVloraciones.layoutManager = LinearLayoutManager(requireContext())
                val adapterValoracion = ValoracionAdapter(valoracionesJS, resources,
                    onUserClick = { data ->
                        viewModel.userIdView = data
                        findNavController().navigate(R.id.action_historial2_to_perfiles)
                    })
                recyclerViewVloraciones.adapter = adapterValoracion
            }
        }
    }

    class MyViewHolder2(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombre: TextView = itemView.findViewById(R.id.usuario)
        val fecha: TextView = itemView.findViewById(R.id.fecha)
        val puntuacion: TextView = itemView.findViewById(R.id.puntuacion)
        val valoracion: TextView = itemView.findViewById(R.id.valoracion)
        val valor: TextView = itemView.findViewById(R.id.valor)
        val marginCompra: Space = itemView.findViewById(R.id.margin_compra)
        val marginVenta: Space = itemView.findViewById(R.id.margin_venta)
        val tarjeta: CardView = itemView.findViewById(R.id.tarjeta)
        val context: Context
            get() = itemView.context
    }
    class ValoracionAdapter(private val dataList: List<Valoraciones>, private var resources: Resources,
                            private val onUserClick: (Int) -> Unit): RecyclerView.Adapter<MyViewHolder2>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder2 {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.adapter_valoracion_ext, parent, false)
            return MyViewHolder2(itemView)
        }
        override fun onBindViewHolder(holder: MyViewHolder2, position: Int) {
            val data = dataList[position]
            holder.nombre.text = data.username
            holder.fecha.text = data.fecha
            holder.puntuacion.text = data.score.toInt().toString()
            holder.puntuacion.setTextColor(resources.getColor(if(data.score >= 4) R.color.nearMint else if (data.score >= 2.5) R.color.good else R.color.poor))
            holder.valoracion.text = data.valoracion
            holder.valor.text = String.format("%.2f", data.valor) + "€"
            holder.nombre.setOnClickListener {
                onUserClick(data.userid)
            }
            holder.tarjeta.backgroundTintList =
                when(data.tipo){
                    1 -> ColorStateList.valueOf(resources.getColor(R.color.mint)) // Compro yo
                    2 -> ColorStateList.valueOf(resources.getColor(R.color.good)) // vendo yo
                    else -> ColorStateList.valueOf(resources.getColor(R.color.grey))
                }
            holder.marginCompra.visibility = if(data.tipo == 1) View.VISIBLE else View.GONE
            holder.marginVenta.visibility = if(data.tipo == 1) View.GONE else View.VISIBLE
        }
        override fun getItemCount(): Int {
            return dataList.size
        }
    }
}