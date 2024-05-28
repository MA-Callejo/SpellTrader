package com.kiwistudio.spelltrader.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.kiwistudio.spelltrader.LogInViewModel
import com.kiwistudio.spelltrader.MainActivity
import com.kiwistudio.spelltrader.R
class Launch : Fragment() {
    private lateinit var viewModel: LogInViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_launch, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[LogInViewModel::class.java]
        val (username, password) = viewModel.securePreferenceHelper.getUserCredentials()
        if(username == null || password == null){
            viewModel.securePreferenceHelper.clearUserCredentials()
            findNavController().navigate(R.id.action_launch_to_logIn)
        }else{
            viewModel.logIn(username, password).observeForever{
                if(it.code == 200){
                    viewModel.securePreferenceHelper.saveUserCredentials(username, password)
                    viewModel.securePreferenceHelper.saveUserData(if (it.body?.has("id") == true) it.body.getInt("id") else 0, if (it.body?.has("token") == true) it.body.getString("token") else "")
                    val intent = Intent(requireContext(), MainActivity::class.java)
                    startActivity(intent)
                    activity?.finish()
                }else{
                    viewModel.securePreferenceHelper.clearUserCredentials()
                    findNavController().navigate(R.id.action_launch_to_logIn)
                }
            }
        }
    }
}