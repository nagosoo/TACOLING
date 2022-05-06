package com.eundmswlji.tacoling

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Html
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.internal.format
import java.text.DecimalFormat


object Util {

    fun Fragment.toast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    fun Activity.toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    fun <T> debounce(
        waitMs: Long = 300L,
        coroutineScope: CoroutineScope,
        destinationFunction: (T) -> Unit
    ): (T) -> Unit {
        var debounceJob: Job? = null
        return { param: T ->
            debounceJob?.cancel()
            debounceJob = coroutineScope.launch {
                delay(waitMs)
                destinationFunction(param)
            }
        }
    }

    fun Fragment.hideKeyboard(view: View) {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    class ImageGetter(private val context: Context, private val width: Int, private val height: Int) : Html.ImageGetter {
        override fun getDrawable(source: String?): Drawable {
            val resID = context.resources.getIdentifier(
                source,
                "drawable",
                context.packageName
            )
            val d = context.resources.getDrawable(resID, null)
            d.setBounds(0, 0, width, height)
            return d
        }
    }

    val wonFormat = DecimalFormat("#,###원")

}

