package com.eundmswlji.tacoling.ui.setting.my_zzim

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.eundmswlji.tacoling.data.model.Shop

object MyZzimBindingAdapter {
    @BindingAdapter("myZzimList")
    @JvmStatic
    fun setMyZzimList(rv: RecyclerView, list: List<Shop>) {
        (rv.adapter as? ListAdapter<Shop, MyZzimAdapter.MyViewHolder>)?.submitList(list)
    }
}