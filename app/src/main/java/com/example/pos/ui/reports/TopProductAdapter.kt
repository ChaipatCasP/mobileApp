package com.example.pos.ui.reports

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pos.R

class TopProductAdapter : ListAdapter<TopProduct, TopProductAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_top_product, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvRank: TextView = itemView.findViewById(R.id.tvRank)
        private val ivImage: ImageView = itemView.findViewById(R.id.ivTopProductImage)
        private val tvName: TextView = itemView.findViewById(R.id.tvTopProductName)
        private val tvCategory: TextView = itemView.findViewById(R.id.tvTopProductCategory)
        private val tvPrice: TextView = itemView.findViewById(R.id.tvTopProductPrice)

        fun bind(item: TopProduct) {
            tvRank.text = item.rankLabel
            tvName.text = item.name
            tvCategory.text = item.category
            tvPrice.text = item.priceFormatted

            Glide.with(itemView.context)
                .load(item.imageUrl)
                .placeholder(R.drawable.bg_product_placeholder)
                .centerCrop()
                .into(ivImage)
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<TopProduct>() {
        override fun areItemsTheSame(oldItem: TopProduct, newItem: TopProduct) = oldItem.rank == newItem.rank
        override fun areContentsTheSame(oldItem: TopProduct, newItem: TopProduct) = oldItem == newItem
    }
}
