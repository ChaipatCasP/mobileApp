package com.example.pos.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.pos.R
import com.example.pos.databinding.FragmentHomeBinding
import com.example.pos.service.TokenManager

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        // Observe greeting
        viewModel.greeting.observe(viewLifecycleOwner) { greeting ->
            binding.tvGreeting.text = greeting
        }

        // Observe date
        viewModel.todayDate.observe(viewLifecycleOwner) { date ->
            binding.tvTodayDate.text = date
            binding.tvDateValue.text = date
        }

        // Observe stats
        viewModel.todaySales.observe(viewLifecycleOwner) { binding.tvTodaySales.text = it }
        viewModel.todayOrders.observe(viewLifecycleOwner) { binding.tvTodayOrders.text = it.toString() }

        // Set welcome name from logged-in user
        val userName = TokenManager.getUserName()
        binding.tvWelcomeName.text = if (userName.isNotEmpty())
            "สวัสดี, $userName 👋" else "ยินดีต้อนรับ! 👋"

        // Quick action navigation
        binding.cardTables.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_tableList)
        }
        binding.cardInventory.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_inventory)
        }
        binding.cardReports.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_reports)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}