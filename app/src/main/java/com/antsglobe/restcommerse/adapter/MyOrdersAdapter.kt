package com.antsglobe.restcommerse.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.antsglobe.restcommerse.R
import com.antsglobe.restcommerse.Utils.PreferenceManager
import com.antsglobe.restcommerse.databinding.OrdersRecyclerViewBinding
import com.antsglobe.restcommerse.model.Response.OrderResponse

class MyOrdersAdapter(val orderlist: List<OrderResponse>, val context: Context) :
    RecyclerView.Adapter<MyOrdersAdapter.MainViewHolder>() {

    private lateinit var sharedPreferences: PreferenceManager
    private var orderClickListener: OnClickOrderListener? = null

    inner class MainViewHolder(val itemsbinding: OrdersRecyclerViewBinding) :
        RecyclerView.ViewHolder(itemsbinding.root) {
        fun binditem(order: OrderResponse) {

            sharedPreferences = PreferenceManager(context)
            if (sharedPreferences.getMode() == true) {
                itemsbinding.background.setBackgroundResource(R.drawable.profile_round_corner_bg_grey)
                itemsbinding.orderid.setTextColor(context.resources.getColor(R.color.whitefordark))
                itemsbinding.transactionid.setTextColor(context.resources.getColor(R.color.whitefordark))
                itemsbinding.quantity.setTextColor(context.resources.getColor(R.color.whitefordark))
                itemsbinding.totalprice.setTextColor(context.resources.getColor(R.color.whitefordark))
            }
            itemsbinding.date.text = order.orderdate
            itemsbinding.invoice.text = order.invoiceno
            itemsbinding.orderid.text = order.order_no
            itemsbinding.quantity.text = order.item_qty.toString()
            itemsbinding.status.text = order.delivery_status
            itemsbinding.totalprice.text = "₹ " + order.grandtotal.toString()
            itemsbinding.transactionid.text = order.transaction_id

            itemView.setOnClickListener {
                val pId = order.order_no
                val transactionId = order.transaction_id
                orderClickListener?.onOrderIdClick(
                    pId.toString(),transactionId!!
                )
            }

        }
    }

    interface OnClickOrderListener {
        fun onOrderIdClick(
            orderId: String,
            transactionId : String
        )
    }

    fun setOnClickOrderListener(listener: OnClickOrderListener) {
        orderClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        return MainViewHolder(
            OrdersRecyclerViewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        Log.d("size", orderlist.size.toString())
        return orderlist.size
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val order = orderlist[position]
        holder.binditem(order)
    }
}