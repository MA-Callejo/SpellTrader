package com.kiwistudio.spelltrader.ui.settings

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.kiwistudio.spelltrader.MainViewModel
import com.kiwistudio.spelltrader.R
import com.kiwistudio.spelltrader.entities.Ubicacion

class Ubicaciones : Fragment() {
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ubicaciones, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        view.findViewById<FloatingActionButton>(R.id.btnAdd).setOnClickListener{
            val dialog = DialogUbicacion(null)
            dialog.onConfirmListener = {
                recargar(view)
            }
            dialog.show(parentFragmentManager, "EditUbicacionDialog")
        }
        recargar(view)
    }
    private fun recargar(view: View){
        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        viewModel.getUbicaciones(viewModel.userId).observeForever { it ->
            val datas: MutableList<Ubicacion> = mutableListOf()
            val ubicacionesResponse = it.body?.getJSONArray("ubicaciones")
            if (ubicacionesResponse != null) {
                for (i in 0 until ubicacionesResponse.length()) {
                    datas.add(Ubicacion(
                        nombre = ubicacionesResponse.getJSONObject(i).getString("nombre"),
                        calle = ubicacionesResponse.getJSONObject(i).getString("calle"),
                        poblacion = ubicacionesResponse.getJSONObject(i).getString("poblacion"),
                        cp = ubicacionesResponse.getJSONObject(i).getString("cp"),
                        numero = ubicacionesResponse.getJSONObject(i).getInt("numero"),
                        id = ubicacionesResponse.getJSONObject(i).getInt("id"),
                        provincia = ubicacionesResponse.getJSONObject(i).getInt("provincia")
                    ))
                }
            }
            val adapter = UbicacionAdapter(datas,
                onCardClick = { data ->
                    // Código a ejecutar cuando se pulse el CardView
                    val dialog = DialogUbicacion(data)
                    dialog.onConfirmListener = {
                        recargar(view)
                    }
                    dialog.show(parentFragmentManager, "EditUbicacionDialog")
                },
                onDelete = { data ->
                    val builder = AlertDialog.Builder(requireActivity())
                    builder.setTitle("Confirmacion")
                    builder.setMessage("¿Esta seguro de que desea eliminar esta ubicacion?")
                    builder.setPositiveButton("Si"){ _, _: Int ->
                        viewModel.deleteUbicacion(data.id).observeForever{_ ->
                            if(it != null){
                                recargar(view)
                            }
                        }
                    }
                    builder.setNegativeButton("No"){ dialogInterface, _: Int ->
                        dialogInterface.dismiss()
                    }
                    builder.show()
                })
            recyclerView.adapter = adapter
        }
    }
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.nombreUbicacion)
        val calle: TextView = itemView.findViewById(R.id.calle)
        val poblacion: TextView = itemView.findViewById(R.id.poblacion)
        val borrar: ImageButton = itemView.findViewById(R.id.btnBorrarUbicacion)
    }
    class UbicacionAdapter(private val dataList: List<Ubicacion>,
                           private val onCardClick: (Ubicacion) -> Unit,
                           private val onDelete: (Ubicacion) -> Unit) : RecyclerView.Adapter<MyViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.ubicacion_editar, parent, false)
            return MyViewHolder(itemView)
        }
        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val data = dataList[position]
            holder.title.text = data.nombre
            holder.calle.text = "${data.calle}, Nº${data.numero}"
            holder.poblacion.text = "${data.poblacion} (${data.cp}), ${data.provincia}"
            holder.itemView.setOnClickListener {
                onCardClick(data)
            }
            holder.borrar.setOnClickListener{
                onDelete(data)
            }
        }
        override fun getItemCount(): Int {
            return dataList.size
        }
    }
}