package com.example.pos.ui.order

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.pos.R
import com.example.pos.databinding.ItemTableCardBinding
import com.example.pos.service.table.TableModel
import com.example.pos.service.table.TableStatus

/**
 * Adapter สำหรับแสดงรายการโต๊ะในหน้า Table List
 */
class TableAdapter(
    private val onTableClick: (TableModel) -> Unit
) : ListAdapter<TableModel, TableAdapter.TableViewHolder>(DIFF_CALLBACK) {

    inner class TableViewHolder(
        private val binding: ItemTableCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(table: TableModel) {
            binding.tvTableName.text = table.code
            // แสดงชื่อโต๊ะ (name_en) แทน seats หรือแสดงทั้งคู่
            binding.tvSeats.text = "${table.totalSit} Seats  ·  ${table.nameEn}"

            when (table.status) {
                TableStatus.AVAILABLE -> bindAvailable(table)
                TableStatus.OCCUPIED  -> bindOccupied(table)
                TableStatus.RESERVED  -> bindReserved(table)
            }

            binding.root.setOnClickListener { onTableClick(table) }
        }

        private fun bindAvailable(table: TableModel) {
            val ctx = binding.root.context
            binding.root.background = ContextCompat.getDrawable(ctx, R.drawable.bg_table_card_available)
            binding.ivStatusIcon.setImageResource(R.drawable.ic_check_circle)
            binding.ivStatusIcon.imageTintList = ContextCompat.getColorStateList(ctx, R.color.table_available_text)
            binding.tvStatusLabel.text = "AVAILABLE"
            binding.tvStatusLabel.setTextColor(ContextCompat.getColor(ctx, R.color.table_available_text))
            binding.tvStatusValue.text = "Ready"
            binding.tvStatusValue.setTextColor(ContextCompat.getColor(ctx, R.color.table_title_dark))
        }

        private fun bindOccupied(table: TableModel) {
            val ctx = binding.root.context
            binding.root.background = ContextCompat.getDrawable(ctx, R.drawable.bg_table_card_occupied)
            binding.ivStatusIcon.setImageResource(R.drawable.ic_timer)
            binding.ivStatusIcon.imageTintList = ContextCompat.getColorStateList(ctx, R.color.table_occupied_text)
            binding.tvStatusLabel.text = "OCCUPIED"
            binding.tvStatusLabel.setTextColor(ContextCompat.getColor(ctx, R.color.table_occupied_text))
            binding.tvStatusValue.text = "In Use"
            binding.tvStatusValue.setTextColor(ContextCompat.getColor(ctx, R.color.table_title_dark))
        }

        private fun bindReserved(table: TableModel) {
            val ctx = binding.root.context
            binding.root.background = ContextCompat.getDrawable(ctx, R.drawable.bg_table_card_reserved)
            binding.ivStatusIcon.setImageResource(R.drawable.ic_event)
            binding.ivStatusIcon.imageTintList = ContextCompat.getColorStateList(ctx, R.color.table_reserved_text)
            binding.tvStatusLabel.text = "RESERVED"
            binding.tvStatusLabel.setTextColor(ContextCompat.getColor(ctx, R.color.table_reserved_text))
            binding.tvStatusValue.text = "Reserved"
            binding.tvStatusValue.setTextColor(ContextCompat.getColor(ctx, R.color.table_title_dark))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TableViewHolder {
        val binding = ItemTableCardBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return TableViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TableViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<TableModel>() {
            override fun areItemsTheSame(oldItem: TableModel, newItem: TableModel) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: TableModel, newItem: TableModel) =
                oldItem == newItem
        }
    }
}
