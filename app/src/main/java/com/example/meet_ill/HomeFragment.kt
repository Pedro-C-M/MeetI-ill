package com.example.meet_ill

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.meet_ill.adapters.GroupAdapter
import com.example.meet_ill.data_classes.Grupo
import com.example.meet_ill.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding

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
        generarRecyclerContactos()
    }

    private fun generarRecyclerContactos() {

        val listaGrupos = crearGrupos()
        binding.recyclerGrupos.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerGrupos.adapter = GroupAdapter(listaGrupos){ grupo ->
            //val destino = HomeFragmentDirections.actionHomeFragmentToChatFragment(grupo!!)
            //findNavController().navigate(destino)
        }
    }

    private fun crearGrupos(): List<Grupo> {
        return listOf(
            Grupo(
                titulo = "Grupo de pie",
                numeroDeIntegrantes = 5,
                urlImagen = R.drawable.fondo1
            ),
            Grupo(
                titulo = "Grupo de pata",
                numeroDeIntegrantes = 3,
                urlImagen = R.drawable.fondo2
            ),
            Grupo(
                titulo = "Grupo de patonas",
                numeroDeIntegrantes = 7,
                urlImagen = R.drawable.fondo3
            )
        )
    }
}