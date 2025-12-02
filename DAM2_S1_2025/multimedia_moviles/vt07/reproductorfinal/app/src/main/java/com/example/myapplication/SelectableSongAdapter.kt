package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class SelectableSongAdapter(
    private var songs: List<InfoCancion>,
    private val onSongSelected: (InfoCancion, Boolean) -> Unit
) : RecyclerView.Adapter<SelectableSongAdapter.ViewHolder>() {

    private val selectedItems = mutableSetOf<InfoCancion>()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.song_title)
        val artist: TextView = itemView.findViewById(R.id.song_artist)
        val cover: ImageView = itemView.findViewById(R.id.song_cover)
        val checkbox: CheckBox = itemView.findViewById(R.id.song_checkbox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_song_selectable, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val song = songs[position]
        holder.title.text = song.titulo

        if (song.artista.isNullOrEmpty() || song.artista == "<unknown>") {
            holder.artist.visibility = View.GONE
        } else {
            holder.artist.visibility = View.VISIBLE
            holder.artist.text = song.artista
        }

        if (song.customImageUriString != null) {
            Glide.with(holder.itemView.context).load(song.customImageUriString).into(holder.cover)
        } else {
            holder.cover.setImageResource(R.drawable.nota_musical)
        }

        holder.checkbox.isChecked = selectedItems.contains(song)

        holder.itemView.setOnClickListener {
            val isNowSelected: Boolean
            if (selectedItems.contains(song)) {
                selectedItems.remove(song)
                isNowSelected = false
            } else {
                selectedItems.add(song)
                isNowSelected = true
            }
            holder.checkbox.isChecked = isNowSelected
            onSongSelected(song, isNowSelected)
        }
    }

    override fun getItemCount() = songs.size

    fun getSelectedSongs(): List<InfoCancion> {
        return selectedItems.toList()
    }

    fun toggleSelectAll() {
        if (selectedItems.size == songs.size) {
            selectedItems.clear()
        } else {
            selectedItems.clear()
            selectedItems.addAll(songs)
        }
        notifyDataSetChanged()
    }

    fun clearSelection() {
        selectedItems.clear()
        notifyDataSetChanged()
    }

    fun updateSongs(newSongs: List<InfoCancion>) {
        this.songs = newSongs
        notifyDataSetChanged()
    }
}