package com.example.meet_ill.adapters

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater

import android.view.ViewGroup

import androidx.recyclerview.widget.RecyclerView
import coil3.load
import com.example.meet_ill.R

import com.example.meet_ill.data_classes.User

import com.example.meet_ill.databinding.RecyclerParticipanteItemBinding
import com.example.meet_ill.repos.GroupRepository
import com.example.meet_ill.repos.UserRepository
import com.example.meet_ill.util.UserType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


class ParticipantesAdapter(val listaParcitipantes: List<User>, private val coroutineScope: CoroutineScope, val tipoUsuario: UserType, val groupId: String,val context: Context,
                           private val onClickListener: (User?) -> Unit): RecyclerView.Adapter<ParticipantesAdapter.ViewHolder>() {


    class ViewHolder(private val binding: RecyclerParticipanteItemBinding, private val coroutineScope: CoroutineScope, tipoUsuario:UserType,  groupId: String,context:Context,onClickListener: (User?) -> Unit): RecyclerView.ViewHolder(binding.root){

        private var usuarioActual: User? = null
        private val userRepository = UserRepository()
        private val groupRepository = GroupRepository()

        fun bind(participante: User){
            coroutineScope.launch {
                val imageBitmap = userRepository.obtenerImgUserParametro(participante.idUsuario)
                if (imageBitmap != null) {
                    binding.ivImagenPerfil.setImageBitmap(imageBitmap)
                } else {
                    binding.ivImagenPerfil.setImageResource(R.drawable.default_profile_image) // Imagen predeterminada
                }
            }
            binding.tvNombre.text = participante.nombreUsuario
            usuarioActual = participante
            //la funcionalidad del botón más tarde

        }

        init{
            if(tipoUsuario.equals(UserType.ADMIN)){
                binding.btAbrirChat.text = "Expulsar"
                binding.btAbrirChat.setBackgroundColor(Color.RED)
                binding.btAbrirChat.setOnClickListener{
                    coroutineScope.launch{
                        groupRepository.abandonarGrupo(groupId,usuarioActual!!.idUsuario)

                        val activity = context as Activity
                        val intent = activity.intent
                        activity.finish()
                        activity.startActivity(intent)
                    }
                }
            }else if(tipoUsuario.equals(UserType.USER)){
                binding.btAbrirChat.setOnClickListener{it ->
                    onClickListener(usuarioActual)
                }
            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {;

        val binding = RecyclerParticipanteItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding,coroutineScope,tipoUsuario,groupId,context,onClickListener)
    }

    override fun getItemCount(): Int {
        return listaParcitipantes.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listaParcitipantes[position])
    }


}