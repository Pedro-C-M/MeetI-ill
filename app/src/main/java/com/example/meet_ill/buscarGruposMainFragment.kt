package com.example.meet_ill

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.meet_ill.adapters.FilteredGroupAdapter
import com.example.meet_ill.data_classes.Grupo
import com.example.meet_ill.data_classes.User
import com.example.meet_ill.databinding.FragmentBuscarGruposMainBinding
import com.example.meet_ill.databinding.FragmentHomeBinding
import com.example.meet_ill.repos.GroupRepository
import com.example.meet_ill.repos.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class buscarGruposMainFragment : Fragment() {

    private lateinit var binding: FragmentBuscarGruposMainBinding

    private var userRepo: UserRepository = UserRepository()
    private var groupRepo: GroupRepository = GroupRepository()
    private var listaGrupos:MutableList<Grupo> = mutableListOf()

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

        binding.rgFilters.setOnCheckedChangeListener { _, checkedId ->
            // Filtramos la lista según el RadioButton seleccionado
            when (checkedId) {
                R.id.rbSuggested -> filtrarGrupos("sugeridos")
                R.id.rbAll -> filtrarGrupos("todos")
                R.id.rbJoined -> filtrarGrupos("ya_unido")
            }
        }
    }

    private fun generarRecyclerFiltrado() {

        lifecycleScope.launch(Dispatchers.IO) {
            listaGrupos = groupRepo.getAllGroups(requireContext())
            listaGrupos.size

            withContext(Dispatchers.Main) {
                // Actualizamos el RecyclerView con los grupos obtenidos
                binding.recyclerFilteredGroups.layoutManager = GridLayoutManager(requireContext(), 2)
                binding.recyclerFilteredGroups.adapter = FilteredGroupAdapter(findNavController(),listaGrupos)
            }
        }
    }
    private fun filtrarGrupos(filtro: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val user: User? = userRepo.getUserById(FirebaseAuth.getInstance().currentUser?.uid.toString())
            withContext(Dispatchers.Main) {
                val listaFiltrada = when (filtro) {
                    "sugeridos" -> listaGrupos.filter { it.numeroDeIntegrantes >= 2 }
                    "todos" -> listaGrupos.filter { !(user!!.grupsIds.contains(it.idGrupo))}
                    "ya_unido" -> listaGrupos.filter { user!!.grupsIds.contains(it.idGrupo) }
                    else -> listaGrupos
                }
                var adap: FilteredGroupAdapter = binding.recyclerFilteredGroups.adapter as FilteredGroupAdapter
                adap.updateList(listaFiltrada)
            }
        }
    }
}