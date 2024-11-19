package com.example.meet_ill.data_classes
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    var idUsuario: String,
    var nombreUsuario: String,
    var nombreReal: String,
    var correo: String,
    var grupsIds: MutableList<String>,
    var patologias: MutableList<String>,
    var imagenPerfil: String, // URI o URL de la imagen de perfil
) : Parcelable {

}
