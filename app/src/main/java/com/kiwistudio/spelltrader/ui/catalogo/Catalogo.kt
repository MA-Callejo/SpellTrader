package com.kiwistudio.spelltrader.ui.catalogo

import android.app.AlertDialog
import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Visibility
import com.kiwistudio.spelltrader.MainViewModel
import com.kiwistudio.spelltrader.R
import com.kiwistudio.spelltrader.entities.Anuncio

class Catalogo : Fragment() {
    private lateinit var viewModel: MainViewModel
    private var filtro = 0
    private lateinit var btnPacks: Button
    private lateinit var btnSingles: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_catalogo, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        view.findViewById<ImageButton>(R.id.btnSearch).setOnClickListener{
            val nombre = view.findViewById<EditText>(R.id.search).text
            Toast.makeText(context, nombre, Toast.LENGTH_SHORT).show()
        }
        btnSingles = view.findViewById(R.id.btnSingles)
        btnPacks = view.findViewById(R.id.btnPacks)
        btnSingles.setOnClickListener{
            filtro = if(filtro == 1) 0 else 1
            setFiltros()
        }
        btnPacks.setOnClickListener{
            filtro = if(filtro == 2) 0 else 2
            setFiltros()
        }
        recargar(view)
    }
    fun setFiltros(){
        when (filtro) {
            0 -> {
                btnSingles.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.inactive))
                btnPacks.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.inactive))
            }
            1 -> {
                btnSingles.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.active))
                btnPacks.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.inactive))
            }
            2 -> {
                btnSingles.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.inactive))
                btnPacks.backgroundTintList = ColorStateList.valueOf(resources.getColor(R.color.active))
            }
        }
    }

    private fun recargar(view: View){
        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        viewModel.getMisAnuncios().observeForever { it ->
            val datas: MutableList<Anuncio> = mutableListOf()
            val anunciosResponse = it.body?.getJSONArray("anuncios")
            if (anunciosResponse != null) {
                for (i in 0 until anunciosResponse.length()) {
                    datas.add(
                        Anuncio(
                            id = anunciosResponse.getJSONObject(i).getInt("id"),
                            nombre = anunciosResponse.getJSONObject(i).getString("nombre"),
                            descripcion = anunciosResponse.getJSONObject(i).getString("descripcion"),
                            fkPropietario = anunciosResponse.getJSONObject(i).getInt("fkPropietario"),
                            carta = anunciosResponse.getJSONObject(i).getString("carta"),
                            estado = anunciosResponse.getJSONObject(i).getInt("estado"),
                            modoPrecio = anunciosResponse.getJSONObject(i).getInt("modoPrecio"),
                            precio = anunciosResponse.getJSONObject(i).getDouble("precio"),
                            porcentaje = anunciosResponse.getJSONObject(i).getDouble("porcentaje"),
                            comision = anunciosResponse.getJSONObject(i).getDouble("comision"),
                            idioma = anunciosResponse.getJSONObject(i).getInt("idioma"),
                            foil = (anunciosResponse.getJSONObject(i).getInt("foil") == 1),
                    ))
                }
            }
            val adapter = AnuncioAdapter(datas, viewModel,
                onCardClick = { data ->
                    // Código a ejecutar cuando se pulse el CardView
                    val dialog = DialogMiAnuncio(data)
                    dialog.onConfirmListener = {
                        recargar(view)
                    }
                    dialog.show(parentFragmentManager, "EditAnuncioDialog")
                },
                onDelete = { data ->
                    val builder = AlertDialog.Builder(requireActivity())
                    builder.setTitle("Confirmacion")
                    builder.setMessage("¿Esta seguro de que desea eliminar este anuncio?")
                    builder.setPositiveButton("Si") { _, _: Int ->
                        viewModel.deleteUbicacion(data.id).observeForever { it ->
                            if (it != null) {
                                recargar(view)
                            }
                        }
                    }
                    builder.setNegativeButton("No") { dialogInterface, _: Int ->
                        dialogInterface.dismiss()
                    }
                    builder.show()
                })
            recyclerView.adapter = adapter
        }
    }
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.nombre)
        val borrar: ImageButton = itemView.findViewById(R.id.btnBorrar)
        val descripcion: TextView = itemView.findViewById(R.id.descripcion)
        val precio: TextView = itemView.findViewById(R.id.precio)
        val edicion: TextView = itemView.findViewById(R.id.edicion)
        val imagen: ImageView = itemView.findViewById(R.id.imagen)
        val idioma: ImageView = itemView.findViewById(R.id.idioma)
        val graded: ImageView = itemView.findViewById(R.id.graded)
        val foil: ImageView = itemView.findViewById(R.id.foil)
        val context: Context
            get() = itemView.context
    }
    class AnuncioAdapter(private val dataList: List<Anuncio>,
                           private val viewModel: MainViewModel,
                           private val onCardClick: (Anuncio) -> Unit,
                           private val onDelete: (Anuncio) -> Unit) : RecyclerView.Adapter<MyViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.anuncio_editar, parent, false)
            return MyViewHolder(itemView)
        }
        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val data = dataList[position]
            viewModel.getCard(data.carta).observeForever{
                holder.title.text = it.getString("name")
                holder.edicion.text = it.getString("set_name")
                var precio = 0.0
                if(data.modoPrecio == 1){
                    precio = data.precio
                }else {
                    val precios = it.getJSONObject("prices")
                    var precioBase =
                        (if (data.foil) precios.getString("eur_foil") else precios.getString("eur")).toDouble()
                    precio = precioBase*(1+(data.porcentaje/100)) + data.comision
                }
                holder.precio.text = String.format("%.2f", precio)+"€"
                var uri = it.getJSONObject("image_uris").getString("art_crop")
            }
            holder.itemView.setBackgroundColor(
                when(data.estado){
                    1 -> R.color.mint
                    2 -> R.color.nearMint
                    3 -> R.color.excellent
                    4 -> R.color.good
                    5 -> R.color.lightplayed
                    6 -> R.color.played
                    7 -> R.color.poor
                    else -> R.color.grey
                })
            holder.idioma.setImageResource(
                when(data.idioma){
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
            holder.foil.visibility = if(data.foil) View.VISIBLE else View.GONE
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