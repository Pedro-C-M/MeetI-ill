package com.example.meet_ill.adapters

import android.view.LayoutInflater

import android.view.ViewGroup

import androidx.recyclerview.widget.RecyclerView
import coil3.load

import com.example.meet_ill.data_classes.User

import com.example.meet_ill.databinding.RecyclerParticipanteItemBinding




class ParticipantesAdapter(val listaParcitipantes: List<User>,
                           private val onClickListener: (User?) -> Unit): RecyclerView.Adapter<ParticipantesAdapter.ViewHolder>() {




    class ViewHolder(private val binding: RecyclerParticipanteItemBinding,onClickListener: (User?) -> Unit): RecyclerView.ViewHolder(binding.root){

        private var usuarioActual: User? = null


        fun bind(participante: User){
            binding.ivImagenPerfil.load(participante.imagenPerfil)
            binding.tvNombre.text = participante.nombreUsuario
            usuarioActual = participante
            //la funcionalidad del botón más tarde

        }

        init{
            binding.btAbrirChat.setOnClickListener{it ->
                onClickListener(usuarioActual)
            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {;

        val binding = RecyclerParticipanteItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding,onClickListener)
    }

    override fun getItemCount(): Int {
        return listaParcitipantes.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listaParcitipantes[position])
    }


}