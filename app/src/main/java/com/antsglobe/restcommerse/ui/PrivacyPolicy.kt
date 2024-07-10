package com.antsglobe.restcommerse.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.antsglobe.restcommerse.R
import com.antsglobe.restcommerse.Utils.PreferenceManager
import com.antsglobe.restcommerse.databinding.FragmentPrivacyPolicyBinding


class PrivacyPolicy : Fragment() {

    private lateinit var binding: FragmentPrivacyPolicyBinding

    private lateinit var sharedPreferences: PreferenceManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentPrivacyPolicyBinding.inflate(inflater, container, false)

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
            binding.PrivacyPolicy.setBackgroundColor(resources.getColor(R.color.black))
            binding.abouttext.setTextColor(resources.getColor(R.color.dark_grey))
            binding.abouttext.setText(R.string.PrivacyPolicydark)
        }
    }


}