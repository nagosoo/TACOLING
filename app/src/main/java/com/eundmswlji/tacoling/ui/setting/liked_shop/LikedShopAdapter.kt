package com.eundmswlji.tacoling.ui.setting.liked_shop

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.eundmswlji.tacoling.data.model.LikedShopX
import com.eundmswlji.tacoling.databinding.ItemShopBinding

class LikedShopAdapter(
    private val onItemClickListener: (Int) -> (Unit),
    //private val onHeartClickListener: (Int) -> (Unit)
) : ListAdapter<LikedShopX, LikedShopAdapter.LikedShopViewHolder>(DIFF_UTIL) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LikedShopViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemShopBinding.inflate(inflater, parent, false)
        return LikedShopViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LikedShopViewHolder, position: Int) {
        holder.bind()
    }

    inner class LikedShopViewHolder(private val binding: ItemShopBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            val item = getItem(absoluteAdapterPosition)
            binding.tvName.text = item.name
//            binding.tvHeart.setOnClickListener {
//                onHeartClickListener(item.id)
//            }
            //밀어서 삭제로 구현 ㄱ
            itemView.setOnClickListener {
                onItemClickListener(item.id!!)
            }
        }
    }

    companion object {
        val DIFF_UTIL = object : DiffUtil.ItemCallback<LikedShopX>() {
            override fun areItemsTheSame(oldItem: LikedShopX, newItem: LikedShopX): Boolean =
                oldItem === newItem

            override fun areContentsTheSame(oldItem: LikedShopX, newItem: LikedShopX): Boolean =
                oldItem.id == newItem.id
        }
    }
}