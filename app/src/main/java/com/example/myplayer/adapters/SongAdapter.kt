package com.example.myplayer.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.myplayer.R
import com.example.myplayer.databinding.ListSongItemBinding
import com.example.myplayer.models.Song
import com.example.myplayer.ui.fragments.ListSongFragmentDirections

class SongAdapter():RecyclerView.Adapter<SongAdapter.SongViewHolder>() {
    inner class SongViewHolder( val binding: ListSongItemBinding) :RecyclerView.ViewHolder(binding.root) {

        fun bind(song: Song){
            binding.songTitle.text = song.songTitle
            binding.songArtist.text = song.songArtist
        }
    }



    private val differCallback = object :DiffUtil.ItemCallback<Song>(){
        override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem.songUri == newItem.songUri
        }


        override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        return SongViewHolder(
            ListSongItemBinding.bind(LayoutInflater.from(parent.context).inflate(R.layout.list_song_item,parent,false))
        )
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {

        val currentSong = differ.currentList[position]
        holder.bind(currentSong)
        holder.binding.root.setOnClickListener {
            val direction = ListSongFragmentDirections.actionListSongFragmentToMusicDetailFragment(currentSong)
            it.findNavController().navigate(direction)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}