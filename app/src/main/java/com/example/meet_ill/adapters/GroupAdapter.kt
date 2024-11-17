package com.example.meet_ill.adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import com.example.meet_ill.R
import com.example.meet_ill.data_classes.Grupo
import com.example.meet_ill.databinding.RecyclerContactoItem2Binding


class GroupAdapter(
    val navController: NavController,
    val listaGrupos: List<Grupo>,
    //Añadir aquí la info que se pasa al listener en el futuro
    private val onClickListener: (Grupo?) -> Unit

): RecyclerView.Adapter<GroupAdapter.ViewHolder>() {

    class ViewHolder(
        private val navController: NavController,
        private val binding: RecyclerContactoItem2Binding,
        onClickListener: (Grupo?) -> Unit
    ): RecyclerView.ViewHolder(binding.root){
        private var grupoActual: Grupo? = null

        fun bind(grupo: Grupo){
            binding.tvTitle.text = grupo.titulo
            binding.tvIntegrantes.text = grupo.strIntegrantes
            //Si estamos con el caso de el card de sin grupos
            if(grupo.numeroDeIntegrantes == -1){
                binding.btEntrar.visibility = View.GONE
            }

            // Convertir el nombre a un ID de recurso, si lo hago remoto cambiar, de momento imagenes en drawable
            binding.ivPortada.load(grupo.urlImagen)
            grupoActual = grupo
        }

        init{
            binding.root.setOnClickListener{
                onClickListener(grupoActual)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyclerContactoItem2Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(navController, binding, onClickListener)

    }

    override fun getItemCount(): Int {
        return listaGrupos.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listaGrupos[position])
    }

}