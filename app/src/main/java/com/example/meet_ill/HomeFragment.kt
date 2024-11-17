package com.example.meet_ill

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.meet_ill.adapters.GroupAdapter
import com.example.meet_ill.data_classes.Grupo
import com.example.meet_ill.data_classes.User
import com.example.meet_ill.databinding.FragmentHomeBinding
import com.example.meet_ill.repos.GroupRepository
import com.example.meet_ill.repos.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding

    private var userRepo: UserRepository = UserRepository()
    private var groupRepo: GroupRepository = GroupRepository()


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
        //FirebaseAuth.getInstance().signOut()
        lifecycleScope.launch(Dispatchers.IO) {
            val user: User? = userRepo.getUserById(FirebaseAuth.getInstance().currentUser?.uid.toString())
            val listaGrupos: MutableList<Grupo> = mutableListOf()

            if (user != null) {
                val deferredGrupos = user.grupsIds.map { groupId ->
                    async {
                        // Intentamos obtener cada grupo
                        groupRepo.getGrupoById(groupId, requireContext())
                    }
                }

                // Esperamos a que todos los grupos sean obtenidos
                val grupos = try {
                    deferredGrupos.awaitAll() // Esperamos todos los resultados
                } catch (e: Exception) {
                    Log.e("Error", "Error obteniendo los grupos", e)
                    emptyList<Grupo>() // Retornamos una lista vacía si hay algún error
                }

                // Filtramos los resultados nulos y los añadimos a la lista
                listaGrupos.addAll(grupos.filterNotNull())

                // Volvemos al hilo principal para actualizar la UI
                withContext(Dispatchers.Main) {
                    // Actualizamos el RecyclerView con los grupos obtenidos
                    binding.recyclerGrupos.layoutManager = GridLayoutManager(requireContext(), 2)
                    binding.recyclerGrupos.adapter = GroupAdapter(listaGrupos) { grupo ->
                        // Acción cuando se selecciona un grupo
                    }
                }
            } else {
                // Manejo en caso de que el usuario no tenga grupos
                withContext(Dispatchers.Main) {
                    // Puede mostrar un mensaje si no hay grupos disponibles
                    Log.w("Info", "El usuario no tiene grupos.")
                }
            }
        }
    }
}