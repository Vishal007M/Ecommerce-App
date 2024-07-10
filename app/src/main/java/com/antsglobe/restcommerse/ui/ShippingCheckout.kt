package com.antsglobe.restcommerse.ui

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.antsglobe.restcommerse.R
import com.antsglobe.restcommerse.Utils.PreferenceManager
import com.antsglobe.restcommerse.adapter.AddressListAdaptor
import com.antsglobe.restcommerse.databinding.FragmentShippingCheckoutBinding
import com.antsglobe.restcommerse.model.Response.AddressList
import com.antsglobe.restcommerse.network.RetrofitClient
import com.antsglobe.restcommerse.ui.bottomSheet.AddressDetailsBottomSheet
import com.antsglobe.restcommerse.viewmodel.AddressListViewModel
import com.antsglobe.restcommerse.viewmodel.ViewModelFactory


class ShippingCheckout : Fragment(), AddressListAdaptor.OnClickDeleteAddressListener,
    AddressListAdaptor.OnClickEditAddressListener,
    AddressListAdaptor.OnClickDefaultAddressListener {

    private var defaultPin: String? = null
    private var defaultID: String? = null
    private var binding: FragmentShippingCheckoutBinding? = null
    private lateinit var viewmodel: AddressListViewModel
    private lateinit var sharedPreferences: PreferenceManager
    private lateinit var addressListAdapter: AddressListAdaptor
    private var items: ArrayList<AddressList?>? = null
    val bottomSheetFragment = AddressDetailsBottomSheet()

    private var cartValueAmount: String = ""
    private var couponValueAmount: Double = 0.0
    private var strCouponName: String = ""

    private var isMap: Boolean? = false
    private var address: String? = ""
    private var appartment: String? = ""
    private var landmark: String? = ""
    private var city: String? = ""
    private var state: String? = ""
    private var pin: String? = ""
    private var latitude: String? = ""
    private var longitude: String? = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            isMap = it.getBoolean("IsMap")
            address = it.getString("address")
            appartment = it.getString("appartment")
            landmark = it.getString("landmark")
            city = it.getString("city")
            state = it.getString("state")
            pin = it.getString("pin")
            latitude = it.getString("latitude")
            longitude = it.getString("longitude")
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentShippingCheckoutBinding.inflate(inflater, container, false)
        viewmodel = ViewModelProvider(
            this, ViewModelFactory(RetrofitClient.apiService)
        )[AddressListViewModel::class.java]
        sharedPreferences = PreferenceManager(requireContext())
        viewmodel.getAddressResponse(sharedPreferences.getEmail()!!)

        strCouponName = arguments?.getString("CouponName").toString()
        cartValueAmount = arguments?.getString("Cart_value").toString()
        couponValueAmount = arguments?.getDouble("Coupon_Discount") ?: 0.0
        setOnBackPressed()
        return binding!!.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding!!.addNewAddress.setOnClickListener {

            fetchCurrentLocation()

            /*  val bundle = Bundle().apply {
                  putBoolean("is_editAddress", false)
              }
              bottomSheetFragment.arguments = bundle
              bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.tag)
  */
        }
        bottomSheetFragment.setOnBottomSheetCloseListener(object :
            AddressDetailsBottomSheet.OnBottomSheetCloseListener {
            override fun onBottomSheetClose() {
                viewmodel.getAddressResponse(sharedPreferences.getEmail()!!)
            }
        })


        binding!!.back.setOnClickListener {
//            findNavController().navigate(R.id.shipping_to_My_cart)
            val intent = Intent(activity, HomeActivity::class.java)
            startActivity(intent)
        }

        if (sharedPreferences.getMode() == true){
            binding!!.background.setBackgroundColor(resources.getColor(R.color.blackfordark))
            binding!!.newadd.setTextColor(resources.getColor(R.color.whitefordark))
            binding!!.back.setBackgroundResource(R.drawable.profile_round_corner_bg_dark_wish)
            binding!!.back.backgroundTintList= ColorStateList.valueOf(Color.parseColor("#1F201D"))
            binding!!.next.setTextColor(Color.BLACK)

        }
        if (isMap == true) {
            val bundle = Bundle().apply {
                putBoolean("IsMap", true)
                putString("address", address)
                putString("appartment", appartment)
                putString("landmark", landmark)
                putString("city", city)
                putString("state", state)
                putString("pin", pin)
                putString("latitude", latitude)
                putString("longitude", longitude)
            }
            bottomSheetFragment.arguments = bundle
            bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.tag)
        }

        viewmodel.getAddressListResponse.observe(viewLifecycleOwner) { AddressListResp ->
            binding?.llMainScreen?.visibility = View.VISIBLE
            binding?.llLoadingScreen?.visibility = View.GONE

            Log.e("GetAddressListResp", "onCreateView: $AddressListResp")

            if (AddressListResp!!.isEmpty() && AddressListResp.size >= 0) {
                binding?.llMainScreen?.visibility = View.GONE
                binding?.llEmptyScreen?.visibility = View.VISIBLE
            } else {
                binding?.llMainScreen?.visibility = View.VISIBLE
                binding?.llEmptyScreen?.visibility = View.GONE
            }

            items?.addAll(AddressListResp!!)
            addressListAdapter = AddressListAdaptor(requireContext(), AddressListResp!!)
            binding?.rvAddresslist?.layoutManager = LinearLayoutManager(context)
            binding?.rvAddresslist?.adapter = addressListAdapter
            addressListAdapter.setOnDeleteAddressListener(this)
            addressListAdapter.setOnEditAddressListener(this)
            addressListAdapter.setOnDefaultAddressListener(this)

            for (countLoop in AddressListResp!!) {
                val defaultStatus = countLoop?.is_default
                if (defaultStatus == true) {
                    defaultID = countLoop.id.toString()
                    defaultPin = countLoop.pin
                }
            }
        }

        binding!!.next.setOnClickListener {
            if (defaultPin.isNullOrEmpty()) {
                customToast(
                    requireContext(),
                    "First add your address to make a payment.",
                    R.drawable.ic_info
                )
            } else {
                val bundle = Bundle()
                bundle.putString("DefaultId", defaultID)
                bundle.putString("DefaultPin", defaultPin)
                /* bundle.putString("CouponName", strCouponName)
                 bundle.putString("Cart_value", cartValueAmount)
                 bundle.putDouble("Coupon_Discount", couponValueAmount)*/
                findNavController().navigate(
                    R.id.action_shippingCheckout_to_paymentCheckout,
                    bundle
                )
            }
        }
    }


    override fun onDeleteAddressClick(addressId: String, isDefault: Boolean) {

        if (isDefault == true) {
            customToast(
                requireContext(), "You cannot delete this address. Please change your" +
                        " default address & try it delete!", R.drawable.ic_info
            )
        } else {
            onDeleteDialog("Do you really want to delete this Address?", addressId)
        }
    }

    private fun deleteResponse() {
        viewmodel.deleteAddressResponse.observe(viewLifecycleOwner) { deleteAddressResp ->
//            LoadingDialog.dismissProgressDialog()
            if (deleteAddressResp?.is_success == true) {
                Log.e("deleteAddressResp", "deleteAddressResp $deleteAddressResp")
                viewmodel.getAddressResponse(sharedPreferences.getEmail()!!)
                customToast(
                    requireContext(),
                    "Address has been deleted successfully",
                    R.drawable.success_toast_icon
                )
            } else {
                customToast(requireContext(), "${deleteAddressResp?.message}", R.drawable.ic_info)

            }
        }
    }

    private fun onDeleteDialog(title: String, addressId: String) {

        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.address_custom_layout)

        val body = dialog.findViewById(R.id.tv_heading) as TextView
        body.text = title

        val yesBtn = dialog.findViewById(R.id.btn_yes) as Button
        yesBtn.setOnClickListener {
            dialog.dismiss()
            viewmodel.DeleteAddressResponse(sharedPreferences.getEmail()!!, addressId)
            deleteResponse()
        }

        val noBtn = dialog.findViewById(R.id.btn_no) as Button
        noBtn.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    override fun onEditAddressClick(
        addressId: String,
        name: String,
        phone: String,
        addressType: String,
        address: String,
        isDefault: Boolean,
        appartment: String,
        landmark: String,
        city: String,
        state: String,
        pin: String
    ) {
        val bundle = Bundle().apply {
            putBoolean("is_editAddress", true)
            putString("address_id", addressId)
            putString("customer_name", name)
            putString("customer_phone", phone)
            putString("address", address)
            putString("addressType", addressType)
            putBoolean("is_default", isDefault)
            putString("appartment", appartment)
            putString("landmark", landmark)
            putString("city", city)
            putString("state", state)
            putString("pin", pin)
        }
        bottomSheetFragment.arguments = bundle
        bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.tag)

    }

    override fun onDefaultAddressClick(addressId: String, address: String) {
        viewmodel.DefaultAddressResponse(sharedPreferences.getEmail()!!, addressId)
        defaultResponse()
        sharedPreferences.setAddress(address)
    }

    private fun defaultResponse() {
        viewmodel.defaultAddressResponse.observe(viewLifecycleOwner) { defaultAddressResp ->
//            LoadingDialog.dismissProgressDialog()
            if (defaultAddressResp?.is_success == true) {
                Log.e("defaultAddressResp", "defaultAddressResp $defaultAddressResp")
                viewmodel.getAddressResponse(sharedPreferences.getEmail()!!)
                customToast(requireContext(), "Set As Default", R.drawable.success_toast_icon)
            } else {
                customToast(requireContext(), "${defaultAddressResp?.message}", R.drawable.ic_info)

            }
        }
    }

    private fun setOnBackPressed() {
        // Initialize onBackPressedCallback
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(activity, HomeActivity::class.java)
                startActivity(intent)

            }
        }

        // Add the onBackPressedCallback to the activity's onBackPressedDispatcher
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            onBackPressedCallback
        )
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1000 -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchCurrentLocation()
            }
        }
    }

    private fun fetchCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                1000
            )
            return
        }
        //  Toast.makeText(context, "permission granted", Toast.LENGTH_SHORT).show()
        if (!isGPSEnabled()) {
            enableGPS()
        } else {
            val bundle = Bundle()
            bundle.putBoolean("isShippingCheckout", true)
            findNavController().navigate(R.id.action_shippingCheckout_to_maps_fragment, bundle)
        }
    }

    private fun isGPSEnabled(): Boolean {
        val locationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun enableGPS() {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
    }
}