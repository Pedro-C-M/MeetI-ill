package com.example.meet_ill


import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.meet_ill.adapters.ChatAdapter
import com.example.meet_ill.adapters.ParticipantesAdapter

import com.example.meet_ill.data_classes.User
import com.example.meet_ill.databinding.ActivityGroupInfoBinding


class GroupInfoActivity : AppCompatActivity() {

    private lateinit var recyclerParticipantes: RecyclerView
    private lateinit var binding: ActivityGroupInfoBinding
    private var listaParticipantes: MutableList<User> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityGroupInfoBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        initVars()


    }


    private fun initVars(){

    }


    private fun inicializaRecyclerParticipantes(){
        recyclerParticipantes = binding.rvParticipantes

        recyclerParticipantes.layoutManager = LinearLayoutManager(applicationContext).apply {
            stackFromEnd = true
            reverseLayout = false
        }

        recyclerParticipantes.adapter = ParticipantesAdapter(listaParticipantes)

    }
}