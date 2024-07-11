package com.antsglobe.restcommerse.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.antsglobe.restcommerse.databinding.ProductPaymentRecyclerViewBinding
import com.antsglobe.restcommerse.model.Response.OrderDetail


class OrderItemAdaptor(val items: List<OrderDetail?>) :
    RecyclerView.Adapter<OrderItemAdaptor.MainViewHolder>() {

    inner class MainViewHolder(val itemsBinding: ProductPaymentRecyclerViewBinding) :
        RecyclerView.ViewHolder(itemsBinding.root) {
        fun bindItem(list: OrderDetail) {

            if (list.size.isNullOrEmpty()) {
                itemsBinding.tvProductName.text = "${list.productname}"
            } else {
                itemsBinding.tvProductName.text = "${list.productname} ${list.size}"
            }

            val txtRating = String.format("%.0f", list.quantity?.toDouble())
            itemsBinding.tvProductQuantity.text = txtRating

            // itemsBinding.tvProductPrice.text = "₹" + list.original_price.toString()
//            itemsBinding.tvProductPrice.text = "₹" + list.dis_price.toString()

            try {
                if (list.dis_price == null) {
                    itemsBinding.tvProductPrice.text = "₹" + list.original_price.toString()
                } else {
                    itemsBinding.tvProductPrice.text = "₹" + list.dis_price.toString()
                }
            } catch (e: Exception) {
                println("catch the error " + e.message)
            }

        }
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val item = items[position]
        holder.bindItem(item!!)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        return MainViewHolder(
            ProductPaymentRecyclerViewBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }
}