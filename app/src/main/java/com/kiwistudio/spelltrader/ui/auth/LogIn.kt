package com.kiwistudio.spelltrader.ui.auth

import android.content.Intent
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
import com.kiwistudio.spelltrader.MainActivity
import com.kiwistudio.spelltrader.R

class LogIn : Fragment() {
    private lateinit var viewModel: LogInViewModel
    private var user = ""
    private var pass = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_log_in, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[LogInViewModel::class.java]
        view.findViewById<TextView>(R.id.signinText).setOnClickListener{
            findNavController().navigate(R.id.action_logIn_to_singIn2)
        }
        view.findViewById<Button>(R.id.enter).setOnClickListener{
            user = view.findViewById<EditText>(R.id.email).text.toString()
            pass = view.findViewById<EditText>(R.id.password).text.toString()
            viewModel.logIn(user, pass).observeForever{
                if(it.code == 200){
                    viewModel.securePreferenceHelper.saveUserCredentials(user, pass)
                    viewModel.securePreferenceHelper.saveUserData(if (it.body?.has("id") == true) it.body.getInt("id") else 0, if (it.body?.has("token") == true) it.body.getString("token") else "")
                    val intent = Intent(requireContext(), MainActivity::class.java)
                    startActivity(intent)
                    activity?.finish()
                }else{
                    Toast.makeText(context, it.msg, Toast.LENGTH_SHORT).show()
                    view.findViewById<EditText>(R.id.password).setText("")
                }
            }
        }
    }
}