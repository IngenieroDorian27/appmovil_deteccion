package com.example.visionia.shriavis.menuenfoques

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.visionia.R
import com.example.visionia.shriavis.network.PiscinaResponse

class PiscinasAdapter(
    private var listaPiscinas: MutableList<PiscinaResponse>,
    private val onUbicacionClick: (PiscinaResponse) -> Unit,

) : RecyclerView.Adapter<PiscinasAdapter.PiscinaViewHolder>() {

    inner class PiscinaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNombre: TextView = itemView.findViewById(R.id.tvItemPiscina)
        val tvUbicacion: TextView = itemView.findViewById(R.id.tvItemUbicacion)
        val btnUbicacion: ImageButton = itemView.findViewById(R.id.btnUbicacion)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PiscinaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_piscina, parent, false)
        return PiscinaViewHolder(view)
    }

    override fun onBindViewHolder(holder: PiscinaViewHolder, position: Int) {
        val piscina = listaPiscinas[position]
        holder.tvNombre.text = piscina.nombre_piscina
        holder.tvUbicacion.text = "Ubicación: ${piscina.ubicacion_piscina}"

        holder.btnUbicacion.setOnClickListener {
            onUbicacionClick(piscina)
        }



        holder.itemView.setOnClickListener {
            if (piscina.latitud != null && piscina.longitud != null) {
                val uri = Uri.parse("geo:${piscina.latitud},${piscina.longitud}?q=${piscina.latitud},${piscina.longitud}(${piscina.nombre_piscina})")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                intent.setPackage("com.google.android.apps.maps")
                holder.itemView.context.startActivity(intent)
            } else {
                Toast.makeText(
                    holder.itemView.context,
                    "No se ha registrado ubicación para esta piscina",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun getItemCount(): Int = listaPiscinas.size

    fun actualizarLista(nuevaLista: MutableList<PiscinaResponse>) {
        listaPiscinas = nuevaLista
        notifyDataSetChanged()
    }

}