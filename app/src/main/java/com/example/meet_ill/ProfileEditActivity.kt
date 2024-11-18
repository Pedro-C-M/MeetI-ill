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
import androidx.lifecycle.lifecycleScope
import com.example.meet_ill.repos.UserRepository
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch


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

    private val userRepository = UserRepository()
    private lateinit var currentUserId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_edit)


        imgProfile = findViewById(R.id.imgProfile)
        etUsername = findViewById(R.id.etUsername)
        etRealName = findViewById(R.id.etRealName)
        btnSave = findViewById(R.id.btnSave)
        imgProfile.setImageResource(R.drawable.default_profile_image)
        spinnersContainer = findViewById(R.id.spinnersContainer)
        btnAddSpinner = findViewById(R.id.btnAddSpinner)
        btnRemoveSpinner = findViewById(R.id.btnRemoveSpinner)

        currentUserId = userRepository.getCurrentUserId() ?: ""

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


    private fun guardarCambios() {
        //Lo pongo por si modificamos unos campos y otros no
        val updates = mutableMapOf<String, Any>()

        val username = etUsername.text.toString()
        if (username.isNotEmpty()) updates["apodo"] = username

        val realName = etRealName.text.toString()
        if (realName.isNotEmpty()) updates["name"] = realName

        val patologiasSeleccionadas = spinnerList.map { it.selectedItem.toString() }

        if (patologiasSeleccionadas.size != patologiasSeleccionadas.distinct().size) {
            Toast.makeText(this, "Las patologías no pueden repetirse", Toast.LENGTH_SHORT).show()
            return
        }
        if (patologiasSeleccionadas.isNotEmpty()) updates["patologias"] = patologiasSeleccionadas

        // Subir la imagen. todo: lo quitamos por ahora
        //uploadImage(userId)

        if (updates.isEmpty()) {
            Toast.makeText(this, "No hay cambios para guardar", Toast.LENGTH_SHORT).show()
            return
        }

        userRepository.updateUser(currentUserId, updates,
            onSuccess = {
                Toast.makeText(this, "Perfil actualizado con éxito", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
            },
            onFailure = {
                Toast.makeText(this, "Error al actualizar el perfil", Toast.LENGTH_SHORT).show()
            }
        )

    }

    private fun createSpinner(): Spinner {
        val patologias = listOf(
            "Diabetes",
            "VIH",
            "Hipertensión",
            "Asma",
            "Artritis",
            "Epilepsia",
            "Alzheimer",
            "Parkinson",
            "Esclerosis múltiple",
            "Cáncer",
            "Enfermedad cardíaca",
            "EPOC",
            "Insuficiencia renal",
            "Fibromialgia",
            "Lupus",
            "Anemia",
            "Migraña",
            "Obesidad",
            "Hipotiroidismo",
            "Alergias"
        )
        val spinner = Spinner(this)

        val adapter =
            object : ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, patologias) {
                override fun getView(
                    position: Int,
                    convertView: android.view.View?,
                    parent: android.view.ViewGroup
                ): android.view.View {
                    val view = super.getView(position, convertView, parent)
                    val textView = view as TextView
                    textView.textSize = 19f
                    textView.setPadding(20, 20, 20, 20)
                    return view
                }

                override fun getDropDownView(
                    position: Int,
                    convertView: android.view.View?,
                    parent: android.view.ViewGroup
                ): android.view.View {
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

        lifecycleScope.launch() {
            val user = userRepository.getUserById(currentUserId)
            if (user != null) {
                etUsername.hint = user.nombreUsuario
                etRealName.hint = user.nombreReal

                user.patologias.forEach {
                    val spinner = createSpinner()
                    spinnerList.add(spinner)
                    spinnersContainer.addView(spinner)
                }
                for (i in 0 until  user.patologias.size) {
                    if (i < spinnerList.size) {
                        val spinner = spinnerList[i]
                        val adapter = spinner.adapter as ArrayAdapter<String>
                        val position = adapter.getPosition(user.patologias[i])
                        spinner.setSelection(position)
                    }
                }
            }
        }

    }


    //todo Lo de abjo: Cosas para el tema de subir imagenes
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

        storageReference.putFile(selectedImageUri).addOnSuccessListener {

            imgProfile.setImageURI(null)
            Toast.makeText(this, "Successfully Uploaded", Toast.LENGTH_SHORT).show()
            if (progressDialog.isShowing) progressDialog.dismiss()
        }.addOnFailureListener {
            if (progressDialog.isShowing) progressDialog.dismiss()
            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
        }
    }


}
