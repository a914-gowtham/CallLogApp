package com.gowtham.calllogapp

import android.content.Context
import android.view.View
import androidx.navigation.NavController
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope

fun View.gone(){
    this.visibility= View.GONE
}

fun View.show(){
    this.visibility= View.VISIBLE
}

fun View.hide(){
    this.visibility= View.INVISIBLE
}

fun NavController.isValidDestination(destination: Int): Boolean {
    return destination == this.currentDestination!!.id
}

fun  <T, VH : RecyclerView.ViewHolder> ListAdapter<T, VH>.addRestorePolicy(){
    stateRestorationPolicy =
        RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
}

