package com.example.pos.ui.inventory

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pos.R
import com.example.pos.databinding.FragmentInventoryBinding
import com.example.pos.ui.order.CartManager

class InventoryFragment : Fragment() {

    private var _binding: FragmentInventoryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: InventoryViewModel by viewModels()
    private lateinit var adapter: InventoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInventoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSearch()
        observeViewModel()
        setupBottomTabs()
        observeCartBadge()
    }

    private fun setupRecyclerView() {
        adapter = InventoryAdapter()
        binding.rvInventory.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@InventoryFragment.adapter
        }
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.search(s?.toString() ?: "")
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun observeViewModel() {
        viewModel.filteredItems.observe(viewLifecycleOwner) { items ->
            adapter.submitList(items)
            binding.tvProductCount.text = "${items.size} products"
        }
    }

    private fun setupBottomTabs() {
        // Sales tab → navigate to Order screen
        binding.tabSales.setOnClickListener {
            findNavController().navigate(
                R.id.nav_order,
                null,
                androidx.navigation.NavOptions.Builder()
                    .setPopUpTo(R.id.nav_order, true)
                    .setLaunchSingleTop(true)
                    .build()
            )
        }
        // Inventory tab → already here, no-op
        binding.tabInventory.setOnClickListener { }
        // Reports tab → placeholder
        binding.tabReports.setOnClickListener { }
    }

    private fun observeCartBadge() {
        CartManager.items.observe(viewLifecycleOwner) { items ->
            val count = CartManager.totalCount
            val badge = binding.root.findViewById<TextView>(R.id.badgeSales)
            if (count > 0) {
                badge?.visibility = View.VISIBLE
                badge?.text = if (count > 99) "99+" else count.toString()
            } else {
                badge?.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
