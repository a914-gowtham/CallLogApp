package com.gowtham.calllogapp

import android.app.Application
import com.gowtham.calllogapp.db.daos.ContactDao
import com.gowtham.calllogapp.db.data.Contact
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class MApplication : Application() {

    @Inject
    lateinit var contactDao: ContactDao

    override fun onCreate() {
        super.onCreate()
        initTimber()
    }

    private fun initTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(object : Timber.DebugTree() {
                override fun createStackElementTag(element: StackTraceElement): String? {
                    return "CallLogApp/${element.fileName}:${element.lineNumber})#${element.methodName}"
                }
            })
        }
    }
}