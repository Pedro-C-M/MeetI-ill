package com.example.meet_ill.data_classes

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ConjuntoSolicitudes (
    val enfermedad: String,//Esto será la URL o lo que represente la imagen del usuario
    val cantidad: Int,
) : Parcelable