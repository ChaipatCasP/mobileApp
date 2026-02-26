package com.example.pos.ui.inventory

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pos.R

class InventoryAdapter : ListAdapter<InventoryItem, InventoryAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_inventory, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivImage: ImageView = itemView.findViewById(R.id.ivInventoryImage)
        private val tvName: TextView = itemView.findViewById(R.id.tvInventoryName)
        private val tvCategory: TextView = itemView.findViewById(R.id.tvInventoryCategory)
        private val tvQty: TextView = itemView.findViewById(R.id.tvInventoryQty)
        private val tvStatus: TextView = itemView.findViewById(R.id.tvStockStatus)

        fun bind(item: InventoryItem) {
            tvName.text = item.name
            tvCategory.text = item.category
            tvQty.text = item.quantity.toString()
            tvStatus.text = item.stockLabel

            val ctx = itemView.context
            when (item.stockStatus) {
                InventoryItem.StockStatus.IN_STOCK -> {
                    tvStatus.background = ContextCompat.getDrawable(ctx, R.drawable.bg_badge_in_stock)
                    tvStatus.setTextColor(ContextCompat.getColor(ctx, R.color.green_primary))
                    tvQty.setTextColor(ContextCompat.getColor(ctx, R.color.text_title))
                }
                InventoryItem.StockStatus.LIMITED -> {
                    tvStatus.background = ContextCompat.getDrawable(ctx, R.drawable.bg_badge_limited)
                    tvStatus.setTextColor(ContextCompat.getColor(ctx, android.R.color.holo_orange_dark))
                    tvQty.setTextColor(ContextCompat.getColor(ctx, android.R.color.holo_orange_dark))
                }
                InventoryItem.StockStatus.LOW_STOCK -> {
                    tvStatus.background = ContextCompat.getDrawable(ctx, R.drawable.bg_badge_low_stock)
                    tvStatus.setTextColor(ContextCompat.getColor(ctx, android.R.color.holo_red_dark))
                    tvQty.setTextColor(ContextCompat.getColor(ctx, android.R.color.holo_red_dark))
                }
            }

            Glide.with(ctx)
                .load(item.imageUrl)
                .placeholder(R.drawable.bg_product_placeholder)
                .centerCrop()
                .into(ivImage)
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<InventoryItem>() {
        override fun areItemsTheSame(oldItem: InventoryItem, newItem: InventoryItem) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: InventoryItem, newItem: InventoryItem) = oldItem == newItem
    }
}
