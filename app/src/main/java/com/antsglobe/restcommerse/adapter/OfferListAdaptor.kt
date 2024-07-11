package com.antsglobe.restcommerse.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.antsglobe.restcommerse.R
import com.antsglobe.restcommerse.Utils.PreferenceManager
import com.antsglobe.restcommerse.databinding.OfferRecyclerViewItemBinding
import com.antsglobe.restcommerse.model.Response.AllProductsList
import com.bumptech.glide.Glide

class OfferListAdaptor(val items: List<AllProductsList>, val context: Context) :
    RecyclerView.Adapter<OfferListAdaptor.MainViewHolder>() {

    private lateinit var sharedPreferences: PreferenceManager
    private var productClickListener: OnClickProductListener? = null


    inner class MainViewHolder(val itemsBinding: OfferRecyclerViewItemBinding) :
        RecyclerView.ViewHolder(itemsBinding.root) {
        fun bindItem(list: AllProductsList) {
            sharedPreferences = PreferenceManager(context)
            if (sharedPreferences.getMode() == true) {
                itemsBinding.background.setBackgroundResource(R.drawable.profile_round_corner_bg_dark_wish)
                itemsBinding.wishcard.setBackgroundResource(R.drawable.profile_round_corner_bg_dark_wishcard)
                itemsBinding.productname.setTextColor(context.resources.getColor(R.color.whitefordark))
                itemsBinding.tvRatingOffer.setTextColor(context.resources.getColor(R.color.whitefordark))
                itemsBinding.productreviews.setTextColor(context.resources.getColor(R.color.whitefordark))
                itemsBinding.wishlist.setImageResource(R.drawable.heart_plus_fordark)
                itemsBinding.innerbackground.setBackgroundResource(R.drawable.profile_round_corner_bg_dark_wishcard)
            }
            var offprice: Int
            offprice = list.Off_Price!!.toInt()
            Glide.with(itemsBinding.productUrl)
                .load(list.product_url)
                .into(itemsBinding.productUrl)
            itemsBinding.tvSave.text = "YOU SAVE ₹${offprice}"
            itemsBinding.productname.text = list.productname

            itemsBinding.productreviews.text = "(${list.totalreview.toString()})"


            val rating: String
            if (list.rating == null) {
                rating = "0.0"
            } else {
                rating = String.format("%.1f", list.rating)
            }
            itemsBinding.ratingBarIndicator.rating = rating.toFloat()
            itemsBinding.tvRatingOffer.text = rating

            itemView.setOnClickListener {
                val pId = list.PID
                productClickListener?.onProductIdClick(
                    pId.toString()
                )
            }

        }
    }

    interface OnClickProductListener {
        fun onProductIdClick(pId: String)
    }

    fun setOnClickProductViewListener(listener: OnClickProductListener) {
        productClickListener = listener
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val item = items[position]
        holder.bindItem(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        return MainViewHolder(
            OfferRecyclerViewItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }
}