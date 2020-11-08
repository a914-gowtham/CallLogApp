package com.gowtham.calllogapp.fragments.phonebook

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.gowtham.calllogapp.R
import com.gowtham.calllogapp.databinding.FPhoneBookBinding
import com.gowtham.calllogapp.fragments.phonebook.contacts.FContacts
import com.gowtham.calllogapp.fragments.phonebook.fav.FFavs
import com.gowtham.calllogapp.gone
import com.gowtham.calllogapp.show
import com.gowtham.calllogapp.utils.ContactScreenState
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class FPhoneBook : Fragment() {

    private lateinit var binding: FPhoneBookBinding

    private var fFavs=FFavs()

    private var fContacts=FContacts()

    private val title= arrayOf("Favorites","Contacts")

    private lateinit var searchView: SearchView

    private lateinit var searchItem: MenuItem

    private val viewModel by activityViewModels<FPhoneBookViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        binding = FPhoneBookBinding.inflate(layoutInflater, container, false)
        binding.lifecycleOwner=viewLifecycleOwner
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
         setDataInView()
         subscribeObservers()
    }

    private fun subscribeObservers() {
        try {
            viewModel.getState().observe(viewLifecycleOwner,{ state->
                Timber.v("State Changed $state")
                when(state){
                    ContactScreenState.DeleteState -> {
                        searchItem.collapseActionView()
                        viewModel.setLastQuery("")
                        binding.viewPager.isUserInputEnabled=false
                        binding.tabs.gone()
                    }
                    ContactScreenState.SearchState -> {
                        binding.viewPager.isUserInputEnabled=false
                        binding.tabs.gone()
                    }
                    ContactScreenState.IdleState -> {
                        viewModel.setLastQuery("")
                        binding.viewPager.isUserInputEnabled=true
                        binding.tabs.show()
                    }
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun setDataInView() {
        try {
            binding.viewPager.apply {
                isSaveEnabled=true
                offscreenPageLimit=2
                adapter=ViewPagerAdapter(requireActivity())
            }
            val tabLayoutMediator = TabLayoutMediator(binding.tabs,
                                binding.viewPager, true){ tab: TabLayout.Tab, position: Int ->
                tab.text = title[position]
            }
            tabLayoutMediator.attach()

            binding.toolbar.inflateMenu(R.menu.menu_search)
            searchItem = binding.toolbar.menu.findItem(R.id.action_search)
            searchView = searchItem.actionView as SearchView
            searchView.apply {
                maxWidth = Integer.MAX_VALUE
                queryHint = getString(R.string.txt_search)
            }

            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    viewModel.setLastQuery(newText.toString())
                    return true
                }
            })

            searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
                override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                    viewModel.setState(ContactScreenState.SearchState)
                    return true
                }

                override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                    if (viewModel.getState().value is ContactScreenState.SearchState)
                        viewModel.setState(ContactScreenState.IdleState)
                    return true
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private inner class ViewPagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> fFavs
                else -> fContacts
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fContacts.clearActionMode()
    }

}