package com.kiwistudio.spelltrader.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.ViewModelProvider
import com.kiwistudio.spelltrader.Autentificacion
import com.kiwistudio.spelltrader.MainViewModel
import com.kiwistudio.spelltrader.R

class Main : Fragment() {
    private lateinit var viewModel: MainViewModel
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
        view.findViewById<Button>(R.id.logout).setOnClickListener{
            viewModel.securePreferenceHelper.clearUserCredentials()
            val intent = Intent(requireContext(), Autentificacion::class.java)
            startActivity(intent)
            activity?.finish()
        }
    }
}