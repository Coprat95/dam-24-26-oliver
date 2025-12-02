package com.example.myapplication

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide

// Versión final y simplificada del MiniPlayerFragment
class MiniPlayerFragment : Fragment() {

    private lateinit var ivCover: ImageView
    private lateinit var tvSongTitle: TextView
    private lateinit var btnPlayPause: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_mini_player, container, false)
        ivCover = view.findViewById(R.id.ivCover)
        tvSongTitle = view.findViewById(R.id.tvSongTitle)
        btnPlayPause = view.findViewById(R.id.btnPlayPause)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnPlayPause.setOnClickListener {
            MusicPlayerManager.playPause(requireContext())
        }

        MusicPlayerManager.currentSong.observe(viewLifecycleOwner, Observer { song ->
            song?.let { updateUI(it) }
        })

        MusicPlayerManager.isPlaying.observe(viewLifecycleOwner, Observer { isPlaying ->
            updatePlayPauseButton(isPlaying)
        })
    }

    private fun updateUI(song: InfoCancion) {
        tvSongTitle.text = song.titulo
        if (song.customImageUriString != null) {
            Glide.with(this).load(Uri.parse(song.customImageUriString)).into(ivCover)
        } else if (song.fotoResId != 0) {
            ivCover.setImageResource(song.fotoResId)
        } else {
            ivCover.setImageResource(R.drawable.nota_musical) // Imagen por defecto
        }
    }

    private fun updatePlayPauseButton(isPlaying: Boolean) {
        if (isPlaying) {
            btnPlayPause.setImageResource(R.drawable.ic_pause)
        } else {
            btnPlayPause.setImageResource(R.drawable.ic_play_arrow)
        }
    }
}
