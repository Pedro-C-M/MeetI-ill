package com.example.meet_ill.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.meet_ill.R
import com.example.meet_ill.data_classes.Grupo
import com.example.meet_ill.data_classes.User
import com.example.meet_ill.repos.GroupRepository
import com.example.meet_ill.repos.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class HomeFragmentViewModel(): ViewModel() {
    private var userRepo: UserRepository = UserRepository()
    private var groupRepo: GroupRepository = GroupRepository()

    private val _groupsList =  MutableLiveData<List<Grupo>>()

    val groupsList: LiveData<List<Grupo>>
        get() = _groupsList

    fun getUserGroups(context: Context){
        if (_groupsList.value != null) return // Evita recargar si ya hay datos


        viewModelScope.launch(Dispatchers.IO) {
            val user: User? = userRepo.getUserById(FirebaseAuth.getInstance().currentUser?.uid.toString())
            val groupList = mutableListOf<Grupo>()

            user?.grupsIds?.map { groupId ->
                async { groupRepo.getGrupoById(groupId, context) }
            }?.awaitAll()?.filterNotNull()?.let { groupList.addAll(it) }

            if (groupList.isEmpty()) {
                groupList.add(crearCardSinGrupos())
            }

            _groupsList.postValue(groupList)
        }
    }

    //Si no hay grupo aqui creo un card que en verdad es un grupo indicandolo
    private fun crearCardSinGrupos(): Grupo {
        return Grupo(
            enfermedad = "Sin grupos aún",
            idGrupo = "",
            numeroDeIntegrantes = -1,
            urlImagen = R.drawable.fondo1
        )
    }
}

