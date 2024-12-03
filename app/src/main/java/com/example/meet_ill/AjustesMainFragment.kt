package com.example.meet_ill

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.meet_ill.data_classes.User
import com.example.meet_ill.databinding.FragmentAjustesMainBinding
import com.example.meet_ill.repos.SolicitudesRepository
import com.example.meet_ill.repos.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AjustesMainFragment : Fragment() {

    private var userRepo: UserRepository = UserRepository()
    private var solicitudesRepo: SolicitudesRepository = SolicitudesRepository()


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


        prepararSolicitudGrupos()//Esto toca el tx y la solicitud del grupo

        binding.botonVerSolicitudes.setOnClickListener{
            val intent = Intent(requireContext(), VisualizarSolicitudesActivity::class.java)
            startActivity(intent)
        }

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

    private fun prepararSolicitudGrupos() {
        binding.textFieldGrupo.setEndIconOnClickListener {
            if(binding.tinputSolicitud.text!!.isNotBlank()){
                var enfermedadSolicitada = binding.tinputSolicitud.text.toString().lowercase()

                //Comprobar que el usuario ya solicito esto
                lifecycleScope.launch(Dispatchers.IO) {
                    var user: User? = userRepo.getUserById(FirebaseAuth.getInstance().currentUser?.uid.toString())
                    if(solicitudesRepo.getUserAlreadySolicited(user!!.idUsuario, enfermedadSolicitada)){
                        withContext(Dispatchers.Main) {
                            //Error por solicitar patologia ya pedida por el usuario
                            Toast.makeText(requireContext(), "Ya has solicitado esta patología", Toast.LENGTH_LONG).show()
                            binding.textFieldGrupo.boxStrokeColor = ContextCompat.getColor(requireContext(), R.color.error_red)
                        }
                        }else{
                            //Cargar su solicitud
                            if(solicitudesRepo.createSolicitud(user, enfermedadSolicitada)){
                                //Si va bien
                                withContext(Dispatchers.Main) {
                                    binding.textFieldGrupo.boxStrokeColor = ContextCompat.getColor(requireContext(), R.color.success_green)
                                    binding.tinputSolicitud.setText("")
                                    Toast.makeText(requireContext(), "Grupo solicitado, puedes comprobar tu solicitud en el botón de abajo", Toast.LENGTH_LONG).show()
                                }
                            }else{//Si falla la carga por algo
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(requireContext(), "Tienes que escribir la enfermedad que deseas solicitar", Toast.LENGTH_LONG).show()
                                    binding.textFieldGrupo.boxStrokeColor = ContextCompat.getColor(requireContext(), R.color.error_red)
                                }
                            }
                        }
                    }
            }else{//Si intenta cargar vacio
                Toast.makeText(requireContext(), "Tienes que escribir la enfermedad que deseas solicitar", Toast.LENGTH_LONG).show()
                binding.textFieldGrupo.boxStrokeColor = ContextCompat.getColor(requireContext(), R.color.error_red)
        }
        }
    }
}