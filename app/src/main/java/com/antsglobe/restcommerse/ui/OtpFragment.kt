package com.antsglobe.restcommerse.ui

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.antsglobe.restcommerse.R
import com.antsglobe.restcommerse.Utils.PreferenceManager
import com.antsglobe.restcommerse.databinding.FragmentOtpBinding


class OtpFragment : Fragment() {

    private var _binding: FragmentOtpBinding? = null
    private val binding get() = _binding!!

    private var saveOtp: String = ""
    private var emailId: String = ""
    private var isSignUp: Boolean? = null
    private lateinit var sharedPreferences: PreferenceManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()

        _binding = FragmentOtpBinding.inflate(inflater, container, false)

        saveOtp = arguments?.getString("saveOtp").toString()
        emailId = arguments?.getString("emailId").toString()
        isSignUp = arguments?.getBoolean("isSignUp")


        sharedPreferences = PreferenceManager(requireContext())

        if (sharedPreferences.getMode() == true) {
            binding.fullscreen.setBackgroundColor(resources.getColor(R.color.blackfordark))
            binding.Otp.setTextColor(resources.getColor(R.color.whitefordark))
            binding.forgotOtp.setTextColor(resources.getColor(R.color.whitefordark))
            binding.forgotOtp.setHintTextColor(Color.GRAY)
            binding.otpheading.setTextColor(resources.getColor(R.color.whitefordark))
            binding.btnFP2.setTextColor(resources.getColor(R.color.blackfordark))
            binding.description.setHintTextColor(Color.GRAY)
            binding.otplayout.setBackgroundResource(R.drawable.profile_round_corner_bg_dark_wish)

        }
//        Toast.makeText(context, "$saveOtp", Toast.LENGTH_SHORT).show()
        binding.btnFP2.setOnClickListener {
            var enterOtp = binding.forgotOtp.text.toString()

            if (enterOtp == saveOtp) {
                if (isSignUp == true) {
                    val bundle = Bundle()
                    bundle.putBoolean("isSignUp", true)
                    bundle.putString("emailId", emailId)
                    findNavController().navigate(R.id.action_OtpFragment_to_SetPassword, bundle)
                } else {
                    findNavController().navigate(R.id.action_OtpFragment_to_SetPassword)
                }
            } else {
                customToast(requireContext(), "Otp does not match, enter again", R.drawable.ic_info)
            }
        }

        return binding.root
    }

    private fun customToast(context: Context, title: String, imageResourceId: Int) {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout = inflater.inflate(R.layout.custom_toast_layout, null)

        val imageViewIcon = layout.findViewById<ImageView>(R.id.imageViewIcon)
        imageViewIcon.setImageResource(imageResourceId) // Set the image using the passed resource ID

        val textViewMessage = layout.findViewById<TextView>(R.id.textViewMessage)
        textViewMessage.text = title

        val toast = Toast(context)
        toast.duration = Toast.LENGTH_SHORT
        toast.view = layout
        toast.show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

}