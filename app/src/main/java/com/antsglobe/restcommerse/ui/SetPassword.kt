package com.antsglobe.restcommerse.ui

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
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
import com.antsglobe.restcommerse.databinding.FragmentSetPasswordBinding
import com.antsglobe.restcommerse.network.RetrofitClient
import com.antsglobe.restcommerse.viewmodel.ResetPassViewModel
import com.antsglobe.restcommerse.viewmodel.ViewModelFactory
import java.util.regex.Pattern


class SetPassword : Fragment() {

    private var isSignUp: Boolean? = null
    private var _binding: FragmentSetPasswordBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ResetPassViewModel
    private var savedString: String = ""
    private var email: String = ""
    private var emailId: String = ""


    private lateinit var sharedPreferences: PreferenceManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
        _binding = FragmentSetPasswordBinding.inflate(inflater, container, false)
        emailId = arguments?.getString("emailId").toString()
        isSignUp = arguments?.getBoolean("isSignUp")
        val sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE)
        Log.e("Test TU", "Password emailId: $emailId, Password isSignUp : $isSignUp")

        email = sharedPreferences.getString("key_data_email", savedString).toString().trim()
//        Toast.makeText(context, email, Toast.LENGTH_SHORT).show()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(
            requireActivity(),
            ViewModelFactory(RetrofitClient.apiService)
        )[ResetPassViewModel::class.java]

// Retrieve the string using the key

//        Toast.makeText(context, email, Toast.LENGTH_SHORT).show()

        if (isSignUp == true) {
            binding.tvChangePassword.text = "Create Password"
            binding.btnFP3.text = "Set Password"
            binding.tvSubname.text = "Cerate your new password"
        } else {
            binding.tvChangePassword.text = "Change Password"
            binding.btnFP3.text = "Reset Password"
            binding.tvSubname.text = "Set your new password"
        }

        sharedPreferences = PreferenceManager(requireContext())

        if (sharedPreferences.getMode() == true) {
            binding.fullscreen.setBackgroundColor(resources.getColor(R.color.blackfordark))
            binding.tvChangePassword.setTextColor(resources.getColor(R.color.whitefordark))
            binding.passheading.setTextColor(resources.getColor(R.color.whitefordark))
            binding.passheading2.setTextColor(resources.getColor(R.color.whitefordark))
            binding.btnFP3.setTextColor(resources.getColor(R.color.blackfordark))
            binding.tvSubname.setHintTextColor(Color.GRAY)
            binding.passlayout.setBackgroundResource(R.drawable.profile_round_corner_bg_dark_wish)
            binding.passlayout2.setBackgroundResource(R.drawable.profile_round_corner_bg_dark_wish)

        }

        binding.btnFP3.setOnClickListener {
            if (validateFields()) {
                val enterPass = binding.newPassword.text.toString()
                val reEnterPass = binding.conformNewPassword.text.toString()
                if (enterPass == reEnterPass) {
                    if (isSignUp == true) {
                        viewModel.resetPassVM(emailId, reEnterPass)
                    } else {
                        viewModel.resetPassVM(email, reEnterPass)
                    }
                    initObserver()

                } else {
                    customToast(requireContext(), "Password does not match", R.drawable.ic_info)

                }

            }
        }

    }

    private fun initObserver() {
        viewModel.apiResponse.observe(viewLifecycleOwner) { resetPass ->
//            LoadingDialog.dismissProgressDialog()
            if (resetPass?.is_success.toString() == "true") {
                Log.e("resetPass", "resetPass $resetPass")
                customToast(
                    requireContext(),
                    "Password reset successfully",
                    R.drawable.success_toast_icon
                )

                findNavController().navigate(R.id.action_SetPassword_to_LoginFragment)

            } else {
                customToast(requireContext(), "${resetPass?.message}", R.drawable.ic_info)

            }
        }
    }

    private fun validateFields(): Boolean {
        val enterPass = binding.newPassword.text.toString().trim()
        val reEnterPass = binding.conformNewPassword.text.toString().trim()

        if (enterPass.isEmpty()) {
            binding.newPassword.error = "Password cannot be empty"
            return false
        }
        if (reEnterPass.isEmpty()) {
            binding.conformNewPassword.error = "Password cannot be empty"
            return false
        }


        val passwordPattern: Pattern =
            Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#+_/;:,-])[A-Za-z\\d@$!%*?&#+_/;:,-]{8,}$")
        if (!passwordPattern.matcher(enterPass).matches()) {
            binding.newPassword.error =
                "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special symbol"
            return false
        }
        if (!passwordPattern.matcher(reEnterPass).matches()) {
            binding.conformNewPassword.error =
                "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special symbol"
            return false
        }

        return true
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
}