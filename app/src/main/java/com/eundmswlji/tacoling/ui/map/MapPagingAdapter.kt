package com.eundmswlji.tacoling.ui.map

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.eundmswlji.tacoling.data.model.Document
import com.eundmswlji.tacoling.databinding.ItemAddressBinding

class MapPagingAdapter(private val clickListener: (x: Double, y: Double, address: String) -> (Unit)) :
    PagingDataAdapter<Document, MapPagingAdapter.PagingViewHolder>(DIFF_CALLBACK) {

    override fun onBindViewHolder(holder: PagingViewHolder, position: Int) {
        holder.bind()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagingViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemAddressBinding.inflate(inflater, parent, false)
        return PagingViewHolder(binding)
    }

    inner class PagingViewHolder(private val binding: ItemAddressBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind() {
            val item = getItem(absoluteAdapterPosition)

            item?.let {
                itemView.setOnClickListener {
                    clickListener(item.x.toDouble(), item.y.toDouble(), item.addressName)
                }

                binding.textView.apply {
                    text = item.addressName
                }

            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Document>() {
            override fun areItemsTheSame(oldItem: Document, newItem: Document): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: Document, newItem: Document): Boolean {
                return oldItem == newItem
            }
        }
    }
}