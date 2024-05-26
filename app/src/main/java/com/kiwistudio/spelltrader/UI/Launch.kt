package com.kiwistudio.spelltrader.UI

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.kiwistudio.spelltrader.MainViewModel
import com.kiwistudio.spelltrader.R
class Launch : Fragment() {
    private lateinit var viewModel: MainViewModel

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
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        val (username, password) = viewModel.securePreferenceHelper.getUserCredentials()
        if(username == null || password == null){
            viewModel.securePreferenceHelper.clearUserCredentials()
            findNavController().navigate(R.id.action_launch_to_logIn)
        }else{
            viewModel.response.observeForever{
                if(it.code == 200){
                    viewModel.securePreferenceHelper.saveUserCredentials(username, password)
                    it.body?.let { it1 -> viewModel.setUser(it1.getInt("id"), it.body.getString("token")) }
                    findNavController().navigate(R.id.action_launch_to_main2)

                }else{
                    viewModel.securePreferenceHelper.clearUserCredentials()
                    findNavController().navigate(R.id.action_launch_to_logIn)
                }
            }
            viewModel.logIn(username, password)
        }
    }
}