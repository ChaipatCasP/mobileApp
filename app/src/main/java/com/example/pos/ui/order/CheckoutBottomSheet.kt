package com.example.pos.ui.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pos.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton

class CheckoutBottomSheet : BottomSheetDialogFragment() {

    private enum class PaymentMethod { CASH, CARD, PROMPTPAY }
    private var selectedPayment = PaymentMethod.CASH

    private lateinit var cartAdapter: CartAdapter
    private lateinit var btnPayCash: LinearLayout
    private lateinit var btnPayCard: LinearLayout
    private lateinit var btnPayPromptPay: LinearLayout
    private lateinit var tvCheckoutTotal: TextView
    private lateinit var btnCharge: MaterialButton
    private lateinit var rvCartItems: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.bottom_sheet_checkout, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnPayCash = view.findViewById(R.id.btnPayCash)
        btnPayCard = view.findViewById(R.id.btnPayCard)
        btnPayPromptPay = view.findViewById(R.id.btnPayPromptPay)
        tvCheckoutTotal = view.findViewById(R.id.tvCheckoutTotal)
        btnCharge = view.findViewById(R.id.btnCharge)
        rvCartItems = view.findViewById(R.id.rvCartItems)

        setupRecyclerView()
        setupPaymentButtons()
        observeCart()
        updatePaymentUI()

        view.findViewById<View>(R.id.btnCloseCheckout).setOnClickListener { dismiss() }

        btnCharge.setOnClickListener {
            CartManager.clearCart()
            dismiss()
        }
    }

    private fun setupRecyclerView() {
        cartAdapter = CartAdapter(
            onIncrease = { item -> CartManager.increaseQuantity(item.product) },
            onDecrease = { item -> CartManager.decreaseQuantity(item.product) }
        )
        rvCartItems.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = cartAdapter
            isNestedScrollingEnabled = false
        }
    }

    private fun setupPaymentButtons() {
        btnPayCash.setOnClickListener {
            selectedPayment = PaymentMethod.CASH
            updatePaymentUI()
        }
        btnPayCard.setOnClickListener {
            selectedPayment = PaymentMethod.CARD
            updatePaymentUI()
        }
        btnPayPromptPay.setOnClickListener {
            selectedPayment = PaymentMethod.PROMPTPAY
            updatePaymentUI()
        }
    }

    private fun updatePaymentUI() {
        btnPayCash.setBackgroundResource(
            if (selectedPayment == PaymentMethod.CASH) R.drawable.bg_payment_selected
            else R.drawable.bg_payment_unselected
        )
        btnPayCard.setBackgroundResource(
            if (selectedPayment == PaymentMethod.CARD) R.drawable.bg_payment_selected
            else R.drawable.bg_payment_unselected
        )
        btnPayPromptPay.setBackgroundResource(
            if (selectedPayment == PaymentMethod.PROMPTPAY) R.drawable.bg_payment_selected
            else R.drawable.bg_payment_unselected
        )

        val cashIcon = btnPayCash.getChildAt(0)
        val cardIcon = btnPayCard.getChildAt(0)
        val qrIcon = btnPayPromptPay.getChildAt(0)
        val cashLabel = btnPayCash.getChildAt(1) as? TextView
        val cardLabel = btnPayCard.getChildAt(1) as? TextView
        val qrLabel = btnPayPromptPay.getChildAt(1) as? TextView

        val activeColor = resources.getColor(android.R.color.white, null)
        val inactiveColor = resources.getColor(R.color.text_gray, null)

        val cashSelected = selectedPayment == PaymentMethod.CASH
        val cardSelected = selectedPayment == PaymentMethod.CARD
        val qrSelected = selectedPayment == PaymentMethod.PROMPTPAY

        cashLabel?.setTextColor(if (cashSelected) activeColor else inactiveColor)
        cardLabel?.setTextColor(if (cardSelected) activeColor else inactiveColor)
        qrLabel?.setTextColor(if (qrSelected) activeColor else inactiveColor)
    }

    private fun observeCart() {
        CartManager.items.observe(viewLifecycleOwner) { items ->
            cartAdapter.submitList(items.toList())
            tvCheckoutTotal.text = CartManager.totalAmountFormatted
            btnCharge.text = "Charge ${CartManager.totalAmountFormatted}"
        }
    }
}
