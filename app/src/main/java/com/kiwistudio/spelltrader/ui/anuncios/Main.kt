package com.kiwistudio.spelltrader.ui.anuncios

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.content.res.Resources
import android.location.Location
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
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.kiwistudio.spelltrader.MainViewModel
import com.kiwistudio.spelltrader.R
import com.kiwistudio.spelltrader.entities.AnuncioFull
import com.kiwistudio.spelltrader.entities.Filtros
import com.squareup.picasso.Picasso

class Main : Fragment() {
    private lateinit var viewModel: MainViewModel
    private var filtro = 0
    private lateinit var btnPacks: Button
    private lateinit var btnSingles: Button
    private lateinit var searchBar: EditText
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var latitud: Double = 0.0
    private var longitud: Double = 0.0
    private var filtroUser: Int? = null
    private var filtroUserName: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        if(viewModel.filtroUSer != null){
            filtroUser = viewModel.filtroUSer
            filtroUserName = viewModel.filtroUserName
            viewModel.filtroUSer = null
        }
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
        searchBar = view.findViewById(R.id.search)
        view.findViewById<ImageButton>(R.id.btnSearch).setOnClickListener{
            recargar(view)
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                latitud = it.latitude
                longitud = it.longitude
            } ?: run {
                latitud = 0.0
                longitud = 0.0
            }
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
        val filtros = Filtros(latitud, longitud, searchBar.text.toString(), 0, filtroUser)
        viewModel.getAnuncios(filtros).observeForever { it ->
            val datas: MutableList<AnuncioFull> = mutableListOf()
            val anunciosResponse = it.body?.getJSONArray("anuncios")
            if (anunciosResponse != null) {
                for (i in 0 until anunciosResponse.length()) {
                    datas.add(
                        AnuncioFull(
                            id = anunciosResponse.getJSONObject(i).getInt("id"),
                            nombre = anunciosResponse.getJSONObject(i).getString("nombre"),
                            fkPropietario = anunciosResponse.getJSONObject(i).getInt("fkPropietario"),
                            carta = anunciosResponse.getJSONObject(i).getString("carta"),
                            estado = anunciosResponse.getJSONObject(i).getInt("estado"),
                            modoPrecio = anunciosResponse.getJSONObject(i).getInt("modoPrecio"),
                            precio = anunciosResponse.getJSONObject(i).getDouble("precio"),
                            porcentaje = anunciosResponse.getJSONObject(i).getDouble("porcentaje"),
                            comision = anunciosResponse.getJSONObject(i).getDouble("comision"),
                            idioma = anunciosResponse.getJSONObject(i).getInt("idioma"),
                            foil = (anunciosResponse.getJSONObject(i).getInt("foil") == 1),
                            userName = anunciosResponse.getJSONObject(i).getString("username"),
                            userScore = anunciosResponse.getJSONObject(i).getDouble("score"),
                            distance = anunciosResponse.getJSONObject(i).getDouble("distancia")
                        )
                    )
                }
            }
            val adapter = AnuncioAdapter(datas, viewModel, resources,
                onCardClick = { data ->
                    // Código a ejecutar cuando se pulse el CardView
                    val dialog = DialogAnuncio(data)
                    dialog.onUser = {
                        viewModel.userIdView = data.fkPropietario
                        findNavController().navigate(R.id.action_main2_to_perfilViwe)
                    }
                    dialog.onReserve = {
                        viewModel.reservar(data.id, it).observeForever{
                            Toast.makeText(context, "Añadido a las reservas", Toast.LENGTH_SHORT).show()
                        }
                    }
                    dialog.show(parentFragmentManager, "AnuncioDialog")
                },
                onUserClick = { data ->
                    viewModel.userIdView = data
                    findNavController().navigate(R.id.action_main2_to_perfilViwe)
                })
            recyclerView.adapter = adapter
        }
    }
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.nombre)
        val descripcion: TextView = itemView.findViewById(R.id.descripcion)
        val precio: TextView = itemView.findViewById(R.id.precio)
        val edicion: TextView = itemView.findViewById(R.id.edicion)
        val imagen: ImageView = itemView.findViewById(R.id.imagen)
        val idioma: ImageView = itemView.findViewById(R.id.idioma)
        val graded: ImageView = itemView.findViewById(R.id.graded)
        val foil: ImageView = itemView.findViewById(R.id.foil)
        val distance: TextView = itemView.findViewById(R.id.distance)
        val user: TextView = itemView.findViewById(R.id.user)
        val context: Context
            get() = itemView.context
    }
    class AnuncioAdapter(private val dataList: List<AnuncioFull>,
                         private val viewModel: MainViewModel,
                         private var resources: Resources,
                         private val onCardClick: (AnuncioFull) -> Unit,
                         private val onUserClick: (Int) -> Unit) : RecyclerView.Adapter<MyViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.anuncio_ver, parent, false)
            return MyViewHolder(itemView)
        }
        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val data = dataList[position]
            viewModel.getCard(data.carta).observeForever{
                holder.title.text = it.getString("name")
                holder.edicion.text = it.getString("set_name")
                val precio: Double
                if(data.modoPrecio == 1){
                    precio = data.precio
                }else {
                    val precios = it.getJSONObject("prices")
                    val precioBase =
                        (if (data.foil) precios.getString("eur_foil") else precios.getString("eur")).toDouble()
                    precio = precioBase*(1+(data.porcentaje/100)) + data.comision
                }
                holder.precio.text = String.format("%.2f", precio)+"€"
                val uri = it.getJSONObject("image_uris").getString("art_crop")
                Picasso.get()
                    .load(uri) // Reemplaza con el campo adecuado de tu clase Anuncio
                    .into(holder.imagen)
            }
            holder.itemView.backgroundTintList =
                when(data.estado){
                    1 -> ColorStateList.valueOf(resources.getColor(R.color.mint))
                    2 -> ColorStateList.valueOf(resources.getColor(R.color.nearMint))
                    3 -> ColorStateList.valueOf(resources.getColor(R.color.excellent))
                    4 -> ColorStateList.valueOf(resources.getColor(R.color.good))
                    5 -> ColorStateList.valueOf(resources.getColor(R.color.lightplayed))
                    6 -> ColorStateList.valueOf(resources.getColor(R.color.played))
                    7 -> ColorStateList.valueOf(resources.getColor(R.color.poor))
                    else -> ColorStateList.valueOf(resources.getColor(R.color.grey))
                }
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
            holder.distance.text = "${data.distance}Km"
            holder.user.text = data.userName
            holder.user.setTextColor(resources.getColor(if(data.userScore >= 4) R.color.nearMint else if (data.userScore >= 2.5) R.color.good else R.color.poor))
            holder.foil.visibility = if(data.foil) View.VISIBLE else View.GONE
            holder.itemView.setOnClickListener {
                onCardClick(data)
            }
            holder.user.setOnClickListener{
                onUserClick(data.fkPropietario)
            }
        }
        override fun getItemCount(): Int {
            return dataList.size
        }
    }
}