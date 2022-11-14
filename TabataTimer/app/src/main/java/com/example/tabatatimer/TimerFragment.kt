package com.example.tabatatimer

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tabatatimer.databinding.FragmentTimerBinding
import com.example.tabatatimer.db.TimerItem
import com.example.tabatatimer.db.TrainingDatabase

class TimerFragment : Fragment() {
    private lateinit var binding: FragmentTimerBinding
    private lateinit var viewModel: TimerViewModel
    private lateinit var adapter: TimerRecyclerViewAdapter
    private var trainingId: Int = -1
    private var bundle = Bundle()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTimerBinding.inflate(inflater, container, false)
        trainingId = requireArguments().getInt("selected_training_id")

        val dao = TrainingDatabase.getInstance(container!!.context).timerDao()
        val factory = TimerViewModelFactory(dao)

        viewModel = ViewModelProvider(this, factory).get(TimerViewModel::class.java)

        initButtons()
        initRecycleView(container)

        requireActivity()
            .onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    Log.d("MYTAG", "Fragment back pressed invoked ")
                    if (isEnabled) {
                        isEnabled = false
                        findNavController().navigate(R.id.action_timerFragment_to_mainFragment)
                    }

                }
            }
            )

        return binding.root
    }


    private fun initButtons() {
        binding.apply {
            btnAddTimer.setOnClickListener{
                bundle = bundleOf("selected_training_id" to trainingId)
                findNavController().navigate(R.id.action_timerFragment_to_addTimerFragment, bundle)
            }
            btnBackToTrainingsList.setOnClickListener {
                findNavController().navigate(R.id.action_timerFragment_to_mainFragment)
            }
            btnStartTrainingFromTimer.setOnClickListener{
                viewModel.timers?.observe(viewLifecycleOwner, {
                    if(it.isNotEmpty()){
                        bundle = bundleOf("selected_training_id" to trainingId)
                        findNavController().navigate(R.id.action_timerFragment_to_timerWorkingFragment, bundle)
                    } else {
                        Toast.makeText(binding.root.context, R.string.CantStartTimer, Toast.LENGTH_SHORT).show()
                    }
                })

            }
        }
    }

    private fun initRecycleView(container: ViewGroup?){
        binding.rvForTimers.layoutManager = LinearLayoutManager(binding.root.context)
        adapter = TimerRecyclerViewAdapter (
            {selectedItem: TimerItem -> updateSelectedItem(selectedItem)},
            {selectedItem: TimerItem -> updateTraining(selectedItem)},
            {selectedItem: TimerItem -> deleteTraining(selectedItem)}
        )
        val callback: ItemTouchHelper.Callback = RecyclerRowMoveCallback(adapter)
        val itemTouchHelper:ItemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(binding.rvForTimers)

        binding.rvForTimers.adapter = adapter
        displayList()
    }

    private fun deleteTraining(timer: TimerItem) {
        viewModel.deleteTimerItem(timer)
    }

    private fun updateTraining(timer: TimerItem) {
        bundle = bundleOf("selected_training_id" to trainingId,
                                "timer_id" to timer.id)
        findNavController().navigate(R.id.action_timerFragment_to_addTimerFragment, bundle)
    }

    private fun updateSelectedItem(item: TimerItem) {
        viewModel.updateTimerItem(item)
    }

    private fun displayList() {
        viewModel.updateTimerList(trainingId)
        viewModel.timers?.observe(viewLifecycleOwner, {
            adapter.setList(it)
            adapter.notifyDataSetChanged()
        })
    }


}