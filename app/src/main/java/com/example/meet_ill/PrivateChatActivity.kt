package com.example.meet_ill

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.example.meet_ill.data_classes.Message
import com.example.meet_ill.data_classes.User
import com.example.meet_ill.databinding.ActivityChatBinding
import com.example.meet_ill.repos.ChatRepository
import com.example.meet_ill.repos.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PrivateChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private lateinit var otroUsuario: User
    private lateinit var usuarioId: String
    private lateinit var launcher: ActivityResultLauncher<Intent>
    private var chatRepo: ChatRepository = ChatRepository()
    private lateinit var recyclerChats: RecyclerView
    private var chatId: String = ""
    private var userRepo: UserRepository = UserRepository()

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


    private fun initVars() {

        otroUsuario = intent.getParcelableExtra("user")!!

        usuarioId = FirebaseAuth.getInstance().currentUser?.uid.toString()

        binding.tVContactName.text = otroUsuario.nombreUsuario

        binding.iVContactImage.load(otroUsuario.imagenPerfil)

        launcher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { resultado ->
                var devuelto = resultado.data?.getStringExtra("despido")
                Toast.makeText(this, devuelto, Toast.LENGTH_LONG).show()
            }

        onStart()
        var context: Context = this

        lifecycleScope.launch(Dispatchers.IO) {
            chatId = chatRepo.getChatId(otroUsuario, usuarioId)!!


            // Actualizar la UI con los mensajes
            withContext(Dispatchers.Main) {

                lifecycleScope.launch(Dispatchers.IO) {
                    var user: User? =
                        userRepo.getUserById(FirebaseAuth.getInstance().currentUser?.uid.toString())

                    withContext(Dispatchers.Main) {
                //Recycler
                recyclerChats = binding.rVMessages
                val chatAdapter = ChatAdapter(
                    mutableListOf(),
                    context,
                    user!!.tipoUsuario,
                    chatId,
                    1,//Para grupos 0 para chats 1
                    coroutineScope = lifecycleScope
                )
                recyclerChats.adapter = chatAdapter
                recyclerChats.layoutManager = LinearLayoutManager(applicationContext).apply {
                    stackFromEnd = true
                    reverseLayout = false
                }


                val database =
                    FirebaseDatabase.getInstance("https://meet-ill-default-rtdb.europe-west1.firebasedatabase.app")
                val messagesRef = database
                    .getReference("chats")
                    .child(chatId)
                    .child("messages")

                messagesRef.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val messages = mutableListOf<Message>()
                        val deferred = CompletableDeferred<Unit>()
                        for (child in snapshot.children) {
                            lifecycleScope.launch {
                                val message = cargarMensaje(child)
                                messages.add(message as Message)
                                if (messages.size == snapshot.childrenCount.toInt()) {
                                    deferred.complete(Unit)  // Marca como completado
                                }
                            }
                        }
                        lifecycleScope.launch {
                            deferred.await()  // Espera a que se complete
                            val ordenados = cambiarFecha(messages)
                            chatAdapter.updateMessages(ordenados)  // Actualiza el adaptador
                            binding.rVMessages.scrollToPosition(ordenados.size - 1)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("Firebasemensajes", "La has cagado")
                    }
                })
            }}




        binding.bSend.setOnClickListener {

            if (binding.eTMessage.text.toString().isNotEmpty())
                añadirMensaje()

        }


        binding.backButton.setOnClickListener {
            finish()
        }



}
        }
    }

    private fun cambiarFecha(messages: MutableList<Message>): MutableList<Message> {
        val ordenados = messages.sortedBy { it.fecha }
        var devolver = mutableListOf<Message>()
        for (message in ordenados) {
            val date = Date(message.fecha.toLong() * 1000)
            // Formatear la fecha
            val format = SimpleDateFormat("hh:mm a", Locale.getDefault())
            val formattedDate = format.format(date)
            devolver.add(
                Message(
                    message.content, message.isReceived, message.user, formattedDate,
                    message.urlFoto, message.messageId
                )
            )
        }

        return devolver

    }

    private fun añadirMensaje() {

        val database =
            FirebaseDatabase.getInstance("https://meet-ill-default-rtdb.europe-west1.firebasedatabase.app")
        val ref = database
            .getReference("chats")

        val groupRef = ref.child(chatId)
        val messagesRef = groupRef.child("messages")

        val message = hashMapOf(
            "sender" to FirebaseAuth.getInstance().currentUser?.uid.toString(),
            "text" to binding.eTMessage.text.toString(),
            "timeSent" to System.currentTimeMillis() / 1000
        )

        groupRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // El grupo existe, agregar el mensaje
                    messagesRef.push().setValue(message)
                } else {
                    // El grupo no existe, crear el grupo con "messages" y el primer mensaje
                    val newGroup = hashMapOf(
                        "messages" to mapOf(
                            messagesRef.push().key to message // Crea el mensaje en el nuevo grupo
                        )
                    )
                    groupRef.setValue(newGroup)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Error al verificar la existencia del grupo
                Log.e("Firebase", "Error checking group existence", error.toException())
            }
        })

        binding.eTMessage.text.clear()

    }


    private suspend fun cargarMensaje(child: DataSnapshot?): Any {

        lateinit var message: Message
        lateinit var usuario: User
        val data = child?.value as? Map<*, *>
        val id = child?.key.toString()
        val content = data?.get("text").toString()
        val sender = data?.get("sender").toString()
        val fecha = data?.get("timeSent").toString()
        lateinit var user: User

        // Crear una instancia de Date con el timestamp

        if (sender.equals(FirebaseAuth.getInstance().currentUser?.uid.toString())) {
            message = Message(content, false, "", fecha, "", id)
        } else {
            usuario = withContext(Dispatchers.IO) {
                userRepo.getUserById(sender)!!
            }
            message = Message(content, true, usuario.nombreUsuario, fecha, "",id)
        }


        return message

    }
}