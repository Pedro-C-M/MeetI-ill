package com.example.meet_ill.adapters


import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.meet_ill.R
import com.example.meet_ill.data_classes.Contacto

class ContactAdapter(val listaContactos: List<Contacto>): RecyclerView.Adapter<ContactAdapter.ViewHolder>() {

    class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        private var tvNombre: TextView = view.findViewById(R.id.tvNombreContacto)
        private var tvUltMensaje: TextView = view.findViewById(R.id.tvUltimoMensaje)
        private var tvHora: TextView = view.findViewById(R.id.tvHora)
        //De momento no hay imagen
        private var ivPerfilImage: ImageView = view.findViewById(R.id.ivImagenPerfil)

        fun bind(contacto: Contacto){
            tvNombre.text = contacto.nombre
            tvUltMensaje.text = contacto.ultimoMensaje
            tvHora.text = contacto.horaUltimoMensaje
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutElement = R.layout.recycler_contacto_item
        val view = LayoutInflater.from(parent.context).inflate(layoutElement,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listaContactos.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listaContactos[position])
    }

}