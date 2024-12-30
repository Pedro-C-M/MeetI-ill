package com.example.meet_ill

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
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
import com.example.meet_ill.repos.GroupRepository
import com.example.meet_ill.repos.UserRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

class ChatActivity : AppCompatActivity() {


    private lateinit var recyclerChats: RecyclerView
    private lateinit var tVContactName: TextView
    private lateinit var iVContactImage: ImageView
    private lateinit var bSendMessage: ImageButton
    private lateinit var bBack: Button
    private lateinit var eTMessage: EditText
    private var userRepo: UserRepository = UserRepository()
    private lateinit var grupo: Grupo
    private lateinit var launcher : ActivityResultLauncher<Intent>



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

        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ resultado ->
            var devuelto = resultado.data?.getStringExtra("despido")
            Toast.makeText(this, devuelto, Toast.LENGTH_LONG).show()
        }

        onStart()

        eTMessage = findViewById(R.id.eTMessage)
        bSendMessage = findViewById(R.id.bSend)
        bBack = findViewById(R.id.backButton)


        //Recycler
        recyclerChats = findViewById(R.id.rVMessages)
        val chatAdapter = ChatAdapter(mutableListOf())
        recyclerChats.adapter = chatAdapter
        recyclerChats.layoutManager = LinearLayoutManager(applicationContext).apply {
            stackFromEnd = true
            reverseLayout = false
        }


        val database = FirebaseDatabase.getInstance("https://meet-ill-default-rtdb.europe-west1.firebasedatabase.app")
        val messagesRef = database
            .getReference("grupos")
            .child(grupo.idGrupo)
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
                    recyclerChats.scrollToPosition(ordenados.size - 1)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebasemensajes", "La has cagado")
            }
        })




        bSendMessage.setOnClickListener{

            if(eTMessage.text.toString().isNotEmpty())
                añadirMensaje()

        }


        bBack.setOnClickListener{
            finish()
        }

        tVContactName.setOnClickListener{
            val intent = Intent(applicationContext, GroupInfoActivity::class.java)
            intent.putExtra("grupo", grupo)
            launcher.launch(intent)
        }






    }

    private fun cambiarFecha(messages: MutableList<Message>): MutableList<Message> {
         val ordenados = messages.sortedBy{it.fecha}
        var devolver = mutableListOf<Message>()
        for( message in ordenados){
            val date = Date( message.fecha.toLong() * 1000)
            // Formatear la fecha
            val format = SimpleDateFormat("hh:mm a", Locale.getDefault())
            val formattedDate = format.format(date)
            devolver.add(Message(message.content,message.isReceived,message.user,formattedDate,
                message.urlFoto))
        }

        return devolver

    }

    private fun añadirMensaje() {

        val database = FirebaseDatabase.getInstance("https://meet-ill-default-rtdb.europe-west1.firebasedatabase.app")
        val ref = database
            .getReference("grupos")

        val groupRef = ref.child(grupo.idGrupo)
        val messagesRef = groupRef.child("messages")

        val message = hashMapOf(
            "sender" to FirebaseAuth.getInstance().currentUser?.uid.toString(),
            "text" to eTMessage.text.toString(),
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

        eTMessage.text.clear()

    }


    private suspend fun cargarMensaje(child: DataSnapshot?): Any {

        lateinit var message: Message
        lateinit var usuario: User
        val data = child?.value as? Map<*, *>

        val content = data?.get("text").toString()
        val sender = data?.get("sender").toString()
        val fecha = data?.get("timeSent").toString()
        lateinit var user: User

        // Crear una instancia de Date con el timestamp

        if(sender.equals(FirebaseAuth.getInstance().currentUser?.uid.toString())) {
            message =  Message(content,false,"",fecha,"")
        }
        else{
             usuario = withContext(Dispatchers.IO) {
                userRepo.getUserById(sender)!!
            }
                message =  Message(content,true,usuario.nombreUsuario,fecha,"")
        }


    return message

    }


}