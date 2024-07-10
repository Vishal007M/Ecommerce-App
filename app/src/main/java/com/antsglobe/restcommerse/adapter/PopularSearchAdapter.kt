package com.antsglobe.restcommerse.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.antsglobe.restcommerse.Utils.PreferenceManager
import com.antsglobe.restcommerse.databinding.RecentSearchListRecyclerViewBinding
import com.antsglobe.restcommerse.model.Response.PopularSearchList

class PopularSearchAdapter(val items: List<PopularSearchList>, val context: Context) :
    RecyclerView.Adapter<PopularSearchAdapter.MainViewHolder>() {

    private var productClickListener: OnClickPopularProductListener? = null
    private lateinit var sharedPreferences: PreferenceManager

    inner class MainViewHolder(val itemsBinding: RecentSearchListRecyclerViewBinding) :
        RecyclerView.ViewHolder(itemsBinding.root) {

        fun bindItem(list: PopularSearchList) {

            sharedPreferences = PreferenceManager(context)
            if (sharedPreferences.getMode() == true) {
                itemsBinding.background.setBackgroundColor(Color.parseColor("#1F201D"))
                itemsBinding.searchName.setTextColor(Color.GRAY)
            }
            itemsBinding.searchName.text = list.catname

            itemView.setOnClickListener {
                val pName = list.catname
                productClickListener?.onPopularProductIdClick(
                    pName.toString()
                )
            }

        }

    }

    interface OnClickPopularProductListener {
        fun onPopularProductIdClick(
            pName: String
        )

    }

    fun setOnPopularClickProductListener(listener: OnClickPopularProductListener) {
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