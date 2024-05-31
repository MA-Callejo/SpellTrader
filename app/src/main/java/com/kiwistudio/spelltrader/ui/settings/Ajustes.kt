package com.kiwistudio.spelltrader.ui.settings

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.kiwistudio.spelltrader.Autentificacion
import com.kiwistudio.spelltrader.MainViewModel
import com.kiwistudio.spelltrader.R
class Ajustes : Fragment() {
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ajustes, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        view.findViewById<CardView>(R.id.perfil).setOnClickListener{
            findNavController().navigate(R.id.action_ajustes2_to_perfil2)
        }
        view.findViewById<CardView>(R.id.ubicaciones).setOnClickListener{
            findNavController().navigate(R.id.action_ajustes2_to_ubicaciones2)
        }
        view.findViewById<CardView>(R.id.notificaciones).setOnClickListener{
            findNavController().navigate(R.id.action_ajustes2_to_notificaciones2)
        }
        view.findViewById<CardView>(R.id.historial).setOnClickListener{
            findNavController().navigate(R.id.action_ajustes2_to_historial2)
        }
        view.findViewById<CardView>(R.id.importar).setOnClickListener{
            findNavController().navigate(R.id.action_ajustes2_to_importar2)
        }
        view.findViewById<CardView>(R.id.historial).setOnClickListener{
            findNavController().navigate(R.id.action_ajustes2_to_historial2)
        }
    }
}