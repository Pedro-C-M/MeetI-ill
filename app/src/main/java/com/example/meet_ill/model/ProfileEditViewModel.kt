package com.example.meet_ill.model


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.meet_ill.repos.UserRepository
import kotlinx.coroutines.launch


class ProfileEditViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _username = MutableLiveData<String>()
    val username: LiveData<String> get() = _username

    private val _realName = MutableLiveData<String>()
    val realName: LiveData<String> get() = _realName

    private val _profileImageBase64 = MutableLiveData<String?>()
    val profileImageBase64: LiveData<String?> get() = _profileImageBase64

    private val _selectedPathologies = MutableLiveData<List<String>>()
    val selectedPathologies: LiveData<List<String>> get() = _selectedPathologies

    private val _isProfileUpdated = MutableLiveData<Boolean>()
    val isProfileUpdated: LiveData<Boolean> get() = _isProfileUpdated

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    val userId: String = userRepository.getCurrentUserId() ?: ""

    private var firstLoad = true

    fun loadUserData(userId: String) {
        if(firstLoad){
            viewModelScope.launch {
                try {
                    val user = userRepository.getUserById(userId)
                    _username.value = user?.nombreUsuario
                    _realName.value = user?.nombreReal
                    _selectedPathologies.value = user?.patologias ?: emptyList()
                    _profileImageBase64.value = user?.imagenPerfil
                    firstLoad=false
                } catch (e: Exception) {
                    _errorMessage.value = "Error al cargar los datos del usuario."
                }
            }
        }
    }

    fun updateProfile(userId: String) {
        viewModelScope.launch {
            val updates = mutableMapOf<String, Any>()

            username.value?.let {
                if (it.isNotEmpty()) updates["apodo"] = it
            }
            realName.value?.let {
                if (it.isNotEmpty()) updates["name"] = it
            }
            selectedPathologies.value?.let {
                updates["patologias"] = it
            }
            profileImageBase64.value?.let {
                if (it.isNotEmpty()) updates["imagenPerfil"] = it
            }

            if (updates.isEmpty()) {
                _errorMessage.value = "No hay cambios para guardar."
                return@launch
            }

            try {
                userRepository.updateUser(userId, updates,
                    onSuccess = {
                        _isProfileUpdated.value = true
                    },
                    onFailure = {
                        _errorMessage.value = "Error al actualizar el perfil"
                    }
                )
            } catch (e: Exception) {
                _errorMessage.value = "Error al actualizar el perfil."
            }
        }
    }

    fun setProfileImageBase64(base64Image: String?) {
        _profileImageBase64.value = base64Image
    }

    fun setSelectedPathologies(pathologies: List<String>) {
        val duplicates = pathologies.groupBy { it }
            .filter { it.value.size > 1 }
            .keys

        if (duplicates.isEmpty()) {
            _selectedPathologies.value = pathologies
        } else {
            _errorMessage.value = "Las patologías no pueden repetirse: ${duplicates.joinToString(", ")}"
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun setUsername(newUsername: String) {
        _username.value = newUsername
    }

    fun setRealName(newRealName: String) {
        _realName.value = newRealName
    }

    fun borrarPatologia() {
        val currentPathologies = selectedPathologies.value ?: return
        if (currentPathologies.isNotEmpty()) {
            _selectedPathologies.value = currentPathologies.dropLast(1)
        }
    }


}
