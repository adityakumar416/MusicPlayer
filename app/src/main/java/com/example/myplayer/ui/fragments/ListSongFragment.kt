package com.example.myplayer.ui.fragments

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myplayer.R
import com.example.myplayer.adapters.SongAdapter
import com.example.myplayer.other.Utils.REQUEST_CODE_ASK_PERMISSIONS
import com.example.myplayer.other.Utils.toast
import com.example.myplayer.models.Song
import com.example.myplayer.other.Utils
import com.example.myplayer.databinding.FragmentListSongBinding

class ListSongFragment : Fragment(R.layout.fragment_list_song) {

    private var _binding: FragmentListSongBinding? = null
    private val binding get() = _binding!!
    var songList: MutableList<Song> = ArrayList()
    lateinit var songAdapter: SongAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentListSongBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as AppCompatActivity).supportActionBar?.show()
        if (songList.isEmpty()) {
            loadSongs()
        }
        checkUserPermissions()
        setUpRecyclerView()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setUpRecyclerView() {
        songAdapter = SongAdapter()
        songAdapter.differ.submitList(songList)
        binding.rvSongs.apply {
            adapter = songAdapter
            layoutManager = LinearLayoutManager(activity)
            setHasFixedSize(true)
            addItemDecoration(object :DividerItemDecoration(
                activity,LinearLayout.VERTICAL
            ){})
        }
    }

    @SuppressLint("Range")
    private fun loadSongs() {


        val allSongsURI = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection = MediaStore.Audio.Media.IS_MUSIC + "!=0"
        val sortOrder = " ${MediaStore.Audio.Media.DISPLAY_NAME} ASC"

        val cursor = activity?.applicationContext?.contentResolver!!.query(
            allSongsURI, null, selection, null, sortOrder
        )

        if (cursor != null) {

            while (cursor.moveToNext()) {
                val songURI =
                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                val songAuthor =
                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                val songName =
                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME))
                val songDuration: String? =
                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                val songDurLong: String? = songDuration?.let {
                    Utils.durationConverter(it.toLong())
                }
                songList.add(
                    Song(
                        songName, songAuthor,
                        songURI, songDurLong
                    )
                )
            }
            cursor.close()
        }

    }


    private fun checkUserPermissions() {
        if (activity?.let {
                ActivityCompat.checkSelfPermission(
                    it,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                )
            }
            != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ),
                REQUEST_CODE_ASK_PERMISSIONS
            )
            return
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CODE_ASK_PERMISSIONS -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                activity?.toast("Permission Granted")
            } else {
                activity?.toast("Permission Denied, Add permission!!")
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }


}