package com.antsglobe.restcommerse.ui

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.antsglobe.restcommerse.R
import com.antsglobe.restcommerse.Utils.PreferenceManager
import com.antsglobe.restcommerse.databinding.DialogImageSelectionBinding
import com.antsglobe.restcommerse.databinding.FragmentEditProfileBinding
import com.antsglobe.restcommerse.network.RetrofitClient
import com.antsglobe.restcommerse.viewmodel.ProfileViewModel
import com.antsglobe.restcommerse.viewmodel.UpdateProfileViewModel
import com.antsglobe.restcommerse.viewmodel.ViewModelFactory
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.regex.Pattern


class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var sharedPreferences: PreferenceManager
    private lateinit var updateProfileViewModel: UpdateProfileViewModel
    var avtarImage: Int = 0

    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener
    private lateinit var sdf1: SimpleDateFormat
    private var cal = Calendar.getInstance()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        sharedPreferences = PreferenceManager(requireContext())

//        binding.profileImage.setImageResource(sharedPreferences.getProfilePic())

        val profilePicName = sharedPreferences.getProfilePic()

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

            val imageView = binding.profileImage

            imageView.setImageResource(drawableMap[drawableName] ?: 0)

        } else {
            binding.profileImage.setImageResource(R.drawable.boy1)
        }

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        profileViewModel = ViewModelProvider(
            requireActivity(), ViewModelFactory(RetrofitClient.apiService)
        )[ProfileViewModel::class.java]

        updateProfileViewModel = ViewModelProvider(
            requireActivity(), ViewModelFactory(RetrofitClient.apiService)
        )[UpdateProfileViewModel::class.java]


        val email = sharedPreferences.getEmail().toString().trim()

        binding.backButton.setOnClickListener {

            findNavController().popBackStack()
        }

        if (sharedPreferences.getMode() == true) {
            binding.logoImageHome.setTextColor(resources.getColor(R.color.blackfordark))
            binding.backButton.setImageResource(R.drawable.ic_baseline_arrow_back_24_dark)
            binding.fullscreen.setBackgroundColor(resources.getColor(R.color.blackfordark))
            binding.userName.setTextColor(resources.getColor(R.color.whitefordark))
            binding.editimage.setBackgroundResource(R.drawable.background_circle_black)
            binding.userEmail.setTextColor(resources.getColor(R.color.dark_grey))
            binding.userName2.setHintTextColor(resources.getColor(R.color.dark_grey))
            binding.userName2.setTextColor(resources.getColor(R.color.whitefordark))
            binding.userEmail2.setHintTextColor(resources.getColor(R.color.dark_grey))
            binding.userEmail2.setTextColor(resources.getColor(R.color.whitefordark))
            binding.userPhone.setHintTextColor(resources.getColor(R.color.dark_grey))
            binding.userPhone.setTextColor(resources.getColor(R.color.whitefordark))
            binding.etDate.setHintTextColor(resources.getColor(R.color.dark_grey))
            binding.etDate.setTextColor(resources.getColor(R.color.whitefordark))
            binding.male.setTextColor(resources.getColor(R.color.whitefordark))
            binding.female.setTextColor(resources.getColor(R.color.whitefordark))
            binding.other.setTextColor(resources.getColor(R.color.whitefordark))
            binding.nameheading.setTextColor(resources.getColor(R.color.whitefordark))
            binding.emailheading.setTextColor(resources.getColor(R.color.whitefordark))
            binding.dateheading.setTextColor(resources.getColor(R.color.whitefordark))
            binding.genderheading.setTextColor(resources.getColor(R.color.whitefordark))
            binding.nameedit.setBackgroundResource(R.drawable.profile_round_corner_bg_dark_wish)
            binding.emailedit.setBackgroundResource(R.drawable.profile_round_corner_bg_dark_wish)
            binding.etDate.setBackgroundResource(R.drawable.profile_round_corner_bg_dark_wish)
            binding.phoneedit.setBackgroundResource(R.drawable.profile_round_corner_bg_dark_wish)
        }

        profileViewModel.getProfileVM(email)
        profileInitObserver()

        binding.changeImageButton.setOnClickListener {
            showImageSelectionDialog()
        }


        dateSetListener =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val myFormat = "dd-MM-yyyy"
                val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
                binding.etDate.setText(sdf.format(cal.time).toString())

                val myFormat1 = "MM-dd-yyyy"
                sdf1 = SimpleDateFormat(myFormat1, Locale.getDefault())
//                Toast.makeText(context,sdf1.format(cal.time).toString() , Toast.LENGTH_SHORT).show()

            }

        if (sharedPreferences.getMode() == true) {
            binding.etDate.setOnClickListener {
                DatePickerDialog(
                    requireContext(),
                    R.style.MyDatePickerDialogTheme,
                    dateSetListener,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                ).show()
            }
        } else {
            binding.etDate.setOnClickListener {
                DatePickerDialog(
                    requireContext(),
                    dateSetListener,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                ).show()
            }
        }



        binding.UpdateProfileButton.setOnClickListener {
            if (validateFields()) {
                val email = binding.userEmail2.text.toString().trim()
                val name = binding.userName2.text.toString().trim()
//                val dob = binding.dob.text.toString().trim()
                val dob = binding.etDate.text.toString()

//                val address = binding.userPassword.text.toString().trim()
                val phone = binding.userPhone.text.toString().trim()
//                val gender = binding.gender.selectedItemPosition.toString()

                val selectedRadioButtonId = binding.genderRB.checkedRadioButtonId + 1

                var gender: String = "0"
                if (binding.male.isChecked) {
                    gender = "1"
//                    Toast.makeText(context, "fffff", Toast.LENGTH_SHORT).show()
                } else if (binding.female.isChecked) {
                    gender = "2"
                } else if (binding.other.isChecked) {
                    gender = "3"
                }

//                val selectedRadioButton = binding.root.findViewById<RadioButton>(selectedRadioButtonId)
//                val gender = selectedRadioButtonId.toString()
                val avtarImage = sharedPreferences.getAvatarImg()


                val inputDateStr = dob
                val inputDateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.US)
                val date = inputDateFormat.parse(inputDateStr)
                val outputDateFormat = SimpleDateFormat("MM-dd-yyy", Locale.US)
                val outputDateStr = outputDateFormat.format(date)

//                println(outputDateStr)

                sharedPreferences.setEmail(email)
                updateProfileViewModel.UpdateProfileVM(
                    email,
                    name,
                    outputDateStr,
                    "",
                    phone,
                    gender,
                    avtarImage.toString()
                )
            }
            updateProfileInitObserver()
        }

    }

    private fun updateProfileInitObserver() {
        updateProfileViewModel.getUpdateProfile.observe(viewLifecycleOwner) { updateProfileResp ->
//            LoadingDialog.dismissProgressDialog()
            if (updateProfileResp?.is_success == "true") {
                customToast(requireContext(), "profile updated", R.drawable.ic_info)
                Log.e("loginResp", "loginResp $updateProfileResp")

                val email = sharedPreferences.getEmail().toString().trim()


                val updateImage = sharedPreferences.getAvatarImg()

                if (updateImage != null) {

                    sharedPreferences.setProfilePic(updateImage)
                    sharedPreferences.setAvatarImg(updateImage)
                }

                findNavController().popBackStack()

//                profileViewModel.getProfileVM(email)
//                profileInitObserver()


            } else {
                customToast(requireContext(), "${updateProfileResp?.message}", R.drawable.ic_info)

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
        val email = binding.userEmail2.text.toString().trim()

        if (!isEmailValid(email)) {
            binding.userEmail2.error = "Invalid email address"
            return false
        }

        val phoneNumber = binding.userPhone.text.toString().trim()
        if (!isPhoneValid(phoneNumber)) {
            binding.userPhone.error = "Invalid phone no"
            return false
        }

        return true

    }

    private fun isPhoneValid(phoneNumber: String): Boolean {
        val regex = "\\d{10}"
        val pattern = Pattern.compile(regex)
        val matcher = pattern.matcher(phoneNumber)
        return matcher.matches()
    }

    private fun isEmailValid(email: String): Boolean {
        val emailPattern: Pattern = Patterns.EMAIL_ADDRESS
        return emailPattern.matcher(email).matches()
    }

    private fun showImageSelectionDialog() {
        val dialogBinding =
            DialogImageSelectionBinding.inflate(LayoutInflater.from(requireContext()))
        val dialog = Dialog(requireContext()) // Set the theme here
        dialog.setContentView(dialogBinding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val drawableNames = mutableListOf<String>()

        val defaultImages = arrayOf(
            R.drawable.boy1,
            R.drawable.boy2,
            R.drawable.boy3,
            R.drawable.boy4,
            R.drawable.girl3,
            R.drawable.girl4,
            R.drawable.girl1,
            R.drawable.girl2,
            R.drawable.girl5
        )

        val defaultImageButtons = arrayOf(
            dialogBinding.defaultImage1,
            dialogBinding.defaultImage2,
            dialogBinding.defaultImage3,
            dialogBinding.defaultImage4,
            dialogBinding.defaultImage5,
            dialogBinding.defaultImage6,
            dialogBinding.defaultImage7,
            dialogBinding.defaultImage8,
            dialogBinding.defaultImage9
        )

        val cameraButton = dialogBinding.cameraButton
        for (i in defaultImageButtons.indices) {
            val resourceId = defaultImages[i]
            defaultImageButtons[i].setOnClickListener {

                val resourceName = resources.getResourceEntryName(resourceId)
                drawableNames.add(resourceName)

//                Toast.makeText(context, "$resourceName", Toast.LENGTH_SHORT).show()

//                binding.profileImage.setImageResource(resourceId)
                sharedPreferences.setAvatarImg(resourceName)


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

                val drawableName = resourceName

                val imageView = binding.profileImage

                imageView.setImageResource(drawableMap[drawableName] ?: 0)

                if (drawableName != null) {
                    sharedPreferences.setAvatarImg(drawableName)
                }



                dialog.dismiss()
            }
        }

        cameraButton.setOnClickListener {
            galleryLauncher.launch("image/*")
            dialog.dismiss()
        }

        dialog.show()
    }


    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                binding.profileImage.setImageURI(it)
//            saveProfileImageUri(it)
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
                    val dob = profileData[0].dob
                    val address = profileData[0].address
                    val gender = profileData[0].gender
                    val profileImage = profileData[0].uer_image
                    val accessToken = profileData[0].token

                    binding.userName.setText(name).toString().trim()
                    binding.userEmail.setText(email).toString().trim()
                    binding.userName2.setText(name).toString().trim()
                    binding.userEmail2.setText(email).toString().trim()
                    binding.userPhone.setText(phoneNo).toString().trim()
//                    binding.profileImage.setImageResource(sharedPreferences.getProfilePic())


                    sharedPreferences.setName(name)
                    sharedPreferences.setPhoneNo(phoneNo)


                    if (gender != null) {
                        if (gender.isNotEmpty()) {
//                            binding.gender.setSelection(Integer.parseInt(gender))

                            if (profile.content[0].gender.equals("1", ignoreCase = true)) {
                                binding.male.isChecked = true
                            } else if (profile.content[0].gender.equals("2", ignoreCase = true)) {
                                binding.female.isChecked = true
                            } else if (profile.content[0].gender.equals("3", ignoreCase = true)) {
                                binding.other.isChecked = true
                            }


                        } else {
//                            binding.gender.setSelection(0)
                        }
                    } else {
//                        binding.gender.setSelection(0)
                    }


                    if (profileImage != null) {

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

                        val drawableName = profileImage
                        val imageView = binding.profileImage
                        imageView.setImageResource(drawableMap[drawableName] ?: 0)

                    } else {
                        binding.profileImage.setImageResource(R.drawable.boy1)
                    }

                    if (profileImage != null) {

                        sharedPreferences.setProfilePic(profileImage)
                        sharedPreferences.setAvatarImg(profileImage)
                    }

                    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")

                    try {
                        val date = dob?.let { dateFormat.parse(it) }

                        val calendar = Calendar.getInstance()
                        if (date != null) {
                            calendar.time = date
                            val year = calendar[Calendar.YEAR]
                            val month = calendar[Calendar.MONTH] + 1
                            val day = calendar[Calendar.DAY_OF_MONTH]
                            val dobb = "$day-$month-$year"

                            binding.etDate.setText(dobb)
                        } else {
                            binding.etDate.setText("")

                        }


                    } catch (e: ParseException) {
                        e.printStackTrace()
                    }

//                    binding.etDate.setText(it.content[0].dob)


//                    sharedPreferences.setName(name)
//                    sharedPreferences.setEmail(email)
//                    sharedPreferences.setPhoneNo(phoneNo)
//                    sharedPreferences.setAccessToken(accessToken)
//                    sharedPreferences.setLoggedIn(true)

                }

            } else {
                customToast(requireContext(), "${profile?.message}", R.drawable.ic_info)

            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}