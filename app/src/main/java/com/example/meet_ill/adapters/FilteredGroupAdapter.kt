package com.example.meet_ill.adapters


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import com.example.meet_ill.data_classes.Grupo
import com.example.meet_ill.databinding.RecyclerContactoItem2Binding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job


class FilteredGroupAdapter(
    val navController: NavController,
    var listaGrupos: List<Grupo>,

    private val onJoinGroup: (Grupo) -> Unit // Callback
): RecyclerView.Adapter<FilteredGroupAdapter.ViewHolder>() {

    private val adapterScope = CoroutineScope(Dispatchers.Main + Job()) // Define el scope del adaptador

    class ViewHolder(
        private val navController: NavController,
        private val binding: RecyclerContactoItem2Binding,

        private val onJoinGroup: (Grupo) -> Unit
    ): RecyclerView.ViewHolder(binding.root){
        private var grupoActual: Grupo? = null

        fun bind(grupo: Grupo){
            binding.tvTitle.text = grupo.titulo
            binding.tvIntegrantes.text = grupo.strIntegrantes
            // Convertir el nombre a un ID de recurso, si lo hago remoto cambiar, de momento imagenes en drawable
            binding.ivPortada.load(grupo.urlImagen)
            grupoActual = grupo

            if(grupo.usuarioUnido!=null && !grupo.usuarioUnido!!){
                binding.btEntrar.text = "Unirse"
            }else{
                binding.btEntrar.text = "Unido"
            }


            binding.btEntrar.setOnClickListener{
                if(grupo.usuarioUnido!=null && !grupo.usuarioUnido!!){
                    onJoinGroup(grupo)
                    binding.btEntrar.text = "Unido"
                }
            }
        }

        init{
            //Nada de momento
        }
    }

    fun updateList(newList: List<Grupo>) {
        listaGrupos = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyclerContactoItem2Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(navController, binding, onJoinGroup)

    }

    override fun onBindViewHolder(holder: FilteredGroupAdapter.ViewHolder, position: Int) {
        holder.bind(listaGrupos[position])
    }

    override fun getItemCount(): Int {
        return listaGrupos.size
    }
}