package com.gowtham.calllogapp.fragments.phonebook.contacts

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gowtham.calllogapp.databinding.RowContactBinding
import com.gowtham.calllogapp.db.data.Contact
import com.gowtham.calllogapp.utils.ItemClickListener
import timber.log.Timber
import java.util.*

class AdContacts(private val context: Context,private var itemClickListener: ItemClickListener,
                 private var itemLongListener: ItemClickListener?=null) :
    ListAdapter<Contact, RecyclerView.ViewHolder>(DiffCallbackChats()) {

    companion object {
        lateinit var contactList: MutableList<Contact>
        lateinit var allContacts: MutableList<Contact>
    }

    fun filter(query: String) {
        try {
            val list= mutableListOf<Contact>()
            if (query.isEmpty())
                list.addAll(allContacts)
            else {
                for (contact in allContacts) {
                    if (contact.name.toLowerCase(Locale.getDefault())
                            .contains(query.toLowerCase(Locale.getDefault()))) {
                        list.add(contact)
                    }
                }
            }
            submitList(null)
            submitList(list)
        } catch (e: Exception) {
            e.stackTrace
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = RowContactBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder=holder as ViewHolder
        viewHolder.bind(getItem(position),itemClickListener,itemLongListener)
    }

    class ViewHolder(private val binding: RowContactBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Contact, itemClickListener: ItemClickListener,itemLongListener: ItemClickListener?) {
            binding.contact = item
            binding.viewRoot.setOnLongClickListener { v ->
                itemLongListener?.onItemClicked(v, bindingAdapterPosition)
                true
            }
            binding.viewRoot.setOnClickListener { v ->
                itemClickListener.onItemClicked(v, bindingAdapterPosition)
            }
            binding.imgFav.setOnClickListener { v ->
                itemClickListener.onItemClicked(v,bindingAdapterPosition)
            }
            binding.imgCall.setOnClickListener { v ->
                itemClickListener.onItemClicked(v,bindingAdapterPosition)
            }
            binding.checkbox.setOnClickListener { v ->
                itemClickListener.onItemClicked(v,bindingAdapterPosition)
            }
            binding.executePendingBindings()
        }
    }

}

class DiffCallbackChats : DiffUtil.ItemCallback<Contact>() {
    override fun areItemsTheSame(oldItem: Contact, newItem: Contact): Boolean {
        return oldItem.mobile == oldItem.mobile
    }

    override fun areContentsTheSame(oldItem: Contact, newItem: Contact): Boolean {
        return oldItem.isSelectionMode == newItem.isSelectionMode &&
                oldItem.isSelected == newItem.isSelected &&
                oldItem.isFavorite == newItem.isFavorite
    }
}
