package com.example.myapplication

import android.graphics.Color
import android.graphics.drawable.AnimatedVectorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class SongsAdapter(
    private var songs: List<InfoCancion>,
    private val onItemClick: (Int, View) -> Unit,
    private val onMoreClick: (Int, View) -> Unit,
    private val onSelectionModeChange: (Boolean) -> Unit
) : RecyclerView.Adapter<SongsAdapter.SongViewHolder>() {

    var isSelectionMode = false
    val selectedItems = mutableSetOf<InfoCancion>()
    private var currentPlayingSong: InfoCancion? = null
    private var isPlaying = false

    fun getSelectedItems(): List<InfoCancion> {
        return selectedItems.toList()
    }

    fun setCurrentPlayingSong(song: InfoCancion?) {
        val previousPlayingSong = currentPlayingSong
        currentPlayingSong = song
        if (previousPlayingSong != null) {
            notifyItemChanged(songs.indexOf(previousPlayingSong))
        }
        if (currentPlayingSong != null) {
            notifyItemChanged(songs.indexOf(currentPlayingSong))
        }
    }

    fun setPlayingState(playing: Boolean) {
        val wasPlaying = isPlaying
        isPlaying = playing
        if (wasPlaying != isPlaying && currentPlayingSong != null) {
            notifyItemChanged(songs.indexOf(currentPlayingSong))
        }
    }

    class SongViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgIcon: ImageView = view.findViewById(R.id.imgIcon)
        val tvTitle: TextView = view.findViewById(R.id.tvSongTitle)
        val tvArtist: TextView = view.findViewById(R.id.tvSongArtist)
        val btnMore: ImageButton = view.findViewById(R.id.btnMore)
        val checkbox: CheckBox = view.findViewById(R.id.checkbox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_song, parent, false)
        return SongViewHolder(view)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = songs[position]
        holder.tvTitle.text = song.titulo
        
        if (song.artista.isNullOrEmpty() || song.artista == "<unknown>") {
            holder.tvArtist.visibility = View.GONE
        } else {
            holder.tvArtist.visibility = View.VISIBLE
            holder.tvArtist.text = song.artista
        }

        if (isSelectionMode) {
            holder.checkbox.visibility = View.VISIBLE
            holder.btnMore.visibility = View.GONE
            holder.checkbox.isChecked = selectedItems.contains(song)
        } else {
            holder.checkbox.visibility = View.GONE
            holder.btnMore.visibility = View.VISIBLE
            holder.checkbox.isChecked = false
        }

        if (selectedItems.contains(song)) {
            holder.itemView.setBackgroundColor(Color.parseColor("#505050"))
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT)
        }

        if (song.customImageUriString != null) {
            Glide.with(holder.itemView.context).load(song.customImageUriString).into(holder.imgIcon)
            holder.tvTitle.setTextColor(Color.WHITE)
            holder.tvArtist.setTextColor(Color.parseColor("#B0B0B0"))
            holder.imgIcon.clearColorFilter()
        } else {
            if (song == currentPlayingSong) {
                holder.tvTitle.setTextColor(Color.parseColor("#34F5C5"))
                holder.tvArtist.setTextColor(Color.parseColor("#34F5C5"))
                holder.imgIcon.setImageResource(if (isPlaying) R.drawable.avd_equalizer else R.drawable.ic_playing_indicator)
                holder.imgIcon.setColorFilter(Color.parseColor("#34F5C5"))
                val drawable = holder.imgIcon.drawable
                if (drawable is AnimatedVectorDrawable) {
                    drawable.start()
                }
            } else {
                holder.tvTitle.setTextColor(Color.WHITE)
                holder.tvArtist.setTextColor(Color.parseColor("#B0B0B0"))
                holder.imgIcon.setImageResource(R.drawable.nota_musical)
                holder.imgIcon.clearColorFilter()
            }
        }

        holder.itemView.setOnClickListener {
            if (isSelectionMode) {
                toggleSelection(song)
            } else {
                onItemClick(position, holder.itemView)
            }
        }

        holder.itemView.setOnLongClickListener {
            if (!isSelectionMode) {
                setMode(true)
            }
            toggleSelection(song)
            true
        }

        holder.btnMore.setOnClickListener { view -> onMoreClick(position, view) }
    }

    private fun toggleSelection(song: InfoCancion) {
        if (selectedItems.contains(song)) {
            selectedItems.remove(song)
        } else {
            selectedItems.add(song)
        }
        if (selectedItems.isEmpty() && isSelectionMode) {
            setMode(false)
        }
        notifyItemChanged(songs.indexOf(song))
        onSelectionModeChange(isSelectionMode) // Notificar para actualizar el título
    }

    fun setMode(enabled: Boolean) {
        val changed = isSelectionMode != enabled
        isSelectionMode = enabled
        if (!enabled) {
            selectedItems.clear()
        }
        if (changed) {
            onSelectionModeChange(enabled)
            notifyDataSetChanged()
        }
    }

    fun updateSongs(newSongs: List<InfoCancion>) {
        songs = newSongs
        if (isSelectionMode) {
            setMode(false)
        }
        notifyDataSetChanged()
    }

    fun toggleSelectAll() {
        if (selectedItems.size == songs.size) {
            selectedItems.clear()
        } else {
            selectedItems.clear()
            selectedItems.addAll(songs)
        }
        notifyDataSetChanged()
        onSelectionModeChange(true)
    }

    override fun getItemCount() = songs.size
}