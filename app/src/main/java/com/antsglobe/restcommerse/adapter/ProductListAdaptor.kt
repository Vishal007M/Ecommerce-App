package com.antsglobe.restcommerse.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.antsglobe.restcommerse.R
import com.antsglobe.restcommerse.Utils.PreferenceManager
import com.antsglobe.restcommerse.databinding.ProductListRecyclerViewBinding
import com.antsglobe.restcommerse.model.Response.CartListData
import com.antsglobe.restcommerse.model.Response.ProductList
import com.bumptech.glide.Glide

class ProductListAdaptor(
    var items: List<ProductList?>?,
    var cartlist: List<CartListData?>?,
    val context: Context
) :
    RecyclerView.Adapter<ProductListAdaptor.MainViewHolder>() {

    private var productListClickListener: OnClickProductListListener? = null

    private lateinit var sharedPreferences: PreferenceManager

    inner class MainViewHolder(val itemsBinding: ProductListRecyclerViewBinding) :
        RecyclerView.ViewHolder(itemsBinding.root) {
        fun bindItem(list: ProductList) {
            sharedPreferences = PreferenceManager(context)

            if (sharedPreferences.getMode() == true) {
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

            itemsBinding.saved.text = "YOU SAVE ₹" + list.Off_Price?.toInt()

            if (list.prod_type == "Non-Veg") {
                itemsBinding.foodtype.setImageResource(R.drawable.non_veg_icon)
            } else if (list.prod_type == "Veg") {
                itemsBinding.foodtype.setImageResource(R.drawable.veg_icon)
            }


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

            itemsBinding.productreviews.text = "(${list.totalreview.toString()})"

            val rating: String
            if (list.rating == null) {
                rating = "0.0"
            } else {
                rating = String.format("%.1f", list.rating)
            }
            itemsBinding.ratingBarIndicator.rating = rating.toFloat()
            itemsBinding.tvRatingOffer.text = rating

            var addedtocart = false
            Log.d("checking",cartlist.toString())
            cartlist?.forEach {
                if (list.PID == it?.product_id) {
                    addedtocart = true
                    itemsBinding.addtocart.setImageResource(R.drawable.added_to_cart)
                }
            }
            var quantity = "1"
            itemsBinding.addtocart.setOnClickListener {
                if (addedtocart) {
                    if (sharedPreferences.getMode() == true){
                        itemsBinding.addtocart.setImageResource(R.drawable.add_to_cart_dark)
                    }
                    else{
                        itemsBinding.addtocart.setImageResource(R.drawable.add_to_cart)
                    }

                    productListClickListener?.onDeleteCartClick(
                        list.PID.toString(),
                        list.disc_price.toString()
                    )
                    addedtocart = false
                } else {
                    itemsBinding.addtocart.setImageResource(R.drawable.added_to_cart)
                    productListClickListener?.onaddtoCartClick(
                        list.PID.toString(),
                        list.product_price.toString(),
                        list.disc_price.toString(),
                        quantity,
                        list.disc_price.toString(),
                    )
                    addedtocart = true
                }
            }

            Glide.with(itemsBinding.foodItemImg)
                .load(list.product_url!!)
                .into(itemsBinding.foodItemImg)

            itemView.setOnClickListener {
                val pId = list.PID
                productListClickListener?.onProductListClick(pId!!.toString())
            }
        }

    }


    interface OnClickProductListListener {
        fun onProductListClick(categoryId: String)
        fun onaddtoCartClick(
            pId: String,
            price: String,
            discountPrice: String,
            quantity: String,
            totalPrice: String
        )

        fun onDeleteCartClick(pId: String, price: String)
    }

    fun setOnClickCategoryListener(listener: OnClickProductListListener) {
        productListClickListener = listener
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val item = items?.get(position)
        holder.bindItem(item!!)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        return MainViewHolder(
            ProductListRecyclerViewBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )


    }

    override fun getItemCount(): Int {
        return items?.size!!
    }

    fun updateStudentList(data: List<ProductList?>?) {
        this.items = data!!
        notifyDataSetChanged()
    }
    fun updateCartList(data: List<CartListData?>?) {
        this.cartlist = data!!
        notifyDataSetChanged()
    }


}