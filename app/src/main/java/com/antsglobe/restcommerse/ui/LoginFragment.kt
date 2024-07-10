package com.antsglobe.restcommerse.ui

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.antsglobe.restcommerse.R
import com.antsglobe.restcommerse.Utils.PreferenceManager
import com.antsglobe.restcommerse.databinding.FragmentLoginBinding
import com.antsglobe.restcommerse.network.RetrofitClient
import com.antsglobe.restcommerse.viewmodel.LoginViewModel
import com.antsglobe.restcommerse.viewmodel.ProfileViewModel
import com.antsglobe.restcommerse.viewmodel.SignUpViewModel
import com.antsglobe.restcommerse.viewmodel.ViewModelFactory
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import java.util.regex.Pattern


class LoginFragment : Fragment() {


    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedPreferences: PreferenceManager
    private lateinit var signUpViewModel: SignUpViewModel
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var profileViewModel: ProfileViewModel

    private var remember: Boolean = false

    var isPasswordVisible = false

    private companion object {
        const val RC_SIGN_IN = 123
    }

    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var mAuth: FirebaseAuth

    private var token: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        sharedPreferences = PreferenceManager(requireContext())

        mAuth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        return binding.root

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        if (sharedPreferences.getMode() == true) {
            binding.fullscreen.setBackgroundColor(resources.getColor(R.color.blackfordark))
            binding.login.setTextColor(resources.getColor(R.color.whitefordark))
            binding.forgotPasswordBtn.setTextColor(resources.getColor(R.color.whitefordark))
            binding.emailheading.setTextColor(resources.getColor(R.color.whitefordark))
            binding.or.setTextColor(resources.getColor(R.color.whitefordark))
            binding.already.setHintTextColor(resources.getColor(R.color.whitefordark))
            binding.createAccount.setTextColor(resources.getColor(R.color.orange))
            binding.emailheading.setTextColor(resources.getColor(R.color.whitefordark))
            binding.passheading.setTextColor(resources.getColor(R.color.whitefordark))
            binding.UserNameEmail.setHintTextColor(Color.GRAY)
            binding.userPassword.setHintTextColor(Color.GRAY)
            binding.btnLogin.setTextColor(resources.getColor(R.color.blackfordark))
            binding.description.setHintTextColor(Color.GRAY)
            binding.UserNameEmail.setTextColor(resources.getColor(R.color.whitefordark))
            binding.userPassword.setTextColor(resources.getColor(R.color.whitefordark))
            binding.emaillayout.setBackgroundResource(R.drawable.profile_round_corner_bg_dark_wish)
            binding.passlayout.setBackgroundResource(R.drawable.profile_round_corner_bg_dark_wish)

        }

        loginViewModel = ViewModelProvider(
            requireActivity(),
            ViewModelFactory(RetrofitClient.apiService)
        )[LoginViewModel::class.java]

        signUpViewModel = ViewModelProvider(
            this,
            ViewModelFactory(RetrofitClient.apiService)
        )[SignUpViewModel::class.java]

        profileViewModel = ViewModelProvider(
            requireActivity(), ViewModelFactory(RetrofitClient.apiService)
        )[ProfileViewModel::class.java]

        binding.forgotPasswordBtn.setOnClickListener {
            findNavController().navigate(R.id.action_LoginFragment_to_ForgetPassFragment)
        }

        binding.googleSignup.setOnClickListener {
            signInWithGoogleActivity()
        }

        binding.createAccount.setOnClickListener {
            findNavController().navigate(R.id.action_LoginFragment_to_SignUpFragment)
        }


        binding.rememberMe.setOnClickListener {
            if (binding.rememberMe.isChecked) {
                remember = true
            } else {
                remember = false
            }
        }

        binding.btnLogin.setOnClickListener {

            if (validateFields()) {
                val email = binding.UserNameEmail.text.toString().trim()
                val password = binding.userPassword.text.toString().trim()
//                LoadingDialog.showProgressDialog(this, "loading...")

                loginViewModel.login(email, password, remember.toString())

            }
            loginInitObserver()
        }


        binding.visibilityPass.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {

                binding.visibilityPass.setImageResource(R.drawable.visibility)

                // Show password
                binding.userPassword.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {

                binding.visibilityPass.setImageResource(R.drawable.hide_password)

                // Hide password
                binding.userPassword.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            binding.userPassword.setSelection(binding.userPassword.text.length)
        }

    }

    private fun signInWithGoogleActivity() {

        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account?.idToken)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
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

//    private fun firebaseAuthWithGoogle(idToken: String?) {
//        val credential = GoogleAuthProvider.getCredential(idToken, null)
//        mAuth.signInWithCredential(credential)
//            .addOnCompleteListener(requireActivity()) { task ->
//                if (task.isSuccessful) {
//
//                    val user = mAuth.currentUser
//                    val email = user?.email.toString()
//
//                    profileViewModel.getProfileVM(email)
//                    profileInitObserver()
//
//                } else {
//                    Log.w(TAG, "signInWithCredential:failure", task.exception);
//                }
//            }
//    }

    private fun profileInitObserver() {
        profileViewModel.getProfile.observe(viewLifecycleOwner) { profile ->
//            LoadingDialog.dismissProgressDialog()
            if (profile?.status.toString() == "200") {

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

                val user = mAuth.currentUser
                val email = user?.email.toString()
                val pass = user?.uid.toString()
                val name = user?.displayName.toString()
                val phone = user?.phoneNumber.toString()


                signUpViewModel.signUp(name, email, phone, pass, token.toString())
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

                loginViewModel.login(email, pass, remember.toString())
                loginInitObserver()

            } else {
                customToast(
                    requireContext(),
                    "Already exist email try with another id ",
                    R.drawable.ic_info
                )


            }
        }
        //LoadingDialog.showProgressDialog(this, "loading...")
    }

    private fun loginInitObserver() {
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
        val email = binding.UserNameEmail.text.toString().trim()

        if (!isEmailValid(email)) {
            binding.UserNameEmail.error = "Invalid email address"
            return false
        }

        val password = binding.userPassword.text.toString().trim()
        if (password.isEmpty()) {
            binding.userPassword.error = "Password cannot be empty"
            return false
        }

        val passwordPattern: Pattern =
            Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#+_/;:,-])[A-Za-z\\d@$!%*?&#+_/;:,-]{8,}$")
        if (!passwordPattern.matcher(password).matches()) {
            binding.userPassword.error =
                "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special symbol"
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