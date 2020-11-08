package com.gowtham.calllogapp.utils

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.gowtham.calllogapp.R
import com.gowtham.calllogapp.db.data.Call


object BindingAdapters {

    @BindingAdapter("imageUrl")
    @JvmStatic
    fun loadUserImage(view: ImageView, url: String) {
        if (url.isEmpty())
            view.setImageResource(R.drawable.ic_other_user)
        else {
            Glide.with(view.context).load(url)
                .apply(returnCache(R.drawable.ic_other_user))
                .into(view)
        }
    }

    private fun returnCache(pHolder: Int): RequestOptions {
        return RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)
            .circleCrop()
            .placeholder(pHolder).error(pHolder)
    }

    @BindingAdapter("showStar")
    @JvmStatic
    fun showStar(view: ImageView, isFav: Boolean) {
        val star = if (isFav) R.drawable.ic_fav_selected else R.drawable.ic_fav_unselected
        view.setImageResource(star)
    }

    @BindingAdapter("setDate")
    @JvmStatic
    fun setDate(txtView: TextView, call: Call) {
        txtView.text = Utils.getTime(call.timeInMillis)
    }

}