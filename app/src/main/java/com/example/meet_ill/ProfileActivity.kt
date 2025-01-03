package com.example.meet_ill

import android.content.Intent
import android.graphics.BitmapFactory
import android.widget.ImageView
import android.os.Bundle
import android.util.Base64
import android.widget.TextView
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.meet_ill.model.ProfileViewModel
import com.example.meet_ill.model.ViewModelFactory
import com.example.meet_ill.repos.UserRepository
import com.google.firebase.auth.FirebaseAuth

class ProfileActivity : AppCompatActivity() {

    private lateinit var imgProfile: ImageView
    private lateinit var tvUsername: TextView
    private lateinit var tvRealName: TextView
    private lateinit var tvCorreo: TextView
    private lateinit var btnEditUser: Button
    private lateinit var btnLogout: Button

    private lateinit var profileViewModel: ProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val userRepository = UserRepository()
        profileViewModel = ViewModelProvider(
            this,
            ViewModelFactory(userRepository)
        )[ProfileViewModel::class.java]

        setupUI()
        observeViewModel()

        profileViewModel.loadUserData(userRepository.getCurrentUserId() ?: "")
    }

    private fun setupUI() {
        imgProfile = findViewById(R.id.imgProfile)
        tvUsername = findViewById(R.id.tvUsername)
        tvRealName = findViewById(R.id.tvRealName)
        tvCorreo = findViewById(R.id.tvCorreo)
        btnEditUser = findViewById(R.id.btnEditUser)
        btnLogout = findViewById(R.id.btnLogout)

        btnEditUser.isEnabled = true
        btnEditUser.setOnClickListener {
            val intent = Intent(this, ProfileEditActivity::class.java)
            startActivity(intent)
        }

        btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        val btnBack = findViewById<Button>(R.id.backButton)
        btnBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }
    }

    private fun observeViewModel() {
        profileViewModel.user.observe(this) { user ->
            if (user != null) {
                tvUsername.text = user.nombreUsuario
                tvRealName.text = user.nombreReal
                tvCorreo.text = user.correo
                cargarImagen(user.imagenPerfil)
                cargarPatologias(user.patologias)
            }
        }

        profileViewModel.loading.observe(this) { isLoading ->
            // Mostrar u ocultar un indicador de carga si es necesario
        }

        profileViewModel.error.observe(this) { error ->
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
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

    //Hace que te vayas directamente al Main activity, para que funcione el boton de ir pa tras
    override fun onBackPressed() {

        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)

        super.onBackPressed()
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
