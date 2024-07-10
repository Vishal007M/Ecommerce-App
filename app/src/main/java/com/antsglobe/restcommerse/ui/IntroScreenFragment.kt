package com.antsglobe.restcommerse.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import com.antsglobe.restcommerse.R
import com.antsglobe.restcommerse.Utils.PreferenceManager
import com.antsglobe.restcommerse.adapter.SliderAdapter
import com.antsglobe.restcommerse.databinding.FragmentIntroScreenBinding
import com.antsglobe.restcommerse.model.SliderData


class IntroScreenFragment : Fragment() {

    private var binding: FragmentIntroScreenBinding? = null
    lateinit var viewPager: ViewPager
    lateinit var sliderAdapter: SliderAdapter
    lateinit var sliderList: ArrayList<SliderData>
    lateinit var skipBtn: Button
    lateinit var indicatorSlideOneTV: TextView
    lateinit var indicatorSlideTwoTV: TextView
    lateinit var indicatorSlideThreeTV: TextView
    private lateinit var sharedPreferences: PreferenceManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            //  param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //return inflater.inflate(R.layout.fragment_intro_screen, container, false)
        binding = FragmentIntroScreenBinding.inflate(inflater, container, false)

        // on below line we are initializing all
        // our variables with their ids.
        viewPager = binding!!.idViewPager
        skipBtn = binding!!.idBtnSkip
        indicatorSlideOneTV = binding!!.idTVSlideOne
        indicatorSlideTwoTV = binding!!.idTVSlideTwo
        indicatorSlideThreeTV = binding!!.idTVSlideThree

        sharedPreferences = PreferenceManager(requireContext())


        if (sharedPreferences.getMode() == true) {
            binding!!.skip.setTextColor(resources.getColor(R.color.whitefordark))
            binding!!.idBtnSkip.setTextColor(resources.getColor(R.color.blackfordark))
            binding!!.container.setBackgroundColor(resources.getColor(R.color.blackfordark))
            binding!!.llnext.setBackgroundColor(resources.getColor(R.color.blackfordark))
            binding!!.idViewPager.setBackgroundColor(resources.getColor(R.color.blackfordark))
        }
        binding!!.skip.setOnClickListener {
            findNavController().navigate(R.id.action_IntroScreenFragment_to_LoginFragment)
        }

        skipBtn.setOnClickListener {
            val current = viewPager.currentItem + 1
            if (current < sliderAdapter.getCount()) {
                // move to next screen
                viewPager.currentItem = current
            } else {
                findNavController().navigate(R.id.action_IntroScreenFragment_to_LoginFragment)
            }
        }

        // on below line we are initializing our slider list.
        sliderList = ArrayList()

        var img1 = R.drawable.sliderimg1
        var img2 = R.drawable.sliderimg2
        var img3 = R.drawable.sliderimg3
        if (sharedPreferences.getMode() == true) {
            indicatorSlideTwoTV.setTextColor(resources.getColor(R.color.dark_grey))
            indicatorSlideThreeTV.setTextColor(resources.getColor(R.color.dark_grey))
            indicatorSlideOneTV.setTextColor(resources.getColor(R.color.orange))
            img1 = R.drawable.sliderimg1dark
            img2 = R.drawable.sliderimg2dark
            img3 = R.drawable.sliderimg3dark
        }
        // on below line we are adding data to our list
        sliderList.add(
            SliderData(
                "Find the item you’ve been looking for",
                "Here you’ll see rich varieties of goods, carefully classified for seamless browsing experience.",
                img1
            )
        )

        sliderList.add(
            SliderData(
                "Get those shopping bags filled",
                "Add any item you want to your cart, or save it on your wish list for future purchasing.",
                img2
            )
        )

        sliderList.add(
            SliderData(
                "Fast and secure payment options",
                "There are many payment options available for your ease.",
                img3
            )
        )

        // on below line we are adding slider list
        // to our adapter class.
        sliderAdapter = SliderAdapter(requireContext(), sliderList)
        viewPager.adapter = sliderAdapter
        viewPager.addOnPageChangeListener(viewListener)


        return binding?.root
    }

    var viewListener: ViewPager.OnPageChangeListener = object : ViewPager.OnPageChangeListener {
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
        }

        override fun onPageSelected(position: Int) {
            // we are calling our dots method to
            // change the position of selected dots.

            // on below line we are checking position and updating text view text color.
            if (sharedPreferences.getMode() == true) {
                if (position == 0) {
                    indicatorSlideTwoTV.setTextColor(resources.getColor(R.color.dark_grey))
                    indicatorSlideThreeTV.setTextColor(resources.getColor(R.color.dark_grey))
                    indicatorSlideOneTV.setTextColor(resources.getColor(R.color.orange))

                } else if (position == 1) {
                    indicatorSlideTwoTV.setTextColor(resources.getColor(R.color.orange))
                    indicatorSlideThreeTV.setTextColor(resources.getColor(R.color.dark_grey))
                    indicatorSlideOneTV.setTextColor(resources.getColor(R.color.dark_grey))
                } else {
                    indicatorSlideTwoTV.setTextColor(resources.getColor(R.color.dark_grey))
                    indicatorSlideThreeTV.setTextColor(resources.getColor(R.color.orange))
                    indicatorSlideOneTV.setTextColor(resources.getColor(R.color.dark_grey))
                }
            } else {
                if (position == 0) {
                    indicatorSlideTwoTV.setTextColor(resources.getColor(R.color.black))
                    indicatorSlideThreeTV.setTextColor(resources.getColor(R.color.black))
                    indicatorSlideOneTV.setTextColor(resources.getColor(R.color.orange))

                } else if (position == 1) {
                    indicatorSlideTwoTV.setTextColor(resources.getColor(R.color.orange))
                    indicatorSlideThreeTV.setTextColor(resources.getColor(R.color.black))
                    indicatorSlideOneTV.setTextColor(resources.getColor(R.color.black))
                } else {
                    indicatorSlideTwoTV.setTextColor(resources.getColor(R.color.black))
                    indicatorSlideThreeTV.setTextColor(resources.getColor(R.color.orange))
                    indicatorSlideOneTV.setTextColor(resources.getColor(R.color.black))
                }
            }

        }

        // below method is use to check scroll state.
        override fun onPageScrollStateChanged(state: Int) {}
    }

}