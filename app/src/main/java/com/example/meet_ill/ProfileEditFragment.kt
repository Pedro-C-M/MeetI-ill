package com.example.meet_ill

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
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
import com.example.meet_ill.databinding.FragmentEditProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.net.URI
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ProfileEditFragment : Fragment()  {
    private lateinit var imgProfile: ImageView
    private lateinit var etUsername: EditText
    private lateinit var etRealName: EditText
    private lateinit var spnPatologia1: Spinner
    private lateinit var spnPatologia2: Spinner
    private lateinit var spnPatologia3: Spinner
    private lateinit var btnSave: Button

    private lateinit var selectedImageUri: Uri


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

        // Imagen de perfil (click para seleccionar)
        imgProfile.setOnClickListener {
            seleccionarImagen()
        }

        // Rellenar spinners
        rellenaPatologias()

        return view
    }

    private fun seleccionarImagen() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT

        startActivityForResult(intent, 100)
    }

    private fun uploadImage(){
        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Uploading File...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        val formatter=SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
        val now= Date()
        val fileName = formatter.format(now)
        val storageReference = FirebaseStorage.getInstance().getReference("images/$fileName")

        storageReference.putFile(selectedImageUri).
                    addOnSuccessListener {

                        imgProfile.setImageURI(null)
                        Toast.makeText(requireContext(), "Successfully Uploaded", Toast.LENGTH_SHORT).show()
                        if(progressDialog.isShowing) progressDialog.dismiss()
                    }.addOnFailureListener{
                        if(progressDialog.isShowing) progressDialog.dismiss()
                        Toast.makeText(requireContext(), "Failed", Toast.LENGTH_SHORT).show()
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            selectedImageUri= data?.data!!
            imgProfile.setImageURI(selectedImageUri)
        }
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
                val currentUser = FirebaseAuth.getInstance().currentUser
                if (currentUser == null) {
                    Toast.makeText(requireContext(), "Usuario no autenticado", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                val userId = currentUser.uid
                val db = FirebaseFirestore.getInstance()

                //Lo pongo por si modificamos unos campos y otros no
                val updates = mutableMapOf<String, Any>()

                val username = etUsername.text.toString()
                if (username.isNotEmpty()) {
                    updates["apodo"] = username
                }
                val realName = etRealName.text.toString()
                if (realName.isNotEmpty()) {
                    updates["name"] = realName
                }

                val patologia1 = spnPatologia1.selectedItem.toString()
                val patologia2 = spnPatologia2.selectedItem.toString()
                val patologia3 = spnPatologia3.selectedItem.toString()

                if (patologia1 != patologia2 && patologia1 != patologia3 && patologia2 != patologia3) {
                    updates["patologia1"] = patologia1
                    updates["patologia2"] = patologia2
                    updates["patologia3"] = patologia3
                } else {
                    Toast.makeText(requireContext(), "Las patologías no pueden repetirse", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                // Si se seleccionó una imagen, incluir su URI en las actualizaciones
                selectedImageUri?.let { uri ->
                    updates["imagenPerfil"] = uri.toString()
                }

                // Si no hay nada que actualizar, no hacer nada
                if (updates.isEmpty()) {
                    Toast.makeText(requireContext(), "No hay cambios para guardar", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                // Actualizar Firestore
                db.collection("users").document(userId)
                    .update(updates)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Perfil actualizado con éxito", Toast.LENGTH_SHORT).show()
                        findNavController().navigateUp()
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), "Error al actualizar el perfil", Toast.LENGTH_SHORT).show()
                    }

            }

        }
    }

}
