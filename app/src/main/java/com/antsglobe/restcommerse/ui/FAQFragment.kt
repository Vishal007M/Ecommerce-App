package com.antsglobe.restcommerse.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.antsglobe.restcommerse.R
import com.antsglobe.restcommerse.Utils.PreferenceManager
import com.antsglobe.restcommerse.databinding.FragmentFAQBinding

class FAQFragment : Fragment() {
    private var binding: FragmentFAQBinding? = null


    private lateinit var sharedPreferences:PreferenceManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            // param1 = it.getString(ARG_PARAM1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //  return inflater.inflate(R.layout.fragment_f_a_q, container, false)
        binding = FragmentFAQBinding.inflate(inflater, container, false)

        binding?.backButton?.setOnClickListener {
            findNavController().popBackStack()
        }

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreferences= PreferenceManager(requireContext())
        if (sharedPreferences.getMode() == true){
            binding!!.logoImageHome.setTextColor(Color.BLACK)
            binding!!.tv1.setTextColor(Color.WHITE)
            binding!!.tv2.setTextColor(Color.WHITE)
            binding!!.tv3.setTextColor(Color.WHITE)
            binding!!.tv4.setTextColor(Color.WHITE)
            binding!!.tv5.setTextColor(Color.WHITE)
            binding!!.tv6.setTextColor(Color.WHITE)
            binding!!.tv7.setTextColor(Color.WHITE)
            binding!!.tv8.setTextColor(Color.WHITE)
            binding!!.backButton.setImageResource(R.drawable.ic_baseline_arrow_back_24_dark)
            binding!!.fullscreen.setBackgroundColor(Color.BLACK)
            binding!!.rl1.setBackgroundResource(R.drawable.profile_round_corner_bg_dark_wish)
            binding!!.rl2.setBackgroundResource(R.drawable.profile_round_corner_bg_dark_wish)
            binding!!.rl3.setBackgroundResource(R.drawable.profile_round_corner_bg_dark_wish)
            binding!!.rl4.setBackgroundResource(R.drawable.profile_round_corner_bg_dark_wish)
            binding!!.rl5.setBackgroundResource(R.drawable.profile_round_corner_bg_dark_wish)
            binding!!.rl6.setBackgroundResource(R.drawable.profile_round_corner_bg_dark_wish)
            binding!!.rl7.setBackgroundResource(R.drawable.profile_round_corner_bg_dark_wish)
            binding!!.rl8.setBackgroundResource(R.drawable.profile_round_corner_bg_dark_wish)
        }
    }

}