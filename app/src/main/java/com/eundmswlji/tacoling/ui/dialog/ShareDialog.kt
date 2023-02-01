package com.eundmswlji.tacoling.ui.dialog

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.eundmswlji.tacoling.R
import com.eundmswlji.tacoling.databinding.DialogShareBinding
import com.eundmswlji.tacoling.util.LinkUtil
import com.eundmswlji.tacoling.util.Util.toast

class ShareDialog(private val shopId: Int) : DialogFragment() {
    private lateinit var binding: DialogShareBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.DialogStyleHeight180)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogShareBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListener()
    }

    private fun setOnClickListener() {
        binding.buttonKakao.setOnClickListener {


        }
        binding.buttonLink.setOnClickListener {
            LinkUtil.setDynamicLinks(shopId, "타코왕")?.let { shortLink ->
                val clip: ClipData = ClipData.newPlainText("appUrl", "$shortLink")
                val clipboard =
                    requireContext().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                clipboard.setPrimaryClip(clip)
                Toast.makeText(context, "링크가 복사되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}