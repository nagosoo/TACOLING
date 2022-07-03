package com.eundmswlji.tacoling.ui

import android.content.Context
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.eundmswlji.tacoling.ui.map.MapFragment
import com.eundmswlji.tacoling.ui.setting.SettingFragment

open class BaseFragment : Fragment() {
    private lateinit var callback: OnBackPressedCallback

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                when (this@BaseFragment) {
                    is MapFragment, is SettingFragment -> {
                        activity?.finishAffinity()
                    }
                    else -> {
                        findNavController().popBackStack()
                    }
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onDetach() {
        super.onDetach()
        callback.remove()
    }

}