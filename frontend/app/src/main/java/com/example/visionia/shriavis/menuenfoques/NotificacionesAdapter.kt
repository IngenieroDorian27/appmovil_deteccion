package com.example.visionia.shriavis.menuenfoques


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.visionia.R
import com.example.visionia.shriavis.network.NotificacionPendienteResponse

class NotificacionesAdapter(
    private val listaNotificaciones: List<NotificacionPendienteResponse>,
    private val onItemClick: (NotificacionPendienteResponse) -> Unit
) : RecyclerView.Adapter<NotificacionesAdapter.NotificacionViewHolder>() {

    class NotificacionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvItemPiscina: TextView = itemView.findViewById(R.id.tvItemPiscina)
        val tvItemResumen: TextView = itemView.findViewById(R.id.tvItemResumen)
        val tvItemFechaHora: TextView = itemView.findViewById(R.id.tvItemFechaHora)
        val tvItemEstado: TextView = itemView.findViewById(R.id.tvItemEstado)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificacionViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notificacion, parent, false)

        return NotificacionViewHolder(vista)
    }

    override fun onBindViewHolder(holder: NotificacionViewHolder, position: Int) {
        val notificacion = listaNotificaciones[position]

        holder.tvItemPiscina.text = notificacion.piscina
        holder.tvItemResumen.text = "Resultado del monitoreo disponible"
        holder.tvItemFechaHora.text = "${notificacion.fecha_alerta} - ${notificacion.hora_alerta}"
        holder.tvItemEstado.text = if (notificacion.visto) "Vista" else "Pendiente"

        holder.itemView.setOnClickListener {
            onItemClick(notificacion)
        }
    }

    override fun getItemCount(): Int {
        return listaNotificaciones.size
    }
}
