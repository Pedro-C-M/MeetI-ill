package com.example.meet_ill

import android.content.Intent


import android.os.Bundle
import android.widget.Button
import android.widget.EditText
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
import com.example.meet_ill.viewmodels.LoginViewModel
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var signUpButton: Button
    private lateinit var logInButton: Button
    private lateinit var imageIcon: ImageView

    private lateinit var launcher : ActivityResultLauncher<Intent>

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initVars()
        observe()
        comprobarUsuarioLogeado()
    }

    private fun observe(){

        viewModel.loginResult.observe(this){result->
            if(result){
                showHome(emailEditText.text.toString())
            }
            else{
                showAlert()
            }
        }
    }

    private fun comprobarUsuarioLogeado() {

        val currentUser = FirebaseAuth.getInstance().currentUser
        if(currentUser != null){
            showHome(currentUser.email.toString())
        }
    }

    private fun initVars() {
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.editTextPassword)
        signUpButton = findViewById(R.id.signUpButton)
        logInButton = findViewById(R.id.logInButton)
        imageIcon = findViewById(R.id.logoImageView)
        imageIcon.setImageResource(R.drawable.meetill)

        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ resultado ->
            clearInputs()

            var devuelto = resultado.data?.getStringExtra("despido")
            Toast.makeText(this, devuelto, Toast.LENGTH_LONG).show()
        }


        signUpButton.setOnClickListener{
            val intent = Intent(applicationContext, SignupActivity::class.java)
            launcher.launch(intent)
        }

        logInButton.setOnClickListener{
            viewModel.login(emailEditText.text.toString(),passwordEditText.text.toString())
        }
    }

    private fun clearInputs() {
        //Borramos los campos
        emailEditText.text!!.clear()
        passwordEditText.text!!.clear()
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