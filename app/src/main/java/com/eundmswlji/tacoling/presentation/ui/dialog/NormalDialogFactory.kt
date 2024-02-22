package com.eundmswlji.tacoling.presentation.ui.dialog

import android.text.Spanned
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory

class NormalDialogFactory(
    private val title: String,
    private val message: String?=null,
    private val spannedMessage: Spanned?=null,
    private val positiveMessage: String,
    private val negativeMessage: String,
    private val positiveButtonListener: () -> (Unit),
    private val negativeButtonListener: (() -> (Unit))? = null
) : FragmentFactory() {
    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return when (className) {
            NormalDialog::class.java.name -> NormalDialog(
                title,
                message,
                spannedMessage,
                positiveMessage,
                negativeMessage,
                positiveButtonListener,
                negativeButtonListener
            )
            else -> super.instantiate(classLoader, className)
        }
    }
}