package com.eundmswlji.tacoling

import android.widget.EditText
import androidx.databinding.BindingAdapter


object BindingAdapter {
    @BindingAdapter("currentAddress")
    @JvmStatic
    fun setCurrentAddress(editText: EditText, address: String?) {
        address?.let { editText.setText(address) }
    }
}