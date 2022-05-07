package com.eundmswlji.tacoling.ui.shop

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eundmswlji.tacoling.Util.wonFormat
import com.eundmswlji.tacoling.data.model.Menu
import com.eundmswlji.tacoling.databinding.ItemMenuBinding

class ShopAdapter(private val menuList: List<Menu>) : RecyclerView.Adapter<ShopAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemMenuBinding.inflate(inflater, parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind()
    }

    override fun getItemCount(): Int {
        return menuList.size
    }

    inner class MyViewHolder(private val binding: ItemMenuBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            val item = menuList[absoluteAdapterPosition]
            binding.tvMenu.text = item.name
            binding.tvPrice.text = wonFormat.format(item.price)
        }
    }
}