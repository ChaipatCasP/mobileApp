package com.example.pos.ui.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.pos.R
import com.example.pos.databinding.FragmentTableListBinding
import com.example.pos.service.table.TableStatus

/**
 * Fragment แสดงรายการโต๊ะ (Table View)
 *
 * - โหลดข้อมูลจาก GET /api/tables ผ่าน TableListViewModel / TableService
 * - แสดงโต๊ะเป็น Grid 2 คอลัมน์
 * - กรองตาม status (Available / Occupied / Reserved)
 * - เมื่อกดโต๊ะ → ไปหน้า Order (เพิ่มรายการ)
 */
class TableListFragment : Fragment() {

    private var _binding: FragmentTableListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TableListViewModel by viewModels()
    private lateinit var tableAdapter: TableAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTableListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupFilterChips()
        setupSwipeRefresh()
        setupBottomTabs()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        tableAdapter = TableAdapter { table ->
            // นำทางไปหน้า Order พร้อมส่ง tableId
            val args = Bundle().apply {
                putInt("tableId", table.id)
                putString("tableName", table.nameEn)
            }
            findNavController().navigate(R.id.nav_order, args)
        }

        binding.rvTables.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = tableAdapter
            setHasFixedSize(true)
        }
    }

    private fun setupFilterChips() {
        // กด chip Available
        binding.chipAvailable.setOnClickListener {
            val current = viewModel.activeFilter.value
            viewModel.setFilter(if (current == TableStatus.AVAILABLE) null else TableStatus.AVAILABLE)
        }
        // กด chip Occupied
        binding.chipOccupied.setOnClickListener {
            val current = viewModel.activeFilter.value
            viewModel.setFilter(if (current == TableStatus.OCCUPIED) null else TableStatus.OCCUPIED)
        }
        // กด chip Reserved
        binding.chipReserved.setOnClickListener {
            val current = viewModel.activeFilter.value
            viewModel.setFilter(if (current == TableStatus.RESERVED) null else TableStatus.RESERVED)
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setColorSchemeColors(
            ContextCompat.getColor(requireContext(), R.color.blue_primary)
        )
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.loadTables()
        }
    }

    private fun setupBottomTabs() {
        // TABLES tab — already here
        binding.tabTables.setOnClickListener { /* no-op */ }

        // ORDERS tab → navigate to order (product) screen without a specific table
        binding.tabOrders.setOnClickListener {
            findNavController().navigate(
                R.id.nav_order,
                null,
                androidx.navigation.NavOptions.Builder()
                    .setLaunchSingleTop(true)
                    .build()
            )
        }

        // MENU tab → navigate to Inventory (Menu)
        binding.tabMenu.setOnClickListener {
            findNavController().navigate(
                R.id.nav_inventory,
                null,
                androidx.navigation.NavOptions.Builder()
                    .setLaunchSingleTop(true)
                    .build()
            )
        }

        // MORE tab → navigate to Home (Settings/More)
        binding.tabMore.setOnClickListener {
            findNavController().navigate(
                R.id.nav_home,
                null,
                androidx.navigation.NavOptions.Builder()
                    .setLaunchSingleTop(true)
                    .build()
            )
        }
    }

    private fun observeViewModel() {
        viewModel.filteredTables.observe(viewLifecycleOwner) { tables ->
            tableAdapter.submitList(tables)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
            binding.swipeRefresh.isRefreshing = loading
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                binding.tvError.text = error
                binding.tvError.visibility = View.VISIBLE
            } else {
                binding.tvError.visibility = View.GONE
            }
        }

        viewModel.availableCount.observe(viewLifecycleOwner) { count ->
            binding.tvAvailableCount.text = "Available ($count)"
        }

        viewModel.occupiedCount.observe(viewLifecycleOwner) { count ->
            binding.tvOccupiedCount.text = "Occupied ($count)"
        }

        viewModel.reservedCount.observe(viewLifecycleOwner) { count ->
            binding.tvReservedCount.text = "Reserved ($count)"
        }

        viewModel.activeFilter.observe(viewLifecycleOwner) { filter ->
            updateChipHighlight(filter)
        }
    }

    private fun updateChipHighlight(active: TableStatus?) {
        val ctx = requireContext()

        // Reset all chips
        listOf(binding.chipAvailable, binding.chipOccupied, binding.chipReserved).forEach {
            it.background = ContextCompat.getDrawable(ctx, R.drawable.bg_filter_chip)
        }

        when (active) {
            TableStatus.AVAILABLE -> binding.chipAvailable.setBackgroundColor(
                ContextCompat.getColor(ctx, R.color.table_available_bg)
            )
            TableStatus.OCCUPIED -> binding.chipOccupied.setBackgroundColor(
                ContextCompat.getColor(ctx, R.color.table_occupied_bg)
            )
            TableStatus.RESERVED -> binding.chipReserved.setBackgroundColor(
                ContextCompat.getColor(ctx, R.color.table_reserved_bg)
            )
            null -> { /* all reset already */ }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
