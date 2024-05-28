package com.kiwistudio.spelltrader.ui.settings

import android.annotation.SuppressLint
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.kiwistudio.spelltrader.MainViewModel
import com.kiwistudio.spelltrader.R
import com.kiwistudio.spelltrader.entities.Ubicacion
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.util.Locale

class DialogUbicacion(val ubicacion: Ubicacion?): DialogFragment() {
    private lateinit var viewModel: MainViewModel
    private lateinit var nombre: EditText
    private lateinit var calle: TextView
    private lateinit var btnOk: Button
    private lateinit var mapView: MapView
    private var latitud = 0.0
    private var longitud = 0.0
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    var onConfirmListener: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_ubicacion, container, false)
    }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        btnOk = view.findViewById(R.id.btnOk)
        nombre = view.findViewById(R.id.nombreUbicacion)
        calle = view.findViewById(R.id.calle)
        btnOk.setOnClickListener {
            enviar()
        }
        if(ubicacion != null){
            nombre.setText(ubicacion.nombre)
            latitud = ubicacion.altitud!!
            longitud = ubicacion.longitud!!
        }
        mapView = view.findViewById(R.id.mapView)
        mapView.setTileSource(TileSourceFactory.MAPNIK)

        // Habilitar los controles de zoom
        mapView.setBuiltInZoomControls(true)
        mapView.setMultiTouchControls(true)

        // Configurar el punto inicial del mapa y la configuración del zoom
        val startPoint = GeoPoint(latitud, longitud) // Ejemplo: París, Torre Eiffel
        mapView.controller.setZoom(15.0)
        mapView.controller.setCenter(startPoint)

        // Añadir un marcador en la ubicación seleccionada
        val marker = Marker(mapView)
        marker.position = startPoint
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        mapView.overlays.add(marker)

        if(ubicacion == null) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    latitud = it.latitude
                    longitud = it.longitude
                    val geoPoint = GeoPoint(latitud, longitud)
                    marker.position = geoPoint
                    mapView.controller.animateTo(geoPoint)
                    Log.d("MAP", "Lat: $latitud Lon: $longitud")
                } ?: run {
                    Log.d("MAP", "NO LOCATION")
                    latitud = 0.0
                    longitud = 0.0
                }
            }
        }else{
            marker.title = ubicacion.nombre
        }

        mapView.addMapListener(object : MapListener {
            override fun onScroll(event: ScrollEvent?): Boolean {
                val center = mapView.mapCenter as GeoPoint
                marker.position = center
                latitud = center.latitude
                longitud = center.longitude
                mapView.invalidate()
                return false
            }

            override fun onZoom(event: ZoomEvent?): Boolean {
                return false
            }
        })
        nombre.addTextChangedListener{
            marker.title = nombre.text.toString()
        }
    }
    private fun enviar(){
        val ubi = Ubicacion(
            nombre = nombre.text.toString(),
            altitud = latitud,
            longitud = longitud
        )
        if(ubicacion != null){
            ubi.id = ubicacion.id
            viewModel.updateUbicacion(ubi).observeForever{
                if(it != null){
                    onConfirmListener?.invoke()
                    dismiss()
                }
            }
        }else{
            viewModel.createUbicacion(ubi).observeForever{
                if(it != null){
                    onConfirmListener?.invoke()
                    dismiss()
                }
            }
        }
    }
}