package com.example.pos.ui.order

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

class CartAdapter(
    private val onIncrease: (CartItem) -> Unit,
    private val onDecrease: (CartItem) -> Unit
) : ListAdapter<CartItem, CartAdapter.CartViewHolder>(CartDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivCartItemImage: ImageView = itemView.findViewById(R.id.ivCartItemImage)
        private val tvCartItemName: TextView = itemView.findViewById(R.id.tvCartItemName)
        private val tvCartItemPrice: TextView = itemView.findViewById(R.id.tvCartItemPrice)
        private val tvQty: TextView = itemView.findViewById(R.id.tvQty)
        private val btnIncrease: View = itemView.findViewById(R.id.btnIncrease)
        private val btnDecrease: View = itemView.findViewById(R.id.btnDecrease)

        fun bind(item: CartItem) {
            tvCartItemName.text = item.product.name
            tvCartItemPrice.text = item.subtotalFormatted
            tvQty.text = item.quantity.toString()

            Glide.with(itemView.context)
                .load(item.product.imageUrl)
                .placeholder(R.drawable.bg_product_placeholder)
                .centerCrop()
                .into(ivCartItemImage)

            btnIncrease.setOnClickListener { onIncrease(item) }
            btnDecrease.setOnClickListener { onDecrease(item) }
        }
    }

    class CartDiffCallback : DiffUtil.ItemCallback<CartItem>() {
        override fun areItemsTheSame(oldItem: CartItem, newItem: CartItem) =
            oldItem.product.id == newItem.product.id
        override fun areContentsTheSame(oldItem: CartItem, newItem: CartItem) = oldItem == newItem
    }
}
