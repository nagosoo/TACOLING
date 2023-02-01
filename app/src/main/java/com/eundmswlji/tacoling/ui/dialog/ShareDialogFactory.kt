package com.eundmswlji.tacoling.ui.dialog

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory

class ShareDialogFactory(private val shopId : Int) : FragmentFactory() {
    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return when(className){
            ShareDialog::class.java.name -> ShareDialog(shopId)
            else -> super.instantiate(classLoader, className)
        }
    }
}