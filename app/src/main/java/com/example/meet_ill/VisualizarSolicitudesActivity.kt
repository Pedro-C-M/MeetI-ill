package com.example.meet_ill

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.meet_ill.adapters.SolicitudesAdapter
import com.example.meet_ill.data_classes.ConjuntoSolicitudes
import com.example.meet_ill.databinding.ActivityVisualizarSolicitudesBinding
import com.example.meet_ill.repos.SolicitudesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class VisualizarSolicitudesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVisualizarSolicitudesBinding

    private var solicitudesRepo: SolicitudesRepository = SolicitudesRepository()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityVisualizarSolicitudesBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initVars()

    }

    private fun initVars() {
        initBackButton()
        initRecyclerView()
    }

    private fun initBackButton() {
        binding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun initRecyclerView() {
        lifecycleScope.launch(Dispatchers.IO) {

            var listaEnfermedadesSolicitadas : Map<String, Int> = solicitudesRepo.getDiferentesSolicitudes()
            var listaConjuntosSolicitudes : MutableList<ConjuntoSolicitudes> = mutableListOf()

            for ((enfermedad, repeticiones) in listaEnfermedadesSolicitadas) {
                val conjunto = ConjuntoSolicitudes(enfermedad, repeticiones)
                listaConjuntosSolicitudes.add(conjunto)
            }

            withContext(Dispatchers.Main) {
                binding.rvSolicitudes.layoutManager = LinearLayoutManager(applicationContext).apply {}
                binding.rvSolicitudes.adapter = SolicitudesAdapter(listaConjuntosSolicitudes)
            }
        }
    }
}