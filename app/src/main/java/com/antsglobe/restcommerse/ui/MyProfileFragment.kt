package com.antsglobe.restcommerse.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.antsglobe.restcommerse.R
import com.antsglobe.restcommerse.Utils.PreferenceManager
import com.antsglobe.restcommerse.databinding.FragmentMyProfileBinding


class MyProfileFragment : Fragment() {

    private var _binding: FragmentMyProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var sharedPreferences: PreferenceManager
    private var isNavigation = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            // param1 = it.getString(ARG_PARAM1)
            isNavigation = it.getBoolean("isNavigation")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyProfileBinding.inflate(inflater, container, false)
        sharedPreferences = PreferenceManager(requireContext())

//        binding.profilePic.setImageResource(sharedPreferences.getProfilePic())
        val profilePicName = sharedPreferences.getProfilePic()

        /*    if (isNavigation == true) {
                binding.backButton.visibility = View.VISIBLE
            } else {
                binding.backButton.visibility = View.GONE
            }*/

        if (profilePicName != null) {

            val drawableMap = mapOf(
                "boy1" to R.drawable.boy1,
                "boy2" to R.drawable.boy2,
                "boy3" to R.drawable.boy3,
                "boy4" to R.drawable.boy4,
                "girl1" to R.drawable.girl1,
                "girl2" to R.drawable.girl2,
                "girl3" to R.drawable.girl3,
                "girl4" to R.drawable.girl4,
                "girl5" to R.drawable.girl5,
            )

            val drawableName = profilePicName

            val imageView = binding.profilePic

            imageView.setImageResource(drawableMap[drawableName] ?: 0)

        } else {
            binding.profilePic.setImageResource(R.drawable.boy1)
        }


        return binding.root
    }

    /*    companion object {
            @JvmStatic
            fun newInstance(param1: String, param2: String) =
                MyProfileFragment().apply {
                    arguments = Bundle().apply {
                        //putString(ARG_PARAM1, param1)
                        //putString(ARG_PARAM2, param2)
                    }
                }
        }*/

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        if (sharedPreferences.getMode() == true) {
            binding.logoImageHome.setTextColor(resources.getColor(R.color.blackfordark))
            binding.backButton.setImageResource(R.drawable.ic_baseline_arrow_back_24_dark)
            binding.fullscreen.setBackgroundColor(resources.getColor(R.color.blackfordark))
            binding.linearLayout.setBackground(resources.getDrawable(R.drawable.profile_round_corner_bg_dark_wish))
            binding.cvEditProfile.setBackground(resources.getDrawable(R.drawable.profile_round_corner_bg_dark_wish))
            binding.cvEditSettings.setBackground(resources.getDrawable(R.drawable.profile_round_corner_bg_dark_wish))
            binding.cvWishList.setBackground(resources.getDrawable(R.drawable.profile_round_corner_bg_dark_wish))
            binding.cvWallet.setBackground(resources.getDrawable(R.drawable.profile_round_corner_bg_dark_wish))
            binding.cvLanguage.setBackground(resources.getDrawable(R.drawable.profile_round_corner_bg_dark_wish))
            binding.cvMyOrder.setBackground(resources.getDrawable(R.drawable.profile_round_corner_bg_dark_wish))
            binding.EditT.setTextColor(resources.getColor(R.color.whitefordark))
            binding.SettingT.setTextColor(resources.getColor(R.color.whitefordark))
            binding.OrderT.setTextColor(resources.getColor(R.color.whitefordark))
            binding.WishlistT.setTextColor(resources.getColor(R.color.whitefordark))
            binding.WalletT.setTextColor(resources.getColor(R.color.whitefordark))
            binding.LanguageT.setTextColor(resources.getColor(R.color.whitefordark))
            binding.a1.setColorFilter(resources.getColor(R.color.dark_grey))
            binding.a2.setColorFilter(resources.getColor(R.color.dark_grey))
            binding.a3.setColorFilter(resources.getColor(R.color.dark_grey))
            binding.a4.setColorFilter(resources.getColor(R.color.dark_grey))
            binding.a5.setColorFilter(resources.getColor(R.color.dark_grey))
            binding.a6.setColorFilter(resources.getColor(R.color.dark_grey))
            binding.LanguageI.setImageResource(R.drawable.world)
            binding.WalletI.setImageResource(R.drawable.profile_wallet_dark)
            binding.OrderI.setImageResource(R.drawable.profile_order_dark)
            binding.EditI.setImageResource(R.drawable.profile_edit_dark)
            binding.WishlistI.setImageResource(R.drawable.profile_heart_dark)
            binding.SettingI.setImageResource(R.drawable.profile_settings_dark)
        }

        val name = sharedPreferences.getName().toString().trim()
        val email = sharedPreferences.getEmail().toString().trim()
        val mobileno = sharedPreferences.getPhoneNo().toString().trim()

        binding.UserNameTV.setText(name).toString().trim()

        binding.UserNameEmail.setText(email).toString().trim()

        binding.UserMobile.text = mobileno

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

//        Toast.makeText(context, "$name $email", Toast.LENGTH_SHORT).show()

        binding.cvEditProfile.setOnClickListener {
            findNavController().navigate(R.id.action_MyProfileFragment_to_EditProfileFragment)
        }

        binding.cvEditSettings.setOnClickListener {
            findNavController().navigate(R.id.action_my_profile_to_Setting)
        }

        binding.cvWishList.setOnClickListener {
            val bundle = Bundle()
            bundle.putBoolean("isMyProfile", true)
            findNavController().navigate(R.id.action_my_profile_to_MyWishListFragment, bundle)
        }
        binding.cvMyOrder.setOnClickListener {
            findNavController().navigate(R.id.action_my_profile_to_MyOrderFragment)
        }
    }
}