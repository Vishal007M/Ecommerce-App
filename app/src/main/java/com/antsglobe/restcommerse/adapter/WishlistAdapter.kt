package com.antsglobe.restcommerse.adapter

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.antsglobe.restcommerse.R
import com.antsglobe.restcommerse.Utils.PreferenceManager
import com.antsglobe.restcommerse.databinding.WishlistRecyclerviewBinding
import com.antsglobe.restcommerse.model.Response.CartListData
import com.antsglobe.restcommerse.model.Response.MostPopularData
import com.squareup.picasso.Picasso

class WishlistAdapter(
    val list: List<MostPopularData?>,
    val cartlist: List<CartListData>,
    val context: Context
) :
    RecyclerView.Adapter<WishlistAdapter.MainViewHolder>() {


    private var productDeleteListener: OnClickDeleteListener? = null
    private lateinit var sharedPreferences: PreferenceManager

    inner class MainViewHolder(val itemsbinding: WishlistRecyclerviewBinding) :
        RecyclerView.ViewHolder(itemsbinding.root) {
        fun bindItem(item: MostPopularData) {
            Picasso.get().load(item.product_url).into(itemsbinding.foodItemImg)
            sharedPreferences = PreferenceManager(context)
            if (sharedPreferences.getMode() == true) {
                itemsbinding.background.setBackgroundResource(R.drawable.profile_round_corner_bg_dark_wish)
                itemsbinding.foodItemName.setTextColor(context.resources.getColor(R.color.whitefordark))
                itemsbinding.tvRatingOffer.setTextColor(context.resources.getColor(R.color.whitefordark))
                itemsbinding.foodItemName.setTextColor(context.resources.getColor(R.color.whitefordark))
                itemsbinding.foodItemPrice.setTextColor(context.resources.getColor(R.color.whitefordark))
                itemsbinding.foodItemPrice.setTextColor(context.resources.getColor(R.color.whitefordark))
                itemsbinding.productreviews.setTextColor(context.resources.getColor(R.color.whitefordark))
                itemsbinding.itemDelete.setBackgroundResource(R.drawable.profile_round_corner_bg_dark_wish)
                //itemsbinding.ratingBarIndicator.setBackgroundResource(R.drawable.profile_round_corner_bg_gray_dark)
                itemsbinding.imagebackground.backgroundTintList =
                    ColorStateList.valueOf(context.resources.getColor(R.color.blackfordark))
                itemsbinding.outOfstk.setTextColor(context.resources.getColor(R.color.blackfordark))
                itemsbinding.wishlistText.setTextColor(context.resources.getColor(R.color.blackfordark))

//                itemsbinding.itemProductWishlist.setImageDrawable(context.resources.getDrawable(R.drawable.add_to_cart_dark))
            } else {
                itemsbinding.background.setBackgroundResource(R.drawable.round_corner_bg)
                itemsbinding.foodItemName.setTextColor(context.resources.getColor(R.color.blackfordark))
                //itemsbinding.tvRatingOffer.setTextColor(context.resources.getColor(R.color.blackfordark))
                itemsbinding.foodItemName.setTextColor(context.resources.getColor(R.color.blackfordark))
                itemsbinding.itemDelete.setBackgroundResource(R.drawable.round_corner_bg)
                itemsbinding.foodItemPrice.setTextColor(context.resources.getColor(R.color.blackfordark))
                itemsbinding.foodItemPrice.setTextColor(context.resources.getColor(R.color.blackfordark))
                itemsbinding.productreviews.setTextColor(context.resources.getColor(R.color.dark_grey))
                itemsbinding.imagebackground.setBackgroundResource(R.drawable.round_corner_bg)
//                itemsbinding.itemProductWishlist.setImageDrawable(context.resources.getDrawable(R.drawable.add_to_cart))
            }

            if (item.prod_type == "Non-Veg") {
                itemsbinding.foodtype.setImageResource(R.drawable.non_veg_icon)
            } else if (item.prod_type == "Veg") {
                itemsbinding.foodtype.setImageResource(R.drawable.veg_icon)
            }

            if (item.prod_availability.toString() == "Out of Stock") {
//                itemsBinding.stockChecking.visibility = View.VISIBLE
//                itemsBinding.foodItemImg.setColorFilter(ContextCompat.getColor(context, R.color.transparent_no_stock))

                itemsbinding.addtocart.visibility = View.GONE
                itemsbinding.outOfStock.visibility = View.VISIBLE
                itemsbinding.OOS.visibility = View.VISIBLE
            } else {
//                itemsBinding.foodItemImg.setColorFilter(ContextCompat.getColor(context, R.color.transparent))

//                itemsBinding.stockChecking.visibility = View.GONE
                itemsbinding.addtocart.visibility = View.VISIBLE
                itemsbinding.outOfStock.visibility = View.GONE
                itemsbinding.OOS.visibility = View.GONE

            }

            itemsbinding.foodItemName.text = item.productname
            itemsbinding.foodItemPrice.text = " ₹" + item.disc_price.toString()
            itemsbinding.price.text = "₹" + item.product_price.toString()
            itemsbinding.price.setPaintFlags(itemsbinding.price.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)
            itemsbinding.productreviews.text = "(" + item.totalreview + ")"
            val rating = String.format("%.1f", item.rating)
            itemsbinding.tvRatingOffer.text = rating
            itemsbinding.ratingBarIndicator.rating = rating.toFloat()
            itemsbinding.saved.text = "YOU SAVE" + " ₹" + item.Off_Price?.toInt()
            itemsbinding.itemDelete.setOnClickListener {
                val pid = item.PID
                productDeleteListener?.onProductdeleteClick(
                    pid.toString()
                )
                val animator = ValueAnimator.ofInt(270, 0)
                animator.addUpdateListener { valueAnimator ->
                    val animatedValue = valueAnimator.animatedValue as Int
                    val layoutParams = itemView.layoutParams
                    layoutParams.height = animatedValue
                    itemView.layoutParams = layoutParams
                }
                animator.duration = 1000
                animator.start()
            }
            itemView.setOnClickListener {
                val pid = item.PID
                productDeleteListener?.onProductClick(
                    pid.toString()
                )
            }

            var addedtocart = false
            cartlist.forEach {
                if (item.PID == it.product_id) {
                    addedtocart = true
//                    itemsbinding.itemProductWishlist.setImageResource(R.drawable.added_to_cart)

                    itemsbinding.wishlistText.text = "Remove from Cart"
                }
            }
            var quantity = "1"

            itemsbinding.addtocart.setOnClickListener {
                if (addedtocart) {
//                    if (sharedPreferences.getMode() == true) {
//                        itemsbinding.itemProductWishlist.setImageResource(R.drawable.add_to_cart_dark)
//                    } else {
//                        itemsbinding.itemProductWishlist.setImageResource(R.drawable.add_to_cart)
//                    }

                    itemsbinding.wishlistText.text = "Add to Cart"

                    productDeleteListener?.onDeleteCartClick(
                        item.PID.toString(),
                        item.product_price.toString(),
                        item.disc_price.toString(),
                        "",
                    )
                    addedtocart = false
                } else {
//                    itemsbinding.itemProductWishlist.setImageResource(R.drawable.added_to_cart)
                    itemsbinding.wishlistText.text = "Remove from Cart"

                    productDeleteListener?.onaddtoCartClick(
                        item.PID.toString(),
                        item.product_price.toString(),
                        item.disc_price.toString(),
                        quantity,
                        item.disc_price.toString(),
                    )
                    addedtocart = true
                }
            }


        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        return MainViewHolder(
            WishlistRecyclerviewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }


    interface OnClickDeleteListener {
        fun onProductdeleteClick(
            pId: String
        )

        fun onProductClick(pId: String)

        fun onaddtoCartClick(
            pId: String,
            price: String,
            discountPrice: String,
            quantity: String,
            totalPrice: String
        )

        fun onDeleteCartClick(pId: String, price: String, discountPrice: String, variation: String)
    }

    fun setOnClickProductListener(listener: OnClickDeleteListener) {
        productDeleteListener = listener
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val item = list[position]
        holder.bindItem(item!!)

    }

    override fun getItemCount(): Int {
        return list.size
    }

}