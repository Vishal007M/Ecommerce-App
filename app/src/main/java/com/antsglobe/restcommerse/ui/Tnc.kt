package com.antsglobe.restcommerse.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.antsglobe.restcommerse.R
import com.antsglobe.restcommerse.Utils.PreferenceManager
import com.antsglobe.restcommerse.databinding.FragmentTncBinding


class Tnc : Fragment() {

    private lateinit var binding: FragmentTncBinding


    private lateinit var backbutton: ImageView

    private lateinit var sharedPreferences: PreferenceManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentTncBinding.inflate(inflater, container, false)

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        sharedPreferences = PreferenceManager(requireContext())

        if (sharedPreferences.getMode() == true) {
            binding.logoImageHome.setTextColor(resources.getColor(R.color.blackfordark))
            binding.backButton.setImageResource(R.drawable.ic_baseline_arrow_back_24_dark)
            binding.Tnc.setBackgroundColor(resources.getColor(R.color.black))
            binding.t1.setTextColor(resources.getColor(R.color.dark_grey))
            binding.t2.setTextColor(resources.getColor(R.color.dark_grey))
            binding.t3.setTextColor(resources.getColor(R.color.dark_grey))
            binding.t4.setTextColor(resources.getColor(R.color.dark_grey))
            binding.t5.setTextColor(resources.getColor(R.color.dark_grey))
            binding.t6.setTextColor(resources.getColor(R.color.dark_grey))
            binding.t7.setTextColor(resources.getColor(R.color.dark_grey))
            binding.t8.setTextColor(resources.getColor(R.color.dark_grey))
            binding.t9.setTextColor(resources.getColor(R.color.dark_grey))
            binding.t10.setTextColor(resources.getColor(R.color.dark_grey))
            binding.t11.setTextColor(resources.getColor(R.color.dark_grey))
            binding.t13.setTextColor(resources.getColor(R.color.dark_grey))
            binding.t14.setTextColor(resources.getColor(R.color.dark_grey))
            binding.t1.setText(R.string.Tnc1dark)
            binding.t2.setText(R.string.Tnc2dark)
            binding.t3.setText(R.string.Tnc3dark)
            binding.t4.setText(R.string.Tnc4dark)
            binding.t5.setText(R.string.Tnc5dark)
            binding.t6.setText(R.string.Tnc6dark)
            binding.t7.setText(R.string.Tnc7dark)
            binding.t8.setText(R.string.Tnc8dark)
            binding.t9.setText(R.string.Tnc9dark)
            binding.t10.setText(R.string.Tnc10dark)
            binding.t11.setText(R.string.Tnc11dark)
            binding.t13.setText(R.string.Tnc12dark)
            binding.t14.setText(R.string.Tnc14dark)
        }

    }


}