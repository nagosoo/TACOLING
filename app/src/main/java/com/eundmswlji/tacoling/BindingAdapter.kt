package com.eundmswlji.tacoling

import android.widget.EditText
import androidx.appcompat.widget.SwitchCompat
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.eundmswlji.tacoling.data.model.Shop
import com.eundmswlji.tacoling.ui.setting.my_zzim.MyZzimAdapter


object BindingAdapter {
    @BindingAdapter("currentAddress")
    @JvmStatic
    fun setCurrentAddress(editText: EditText, address: String?) {
        address?.let { editText.setText(address) }
    }

    @BindingAdapter("alarmOnOff")
    @JvmStatic
    fun setSwitch(switchCompat: SwitchCompat, isAlarmOn: Boolean) {
        switchCompat.isChecked = isAlarmOn
    }

    @BindingAdapter("myZzimList")
    @JvmStatic
    fun setMyZzimList(rv: RecyclerView, list:List<Shop>){
        (rv.adapter as? ListAdapter<Shop, MyZzimAdapter.MyViewHolder>)?.submitList(list)
    }
}