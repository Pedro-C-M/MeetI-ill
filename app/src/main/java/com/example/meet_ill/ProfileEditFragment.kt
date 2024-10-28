package com.example.meet_ill

import android.widget.ImageView
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner

class ProfileEditFragment : Fragment()  {
    private lateinit var imgProfile: ImageView
    private lateinit var etUsername: EditText
    private lateinit var etRealName: EditText
    private lateinit var spnPatologia1: Spinner
    private lateinit var spnPatologia2: Spinner
    private lateinit var spnPatologia3: Spinner
    private lateinit var btnSave: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflar el diseño del fragmento
        val view = inflater.inflate(R.layout.fragment_edit_profile, container, false)

        // Inicializar vistas
        imgProfile = view.findViewById(R.id.imgProfile)
        etUsername = view.findViewById(R.id.etUsername)
        etRealName = view.findViewById(R.id.etRealName)
        spnPatologia1 = view.findViewById(R.id.spnPatologia1)
        spnPatologia2 = view.findViewById(R.id.spnPatologia2)
        spnPatologia3 = view.findViewById(R.id.spnPatologia3)
        btnSave = view.findViewById(R.id.btnSave)


        return view
    }

}
