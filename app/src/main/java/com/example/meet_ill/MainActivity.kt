package com.example.meet_ill

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.meet_ill.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }



        val navController = findNavController(R.id.fragmentContainer)

        binding.customBar.perfilBtn.setOnClickListener {
            // Para que no pete, solo funciona si estamos en home fragment
            val currentFragment = navController.currentDestination?.id
            if (currentFragment == R.id.homeFragment) {
                val action = HomeFragmentDirections.actionHomeFragmentToProfileFragment()
                navController.navigate(action)
            }
        }

        val bottomViewNav = findViewById<BottomNavigationView>(R.id.navBar)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer) as NavHostFragment
        bottomViewNav.setupWithNavController(navHostFragment.navController)
    }
}