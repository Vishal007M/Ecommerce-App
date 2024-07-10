package com.antsglobe.restcommerse.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.antsglobe.restcommerse.R
import com.antsglobe.restcommerse.Utils.PreferenceManager
import com.antsglobe.restcommerse.databinding.FragmentAboutUsBinding


class AboutUs : Fragment() {

    private lateinit var binding: FragmentAboutUsBinding


    private lateinit var abouttext: TextView
    private lateinit var aboutusbutton: TextView
    private lateinit var sharedPreferences: PreferenceManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentAboutUsBinding.inflate(inflater, container, false)

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        abouttext = binding.abouttext

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        sharedPreferences = PreferenceManager(requireContext())

        if (sharedPreferences.getMode() == true) {
            binding.Aboutus.setBackgroundColor(resources.getColor(R.color.blackfordark))
            binding.logoImageHome.setTextColor(resources.getColor(R.color.blackfordark))
            binding.backButton.setImageResource(R.drawable.ic_baseline_arrow_back_24_dark)
            binding.abouttext.setTextColor(resources.getColor(R.color.whitefordark))
            binding.abouttext2.setTextColor(resources.getColor(R.color.whitefordark))
        }


    }


}