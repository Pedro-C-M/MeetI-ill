package com.example.meet_ill

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.findNavController


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Boton perfil
        val perfilBtn: Button = findViewById(R.id.perfilBtn)

        val usuario = Usuario("Paquillo1","Paco Menen","Sida",
            "VIH","Mena", "Mena")

        val navController = findNavController(R.id.fragmentContainer)

        perfilBtn.setOnClickListener {
            // Para que no pete, solo funciona si estamos en home fragment
            val currentFragment = navController.currentDestination?.id
            if (currentFragment == R.id.homeFragment) {
                val action = HomeFragmentDirections.actionHomeFragmentToProfileFragment(usuario)
                navController.navigate(action)
            }
        }
    }
}