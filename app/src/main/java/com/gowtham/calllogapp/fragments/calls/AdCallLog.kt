package com.gowtham.calllogapp.fragments.calls

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gowtham.calllogapp.databinding.RowCallBinding
import com.gowtham.calllogapp.db.data.Call

class AdCallLog() :
    ListAdapter<Call, RecyclerView.ViewHolder>(DiffCallbackChats()) {

    companion object {
        lateinit var allCalls: MutableList<Call>
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = RowCallBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder=holder as ViewHolder
        viewHolder.bind(getItem(position))
    }

    class ViewHolder(private val binding: RowCallBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Call) {
            binding.call = item
            binding.executePendingBindings()
        }
    }

}

class DiffCallbackChats : DiffUtil.ItemCallback<Call>() {
    override fun areItemsTheSame(oldItem: Call, newItem: Call): Boolean {
        return oldItem.timeInMillis == oldItem.timeInMillis
    }

    override fun areContentsTheSame(oldItem: Call, newItem: Call): Boolean {
        return oldItem.contact == newItem.contact
    }
}
