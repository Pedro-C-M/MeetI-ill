package com.example.meet_ill.util

import com.google.firebase.auth.FirebaseAuth
//Object se usa en vez de class para por ejemplo singletons
object AuthSingleton {
    //Para acceder poner sin más AuthSingleton.auth y ya está
    val auth: FirebaseAuth = FirebaseAuth.getInstance()

}