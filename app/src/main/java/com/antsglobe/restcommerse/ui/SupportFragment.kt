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
import com.antsglobe.restcommerse.databinding.FragmentSupportBinding


class SupportFragment : Fragment() {
    private var binding: FragmentSupportBinding? = null

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
        // return inflater.inflate(R.layout.fragment_support, container, false)
        binding = FragmentSupportBinding.inflate(inflater, container, false)

        binding?.backButton?.setOnClickListener {
            findNavController().popBackStack()
        }

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreferences = PreferenceManager(requireContext())
        if (sharedPreferences.getMode() == true) {
            binding!!.backButton.setImageResource(R.drawable.ic_baseline_arrow_back_24_dark)
            binding!!.logoImageHome.setTextColor(Color.BLACK)
            binding!!.coming.setTextColor(Color.WHITE)
            binding!!.fullscreen.setBackgroundColor(Color.BLACK)
        }
    }


}