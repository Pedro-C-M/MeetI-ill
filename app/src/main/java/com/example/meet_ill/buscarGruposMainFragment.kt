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
import com.example.meet_ill.adapters.GroupAdapter
import com.example.meet_ill.data_classes.Grupo
import com.example.meet_ill.databinding.FragmentHomeBinding
import com.example.meet_ill.repos.GroupRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class buscarGruposMainFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

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
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        generarRecyclerFiltrado()
    }

    private fun generarRecyclerFiltrado() {
        lifecycleScope.launch(Dispatchers.IO) {
            listaGrupos = groupRepo.getAllGroups(requireContext())
            listaGrupos.size

            withContext(Dispatchers.Main) {
                // Actualizamos el RecyclerView con los grupos obtenidos
                binding.recyclerGrupos.layoutManager = GridLayoutManager(requireContext(), 2)
                binding.recyclerGrupos.adapter = FilteredGroupAdapter(findNavController(),listaGrupos)
            }
        }

    }

}