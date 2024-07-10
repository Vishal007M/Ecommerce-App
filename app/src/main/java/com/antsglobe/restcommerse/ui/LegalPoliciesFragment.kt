package com.antsglobe.restcommerse.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.antsglobe.restcommerse.R
import com.antsglobe.restcommerse.Utils.PreferenceManager
import com.antsglobe.restcommerse.databinding.FragmentLegalPoliciesBinding


class LegalPoliciesFragment : Fragment() {

    private lateinit var binding: FragmentLegalPoliciesBinding

    private lateinit var tncbutton: CardView

    private lateinit var sharedPreferences: PreferenceManager
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLegalPoliciesBinding.inflate(inflater, container, false)

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tncbutton = binding.Tncbutton

        binding.a1.rotation = 180f
        binding.a2.rotation = 180f
        binding.a3.rotation = 180f
        binding.a4.rotation = 180f
        sharedPreferences = PreferenceManager(requireContext())

        if (sharedPreferences.getMode() == true) {
            binding.backButton.setImageResource(R.drawable.ic_baseline_arrow_back_24_dark)
            binding.a1.setImageResource(R.drawable.ic_baseline_arrow_back_24_dark)
            binding.a2.setImageResource(R.drawable.ic_baseline_arrow_back_24_dark)
            binding.a3.setImageResource(R.drawable.ic_baseline_arrow_back_24_dark)
            binding.a4.setImageResource(R.drawable.ic_baseline_arrow_back_24_dark)
            binding.layout1.setBackgroundResource(R.drawable.profile_round_corner_bg_dark_wish)
            binding.layout2.setBackgroundResource(R.drawable.profile_round_corner_bg_dark_wish)
            binding.layout3.setBackgroundResource(R.drawable.profile_round_corner_bg_dark_wish)
            binding.layout4.setBackgroundResource(R.drawable.profile_round_corner_bg_dark_wish)
            binding.h1.setTextColor(resources.getColor(R.color.whitefordark))
            binding.h2.setTextColor(resources.getColor(R.color.whitefordark))
            binding.h3.setTextColor(resources.getColor(R.color.whitefordark))
            binding.h4.setTextColor(resources.getColor(R.color.whitefordark))
            binding.d1.setTextColor(resources.getColor(R.color.dark_grey))
            binding.d2.setTextColor(resources.getColor(R.color.dark_grey))
            binding.d3.setTextColor(resources.getColor(R.color.dark_grey))
            binding.d4.setTextColor(resources.getColor(R.color.dark_grey))
            binding.logoImageHome.setTextColor(resources.getColor(R.color.blackfordark))
            binding.fullscreen.setBackgroundColor(resources.getColor(R.color.blackfordark))


        }


        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        tncbutton.setOnClickListener {
            findNavController().navigate(R.id.action_legal_policies_to_Tnc)
        }
        binding.privacybutton.setOnClickListener {
            findNavController().navigate(R.id.action_legal_policies_to_PrivacyPolicy)
        }
        binding.refundbutton.setOnClickListener {
            findNavController().navigate(R.id.action_legal_policies_to_RefundPolicy)
        }
        binding.shippingbutton.setOnClickListener {
            findNavController().navigate(R.id.action_legal_policies_to_ShippingPolicy)
        }


    }

}