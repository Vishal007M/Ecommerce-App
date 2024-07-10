package com.antsglobe.restcommerse.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.antsglobe.restcommerse.databinding.CouponRecyclerViewItemBinding
import com.antsglobe.restcommerse.model.OfferItemList
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import com.antsglobe.restcommerse.model.Response.Coupon
import java.util.Date
import java.util.Locale


class CouponListAdaptor(val items: List<Coupon?>) :
    RecyclerView.Adapter<CouponListAdaptor.MainViewHolder>()  {


    private val currentdate=getCurrentDateTime()
    inner class MainViewHolder(val itemsBinding: CouponRecyclerViewItemBinding) :
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

            val result=compareDates(list.valid_to.toString(),currentdate)
            if (result<0) {
                itemsBinding.background.layoutParams.height=0
                itemsBinding.background.visibility= View.GONE


            }
            itemsBinding.tvCouponName.text = list.coupon_name
            itemsBinding.tvTiilDate.text = "valid until:-"+dateOnly2


        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val item = items[position]
        holder.bindItem(item!!)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        return MainViewHolder(
            CouponRecyclerViewItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun compareDates(date1: String, date2: String): Int {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val dateTime1 = format.parse(date1)
        val dateTime2 = format.parse(date2)

        return dateTime1.compareTo(dateTime2)
    }
    fun getCurrentDateTime(): String {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        return format.format(Date())
    }
}