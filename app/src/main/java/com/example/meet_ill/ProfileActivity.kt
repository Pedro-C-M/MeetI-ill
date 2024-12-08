package com.example.meet_ill

import android.content.Intent
import android.graphics.BitmapFactory
import android.widget.ImageView
import android.os.Bundle
import android.util.Base64
import android.widget.TextView
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.meet_ill.repos.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import java.io.File

class ProfileActivity : AppCompatActivity() {

    private lateinit var imgProfile: ImageView
    private lateinit var tvUsername: TextView
    private lateinit var tvRealName: TextView
    private lateinit var tvCorreo: TextView
    private lateinit var btnEditUser: Button
    private lateinit var btnLogout: Button

    private val userRepository = UserRepository()
    private lateinit var currentUserId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        imgProfile = findViewById(R.id.imgProfile)
        tvUsername = findViewById(R.id.tvUsername)
        tvRealName = findViewById(R.id.tvRealName)
        tvCorreo = findViewById(R.id.tvCorreo)
        btnEditUser = findViewById(R.id.btnEditUser)
        btnLogout = findViewById(R.id.btnLogout)
        currentUserId = userRepository.getCurrentUserId() ?: ""

        cargarDatosUsuario()

        btnEditUser.isEnabled = true
        btnEditUser.setOnClickListener {
            val intent = Intent(this, ProfileEditActivity::class.java)
            startActivity(intent)
        }

        //Pa cerrar sesion
        btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut() // Cierra sesión en Firebase
            Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LoginActivity::class.java) // Redirige al login
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish() // Finaliza la actividad actual
        }
    }

    //Hace que te vayas directamente al Main activity, para que funcione el boton de ir pa tras
    override fun onBackPressed() {

        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)

        super.onBackPressed()
    }

    private fun cargarDatosUsuario() {
        lifecycleScope.launch() {
            val user = userRepository.getUserById(currentUserId)
            if (user != null) {
                tvUsername.text = user.nombreUsuario
                tvRealName.text = user.nombreReal
                tvCorreo.text = user.correo

                cargarPatologias(user.patologias)
                cargarImagen(user.imagenPerfil)

            } else {
                Toast.makeText(
                    this@ProfileActivity,
                    "Error al cargar los datos",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
    private fun cargarPatologias(patologias: MutableList<String>) {
        val layoutPatologias = findViewById<LinearLayout>(R.id.linearLayoutPatologias)
        layoutPatologias?.removeAllViews()

        for (i in 0 until patologias.size) {
            val textView = TextView(this)
            textView.text = patologias[i]
            textView.textSize = 18f // Establecer tamaño de texto
            textView.setPadding(0, 5, 0, 5) // Añadir algo de espacio entre patologías
            layoutPatologias?.addView(textView)
        }
    }


    private fun cargarImagen(imagenPerfil: String) {
        try {
            val decodedBytes = Base64.decode(imagenPerfil, Base64.NO_WRAP)
            val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
            imgProfile.setImageBitmap(bitmap)
        } catch (e: Exception) {
            e.printStackTrace()
            imgProfile.setImageResource(R.drawable.default_profile_image)
        }
    }
}
