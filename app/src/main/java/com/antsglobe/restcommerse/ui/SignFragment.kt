package com.antsglobe.restcommerse.ui

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
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
import com.antsglobe.restcommerse.databinding.FragmentSignBinding
import com.antsglobe.restcommerse.network.RetrofitClient
import com.antsglobe.restcommerse.viewmodel.LoginViewModel
import com.antsglobe.restcommerse.viewmodel.ProfileViewModel
import com.antsglobe.restcommerse.viewmodel.SignUpViewModel
import com.antsglobe.restcommerse.viewmodel.ViewModelFactory
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.messaging.FirebaseMessaging
import java.util.regex.Pattern

class SignFragment : Fragment() {

    private var token: String? = null
    private lateinit var binding: FragmentSignBinding
    private lateinit var signUpViewModel: SignUpViewModel
    private lateinit var sharedPreferences: PreferenceManager
    private var saveOtp: String? = null
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var profileViewModel: ProfileViewModel

    private companion object {
        const val RC_SIGN_IN = 123
    }

    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var mAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
        // Inflate the layout for this fragment
        binding = FragmentSignBinding.inflate(inflater, container, false)

        signUpViewModel = ViewModelProvider(
            this,
            ViewModelFactory(RetrofitClient.apiService)
        )[SignUpViewModel::class.java]

        loginViewModel = ViewModelProvider(
            requireActivity(),
            ViewModelFactory(RetrofitClient.apiService)
        )[LoginViewModel::class.java]

        profileViewModel = ViewModelProvider(
            requireActivity(), ViewModelFactory(RetrofitClient.apiService)
        )[ProfileViewModel::class.java]

        sharedPreferences = PreferenceManager(requireContext())

        mAuth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.LoginAccount?.setOnClickListener {
            findNavController().navigate(R.id.action_SignFragment_to_LoginFragment)
        }
        if (sharedPreferences.getMode() == true) {
            binding.fullscreen.setBackgroundColor(resources.getColor(R.color.blackfordark))
            binding.signup.setTextColor(resources.getColor(R.color.whitefordark))
            binding.nameheading.setTextColor(resources.getColor(R.color.whitefordark))
            binding.emailheading.setTextColor(resources.getColor(R.color.whitefordark))
            binding.phoneheading.setTextColor(resources.getColor(R.color.whitefordark))
            binding.or.setTextColor(resources.getColor(R.color.whitefordark))
            binding.already.setHintTextColor(resources.getColor(R.color.whitefordark))
            binding.LoginAccount.setTextColor(resources.getColor(R.color.orange))
            binding.etUserName.setHintTextColor(Color.GRAY)
            binding.emailheading.setTextColor(resources.getColor(R.color.whitefordark))
            binding.etUserEmail.setHintTextColor(Color.GRAY)
            binding.etUserPhone.setHintTextColor(Color.GRAY)
            binding.nameheading.setTextColor(resources.getColor(R.color.white))
            binding.phoneheading.setTextColor(resources.getColor(R.color.white))
            binding.btnSignup.setTextColor(resources.getColor(R.color.blackfordark))
            binding.description.setHintTextColor(Color.GRAY)
            binding.etUserEmail.setTextColor(resources.getColor(R.color.whitefordark))
            binding.etUserName.setTextColor(resources.getColor(R.color.whitefordark))
            binding.emaillayout.setBackgroundResource(R.drawable.profile_round_corner_bg_dark_wish)
            binding.namelayout.setBackgroundResource(R.drawable.profile_round_corner_bg_dark_wish)
            binding.phonelayout.setBackgroundResource(R.drawable.profile_round_corner_bg_dark_wish)

        }

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "FCM registration token", task.exception)
                return@OnCompleteListener
            }
            // Get new FCM registration token
            token = task.result
            // Log and toast
            Log.d(TAG, "firebase : ${token.toString()}")
//            Toast.makeText(this@MainActivity, token, Toast.LENGTH_SHORT).show()
//            binding.problemET.setText(token)

        })

        binding?.btnSignup?.setOnClickListener {
            if (validateFields()) {
                val fullName = binding.etUserName.text.toString()
                val mEmail = binding.etUserEmail.text.toString()
                val phone = binding.etUserPhone.text.toString().trim()
                //   val password = binding.etUserPassword.text.toString().trim()

                signUpViewModel.signUp(fullName, mEmail, phone, " ", token.toString())
                initObserver()

            }
        }

//        binding.signinGoogleBtn.setOnClickListener {
//            signInWithGoogleActivity()
//        }
    }

    private fun signInWithGoogleActivity() {

        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account?.idToken)

            } catch (e: ApiException) {
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }


    private fun firebaseAuthWithGoogle(idToken: String?) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {

                    val user = mAuth.currentUser
                    val email = user?.email.toString()

                    profileViewModel.getProfileVM(email)
                    profileInitObserver()


                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception);
                }
            }
    }

    private fun profileInitObserver() {
        profileViewModel.getProfile.observe(viewLifecycleOwner) { profile ->
//            LoadingDialog.dismissProgressDialog()
            if (profile?.status == "200") {

                profileViewModel.getProfileItem.observe(viewLifecycleOwner) { profileData ->

                    Log.e("profileData", "profileData $profileData")
                    val name = profileData[0].name
                    val email = profileData[0].email
                    val phoneNo = profileData[0].mobno
                    val accessToken = profileData[0].token

                    sharedPreferences.setName(name)
                    sharedPreferences.setEmail(email)
                    sharedPreferences.setPhoneNo(phoneNo)
                    sharedPreferences.setAccessToken(accessToken)
                    sharedPreferences.setLoggedIn(true)

                    val intent = Intent(context, HomeActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()

                }

            } else {
                initObserverGoogle()
//                Toast.makeText(context, "${profile?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initObserverGoogle() {
        signUpViewModel.apiResponse.observe(viewLifecycleOwner) { spResp ->
            Log.e("signup", "signup: $spResp")
            //LoadingDialog.dismissProgressDialog()
            if (spResp?.is_success == true) {

                val user = mAuth.currentUser
                val email = user?.email.toString()
                val pass = user?.uid.toString()

                loginViewModel.login(email, pass, true.toString())
                loginIinitObserver()

            } else {

                customToast(
                    requireContext(),
                    "Already exist email try with another id",
                    R.drawable.ic_info
                )

            }
        }
        //LoadingDialog.showProgressDialog(this, "loading...")
    }


    private fun loginIinitObserver() {
        loginViewModel.apiResponse.observe(viewLifecycleOwner) { loginResp ->
//            LoadingDialog.dismissProgressDialog()
            if (loginResp?.get(0)?.status == "200") {
//                Toast.makeText(context, "Login success", Toast.LENGTH_SHORT).show()
                Log.e("loginResp", "loginResp $loginResp")
                val name = loginResp[0].name
                val email = loginResp[0].email
                val phoneNo = loginResp[0].mobno
                val accessToken = loginResp[0].token

                sharedPreferences.setName(name)
                sharedPreferences.setEmail(email)
                sharedPreferences.setPhoneNo(phoneNo)
                sharedPreferences.setAccessToken(accessToken)
                sharedPreferences.setLoggedIn(true)

                val intent = Intent(context, HomeActivity::class.java)
                startActivity(intent)
                requireActivity().finish()

            } else {
                customToast(requireContext(), "${loginResp?.get(0)?.message}", R.drawable.ic_info)

            }
        }
    }


    private fun validateFields(): Boolean {
        /* val passwordPattern: Pattern =
             Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#+_/;:,-])[A-Za-z\\d@$!%*?&#+_/;:,-]{8,}$")*/
        val phoneNumberPattern: Pattern = Pattern.compile("^[6-9]\\d{9}\$")
        val phoneNumber = binding.etUserPhone.text.toString().trim()
        val emailText = binding.etUserEmail.text.toString().trim()
        val fullNameText = binding.etUserName.text.toString().trim()
        // val Password = binding.etUserPassword.text.toString().trim()

        if (fullNameText.toString().isNullOrEmpty()) {
            binding.etUserName.error = "Invalid full name format"
            return false
        }
//        if (!isFullNameValid(fullNameText)) {
//            binding.etUserName.error = "Invalid full name format"
//            return false
//        }

        if (!isEmailValid(emailText)) {
            binding.etUserEmail.error = "Invalid email address"
            return false
        }

        if (!phoneNumberPattern.matcher(phoneNumber).matches()) {
            binding.etUserPhone.error = "Invalid phone number format (XXX-XXX-XXXX)"
            return false
        }

        /* if (!passwordPattern.matcher(Password).matches()) {
             binding.etUserPassword.error =
                 "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special symbol"
             return false
         }*/

        return true
    }

    private fun isEmailValid(email: String): Boolean {
        val emailPattern: Pattern = Patterns.EMAIL_ADDRESS
        return emailPattern.matcher(email).matches()
    }

    private fun isFullNameValid(fullName: String): Boolean {
        val lettersOnly = fullName.replace(" ", "").filter { it.isLetter() }
        return lettersOnly.length >= 8
    }


    private fun initObserver() {
        signUpViewModel.apiResponse.observe(viewLifecycleOwner) { spResp ->
            Log.e("signup", "signup: $spResp")
            //LoadingDialog.dismissProgressDialog()
            if (spResp?.is_success == true) {
                sharedPreferences.setAccessToken(spResp.authtoken)
                signUpViewModel.OtpVM("", spResp.email) // otp verfication
                initOtpObserver(spResp.email)
            } else {
                customToast(
                    requireContext(),
                    "Already exist email try with another id",
                    R.drawable.ic_info
                )

            }
        }
        //LoadingDialog.showProgressDialog(this, "loading...")
    }

    private fun initOtpObserver(email: String) {
        signUpViewModel.apiOtpResponse.observe(viewLifecycleOwner) { otpResp ->
//            LoadingDialog.dismissProgressDialog()
            if (otpResp?.is_success.toString() == "true") {
                customToast(
                    requireContext(),
                    "Otp sent to your mail Id",
                    R.drawable.success_toast_icon
                )

                Log.e("otpResp", "otpResp $otpResp")
                saveOtp = otpResp?.OTP

                val bundle = Bundle()
                bundle.putString("saveOtp", saveOtp)
                bundle.putBoolean("isSignUp", true)
                bundle.putString("emailId", email)
                findNavController().navigate(R.id.action_SignUpFragment_to_optFragment, bundle)
                // findNavController().navigate(R.id.action_ForgetPassFragment_to_OtpFragment, bundle)

            } else {
                customToast(requireContext(), "${otpResp?.message}", R.drawable.ic_info)

            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding
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