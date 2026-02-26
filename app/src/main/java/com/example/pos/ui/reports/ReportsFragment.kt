package com.example.pos.ui.reports

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pos.R
import com.example.pos.databinding.FragmentReportsBinding
import com.example.pos.ui.order.CartManager
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

class ReportsFragment : Fragment() {

    private var _binding: FragmentReportsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ReportsViewModel by viewModels()
    private lateinit var adapter: TopProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupStats()
        setupChart()
        setupTopProducts()
        setupBottomTabs()
        observeCartBadge()
    }

    private fun setupStats() {
        val s = viewModel.summary
        binding.tvRevenue.text = s.revenue
        binding.tvOrders.text = s.orders
        binding.tvAvgOrder.text = s.avgOrder
    }

    private fun setupChart() {
        val entries = viewModel.dailyRevenue.mapIndexed { index, value ->
            Entry(index.toFloat(), value)
        }

        val greenColor = ContextCompat.getColor(requireContext(), R.color.green_primary)

        val dataSet = LineDataSet(entries, "Revenue").apply {
            color = greenColor
            setCircleColor(greenColor)
            circleRadius = 5f
            lineWidth = 2.5f
            setDrawValues(false)
            setDrawFilled(true)
            fillAlpha = 30
            fillColor = greenColor
            mode = LineDataSet.Mode.CUBIC_BEZIER
            setDrawCircleHole(true)
            circleHoleColor = Color.WHITE
            circleHoleRadius = 3f
            highLightColor = greenColor
        }

        val chart = binding.lineChart
        chart.apply {
            data = LineData(dataSet)
            description.isEnabled = false
            legend.isEnabled = false
            setTouchEnabled(false)
            setDrawGridBackground(false)
            setExtraOffsets(8f, 16f, 8f, 8f)

            xAxis.apply {
                valueFormatter = IndexAxisValueFormatter(viewModel.dayLabels)
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                setDrawGridLines(true)
                gridColor = Color.parseColor("#F0F0F0")
                axisLineColor = Color.parseColor("#F0F0F0")
                textColor = Color.parseColor("#9B9B9B")
                textSize = 11f
                setDrawAxisLine(false)
            }

            axisLeft.apply {
                setDrawGridLines(true)
                gridColor = Color.parseColor("#F0F0F0")
                axisLineColor = Color.parseColor("#F0F0F0")
                textColor = Color.parseColor("#9B9B9B")
                textSize = 10f
                setDrawAxisLine(false)
                setValueFormatter(object : com.github.mikephil.charting.formatter.ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return "\$${value.toInt()}"
                    }
                })
            }

            axisRight.isEnabled = false
            animateX(800)
        }
    }

    private fun setupTopProducts() {
        adapter = TopProductAdapter()
        binding.rvTopProducts.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@ReportsFragment.adapter
            isNestedScrollingEnabled = false
        }
        adapter.submitList(viewModel.topProducts)
    }

    private fun setupBottomTabs() {
        binding.tabSales.setOnClickListener {
            findNavController().navigate(
                R.id.nav_order,
                null,
                androidx.navigation.NavOptions.Builder()
                    .setLaunchSingleTop(true)
                    .build()
            )
        }
        binding.tabInventory.setOnClickListener {
            findNavController().navigate(
                R.id.nav_inventory,
                null,
                androidx.navigation.NavOptions.Builder()
                    .setLaunchSingleTop(true)
                    .build()
            )
        }
        binding.tabReports.setOnClickListener { /* already here */ }
    }

    private fun observeCartBadge() {
        CartManager.items.observe(viewLifecycleOwner) {
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
