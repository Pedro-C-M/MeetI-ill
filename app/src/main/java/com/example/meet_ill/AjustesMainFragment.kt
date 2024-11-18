package com.example.meet_ill

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.meet_ill.databinding.FragmentAjustesMainBinding
import com.google.firebase.auth.FirebaseAuth

class AjustesMainFragment : Fragment() {

    private lateinit var binding: FragmentAjustesMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAjustesMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnCerrarSesion.setOnClickListener{
            FirebaseAuth.getInstance().signOut()

            // Crear un Intent para ir a la actividad de inicio de sesión
            val intent = Intent(requireActivity(), LoginActivity::class.java)
            //Esto para que no pueda darle a la flecha pa atras y volver a la aplicacion
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        }
    }
}