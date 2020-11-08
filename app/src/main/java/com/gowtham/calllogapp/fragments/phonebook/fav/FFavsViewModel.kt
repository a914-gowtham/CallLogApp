package com.gowtham.calllogapp.fragments.phonebook.fav

import android.content.Context
import android.widget.Toast
import androidx.hilt.lifecycle.ViewModelInject
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

class FFavsViewModel @ViewModelInject constructor(
    @ApplicationContext val context: Context,
   private val contactDao: ContactDao, private val callDao: CallDao): ViewModel() {

    init {
        Timber.v("FFavsViewModel init")
    }

    fun getContacts() = contactDao.getContacts()

    fun getContactsAsList() = contactDao.getContactsAsList()


    fun insertCall(call: Call) {
        viewModelScope.launch {
            callDao.insertCall(call)
            Toast.makeText(context, "Called to ${call.contact.name}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCleared() {
        super.onCleared()
        Timber.v("FFavsViewModel onCleared")
    }

    fun insertContact(contact: Contact) {
        viewModelScope.launch {
            contactDao.insertContact(contact)
        }
    }
}