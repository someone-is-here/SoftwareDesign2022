package com.example.tabatatimer

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.tabatatimer.databinding.ListItemForTrainingBinding
import com.example.tabatatimer.db.TimerItem
import com.example.tabatatimer.db.Training
import com.example.tabatatimer.db.TrainingDatabase
import com.google.android.material.card.MaterialCardView

class TrainingRecyclerViewAdapter(private val fragment: Fragment, private val clickListener: (Training, MaterialCardView?) -> Unit): RecyclerView.Adapter<TrainingViewHolder>() {
    private val trainingsList = ArrayList<Training>()
    private lateinit var viewModel: TimerViewModel

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrainingViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val listItem = layoutInflater.inflate(R.layout.list_item_for_training, parent, false)
        val items = parent.resources.getStringArray(R.array.TimerVariants)
        val binding = ListItemForTrainingBinding.inflate(LayoutInflater.from(parent.context),parent,false)

        val dao = TrainingDatabase.getInstance(parent.context).timerDao()
        val factory = TimerViewModelFactory(dao)

        viewModel = ViewModelProvider(fragment, factory).get(TimerViewModel::class.java)

        return TrainingViewHolder(binding, fragment, items)
    }

    override fun onBindViewHolder(holder: TrainingViewHolder, position: Int) {
        if(trainingsList.size > 0) {
            viewModel.updateTimerList(trainingsList[position].id)
            viewModel.timers?.observe(this.fragment, {
                if(trainingsList.size > 0) holder.bind(trainingsList[position], clickListener, it)
            })
        }
    }

    override fun getItemCount(): Int {
       return trainingsList.size
    }

    fun setList(trainings: List<Training>){
        trainingsList.clear()
        trainingsList.addAll(trainings)
    }

}

class TrainingViewHolder(private val binding: ListItemForTrainingBinding, private val fragment: Fragment, private val titles: Array<String>):RecyclerView.ViewHolder(binding.root){
    private var cardView: MaterialCardView? = null
    fun bind(training: Training, clickListener: (Training, MaterialCardView?) -> Unit, timers: List<TimerItem>){
        binding.apply {
            cardView = cvForTrainingItem
            tvTrainingName.text = training.name
            var resultForView: String = ""
            for(item in timers){
                resultForView += "*${titles[item.name]} ${item.value}s #${item.repeats}\n"
                if(item.description.isNotEmpty()) {
                    resultForView += "${item.description}\n"
                }
            }
            tvForTitles.text = resultForView
            root.setOnClickListener {
                clickListener(training, cardView)
            }
            btnStartTraining.setOnClickListener{
                if(timers.isNotEmpty()) {
                    val bundle = bundleOf("selected_training_id" to training.id)
                    fragment.findNavController()
                        .navigate(R.id.action_mainFragment_to_timerWorkingFragment, bundle)
                } else {
                    Toast.makeText(binding.root.context, R.string.CantStartTimer, Toast.LENGTH_SHORT).show()
                }
            }
            btnListTimer.setOnClickListener{
                val bundle = bundleOf("selected_training_id" to training.id)
                fragment.findNavController().navigate(R.id.action_mainFragment_to_timerFragment, bundle)

            }
            setColor(training)
        }
    }

    private fun setColor(training: Training){
        cardView?.setBackgroundColor(training.color)
    }
}