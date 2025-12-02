package com.example.cursaprat.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cursaprat.R

// 🔹 Adaptador para mostrar los tiempos en el RecyclerView
class TiempoAdapter(private val tiempoList: List<TiempoItem>) :
    RecyclerView.Adapter<TiempoAdapter.TiempoViewHolder>() {

    class TiempoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtPunto: TextView = itemView.findViewById(R.id.txtPunto)
        val txtIdCorredor: TextView = itemView.findViewById(R.id.txtIdCorredor)
        val txtTiempo: TextView = itemView.findViewById(R.id.txtTiempo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TiempoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tiempo, parent, false)
        return TiempoViewHolder(view)
    }

    override fun onBindViewHolder(holder: TiempoViewHolder, position: Int) {
        val item = tiempoList[position]
        holder.txtPunto.text = "Punto: ${item.punto}"
        holder.txtIdCorredor.text = "Corredor: ${item.idCorredor}"
        holder.txtTiempo.text = "Tiempo: ${item.tiempo}"
    }

    override fun getItemCount(): Int = tiempoList.size
}