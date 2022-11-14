package com.example.clapapp

import android.annotation.SuppressLint
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.SeekBar
import android.widget.TextView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var seekBar: SeekBar
    private lateinit var runnable: Runnable
    private lateinit var handler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mediaPlayer = MediaPlayer.create(this, R.raw.claps)
        seekBar = findViewById(R.id.sbClapping)
        handler = Handler(Looper.getMainLooper())
        initializeSeekBar()

        val playerButton = findViewById<FloatingActionButton>(R.id.fabPlay)
        playerButton.setOnClickListener{
            if(mediaPlayer == null){
                mediaPlayer = MediaPlayer.create(this, R.raw.claps)
                initializeSeekBar()
            }
            mediaPlayer?.start()
        }
        val pauseButton = findViewById<FloatingActionButton>(R.id.fabPause)
        pauseButton.setOnClickListener{
            mediaPlayer?.pause()
        }
        val stopButton = findViewById<FloatingActionButton>(R.id.fabStop)
        stopButton.setOnClickListener{
            mediaPlayer?.stop()
            mediaPlayer?.reset()
            mediaPlayer?.release()
            if(mediaPlayer != null){
                handler.removeCallbacks(runnable)
                seekBar.progress = 0
            }
            mediaPlayer = null
        }
    }
    @SuppressLint("SetTextI18n")
    private fun initializeSeekBar(){
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

                if(fromUser) {
                    mediaPlayer?.seekTo(progress)
                    Log.i("play", progress.toString())
                }
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })

        val tvPlayed = findViewById<TextView>(R.id.tvPlayed)
        val tvDue = findViewById<TextView>(R.id.tvDue)

        seekBar.max = mediaPlayer!!.duration
        runnable = Runnable {
            seekBar.progress = mediaPlayer!!.currentPosition

            val playedTime =  mediaPlayer!!.currentPosition / 1000
            tvPlayed.text = "$playedTime sec"
            val duration = mediaPlayer!!.duration / 1000
            tvDue.text = "${duration-playedTime} sec"

            handler.postDelayed(runnable, 1000)
            Log.i("play", mediaPlayer!!.currentPosition.toString())
        }
        handler.postDelayed(runnable, 1000)
    }
}