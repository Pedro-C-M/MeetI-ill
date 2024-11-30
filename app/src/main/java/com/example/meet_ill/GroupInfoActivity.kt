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
import com.example.meet_ill.adapters.ParticipantesAdapter
import com.example.meet_ill.data_classes.Grupo

import com.example.meet_ill.data_classes.User
import com.example.meet_ill.databinding.ActivityGroupInfoBinding
import com.example.meet_ill.repos.GroupRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class GroupInfoActivity : AppCompatActivity() {

    private lateinit var recyclerParticipantes: RecyclerView
    private lateinit var binding: ActivityGroupInfoBinding
    private var listaParticipantes: MutableList<User> = mutableListOf()
    private var groupRepo: GroupRepository = GroupRepository()
    private lateinit var grupo: Grupo
    private lateinit var launcher : ActivityResultLauncher<Intent>

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

        grupo = intent.getParcelableExtra("grupo")!!

        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ resultado ->
            var devuelto = resultado.data?.getStringExtra("despido")
            Toast.makeText(this, devuelto, Toast.LENGTH_LONG).show()
        }

        binding.ivFotoPerfil.load(grupo.urlImagen)
        binding.tvNombreGrupo.text = grupo.titulo

        binding.backButton.setOnClickListener{
            finish()
            val intent = Intent(applicationContext, ChatActivity::class.java)
            intent.putExtra("grupo", grupo)
            launcher.launch(intent)
        }

        lifecycleScope.launch(Dispatchers.IO) {
            listaParticipantes = groupRepo.getParticipantsById(grupo.idGrupo)!!

            // Actualizar la UI con los mensajes
            withContext(Dispatchers.Main) {

                inicializaRecyclerParticipantes()
                binding.tvNumeroParticipantes.text= "${listaParticipantes.count()+1} participantes"
            }
        }

        binding.btSalir.setOnClickListener{
            lifecycleScope.launch(Dispatchers.IO) {
                groupRepo.abandonarGrupo(grupo.idGrupo!!, FirebaseAuth.getInstance().currentUser?.uid.toString())
            }

            finish()
            val intent = Intent(applicationContext, MainActivity::class.java)
            launcher.launch(intent)
        }

    }


    private fun inicializaRecyclerParticipantes(){
        recyclerParticipantes = binding.rvParticipantes

        recyclerParticipantes.layoutManager = LinearLayoutManager(applicationContext).apply {
        }

        recyclerParticipantes.adapter = ParticipantesAdapter(listaParticipantes)

    }
}