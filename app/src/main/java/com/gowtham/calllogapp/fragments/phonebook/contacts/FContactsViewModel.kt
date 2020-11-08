package com.gowtham.calllogapp.fragments.phonebook.contacts

import android.content.Context
import android.widget.Toast
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gowtham.calllogapp.db.daos.CallDao
import com.gowtham.calllogapp.db.daos.ContactDao
import com.gowtham.calllogapp.db.data.Call
import com.gowtham.calllogapp.db.data.Contact
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class FContactsViewModel @ViewModelInject constructor(
            @ApplicationContext val context: Context,
            val contactDao: ContactDao, val callDao: CallDao) : ViewModel() {

    private var listSelected = MutableLiveData<List<Contact>>()

    init {
        Timber.v("FContacts init")
    }

    fun getContacts() = contactDao.getContacts()

    fun getContactsAsList() = contactDao.getContactsAsList()

    fun deleteContactById(mobile: Long){
        viewModelScope.launch {
            contactDao.deleteContactById(mobile)
        }
    }

    fun setLastList(list: List<Contact>) {
        listSelected.value=list
    }

    fun updateList(list: List<Contact>) {
        viewModelScope.launch {
            contactDao.insertMultipleContact(list)
        }
    }

    fun getLastList() : LiveData<List<Contact>> {
        return listSelected
    }

    fun deleteContacts(list: List<Long>) {
        viewModelScope.launch {
            contactDao.deleteContacts(list)
        }
    }

    override fun onCleared() {
        super.onCleared()
        Timber.v("FContacts onCleared")
    }

    fun insertCall(call: Call) {
        viewModelScope.launch {
            callDao.insertCall(call)
            Toast.makeText(context, "Called to ${call.contact.name}", Toast.LENGTH_SHORT).show()
        }
    }

}