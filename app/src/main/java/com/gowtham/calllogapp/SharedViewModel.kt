package com.gowtham.calllogapp

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gowtham.calllogapp.utils.ContactScreenState
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityScoped
import timber.log.Timber
import java.util.*
import javax.inject.Singleton
import kotlin.concurrent.schedule

@Singleton
class SharedViewModel @ViewModelInject
constructor(@ApplicationContext private val context: Context) : ViewModel() {

   private val _state = MutableLiveData<ContactScreenState>(ContactScreenState.IdleState)

    private var timer: TimerTask? = null


    init {
        Timber.v("Init SharedViewModel")
    }

    fun setState(state: ContactScreenState){
        _state.value=state
    }

    override fun onCleared() {
        super.onCleared()
        Timber.v("onCleared SharedViewModel")
    }
}