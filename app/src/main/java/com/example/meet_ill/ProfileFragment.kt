package com.example.meet_ill

import android.graphics.BitmapFactory
import android.widget.ImageView
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Button
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class ProfileFragment : Fragment() {

    private lateinit var imgProfile: ImageView
    private lateinit var tvUsername: TextView
    private lateinit var tvRealName: TextView
    private lateinit var tvCorreo: TextView
    private lateinit var tvPatologia1: TextView
    private lateinit var tvPatologia2: TextView
    private lateinit var tvPatologia3: TextView
    private lateinit var btnEditUser: Button

    private val argumentos : ProfileFragmentArgs by navArgs()

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
        tvCorreo = view.findViewById(R.id.tvCorreo)
        tvPatologia1 = view.findViewById(R.id.tvNombrePatologia1)
        tvPatologia2 = view.findViewById(R.id.tvNombrePatologia2)
        tvPatologia3 = view.findViewById(R.id.tvNombrePatologia3)
        btnEditUser = view.findViewById(R.id.btnEditUser)


        cargarDatosUsuario()
        //cargarDatosAntiguosUsuario()

        return view
    }

    private fun cargarDatosAntiguosUsuario() {
        //Saco los valores del usuario....
        tvUsername.text=argumentos.usuario.nombreUsuario
        tvRealName.text=argumentos.usuario.nombreReal
        tvCorreo.text=argumentos.usuario.email
        tvPatologia1.text=argumentos.usuario.patologia1
        tvPatologia2.text=argumentos.usuario.patologia2
        tvPatologia3.text=argumentos.usuario.patologia3

        imgProfile.setImageResource(R.drawable.default_profile_image)
    }

    private fun cargarDatosUsuario() {
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {
            val userId = currentUser.uid

            FirebaseFirestore.getInstance().collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        // Extraer datos del documento
                        tvUsername.text = document.getString("apodo") ?: "Sin apodo"
                        tvRealName.text = document.getString("name") ?: "Sin nombre"
                        tvCorreo.text = document.getString("email") ?: "Sin correo"

                        // No hay campos específicos para patologías en tu estructura actual, se pueden dejar vacíos o manejar de otra forma
                        tvPatologia1.text =  document.getString("patologia1") ?: "N/A"
                        tvPatologia2.text =  document.getString("patologia2") ?: "N/A"
                        tvPatologia3.text =  document.getString("patologia3") ?: "N/A"

                        val imageUrl = document.getString("imagenPerfil")
                        if (!imageUrl.isNullOrEmpty()) {
                            // Obtener el archivo desde Firebase Storage
                            val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl)

                            // Ruta local para guardar la imagen
                            val localFile = File.createTempFile("profile_image", "jpg")

                            // Descargar la imagen desde Firebase Storage
                            storageReference.getFile(localFile)
                                .addOnSuccessListener {
                                    // La imagen ha sido descargada exitosamente
                                    val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                                    imgProfile.setImageBitmap(bitmap)
                                }
                                .addOnFailureListener {
                                    // Error al descargar la imagen
                                    imgProfile.setImageResource(R.drawable.default_profile_image)
                                    Toast.makeText(requireContext(), "Error al cargar la imagen", Toast.LENGTH_SHORT).show()
                                }
                        } else {
                            imgProfile.setImageResource(R.drawable.default_profile_image)
                        }

                    }
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Error al cargar los datos", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(requireContext(), "No se encontró usuario en sesión", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onStart() {
        super.onStart()

        btnEditUser.isEnabled = true
        btnEditUser.setOnClickListener {
            btnEditUser.setOnClickListener {

                val destino = ProfileFragmentDirections.actionProfileFragmentToProfileEditFragment(argumentos.usuario)
                findNavController().navigate(destino)
            }

        }
    }
}
