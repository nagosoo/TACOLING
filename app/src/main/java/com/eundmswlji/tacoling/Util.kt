package com.eundmswlji.tacoling

import android.content.Context
import android.widget.Toast
import androidx.fragment.app.Fragment
import dagger.hilt.android.qualifiers.ApplicationContext

object Util {

    fun Fragment.toast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

}