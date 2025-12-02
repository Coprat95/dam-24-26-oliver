package com.example.presentacion.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.presentacion.R

data class TiempoItem(val idCorredor: String, val punto: String, val tiempo: String)

class TiempoAdapter(private val tiempos: List<TiempoItem>) :
    RecyclerView.Adapter<TiempoAdapter.TiempoViewHolder>() {

    class TiempoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtPunto: TextView = view.findViewById(R.id.txtPunto)
        val txtIdCorredor: TextView = view.findViewById(R.id.txtIdCorredor)
        val txtTiempo: TextView = view.findViewById(R.id.txtTiempo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TiempoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tiempo, parent, false)
        return TiempoViewHolder(view)
    }

    override fun onBindViewHolder(holder: TiempoViewHolder, position: Int) {
        val tiempoItem = tiempos[position]
        holder.txtPunto.text = "Punto: ${tiempoItem.punto}"
        holder.txtIdCorredor.text = "Corredor ID: ${tiempoItem.idCorredor}"
        holder.txtTiempo.text = "Tiempo: ${tiempoItem.tiempo}"
    }

    override fun getItemCount() = tiempos.size
}