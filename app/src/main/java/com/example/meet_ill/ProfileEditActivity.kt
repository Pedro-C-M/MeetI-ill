package com.example.meet_ill

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.widget.ImageView
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage


class ProfileEditActivity : AppCompatActivity() {
    private lateinit var imgProfile: ImageView
    private lateinit var etUsername: EditText
    private lateinit var etRealName: EditText
    private lateinit var btnSave: Button

    private lateinit var selectedImageUri: Uri

    private lateinit var btnAddSpinner: Button
    private lateinit var btnRemoveSpinner: Button
    private lateinit var spinnersContainer: LinearLayout
    private val spinnerList = mutableListOf<Spinner>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_edit_profile)


        imgProfile = findViewById(R.id.imgProfile)
        etUsername = findViewById(R.id.etUsername)
        etRealName = findViewById(R.id.etRealName)
        btnSave = findViewById(R.id.btnSave)
        imgProfile.setImageResource(R.drawable.default_profile_image)


        spinnersContainer = findViewById(R.id.spinnersContainer)
        btnAddSpinner = findViewById(R.id.btnAddSpinner)
        btnRemoveSpinner = findViewById(R.id.btnRemoveSpinner)

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

        imgProfile.setOnClickListener {
            seleccionarImagen()
        }

        rellenaHints()

        btnSave.setOnClickListener { guardarCambios() }
    }


    private fun guardarCambios(){
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
            return
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
            Toast.makeText(this, "Las patologías no pueden repetirse", Toast.LENGTH_SHORT).show()
            return
        }

        // Subir la imagen. todo: lo quitamos por ahora
        //uploadImage(userId)


        if (updates.isEmpty()) {
            Toast.makeText(this, "No hay cambios para guardar", Toast.LENGTH_SHORT).show()
            return
        }

        db.collection("users").document(userId)
            .update(updates)
            .addOnSuccessListener {
                Toast.makeText(this, "Perfil actualizado con éxito", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al actualizar el perfil", Toast.LENGTH_SHORT).show()
            }

    }
    private fun createSpinner(): Spinner {
        val patologias = listOf("Diabetes", "VIH", "Hipertensión", "Asma", "Artritis", "Epilepsia",
            "Alzheimer", "Parkinson", "Esclerosis múltiple", "Cáncer", "Enfermedad cardíaca", "EPOC",
            "Insuficiencia renal", "Fibromialgia", "Lupus", "Anemia", "Migraña", "Obesidad",
            "Hipotiroidismo", "Alergias"
        )
        val spinner = Spinner(this)

        val adapter = object : ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, patologias) {
            override fun getView(position: Int, convertView: android.view.View?, parent: android.view.ViewGroup): android.view.View {
                val view = super.getView(position, convertView, parent)
                val textView = view as TextView
                textView.textSize = 19f
                textView.setPadding(20, 20, 20, 20)
                return view
            }

            override fun getDropDownView(position: Int, convertView: android.view.View?, parent: android.view.ViewGroup): android.view.View {
                val view = super.getDropDownView(position, convertView, parent)
                val textView = view as TextView
                textView.textSize = 25f
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

                        val patologias = mutableListOf<String>()
                        for (i in 1..5) {
                            val patologia = document.getString("patologia$i")
                            if (!patologia.isNullOrEmpty()) {
                                patologias.add(patologia)
                            }
                        }
                        for (i in 0 until patologias.size) {
                            val newSpinner = createSpinner()
                            spinnerList.add(newSpinner)
                            spinnersContainer.addView(newSpinner)
                        }

                        // Rellenar los spinners si hay patologías existentes
                        for (i in 0 until patologias.size) {
                            if (i < spinnerList.size) {
                                val spinner = spinnerList[i]
                                val adapter = spinner.adapter as ArrayAdapter<String>
                                val position = adapter.getPosition(patologias[i])
                                spinner.setSelection(position)
                            }
                        }
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
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            data?.data?.let {
                selectedImageUri = it
                imgProfile.setImageURI(selectedImageUri)
            }
        }
    }


    private fun uploadImage(userId: String) {
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Uploading File...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        // Usar la userId para crear el nombre del archivo
        val storageReference = FirebaseStorage.getInstance().getReference("images/$userId.jpg")

        storageReference.putFile(selectedImageUri).
                    addOnSuccessListener {

                        imgProfile.setImageURI(null)
                        Toast.makeText(this, "Successfully Uploaded", Toast.LENGTH_SHORT).show()
                        if(progressDialog.isShowing) progressDialog.dismiss()
                    }.addOnFailureListener{
                        if(progressDialog.isShowing) progressDialog.dismiss()
                        Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
        }
    }


}
