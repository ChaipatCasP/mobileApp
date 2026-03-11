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
            binding.tvSeats.text     = "${table.totalSit} ที่นั่ง  ·  ${table.nameEn}"

            when (table.status) {
                TableStatus.AVAILABLE -> bindStyle(table.status)
                TableStatus.OCCUPIED  -> bindStyle(table.status)
                TableStatus.RESERVED  -> bindStyle(table.status)
                TableStatus.DIRTY     -> bindStyle(table.status)
            }

            binding.root.setOnClickListener { onTableClick(table) }
        }

        private fun bindStyle(status: TableStatus) {
            val ctx = binding.root.context
            when (status) {
                TableStatus.AVAILABLE -> {
                    binding.root.background = ContextCompat.getDrawable(ctx, R.drawable.bg_table_card_available)
                    binding.ivStatusIcon.setImageResource(R.drawable.ic_check_circle)
                    binding.ivStatusIcon.imageTintList = ContextCompat.getColorStateList(ctx, R.color.table_available_text)
                    binding.tvStatusLabel.text  = "ว่าง"
                    binding.tvStatusLabel.setTextColor(ContextCompat.getColor(ctx, R.color.table_available_text))
                    binding.tvStatusValue.text  = "พร้อมรับลูกค้า"
                }
                TableStatus.OCCUPIED -> {
                    binding.root.background = ContextCompat.getDrawable(ctx, R.drawable.bg_table_card_occupied)
                    binding.ivStatusIcon.setImageResource(R.drawable.ic_timer)
                    binding.ivStatusIcon.imageTintList = ContextCompat.getColorStateList(ctx, R.color.table_occupied_text)
                    binding.tvStatusLabel.text  = "มีลูกค้า"
                    binding.tvStatusLabel.setTextColor(ContextCompat.getColor(ctx, R.color.table_occupied_text))
                    binding.tvStatusValue.text  = "กำลังใช้งาน"
                }
                TableStatus.RESERVED -> {
                    binding.root.background = ContextCompat.getDrawable(ctx, R.drawable.bg_table_card_reserved)
                    binding.ivStatusIcon.setImageResource(R.drawable.ic_event)
                    binding.ivStatusIcon.imageTintList = ContextCompat.getColorStateList(ctx, R.color.table_reserved_text)
                    binding.tvStatusLabel.text  = "จอง"
                    binding.tvStatusLabel.setTextColor(ContextCompat.getColor(ctx, R.color.table_reserved_text))
                    binding.tvStatusValue.text  = "จองแล้ว"
                }
                TableStatus.DIRTY -> {
                    binding.root.background = ContextCompat.getDrawable(ctx, R.drawable.bg_table_card_dirty)
                    binding.ivStatusIcon.setImageResource(R.drawable.ic_close)
                    binding.ivStatusIcon.imageTintList = ContextCompat.getColorStateList(ctx, R.color.table_dirty_text)
                    binding.tvStatusLabel.text  = "รอทำความสะอาด"
                    binding.tvStatusLabel.setTextColor(ContextCompat.getColor(ctx, R.color.table_dirty_text))
                    binding.tvStatusValue.text  = "รอทำความสะอาด"
                }
            }
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
