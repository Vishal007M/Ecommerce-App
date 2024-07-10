package com.antsglobe.restcommerse.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.antsglobe.restcommerse.Utils.PreferenceManager
import com.antsglobe.restcommerse.databinding.RecentSearchListRecyclerViewBinding
import com.antsglobe.restcommerse.model.Response.RecentSearchList

class RecentSearchAdapter(val items: List<RecentSearchList>, val context: Context) :
    RecyclerView.Adapter<RecentSearchAdapter.MainViewHolder>() {

    private var productClickListener: OnClickRecentProductListener? = null
    private lateinit var sharedPreferences: PreferenceManager

    inner class MainViewHolder(val itemsBinding: RecentSearchListRecyclerViewBinding) :
        RecyclerView.ViewHolder(itemsBinding.root) {

        fun bindItem(list: RecentSearchList) {

            sharedPreferences = PreferenceManager(context)
            if (sharedPreferences.getMode() == true) {
                itemsBinding.background.setBackgroundColor(Color.parseColor("#1F201D"))
                itemsBinding.searchName.setTextColor(Color.GRAY)
            }
            itemsBinding.searchName.text = list.product_name

            itemView.setOnClickListener {
                val pName = list.product_name
                productClickListener?.onRecentProductIdClick(
                    pName.toString()
                )
            }

        }

    }

    interface OnClickRecentProductListener {
        fun onRecentProductIdClick(
            pName: String
        )

    }

    fun setOnRecentClickProductListener(listener: OnClickRecentProductListener) {
        productClickListener = listener
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val item = items[position]
        holder.bindItem(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        return MainViewHolder(
            RecentSearchListRecyclerViewBinding.inflate(
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