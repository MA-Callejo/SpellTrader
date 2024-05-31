package com.kiwistudio.spelltrader.ui.chats

import android.content.Context
import android.location.Geocoder
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Space
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kiwistudio.spelltrader.MainViewModel
import com.kiwistudio.spelltrader.R
import com.kiwistudio.spelltrader.entities.ChatPreview
import com.kiwistudio.spelltrader.entities.Ubicacion
import com.kiwistudio.spelltrader.entities.UserDetail
import com.kiwistudio.spelltrader.entities.Valoraciones
import com.kiwistudio.spelltrader.ui.anuncios.DialogVerUbicacion
import com.kiwistudio.spelltrader.ui.anuncios.PerfilViwe
import java.util.Locale

class PedidosList : Fragment() {
    private lateinit var viewModel: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pedidos_list, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        recargar(view)
    }
    private fun recargar(view: View){
        viewModel.getPendientes().observeForever{
            if(it.body != null) {
                val chatsJS: MutableList<ChatPreview> = mutableListOf()
                val chats = it.body.getJSONArray("chats")
                for(i in 0 until chats.length()){
                    chatsJS.add(
                        ChatPreview(
                            username = chats.getJSONObject(i).getString("username"),
                            valor = chats.getJSONObject(i).getDouble("valor"),
                            mensaje = chats.getJSONObject(i).getString("mensaje"),
                            saliente = (chats.getJSONObject(i).getInt("saliente") == 1),
                            pedidoId = chats.getJSONObject(i).getInt("pedidoId"),
                            tipo = chats.getJSONObject(i).getInt("tipo")
                    )
                    )
                }
                val recyclerView: RecyclerView = view.findViewById(R.id.chats)
                recyclerView.layoutManager = LinearLayoutManager(requireContext())
                val adapter = Adapter(chatsJS,
                    onCardClick = { data ->
                        viewModel.filtroUserName = data.username
                        viewModel.pedidoID = data.pedidoId
                        findNavController().navigate(R.id.action_pedidosList_to_chat)
                        // Código a ejecutar cuando se pulse el CardView
                        //val dialog = DialogVerUbicacion(data)
                        //dialog.show(parentFragmentManager, "EditUbicacionDialog")
                    })
                recyclerView.adapter = adapter
            }
        }
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val usuario: TextView = itemView.findViewById(R.id.usuario)
        val valor: TextView = itemView.findViewById(R.id.valor)
        val mensaje: TextView = itemView.findViewById(R.id.mensaje)
        val margin_venta: Space = itemView.findViewById(R.id.margin_venta)
        val margin_compra: Space = itemView.findViewById(R.id.margin_compra)
        val context: Context
            get() = itemView.context
    }
    class Adapter(private val dataList: List<ChatPreview>,
                           private val onCardClick: (ChatPreview) -> Unit): RecyclerView.Adapter<MyViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.adapter_pedido, parent, false)
            return MyViewHolder(itemView)
        }
        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val data = dataList[position]
            holder.mensaje.text = (if(data.saliente) "<< " else ">> ") + data.mensaje
            holder.usuario.text = data.username
            holder.valor.text = String.format("%.2f", data.valor)+"€"
            holder.margin_venta.visibility = if(data.tipo == 1) View.GONE else View.VISIBLE
            holder.margin_compra.visibility = if(data.tipo == 1) View.VISIBLE else View.GONE
            holder.itemView.setOnClickListener {
                onCardClick(data)
            }
        }
        override fun getItemCount(): Int {
            return dataList.size
        }
    }
}