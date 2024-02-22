package com.eundmswlji.tacoling.presentation.ui.setting.favorite_shop

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.eundmswlji.tacoling.data.model.FavoriteShop
import com.eundmswlji.tacoling.databinding.ItemShopBinding
import com.eundmswlji.tacoling.domain.model.FavoriteShopModel

class FavoriteShopAdapter(
    private val onItemClickListener: (Int) -> (Unit)
) : RecyclerView.Adapter<FavoriteShopAdapter.FavoriteShopViewHolder>() {

    private val favoriteShopList = mutableListOf<FavoriteShopModel>()

    fun updateList(list: List<FavoriteShopModel>) {
        favoriteShopList.clear()
        favoriteShopList.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteShopViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemShopBinding.inflate(inflater, parent, false)
        return FavoriteShopViewHolder(binding)
    }

    override fun getItemCount(): Int = favoriteShopList.size

    override fun onBindViewHolder(holder: FavoriteShopViewHolder, position: Int) {
        holder.bind()
    }

    inner class FavoriteShopViewHolder(private val binding: ItemShopBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            val item = favoriteShopList[absoluteAdapterPosition]
            binding.tvName.text = item.shopName
            itemView.setOnClickListener {
                onItemClickListener(item.shopId)
            }
        }
    }
}