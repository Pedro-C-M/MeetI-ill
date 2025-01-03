package com.example.meet_ill.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class LoginViewModel: ViewModel() {


    private val _loginResult = MutableLiveData<Boolean>()
    val loginResult: LiveData<Boolean>
    get() = _loginResult


    fun login(emailValue:String, passwordValue:String){
        Log.d("aver",emailValue)
        Log.d("aver",passwordValue)
        if(emailValue.isNotEmpty() && passwordValue.isNotEmpty()){//Como es asincrono el createUser le ponemos una funcion callback que es el listener
            FirebaseAuth.getInstance()
                .signInWithEmailAndPassword(
                    emailValue
                    ,passwordValue)
                .addOnCompleteListener{//El parametro que nos llega se llama it (this de los lambdas), podemo poner otro nombre con result -> como normal vaya
                    if(it.isSuccessful){
                        _loginResult.value = true
                    }else{
                        _loginResult.value = false
                    }
                }
        }
    }





}