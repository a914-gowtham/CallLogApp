package com.gowtham.calllogapp.db.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gowtham.calllogapp.db.data.Call
import com.gowtham.calllogapp.db.data.Contact
import kotlinx.coroutines.flow.Flow

@Dao
interface CallDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCall(user: Call)

    @Query("SELECT * FROM Call")
    fun getCalls(): Flow<List<Call>>

    @Query("SELECT * FROM Call")
    fun getCallAsList(): List<Call>

}