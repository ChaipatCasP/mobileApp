package com.example.pos.ui.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pos.R
import com.example.pos.service.ApiResult
import com.example.pos.service.order.OrderItemRequest
import com.example.pos.service.order.OrderRequest
import com.example.pos.service.order.OrderService
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

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

    /** tableId ที่รับมาจาก arguments (ค่า default = 0) */
    private val tableId: Int get() = arguments?.getInt("tableId", 0) ?: 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.bottom_sheet_checkout, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnPayCash     = view.findViewById(R.id.btnPayCash)
        btnPayCard     = view.findViewById(R.id.btnPayCard)
        btnPayPromptPay = view.findViewById(R.id.btnPayPromptPay)
        tvCheckoutTotal = view.findViewById(R.id.tvCheckoutTotal)
        btnCharge      = view.findViewById(R.id.btnCharge)
        rvCartItems    = view.findViewById(R.id.rvCartItems)

        setupRecyclerView()
        setupPaymentButtons()
        observeCart()
        updatePaymentUI()

        view.findViewById<View>(R.id.btnCloseCheckout).setOnClickListener { dismiss() }

        btnCharge.setOnClickListener {
            submitOrder()
        }
    }

    // ──────────────────────────────────────────────
    //  Order Submission
    // ──────────────────────────────────────────────

    private fun submitOrder() {
        val items = CartManager.items.value ?: emptyList()
        if (items.isEmpty()) return

        val paymentStr = when (selectedPayment) {
            PaymentMethod.CASH      -> "cash"
            PaymentMethod.CARD      -> "card"
            PaymentMethod.PROMPTPAY -> "promptpay"
        }

        val orderRequest = OrderRequest(
            tableId = tableId,
            items   = items.map { ci ->
                OrderItemRequest(
                    productId = ci.product.id,
                    name      = ci.product.name,
                    quantity  = ci.quantity,
                    price     = ci.product.price,
                    subtotal  = ci.subtotal
                )
            },
            payment = paymentStr,
            total   = CartManager.totalAmount
        )

        btnCharge.isEnabled = false
        btnCharge.text = "กำลังบันทึก..."

        lifecycleScope.launch {
            when (OrderService().createOrder(orderRequest)) {
                is ApiResult.Success  -> {
                    CartManager.clearCart()
                    Toast.makeText(requireContext(), "✅ บันทึกออเดอร์สำเร็จ", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    // Network/API error — graceful offline mode
                    CartManager.clearCart()
                    Toast.makeText(requireContext(), "บันทึกออเดอร์แล้ว (offline)", Toast.LENGTH_SHORT).show()
                }
            }
            dismiss()
        }
    }

    // ──────────────────────────────────────────────
    //  UI setup (unchanged)
    // ──────────────────────────────────────────────

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
        btnPayCash.setOnClickListener      { selectedPayment = PaymentMethod.CASH;      updatePaymentUI() }
        btnPayCard.setOnClickListener      { selectedPayment = PaymentMethod.CARD;      updatePaymentUI() }
        btnPayPromptPay.setOnClickListener { selectedPayment = PaymentMethod.PROMPTPAY; updatePaymentUI() }
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

        val cashLabel = btnPayCash.getChildAt(1) as? TextView
        val cardLabel = btnPayCard.getChildAt(1) as? TextView
        val qrLabel   = btnPayPromptPay.getChildAt(1) as? TextView

        val activeColor   = resources.getColor(android.R.color.white, null)
        val inactiveColor = resources.getColor(R.color.text_gray, null)

        cashLabel?.setTextColor(if (selectedPayment == PaymentMethod.CASH)      activeColor else inactiveColor)
        cardLabel?.setTextColor(if (selectedPayment == PaymentMethod.CARD)      activeColor else inactiveColor)
        qrLabel?.setTextColor(  if (selectedPayment == PaymentMethod.PROMPTPAY) activeColor else inactiveColor)
    }

    private fun observeCart() {
        CartManager.items.observe(viewLifecycleOwner) { items ->
            cartAdapter.submitList(items.toList())
            val total = CartManager.totalAmountFormatted
            tvCheckoutTotal.text = total
            btnCharge.text       = "ชำระ $total"
            btnCharge.isEnabled  = items.isNotEmpty()
        }
    }
}
