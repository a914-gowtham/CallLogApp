package com.gowtham.calllogapp.fragments.phonebook.contacts

import android.os.Bundle
import android.view.*
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.view.ActionMode
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.gowtham.calllogapp.MainActivity
import com.gowtham.calllogapp.R
import com.gowtham.calllogapp.addRestorePolicy
import com.gowtham.calllogapp.databinding.FContactsBinding
import com.gowtham.calllogapp.db.data.Call
import com.gowtham.calllogapp.db.data.Contact
import com.gowtham.calllogapp.fragments.phonebook.FPhoneBookViewModel
import com.gowtham.calllogapp.utils.ContactScreenState
import com.gowtham.calllogapp.utils.DeleteEvent
import com.gowtham.calllogapp.utils.ItemClickListener
import com.gowtham.calllogapp.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber

@AndroidEntryPoint
class FContacts : Fragment(), ItemClickListener {

    private var contacts = mutableListOf<Contact>()

    private val viewModel: FContactsViewModel by viewModels()

    private val sharedViewModel by activityViewModels<FPhoneBookViewModel>()

    private lateinit var binding: FContactsBinding

    private var actionMode: ActionMode? = null

    private lateinit var adContact: AdContacts

    private var viewDetched = false

    private var lastActionTriggered = 0L

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        binding = FContactsBinding.inflate(layoutInflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        setHasOptionsMenu(true)
        EventBus.getDefault().register(this)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setDataInView()
        subscribeObservers()
    }

    private fun setDataInView() {
        try {
            adContact = AdContacts(requireContext(), this, LongClickListener())
            binding.listConatacs.apply {
                adapter = adContact
                itemAnimator = null
            }
            adContact.addRestorePolicy()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun subscribeObservers() {
        try {
            lifecycleScope.launch {
                viewModel.getContacts().collect { list ->
                    if (sharedViewModel.getState().value is ContactScreenState.IdleState) {
                        Timber.v("List ContactScreenState")
                        contacts = list as MutableList<Contact>
                        AdContacts.contactList = contacts
                        AdContacts.allContacts = contacts
                        adContact.submitList(contacts)
                    }
                }
            }

            sharedViewModel.getState().observe(viewLifecycleOwner, { state ->
                when (state) {
                    ContactScreenState.DeleteState -> {
                        Timber.v("DeleteState")
                        val diff = System.currentTimeMillis() - lastActionTriggered
                        if (diff < 500)  //workaround for actionmode issue
                            return@observe
                        if (actionMode == null) {
                            Timber.v("DeleteState actionMode == null")
                            val act = activity as MainActivity
                            act.startSupportActionMode(actionModeCallback)
                            adContact.submitList(viewModel.getLastList().value)
                        }
                    }
                    ContactScreenState.SearchState -> {
                        adContact.filter(sharedViewModel.getLastQuery().value.toString())
                    }
                    ContactScreenState.IdleState -> {
                        actionMode?.finish()
                        viewModel.setLastList(emptyList())
                        lifecycleScope.launch(Dispatchers.IO) {
                            val list = viewModel.getContactsAsList()
                            Timber.v("List IdleState")
                            updateList(list)
                        }
                    }
                }
            })

            sharedViewModel.getLastQuery().observe(viewLifecycleOwner, { query ->
                if (sharedViewModel.getState().value is ContactScreenState.SearchState)
                    adContact.filter(sharedViewModel.getLastQuery().value.toString())
            })

            viewModel.getLastList().observe(viewLifecycleOwner, { selectedList ->
                if (sharedViewModel.getState().value is ContactScreenState.DeleteState) {
                    val selectedContact = selectedList.filter { it.isSelected }
                    actionMode?.title = "${selectedContact.size}"
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun updateList(list: List<Contact>) {
        Timber.v("List updated")
        withContext(Dispatchers.Main) {
            contacts.clear()
            contacts.addAll(list)
            AdContacts.contactList = contacts
            AdContacts.allContacts = contacts
            adContact.submitList(null)
            adContact.submitList(contacts)
        }
    }

    private val actionModeCallback: ActionMode.Callback = object : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode,menu: Menu): Boolean {
            lastActionTriggered = System.currentTimeMillis()
            actionMode = mode
            Timber.v("onCreateActionMod is null ${actionMode == null}")
            sharedViewModel.setState(ContactScreenState.DeleteState)
            mode.menuInflater.inflate(R.menu.menu_action, menu)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode,menu: Menu): Boolean {
            return false
        }

        override fun onActionItemClicked(mode: ActionMode,item: MenuItem): Boolean {
            return when (item.itemId) {
                R.id.action_del -> {
                    Utils.showDeleteDialog(this@FContacts)
                    true
                }
                else -> {
                    false
                }
            }
        }

        override fun onDestroyActionMode(mode: ActionMode) {
            try {
                Timber.v("onDestroyActionMode")
                actionMode = null
                if (!viewDetched && sharedViewModel.getState().value !is ContactScreenState.IdleState)
                    sharedViewModel.setState(ContactScreenState.IdleState)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    inner class LongClickListener : ItemClickListener {
        override fun onItemClicked(v: View, position: Int) {
            try {
                val currentList = adContact.currentList
                if (actionMode == null) {
                    val act = activity as MainActivity
                    act.startSupportActionMode(actionModeCallback)
                    currentList.map { it.isSelectionMode = true }
                }
                currentList[position].apply {
                    isSelected = !isSelected
                }
                if (currentList.none { it.isSelected })
                    actionMode?.finish()
                viewModel.setLastList(currentList)
                adContact.submitList(currentList)
                adContact.notifyDataSetChanged()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onItemClicked(v: View, position: Int) {
        when (v.id) {
            R.id.view_root -> {
                caseRootView(position)
            }
            R.id.img_fav -> {
                caseFav(position)
            }
            R.id.img_call -> {
                caseCallClicked(position)
            }
            R.id.checkbox -> {
                caseCheckBox(v, position)
            }
        }
    }

    private fun caseCallClicked(position: Int) {
        try {
            val contact = adContact.currentList[position]
            val call = Call(System.currentTimeMillis(), contact)
            viewModel.insertCall(call)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun caseCheckBox(v: View, position: Int) {
        try {
            v as CheckBox
            val currentList = adContact.currentList
            currentList[position].apply {
                isSelected = v.isChecked
            }
            if (currentList.none { it.isSelected })
                actionMode?.finish()
            viewModel.setLastList(currentList)
            adContact.submitList(currentList)
            adContact.notifyDataSetChanged()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun caseRootView(position: Int) {
        try {
            if (sharedViewModel.getState().value is ContactScreenState.DeleteState) {
                val currentList = adContact.currentList
                currentList[position].apply {
                    isSelected = !isSelected
                }
                if (currentList.none { it.isSelected })
                    actionMode?.finish()
                viewModel.setLastList(currentList)
                adContact.submitList(currentList)
                adContact.notifyDataSetChanged()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun caseFav(position: Int) {
        try {
            val currentList = adContact.currentList
            val contact = currentList[position].apply {
                isFavorite = !isFavorite
            }
            adContact.submitList(currentList)
            adContact.notifyItemChanged(position)
            lifecycleScope.launch {
                viewModel.contactDao.insertContact(contact)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun clearActionMode() {
        viewDetched = true
        actionMode?.finish()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onDeleteDialogItemClicked(event: DeleteEvent) {  //event will be called when
        try {
            if (event.isDeleteClicked){
                val lastList = viewModel.getLastList().value
                if (lastList.isNullOrEmpty())
                    return
                val removedContactsIds = lastList.filter { it.isSelected }.map { it.mobile }
                val msg = if (removedContactsIds.size == 1) "1 Contact" else "${removedContactsIds.size} Contacts"
                Toast.makeText(requireContext(), "$msg deleted.", Toast.LENGTH_SHORT).show()
                lifecycleScope.launch {
                    viewModel.deleteContacts(removedContactsIds)
                    delay(200)
                    sharedViewModel.setState(ContactScreenState.IdleState)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}