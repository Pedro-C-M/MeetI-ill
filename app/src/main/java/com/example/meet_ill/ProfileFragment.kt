package com.example.meet_ill

import android.widget.ImageView
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Spinner
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Button

class ProfileFragment : Fragment() {

    private lateinit var imgProfile: ImageView
    private lateinit var tvUsername: TextView
    private lateinit var tvRealName: TextView
    private lateinit var spnPatologia1: Spinner
    private lateinit var spnPatologia2: Spinner
    private lateinit var spnPatologia3: Spinner
    private lateinit var btnEditUser: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el diseño del fragmento
        val view = inflater.inflate(R.layout.fragment_profileMenu, container, false)

        // Inicializar vistas
        imgProfile = view.findViewById(R.id.imgProfile)
        tvUsername = view.findViewById(R.id.tvUsername)
        tvRealName = view.findViewById(R.id.tvRealName)
        spnPatologia1 = view.findViewById(R.id.spnPatologias1)
        spnPatologia2 = view.findViewById(R.id.spnPatologias2)
        spnPatologia3 = view.findViewById(R.id.spnPatologias3)
        btnEditUser = view.findViewById(R.id.btnEditUser)

        // Opciones para el spinner
        val patologias = arrayOf("Diabetes", "Hipertensión", "Asma", "Ninguna")

        // Adaptador para los Spinners
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, patologias)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Asignar adaptadores a cada Spinner
        spnPatologia1.adapter = adapter
        spnPatologia2.adapter = adapter
        spnPatologia3.adapter = adapter

        // Cargar datos de usuario si es necesario

        return view
    }
}
