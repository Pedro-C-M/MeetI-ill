package com.example.meet_ill
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Usuario(

    var nombreUsuario: String,
    var nombreReal: String,
    var patologia1: String,
    var patologia2: String,
    var patologia3: String,
    var imagenPerfil: String // URI o URL de la imagen de perfil
) : Parcelable
