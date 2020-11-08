package com.gowtham.calllogapp.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.gowtham.calllogapp.db.daos.CallDao
import com.gowtham.calllogapp.db.daos.ContactDao
import com.gowtham.calllogapp.db.data.Call
import com.gowtham.calllogapp.db.data.Contact

@Database(entities = [Contact::class, Call::class],
    version = 1, exportSchema = false)
abstract class ContactDatabase : RoomDatabase()  {
    abstract fun getCallDao(): CallDao
    abstract fun getContactDao(): ContactDao
}