package com.example.meet_ill

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.meet_ill.adapters.RecentChatAdapter
import com.example.meet_ill.adapters.onChatClicked
import com.example.meet_ill.data_classes.ChatRecientes
import com.example.meet_ill.repos.ChatRepository
import com.example.meet_ill.repos.UserRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class ChatPrivadoMainFragment : Fragment() {

    private val userRepository = UserRepository()
    private val chatRepository = ChatRepository()
    private lateinit var adapter: RecentChatAdapter
    private val chatsRecientes = mutableListOf<ChatRecientes>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = RecentChatAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chat_privado_main, container, false)
        setupRecyclerView(view)
        cargarChatsRecientes(view)
        return view
    }

    private fun setupRecyclerView(view: View) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewChats)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = RecentChatAdapter()
        recyclerView.adapter = adapter

        // Configurar un listener opcional en el adaptador
        adapter.setOnChatClickListener(object : onChatClicked {
            override fun getOnChatCLickedItem(position: Int, chatList: ChatRecientes) {
                Toast.makeText(requireContext(), "Chat con ${chatList.nombre}", Toast.LENGTH_SHORT).show()
                // Aquí podrías iniciar una nueva actividad o fragmento
            }
        })
    }

    private fun cargarChatsRecientes(view: View) {
        val currentUserId = userRepository.getCurrentUserId() ?: return

        lifecycleScope.launch {
            val chats = chatRepository.getChatsForUser(currentUserId)
            chatsRecientes.clear()
            chatsRecientes.addAll(chats)
            val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewChats)
            val noMessagesTextView = view.findViewById<TextView>(R.id.tvNoMessages)

            if (chatsRecientes.isNotEmpty()) {
                noMessagesTextView.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
                adapter.setList(chatsRecientes)
                adapter.notifyDataSetChanged()
            } else {
                noMessagesTextView.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            }
        }
    }

}