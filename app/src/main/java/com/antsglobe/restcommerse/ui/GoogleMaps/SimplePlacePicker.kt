package com.antsglobe.restcommerse.ui.GoogleMaps

interface SimplePlacePicker {

    companion object {
        const val PACKAGE_NAME = "com.poona.agrocart"
        const val RECEIVER = "$PACKAGE_NAME.RECEIVER"
        const val RESULT_DATA_KEY = "$PACKAGE_NAME.RESULT_DATA_KEY"
        const val LOCATION_LAT_EXTRA = "$PACKAGE_NAME.LOCATION_LAT_EXTRA"
        const val LOCATION_LNG_EXTRA = "$PACKAGE_NAME.LOCATION_LNG_EXTRA"
        const val SELECTED_ADDRESS = "$PACKAGE_NAME.SELECTED_ADDRESS"
        const val SELECTED_STATE = "$PACKAGE_NAME.SELECTED_STATE"
        const val SELECTED_CITY = "$PACKAGE_NAME.SELECTED_CITY"
        const val SELECTED_AREA = "$PACKAGE_NAME.SELECTED_AREA"
        const val SELECTED_PIN_CODE = "$PACKAGE_NAME.SELECTED_PIN_CODE"
        const val SELECTED_CITY_ID = "$PACKAGE_NAME.SELECTED_CITY_ID"
        const val SELECTED_STATE_ID = "$PACKAGE_NAME.SELECTED_STATE_ID"
        const val SELECTED_AREA_ID = "$PACKAGE_NAME.SELECTED_AREA_ID"
        const val SELECTED_HOUSE_NUMBER = "$PACKAGE_NAME.SELECTED_HOUSE_NUMBER"
        const val SELECTED_STREET = "$PACKAGE_NAME.SELECTED_STREET"
        const val SELECTED_LANDMARK = "$PACKAGE_NAME.SELECTED_LANDMARK"
        const val LANGUAGE = "$PACKAGE_NAME.LANGUAGE"

        const val API_KEY = "$PACKAGE_NAME.API_KEY"
        const val COUNTRY = "$PACKAGE_NAME.COUNTRY"
        const val SUPPORTED_AREAS = "$PACKAGE_NAME.SUPPORTED_AREAS"

        const val SUCCESS_RESULT = 0
        const val FAILURE_RESULT = 1
        const val SELECT_LOCATION_REQUEST_CODE = 22
    }
}