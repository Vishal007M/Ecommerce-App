package com.antsglobe.restcommerse.ui

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.antsglobe.restcommerse.R
import com.antsglobe.restcommerse.Utils.PreferenceManager
import com.antsglobe.restcommerse.databinding.FragmentForgetPassBinding
import com.antsglobe.restcommerse.network.RetrofitClient
import com.antsglobe.restcommerse.viewmodel.OtpViewModel
import com.antsglobe.restcommerse.viewmodel.ViewModelFactory
import java.util.regex.Pattern

class ForgetPassFragment : Fragment() {

    private var _binding: FragmentForgetPassBinding? = null
    private lateinit var viewModel: OtpViewModel

    private lateinit var sharedPreferences: PreferenceManager

    private val binding get() = _binding!!
    private var saveOtp: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
        _binding = FragmentForgetPassBinding.inflate(inflater, container, false)


        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(
            requireActivity(),
            ViewModelFactory(RetrofitClient.apiService)
        )[OtpViewModel::class.java]

        sharedPreferences = PreferenceManager(requireContext())

        binding.LoginAccount.setOnClickListener {
            findNavController().navigate(R.id.action_ForgetPassFragment_to_LoginFragment)
        }
        if (sharedPreferences.getMode() == true) {
            binding.fullscreen.setBackgroundColor(resources.getColor(R.color.blackfordark))
            binding.forgot.setTextColor(resources.getColor(R.color.whitefordark))
            binding.emailheading.setTextColor(resources.getColor(R.color.whitefordark))
            binding.or.setTextColor(resources.getColor(R.color.whitefordark))
            binding.already.setHintTextColor(resources.getColor(R.color.whitefordark))
            binding.LoginAccount.setTextColor(resources.getColor(R.color.orange))
            binding.emailheading.setTextColor(resources.getColor(R.color.whitefordark))
            binding.forgotEmail.setHintTextColor(Color.GRAY)
            binding.btnFP1.setTextColor(resources.getColor(R.color.blackfordark))
            binding.description.setHintTextColor(Color.GRAY)
            binding.forgotEmail.setTextColor(resources.getColor(R.color.whitefordark))
            binding.emaillayout.setBackgroundResource(R.drawable.profile_round_corner_bg_dark_wish)

        }

        binding.btnFP1.setOnClickListener {
            if (validateFields()) {
                val email = binding.forgotEmail.text.toString().trim()

                val sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString("key_data_email", email)
                editor.apply()

//                LoadingDialog.showProgressDialog(this, "loading...")
                viewModel.OtpVM("", email)

            }
            initObserver()
        }
    }


    private fun initObserver() {
        viewModel.apiResponse.observe(viewLifecycleOwner) { otpResp ->
//            LoadingDialog.dismissProgressDialog()
            if (otpResp?.is_success.toString() == "true") {
                customToast(requireContext(), "Otp sent to your mail Id", R.drawable.ic_info)
                Log.e("otpResp", "otpResp $otpResp")
                saveOtp = otpResp?.OTP

                val bundle = Bundle()
                bundle.putString("saveOtp", saveOtp)
                findNavController().navigate(R.id.action_ForgetPassFragment_to_OtpFragment, bundle)

            } else {
                customToast(requireContext(), "${otpResp?.message}", R.drawable.ic_info)

            }
        }
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


    private fun validateFields(): Boolean {
        val email = binding.forgotEmail.text.toString().trim()

        if (!isEmailValid(email)) {
            binding.forgotEmail.error = "Invalid email address"
            return false
        }

        return true
    }

    private fun isEmailValid(email: String): Boolean {
        val emailPattern: Pattern = Patterns.EMAIL_ADDRESS
        return emailPattern.matcher(email).matches()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}