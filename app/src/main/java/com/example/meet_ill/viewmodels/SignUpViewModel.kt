package com.example.meet_ill.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SignUpViewModel: ViewModel() {

    private val _signUpResult = MutableLiveData<Boolean>()
    val signUpResult: LiveData<Boolean>
    get() = _signUpResult


    fun signUp(email:String, userName:String, name:String, password:String){

        if(email.isNotEmpty()  && password.isNotEmpty()
            && userName.isNotEmpty() && name.isNotEmpty()){//Como es asincrono el createUser le ponemos una funcion callback que es el listener
            FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(
                    email
                    ,password)
                .addOnCompleteListener{//El parametro que nos llega se llama it (this de los lambdas), podemo poner otro nombre con result -> como normal vaya
                    if(it.isSuccessful){
                        _signUpResult.value = true
                        añadirUsuario(email,userName,name)
                    }
                    else{
                        _signUpResult.value = false
                    }
                }
        }

    }

    //Función para añadir al usuario también a la bd
    private fun añadirUsuario(email:String, userName:String, name:String){
        val user = FirebaseAuth.getInstance().currentUser
        val userId = user?.uid
        viewModelScope.launch(Dispatchers.IO) {
            val db = FirebaseFirestore.getInstance()
            db.collection("users").document(userId!!).set(
                hashMapOf(
                    "apodo" to userName,
                    "email" to email,
                    "name" to name,
                    "user-type" to "user",
                    "groupsIds" to mutableListOf<String>()
                )
            )
        }
    }
}