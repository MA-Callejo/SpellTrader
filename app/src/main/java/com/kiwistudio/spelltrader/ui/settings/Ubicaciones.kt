package com.kiwistudio.spelltrader.ui.settings

import android.annotation.SuppressLint
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
        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val dataList = listOf(
            Ubicacion("Geeklogia"),
            Ubicacion("CartoonCorp"),
            Ubicacion("Draconomico")
        )
        val adapter = UbicacionAdapter(dataList,
            onCardClick = { data ->
                // Código a ejecutar cuando se pulse el CardView
                Toast.makeText(requireContext(), "Card clicked: ${data.nombre}", Toast.LENGTH_SHORT).show()
            },
            onDelete = { data ->
                Toast.makeText(requireContext(), "Delete: ${data.nombre}", Toast.LENGTH_SHORT).show()
            },
            onEdit = { data ->
                Toast.makeText(requireContext(), "Edit: ${data.nombre}", Toast.LENGTH_SHORT).show()
            })
        recyclerView.adapter = adapter
    }
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.nombreUbicacion)
        val calle: TextView = itemView.findViewById(R.id.calle)
        val poblacion: TextView = itemView.findViewById(R.id.poblacion)
        val editar: ImageButton = itemView.findViewById(R.id.btnEditarUbicacion)
        val borrar: ImageButton = itemView.findViewById(R.id.btnBorrarUbicacion)
    }
    class UbicacionAdapter(private val dataList: List<Ubicacion>,
                           private val onCardClick: (Ubicacion) -> Unit,
                           private val onEdit: (Ubicacion) -> Unit,
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
            holder.editar.setOnClickListener{
                onEdit(data)
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