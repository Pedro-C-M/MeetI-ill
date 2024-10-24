package com.example.meet_ill

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.meet_ill.adapters.ContactAdapter
import com.example.meet_ill.data_classes.Contacto


class HomeFragment : Fragment() {

    private lateinit var recyclerContactos : RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        generarRecyclerContactos()
    }

    private fun generarRecyclerContactos() {

        recyclerContactos = requireView().findViewById(R.id.recyclerContactos)
        val listaContactos = crearContactosSimulado()
        recyclerContactos.layoutManager = LinearLayoutManager(requireContext())
        recyclerContactos.adapter = ContactAdapter(listaContactos)
    }

    private fun crearContactosSimulado(): MutableList<Contacto> {
        val contactos = mutableListOf<Contacto>()

        for (i in 1..15) {
            val contacto = Contacto(
                imagenPerfil = "https://example.com/imagen$i.jpg",
                nombre = "Contacto $i",
                ultimoMensaje = "Este es el último mensaje del contacto $i",
                horaUltimoMensaje = "12:${20-i}" // Genera una hora variada para cada contacto
            )
            contactos.add(contacto)
        }

        return contactos
    }

}