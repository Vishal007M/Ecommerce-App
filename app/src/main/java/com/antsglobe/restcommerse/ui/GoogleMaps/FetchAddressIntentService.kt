package com.antsglobe.restcommerse.ui.GoogleMaps

import android.app.IntentService
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.os.ResultReceiver
import android.text.TextUtils
import android.util.Log
import com.antsglobe.restcommerse.R
import java.io.IOException
import java.util.Locale

class FetchAddressIntentService : IntentService("FetchAddressIntentService") {
    val TAG = FetchAddressIntentService::class.java.simpleName
    protected var receiver: ResultReceiver? = null

    private var state = ""
    private var city = ""
    private var area = ""
    private var pincode = ""

    private var houseNumber = ""
    private var street = ""
    private var landmark = ""

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */

    override fun onHandleIntent(intent: Intent?) {
        if (intent == null) {
            return
        }
        receiver = intent.getParcelableExtra(SimplePlacePicker.RECEIVER)
        val latitude = intent.getDoubleExtra(SimplePlacePicker.LOCATION_LAT_EXTRA, -1.0)
        val longitude = intent.getDoubleExtra(SimplePlacePicker.LOCATION_LNG_EXTRA, -1.0)
        val language = intent.getStringExtra(SimplePlacePicker.LANGUAGE)
        var errorMessage = ""
        var addresses: List<Address>? = null
        val locale = Locale(language)
        val geocoder = Geocoder(this, locale)
        try {
            addresses = geocoder.getFromLocation(
                latitude,
                longitude,
                5
            )
        } catch (ioException: IOException) {
            // Catch network or other I/O problems.
            errorMessage = getString(R.string.service_not_available)
            Log.e(TAG, errorMessage, ioException)
        }
        // Handle case where no address was found.
        if (addresses == null || addresses.size == 0) {
            if (errorMessage.isEmpty()) {
                errorMessage = "No address found"
                Log.e(TAG, errorMessage)
            }
            deliverResultToReceiver(SimplePlacePicker.FAILURE_RESULT, errorMessage)
        } else {
            val result = StringBuilder()
            val address = addresses[0]

            // Fetch the address lines using getAddressLine,
            // join them, and send them to the thread.
            for (i in 0..address.maxAddressLineIndex) {
                if (i == address.maxAddressLineIndex) {
                    result.append(address.getAddressLine(i))
                } else {
                    result.append(address.getAddressLine(i) + ",")
                }
            }



            try {
                area = address.subLocality ?: " "
                state = address.adminArea ?: " "
                city = address.locality ?: " "
                pincode = address.postalCode ?: " "
            } catch (e: Exception) {
                println("in catch ${e.message}")
            }

            Log.i(TAG, "address found")
            houseNumber =
                if (address.featureName != null && !TextUtils.isEmpty(address.featureName)) {
                    address.featureName
                } else {
                    if (address.subThoroughfare != null && !TextUtils.isEmpty(address.subThoroughfare)) {
                        address.subThoroughfare
                    } else {
                        ""
                    }
                }
            if (address.thoroughfare != null && !TextUtils.isEmpty(address.thoroughfare)) {
                street = address.thoroughfare
                landmark = address.thoroughfare + ", " + area
            } else {
                street = ""
                landmark = ""
            }
            Log.i(TAG, "house number: $houseNumber")
            Log.i(TAG, "street: $street")
            Log.i(TAG, "landmark: $landmark")
            Log.i(TAG, "name: " + address.locality)
            Log.i(TAG, "address : $result")
            Log.i(TAG, "state : $state")
            Log.i(TAG, "city : $city")
            Log.i(TAG, "area : $area")
            Log.i(TAG, "pincode : $pincode")
            deliverResultToReceiver(SimplePlacePicker.SUCCESS_RESULT, result.toString())
        }
    }

    private fun deliverResultToReceiver(resultCode: Int, message: String) {
        val bundle = Bundle()
        bundle.putString(SimplePlacePicker.RESULT_DATA_KEY, message)
        bundle.putString(SimplePlacePicker.SELECTED_HOUSE_NUMBER, houseNumber)
        bundle.putString(SimplePlacePicker.SELECTED_STREET, street)
        bundle.putString(SimplePlacePicker.SELECTED_LANDMARK, landmark)
        bundle.putString(SimplePlacePicker.SELECTED_STATE, state)
        bundle.putString(SimplePlacePicker.SELECTED_CITY, city)
        bundle.putString(SimplePlacePicker.SELECTED_AREA, area)
        bundle.putString(SimplePlacePicker.SELECTED_PIN_CODE, pincode)
        receiver!!.send(resultCode, bundle)
    }

}