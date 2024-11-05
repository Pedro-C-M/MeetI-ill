package com.example.meet_ill.adapters


import android.content.DialogInterface.OnClickListener
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import com.example.meet_ill.R
import com.example.meet_ill.data_classes.Contacto

class ContactAdapter(
    val listaContactos: List<Contacto>,
    //Añadir aquí la info que se pasa al listener en el futuro
    private val onClickListener: (Contacto?) -> Unit

): RecyclerView.Adapter<ContactAdapter.ViewHolder>() {

    class ViewHolder(view: View, onClickListener: (Contacto?) -> Unit): RecyclerView.ViewHolder(view){
        private var tvNombre: TextView = view.findViewById(R.id.tvNombreContacto)
        private var tvUltMensaje: TextView = view.findViewById(R.id.tvUltimoMensaje)
        private var tvHora: TextView = view.findViewById(R.id.tvHora)
        private var contactoActual: Contacto? = null
        //De momento no hay imagen
        private var ivPerfilImage: ImageView = view.findViewById(R.id.ivImagenPerfil)

        fun bind(contacto: Contacto){
            tvNombre.text = contacto.nombre

            tvUltMensaje.text = contacto.ultimoMensaje
            tvHora.text = contacto.horaUltimoMensaje
            contactoActual = contacto
            ivPerfilImage.load(contacto.imagenPerfil)
        }

        init{
            view.setOnClickListener{it ->
                onClickListener(contactoActual)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutElement = R.layout.recycler_contacto_item
        val view = LayoutInflater.from(parent.context).inflate(layoutElement,parent,false)
        return ViewHolder(view,onClickListener)
    }

    override fun getItemCount(): Int {
        return listaContactos.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listaContactos[position])
    }

}