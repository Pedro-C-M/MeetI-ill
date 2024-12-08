package com.example.meet_ill

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.ImageView
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
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
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.IOException


class ProfileEditActivity : AppCompatActivity() {
    private lateinit var imgProfile: ImageView
    private lateinit var etUsername: EditText
    private lateinit var etRealName: EditText
    private lateinit var btnSave: Button


    private lateinit var btnAddSpinner: Button
    private lateinit var btnRemoveSpinner: Button
    private lateinit var spinnersContainer: LinearLayout
    private val spinnerList = mutableListOf<Spinner>()

    private val userRepository = UserRepository()
    private lateinit var currentUserId: String

    private var imagenPerfilBase64: String? = null // Variable para almacenar la imagen seleccionada en Base64

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_edit)


        imgProfile = findViewById(R.id.imgProfile)
        etUsername = findViewById(R.id.etUsername)
        etRealName = findViewById(R.id.etRealName)
        btnSave = findViewById(R.id.btnSave)
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
        cargarImagen()
        btnSave.setOnClickListener { guardarCambios() }
    }

    //TODO Copiar esto para convertir las imagenes y cargarlas
    private fun cargarImagen() {
        lifecycleScope.launch() {
            val user = userRepository.getUserById(currentUserId)
            if (user != null) {
                convertir64aImg(user.imagenPerfil)
            }
        }
    }
    private fun convertir64aImg(imagenPerfil: String) {
        try {
            val decodedBytes = Base64.decode(imagenPerfil, Base64.NO_WRAP)
            val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
            imgProfile.setImageBitmap(bitmap)
        } catch (e: Exception) {
            e.printStackTrace()
            imgProfile.setImageResource(R.drawable.default_profile_image)
        }
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
        if (!imagenPerfilBase64.isNullOrEmpty()) {
            updates["imagenPerfil"] = imagenPerfilBase64!!
        }
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


    private fun seleccionarImagen() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, 100) // Llamamos al selector de imágenes
    }

    // Cuando se sale de la actividad de selección de imagen
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            val imageUri: Uri = data.data!!

            try {
                // Convertir la URI de la imagen a un Bitmap
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)

                // Convertir el Bitmap a Base64
                imagenPerfilBase64 = convertirBitmapABase64(bitmap)

                // Mostrar la imagen seleccionada en el ImageView
                imgProfile.setImageBitmap(bitmap)

            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    // Método para convertir un Bitmap a Base64
    private fun convertirBitmapABase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }

    private fun cargarImagenBase64(base64String: String, imageView: CircleImageView) {
        try {
            val decodedBytes = Base64.decode(base64String, Base64.NO_WRAP)
            val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
            imageView.setImageBitmap(bitmap)
        } catch (e: Exception) {
            e.printStackTrace()
            imageView.setImageResource(R.drawable.default_profile_image)
        }
    }

}
