package com.example.meet_ill.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.meet_ill.data_classes.Grupo
import com.example.meet_ill.data_classes.User
import com.example.meet_ill.repos.GroupRepository
import com.example.meet_ill.repos.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

enum class Filtro {
    SUGERIDOS,
    YA_UNIDOS,
    TODOS
}

class BuscarGruposFragmentViewModel(): ViewModel() {
    private var userRepo: UserRepository = UserRepository()
    private var groupRepo: GroupRepository = GroupRepository()

    private var _todosGrupos: List<Grupo> = emptyList()

    private val _groupsList =  MutableLiveData<List<Grupo>>()
    private val _filterState = MutableLiveData(Filtro.TODOS)

    init {//Sin animo de lucro no tengo muy claro q es esto pero sin ello el cambio de filtro no era observado
        _filterState.observeForever { filtro ->
            filtrarGrupos(filtro)
        }
    }

    val groupsList: LiveData<List<Grupo>>
        get() = _groupsList

    val filterState: LiveData<Filtro>
        get() = _filterState

    fun changeFilter(filtro:String) {
        when (filtro) {
            "sugeridos" -> _filterState.value = Filtro.SUGERIDOS
            "ya_unido" -> _filterState.value = Filtro.YA_UNIDOS
            "todos" -> _filterState.value = Filtro.TODOS
        }
    }

    fun fetchGroups(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            var user: User? =  userRepo.getUserById(FirebaseAuth.getInstance().currentUser?.uid.toString())
            val grupos = groupRepo.getAllGroups(context)

            val gruposProcesados = user?.let { convertirYaUnidos(grupos, it) } ?: emptyList()
            _todosGrupos = gruposProcesados
            _filterState.value?.let { filtrarGrupos(it) }
        }
    }

    fun filtrarGrupos(filtro: Filtro) {
        viewModelScope.launch(Dispatchers.IO) {
            var user: User? =  userRepo.getUserById(FirebaseAuth.getInstance().currentUser?.uid.toString())

            val listaFiltrada = when (filtro) {
                Filtro.SUGERIDOS -> _todosGrupos.filter { user!!.patologias.contains(it.enfermedad) }
                Filtro.YA_UNIDOS -> _todosGrupos.filter { user!!.grupsIds.contains(it.idGrupo) }
                Filtro.TODOS -> _todosGrupos
            } ?: emptyList()
            _groupsList.postValue(listaFiltrada)
        }
    }

    private fun convertirYaUnidos(listaGrupos: List<Grupo>, user: User): List<Grupo> {
        return listaGrupos.map { grupo ->
            grupo.apply { usuarioUnido = user.grupsIds.contains(idGrupo) }
        }
    }

    fun unirUsuarioAGrupo(grupo: Grupo) {
        viewModelScope.launch(Dispatchers.IO) {
            var user: User? = userRepo.getUserById(FirebaseAuth.getInstance().currentUser?.uid.toString())
            groupRepo.meterParticipante(grupo.idGrupo, user!!.idUsuario)
            userRepo.unirGrupo(user.idUsuario, grupo.idGrupo)
        }
    }


}