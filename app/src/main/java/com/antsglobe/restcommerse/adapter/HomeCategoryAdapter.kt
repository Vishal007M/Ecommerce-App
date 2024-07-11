package com.antsglobe.aeroquiz

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.antsglobe.restcommerse.R
import com.antsglobe.restcommerse.Utils.PreferenceManager
import com.antsglobe.restcommerse.databinding.HomeCategoriesRecyclerViewBinding
import com.antsglobe.restcommerse.model.Response.HomeCategoryData
import com.squareup.picasso.Picasso

class HomeCategoryAdapter(val items: List<HomeCategoryData>, val context: Context) :
    RecyclerView.Adapter<HomeCategoryAdapter.MainViewHolder>() {

    private var categoryClickListener: OnClickCategoryListener? = null

    private lateinit var sharedpreferences: PreferenceManager

    inner class MainViewHolder(val itemsBinding: HomeCategoriesRecyclerViewBinding) :
        RecyclerView.ViewHolder(itemsBinding.root) {

        fun bindItem(list: HomeCategoryData) {

            var imageUrl = list.img_url
            Picasso.get().load(imageUrl).into(itemsBinding.catImage)

            sharedpreferences = PreferenceManager(context)

            if (sharedpreferences.getMode() == true) {
                itemsBinding.background.setBackgroundResource(R.drawable.profile_round_corner_bg_dark)
                itemsBinding.innerbackground.setBackgroundResource(R.drawable.profile_round_corner_bg_dark_wish)
                itemsBinding.catName.setTextColor(context.resources.getColor(R.color.whitefordark))
            } else {
//                itemsBinding.background.setBackgroundResource(R.drawable.profile_round_corner_bg)
                itemsBinding.innerbackground.setBackgroundResource(R.drawable.round_corner_bg)
                itemsBinding.catName.setTextColor(context.resources.getColor(R.color.blackfordark))
            }
            itemsBinding.catName.text = list.catname.toString()

            itemView.setOnClickListener {
                val categoryId = list.cid.toString()
                val categoryName = list.catname.toString()
                Log.e("TAG", "categoryId: $categoryId")
                categoryClickListener?.onCategoryIdClick(categoryId!!.toString(), categoryName)

            }
        }
    }

    interface OnClickCategoryListener {
        fun onCategoryIdClick(categoryId: String, categoryName: String)
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
            HomeCategoriesRecyclerViewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )

        )
    }

    override fun getItemCount(): Int {
        return items.size
    }


}