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
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.marginTop
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
    private lateinit var btnSave: Button

    private lateinit var selectedImageUri: Uri

    private lateinit var btnAddSpinner: Button
    private lateinit var btnRemoveSpinner: Button
    private lateinit var spinnersContainer: LinearLayout
    private val spinnerList = mutableListOf<Spinner>()



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
        btnSave = view.findViewById(R.id.btnSave)
        imgProfile.setImageResource(R.drawable.default_profile_image)


        spinnersContainer = view.findViewById(R.id.spinnersContainer)
        btnAddSpinner = view.findViewById(R.id.btnAddSpinner)
        btnRemoveSpinner = view.findViewById(R.id.btnRemoveSpinner)

        btnAddSpinner.setOnClickListener {
            if (spinnerList.size < 5) {
                val newSpinner = createSpinner()
                spinnerList.add(newSpinner)
                spinnersContainer.addView(newSpinner)
            }
        }

        btnRemoveSpinner.setOnClickListener {
            if (spinnerList.size > 0) {
                val lastSpinner = spinnerList.removeAt(spinnerList.size - 1)
                spinnersContainer.removeView(lastSpinner)
            }
        }

        // Imagen de perfil (click para seleccionar)
        imgProfile.setOnClickListener {
            seleccionarImagen()
        }

        rellenaHints()

        return view
    }

    private fun createSpinner(): Spinner {
        val patologias = listOf("Diabetes", "VIH", "Hipertensión", "Asma", "Artritis", "Epilepsia",
            "Alzheimer", "Parkinson", "Esclerosis múltiple", "Cáncer", "Enfermedad cardíaca", "EPOC",
            "Insuficiencia renal", "Fibromialgia", "Lupus", "Anemia", "Migraña", "Obesidad",
            "Hipotiroidismo", "Alergias"
        )
        val spinner = Spinner(requireContext())
        // Crear un ArrayAdapter con un TextView personalizado
        val adapter = object : ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, patologias) {
            override fun getView(position: Int, convertView: android.view.View?, parent: android.view.ViewGroup): android.view.View {
                val view = super.getView(position, convertView, parent)
                val textView = view as TextView
                textView.textSize = 19f // Establecer tamaño de texto a 16sp
                textView.setPadding(20, 20, 20, 20)
                return view
            }

            override fun getDropDownView(position: Int, convertView: android.view.View?, parent: android.view.ViewGroup): android.view.View {
                val view = super.getDropDownView(position, convertView, parent)
                val textView = view as TextView
                textView.textSize = 25f // Establecer tamaño de texto a 16sp para el dropdown
                return view
            }
        }

        spinner.adapter = adapter

        return spinner
    }

    private fun rellenaHints() {
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {
            val userId = currentUser.uid

            FirebaseFirestore.getInstance().collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        etUsername.hint = document.getString("apodo") ?: "Apodo"
                        etRealName.hint = document.getString("name") ?: "Nombre real"
                    }
                }
        }
    }

    private fun seleccionarImagen() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT

        startActivityForResult(intent, 100)
    }

    private fun uploadImage(userId: String) {
        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Uploading File...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        // Usar la userId para crear el nombre del archivo
        val storageReference = FirebaseStorage.getInstance().getReference("images/$userId.jpg")

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

                val patologiasSeleccionadas = mutableListOf<String>()

                for (spinner in spinnerList) {
                    val selectedPatologia = spinner.selectedItem.toString()
                    if (selectedPatologia.isNotEmpty()) {
                        patologiasSeleccionadas.add(selectedPatologia)
                    }
                }

                if (patologiasSeleccionadas.size == patologiasSeleccionadas.distinct().size) {
                    for (i in 0 until 5) {
                        if (i < patologiasSeleccionadas.size) {
                            if (patologiasSeleccionadas[i].isNotEmpty()) {
                                updates["patologia${i + 1}"] = patologiasSeleccionadas[i]
                            } else {
                                updates["patologia${i + 1}"] = ""
                            }
                        } else {
                            updates["patologia${i + 1}"] = ""
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "Las patologías no pueden repetirse", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                // Subir la imagen. todo: lo quitamos por ahora
                //uploadImage(userId)

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
