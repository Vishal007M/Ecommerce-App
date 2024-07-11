package com.antsglobe.restcommerse.adapter

import android.annotation.SuppressLint
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.antsglobe.restcommerse.databinding.CouponInvalidRecyclerViewBinding
import com.antsglobe.restcommerse.model.OfferItemList
import com.antsglobe.restcommerse.model.Response.AddressList
import com.antsglobe.restcommerse.model.Response.Coupon
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class CouponInvalidAdaptor(var items: List<Coupon?>) :
    RecyclerView.Adapter<CouponInvalidAdaptor.MainViewHolder>() {

    inner class MainViewHolder(val itemsBinding: CouponInvalidRecyclerViewBinding) :
        RecyclerView.ViewHolder(itemsBinding.root) {
        @RequiresApi(Build.VERSION_CODES.O)
        fun bindItem(list: Coupon) {
            val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
            val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val from = list.valid_from
            val to = list.valid_to
            val dateTime = LocalDateTime.parse(from, inputFormatter)
            val dateTime2 = LocalDateTime.parse(to, inputFormatter)
            val dateOnly = dateTime.format(outputFormatter)
            val dateOnly2 = dateTime2.format(outputFormatter)

            itemsBinding.tvCouponName.text = list.coupon_name
            itemsBinding.tvTiilDate.text = "valid until:-" + dateOnly2
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val item = items[position]
        holder.bindItem(item!!)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        return MainViewHolder(
            CouponInvalidRecyclerViewBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateStudentList(data: List<Coupon>) {
        this.items = data
        notifyDataSetChanged()
    }
}