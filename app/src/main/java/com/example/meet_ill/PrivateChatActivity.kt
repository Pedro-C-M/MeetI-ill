package com.example.meet_ill

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
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
import com.example.meet_ill.data_classes.User
import com.example.meet_ill.databinding.ActivityChatBinding
import com.example.meet_ill.repos.ChatRepository
import com.example.meet_ill.repos.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PrivateChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private lateinit var otroUsuario: User
    private lateinit var usuarioId: String
    private lateinit var launcher : ActivityResultLauncher<Intent>
    private var listaMensajes: MutableList<Message> = mutableListOf()
    private var chatRepo: ChatRepository = ChatRepository()
    private lateinit var recyclerChats: RecyclerView
    private  var chatId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityChatBinding.inflate(layoutInflater)
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

        otroUsuario = intent.getParcelableExtra("user")!!

        usuarioId = FirebaseAuth.getInstance().currentUser?.uid.toString()

        binding.tVContactName.text=otroUsuario.nombreUsuario

        binding.iVContactImage.load(otroUsuario.imagenPerfil)

        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ resultado ->
            var devuelto = resultado.data?.getStringExtra("despido")
            Toast.makeText(this, devuelto, Toast.LENGTH_LONG).show()
        }

        onStart()


        lifecycleScope.launch(Dispatchers.IO) {
            chatId = chatRepo.getChatId(otroUsuario, usuarioId)!!

            listaMensajes = chatRepo.getMessageById(chatId)!!

            // Actualizar la UI con los mensajes
            withContext(Dispatchers.Main) {

                inicializaRecyclerChats()

            }
        }


        binding.bSend.setOnClickListener{
            lifecycleScope.launch(Dispatchers.IO) {
                chatRepo.addMessage(binding.eTMessage.text.toString(), chatId)


                withContext(Dispatchers.Main) {
                    cargarMensajes(chatId)
                    binding.eTMessage.text.clear()
                }
            }

        }

        binding.backButton.setOnClickListener{
            val intent = Intent(applicationContext, MainActivity::class.java)
            launcher.launch(intent)
        }




    }

    private fun cargarMensajes(chatId: String) {

        lifecycleScope.launch(Dispatchers.IO) {

            listaMensajes = chatRepo.getMessageById(chatId)!!

            withContext(Dispatchers.Main) {
                if (listaMensajes != null) {
                    inicializaRecyclerChats()
                }
            }

        }
    }





    private fun inicializaRecyclerChats(){
        recyclerChats = findViewById(R.id.rVMessages)

        recyclerChats.layoutManager = LinearLayoutManager(applicationContext).apply {
            stackFromEnd = true
            reverseLayout = false
        }

        recyclerChats.adapter = ChatAdapter(listaMensajes)

    }
}