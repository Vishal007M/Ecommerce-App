package com.antsglobe.restcommerse.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.antsglobe.restcommerse.R
import com.antsglobe.restcommerse.Utils.PreferenceManager
import com.antsglobe.restcommerse.databinding.ReviewRecyclerViewItemBinding
import com.antsglobe.restcommerse.model.Response.ReviewList
import java.text.SimpleDateFormat
import java.util.Date

class ReviewListAdaptor(val items: List<ReviewList?>, val context: Context) :
    RecyclerView.Adapter<ReviewListAdaptor.MainViewHolder>() {

    private lateinit var sharedPreferences: PreferenceManager

    inner class MainViewHolder(val itemsBinding: ReviewRecyclerViewItemBinding) :
        RecyclerView.ViewHolder(itemsBinding.root) {


        fun bindItem(list: ReviewList) {

            sharedPreferences = PreferenceManager(context)
            itemsBinding.tvCustomerName.text = list.name
            itemsBinding.tvReviewMessage.text = list.cust_review
            itemsBinding.reviewRatingBar.setIsIndicator(true)
            itemsBinding.reviewRatingBar.setRating(list.cust_rating!!.toFloat())

            if (sharedPreferences.getMode() == true) {
                itemsBinding.background.setBackgroundColor(Color.parseColor("#1F201D"))

                itemsBinding.tvCustomerName.setTextColor(context.resources.getColor(R.color.whitefordark))
                itemsBinding.tvTimeDate.setTextColor(context.resources.getColor(R.color.orange))
                itemsBinding.tvReviewMessage.setTextColor(context.resources.getColor(R.color.dark_grey))
            }

            val inputDateString = list.review_date
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            val date: Date = sdf.parse(inputDateString)
            val formattedDate = SimpleDateFormat("yyyy-MM-dd").format(date)
            itemsBinding.tvTimeDate.text = "$formattedDate"
        }
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val item = items[position]
        holder.bindItem(item!!)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        return MainViewHolder(
            ReviewRecyclerViewItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }
}