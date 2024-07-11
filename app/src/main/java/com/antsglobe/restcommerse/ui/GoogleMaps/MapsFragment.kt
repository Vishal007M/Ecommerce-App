package com.antsglobe.restcommerse.ui.GoogleMaps

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.antsglobe.restcommerse.R
import com.antsglobe.restcommerse.Utils.PreferenceManager
import com.antsglobe.restcommerse.databinding.FragmentMapsBinding
import com.antsglobe.restcommerse.network.RetrofitClient
import com.antsglobe.restcommerse.viewmodel.AddressViewModel
import com.antsglobe.restcommerse.viewmodel.ViewModelFactory
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import java.util.Arrays

class MapsFragment : Fragment(), OnMapReadyCallback {

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * In this case, we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to
     * install it inside the SupportMapFragment. This method will only be triggered once the
     * user has installed Google Play services and returned to the app.
     */

    private var binding: FragmentMapsBinding? = null
    private lateinit var sharedPreferences: PreferenceManager
    private lateinit var viewmodel: AddressViewModel
    private var isShippingCheckout: Boolean? = false
    private var isHomeFragment: Boolean? = false

    private var mMap: GoogleMap? = null
    var currentMarker: Marker? = null
    var currentLocation: Location? = null
    var fusedLocationProviderClient: FusedLocationProviderClient? = null

    private var addressResultCode: Int = 0
    private var dailogOpen: Boolean = false
    private var addressOutput: String? = null
    private var isSupportedArea = false
    private var mSupportedArea = arrayOf<String>()
    private var mApiKey = ""
    private var placesClient: PlacesClient? = null
    private var mapView: View? = null
    private var mCountry = ""
    private var predictionList: List<AutocompletePrediction>? = null

    private var currentMarkerPosition: LatLng? = null
    private var locationCallback: LocationCallback? = null
    private val DEFAULT_ZOOM = 17f

    private var state = ""
    private val stateId = ""
    private var city = ""
    private val cityId = ""
    private var area = ""
    private val areaId = ""
    private var pincode = ""
    private var houseNumber = ""
    private var street = ""
    private var landmark = ""
    private var mLanguage = "en"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            // param1 = it.getString(ARG_PARAM1)
            isShippingCheckout = it.getBoolean("isShippingCheckout")
            isHomeFragment = it.getBoolean("isHomeFragment")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // return inflater.inflate(R.layout.fragment_maps, container, false)
        binding = FragmentMapsBinding.inflate(inflater, container, false)

        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewmodel = ViewModelProvider(
            this, ViewModelFactory(RetrofitClient.apiService)
        )[AddressViewModel::class.java]
        sharedPreferences = PreferenceManager(requireContext())



        if (sharedPreferences.getMode() == true) {
            binding!!.cvAddress.setBackgroundResource(R.drawable.profile_round_corner_bg_addresses_dark)
            binding!!.tvDisplayMarkerLocation.setTextColor(Color.WHITE)
            binding!!.submitLocationButton.setTextColor(Color.BLACK)
        }

        if (isShippingCheckout == true) {
            setOnBackPressed()
        }
        initMapsAndPlaces()
        receiveIntent()
        binding!!.submitLocationButton.setOnClickListener { v ->
            submitResultLocation()
        }
    }

    private fun receiveIntent() {
        val intent: Intent = requireActivity().intent
        if (intent.hasExtra(SimplePlacePicker.API_KEY)) {
            mApiKey = intent.getStringExtra(SimplePlacePicker.API_KEY)!!
        }
        if (intent.hasExtra(SimplePlacePicker.COUNTRY)) {
            mCountry = intent.getStringExtra(SimplePlacePicker.COUNTRY)!!
        }
        if (intent.hasExtra(SimplePlacePicker.LANGUAGE)) {
            mLanguage = intent.getStringExtra(SimplePlacePicker.LANGUAGE)!!
        }
        if (intent.hasExtra(SimplePlacePicker.SUPPORTED_AREAS)) {
            mSupportedArea = intent.getStringArrayExtra(SimplePlacePicker.SUPPORTED_AREAS)!!
        }
    }

    private fun submitResultLocation() {

        // if the process of getting address failed or this is not supported area , don't submit
        if (addressResultCode == SimplePlacePicker.FAILURE_RESULT || !isSupportedArea) {
            Toast.makeText(
                requireContext(),
                "Something went wrong! please try again.",
                Toast.LENGTH_SHORT
            )
                .show()
        } else {
            val email = sharedPreferences.getEmail().toString()
            viewmodel.PinCodeResponse(email, pincode)
            pinCodeResponse()

            /*data.putExtra(SimplePlacePicker.SELECTED_ADDRESS, addressOutput)
            data.putExtra(SimplePlacePicker.SELECTED_HOUSE_NUMBER, houseNumber)
            data.putExtra(SimplePlacePicker.SELECTED_STREET, street)
            data.putExtra(SimplePlacePicker.SELECTED_LANDMARK, landmark)
            data.putExtra(SimplePlacePicker.SELECTED_STATE, state)
            data.putExtra(SimplePlacePicker.SELECTED_CITY, city)
            data.putExtra(SimplePlacePicker.SELECTED_AREA, area)
            data.putExtra(SimplePlacePicker.SELECTED_PIN_CODE, pincode)
            data.putExtra(SimplePlacePicker.SELECTED_CITY_ID, cityId)
            data.putExtra(SimplePlacePicker.SELECTED_AREA_ID, areaId)
            data.putExtra(SimplePlacePicker.SELECTED_STATE_ID, stateId)
            data.putExtra(SimplePlacePicker.LOCATION_LAT_EXTRA, currentMarkerPosition!!.latitude)
            data.putExtra(SimplePlacePicker.LOCATION_LNG_EXTRA, currentMarkerPosition!!.longitude)
            requireActivity().setResult(Activity.RESULT_OK, data)
            requireActivity().finish()*/

        }
    }

    private fun pinCodeResponse() {
        viewmodel.pinCodeResponse.observe(viewLifecycleOwner) { pinCodeResp ->
            if (pinCodeResp?.deliverable == true) {
                Log.e("AddAddressResp", "AddAddressResp $pinCodeResp")
                val data = Bundle().apply {
                    putBoolean("IsMap", true)
                    putString("address", addressOutput)
                    putString("appartment", "$houseNumber,  $street")
                    putString("landmark", landmark)
                    putString("city", city)
                    putString("state", state)
                    putString("pin", pincode)
                    putString("latitude", "${currentMarkerPosition!!.latitude}")
                    putString("longitude", "${currentMarkerPosition!!.longitude}")
                }
                if (isHomeFragment == true) {
                    val bundle = Bundle()
                    bundle.putString("pin", pincode)
                    bundle.putString("city", city)
                    findNavController().navigate(
                        R.id.action_maps_fragment_to_HomeFragment,
                        bundle
                    )
                } else if (isShippingCheckout == true) {
                    findNavController().navigate(
                        R.id.action_maps_fragment_to_shippingCheckout,
                        data
                    )

                } else {
                    findNavController().navigate(
                        R.id.action_maps_fragment_to_AddressListFragment,
                        data
                    )
                }
            } else {
                //

                if (isHomeFragment == true) {
                    val bundle = Bundle()
                    bundle.putString("pin", pincode)
                    bundle.putString("city", city)
                    findNavController().navigate(
                        R.id.action_maps_fragment_to_HomeFragment,
                        bundle
                    )
                } else {
                    if (!dailogOpen) {
                        // Show your dialog here
                        onDeleteDialog("Currently, We are not available in that area, will come soon")
                        dailogOpen = true
                    }
                }

                /*if(dailogOpen == false){
                    val alertDialog = AlertDialog.Builder(requireContext())
                    alertDialog.setTitle("PinCode not Available")
                        .setMessage("Currently, We are not available in that area, will come soon")
                        .setPositiveButton("DOne") { dialog, _ ->
                            dialog.dismiss()
                            dailogOpen == true
                        }.create().show()
                }*/
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        mMap!!.isMyLocationEnabled = true
        //enable location button
        mMap!!.uiSettings.isMyLocationButtonEnabled = false
        mMap!!.uiSettings.isCompassEnabled = false

        //move location button to the required position and adjust params such margin
        val locationButton: View? = mapView?.findViewById<View>(Integer.parseInt("1"))?.let {
            (it.parent as View).findViewById(Integer.parseInt("2"))
        }
        (locationButton?.layoutParams as? RelativeLayout.LayoutParams)?.apply {
            addRule(RelativeLayout.ALIGN_PARENT_TOP, 0)
            addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
            setMargins(0, 0, 60, 500)
        }

        val locationRequest = LocationRequest.create().apply {
            interval = 1000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)

        val settingsClient = LocationServices.getSettingsClient(requireContext())
        val task = settingsClient.checkLocationSettings(builder.build())

//if task is successful means the gps is enabled so go and get device location amd move the camera to that location
        task.addOnSuccessListener { locationSettingsResponse -> getDeviceLocation() }

        binding!!.fabFindMyLocation.setOnClickListener { initMapsAndPlaces() }

//if task failed means gps is disabled so ask user to enable gps
        task.addOnFailureListener { e ->
            if (e is ResolvableApiException) {
                val resolvable = e
                try {
                    resolvable.startResolutionForResult(requireActivity(), 51)
                } catch (e1: IntentSender.SendIntentException) {
                    e1.printStackTrace()
                }
            }
        }

        mMap!!.setOnCameraIdleListener {
            binding!!.smallPin.visibility = View.GONE
            binding!!.progressBar.visibility = View.VISIBLE
            Log.i("TAG", "changing address")
//                ToDo : you can use retrofit for this network call instead of using services
            //hint: services is just for doing background tasks when the app is closed no need to use services to update ui
            //best way to do network calls and then update user ui is Retrofit .. consider it
            startIntentService()
        }

    }

    protected fun startIntentService() {
        currentMarkerPosition = mMap!!.cameraPosition.target
        val resultReceiver = AddressResultReceiver(Handler())
        val intent = Intent(requireContext(), FetchAddressIntentService::class.java)
        intent.putExtra(SimplePlacePicker.RECEIVER, resultReceiver)
        intent.putExtra(SimplePlacePicker.LOCATION_LAT_EXTRA, currentMarkerPosition!!.latitude)
        intent.putExtra(SimplePlacePicker.LOCATION_LNG_EXTRA, currentMarkerPosition!!.longitude)
        intent.putExtra(SimplePlacePicker.LANGUAGE, mLanguage)
        requireContext().startService(intent)
    }

    private fun updateUi() {
        binding!!.tvDisplayMarkerLocation.visibility = View.VISIBLE
        binding!!.progressBar.visibility = View.GONE
        mMap!!.clear()
        if (addressResultCode == SimplePlacePicker.SUCCESS_RESULT) {
            //check for supported area
            if (isSupportedArea(mSupportedArea)) {
                //supported
                addressOutput = addressOutput!!.replace("Unnamed Road,", "")
                addressOutput = addressOutput!!.replace("Unnamed Road،", "")
                addressOutput = addressOutput!!.replace("Unnamed Road New,", "")
                binding!!.icPin.visibility = View.VISIBLE
                isSupportedArea = true
                binding!!.tvDisplayMarkerLocation.text = addressOutput
                //  showRippleAnimationToMarker()
            } else {
                //not supported
                binding!!.icPin.visibility = View.GONE
                isSupportedArea = false
                binding!!.tvDisplayMarkerLocation.text = "This area is not supported yet"
            }
        } else if (addressResultCode == SimplePlacePicker.FAILURE_RESULT) {
            binding!!.icPin.visibility = View.GONE
            binding!!.tvDisplayMarkerLocation.text = addressOutput
        }
    }

    private fun isSupportedArea(supportedAreas: Array<String>): Boolean {
        if (supportedAreas.isEmpty())
            return true

        var isSupported = false
        for (area in supportedAreas) {
            if (addressOutput!!.contains(area)) {
                isSupported = true
                break
            }
        }
        return isSupported
    }


    private fun initMapsAndPlaces() {
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())

        //if (mApiKey != null && mApiKey.isNotEmpty())
        Places.initialize(requireContext(), "AIzaSyD_et2uhaZPPHLIydq-VvN83qs7tKapY2A")

        placesClient = Places.createClient(requireContext())
        val token = AutocompleteSessionToken.newInstance()

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        mapView = mapFragment?.view


        binding!!.searchProduct.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val predictionsRequest = FindAutocompletePredictionsRequest.builder()
                    .setCountry(mCountry)
                    .setSessionToken(token)
                    .setQuery(newText.toString())
                    .build()
                placesClient!!.findAutocompletePredictions(predictionsRequest)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val predictionsResponse = task.result
                            if (predictionsResponse != null) {
                                predictionList = predictionsResponse.autocompletePredictions
                                val suggestionsList = ArrayList<String>()
                                for (i in predictionList!!.indices) {
                                    val prediction = predictionList!![i]
                                    suggestionsList.add(prediction.getFullText(null).toString())
                                }
                                /*binding!!.searchProduct.updateLastSuggestions(suggestionsList)
                                Handler().postDelayed({
                                    if (!binding!!.searchProduct.isSuggestionsVisible) {
                                        binding!!.searchProduct.showSuggestionsList()
                                    }
                                }, 1000)*/
                            }
                        } else {
                            Log.i("TAG", "prediction fetching task unSuccessful")
                        }
                    }
                return true
            }
        })

        binding!!.searchProduct.setOnSuggestionListener(object : SearchView.OnSuggestionListener {
            override fun onSuggestionSelect(position: Int): Boolean {

                if (position >= predictionList!!.size) {
                    return true
                }
                val selectedPrediction = predictionList!![position]
                // val suggestion: String =
                // binding!!.searchProduct.getLastSuggestions().get(position).toString()
                //  materialSearchBar.setText(suggestion)
                // Handler().postDelayed({ materialSearchBar.clearSuggestions() }, 1000)
//                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
//                imm?.hideSoftInputFromWindow(
//                    materialSearchBar.getWindowToken(),
//                    InputMethodManager.HIDE_IMPLICIT_ONLY
//                )
                val placeId = selectedPrediction.placeId
                val placeFields =
                    Arrays.asList(Place.Field.LAT_LNG, Place.Field.NAME, Place.Field.ADDRESS)
                val fetchPlaceRequest = FetchPlaceRequest.builder(placeId, placeFields).build()
                placesClient!!.fetchPlace(fetchPlaceRequest)
                    .addOnSuccessListener { fetchPlaceResponse ->
                        val place = fetchPlaceResponse.place
                        Log.i(
                            "TAG",
                            "place found " + place.name + place.address
                        )
                        val latLng = place.latLng
                        if (latLng != null) {
                            val location = CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM)
                            mMap!!.animateCamera(location)
                            //showRippleAnimationToMarker()

                            //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
                        }
                        //showRippleAnimationToMarker()
                    }
                    .addOnFailureListener { e ->
                        if (e is ApiException) {
                            val apiException = e as ApiException
                            apiException.printStackTrace()
                            val statusCode = apiException.statusCode
                            Log.i(
                                "TAG",
                                "place not found" + e.message
                            )
                            Log.i(
                                "TAG",
                                "status code : $statusCode"
                            )
                        }
                    }
                return false
            }

            override fun onSuggestionClick(position: Int): Boolean {
                TODO("Not yet implemented")
            }

        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 51) {
            if (resultCode == Activity.RESULT_OK) {
                initMapsAndPlaces()
            }
        }
    }

    /**
     * is triggered whenever we want to fetch device location
     * in order to get device's location we use FusedLocationProviderClient object that gives us the last location
     * if the task of getting last location is successful and not equal to null ,
     * apply this location to mLastLocation instance and move the camera to this location
     * if the task is not successful create new LocationRequest and LocationCallback instances and update lastKnownLocation with location result
     */

    @SuppressLint("MissingPermission")
    private fun getDeviceLocation() {
        fusedLocationProviderClient!!.lastLocation
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val lastKnownLocation = task.result
                    if (lastKnownLocation != null) {
                        val coordinate =
                            LatLng(lastKnownLocation.latitude, lastKnownLocation.longitude)
                        val location = CameraUpdateFactory.newLatLngZoom(coordinate, DEFAULT_ZOOM)
                        mMap!!.animateCamera(location)

                    } else {
                        val locationRequest = LocationRequest.create().apply {
                            interval = 1000
                            fastestInterval = 5000
                            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                        }
                        locationCallback = object : LocationCallback() {
                            override fun onLocationResult(locationResult: LocationResult) {
                                super.onLocationResult(locationResult!!)
                                if (locationResult == null) {
                                    return
                                }
                                val lastLocation = locationResult.lastLocation

                                val coordinate =
                                    LatLng(lastLocation!!.latitude, lastLocation!!.longitude)
                                val location =
                                    CameraUpdateFactory.newLatLngZoom(coordinate, DEFAULT_ZOOM)
                                mMap!!.animateCamera(location)

                                // showRippleAnimationToMarker()

                                // Remove location updates in order not to continuously check location unnecessarily
                                fusedLocationProviderClient!!.removeLocationUpdates(this)
                            }
                        }
                        //  fusedLocationProviderClient!!.requestLocationUpdates(locationRequest)
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Unable to get last location",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }


    inner class AddressResultReceiver(handler: Handler) : ResultReceiver(handler) {
        override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {
            addressResultCode = resultCode
            if (resultData == null) {
                return
            }

            // Display the address string
            // or an error message sent from the intent service.
            addressOutput = resultData.getString(SimplePlacePicker.RESULT_DATA_KEY) ?: ""

            houseNumber = resultData.getString(SimplePlacePicker.SELECTED_HOUSE_NUMBER) ?: ""
            street = resultData.getString(SimplePlacePicker.SELECTED_STREET) ?: ""
            landmark = resultData.getString(SimplePlacePicker.SELECTED_LANDMARK) ?: ""
            pincode = resultData.getString(SimplePlacePicker.SELECTED_PIN_CODE) ?: ""
            state = resultData.getString(SimplePlacePicker.SELECTED_STATE) ?: ""
            city = resultData.getString(SimplePlacePicker.SELECTED_CITY) ?: ""
            area = resultData.getString(SimplePlacePicker.SELECTED_AREA) ?: ""

            updateUi()
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

    private fun setOnBackPressed() {
        // Initialize onBackPressedCallback
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

                if (isShippingCheckout == true) {
                    val data = Bundle().apply {
                        putBoolean("IsMap", false)
                    }
                    findNavController().navigate(
                        R.id.action_maps_fragment_to_shippingCheckout,
                        data
                    )

                }


            }
        }

        // Add the onBackPressedCallback to the activity's onBackPressedDispatcher
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            onBackPressedCallback
        )
    }


    private fun onDeleteDialog(title: String) {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.pin_custom_layout)
        if (sharedPreferences.getMode() == true) {
            dialog.setContentView(R.layout.pin_custom_layout_dark)
        }

        val body = dialog.findViewById(R.id.tv_heading) as TextView
        body.text = title

        /*  val yesBtn = dialog.findViewById(R.id.btn_yes) as Button
          yesBtn.setOnClickListener {
              dialog.dismiss()
              viewmodel.DeleteAddressResponse(sharedPreferences.getEmail()!!, addressId)
              deleteResponse()
          }*/

        val noBtn = dialog.findViewById(R.id.btn_done) as Button
        noBtn.setOnClickListener {
            dialog.dismiss()
            dailogOpen = false
        }

        dialog.show()
    }
}