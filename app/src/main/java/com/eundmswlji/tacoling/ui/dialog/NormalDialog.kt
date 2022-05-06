package com.eundmswlji.tacoling.ui.dialog

import android.os.Bundle
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.eundmswlji.tacoling.R
import com.eundmswlji.tacoling.databinding.DialogNormalBinding

class NormalDialog(
    private val title: String,
    private val message: String?=null,
    private val spannedMessage: Spanned?=null,
    private val positiveMessage: String,
    private val negativeMessage: String,
    private val positiveButtonListener: () -> (Unit),
    private val negativeButtonListener: (() -> (Unit))? = null
) :
    DialogFragment() {
    private lateinit var binding: DialogNormalBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.DialogStyle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DialogNormalBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvTitle.text = title
        message?.let { binding.tvMessage.text = it }
        spannedMessage?.let { binding.tvMessage.text = it }
        binding.buttonNegative.text = negativeMessage
        binding.buttonPositive.text = positiveMessage
        binding.buttonPositive.setOnClickListener {
            positiveButtonListener()
            dismiss()
        }
        binding.buttonNegative.setOnClickListener {
            negativeButtonListener?.invoke()
            dismiss()
        }
    }
}
