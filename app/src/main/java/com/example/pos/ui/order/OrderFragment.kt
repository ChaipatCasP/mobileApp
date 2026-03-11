package com.example.pos.ui.order

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.pos.R
import com.example.pos.databinding.FragmentOrderBinding
import com.example.pos.service.TokenManager
import java.util.Calendar

class OrderFragment : Fragment() {

    private var _binding: FragmentOrderBinding? = null
    private val binding get() = _binding!!

    private val viewModel: OrderViewModel by viewModels()
    private lateinit var productAdapter: ProductAdapter

    /** tableId + tableName ที่รับมาจาก TableListFragment */
    private val tableId: Int get() = arguments?.getInt("tableId", 0) ?: 0
    private val tableName: String get() = arguments?.getString("tableName", "") ?: ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupGreeting()
        setupProductGrid()
        setupCategories()
        observeViewModel()
        setupCartBar()
        setupBottomTabs()
    }

    private fun setupGreeting() {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val greeting = when {
            hour < 12 -> "สวัสดีตอนเช้า ☕"
            hour < 17 -> "สวัสดีตอนบ่าย ☀️"
            else      -> "สวัสดีตอนเย็น 🌙"
        }
        val userName = TokenManager.getUserName()
        val tableInfo = if (tableName.isNotEmpty()) " · $tableName" else ""
        binding.tvGreeting.text = if (userName.isNotEmpty()) "$greeting, $userName$tableInfo" else "$greeting$tableInfo"
    }

    private fun setupProductGrid() {
        productAdapter = ProductAdapter { product ->
            CartManager.addItem(product)
        }
        binding.rvProducts.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = productAdapter
        }
    }

    private fun setupCategories() {
        OrderViewModel.CATEGORIES.forEach { category ->
            val pill = TextView(requireContext()).apply {
                text = category
                textSize = 13f
                gravity = Gravity.CENTER
                val density = resources.displayMetrics.density
                val hPad = (16 * density).toInt()
                val vPad = (8 * density).toInt()
                setPadding(hPad, vPad, hPad, vPad)
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { setMargins(0, 0, 8, 0) }
                layoutParams = params
                setOnClickListener { viewModel.selectCategory(category) }
            }
            binding.categoryContainer.addView(pill)
        }
        updateCategoryPills(viewModel.selectedCategory.value ?: "All")
    }

    private fun updateCategoryPills(selected: String) {
        val container = binding.categoryContainer
        for (i in 0 until container.childCount) {
            val pill = container.getChildAt(i) as? TextView ?: continue
            val isSelected = pill.text == selected
            pill.background = ContextCompat.getDrawable(
                requireContext(),
                if (isSelected) R.drawable.bg_category_selected else R.drawable.bg_category_unselected
            )
            pill.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (isSelected) android.R.color.white else R.color.text_gray
                )
            )
            if (isSelected) pill.setTypeface(null, android.graphics.Typeface.BOLD)
            else pill.setTypeface(null, android.graphics.Typeface.NORMAL)
        }
    }

    private fun observeViewModel() {
        viewModel.filteredProducts.observe(viewLifecycleOwner) { products ->
            productAdapter.submitList(products)
        }

        viewModel.selectedCategory.observe(viewLifecycleOwner) { category ->
            updateCategoryPills(category)
        }

        // Loading indicator
        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }

        CartManager.items.observe(viewLifecycleOwner) {
            val count = CartManager.totalCount
            if (count > 0) {
                binding.cartBar.visibility = View.VISIBLE
                val label = if (count > 1) "$count รายการ" else "1 รายการ"
                binding.tvCartCount.text = label
                binding.tvCartTotal.text = CartManager.totalAmountFormatted
            } else {
                binding.cartBar.visibility = View.GONE
            }
        }
    }

    private fun setupCartBar() {
        binding.cartBar.setOnClickListener {
            if (CartManager.totalCount > 0) {
                val sheet = CheckoutBottomSheet().apply {
                    arguments = Bundle().apply {
                        putInt("tableId", tableId)
                    }
                }
                sheet.show(childFragmentManager, "checkout")
            }
        }
    }

    private fun setupBottomTabs() {
        binding.btnHome.setOnClickListener {
            findNavController().navigate(
                R.id.nav_home, null,
                androidx.navigation.NavOptions.Builder().setLaunchSingleTop(true).build()
            )
        }
        binding.tabInventory.setOnClickListener {
            findNavController().navigate(
                R.id.nav_inventory, null,
                androidx.navigation.NavOptions.Builder().setLaunchSingleTop(true).build()
            )
        }
        binding.tabSales.setOnClickListener { }
        binding.tabReports.setOnClickListener {
            findNavController().navigate(
                R.id.nav_reports, null,
                androidx.navigation.NavOptions.Builder().setLaunchSingleTop(true).build()
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
