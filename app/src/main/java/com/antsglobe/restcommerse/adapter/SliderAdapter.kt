package com.antsglobe.restcommerse.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.antsglobe.restcommerse.R
import com.antsglobe.restcommerse.Utils.PreferenceManager
import com.antsglobe.restcommerse.model.SliderData

class SliderAdapter(
    val context: Context,
    val sliderList: ArrayList<SliderData>,
) : PagerAdapter() {

    private lateinit var sharedPreferences: PreferenceManager
    override fun getCount(): Int {
        // on below line we are returning
        // the size of slider list
        return sliderList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        // inside isViewFromobject method we are
        // returning our Relative layout object.
        // inside isViewFromobject method we are
        // returning our Relative layout object.
        return view === `object` as RelativeLayout
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        // in this method we will initialize all our layout
        // items and inflate our layout file as well.
        // in this method we will initialize all our layout
        // items and inflate our layout file as well.
        val layoutInflater: LayoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        // below line is use to inflate the
        // layout file which we created.

        // below line is use to inflate the
        // layout file which we created.
        val view: View = layoutInflater.inflate(R.layout.intro_screen_viewer, container, false)

        sharedPreferences = PreferenceManager(context)

        // on below line we are initializing our image view,
        // heading text view and description text view with their ids.
        val imageView: ImageView = view.findViewById(R.id.idIVSlider)
        val background: RelativeLayout = view.findViewById(R.id.idRLSlider)
        val fullscreen: LinearLayout = view.findViewById(R.id.halfscreen)
        val sliderHeadingTV: TextView = view.findViewById(R.id.idTVSliderTitle)
        val sliderDescTV: TextView = view.findViewById(R.id.idTVSliderDescription)

        if (sharedPreferences.getMode() == true) {
            sliderHeadingTV.setTextColor(context.resources.getColor(R.color.whitefordark))
            sliderDescTV.setTextColor(context.resources.getColor(R.color.dark_grey))
            background.setBackgroundColor(context.resources.getColor(R.color.blackfordark))
            imageView.setBackgroundColor(context.resources.getColor(R.color.blackfordark))
            fullscreen.setBackgroundColor(context.resources.getColor(R.color.blackfordark))
        }
        // on below line we are setting data to our text view
        // and image view on below line.
        val sliderData: SliderData = sliderList.get(position)
        sliderHeadingTV.text = sliderData.slideTitle
        sliderDescTV.text = sliderData.slideDescription
        imageView.setImageResource(sliderData.slideImage)

        // on below line we are adding our view to container.
        container.addView(view)

        // on below line we are returning our view.
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        // this is a destroy view method
        // which is use to remove a view.
        // this is a destroy view method
        // which is use to remove a view.
        container.removeView(`object` as RelativeLayout)
    }

}
