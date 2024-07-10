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
import com.antsglobe.restcommerse.databinding.FragmentSettingBinding


class SettingFragment : Fragment() {

    private var binding: FragmentSettingBinding? = null
    private lateinit var sharedPreferences: PreferenceManager


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingBinding.inflate(inflater, container, false)
        sharedPreferences = PreferenceManager(requireContext())

        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding!!.scDarkMode.isChecked = sharedPreferences.getMode() == true
        if (sharedPreferences.getMode() == true) {
            binding!!.logoImageHome.setTextColor(resources.getColor(R.color.blackfordark))
            binding!!.backButton.setImageResource(R.drawable.ic_baseline_arrow_back_24_dark)
            binding!!.fullscreen.setBackgroundColor(Color.BLACK)
            binding!!.darkmode.setTextColor(Color.WHITE)
            binding!!.selectlang.setTextColor(Color.WHITE)
            binding!!.polici.setTextColor(Color.WHITE)
            binding!!.about.setTextColor(Color.WHITE)
            binding!!.ivChangePin.setColorFilter(Color.WHITE)
            binding!!.ivChangePin2.setColorFilter(Color.WHITE)
            binding!!.tvLanguage.setTextColor(Color.WHITE)
        }
        binding!!.scDarkMode.setOnCheckedChangeListener { buttonView, isChecked ->


            if (isChecked) {

                binding!!.logoImageHome.setTextColor(resources.getColor(R.color.blackfordark))
                binding!!.backButton.setImageResource(R.drawable.ic_baseline_arrow_back_24_dark)
                binding!!.logoImageHome.setTextColor(resources.getColor(R.color.blackfordark))
                binding!!.backButton.setImageResource(R.drawable.ic_baseline_arrow_back_24_dark)
                binding!!.fullscreen.setBackgroundColor(Color.BLACK)
                binding!!.darkmode.setTextColor(Color.WHITE)
                binding!!.selectlang.setTextColor(Color.WHITE)
                binding!!.polici.setTextColor(Color.WHITE)
                binding!!.about.setTextColor(Color.WHITE)
                binding!!.ivChangePin.setColorFilter(Color.WHITE)
                binding!!.ivChangePin2.setColorFilter(Color.WHITE)
                binding!!.tvLanguage.setTextColor(Color.WHITE)

                //  AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

                // Toast.makeText(context,"${sharedPreferences.getMode()}" , Toast.LENGTH_SHORT).show()
                sharedPreferences.setMode(true)
                //  requireContext().setTheme(R.style.DARK_SCREEN )

            } else {
                binding!!.logoImageHome.setTextColor(resources.getColor(R.color.whitefordark))
                binding!!.backButton.setImageResource(R.drawable.ic_baseline_arrow_back_24)
                binding!!.fullscreen.setBackgroundColor(Color.WHITE)
                binding!!.darkmode.setTextColor(Color.BLACK)
                binding!!.selectlang.setTextColor(Color.BLACK)
                binding!!.polici.setTextColor(Color.BLACK)
                binding!!.about.setTextColor(Color.BLACK)
                binding!!.ivChangePin.setColorFilter(Color.BLACK)
                binding!!.ivChangePin2.setColorFilter(Color.BLACK)
                binding!!.tvLanguage.setTextColor(Color.BLACK)
                // AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                //requireContext().setTheme(R.style.SCREEN)
                // Toast.makeText(context,"${sharedPreferences.getMode()}" , Toast.LENGTH_SHORT).show()
                sharedPreferences.setMode(false)
            }


        }

        binding!!.llPolicies.setOnClickListener {
            findNavController().navigate(R.id.action_Setting_to_menu_legal_policies)
        }
        binding!!.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
        binding!!.aboutUs.setOnClickListener {
            findNavController().navigate(R.id.action_Setting_to_aboutUs)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}