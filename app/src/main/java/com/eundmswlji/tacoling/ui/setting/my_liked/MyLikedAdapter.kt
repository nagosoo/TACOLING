package com.eundmswlji.tacoling.ui.setting.my_liked

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.eundmswlji.tacoling.data.model.Shop
import com.eundmswlji.tacoling.databinding.ItemShopBinding

class MyLikedAdapter(
    private val onClickListener: (Shop) -> (Unit),
    private val onHeartClickListener: (Shop) -> (Unit)
) : ListAdapter<Shop, MyLikedAdapter.MyViewHolder>(DIFF_UTIL) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemShopBinding.inflate(inflater, parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind()
        val item = getItem(position)
        holder.itemView.setOnClickListener {
            onClickListener(item)
        }
    }

    inner class MyViewHolder(private val binding: ItemShopBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            val item = getItem(absoluteAdapterPosition)
            binding.tvName.text = item.name
            binding.tvHeart.setOnClickListener {
                onHeartClickListener(item)
            }
        }
    }

    companion object {
        val DIFF_UTIL = object : DiffUtil.ItemCallback<Shop>() {
            override fun areItemsTheSame(oldItem: Shop, newItem: Shop): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Shop, newItem: Shop): Boolean =
                oldItem.id == newItem.id &&
                        oldItem.name == newItem.name &&
                        oldItem.menu == newItem.menu
        }
    }
}