package com.antsglobe.restcommerse.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.antsglobe.restcommerse.R
import com.antsglobe.restcommerse.Utils.PreferenceManager
import com.antsglobe.restcommerse.databinding.SearchProductListRecyclerViewBinding
import com.antsglobe.restcommerse.model.Response.AllProductsList
import com.bumptech.glide.Glide


class AllProductListAdaptor(
    var items: List<AllProductsList>,
    val context: Context/*, val cartlist: List<CartListData>*/
) :
    RecyclerView.Adapter<AllProductListAdaptor.MainViewHolder>() {

    private var productListClickListener: OnClickProductListListener? = null

    private lateinit var sharedPreferences: PreferenceManager

    fun updateStudentList(data: List<AllProductsList>) {
        this.items = data
        notifyDataSetChanged()
    }

    inner class MainViewHolder(val itemsBinding: SearchProductListRecyclerViewBinding) :
        RecyclerView.ViewHolder(itemsBinding.root) {
        fun bindItem(list: AllProductsList) {

            sharedPreferences = PreferenceManager(context)
            if (sharedPreferences.getMode() == true) {
                itemsBinding.fullscreen.setBackgroundColor(Color.BLACK)
                itemsBinding.ll.setBackgroundResource(R.drawable.profile_round_corner_bg_dark_wish)
                itemsBinding.ll.setBackgroundResource(R.drawable.profile_round_corner_bg_dark_wish)
                itemsBinding.foodItemName.setTextColor(context.resources.getColor(R.color.whitefordark))
                itemsBinding.tvRatingOffer.setTextColor(context.resources.getColor(R.color.whitefordark))
                itemsBinding.foodItemName.setTextColor(context.resources.getColor(R.color.whitefordark))
                itemsBinding.foodItemPrice.setTextColor(context.resources.getColor(R.color.whitefordark))
                itemsBinding.foodItemPrice.setTextColor(context.resources.getColor(R.color.whitefordark))
                itemsBinding.productreviews.setTextColor(context.resources.getColor(R.color.whitefordark))
                //itemsBinding.ratingBarIndicator.setBackgroundResource(R.drawable.profile_round_corner_bg_gray_dark)
                itemsBinding.llimage.backgroundTintList =
                    ColorStateList.valueOf(context.resources.getColor(R.color.blackfordark))
                itemsBinding.addtocart.setImageDrawable(context.resources.getDrawable(R.drawable.add_to_cart_dark))

            }
            itemsBinding.foodItemName.text = list.productname!!
            itemsBinding.foodItemPrice.text = "₹${list.disc_price!!.toString()}"

            itemsBinding.price.paintFlags =
                itemsBinding.price.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            itemsBinding.price.text = "₹${list.product_price.toString()}"

            itemsBinding.productreviews.text = "(${list.totalreview.toString()})"
            val rating = String.format("%.1f", list.rating)

            itemsBinding.saved.text = "YOU SAVE ₹" + list.Off_Price?.toInt()

            itemsBinding.ratingBarIndicator.rating = rating.toFloat()
            itemsBinding.tvRatingOffer.text = rating
//            itemsBinding.tvRatingOffer.text = list.rating.toString()


            if (list.prod_availability.toString() == "Out of Stock") {
//                itemsBinding.stockChecking.visibility = View.VISIBLE
//                itemsBinding.foodItemImg.setColorFilter(ContextCompat.getColor(context, R.color.transparent_no_stock))

                itemsBinding.addtocart.visibility = View.GONE
                itemsBinding.outOfStock.visibility = View.VISIBLE
                itemsBinding.OOS.visibility = View.VISIBLE
            } else {
//                itemsBinding.foodItemImg.setColorFilter(ContextCompat.getColor(context, R.color.transparent))

//                itemsBinding.stockChecking.visibility = View.GONE
                itemsBinding.addtocart.visibility = View.VISIBLE
                itemsBinding.outOfStock.visibility = View.GONE
                itemsBinding.OOS.visibility = View.GONE

            }

            itemsBinding.addtocart.visibility = View.GONE
//            var addedtocart = false
//            cartlist.forEach {
//                if (list.PID == it.product_id) {
//                    addedtocart = true
//                    itemsBinding.addtocart.setImageResource(R.drawable.added_to_cart)
//                }
//            }
//            itemsBinding.addtocart.setOnClickListener {
//                if (addedtocart) {
//                    itemsBinding.addtocart.setImageResource(R.drawable.add_to_cart)
//                    productListClickListener?.onDeleteCartClick(list.PID.toString())
//                    addedtocart = false
//                } else {
//                    itemsBinding.addtocart.setImageResource(R.drawable.added_to_cart)
//                    productListClickListener?.onaddtoCartClick(
//                        list.PID.toString(),
//                        list.disc_price.toString()
//                    )
//                    addedtocart = true
//                }
//            }


            Glide.with(itemsBinding.foodItemImg)
                .load(list.product_url)
                .into(itemsBinding.foodItemImg)

            itemView.setOnClickListener {
                val pId = list.PID
                productListClickListener?.onProductListClick(pId.toString())
            }
        }

    }

    interface OnClickProductListListener {
        fun onProductListClick(pId: String)
        fun onaddtoCartClick(pId: String, price: String)

        fun onDeleteCartClick(pId: String)
    }

    fun setOnClickListener(listener: OnClickProductListListener) {
        productListClickListener = listener
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val item = items[position]
        holder.bindItem(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        return MainViewHolder(
            SearchProductListRecyclerViewBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )


    }

    override fun getItemCount(): Int {
        return items.size
    }


}