package com.example.meet_ill.adapters

import android.view.LayoutInflater

import android.view.ViewGroup

import androidx.recyclerview.widget.RecyclerView
import coil3.load

import com.example.meet_ill.data_classes.User

import com.example.meet_ill.databinding.RecyclerParticipanteItemBinding

class ParticipantesAdapter(val listaParcitipantes: List<User>): RecyclerView.Adapter<ParticipantesAdapter.ViewHolder>() {




    class ViewHolder(private val binding: RecyclerParticipanteItemBinding,): RecyclerView.ViewHolder(binding.root){



        fun bind(participante: User){
            binding.ivImagenPerfil.load(participante.imagenPerfil)
            binding.tvNombre.text = participante.nombreUsuario
            //la funcionalidad del botón más tarde

        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {;

        val binding = RecyclerParticipanteItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return listaParcitipantes.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listaParcitipantes[position])
    }


}