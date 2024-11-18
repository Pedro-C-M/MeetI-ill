package com.example.meet_ill

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import com.example.meet_ill.adapters.ChatAdapter
import com.example.meet_ill.data_classes.Grupo
import com.example.meet_ill.data_classes.Message
import com.example.meet_ill.repos.GroupRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

class ChatActivity : AppCompatActivity() {


    private lateinit var recyclerChats: RecyclerView
    private lateinit var tVContactName: TextView
    private lateinit var iVContactImage: ImageView
    private lateinit var bSendMessage: ImageButton
    private lateinit var bBack: Button
    private lateinit var eTMessage: EditText
    private var groupRepo: GroupRepository = GroupRepository()
    private var listaMensajes: MutableList<Message> = mutableListOf()
    private lateinit var grupo: Grupo



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_chat)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initVars()



    }


    private fun initVars(){

         grupo = intent.getParcelableExtra("grupo")!!
        tVContactName = findViewById(R.id.tVContactName)
        tVContactName.text=grupo.titulo

        iVContactImage = findViewById(R.id.iVContactImage)
        iVContactImage.load(grupo.urlImagen)

        onStart()

        eTMessage = findViewById(R.id.eTMessage)
        bSendMessage = findViewById(R.id.bSend)
        bBack = findViewById(R.id.backButton)

        lifecycleScope.launch(Dispatchers.IO) {
            listaMensajes = groupRepo.getMessageById(grupo.idGrupo)!!

            // Actualizar la UI con los mensajes
            withContext(Dispatchers.Main) {

                inicializaRecyclerChats()

            }
        }

        bSendMessage.setOnClickListener{
            lifecycleScope.launch(Dispatchers.IO) {
                groupRepo.addMessage(eTMessage.text.toString(),grupo.idGrupo)
                cargarMensajes(grupo)
            }
            eTMessage.text.clear()
        }

        bBack.setOnClickListener{
            finish()
        }






    }

    private fun cargarMensajes(grupo: Grupo) {

        lifecycleScope.launch(Dispatchers.IO) {

            listaMensajes = groupRepo.getMessageById(grupo.idGrupo)!!

            withContext(Dispatchers.Main) {
                if (listaMensajes != null) {
                    inicializaRecyclerChats()
                }
            }

        }
    }





    private fun inicializaRecyclerChats(){
        recyclerChats = findViewById(R.id.rVMessages)

        recyclerChats.layoutManager = LinearLayoutManager(applicationContext)

        recyclerChats.adapter = ChatAdapter(listaMensajes)

    }
}