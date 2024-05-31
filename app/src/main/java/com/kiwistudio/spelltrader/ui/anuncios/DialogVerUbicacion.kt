package com.kiwistudio.spelltrader.ui.anuncios

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

class DialogVerUbicacion(val ubicacion: Ubicacion): DialogFragment() {
    private lateinit var viewModel: MainViewModel
    private lateinit var nombre: TextView
    private lateinit var calle: TextView
    private lateinit var mapView: MapView
    private var latitud = 0.0
    private var longitud = 0.0
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_ver_ubicacion, container, false)
    }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        nombre = view.findViewById(R.id.nombreUbicacion)
        calle = view.findViewById(R.id.calle)
        nombre.setText(ubicacion.nombre)
        latitud = ubicacion.altitud!!
        longitud = ubicacion.longitud!!
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
    }
}