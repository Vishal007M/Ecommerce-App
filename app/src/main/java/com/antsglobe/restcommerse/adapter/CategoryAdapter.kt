package com.antsglobe.restcommerse.adapter

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.antsglobe.restcommerse.R
import com.antsglobe.restcommerse.Utils.PreferenceManager
import com.antsglobe.restcommerse.databinding.CategoriesRecyclerViewBinding
import com.antsglobe.restcommerse.model.Response.CategoriesList
import com.bumptech.glide.Glide

class CategoryAdapter(val items: List<CategoriesList>, val context: Context) :
    RecyclerView.Adapter<CategoryAdapter.MainViewHolder>() {

    private var categoryClickListener: OnClickCategoryListener? = null

    private lateinit var sharedPreferences: PreferenceManager

    inner class MainViewHolder(val itemsBinding: CategoriesRecyclerViewBinding) :
        RecyclerView.ViewHolder(itemsBinding.root) {
        fun bindItem(list: CategoriesList) {
            itemsBinding.TvCategoryItem.text = list.catname

            sharedPreferences = PreferenceManager(context)
            if (sharedPreferences.getMode() == true) {
                itemsBinding.background.setBackgroundResource(R.drawable.background_circle_dark)
                itemsBinding.TvCategoryItem.setTextColor(Color.WHITE)
            }
            Glide.with(itemsBinding.ivCategoryImage)
                .load(list.img_url)
                .placeholder(R.drawable.default_placeholder_greyed)
                .into(itemsBinding.ivCategoryImage)

            itemView.setOnClickListener {
                val categoryId = list.cid.toString()
                val categoryName = list.catname.toString()
                Log.e("TAG", "categoryId: $categoryId")
                categoryClickListener?.onCategoryIdClick(categoryId!!.toString(), categoryName )
            }
        }

    }

    interface OnClickCategoryListener {
        fun onCategoryIdClick(categoryId: String, catergroyNAme: String)
    }

    fun setOnClickCategoryListener(listener: OnClickCategoryListener) {
        categoryClickListener = listener
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val item = items[position]
        holder.bindItem(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        return MainViewHolder(
            CategoriesRecyclerViewBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )


    }

    override fun getItemCount(): Int {
        return items.size
    }


}