package com.antsglobe.aeroquiz

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.antsglobe.restcommerse.Utils.PreferenceManager
import com.antsglobe.restcommerse.databinding.RatingNReviewsRecycleViewBinding
import com.antsglobe.restcommerse.model.Response.ReviewedList
import com.bumptech.glide.Glide
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

class ReviewedAdapter(
    val items: List<ReviewedList>,
    val dark: Boolean,
    private val context: Context
) :
    RecyclerView.Adapter<ReviewedAdapter.MainViewHolder>() {

    private lateinit var sharedPreferences: PreferenceManager
//    private var productClickListener: OnClickProductListener? = null

    inner class MainViewHolder(val itemsBinding: RatingNReviewsRecycleViewBinding) :
        RecyclerView.ViewHolder(itemsBinding.root) {

        fun bindItem(list: ReviewedList) {
            sharedPreferences = PreferenceManager(context)

            Glide.with(itemsBinding.foodItemImg)
                .load(list.product_url)
                .into(itemsBinding.foodItemImg)

            itemsBinding.foodItemName.text = list.productname

            val rating: String
            if (list.cust_rating == null) {
                rating = "0.0"
            } else {
                rating = String.format("%.1f", list.cust_rating)
            }
            itemsBinding.ratingBarIndicator.rating = rating.toFloat()

            try {

                if (list.orderdate.isNullOrEmpty()) {

                } else {
                    val f: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")

                    val d = f.parse(list.orderdate)
                    val date: DateFormat = SimpleDateFormat("dd-MMM-yy")
                    val time: DateFormat = SimpleDateFormat("hh:mm a")
                    val dayOfWeek: DateFormat = SimpleDateFormat("EEE", Locale.getDefault())

                    val formattedTime = time.format(d).uppercase(Locale.ROOT)
                    val formattedDayOfWeek = dayOfWeek.format(d)

                    itemsBinding.dateAndTime.text =
                        "Order Date: $formattedDayOfWeek ${date.format(d)}"

                    itemsBinding.ratingCount.text =
                        "(${list.cust_rating})"
                }


            } catch (e: ParseException) {
                e.printStackTrace()
            }

            itemsBinding.reviewedText.text = list.cust_review.toString()

            if (dark) {
//                itemsBinding.fullscreen.setBackgroundColor(Color.parseColor("#1F201D"))
//                itemsBinding.NotificationText.setTextColor(Color.GRAY)
//                itemsBinding.notificationHeading.setTextColor(Color.WHITE)
            }

//            itemView.setOnClickListener {
//                val pId = list.product_id
//
//                productClickListener?.onProductIdClick(
//                    pId.toString()
//                )
//            }


        }
    }


//    interface OnClickProductListener {
//        fun onProductIdClick(pId: String)
//    }
//
//    fun setOnProductClickListener(listener: OnClickProductListener) {
//        productClickListener = listener
//    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val item = items[position]
        holder.bindItem(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        return MainViewHolder(
            RatingNReviewsRecycleViewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }


}