package com.example.tabatatimer

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.*
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.tabatatimer.databinding.FragmentTimerWorkingBinding
import com.example.tabatatimer.db.TimerItem
import com.example.tabatatimer.db.TrainingDatabase
import java.util.*

class TimerWorkingFragment : Fragment() {
    private lateinit var binding: FragmentTimerWorkingBinding
    private var trainingId:Int = -1
    private var listWithTimers: List<TimerItem> = listOf()
    private lateinit var items: Array<String>
    private var currentIndex: Int = 0
    private var repeats: Int = -1
    private lateinit var viewModel: TimerViewModel

    private var isStarted = false
    private lateinit var serviceIntent: Intent
    private var time = 0

    private var mediaPlayer: MediaPlayer? = null
    private var isSoundOff = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onDestroy() {
        activity?.unregisterReceiver(updateTime)
        super.onDestroy()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTimerWorkingBinding.inflate(inflater, container, false)
        trainingId = requireArguments().getInt("selected_training_id")

        items = resources.getStringArray(R.array.TimerVariants)

        val dao = TrainingDatabase.getInstance(container!!.context).timerDao()
        val factory = TimerViewModelFactory(dao)

        viewModel = ViewModelProvider(this, factory).get(TimerViewModel::class.java)

        initList()
        initButtons()

        mediaPlayer = MediaPlayer.create(binding.root.context, R.raw.done)

        serviceIntent = Intent(binding.root.context, TimerService::class.java)
        activity?.registerReceiver(updateTime, IntentFilter(TimerService.UPDATED_TIME))

        return binding.root
    }

    private fun initButtons() {
        binding.apply {
            btnStart.setOnClickListener {
                isStart()
            }
            btnRestart.setOnClickListener {
                reset()
            }
            btnForward.setOnClickListener{
                if(currentIndex + 1 < listWithTimers.size){
                    if(isStarted){
                        stop()
                    }
                    currentIndex += 1
                    repeats = -1
                    initTimer()
                }
            }
            btnBack.setOnClickListener{
                if(currentIndex - 1 >= 0){
                    if(isStarted){
                        stop()
                    }
                    currentIndex -= 1
                    repeats = -1
                    initTimer()
                }
            }
            btnSoundOff.setOnClickListener {
                isSoundOff = !isSoundOff
                if(isSoundOff) {
                    btnSoundOff.setImageResource(R.drawable.ic_sound_on)
                    Toast.makeText(binding.root.context, R.string.VolumeMode, Toast.LENGTH_SHORT).show()
                } else {
                    btnSoundOff.setImageResource(R.drawable.ic_volume_off)
                    Toast.makeText(binding.root.context, R.string.SilentMode, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
//        if (isStarted){
//            stop()
//        }
    }
    private fun initList(){
        viewModel.updateTimerList(trainingId)
        viewModel.timers?.observe(viewLifecycleOwner) {
            listWithTimers = viewModel.timers.value!!
            binding.tvTimeForView.text = listWithTimers[currentIndex].value.toString()
            initTimer()
        }
    }
    private fun initTimer(){
        binding.apply {
            if (repeats == 0) currentIndex++
            if(currentIndex < listWithTimers.size){
                if(repeats <= 0){
                    repeats =  listWithTimers[currentIndex].repeats
                }
                tvTimerTitle.text = items[listWithTimers[currentIndex].name]
                tvDescription.text = listWithTimers[currentIndex].description
                tvTimeForView.setBackgroundColor(listWithTimers[currentIndex].color)
                time = listWithTimers[currentIndex].value
                tvTimeForView.text = time.toString()

                tvRepeatsLeft.text = (repeats-1).toString()
            } else {
                if(isStarted) {
                    stop()
                }

                Toast.makeText(binding.root.context, R.string.TrainingDone, Toast.LENGTH_SHORT).show()

                if(isSoundOff) {
                mediaPlayer = MediaPlayer.create(binding.root.context, R.raw.applause)
                mediaPlayer?.start()

                    Handler(Looper.getMainLooper()).postDelayed(
                        {
                            mediaPlayer?.stop()
                            mediaPlayer?.reset()
                            mediaPlayer?.release()
                        },
                        5000
                    )
                }
            }
        }
    }

    private fun isStart(){
        if(isStarted){
            stop()
        } else {
            start()
        }
    }

    private fun start(){
        serviceIntent.putExtra(TimerService.CURRENT_TIME, time)
        serviceIntent.putExtra(TimerService.REPEATS_ALL, listWithTimers[currentIndex].repeats)
        serviceIntent.putExtra(TimerService.REPEATS_LEFT, repeats)
        serviceIntent.putExtra(TimerService.TIMER_NAME, items[listWithTimers[currentIndex].name])
        serviceIntent.putExtra(TimerService.TIMER_DESCRIPTION, listWithTimers[currentIndex].description)

        activity?.startService(serviceIntent)

        binding.apply {
            btnStart.setImageResource(R.drawable.ic_pause)
        }
        isStarted = true
        Log.i("MYTAG", "start")
    }
    private fun stop(){
        activity?.stopService(serviceIntent)
        binding.apply {
            btnStart.setImageResource(R.drawable.ic_start)
        }
        isStarted = false

        Log.i("MYTAG", "Stop")
    }
    private fun reset(){
        if(isStarted){
            stop()
        }
        currentIndex = 0
        repeats = -1

        initList()
        initTimer()
    }
    private val updateTime: BroadcastReceiver = object: BroadcastReceiver(){
        override fun onReceive(p0: Context, intent: Intent) {
                time = intent.getIntExtra(TimerService.CURRENT_TIME, 0)

                if (time == 1 && isSoundOff) {
                    mediaPlayer = MediaPlayer.create(binding.root.context, R.raw.done)
                    mediaPlayer?.start()
                }

                if (time == 0) {
                    binding.tvTimeForView.text = time.toString()

                    if (isSoundOff) {
                        mediaPlayer?.stop()
                        mediaPlayer?.reset()
                        mediaPlayer?.release()
                    }

                    repeats -= 1
                    initTimer()

                    if (isStarted) {
                        stop()
                        start()
                    }
                } else {
                    binding.tvTimeForView.text = time.toString()
                }
        }
    }
}

