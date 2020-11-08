package com.gowtham.calllogapp.db.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gowtham.calllogapp.db.data.Contact
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: Contact)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMultipleContact(users: List<Contact>)

    @Query("DELETE FROM Contact WHERE mobile=:mobile")
    suspend fun deleteContactById(mobile: Long)

    @Query("DELETE FROM Contact WHERE mobile in (:userIds)")
    suspend fun deleteContacts(userIds: List<Long>)

    @Query("SELECT * FROM Contact")
    fun getContacts(): Flow<List<Contact>>

    @Query("SELECT * FROM Contact")
    fun getContactsAsList(): List<Contact>

}