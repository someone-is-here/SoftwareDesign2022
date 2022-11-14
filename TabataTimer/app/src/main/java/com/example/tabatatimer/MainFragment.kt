package com.example.tabatatimer

import android.os.Bundle
import android.view.*
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat.recreate
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.tabatatimer.databinding.FragmentMainBinding
import com.example.tabatatimer.db.Training
import com.example.tabatatimer.db.TrainingDatabase
import com.google.android.material.card.MaterialCardView


class MainFragment : Fragment() {
    private lateinit var binding: FragmentMainBinding

    private lateinit var  viewModel: TrainingViewModel
    private lateinit var adapter: TrainingRecyclerViewAdapter

    private var selectedTraining: Training? = null
    private var selectedCardView: MaterialCardView? = null
    private var isSelected = false

    private lateinit var languageManager: LanguageManager
    private lateinit var fontManager: FontManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentMainBinding.inflate(inflater, container, false)

        val dao = TrainingDatabase.getInstance(container!!.context).trainingDao()
        val factory = TrainingViewModelFactory(dao)

        viewModel = ViewModelProvider(this, factory).get(TrainingViewModel::class.java)

        initRecycleView()
        initButtons()

        languageManager = LanguageManager(binding.root.context)
        languageManager.updateResource(languageManager.getLanguage())

        fontManager = FontManager(binding.root.context)
        fontManager.updateResource(fontManager.getFont())

        return binding.root
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.NightMode -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                true
            }
            R.id.LightMode -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                true
            }
            R.id.SmallFont -> {
                fontManager.updateResource(0.75f)
                initRecycleView()
                true
            }
            R.id.NormalFont -> {
                fontManager.updateResource(1.0f)
                initRecycleView()
                true
            }
            R.id.BigFont -> {
                fontManager.updateResource(1.25f)
                initRecycleView()
                true
            }
            R.id.Belarusian -> {
                languageManager.updateResource("be")
                initRecycleView()
                true
            }
            R.id.English -> {
                languageManager.updateResource("en")
                initRecycleView()
                true
            }
            R.id.ClearData -> {
                val dao = TrainingDatabase.getInstance(binding.root.context).timerDao()
                val factory = TimerViewModelFactory(dao)

                val timerViewModel = ViewModelProvider(this, factory).get(TimerViewModel::class.java)
                viewModel.trainings.observe(viewLifecycleOwner, {
                    for(item in it){
                        timerViewModel.updateTimerList(item.id)
                        timerViewModel.timers.observe(viewLifecycleOwner, {
                            for(timer in it) {
                                timerViewModel.deleteTimerItem(timer)
                            }
                        })
                        viewModel.deleteTraining(item)
                    }
                })

                viewModel = ViewModelProvider(this, TrainingViewModelFactory(TrainingDatabase.getInstance(binding.root.context).trainingDao())).get(TrainingViewModel::class.java)

                initRecycleView()
                initButtons()

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initButtons() {
        binding.apply {
            btnAddTraining.setOnClickListener{
                findNavController().navigate(R.id.action_mainFragment_to_addFragment)
            }

            btnUpdateTraining.setOnClickListener {
                val bundle = bundleOf(
                    "id_for_update" to selectedTraining?.id,
                    "name_for_update" to selectedTraining?.name,
                    "color_for_update" to selectedTraining?.color,
                )
                findNavController().navigate(R.id.action_mainFragment_to_addFragment, bundle)
            }
            btnDeleteTraining.setOnClickListener {
                selectedCardView!!.isChecked = !selectedCardView!!.isChecked
                selectedTraining?.let { it1 -> viewModel.deleteTraining(it1) }
                isSelected = false
                selectedCardView = null
                Toast.makeText(context, R.string.SuccessfullyRemoved, Toast.LENGTH_SHORT).show()
                hideButtons()
            }
        }
    }


    private fun initRecycleView(){
        binding.rvTrainingItems.layoutManager =  GridLayoutManager(activity, 2)
        adapter = TrainingRecyclerViewAdapter( this) { selectedItem: Training,
                                                       cardView: MaterialCardView?
            ->
            listItemClicked(selectedItem, cardView)
        }

        binding.rvTrainingItems.adapter = adapter
        displayList()
    }
    private fun listItemClicked(training: Training, cardView: MaterialCardView?){
        binding.apply {
            cardView!!.setOnClickListener{
                if (isSelected && selectedCardView == cardView) {
                    selectedCardView!!.isChecked = !selectedCardView!!.isChecked
                    isSelected = false
                    hideButtons()
                } else if(!isSelected){
                    cardView!!.isChecked = !cardView!!.isChecked
                    selectedCardView = cardView!!
                    selectedTraining = training
                    isSelected = true
                    makeVisibleButtons()
                }
            }
            cardView!!.performClick()
        }
    }

    private fun makeVisibleButtons() {
        binding.apply {
            btnUpdateTraining.visibility = VISIBLE
            btnDeleteTraining.visibility = VISIBLE
        }
    }
    private fun hideButtons() {
        binding.apply {
            btnUpdateTraining.visibility = INVISIBLE
            btnDeleteTraining.visibility = INVISIBLE
        }
    }

    private fun displayList(){
        viewModel.trainings.observe(viewLifecycleOwner, {
            adapter.setList(it)
            adapter.notifyDataSetChanged()
        })
    }

}