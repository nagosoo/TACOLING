package com.eundmswlji.tacoling.adapter

import android.widget.TextView
import androidx.databinding.BindingAdapter

object ShopLocationBindingAdapter {
    @BindingAdapter("location")
    @JvmStatic
    fun setLocation(textview: TextView, location: String) {
        textview.text = location
    }
}