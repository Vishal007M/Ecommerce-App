package com.antsglobe.aeroquiz

import android.content.Context
import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.antsglobe.restcommerse.R
import com.antsglobe.restcommerse.Utils.PreferenceManager
import com.antsglobe.restcommerse.databinding.MostPopularRecyclerViewBinding
import com.antsglobe.restcommerse.model.Response.TopMostPopularData
import com.squareup.picasso.Picasso


class TopMostPopularAdapter(val items: List<TopMostPopularData>, val context: Context) :
    RecyclerView.Adapter<TopMostPopularAdapter.MainViewHolder>() {

    private var productClickListener: OnClickProductListener? = null

    private lateinit var sharedpreferences: PreferenceManager

    inner class MainViewHolder(val itemsBinding: MostPopularRecyclerViewBinding) :
        RecyclerView.ViewHolder(itemsBinding.root) {

        fun bindItem(list: TopMostPopularData) {
            sharedpreferences = PreferenceManager(context)

            if (sharedpreferences.getMode() == true) {
                itemsBinding.background.setBackgroundResource(R.drawable.profile_round_corner_bg_dark_wish)
                itemsBinding.wishcard.setBackgroundResource(R.drawable.profile_round_corner_bg_dark_wishcard)
                itemsBinding.productname.setTextColor(context.resources.getColor(R.color.whitefordark))
                itemsBinding.tvRatingOffer.setTextColor(context.resources.getColor(R.color.whitefordark))
                itemsBinding.discPrice.setTextColor(context.resources.getColor(R.color.whitefordark))
                itemsBinding.tvPeopleCustomer.setTextColor(context.resources.getColor(R.color.whitefordark))

            } else {
                itemsBinding.background.setBackgroundResource(R.drawable.round_corner_bg)
                itemsBinding.wishcard.setBackgroundResource(R.drawable.profile_round_corner_bg_wish)
                itemsBinding.productname.setTextColor(context.resources.getColor(R.color.blackfordark))
                itemsBinding.tvRatingOffer.setTextColor(context.resources.getColor(R.color.blackfordark))
                itemsBinding.discPrice.setTextColor(context.resources.getColor(R.color.blackfordark))
                itemsBinding.tvPeopleCustomer.setTextColor(context.resources.getColor(R.color.dark_grey))
            }
            itemsBinding.productname.text = list.productname

            if (list.prod_type == "Non-Veg") {
                itemsBinding.foodtype.setImageResource(R.drawable.non_veg_icon)
            } else if (list.prod_type == "Veg") {
                itemsBinding.foodtype.setImageResource(R.drawable.veg_icon)
            }

            var imageUrl = list.product_url
            Picasso.get().load(imageUrl).into(itemsBinding.productUrl)

            if (list.prod_availability.toString() == "Out of Stock"){
//                itemsBinding.stockChecking.visibility = View.VISIBLE
//                itemsBinding.productUrl.setColorFilter(ContextCompat.getColor(context, R.color.transparent_no_stock))

                itemsBinding.showPrice.visibility = View.GONE
                itemsBinding.showStock.visibility = View.VISIBLE
                itemsBinding.OOS.visibility = View.VISIBLE
            }else{
//                itemsBinding.productUrl.setColorFilter(ContextCompat.getColor(context, R.color.transparent))

                itemsBinding.showPrice.visibility = View.VISIBLE
                itemsBinding.showStock.visibility = View.GONE
                itemsBinding.OOS.visibility = View.GONE
            }

            itemsBinding.productPrice.text = "₹ " + list.product_price.toString()
            itemsBinding.productPrice.setPaintFlags(itemsBinding.productPrice.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)

            itemsBinding.discPrice.text = "₹ " + list.disc_price.toString()

            itemsBinding.tvPeopleCustomer.text = "(${list.totalreview.toString()})"

            val rating = String.format("%.1f", list.rating)

            itemsBinding.ratingBarIndicator.rating = rating.toFloat()
            itemsBinding.tvRatingOffer.text = rating

            var wishstatus = list.wishlist_status
            if (wishstatus) {
                itemsBinding.wishlist.setImageResource(R.drawable.heart_plus_tapped)
                Log.d("d", wishstatus.toString())
            } else {
                if (sharedpreferences.getMode() == true) {
                    itemsBinding.wishlist.setImageResource(R.drawable.heart_plus_fordark)
                } else {
                    itemsBinding.wishlist.setImageResource(R.drawable.heart_plus)
                }

                Log.d("d", wishstatus.toString())
            }
            itemsBinding.wishlist.setOnClickListener {
                val pid = list.PID
                if (wishstatus) {
                    productClickListener?.onWishdeleteclick(pid.toString())
                    if (sharedpreferences.getMode() == true) {
                        itemsBinding.wishlist.setImageResource(R.drawable.heart_plus_fordark)
                    } else {
                        itemsBinding.wishlist.setImageResource(R.drawable.heart_plus)
                    }

                    wishstatus = false
                    Log.d("d", "3")
                } else {
                    productClickListener?.onWishaddclick(pid.toString())
                    itemsBinding.wishlist.setImageResource(R.drawable.heart_plus_tapped)
                    wishstatus = true
                    Log.d("d", "4")
                }
            }

            itemView.setOnClickListener {
                val pId = list.PID
                productClickListener?.onProductIdClick(
                    pId.toString()
                )
            }


        }

    }

    interface OnClickProductListener {
        fun onProductIdClick(
            pId: String
        )

        fun onWishaddclick(pId: String)
        fun onWishdeleteclick(pId: String)

    }

    fun setOnClickProductListener(listener: OnClickProductListener) {
        productClickListener = listener
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val item = items[position]
        holder.bindItem(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        return MainViewHolder(
            MostPopularRecyclerViewBinding.inflate(
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