package com.eundmswlji.tacoling.ui.map

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.eundmswlji.tacoling.data.model.Document
import com.eundmswlji.tacoling.databinding.ItemAddressBinding

class MapAdapter(private val clickListener: (x: Double, y: Double, address: String) -> (Unit)) :
    PagingDataAdapter<Document, MapAdapter.MyViewHolder>(DIFF_CALLBACK) {
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind()
        holder.itemView.setOnClickListener {
            val item = getItem(position)!!
            clickListener(item.x.toDouble(), item.y.toDouble(), item.addressName)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemAddressBinding.inflate(inflater, parent, false)
        return MyViewHolder(binding)
    }

    inner class MyViewHolder(private val binding: ItemAddressBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            val item = getItem(absoluteAdapterPosition)
            binding.tv.isVisible = item?.addressName.isNullOrEmpty().not()
            binding.tv.text = item?.addressName
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Document>() {
            override fun areItemsTheSame(oldItem: Document, newItem: Document): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Document, newItem: Document): Boolean {
                return oldItem.address == newItem.address &&
                        oldItem.addressName == newItem.addressName &&
                        oldItem.addressType == newItem.addressType &&
                        oldItem.roadAddress == newItem.roadAddress &&
                        oldItem.x == newItem.x &&
                        oldItem.y == newItem.y
            }
        }
    }
}