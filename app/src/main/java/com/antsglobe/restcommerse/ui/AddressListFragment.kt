package com.antsglobe.restcommerse.ui

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.antsglobe.restcommerse.R
import com.antsglobe.restcommerse.Utils.PreferenceManager
import com.antsglobe.restcommerse.adapter.AddressListAdaptor
import com.antsglobe.restcommerse.databinding.FragmentAddressListBinding
import com.antsglobe.restcommerse.model.Response.AddressList
import com.antsglobe.restcommerse.network.RetrofitClient
import com.antsglobe.restcommerse.ui.bottomSheet.AddressDetailsBottomSheet
import com.antsglobe.restcommerse.viewmodel.AddressListViewModel
import com.antsglobe.restcommerse.viewmodel.ViewModelFactory
import java.util.Locale


class AddressListFragment : Fragment(), AddressListAdaptor.OnClickDeleteAddressListener,
    AddressListAdaptor.OnClickEditAddressListener,
    AddressListAdaptor.OnClickDefaultAddressListener {
    private var isMap: Boolean? = false
    private var binding: FragmentAddressListBinding? = null
    private lateinit var viewmodel: AddressListViewModel
    private lateinit var sharedPreferences: PreferenceManager
    private lateinit var addressListAdapter: AddressListAdaptor
    val bottomSheetFragment = AddressDetailsBottomSheet()
    private var address: String? = ""
    private var appartment: String? = ""
    private var landmark: String? = ""
    private var city: String? = ""
    private var state: String? = ""
    private var pin: String? = ""
    private var latitude: String? = ""
    private var longitude: String? = ""
    private var swipeRefreshLayout: SwipeRefreshLayout? = null

    private var mList = ArrayList<AddressList>()

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
    ): View {

        binding = FragmentAddressListBinding.inflate(inflater, container, false)
        viewmodel = ViewModelProvider(this, ViewModelFactory(RetrofitClient.apiService))[AddressListViewModel::class.java]
        sharedPreferences = PreferenceManager(requireContext())
        viewmodel.getAddressResponse(sharedPreferences.getEmail()!!)

        binding?.llLoadingScreen?.visibility = View.VISIBLE
        binding?.llMainScreen?.visibility = View.GONE
        return binding!!.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addressListAdapter = AddressListAdaptor(requireContext(), emptyList())
        binding?.rvAddresslist?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = addressListAdapter
        }

        binding!!.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
        binding!!.addNewAddress.setOnClickListener {
            fetchCurrentLocation()
            /* val bundle = Bundle().apply {
                 putBoolean("is_editAddress", false)
             }
             bottomSheetFragment.arguments = bundle
             bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.tag)*/
        }
        if(sharedPreferences.getMode() == true){
            binding!!.logoImageHome.setTextColor(resources.getColor(R.color.blackfordark))
            binding!!.searchtv.setTextColor(resources.getColor(R.color.whitefordark))
            //binding!!.searchProduct.setHintTextColor(Color.GRAY)
            //binding!!.searchProduct.setTextColor(resources.getColor(R.color.whitefordark))
            binding!!.usemy.setTextColor(Color.GRAY)
            binding!!.noaddress.setTextColor(Color.WHITE)
            binding!!.backButton.setImageResource(R.drawable.ic_baseline_arrow_back_24_dark)
            binding!!.locationimg.setImageResource(R.drawable.ic_my_location_dark)
            binding!!.llsearchet.setBackgroundResource(R.drawable.profile_round_corner_bg_dark_wish)
            binding!!.fullscreen.setBackgroundColor(resources.getColor(R.color.blackfordark))
            binding!!.lladd.setBackgroundColor(Color.parseColor("#1F201D"))
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

        binding!!.llGoggleMap.setOnClickListener {
            fetchCurrentLocation()
        }

        bottomSheetFragment.setOnBottomSheetCloseListener(object :
            AddressDetailsBottomSheet.OnBottomSheetCloseListener {
            override fun onBottomSheetClose() {
                viewmodel.getAddressResponse(sharedPreferences.getEmail()!!)
            }
        })

        getAddressInitObserver()

        binding!!.searchProduct.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    filterList(newText.trim())
                }
                return true
            }
        })

        swipeRefreshLayout = binding?.spRefresher
        swipeRefreshLayout?.setOnRefreshListener {
            fetchData()
        }
    }

    private fun fetchData() {
        Handler(Looper.getMainLooper()).postDelayed({
            viewmodel.getAddressResponse(sharedPreferences.getEmail()!!)
            getAddressInitObserver()
        }, 2000)
    }

    private fun getAddressInitObserver() {
        viewmodel.getAddressListResponse.observe(viewLifecycleOwner) { AddressListResp ->
            Log.e("GetAddressListResp", "onCreateView: $AddressListResp")
            mList.clear()
            swipeRefreshLayout?.isRefreshing = false
            binding?.llMainScreen?.visibility = View.VISIBLE
            binding?.llLoadingScreen?.visibility = View.GONE

            if (AddressListResp.isEmpty() && AddressListResp.size >= 0) {
                binding?.llMainScreen?.visibility = View.GONE
                binding?.llEmptyScreen?.visibility = View.VISIBLE
            } else {
                binding?.llMainScreen?.visibility = View.VISIBLE
                binding?.llEmptyScreen?.visibility = View.GONE
            }
//            addressListAdapter = AddressListAdapto r(requireContext(), AddressListResp!!)
//            binding?.rvAddresslist?.layoutManager = LinearLayoutManager(context)
//            binding?.rvAddresslist?.adapter = addressListAdapter
//            addressListAdapter.notifyDataSetChanged()

            for (addressListResp in AddressListResp){
                if (AddressListResp.size == 1){
                    if (addressListResp.is_default == false){
                        viewmodel.DefaultAddressResponse(sharedPreferences.getEmail()!!, addressListResp.id.toString())
                        defaultResponse()
                    }
                }
            }
            addressListAdapter.updateStudentList(AddressListResp)
            mList.addAll(AddressListResp)
            addressListAdapter.setOnDeleteAddressListener(this)
            addressListAdapter.setOnEditAddressListener(this)
            addressListAdapter.setOnDefaultAddressListener(this)
        }
    }

    private fun filterList(query: String?) {
        if (query != null) {
            val filteredList = ArrayList<AddressList>()
            val lowerCaseQuery = query.lowercase(Locale.ROOT)

            for (i in mList) {
                val pinLowerCase = i.pin?.lowercase(Locale.ROOT)
                val cityLowerCase = i.city?.lowercase(Locale.ROOT)
                val stateLowerCase = i.state?.lowercase(Locale.ROOT)
                val cNameLowerCase = i.customer_name?.lowercase(Locale.ROOT)
                val cNoLowerCase = i.customer_mobno?.lowercase(Locale.ROOT)
                val addTypeLowerCase = i.address_type?.lowercase(Locale.ROOT)
//                i.is_default? = null

                binding?.llMainScreen!!.visibility = View.VISIBLE
                binding?.llEmptyScreen!!.visibility = View.GONE
                binding?.addAtleast!!.visibility = View.VISIBLE

                if (pinLowerCase != null && cityLowerCase != null
                    && stateLowerCase != null && cNameLowerCase != null
                    && cNoLowerCase != null && addTypeLowerCase != null) {
                    if (pinLowerCase.contains(lowerCaseQuery) || cityLowerCase.contains(lowerCaseQuery)
                        || stateLowerCase.contains(lowerCaseQuery) || cNameLowerCase.contains(lowerCaseQuery)
                        || cNoLowerCase.contains(lowerCaseQuery) || addTypeLowerCase.contains(lowerCaseQuery)) {
                        filteredList.add(i)
                    }
                }

                if (filteredList.isEmpty()) {
                    binding?.llMainScreen!!.visibility = View.GONE
                    binding?.llEmptyScreen!!.visibility = View.VISIBLE
                    binding?.addAtleast!!.visibility = View.GONE
                } else {
                    addressListAdapter.updateStudentList(filteredList)
                }
            }
        }
    }

//    private fun filterList(query: String?) {
//        query?.let { q ->
//
//            val filteredList = mList.filter { address ->
//                address.run {
//
//                            address_type?.toLowerCase(Locale.ROOT)?.contains(q.toLowerCase(Locale.ROOT)) == true ||
//                            appartment?.toLowerCase(Locale.ROOT)?.contains(q.toLowerCase(Locale.ROOT)) == true ||
//                            city?.toLowerCase(Locale.ROOT)?.contains(q.toLowerCase(Locale.ROOT)) == true ||
//                            customer_mobno?.toLowerCase(Locale.ROOT)?.contains(q.toLowerCase(Locale.ROOT)) == true ||
//                            customer_name?.toLowerCase(Locale.ROOT)?.contains(q.toLowerCase(Locale.ROOT)) == true ||
//                            landmark?.toLowerCase(Locale.ROOT)?.contains(q.toLowerCase(Locale.ROOT)) == true ||
//                            pin?.toLowerCase(Locale.ROOT)?.contains(q.toLowerCase(Locale.ROOT)) == true ||
//                            state?.toLowerCase(Locale.ROOT)?.contains(q.toLowerCase(Locale.ROOT)) == true
//                }
//            }
//
//            if (filteredList.isEmpty()) {
//                binding?.llMainScreen?.visibility = View.GONE
//                binding?.llEmptyScreen?.visibility = View.VISIBLE
//                binding?.addAtleast?.visibility = View.GONE
//            } else {
//                binding?.llMainScreen?.visibility = View.VISIBLE
//                binding?.llEmptyScreen?.visibility = View.GONE
//                binding?.addAtleast?.visibility = View.VISIBLE
//                addressListAdapter.updateStudentList(filteredList)
//            }
//        }
//    }

    override fun onDeleteAddressClick(addressId: String, isDefault: Boolean) {
        if (isDefault == true) {
            customToast(
                requireContext(), "You cannot delete this address. Please change your" +
                        " default address & try it delete!", R.drawable.ic_info)
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
                customToast(requireContext(), "Address has been deleted successfully", R.drawable.success_toast_icon)
            } else {
                customToast(requireContext(), "${deleteAddressResp?.message}", R.drawable.ic_info)
            }
        }
    }

    private fun onDeleteDialog(title: String, addressId: String) {

        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)

        if (sharedPreferences.getMode() == true){
            dialog.setContentView(R.layout.address_custom_layout_dark)
        }
        else{
            dialog.setContentView(R.layout.address_custom_layout)
        }

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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
         when (requestCode) {
            1000 -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchCurrentLocation()
            }
        }
    }

    private fun fetchCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(requireActivity(),
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 1000)
            return
        }
        //  Toast.makeText(context, "permission granted", Toast.LENGTH_SHORT).show()
        if (!isGPSEnabled()) {
            enableGPS()
        } else {
            findNavController().navigate(R.id.action_AddressListFragment_to_maps_fragment)
        }
    }

    private fun isGPSEnabled(): Boolean {
        val locationManager = requireContext().getSystemService(LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun enableGPS() {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
    }
}