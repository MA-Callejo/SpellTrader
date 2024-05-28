package com.kiwistudio.spelltrader.ui.settings

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
import com.kiwistudio.spelltrader.entities.Ubicacion
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class DialogUbicacion(val ubicacion: Ubicacion?): DialogFragment() {
    private lateinit var viewModel: MainViewModel
    private lateinit var nombre: EditText
    private lateinit var calle: EditText
    private lateinit var provincia: EditText
    private lateinit var poblacion: EditText
    private lateinit var cp: EditText
    private lateinit var numero: EditText
    private lateinit var btnOk: Button
    private lateinit var mapView: MapView

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
        calle = view.findViewById(R.id.calle)
        provincia = view.findViewById(R.id.provincia)
        poblacion = view.findViewById(R.id.poblacion)
        numero = view.findViewById(R.id.numero)
        cp = view.findViewById(R.id.cp)
        btnOk.setOnClickListener {
            enviar()
        }
        if(ubicacion != null){
            numero.setText(ubicacion.numero.toString())
            nombre.setText(ubicacion.nombre)
            calle.setText(ubicacion.calle)
            provincia.setText(ubicacion.provincia.toString())
            poblacion.setText(ubicacion.poblacion)
            cp.setText(ubicacion.cp)
        }
        mapView = view.findViewById(R.id.mapView)
        mapView.setTileSource(TileSourceFactory.MAPNIK)

        // Habilitar los controles de zoom
        mapView.setBuiltInZoomControls(true)
        mapView.setMultiTouchControls(true)

        // Configurar el punto inicial del mapa y la configuración del zoom
        val startPoint = GeoPoint(48.8583, 2.2944) // Ejemplo: París, Torre Eiffel
        mapView.controller.setZoom(15.0)
        mapView.controller.setCenter(startPoint)

        // Añadir un marcador en la ubicación seleccionada
        val marker = Marker(mapView)
        marker.position = startPoint
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        marker.title = "Torre Eiffel"
        mapView.overlays.add(marker)
    }
    private fun enviar(){
        val ubi = Ubicacion(
            cp = cp.text.toString(),
            nombre = nombre.text.toString(),
            calle = calle.text.toString(),
            provincia = provincia.text.toString().toInt(),
            numero = numero.text.toString().toInt(),
            poblacion = poblacion.text.toString()
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