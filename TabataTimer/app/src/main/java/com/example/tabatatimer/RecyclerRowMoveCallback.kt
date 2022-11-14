package com.example.tabatatimer

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class RecyclerRowMoveCallback(touchHelperContract: RecyclerViewRowTouchHelperContract): ItemTouchHelper.Callback() {
    private var touchHelperContract: RecyclerViewRowTouchHelperContract
init {
    this.touchHelperContract = touchHelperContract
}
    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val dragFlag = ItemTouchHelper.UP.or(ItemTouchHelper.DOWN)
        return makeMovementFlags(dragFlag, 0)
    }

    override fun isLongPressDragEnabled(): Boolean {
        return true
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return false
    }
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        touchHelperContract.onRowMoved(viewHolder.adapterPosition, target.adapterPosition)
        return true
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        if(actionState != ItemTouchHelper.ACTION_STATE_IDLE){
            if(viewHolder is TimerViewHolder){
                var myViewHolder: TimerViewHolder = viewHolder
                touchHelperContract.onRowSelected(myViewHolder)
            }
        }
        super.onSelectedChanged(viewHolder, actionState)
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)

        if(viewHolder is TimerViewHolder){
            var myViewHolder: TimerViewHolder = viewHolder
            touchHelperContract.onRowClear(myViewHolder)
        }

    }
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
    }
    public interface RecyclerViewRowTouchHelperContract{
        fun onRowMoved(from: Int, to: Int)
        fun onRowSelected(viewHolder: TimerViewHolder)
        fun onRowClear(viewHolder: TimerViewHolder)
    }
}