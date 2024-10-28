package com.example.meet_ill

import android.widget.ImageView
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Button

class ProfileFragment : Fragment() {

    private lateinit var imgProfile: ImageView
    private lateinit var tvUsername: TextView
    private lateinit var tvRealName: TextView
    private lateinit var tvPatologia1: TextView
    private lateinit var tvPatologia2: TextView
    private lateinit var tvPatologia3: TextView
    private lateinit var btnEditUser: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el diseño del fragmento
        val view = inflater.inflate(R.layout.fragment_profile_menu, container, false)

        // Inicializar vistas
        imgProfile = view.findViewById(R.id.imgProfile)
        tvUsername = view.findViewById(R.id.tvUsername)
        tvRealName = view.findViewById(R.id.tvRealName)
        tvPatologia1 = view.findViewById(R.id.tvNombrePatologia1)
        tvPatologia2 = view.findViewById(R.id.tvNombrePatologia2)
        tvPatologia3 = view.findViewById(R.id.tvNombrePatologia3)
        btnEditUser = view.findViewById(R.id.btnEditUser)

        return view
    }
}
