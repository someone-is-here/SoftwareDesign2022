package com.example.tabatatimer

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.tabatatimer.databinding.FragmentAddTimerBinding
import com.example.tabatatimer.db.TimerItem
import com.example.tabatatimer.db.TrainingDatabase
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener


class AddTimerFragment : Fragment() {
    private lateinit var binding: FragmentAddTimerBinding
    private lateinit var viewModel: TimerViewModel
    private lateinit var items: Array<String>
    private var selectedItem: Int = 0
    private var time = 0
    private var trainingId: Int = -1
    private var timerItemForUpdate:TimerItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        trainingId = requireArguments().getInt("selected_training_id")

        binding = FragmentAddTimerBinding.inflate(inflater, container, false)
        items = resources.getStringArray(R.array.TimerVariants)

        val dao = TrainingDatabase.getInstance(container!!.context).timerDao()
        val factory = TimerViewModelFactory(dao)

        viewModel = ViewModelProvider(this, factory).get(TimerViewModel::class.java)
        viewModel.updateTimerList(trainingId)

        initSpinner()
        initButtons()
        initColorPicker()

        try {
            val timerId = requireArguments().getInt("timer_id")
            initUpdate(timerId)
        }catch(ex:Exception){}

        return binding.root
    }

    private fun initUpdate(timerId: Int) {
        binding.apply {
            viewModel.timers.observe(viewLifecycleOwner, {
                for (item in it) {
                    if (item.id == timerId) {
                        sWithTimersVariants.setSelection(item.name)
                        tvTimer.setText(getFormattedTime(item.value))
                        time = item.value
                        vPreviewSelectedColor.setBackgroundColor(item.color)
                        etTrainingDescription.setText(item.description)
                        timerItemForUpdate = item
                        break
                    }
                }
            })
        }
    }

    private fun initButtons() {
        binding.apply {
            btnIncrementTime.setOnClickListener{
                time += 5
                tvTimer.text = getFormattedTime(time)
            }
            btnDecrementTime.setOnClickListener{
                if(time >= 5) {
                    time -= 5
                    tvTimer.text = getFormattedTime(time)
                }
            }
            btnSubmitTimer.setOnClickListener {
                viewModel.timers?.observe(viewLifecycleOwner) {
                    if (time > 0) {
                        val name = selectedItem
                        val color = (vPreviewSelectedColor!!.background as ColorDrawable).color
                        val description = etTrainingDescription.text.toString()

                        val timerItem = TimerItem(
                            0, time, color, 1,
                            name, description, trainingId,
                            it.size
                        )
                        if(timerItemForUpdate != null){
                            timerItemForUpdate!!.name = selectedItem
                            timerItemForUpdate!!.color = (vPreviewSelectedColor!!.background as ColorDrawable).color
                            timerItemForUpdate!!.description = etTrainingDescription.text.toString()
                            timerItemForUpdate!!.value = time
                            viewModel.updateTimerItem(timerItemForUpdate!!)
                        } else {
                            viewModel.insertTimerItem(timerItem)
                        }

                        val bundle = bundleOf("selected_training_id" to trainingId)
                        findNavController().navigate(
                            R.id.action_addTimerFragment_to_timerFragment,
                            bundle
                        )
                    } else {
                        Toast.makeText(
                            binding.root.context,
                            R.string.WarningTime,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
    private fun getFormattedTime(timeInt: Int): String{
        val hours = timeInt % 86400 / 3600
        val minutes = timeInt % 86400 % 3600 / 60
        val seconds = timeInt % 86400 % 3600 % 60

        return String.format("%02d:%02d:%02d", hours, minutes,seconds)
    }
    private fun initColorPicker() {
        binding.btnPickColor.setOnClickListener{
            ColorPickerDialog.Builder(context)
                .setTitle("ColorPicker Dialog")
                .setPreferenceName("MyColorPickerDialog")
                .setPositiveButton(getString(R.string.confirm),
                    ColorEnvelopeListener { envelope, fromUser ->
                        run {
                            binding.vPreviewSelectedColor.setBackgroundColor(envelope.color)
                        }
                    })
                .setNegativeButton(
                    getString(R.string.cancel)
                ) { dialogInterface, i -> dialogInterface.dismiss() }
                .attachAlphaSlideBar(true) // the default value is true.
                .attachBrightnessSlideBar(true) // the default value is true.
                .setBottomSpace(12) // set a bottom space between the last slidebar and buttons.
                .show()
        }
    }
    private fun initSpinner() {
        binding.apply {

            val adapter = ArrayAdapter(
                root.context,
                android.R.layout.simple_spinner_item, items
            )

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            sWithTimersVariants.adapter = adapter

            sWithTimersVariants.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View, position: Int, id: Long
                ) {
                    selectedItem = position
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
            }
        }
    }
}