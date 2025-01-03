package com.example.meet_ill

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.meet_ill.viewmodels.LoginViewModel
import com.example.meet_ill.viewmodels.SignUpViewModel
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Arrays

class SignupActivity : AppCompatActivity() {

    private lateinit var launcher : ActivityResultLauncher<Intent>
    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var nicknameEditText: TextInputEditText
    private lateinit var nameEditText: TextInputEditText
    private lateinit var signUpButton: Button
    private lateinit var imageIcon: ImageView
    private lateinit var backButton: Button

    private val viewModel: SignUpViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_signup)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initVars()
        observe()
    }

    private fun observe(){
        viewModel.signUpResult.observe(this){result->
            if(result)
                showHome(emailEditText.text.toString())
            else
                showAlert()
        }
    }


    private fun initVars(){
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.editTextPassword)
        nicknameEditText = findViewById(R.id.nicknameEditText)
        nameEditText = findViewById(R.id.realNameEditText)
        signUpButton = findViewById(R.id.signUpButton)
        imageIcon = findViewById(R.id.logoImageView)
        imageIcon.setImageResource(R.drawable.meetill)
        backButton = findViewById(R.id.backButton)
        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ resultado ->
            //clearInputs()

            var devuelto = resultado.data?.getStringExtra("despido")
            Toast.makeText(this, devuelto, Toast.LENGTH_LONG).show()
        }

        backButton.setOnClickListener {
            val intent = Intent(applicationContext, LoginActivity::class.java)
            launcher.launch(intent)
        }


        signUpButton.setOnClickListener{
            viewModel.signUp(emailEditText.text.toString(),nicknameEditText.text.toString(),
                nameEditText.text.toString(),passwordEditText.text.toString())
        }

    }


    private fun showHome(emailStr: String) {
        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.putExtra("email", emailStr)
        launcher.launch(intent)
    }

    // Crea un diálogo de error
    private fun showAlert() {
        val builder= AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error autenticando al usuario")
        builder.setPositiveButton("Aceptar",null)
        val dialog: AlertDialog = builder.create()
        dialog.show()

    }

}
