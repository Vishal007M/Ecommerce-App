package com.antsglobe.restcommerse.ui.bottomSheet

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.antsglobe.restcommerse.R
import com.antsglobe.restcommerse.Utils.PreferenceManager
import com.antsglobe.restcommerse.databinding.AddAddressBottomsheetLayoutBinding
import com.antsglobe.restcommerse.network.RetrofitClient
import com.antsglobe.restcommerse.viewmodel.AddressViewModel
import com.antsglobe.restcommerse.viewmodel.ViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.regex.Pattern

class AddressDetailsBottomSheet : BottomSheetDialogFragment() {

    private var isMapAddress: Boolean? = false
    private var isEditAddress: Boolean? = false
    private var appartment: String? = null
    private var landmark: String? = null
    private var address: String? = ""
    private var binding: AddAddressBottomsheetLayoutBinding? = null
    private lateinit var viewmodel: AddressViewModel
    private lateinit var sharedPreferences: PreferenceManager
    private var addressType: String? = "Home"
    private var isDefault: String? = "false"
    private var addressId: String? = ""
    private var strAddress: String? = ""
    private lateinit var onBottomSheetCloseListener: OnBottomSheetCloseListener

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        //return inflater.inflate(R.layout.add_address_bottomsheet_layout, container, false)
        binding = AddAddressBottomsheetLayoutBinding.inflate(inflater, container, false)
        viewmodel = ViewModelProvider(
            this, ViewModelFactory(RetrofitClient.apiService)
        )[AddressViewModel::class.java]
        sharedPreferences = PreferenceManager(requireContext())

        return binding!!.root
    }

    interface OnBottomSheetCloseListener {
        fun onBottomSheetClose()
    }

    fun setOnBottomSheetCloseListener(listener: OnBottomSheetCloseListener) {
        onBottomSheetCloseListener = listener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isEditAddress = arguments?.getBoolean("is_editAddress")
        isMapAddress = arguments?.getBoolean("IsMap")

        if (isEditAddress == true) {
            binding?.tvheadline?.text = "Edit Address"
            binding?.btnAddAddress?.text = "Update Address"
        } else {
            binding?.tvheadline?.text = "Add New Address"
            binding?.btnAddAddress?.text = "Add Address"
        }
        if(sharedPreferences.getMode() == true){
            binding!!.fullscreen.setBackgroundColor(resources.getColor(R.color.blackfordark))
            binding!!.t1.setTextColor(resources.getColor(R.color.whitefordark))
            binding!!.t2.setTextColor(resources.getColor(R.color.whitefordark))
            binding!!.t3.setTextColor(resources.getColor(R.color.whitefordark))
            binding!!.t4.setTextColor(resources.getColor(R.color.whitefordark))
            binding!!.t5.setTextColor(resources.getColor(R.color.whitefordark))
            binding!!.t6.setTextColor(resources.getColor(R.color.whitefordark))
            binding!!.t7.setTextColor(resources.getColor(R.color.whitefordark))
            binding!!.t8.setTextColor(resources.getColor(R.color.whitefordark))
            binding!!.tvCity.setBackgroundResource(R.drawable.profile_round_corner_bg_dark_wish)
            binding!!.tvPincode.setBackgroundResource(R.drawable.profile_round_corner_bg_dark_wish)
            binding!!.tvCustomerName.setBackgroundResource(R.drawable.profile_round_corner_bg_dark_wish)
            binding!!.tvLandmark.setBackgroundResource(R.drawable.profile_round_corner_bg_dark_wish)
            binding!!.tvheadline.setBackgroundResource(R.drawable.profile_round_corner_bg_dark_wish)
            binding!!.tvCustomerNumber.setBackgroundResource(R.drawable.profile_round_corner_bg_dark_wish)
            binding!!.tvHouseNo.setBackgroundResource(R.drawable.profile_round_corner_bg_dark_wish)
            binding!!.tvPincode.setTextColor(resources.getColor(R.color.whitefordark))
            binding!!.tvCity.setTextColor(resources.getColor(R.color.whitefordark))
            binding!!.tvheadline.setTextColor(resources.getColor(R.color.whitefordark))
            binding!!.tvHouseNo.setTextColor(resources.getColor(R.color.whitefordark))
            binding!!.tvCustomerNumber.setTextColor(resources.getColor(R.color.whitefordark))
            binding!!.tvCustomerName.setTextColor(resources.getColor(R.color.whitefordark))
            binding!!.tvLandmark.setTextColor(resources.getColor(R.color.whitefordark))
            binding!!.rbHome.setTextColor(resources.getColor(R.color.whitefordark))
            binding!!.rbOffice.setTextColor(resources.getColor(R.color.whitefordark))
            binding!!.rbOther.setTextColor(resources.getColor(R.color.whitefordark))
            binding!!.cbDefault.setTextColor(resources.getColor(R.color.whitefordark))
            binding!!.btnAddAddress.setTextColor(resources.getColor(R.color.blackfordark))
            binding!!.tvheadline.setTextColor(resources.getColor(R.color.orange))
            binding!!.tvheadline.setBackgroundColor(resources.getColor(R.color.blackfordark))
        }

        if (isMapAddress == true) {
            mapUiUpdate()
        }



        binding?.rgAddressesType?.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rb_home -> addressType = "Home"
                R.id.rb_office -> addressType = "Office"
                R.id.rb_other -> addressType = "Other"
            }
        }

        binding?.cbDefault?.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                isDefault = "true"
            } else {
                isDefault = "false"
            }
        }

        val email = sharedPreferences.getEmail().toString()
        binding!!.tvPincode.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val strPinCode = binding!!.tvPincode.text.toString()
                val pinCode = s?.length ?: 0
                if (pinCode == 6) {
                    viewmodel.PinCodeResponse(email, strPinCode)
                    pinCodeResponse()
                } else {
                    binding?.tvPincode!!.error = "Enter Valid Pin code"
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })

        binding?.btnAddAddress?.setOnClickListener {

            val customerName = binding?.tvCustomerName!!.text.toString()
            val customerphone = binding?.tvCustomerNumber?.text.toString().trim()
            val apartmentText = binding?.tvHouseNo!!.text.toString().trim()
            val landmarkText = binding?.tvLandmark!!.text.toString().trim()
            val cityText = binding?.tvCity!!.text.toString().trim()
            val pinText = binding?.tvPincode!!.text.toString().trim()
            val state = binding?.etState!!.text.toString().trim()

            if (addressType.isNullOrEmpty()) {
                addressType = "Home"
            }

            if (isEditAddress == false) {
                if (validateFields()) {
                    Log.e(
                        "TAG",
                        "add address $customerName, $customerphone,  $apartmentText, $landmarkText, $cityText" +
                                "$pinText, $customerName, $addressType, $state, $isDefault",
                    )
                    strAddress = "$apartmentText, $landmarkText, $cityText, $state ,$pinText"
                    val address = "$cityText,$pinText"
                    val email = sharedPreferences.getEmail().toString()
                    viewmodel.AddAddressResponse(
                        email,
                        addressType.toString(),
                        strAddress.toString(),
                        isDefault.toString(),
                        apartmentText,
                        landmarkText,
                        cityText,
                        state,
                        pinText,
                        customerName,
                        customerphone
                    )
                    initObserver()
                    if (isDefault == "true") {
                        sharedPreferences.setAddress(address)
                    }
                }
            } else {
                // edit text
                if (validateFields()) {
                    Log.e(
                        "TAG",
                        "edit address $customerName, $customerphone,  $apartmentText, $landmarkText, $cityText" +
                                "$pinText, $customerName, $addressType, $state, $isDefault, $addressId",
                    )
                    strAddress = "$apartmentText, $landmarkText, $cityText, $state ,$pinText"
                    val address = "$cityText,$pinText"
                    val email = sharedPreferences.getEmail().toString()
                    viewmodel.UpdateAddressResponse(
                        email,
                        addressId.toString(),
                        addressType.toString(),
                        strAddress.toString(),
                        isDefault.toString(),
                        apartmentText,
                        landmarkText,
                        cityText,
                        state,
                        pinText,
                        customerName,
                        customerphone,
                    )
                    updateAddressResponse()
                    if (isDefault == "true") {
                        sharedPreferences.setAddress(address)
                    }
                }
            }
        }
    }

    private fun pinCodeResponse() {
        viewmodel.pinCodeResponse.observe(viewLifecycleOwner) { pinCodeResp ->
            if (pinCodeResp?.deliverable == true) {
                Log.e("AddAddressResp", "AddAddressResp $pinCodeResp")
            } else {
                customToast(
                    requireContext(),
                    "currently, We are not available in that area, will come soon",
                    R.drawable.ic_info
                )
            }
        }
    }

    private fun mapUiUpdate() {
        address = arguments?.getString("address")
        appartment = arguments?.getString("appartment")
        landmark = arguments?.getString("landmark")
        val city = arguments?.getString("city")
        val state = arguments?.getString("state")
        val pin = arguments?.getString("pin")
        val latitude = arguments?.getString("latitude")
        val longitude = arguments?.getString("longitude")
        // Log.e("AddressListFragment", "mapAddress: $address, $appartment, $landmark", )


        if (address.isNullOrEmpty() && landmark.isNullOrEmpty()) {
            binding?.tvHouseNo?.setText("")
            binding?.tvLandmark?.setText(" ")
        } else {
            binding?.tvHouseNo?.setText("$appartment")
            binding?.tvLandmark?.setText("$landmark")
        }
        binding?.tvCity?.setText(city)
        binding?.tvPincode?.setText(pin)
        binding?.etState?.setText(state)

    }

    private fun initObserver() {
        viewmodel.addAddressResponse.observe(viewLifecycleOwner) { addAddressResp ->
//            LoadingDialog.dismissProgressDialog()
            if (addAddressResp?.is_success == true) {
                Log.e("AddAddressResp", "AddAddressResp $addAddressResp")
                customToast(requireContext(), "Address Submitted", R.drawable.success_toast_icon)
                dismiss()
                onBottomSheetCloseListener.onBottomSheetClose()
                clearData()
            } else {
                customToast(requireContext(), "${addAddressResp?.message}", R.drawable.ic_info)

            }
        }
    }

    private fun updateAddressResponse() {
        viewmodel.updatedAddressResponse.observe(viewLifecycleOwner) { updateAddressResp ->
//            LoadingDialog.dismissProgressDialog()
            if (updateAddressResp?.is_success == true) {
                Log.e("UpdateAddressResp", "UpdateAddressResp $updateAddressResp")
                customToast(
                    requireContext(),
                    "Update Address Successful",
                    R.drawable.success_toast_icon
                )
                if (isDefault == "true") {
                    viewmodel.DefaultAddressResponse(
                        sharedPreferences.getEmail()!!,
                        addressId.toString()
                    )
                    defaultResponse()
                }
                dismiss()
                onBottomSheetCloseListener.onBottomSheetClose()
                clearData()
            } else {
                customToast(requireContext(), "${updateAddressResp?.message}", R.drawable.ic_info)
            }
        }
    }

    private fun defaultResponse() {
        viewmodel.defaultAddressResponse.observe(viewLifecycleOwner) { defaultAddressResp ->
//            LoadingDialog.dismissProgressDialog()
            if (defaultAddressResp?.is_success == true) {
                Log.e("defaultAddressResp", "defaultAddressResp $defaultAddressResp")
                onBottomSheetCloseListener.onBottomSheetClose()
                customToast(requireContext(), "Default Address", R.drawable.success_toast_icon)
            } else {
                customToast(requireContext(), "${defaultAddressResp?.message}", R.drawable.ic_info)

            }
        }
    }

    override fun onResume() {
        super.onResume()
        editAddress()
        mapUiUpdate()
    }

    private fun editAddress() {
        addressId = arguments?.getString("address_id")
        val customerName = arguments?.getString("customer_name")
        val customerPhone = arguments?.getString("customer_phone")
        val customerAddress = arguments?.getString("address")
        val editIsDefault = arguments?.getBoolean("is_default")
        val editAddressType = arguments?.getString("addressType")
        val appartment = arguments?.getString("appartment")
        val landmark = arguments?.getString("landmark")
        val city = arguments?.getString("city")
        val state = arguments?.getString("state")
        val pin = arguments?.getString("pin")

        binding?.tvCustomerName?.setText(customerName)
        binding?.tvCustomerNumber?.setText(customerPhone)
        binding?.tvHouseNo?.setText(appartment)
        binding?.tvLandmark?.setText(landmark)
        binding?.tvCity?.setText(city)
        binding?.tvPincode?.setText(pin)
        binding?.etState?.setText(state)
        addressType = editAddressType
        isDefault = editIsDefault.toString()

        if (isDefault == "true") {
            binding?.cbDefault?.isChecked = true
            binding?.cbDefault?.isClickable = false
        } else {
            binding?.cbDefault?.isChecked = false
        }

        when (addressType) {
            "Home" -> {
                binding?.rbHome?.isChecked = true
            }

            "Office" -> {
                binding?.rbOffice?.isChecked = true
            }

            "Other" -> {
                binding?.rbOther?.isChecked = true
            }

            else -> {
                binding?.rbHome?.isChecked = true
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        // Clear EditText content when the dialog is dismissed
        clearData()
    }

    fun clearData() {
        binding?.tvCustomerName!!.text?.clear()
        binding?.tvCustomerNumber!!.text?.clear()
        binding?.tvHouseNo!!.text?.clear()
        binding?.tvLandmark!!.text?.clear()
        binding?.tvCity!!.text?.clear()
        binding?.tvPincode!!.text?.clear()
        binding?.rbHome?.isChecked = true
        binding?.cbDefault?.isChecked = false
    }

    private fun validateFields(): Boolean {
        val phoneNumberPattern: Pattern = Pattern.compile("^[6-9]\\d{9}\$")
        val phoneNumber = binding?.tvCustomerNumber?.text.toString().trim()
        val fullNameText = binding?.tvCustomerName!!.text.toString().trim()
        val apartmentText = binding?.tvHouseNo!!.text.toString().trim()
        val landmarkText = binding?.tvLandmark!!.text.toString().trim()
        val cityText = binding?.tvCity!!.text.toString().trim()
        val stateText = binding?.etState!!.text.toString().trim()
        val pinText = binding?.tvPincode!!.text.toString().trim()
        val selectedRadioButtonId = binding?.rgAddressesType?.checkedRadioButtonId

        if (fullNameText.toString().isNullOrEmpty()) {
            binding?.tvCustomerName!!.error = "Invalid full name format"
            return false
        }
        if (!phoneNumberPattern.matcher(phoneNumber).matches()) {
            binding?.tvCustomerNumber?.error = "Invalid phone number format (XXX-XXX-XXXX)"
            return false
        }
        if (apartmentText.toString().isNullOrEmpty()) {
            binding?.tvHouseNo!!.error = "Enter the apartment"
            return false
        }
        if (landmarkText.toString().isNullOrEmpty()) {
            binding?.tvLandmark!!.error = "Enter the landmark"
            return false
        }
        if (cityText.toString().isNullOrEmpty()) {
            binding?.tvCity!!.error = "Enter the city"
            return false
        }
        if (pinText.toString().isNullOrEmpty()) {
            binding?.tvPincode!!.error = "Enter the pincode"
            return false
        }

        if (stateText.toString().isNullOrEmpty()) {
            binding?.etState!!.error = "Enter the pincode"
            return false
        }

        if (selectedRadioButtonId == -1) {
            customToast(requireContext(), "Please select an option", R.drawable.ic_info)
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
