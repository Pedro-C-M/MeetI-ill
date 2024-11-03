package com.example.meet_ill

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil3.load

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


/**
 * A simple [Fragment] subclass.
 * Use the [ChatFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ChatFragment : Fragment() {

    private lateinit var recyclerChats: RecyclerView
    private lateinit var tVContactName: TextView
    private lateinit var iVContactImage: ImageView
    private lateinit var bSendMessage: ImageButton
    private lateinit var eTMessage: EditText
    private lateinit var listaMensajes: MutableList<Message>

    private val args : ChatFragmentArgs by navArgs()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val contacto = args.contacto
        tVContactName = view.findViewById(R.id.tVContactName)
        tVContactName.text=contacto.nombre

        iVContactImage = view.findViewById(R.id.iVContactImage)
        iVContactImage.load(contacto.imagenPerfil)
        listaMensajes = MutableList(15){i ->Message("Mensaje $i",Status.Read)}
        eTMessage = view.findViewById(R.id.eTMessage)
        bSendMessage = view.findViewById(R.id.bSend)

        bSendMessage.setOnClickListener{
            listaMensajes.add(Message(eTMessage.text.toString(),Status.Received))
            eTMessage.text.clear()
            inicializaRecyclerChats()
        }

        inicializaRecyclerChats()
    }

    private fun inicializaRecyclerChats(){
        recyclerChats = requireView().findViewById(R.id.rVMessages)

        recyclerChats.layoutManager = LinearLayoutManager(requireContext())

        recyclerChats.adapter = ChatAdapter(listaMensajes)
    }


}