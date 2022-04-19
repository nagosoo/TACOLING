package com.eundmswlji.tacoling

import android.widget.Toast
import androidx.fragment.app.Fragment

object Util {

    fun Fragment.toast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}