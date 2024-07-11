package com.antsglobe.restcommerse.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.antsglobe.restcommerse.R
import com.antsglobe.restcommerse.databinding.VariantRecyclerViewBinding
import com.antsglobe.restcommerse.model.Response.ProductVariationData


class VariantsAdapter(
    val list: List<ProductVariationData>,
    val default: Int,
    val variantId: String?
) :
    RecyclerView.Adapter<VariantsAdapter.MainViewHolder>() {


    private var selectedPosition: Int = -1
    private var defaultselected = -1
    private var variantclicklistner: OnVariantClickListner? = null
    var clickableVariation = true

    inner class MainViewHolder(val itemsbinding: VariantRecyclerViewBinding) :
        RecyclerView.ViewHolder(itemsbinding.root) {
        fun binditem(item: ProductVariationData, position: Int) {
            itemsbinding.quantity.text = item.size
            itemsbinding.price.text = "₹" + item.discount_price.toInt().toString()

            if (defaultselected != -1) {
                if (position == selectedPosition) {
                    itemsbinding.variantlayout.background = ContextCompat.getDrawable(
                        itemView.context,
                        R.drawable.round_corner_border2_selected
                    )
                } else {
                    itemsbinding.variantlayout.background = ContextCompat.getDrawable(
                        itemView.context,
                        R.drawable.round_corner_border2_varitent
                    )
                }
            }


            Log.e("TAG", " variantId select : $variantId ")


            if (!variantId.isNullOrEmpty()) {
                clickableVariation = false

                if (item.Variation_id == variantId?.toInt()) {
                    itemsbinding.variantlayout.background = ContextCompat.getDrawable(
                        itemView.context,
                        R.drawable.round_corner_border2_selected
                    )
                    defaultselected = adapterPosition
                    Log.e("TAG", "select : $variantId ")
                    variantclicklistner?.onvariantclick(
                        item.Variation_id.toInt(),
                        item.product_price.toInt(),
                        item.discount_price.toInt(),
                        item.size.toString()

                    )
                }

            } else {
                if ((item.discount_price.toInt() == default) && defaultselected == -1) {
                    clickableVariation = true
                    itemsbinding.variantlayout.background = ContextCompat.getDrawable(
                        itemView.context,
                        R.drawable.round_corner_border2_selected
                    )
                    defaultselected = adapterPosition
                    variantclicklistner?.onvariantclick(
                        item.Variation_id.toInt(),
                        item.product_price.toInt(),
                        item.discount_price.toInt(),
                        item.size.toString()
                    )
                }
            }


            /*
                        if ((item.discount_price.toInt() == default) && defaultselected == -1) {
                            itemsbinding.variantlayout.background = ContextCompat.getDrawable(
                                itemView.context,
                                R.drawable.round_corner_border2_selected
                            )
                            defaultselected = adapterPosition
                            variantclicklistner?.onvariantclick(
                                item.Variation_id.toInt(),
                                item.product_price.toInt(),
                                item.discount_price.toInt(),
                                item.size.toString()
                            )
                        }*/

            if (clickableVariation) {
                itemsbinding.variantlayout.setOnClickListener {
                    selectedPosition = adapterPosition
                    notifyDataSetChanged()
                    variantclicklistner?.onvariantclick(
                        item.Variation_id.toInt(),
                        item.product_price.toInt(),
                        item.discount_price.toInt(),
                        item.size.toString()
                    )
                    if (adapterPosition == defaultselected) {
                        itemsbinding.variantlayout.background = ContextCompat.getDrawable(
                            itemView.context,
                            R.drawable.round_corner_border2_varitent
                        )
                    }
                }
            } else {
                itemsbinding.variantlayout.setOnClickListener {
                    itemsbinding.variantlayout.isClickable = false
                }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        return MainViewHolder(
            VariantRecyclerViewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val item = list[position]
        holder.binditem(item, position)
    }

    interface OnVariantClickListner {
        fun onvariantclick(vid: Int, price: Int, dprice: Int, itemSize: String)
    }

    fun setonvariantclick(listner: OnVariantClickListner) {
        variantclicklistner = listner
    }
}