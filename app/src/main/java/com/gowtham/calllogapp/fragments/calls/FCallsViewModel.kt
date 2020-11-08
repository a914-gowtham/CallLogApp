package com.gowtham.calllogapp.fragments.calls

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.gowtham.calllogapp.db.daos.CallDao
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber

class FCallsViewModel @ViewModelInject constructor(
    @ApplicationContext val context: Context,private val callDao: CallDao): ViewModel() {

    init {
        Timber.v("FCallsViewModel init")
    }

    fun getCalls() = callDao.getCalls()

}