package com.example.myapplication

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class PlaylistAdapter(
    private val playlists: List<Pair<String, ArrayList<InfoCancion>>>,
    private val onPlaylistClick: (Pair<String, ArrayList<InfoCancion>>) -> Unit,
    private val onPlaylistPlayClick: (Pair<String, ArrayList<InfoCancion>>) -> Unit,
    private val onOptionsMenuClick: (Pair<String, ArrayList<InfoCancion>>, View) -> Unit
) : RecyclerView.Adapter<PlaylistAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cover: ImageView = view.findViewById(R.id.ivPlaylistCover)
        val name: TextView = view.findViewById(R.id.tvPlaylistName)
        val songCount: TextView = view.findViewById(R.id.tvSongCount)
        val options: ImageButton = view.findViewById(R.id.btnPlaylistOptions)
        val play: ImageButton = view.findViewById(R.id.btnPlay)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_playlist, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val playlist = playlists[position]
        holder.name.text = playlist.first
        holder.songCount.text = "${playlist.second.size} canciones"

        val coverUri = playlist.second.firstOrNull()?.customImageUriString
        if (coverUri != null) {
            Glide.with(holder.cover.context).load(Uri.parse(coverUri)).into(holder.cover)
        } else {
            holder.cover.setImageResource(R.drawable.nota_musical)
        }

        holder.itemView.setOnClickListener { onPlaylistClick(playlist) }
        holder.options.setOnClickListener { onOptionsMenuClick(playlist, holder.options) }
        holder.play.setOnClickListener { onPlaylistPlayClick(playlist) }
    }

    override fun getItemCount() = playlists.size
}
