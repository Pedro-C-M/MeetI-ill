package com.example.meet_ill

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.meet_ill.adapters.FilteredGroupAdapter
import com.example.meet_ill.data_classes.Grupo
import com.example.meet_ill.data_classes.User
import com.example.meet_ill.databinding.FragmentBuscarGruposMainBinding
import com.example.meet_ill.repos.GroupRepository
import com.example.meet_ill.repos.UserRepository
import com.example.meet_ill.viewmodels.BuscarGruposFragmentViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BuscarGruposMainFragment : Fragment() {

    private lateinit var binding: FragmentBuscarGruposMainBinding

            ;
    private var listaGrupos: List<Grupo> = mutableListOf()
    private val viewModel: BuscarGruposFragmentViewModel by viewModels()

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
        //generarRecyclerFiltrado()

        binding.recyclerFilteredGroups.layoutManager = GridLayoutManager(requireContext(), 2)
        val adapter = FilteredGroupAdapter(findNavController(), emptyList()) { grupo ->
            viewModel.unirUsuarioAGrupo(grupo)
            Toast.makeText(requireContext(), "Unido al ${grupo.titulo}", Toast.LENGTH_SHORT).show()
        }

        binding.recyclerFilteredGroups.adapter = adapter

        // Observar cambios en los grupos
        viewModel.groupsList.observe(viewLifecycleOwner, Observer { grupos ->
            adapter.updateList(grupos)
        })



        viewModel.fetchGroups(requireContext())

        binding.mbtgFilters.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.btnSuggested -> viewModel.changeFilter("sugeridos")
                    R.id.btnAll -> viewModel.changeFilter("todos")
                    R.id.btnJoined -> viewModel.changeFilter("ya_unido")
                }
            }
        }
    }
}
/**
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
                    "sugeridos" -> listaGrupos.filter {
                        user!!.patologias.contains(it.enfermedad)
                    }
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
}*/