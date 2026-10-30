package com.example.visionia.shriavis.menuenfoques

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.visionia.R
import com.example.visionia.shriavis.network.PiscinaResponse

class PiscinaSpinnerAdapter(
    private val context: Context,
    private val piscinas: List<PiscinaResponse>
) : BaseAdapter() {

    override fun getCount() = piscinas.size

    override fun getItem(position: Int) = piscinas[position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_spinner_piscina, parent, false)

        val tvNombre = view.findViewById<TextView>(R.id.tvNombrePiscina)
        val tvGps = view.findViewById<TextView>(R.id.tvGps)

        tvNombre.text = piscinas[position].nombre_piscina
        tvGps.text = "GPS Activado"

        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return getView(position, convertView, parent)
    }
}