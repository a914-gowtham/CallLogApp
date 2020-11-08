package com.gowtham.calllogapp.utils

import android.view.View

interface ItemClickListener {
    fun onItemClicked(v: View, position: Int)
}