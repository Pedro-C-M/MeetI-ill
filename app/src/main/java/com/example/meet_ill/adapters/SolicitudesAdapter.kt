package com.example.meet_ill.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import com.example.meet_ill.data_classes.ConjuntoSolicitudes
import com.example.meet_ill.data_classes.Grupo
import com.example.meet_ill.databinding.RecyclerContactoItem2Binding
import com.example.meet_ill.databinding.RecyclerSolicitudesItemBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

class SolicitudesAdapter(
    var listaSolicitudes: List<ConjuntoSolicitudes>,

): RecyclerView.Adapter<SolicitudesAdapter.ViewHolder>() {

    class ViewHolder(
        private val binding: RecyclerSolicitudesItemBinding,
    ): RecyclerView.ViewHolder(binding.root){
        fun bind(solicitud: ConjuntoSolicitudes){

            binding.tvSolicitudName.text = solicitud.enfermedad.replaceFirstChar {
                it.uppercase()
            }
            binding.progressBar.progress = solicitud.cantidad
            binding.tvProgress.text = "${solicitud.cantidad}/10"

        }
        init{
            //Nada de momento
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyclerSolicitudesItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SolicitudesAdapter.ViewHolder, position: Int) {
        holder.bind(listaSolicitudes[position])
    }

    override fun getItemCount(): Int {
        return listaSolicitudes.size
    }
}