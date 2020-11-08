package com.gowtham.calllogapp.fragments.phonebook

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gowtham.calllogapp.utils.ContactScreenState
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityScoped
import timber.log.Timber
import javax.inject.Singleton

@Singleton
class FPhoneBookViewModel @ViewModelInject
               constructor(@ApplicationContext private val context: Context): ViewModel() {

    private val _state = MutableLiveData<ContactScreenState>(ContactScreenState.IdleState)

    private val _lastQuery = MutableLiveData("")

    init {
        Timber.v("Init FPhoneBookViewModel")
    }

    fun setState(state: ContactScreenState){
        _state.value=state
    }

    fun getState() : LiveData<ContactScreenState>{
        return _state
    }


    fun setLastQuery(query: String){
        _lastQuery.value=query
    }

    fun getLastQuery() : LiveData<String>{
        return _lastQuery
    }

    override fun onCleared() {
        super.onCleared()
    }

}