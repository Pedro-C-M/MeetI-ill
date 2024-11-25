package com.example.meet_ill

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.meet_ill.adapters.FilteredGroupAdapter
import com.example.meet_ill.data_classes.Grupo
import com.example.meet_ill.data_classes.User
import com.example.meet_ill.databinding.FragmentBuscarGruposMainBinding
import com.example.meet_ill.repos.GroupRepository
import com.example.meet_ill.repos.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BuscarGruposMainFragment : Fragment() {

    private lateinit var binding: FragmentBuscarGruposMainBinding

    private var userRepo: UserRepository = UserRepository()
    private var groupRepo: GroupRepository = GroupRepository()
    private var listaGrupos:List<Grupo> = mutableListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentBuscarGruposMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        generarRecyclerFiltrado()

        binding.mbtgFilters.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                // Filtramos la lista según el botón seleccionado
                when (checkedId) {
                    R.id.btnSuggested -> filtrarGrupos("sugeridos")
                    R.id.btnAll -> filtrarGrupos("todos")
                    R.id.btnJoined -> filtrarGrupos("ya_unido")
                }
            }
        }
    }

    private fun generarRecyclerFiltrado() {

        lifecycleScope.launch(Dispatchers.IO) {
            listaGrupos = groupRepo.getAllGroups(requireContext())
            listaGrupos.size
            var user: User? =  userRepo.getUserById(FirebaseAuth.getInstance().currentUser?.uid.toString())

            listaGrupos = convertirYaUnidos(listaGrupos, user!!)


            withContext(Dispatchers.Main) {
                // Actualizamos el RecyclerView con los grupos obtenidos
                binding.recyclerFilteredGroups.layoutManager = GridLayoutManager(requireContext(), 2)
                binding.recyclerFilteredGroups.adapter = FilteredGroupAdapter(findNavController(),listaGrupos){ grupo ->

                    lifecycleScope.launch(Dispatchers.IO) {
                        groupRepo.meterParticipante(grupo.idGrupo, user!!.idUsuario)
                        userRepo.unirGrupo(user.idUsuario, grupo.idGrupo)

                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                requireContext(),
                                "Unido al ${grupo.titulo}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                }
            }
        }
    }
    private fun filtrarGrupos(filtro: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val user: User? = userRepo.getUserById(FirebaseAuth.getInstance().currentUser?.uid.toString())
            withContext(Dispatchers.Main) {
                var listaFiltrada = when (filtro) {
                    "sugeridos" -> listaGrupos.filter { it.numeroDeIntegrantes >= 2 }
                    "ya_unido" -> listaGrupos.filter {
                        user!!.grupsIds.contains(it.idGrupo)
                    }
                    else -> listaGrupos
                }

                listaFiltrada = convertirYaUnidos(listaFiltrada, user!!)

                var adap: FilteredGroupAdapter? = null

                //Esta movida paq no pete al ir rapido en el nav
                val adapter = binding.recyclerFilteredGroups.adapter
                if (adapter is FilteredGroupAdapter) {
                    // Si el adaptador es del tipo esperado, asignamos la referencia a adap
                    adap = adapter
                } else {
                    // Si no es del tipo esperado, mostramos un mensaje de error
                    Log.e("AdapterError", "El adaptador no está asignado correctamente o no es del tipo esperado.")
                }

                adap?.updateList(listaFiltrada)
            }
        }
    }

    private fun convertirYaUnidos(listaGrupos: List<Grupo>, user: User): List<Grupo>{
        var returnList = listaGrupos.toMutableList()
        returnList.forEach { grupo ->
            if(user!!.grupsIds.contains(grupo.idGrupo))
                grupo.usuarioUnido = true
        }
        return returnList
    }
}