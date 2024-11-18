package com.example.meet_ill

import android.content.Intent
import android.graphics.BitmapFactory
import android.widget.ImageView
import android.os.Bundle
import android.widget.TextView
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class ProfileActivity : AppCompatActivity() {

    private lateinit var imgProfile: ImageView
    private lateinit var tvUsername: TextView
    private lateinit var tvRealName: TextView
    private lateinit var tvCorreo: TextView
    private lateinit var btnEditUser: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_profile_menu)

        imgProfile = findViewById(R.id.imgProfile)
        tvUsername = findViewById(R.id.tvUsername)
        tvRealName = findViewById(R.id.tvRealName)
        tvCorreo = findViewById(R.id.tvCorreo)
        btnEditUser = findViewById(R.id.btnEditUser)


        cargarDatosUsuario()

        btnEditUser.isEnabled = true
        btnEditUser.setOnClickListener {
            val intent = Intent(this, ProfileEditActivity::class.java)
            startActivity(intent)
        }
    }
    override fun onBackPressed() {

        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)

        super.onBackPressed()
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
                        tvUsername.text = document.getString("apodo") ?: "Sin apodo"
                        tvRealName.text = document.getString("name") ?: "Sin nombre"
                        tvCorreo.text = document.getString("email") ?: "Sin correo"

                        val patologias = listOf(
                            document.getString("patologia1") ?: "",
                            document.getString("patologia2") ?: "",
                            document.getString("patologia3") ?: "",
                            document.getString("patologia4") ?: "",
                            document.getString("patologia5") ?: ""
                        )

                        val layoutPatologias = findViewById<LinearLayout>(R.id.linearLayoutPatologias)
                        layoutPatologias?.removeAllViews()

                        for (patologia in patologias) {
                            if (patologia.isNotEmpty()) {
                                val textView = TextView(this)
                                textView.text = patologia
                                textView.textSize = 18f // Establecer tamaño de texto
                                textView.setPadding(0, 5, 0, 5) // Añadir algo de espacio entre patologías
                                layoutPatologias?.addView(textView)
                            }
                        }


                        val imageUrl = ""
                        if (!imageUrl.isNullOrEmpty()) {

                            val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl)


                            val localFile = File.createTempFile("profile_image", "jpg")

                            storageReference.getFile(localFile)
                                .addOnSuccessListener {
                                    val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                                    imgProfile.setImageBitmap(bitmap)
                                }
                                .addOnFailureListener {
                                    imgProfile.setImageResource(R.drawable.default_profile_image)
                                    Toast.makeText(this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show()
                                }
                        } else {
                            imgProfile.setImageResource(R.drawable.default_profile_image)
                        }

                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error al cargar los datos", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "No se encontró usuario en sesión", Toast.LENGTH_SHORT).show()
        }
    }
}
