package com.eundmswlji.tacoling.ui.shop

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eundmswlji.tacoling.data.model.MenuX
import com.eundmswlji.tacoling.databinding.ItemMenuBinding
import com.eundmswlji.tacoling.util.Ext.wonFormat

class ShopAdapter : RecyclerView.Adapter<ShopAdapter.MyViewHolder>() {

    lateinit var menuList: MutableList<MenuX>

    fun updateList(list: List<MenuX>) {
        menuList.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemMenuBinding.inflate(inflater, parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind()
    }

    override fun getItemCount(): Int = menuList.size

    inner class MyViewHolder(private val binding: ItemMenuBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            val item = menuList[absoluteAdapterPosition]
            binding.tvMenu.text = item.name
            binding.tvPrice.text = item.price.wonFormat()
        }
    }
}