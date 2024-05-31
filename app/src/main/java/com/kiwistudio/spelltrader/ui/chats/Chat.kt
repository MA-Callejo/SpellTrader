package com.kiwistudio.spelltrader.ui.chats

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Space
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kiwistudio.spelltrader.MainViewModel
import com.kiwistudio.spelltrader.R
import com.kiwistudio.spelltrader.db.Mensaje
import com.kiwistudio.spelltrader.entities.ChatPreview
import kotlinx.coroutines.launch

class Chat : Fragment() {
    private lateinit var viewModel: MainViewModel
    private var pedidoId: Int = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        pedidoId = viewModel.pedidoID
        view.findViewById<TextView>(R.id.username).text = viewModel.filtroUserName
        view.findViewById<ImageButton>(R.id.send).setOnClickListener{
            //view.findViewById<EditText>(R.id.escribir).text
            lifecycleScope.launch {
                val mensaje = Mensaje(
                        propio = 1,
                        message = view.findViewById<EditText>(R.id.escribir).text.toString(),
                        timestamp = System.currentTimeMillis(),
                        pedido = pedidoId
                    )
                view.findViewById<EditText>(R.id.escribir).setText("")
                viewModel.sendMensaje(mensaje).observeForever{
                    if(it != null){
                        if(it.code == 200) {
                            lifecycleScope.launch {
                                viewModel.insetMessage(mensaje)
                                recargar(view)
                            }
                        }
                    }
                }
            }
        }
        recargar(view)
    }

    private fun recargar(view: View){
        viewModel.getMensajesServer(pedidoId).observeForever{
            if(it.body != null) {
                val chats = it.body.getJSONArray("mensajes")
                lifecycleScope.launch {
                    for(i in 0 until chats.length()){
                        viewModel.insetMessage(
                            Mensaje(
                                propio = 0,
                                message = chats.getJSONObject(i).getString("cuerpo"),
                                timestamp = chats.getJSONObject(i).getLong("timestamp"),
                                pedido = pedidoId
                            ))
                    }
                    val msgs = viewModel.getMensajes()
                    val recyclerView: RecyclerView = view.findViewById(R.id.mensajes)
                    recyclerView.layoutManager = LinearLayoutManager(requireContext())
                    val adapter = Adapter(msgs)
                    recyclerView.adapter = adapter
                    recyclerView.scrollToPosition(adapter.itemCount - 1)
                }
            }
        }
    }
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val msg: TextView = itemView.findViewById(R.id.msg)
        val spaceSent: Space = itemView.findViewById(R.id.spaceSent)
        val spaceRec: Space = itemView.findViewById(R.id.spaceRec)
        val context: Context
            get() = itemView.context
    }
    class Adapter(private val dataList: List<Mensaje>): RecyclerView.Adapter<MyViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.adapter_mensaje, parent, false)
            return MyViewHolder(itemView)
        }
        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val data = dataList[position]
            holder.msg.text = data.message
            holder.spaceSent.visibility = if(data.propio == 1) View.GONE else View.VISIBLE
            holder.spaceRec.visibility = if(data.propio == 1) View.VISIBLE else View.GONE
        }
        override fun getItemCount(): Int {
            return dataList.size
        }
    }

}