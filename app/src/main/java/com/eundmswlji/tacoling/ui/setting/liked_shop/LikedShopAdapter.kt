package com.eundmswlji.tacoling.ui.setting.liked_shop

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eundmswlji.tacoling.data.model.LikedShopX
import com.eundmswlji.tacoling.databinding.ItemShopBinding

class LikedShopAdapter(
    private val onItemClickListener: (Int) -> (Unit),
) : RecyclerView.Adapter<LikedShopAdapter.LikedShopViewHolder>() {

    private val likedShopList = mutableListOf<LikedShopX>()

    fun updateList(list: List<LikedShopX>) {
        likedShopList.clear()
        likedShopList.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LikedShopViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemShopBinding.inflate(inflater, parent, false)
        return LikedShopViewHolder(binding)
    }

    override fun getItemCount(): Int = likedShopList.size

    override fun onBindViewHolder(holder: LikedShopViewHolder, position: Int) {
        holder.bind()
    }

    inner class LikedShopViewHolder(private val binding: ItemShopBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            val item = likedShopList[absoluteAdapterPosition]
            binding.tvName.text = item.name

            itemView.setOnClickListener {
                onItemClickListener(item.id)
            }
        }
    }
}