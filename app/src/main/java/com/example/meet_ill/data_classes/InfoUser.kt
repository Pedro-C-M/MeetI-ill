package com.example.meet_ill.data_classes

import android.os.Parcelable
import com.example.meet_ill.util.UserType
import kotlinx.parcelize.Parcelize

@Parcelize
data class InfoUser(
    var idUsuario: String,
    var nombreUsuario: String,
    var imagenPerfil: String, // URI o URL de la imagen de perfil

) : Parcelable

