package com.kiwistudio.spelltrader.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.kiwistudio.spelltrader.LogInViewModel
import com.kiwistudio.spelltrader.R

class SingIn : Fragment() {
    private lateinit var viewModel: LogInViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sing_in, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[LogInViewModel::class.java]
        view.findViewById<TextView>(R.id.logInText).setOnClickListener{
            findNavController().navigate(R.id.action_singIn2_to_logIn)
        }
        view.findViewById<Button>(R.id.enter).setOnClickListener{
            val pass = view.findViewById<EditText>(R.id.password).text.toString()
            val pass2 = view.findViewById<EditText>(R.id.password2).text.toString()
            if(pass != pass2){
                Toast.makeText(context, "Las contraseñas han de coincidir", Toast.LENGTH_SHORT).show()
            }else{
                viewModel.singIn(
                    view.findViewById<EditText>(R.id.email).text.toString(),
                    pass,
                    view.findViewById<EditText>(R.id.email).text.toString()).observeForever{
                    if(it.code == 200){
                        Toast.makeText(context, "Cuenta creada con exsito", Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.action_singIn2_to_logIn)
                    }else{
                        Toast.makeText(context, it.msg, Toast.LENGTH_SHORT).show()
                        view.findViewById<EditText>(R.id.password).setText("")
                    }
                }
            }
        }
    }
}