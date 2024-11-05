package com.example.meet_ill

import android.widget.ImageView
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs

class ProfileEditFragment : Fragment()  {
    private lateinit var imgProfile: ImageView
    private lateinit var etUsername: EditText
    private lateinit var etRealName: EditText
    private lateinit var spnPatologia1: Spinner
    private lateinit var spnPatologia2: Spinner
    private lateinit var spnPatologia3: Spinner
    private lateinit var btnSave: Button

    private val argumentos : ProfileEditFragmentArgs by navArgs()

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

        //Establezco las hints
        etUsername.hint=argumentos.usuario.nombreUsuario
        etRealName.hint=argumentos.usuario.nombreReal

        //La imagen del usuario
        imgProfile.setImageResource(R.drawable.default_profile_image)

        //Rellenamos los spinners
        rellenaPatologias()

        return view
    }

    private fun rellenaPatologias() {
        val patologias = listOf("Diabetes", "VIH", "Hipertensión", "Asma", "Artritis", "Epilepsia",
            "Alzheimer", "Parkinson", "Esclerosis múltiple", "Cáncer", "Enfermedad cardíaca", "EPOC",
            "Insuficiencia renal", "Fibromialgia", "Lupus", "Anemia", "Migraña", "Obesidad",
            "Hipotiroidismo", "Alergias"
        )

        // Configurar el ArrayAdapter
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, patologias)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Asignar el adaptador al Spinner
        spnPatologia1.adapter = adapter
        spnPatologia2.adapter = adapter
        spnPatologia3.adapter = adapter
    }

    override fun onStart() {
        super.onStart()

        btnSave.isEnabled = true
        btnSave.setOnClickListener {
            btnSave.setOnClickListener {
                val username = etUsername.text.toString()
                val realName = etRealName.text.toString()
                val patologia1 = spnPatologia1.selectedItem.toString()
                val patologia2 = spnPatologia2.selectedItem.toString()
                val patologia3 = spnPatologia3.selectedItem.toString()

                // Validar campos
                if (username.isEmpty() || realName.isEmpty()) {
                    Toast.makeText(requireContext(), "El nombre de usuario y el nombre real no pueden estar vacíos", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                // Validar patologías
                if (patologia1 == patologia2 || patologia1 == patologia3 || patologia2 == patologia3) {
                    Toast.makeText(requireContext(), "Las patologías no pueden repetirse", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                argumentos.usuario.nombreUsuario = username
                argumentos.usuario.nombreReal = realName
                argumentos.usuario.patologia1 = patologia1
                argumentos.usuario.patologia2 = patologia2
                argumentos.usuario.patologia3 = patologia3
                //Aqui asignarimos la imagen que se deberia seleccionar, no implemntado

                val destino = ProfileEditFragmentDirections.actionProfileEditFragmentToProfileFragment(argumentos.usuario)
                findNavController().navigate(destino)
            }

        }
    }

}
