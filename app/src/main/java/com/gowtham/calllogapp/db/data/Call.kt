package com.gowtham.calllogapp.db.data

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Call(@PrimaryKey
                val timeInMillis: Long,
                @Embedded
                val contact: Contact)