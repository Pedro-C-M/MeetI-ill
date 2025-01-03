package com.example.meet_ill.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.meet_ill.data_classes.User
import com.example.meet_ill.repos.UserRepository
import kotlinx.coroutines.launch

class ProfileViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> get() = _user

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    fun loadUserData(userId: String) {
        _loading.value = true
        viewModelScope.launch {
            try {
                val fetchedUser = userRepository.getUserById(userId)
                _user.value = fetchedUser
                _error.value = if (fetchedUser == null) "Usuario no encontrado" else null
            } catch (e: Exception) {
                _error.value = "Error al cargar el usuario"
                Log.e("UserViewModel", "Error al cargar datos", e)
            } finally {
                _loading.value = false
            }
        }
    }

    fun updateUser(userId: String, updates: Map<String, Any>, onSuccess: () -> Unit, onFailure: () -> Unit) {
        userRepository.updateUser(userId, updates, onSuccess = {
            onSuccess()
        }, onFailure = {
            _error.value = "Error al actualizar el usuario"
            onFailure()
        })
    }
}