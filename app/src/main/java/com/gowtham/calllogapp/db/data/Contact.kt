package com.gowtham.calllogapp.db.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity
data class Contact(@PrimaryKey
                   val mobile: Long,val name: String,val image: String,var isFavorite: Boolean=false,
                   var isSelected: Boolean=false,var isSelectionMode:Boolean=false,var isExternalContact:
                     Boolean=false): Parcelable