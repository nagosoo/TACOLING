package com.eundmswlji.tacoling.ui.setting.my_liked

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.eundmswlji.tacoling.data.model.Shop

object MyLikedBindingAdapter {
    @BindingAdapter("myLikedList")
    @JvmStatic
    fun setMyLikedList(rv: RecyclerView, list: List<Shop>) {
        (rv.adapter as? ListAdapter<Shop, MyLikedAdapter.MyViewHolder>)?.submitList(list)
    }
}