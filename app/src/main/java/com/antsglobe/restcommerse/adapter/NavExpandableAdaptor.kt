package com.antsglobe.restcommerse.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ImageView
import android.widget.TextView
import com.antsglobe.restcommerse.R
import com.antsglobe.restcommerse.Utils.PreferenceManager

class NavExpandableAdaptor(
    private val context: Context,
    private val listDataHeader: List<String>, // header titles
    private val listDataChild: HashMap<String, List<String>> // child data in format of header title, child title
) : BaseExpandableListAdapter() {

    private lateinit var sharedPreferences: PreferenceManager

    override fun getGroupCount(): Int {
        return listDataHeader.size
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return listDataChild[listDataHeader[groupPosition]]?.size ?: 0
    }

    override fun getGroup(groupPosition: Int): Any {
        return listDataHeader[groupPosition]
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        return listDataChild[listDataHeader[groupPosition]]!![childPosition]
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        var convertView = convertView
        sharedPreferences = PreferenceManager(context)
        val headerTitle = getGroup(groupPosition) as String
        if (convertView == null) {
            val inflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = inflater.inflate(R.layout.expandable_list_group, null)
        }
        val lblListHeader = convertView!!.findViewById<TextView>(R.id.tv_expandable_group)
        lblListHeader.text = headerTitle

        if (sharedPreferences.getMode() == true) {
            lblListHeader.setTextColor(context.resources.getColor(R.color.whitefordark))
        } else {
            lblListHeader.setTextColor(context.resources.getColor(R.color.blackfordark))
        }


        if (groupPosition == 0) {
            val groupImageView = convertView.findViewById<ImageView>(R.id.iv_header_image)
            groupImageView.visibility = View.VISIBLE
            if (isExpanded) {
                if (sharedPreferences.getMode() == true) {

                    groupImageView.setImageResource(R.drawable.minus_icon_white)
                } else {
                    groupImageView.setImageResource(R.drawable.minus_icon)
                }

            } else {
                if (sharedPreferences.getMode() == true) {

                    groupImageView.setImageResource(R.drawable.plus_icon_white)
                } else {
                    groupImageView.setImageResource(R.drawable.plus_icon)
                }

            }
        } else {
            // Hide the image for other group positions
            convertView.findViewById<ImageView>(R.id.iv_header_image).visibility = View.GONE
        }


        return convertView
    }

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        var convertView = convertView
        val childText = getChild(groupPosition, childPosition) as String
        if (convertView == null) {
            val inflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = inflater.inflate(R.layout.expandable_list_view, null)
        }
        val txtListChild = convertView!!.findViewById<TextView>(R.id.tv_expandable_item)
        txtListChild.text = childText

        return convertView
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }
}