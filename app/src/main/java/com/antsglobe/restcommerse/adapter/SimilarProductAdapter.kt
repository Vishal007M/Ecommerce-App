package com.antsglobe.restcommerse.adapter

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.antsglobe.restcommerse.R
import com.antsglobe.restcommerse.Utils.PreferenceManager
import com.antsglobe.restcommerse.databinding.SimilarProductsRecyclerViewBinding
import com.antsglobe.restcommerse.model.Response.ProductList
import com.bumptech.glide.Glide

class SimilarProductAdapter(
    var items: List<ProductList?>,
    val context: Context
) :
    RecyclerView.Adapter<SimilarProductAdapter.MainViewHolder>() {

    private var productListClickListener: OnClickProductListListener? = null

    private lateinit var sharedPreferences: PreferenceManager

    inner class MainViewHolder(val itemsBinding: SimilarProductsRecyclerViewBinding) :
        RecyclerView.ViewHolder(itemsBinding.root) {
        fun bindItem(list: ProductList) {

            itemsBinding.productname.text = list.productname!!
            itemsBinding.discPrice.text = "₹${list.disc_price!!.toString()}"

            itemsBinding.productPrice.paintFlags =
                itemsBinding.productPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            itemsBinding.productPrice.text = "₹${list.product_price.toString()}"

            sharedPreferences = PreferenceManager(context)
            if (sharedPreferences.getMode() == true) {
                itemsBinding.background.setBackgroundResource(R.drawable.profile_round_corner_bg_dark_wish)
//                itemsBinding.wishcard.setBackgroundResource(R.drawable.profile_round_corner_bg_dark_wishcard)
                itemsBinding.productname.setTextColor(context.resources.getColor(R.color.whitefordark))
                itemsBinding.tvRatingOffer.setTextColor(context.resources.getColor(R.color.whitefordark))
                itemsBinding.discPrice.setTextColor(context.resources.getColor(R.color.whitefordark))
                itemsBinding.tvPeopleCustomer.setTextColor(context.resources.getColor(R.color.whitefordark))
            }
//            itemsBinding.saved.text = "YOU SAVE ₹" + list.Off_Price?.toInt()

//            itemsBinding.tvPeopleCustomer.text = "(${list.totalreview.toString()})"
            val rating: String
            if (list.rating == null) {
                rating = "0.0"
            } else {
                rating = String.format("%.1f", list.rating)
            }

            if (list.prod_availability.toString() == "Out of Stock") {
//                itemsBinding.stockChecking.visibility = View.VISIBLE
//                itemsBinding.productUrl.setColorFilter(ContextCompat.getColor(context, R.color.transparent_no_stock))

                itemsBinding.showPrice.visibility = View.GONE
                itemsBinding.showStock.visibility = View.VISIBLE
                itemsBinding.OOS.visibility = View.VISIBLE
            } else {
//                itemsBinding.productUrl.setColorFilter(ContextCompat.getColor(context, R.color.transparent))

                itemsBinding.showPrice.visibility = View.VISIBLE
                itemsBinding.showStock.visibility = View.GONE
                itemsBinding.OOS.visibility = View.GONE


            }

            itemsBinding.tvPeopleCustomer.text = "(${list.totalreview.toString()})"
            itemsBinding.ratingBarIndicator.rating = rating.toFloat()
            itemsBinding.tvRatingOffer.text = rating
//            itemsBinding.tvRatingOffer.text = list.rating.toString()

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
//                    productListClickListener?.onDeleteCartClick(
//                        list.PID.toString(),
//                        list.disc_price.toString()
//                    )
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

            Glide.with(itemsBinding.productUrl)
                .load(list.product_url!!)
                .into(itemsBinding.productUrl)

            itemView.setOnClickListener {
                val pId = list.PID
                productListClickListener?.onProductListClick(pId!!.toString())
            }
        }

    }


    interface OnClickProductListListener {
        fun onProductListClick(categoryId: String)
//        fun onaddtoCartClick(pId: String, price: String)
//
//        fun onDeleteCartClick(pId: String, price: String)
    }

    fun setOnClickCategoryListener(listener: OnClickProductListListener) {
        productListClickListener = listener
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val item = items[position]
        holder.bindItem(item!!)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        return MainViewHolder(
            SimilarProductsRecyclerViewBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )


    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun updateStudentList(data: List<ProductList>) {
        this.items = data
        notifyDataSetChanged()
    }


}