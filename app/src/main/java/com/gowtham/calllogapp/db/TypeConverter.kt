package com.gowtham.calllogapp.db

import androidx.room.TypeConverter
import com.gowtham.calllogapp.db.data.Call
import com.gowtham.calllogapp.db.data.Contact
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class TypeConverter {

    @TypeConverter
    fun fromContactToString(contact: Contact): String {
        return Json.encodeToString(contact)
    }

    @TypeConverter
    fun fromStringToContact(contact: String): Contact {
        return Json.decodeFromString(contact)
    }

}