package com.example.tabatatimer

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tabatatimer.databinding.ListItemForTimerBinding
import com.example.tabatatimer.db.TimerItem
import com.example.tabatatimer.db.Training
import com.google.android.material.card.MaterialCardView
import java.util.*
import kotlin.collections.ArrayList

class TimerRecyclerViewAdapter(private val updateValueClickListener: (TimerItem) -> Unit,
                               private val updateClickListener: (TimerItem) -> Unit,
                               private val deleteClickListener: (TimerItem) -> Unit
                                ): RecyclerView.Adapter<TimerViewHolder>(),
                                RecyclerRowMoveCallback.RecyclerViewRowTouchHelperContract {
    private val timerList = ArrayList<TimerItem>()
    private lateinit var items: Array<String>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimerViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val listItem = layoutInflater.inflate(R.layout.list_item_for_timer, parent, false)

        items = parent.resources.getStringArray(R.array.TimerVariants)
        val binding = ListItemForTimerBinding.inflate(LayoutInflater.from(parent.context),parent,false)

        return TimerViewHolder(binding, items)
    }
    override fun onBindViewHolder(holder: TimerViewHolder, position: Int) {
        holder.bind(timerList[position], updateValueClickListener, updateClickListener, deleteClickListener)
    }

    override fun getItemCount(): Int {
        return timerList.size
    }

    fun setList(timerItems: List<TimerItem>){
        timerList.clear()
        timerList.addAll(timerItems)
    }

    override fun onRowMoved(from: Int, to: Int) {
        val priority = timerList[from].timerPriority
        timerList[from].timerPriority = timerList[to].timerPriority
        timerList[to].timerPriority = priority

        if(from < to){
            for(item in from..(to-1)){
                Collections.swap(timerList, item, item + 1)
            }
        } else {
            for(item in from downTo (to+1)){
                Collections.swap(timerList, item, item - 1)
            }
        }
        notifyItemMoved(from, to)
    }

    override fun onRowSelected(viewHolder: TimerViewHolder) {
        viewHolder.background = androidx.transition.R.color.tooltip_background_dark
        viewHolder.itemView.setBackgroundColor(viewHolder.color)
    }

    override fun onRowClear(viewHolder: TimerViewHolder) {
        viewHolder.itemView.setBackgroundColor(viewHolder.background)
        for(item in timerList){
            viewHolder.update(item, updateValueClickListener)
        }
    }
}

class TimerViewHolder(private val binding: ListItemForTimerBinding, private val items:Array<String>):
    RecyclerView.ViewHolder(binding.root){

    var color = 0
    var background = 0

    @SuppressLint("ResourceAsColor")
    fun bind(timer: TimerItem,
             clickListener: (TimerItem) -> Unit,
             updateClickListener: (TimerItem) -> Unit,
             deleteClickListener: (TimerItem) -> Unit){
        binding.apply {
            tvNameOfTimer.text = items[timer.name]
            tvTime.text = timer.value.toString()+ "s"
            tvTimerRepeats.text = timer.repeats.toString()
            setColor(timer)
            btnIncrementRepeats.setOnClickListener{
                timer.repeats += 1
                tvTimerRepeats.text = timer.repeats.toString()
                clickListener(timer)
            }
            btnDecrementRepeats.setOnClickListener{
                if(timer.repeats > 1) {
                    timer.repeats -= 1
                    tvTimerRepeats.text = timer.repeats.toString()
                }
                clickListener(timer)
            }
            btnDeleteTraining.setOnClickListener{
                deleteClickListener(timer)
            }
            btnUpdateTraining.setOnClickListener{
                updateClickListener(timer)
            }
            val color=androidx.transition.R.color.tooltip_background_dark
            this.root.setCardBackgroundColor(color)
        }
    }

    private fun setColor(timer: TimerItem){
        binding.vTimerColor?.setBackgroundColor(timer.color)
        color = timer.color
    }
    fun update(timer: TimerItem, clickListener: (TimerItem) -> Unit){
        clickListener(timer)
    }
}