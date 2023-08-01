package com.example.myplayer.ui.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.AsyncPlayer
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.navArgs
import com.example.myplayer.R
import com.example.myplayer.databinding.FragmentMusicDetailBinding
import com.example.myplayer.models.Song
import com.example.myplayer.other.Utils
import com.example.myplayer.other.Utils.toast


private const val TIME_SKIP = 5000

class MusicDetailFragment : Fragment(R.layout.fragment_music_detail) {

    private var _binding: FragmentMusicDetailBinding? = null
    private val binding get() = _binding!!
    val songArgs: MusicDetailFragmentArgs by navArgs()
    lateinit var song: Song
    private var mediaPlayer: MediaPlayer? = null
    private var seekLength: Int = 0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentMusicDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
        song = songArgs.song!!

        mediaPlayer = MediaPlayer()

        binding.songTitle.text = song.songTitle
        binding.songTitle.isSelected = true
        binding.songArtist.text = song.songArtist
        binding.playerTotalTimeText.text = song.songDuration

        displaySongThumbnail()

        binding.playButton.setOnClickListener {
            playSong()
        }

        binding.forwardButton.setOnClickListener {
            forwardSong()
        }

        binding.backwardButton.setOnClickListener {
            backwardSong()
        }


        binding.loopButton.setOnClickListener {
            if (!mediaPlayer!!.isLooping) {
                mediaPlayer!!.isLooping = true
                binding.loopButton.setBackgroundColor(Color.GRAY)
//                binding.ibRepeat.setImageDrawable(
//                    ContextCompat.getDrawable(
//                        activity?.applicationContext!!,
//                        R.drawable.ic_repeat_white
//                    )
//                )
            } else {
                mediaPlayer!!.isLooping = false
//                binding.loopButton.setImageDrawable(
//                    ContextCompat.getDrawable(
//                        activity?.applicationContext!!,
//                        R.drawable.ic_loop
//                    )
//                )
                binding.loopButton.setBackgroundColor(Color.TRANSPARENT)

            }
        }

        binding.shuffleButton.setOnClickListener {
            activity?.toast("Not Implemented yet :)")
        }

    }




    private fun playSong() {
        if (!mediaPlayer!!.isPlaying) {
            mediaPlayer!!.reset()
            mediaPlayer!!.setDataSource(song.songUri)
            mediaPlayer!!.prepare()
            mediaPlayer!!.seekTo(seekLength)
            mediaPlayer!!.start()

            binding.playButton.setImageDrawable(
                ContextCompat.getDrawable(
                    activity?.applicationContext!!, R.drawable.ic_pause
                )
            )
            binding.animationView.apply {
                visibility = View.VISIBLE
                playAnimation()
            }

            updateSeekBar()



        } else {
            mediaPlayer!!.pause()
            seekLength = mediaPlayer!!.currentPosition
            binding.playButton.setImageDrawable(
                ContextCompat.getDrawable(
                    activity?.applicationContext!!, R.drawable.ic_play_button
                )
            )
            binding.animationView.pauseAnimation()
        }
    }

    private fun clearMediaPlayer() {
        if (mediaPlayer != null) {
            if (mediaPlayer!!.isPlaying) {
                mediaPlayer!!.stop()
            }
            mediaPlayer!!.release()
            mediaPlayer = null
        }
    }



    private fun updateSeekBar() {
        if (mediaPlayer != null) {
            binding.playerCurrentTimeText.text =
                Utils.durationConverter(mediaPlayer!!.currentPosition.toLong())
        }
        seekBarSetUp()
        Handler().postDelayed(runnable, 50)
    }

    val runnable = Runnable { updateSeekBar() }

    private fun seekBarSetUp() {

        if (mediaPlayer != null) {
            binding.playerSeekBar.progress = mediaPlayer!!.currentPosition
            binding.playerSeekBar.max = mediaPlayer!!.duration
        }
        binding.playerSeekBar.setOnSeekBarChangeListener(
            @SuppressLint("AppCompatCustomView")
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    if (fromUser) {
                        mediaPlayer!!.seekTo(progress)
                        binding.playerCurrentTimeText.text =
                            Utils.durationConverter(progress.toLong())
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    if (mediaPlayer != null && mediaPlayer!!.isPlaying) {
                        if (seekBar != null) {
                            mediaPlayer!!.seekTo(seekBar.progress)
                        }
                    }
                }
            })
    }

    override fun onDestroy() {
        super.onDestroy()
        clearMediaPlayer()

    }

    private fun displaySongThumbnail() {
        val metadataRetriever = MediaMetadataRetriever()
        metadataRetriever.setDataSource(song.songUri)
        val thumbnailSongArray: ByteArray? = metadataRetriever.embeddedPicture
        thumbnailSongArray?.let {
            val bitmapImage = BitmapFactory.decodeByteArray(
                thumbnailSongArray, 0, thumbnailSongArray.size
            )
            binding.songThumbnail.scaleType = ImageView.ScaleType.CENTER_CROP
            binding.songThumbnail.setImageBitmap(bitmapImage)
        }
    }

    private fun forwardSong() {
        if (mediaPlayer != null) {
            val currentPosition = mediaPlayer!!.currentPosition
            if (currentPosition + TIME_SKIP <= mediaPlayer!!.duration) {
                mediaPlayer!!.seekTo(currentPosition + TIME_SKIP)
            } else {
                mediaPlayer!!.seekTo(mediaPlayer!!.duration)
            }

        }
    }


    private fun backwardSong() {
        if (mediaPlayer != null) {
            val currentPosition = mediaPlayer!!.currentPosition
            if (currentPosition - TIME_SKIP > 0) {
                mediaPlayer!!.seekTo(currentPosition - TIME_SKIP)
            } else {
                mediaPlayer!!.seekTo(0)
            }
        }
    }


}