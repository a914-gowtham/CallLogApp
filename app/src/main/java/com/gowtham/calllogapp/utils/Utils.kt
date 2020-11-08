package com.gowtham.calllogapp.utils

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.gowtham.calllogapp.fragments.FDeleteDialog
import java.text.SimpleDateFormat

object Utils {

    private const val MIN: Long=1000 * 60
    private const val HOUR= MIN * 60
    private const val DAY= HOUR* 24
    private const val WEEK= DAY * 7

    fun showDeleteDialog(context: Fragment){
        val dialogFragment = FDeleteDialog.newInstance(Bundle())
        dialogFragment.show(context.childFragmentManager, "")
    }

    @SuppressLint("SimpleDateFormat")
    fun getTime(sentTime: Long): String{
        val currentTime=System.currentTimeMillis()
        val difference = currentTime - sentTime
        val calender= java.util.Calendar.getInstance()
        calender.timeInMillis=sentTime
        val date=calender.time
        return when{
            difference> WEEK -> {
                //DD/MM format
                SimpleDateFormat("dd:MM").format(date)
            }
            difference== DAY -> {
                "Yesterday"
            }
            else->{
                //hh:mm aa
                SimpleDateFormat("hh:mm aa").format(date)
            }
        }
    }

}