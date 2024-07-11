package com.antsglobe.aeroquiz

import android.animation.ValueAnimator
import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.antsglobe.restcommerse.R
import com.antsglobe.restcommerse.Utils.PreferenceManager
import com.antsglobe.restcommerse.databinding.CartRecyclerViewBinding
import com.antsglobe.restcommerse.model.Response.CartListData
import com.bumptech.glide.Glide

class CartListAdapter(val items: List<CartListData>, val context: Context) :
    RecyclerView.Adapter<CartListAdapter.MainViewHolder>() {

    private var productClickListener: OnClickProductListener? = null
    private var productDeleteListener: OnClickDeleteListener? = null
    private var productAddListener: OnClickProductAddListener? = null
    private var productMinusListener: OnClickProductMinusListener? = null
    private var wishAddClickListener: OnClickWishAddClickListener? = null


    private lateinit var sharedPreferences: PreferenceManager


    inner class MainViewHolder(val itemsBinding: CartRecyclerViewBinding) :
        RecyclerView.ViewHolder(itemsBinding.root) {

        fun bindItem(list: CartListData) {

            sharedPreferences = PreferenceManager(context)

            if (sharedPreferences.getMode() == true) {
                itemsBinding.background.setBackgroundResource(R.drawable.profile_round_corner_bg_dark_wish)
                itemsBinding.innerbackground.backgroundTintList =
                    ColorStateList.valueOf(context.resources.getColor(R.color.blackfordark))
                itemsBinding.foodItemName.setTextColor(context.resources.getColor(R.color.whitefordark))
                itemsBinding.tvRatingOffer.setTextColor(context.resources.getColor(R.color.whitefordark))
                itemsBinding.foodItemName.setTextColor(context.resources.getColor(R.color.whitefordark))
                itemsBinding.foodItemPrice.setTextColor(context.resources.getColor(R.color.whitefordark))
                itemsBinding.foodItemPrice.setTextColor(context.resources.getColor(R.color.whitefordark))
                itemsBinding.productreviews.setTextColor(context.resources.getColor(R.color.whitefordark))
                itemsBinding.wishlist.setTextColor(context.resources.getColor(R.color.blackfordark))
                itemsBinding.itemCount.setTextColor(context.resources.getColor(R.color.whitefordark))
                itemsBinding.itemProductWishlist.setImageResource(R.drawable.ic_wishlist_cart_dark)
                itemsBinding.minus.backgroundTintList =
                    ColorStateList.valueOf(Color.parseColor("#2C2C2C"))
                itemsBinding.add.backgroundTintList =
                    ColorStateList.valueOf(Color.parseColor("#2C2C2C"))
            }

            var vname: String = ""
            if (list.size != null) {
                vname = "- ${list.size}"
            }

            itemsBinding.foodItemName.text = "${list.productname} ${vname}"
            Glide.with(itemsBinding.foodItemImg)
                .load(list.product_url)
                .into(itemsBinding.foodItemImg)

            itemsBinding.foodItemPrice.text = "₹ " + list.dis_price.toString()
            itemsBinding.itemCount.text = list.quantity.toString()

            itemsBinding.productPrice.setPaintFlags(itemsBinding.productPrice.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)

            itemsBinding.rlDeleteItem.setOnClickListener {
                val pid = list.product_id
                val pPrice = list.orignal_price
                val discPrice = list.dis_price
                val variation = list.variation_id

                productDeleteListener?.onProductdeleteClick(
                    pid.toString(), pPrice.toString(), discPrice.toString(), variation.toString()
                )

                val animator = ValueAnimator.ofInt(300, 0)
                animator.addUpdateListener { valueAnimator ->
                    val animatedValue = valueAnimator.animatedValue as Int
                    val layoutParams = itemView.layoutParams
                    layoutParams.height = animatedValue
                    itemView.layoutParams = layoutParams
                }
                animator.duration = 1000
                animator.start()
            }

            itemsBinding.rlDeleteItem.setOnClickListener {
                val pid = list.product_id
                val pPrice = list.orignal_price
                val discPrice = list.dis_price
                val variation = list.variation_id

                productDeleteListener?.onProductdeleteClick(
                    pid.toString(),
                    pPrice.toString(),
                    discPrice.toString(),
                    variation.toString()
                )

                val animator = ValueAnimator.ofInt(300, 0)
                animator.addUpdateListener { valueAnimator ->
                    val animatedValue = valueAnimator.animatedValue as Int
                    val layoutParams = itemView.layoutParams
                    layoutParams.height = animatedValue
                    itemView.layoutParams = layoutParams
                }
                animator.duration = 1000
                animator.start()
            }
            if (list.prod_type == "Non-Veg") {
                itemsBinding.foodtype.setImageResource(R.drawable.non_veg_icon)
            } else if (list.prod_type == "Veg") {
                itemsBinding.foodtype.setImageResource(R.drawable.veg_icon)
            }

            itemsBinding.productreviews.text = "(${list.totalreview.toString()})"

            itemsBinding.cartSavePrice.text = "₹ ${list.Off_Price}"
            itemsBinding.productPrice.text = "₹ ${list.orignal_price}"


            val rating: String
            if (list.rating == null) {
                rating = "0.0"
            } else {
                rating = String.format("%.1f", list.rating)
            }
            itemsBinding.ratingBarIndicator.rating = rating.toFloat()
            itemsBinding.tvRatingOffer.text = rating

            itemsBinding.add.setOnClickListener {
                val pid = list.product_id
                val pPrice = list.orignal_price
                val discPrice = list.dis_price
                val variation = list.variation_id
                productAddListener?.onProductAddClick(
                    pid.toString(),
                    pPrice.toString(),
                    discPrice.toString(),
                    variation.toString(),

                    )
            }

            itemsBinding.rlMoveToWishlist.setOnClickListener {


                val pid = list.product_id
                wishAddClickListener?.onWishAddClick(pid.toString())


                val pidd = list.product_id
                val pPrice = list.orignal_price
                val discPrice = list.dis_price
                val variation = list.variation_id

                productDeleteListener?.onProductdeleteClick(
                    pidd.toString(),
                    pPrice.toString(),
                    discPrice.toString(),
                    variation.toString()
                )

                val animator = ValueAnimator.ofInt(300, 0)
                animator.addUpdateListener { valueAnimator ->
                    val animatedValue = valueAnimator.animatedValue as Int
                    val layoutParams = itemView.layoutParams
                    layoutParams.height = animatedValue
                    itemView.layoutParams = layoutParams
                }
                animator.duration = 1000
                animator.start()
            }

            itemsBinding.minus.setOnClickListener {
                val pid = list.product_id
                val pPrice = list.orignal_price.toString()
                val discPrice = list.dis_price
                val variation = list.variation_id

                if (list.quantity > 1) {
                    productMinusListener?.onProductMinusClick(
                        pid.toString(),
                        pPrice,
                        discPrice.toString(),
                        variation.toString()
                    )
                } else {
                    showPopUpDialog(pid, pPrice, discPrice, variation)
                }

            }

            itemView.setOnClickListener {
                val pId = list.product_id
                val vId = list.variation_id
                val quantity = list.quantity

                productClickListener?.onProductIdClick(
                    pId.toString(),
                    vId.toString(),
                    quantity.toString(),
                )
            }

        }

    }

    private var popUpDialog: Dialog? = null

    private fun showPopUpDialog(pid: Int, pPrice: String, discPrice: Int, variation: Int) {
        popUpDialog = Dialog(context, R.style.popup_dialog)
        if (sharedPreferences.getMode() == true) {
            popUpDialog?.setContentView(R.layout.popup_dialogbox_delete_from_cart_dark)
        } else {
            popUpDialog?.setContentView(R.layout.popup_dialogbox_delete_from_cart)
        }

//        val window: Window? = popUpDialog!!.getWindow()
//        if (window != null) {
//            val params = window.attributes
//            params.gravity = Gravity.BOTTOM
//            params.dimAmount = 0.2f
//            window.attributes = params
//        }

        popUpDialog!!.show()
//        Objects.requireNonNull<Window>(popUpDialog!!.getWindow())
//            .setBackgroundDrawableResource(R.drawable.border_popup_bg)


        val productNameText: TextView = popUpDialog!!.findViewById(R.id.productName)
        val laterBtn: TextView = popUpDialog!!.findViewById(R.id.doneCart)
        val updateNowBtn: TextView = popUpDialog!!.findViewById(R.id.checkoutCart)

//        productNameText.text = "${productDialogName}"

        laterBtn.setOnClickListener {
            popUpDialog!!.dismiss()
        }

        updateNowBtn.setOnClickListener {
//            findNavController().navigate(R.id.action_ProductDetailsFragment_to_bottom_menu_my_cart)

            productDeleteListener?.onProductdeleteClick(
                pid.toString(),
                pPrice.toString(),
                discPrice.toString(),
                variation.toString()
            )
            popUpDialog!!.dismiss()
        }

    }


    // Interface for item click listeners
    interface OnClickProductAddListener {
        fun onProductAddClick(pId: String, pPrice: String, toString: String, toString1: String)
    }

    interface OnClickProductMinusListener {
        fun onProductMinusClick(pId: String, pPrice: String, toString: String, variation: String)
    }

    interface OnClickDeleteListener {
        fun onProductdeleteClick(pId: String, pPrice: String, toString: String, variation: String)
    }

    interface OnClickProductListener {
        fun onProductIdClick(pId: String, vId: String, quantity: String)
    }

    interface OnClickWishAddClickListener {
        fun onWishAddClick(pId: String)

    }

    // Setters for listeners

    fun setOnClickWishlistAddListener(listener: OnClickWishAddClickListener) {
        wishAddClickListener = listener
    }

    fun setOnClickProductAddListener(listener: OnClickProductAddListener) {
        productAddListener = listener
    }

    fun setOnClickProductMinusListener(listener: OnClickProductMinusListener) {
        productMinusListener = listener
    }

    fun setOnClickProductDeleteListener(listener: OnClickDeleteListener) {
        productDeleteListener = listener
    }

    fun setOnClickProductViewListener(listener: OnClickProductListener) {
        productClickListener = listener
    }

    // Update dataset
//    fun updateItems(newItems: List<CartListData>) {
//        items.clear()
//        items.addAll(newItems)
//        notifyDataSetChanged()
//    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        return MainViewHolder(
            CartRecyclerViewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val item = items[position]
        holder.bindItem(item)
    }


    override fun getItemCount(): Int {
        return items.size
    }


}

