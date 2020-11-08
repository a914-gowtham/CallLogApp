package com.gowtham.calllogapp.fragments.phonebook.fav

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.gowtham.calllogapp.R
import com.gowtham.calllogapp.addRestorePolicy
import com.gowtham.calllogapp.databinding.FFavsBinding
import com.gowtham.calllogapp.db.data.Call
import com.gowtham.calllogapp.db.data.Contact
import com.gowtham.calllogapp.fragments.phonebook.FPhoneBookViewModel
import com.gowtham.calllogapp.hide
import com.gowtham.calllogapp.show
import com.gowtham.calllogapp.utils.ContactScreenState
import com.gowtham.calllogapp.utils.ItemClickListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class FFavs : Fragment(), ItemClickListener {

    private val sharedViewModel by activityViewModels<FPhoneBookViewModel>()

    private var contacts = mutableListOf<Contact>()

    private val viewModel: FFavsViewModel by viewModels()

    private lateinit var binding: FFavsBinding

    private val adFavs by lazy {
        AdFavs(requireContext(),this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        binding = FFavsBinding.inflate(layoutInflater, container, false)
        binding.lifecycleOwner=viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setDataInView()
        subscribeObservers()
    }

    private fun subscribeObservers() {
        lifecycleScope.launch {
            viewModel.getContacts()
                .map { it.filter { item -> item.isFavorite } }
                .collect { list->
                    if(sharedViewModel.getState().value is ContactScreenState.IdleState) {
                        contacts = list.toMutableList().asReversed()
                        AdFavs.allFavs = contacts
                        adFavs.submitList(null)
                        adFavs.submitList(contacts)
                        if(contacts.isEmpty())
                            binding.viewEmpty.show()
                        else
                            binding.viewEmpty.hide()
                    }
                }
        }

        sharedViewModel.getState().observe(viewLifecycleOwner, { state ->
            when (state) {
                ContactScreenState.DeleteState -> {
                }
                ContactScreenState.SearchState -> {
                    adFavs.filter(sharedViewModel.getLastQuery().value.toString())
                }
                ContactScreenState.IdleState -> {
                    lifecycleScope.launch(Dispatchers.IO) {
                        val list = viewModel.getContactsAsList().filter { it.isFavorite }
                        updateList(list)
                    }
                }
            }
        })

        sharedViewModel.getLastQuery().observe(viewLifecycleOwner, { query ->
            if (sharedViewModel.getState().value is ContactScreenState.SearchState)
                adFavs.filter(sharedViewModel.getLastQuery().value.toString())
        })

    }

    private suspend fun updateList(list: List<Contact>) {
        withContext(Dispatchers.Main) {
            contacts= list.toMutableList().asReversed()
            AdFavs.allFavs = contacts
            adFavs.submitList(contacts)
            if(contacts.isEmpty())
                binding.viewEmpty.show()
            else
                binding.viewEmpty.hide()
        }
    }

    private fun setDataInView() {
        try {
            binding.listFavs.apply {
                adapter=adFavs
                itemAnimator = null
            }
            adFavs.addRestorePolicy()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onItemClicked(v: View, position: Int) {
        try {
            if (v.id==R.id.img_fav)
                caseFav(position)
            else if (v.id==R.id.img_call)
                caseCallClicked(position)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun caseFav(position: Int) {
        val currentList = adFavs.currentList.toMutableList()
        val contact = currentList[position].apply {
            isFavorite = false
        }
        currentList.removeAt(position)
        adFavs.submitList(currentList)
        adFavs.notifyItemRemoved(position)
        adFavs.favContactRemoved(contact)
        viewModel.insertContact(contact) //update unfav contact
    }

    private fun caseCallClicked(position: Int) {
        try {
            val contact=adFavs.currentList[position]
            val call= Call(System.currentTimeMillis(),contact)
            viewModel.insertCall(call)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}