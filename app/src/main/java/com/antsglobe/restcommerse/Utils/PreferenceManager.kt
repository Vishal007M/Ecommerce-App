package com.antsglobe.restcommerse.Utils

import android.content.Context
import android.content.SharedPreferences
import java.util.concurrent.atomic.AtomicBoolean

class PreferenceManager(context: Context) {
    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    }

    companion object {
        private const val PREFS_NAME = "ExprepAcademy"
        private const val KEY_IS_LOGGED_IN = "isLoggedIn"
        private const val KEY_FIRST_TIME_OPEN = "firstTimeOpen"
        private const val NOTIFICATION_COUNT = "NotificationCount"
        private const val NOTIFICATION_BLINK_COUNT = "NotificationBlinkCount"
        private const val NOTIFICATION_PREVIOUS_COUNT = "NotificationPreviousCount"
        private const val ADDRESS_ID = "AddressId"
        private const val PAYMENT_AMOUNT = "PaymentAmount"
        private const val DISCOUNT_PRICE = "DiscountPrice"
        private const val COUPON_NAME = "CouponName"
        private const val PROMO_CODE = "PromoCode"
        private const val PRODUCT_ID = "ProductId"
        private const val PRICE = "Price"
        private const val DISC_PRICE = "DiscPrice"
        private const val variation_id = "variation_Id"
        private const val QUANTITY = "Quantity"
        private const val PROMO_DISCOUNT = "PromoDiscount"
        private const val SINGLE_PRODUCT = "SingleProduct"
        private const val CART_AMOUNT = "CartAmount"
        private const val SHIP_PRICE = "shipPrice"
        private const val TAX_PERC = "taxPerc"
        private const val TAX_AMOUNT = "taxAmount"
        private const val GRAND_TOTAL = "grandTotal"
        private const val WISHLIST_COUNT = "WishlistCount"
        private const val NIGHT_MODE = "NightMode"
        const val AUTH_ACCESS_TOKEN = "Access-Token"
        const val SEARCH_DATA = "Search_Data"

        private const val PAYMENT_METHOD = "PAYMENT_METHOD"
        private const val ADMIN_TOKEN = "ADMIN_TOKEN"

        const val KEY_EMAIL = "email_e_commerce"
        const val KEY_NAME = "name_e_commerce"
        const val KEY_PHONE = "phone_e_commerce"
        const val KEY_ADDRESS = "address_e_commerce"

        private const val KEY_PIC = "pic"
        private const val KEY_AVATAR = "pic_avatar"

//        const val KEY_TOKEN = "token"

        //        @StringDef(KEY_TOKEN)
//        @Retention(AnnotationRetention.SOURCE)
        annotation class PrefKey

        private lateinit var instance: PreferenceManager
        private val isInitialized = AtomicBoolean()
        fun initialize(context: Context) {
            if (!isInitialized.getAndSet(true))
                instance = PreferenceManager(context.applicationContext)
        }

        fun get(): PreferenceManager = instance
    }


    fun clearAllData() {
        sharedPreferences.edit().clear().apply()
    }

    fun isFirstTimeOpen(): Boolean {
        return sharedPreferences.getBoolean(KEY_FIRST_TIME_OPEN, true)
    }

    fun setFirstTimeOpen(isFirstTimeOpen: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_FIRST_TIME_OPEN, isFirstTimeOpen).apply()
    }

    fun getString(@PrefKey key: String, value: String): String =
        sharedPreferences.getString(key, value).toString()


    fun clearUserData() {
        sharedPreferences.edit().clear().apply()
    }

    fun setEmail(insCode: String?) {
        sharedPreferences.edit().putString(KEY_EMAIL, insCode).apply()
    }

    fun getEmail(): String? {
        return sharedPreferences.getString(KEY_EMAIL, null)
    }

    fun setSearch(insCode: String?) {
        sharedPreferences.edit().putString(SEARCH_DATA, insCode).apply()
    }

    fun getSearch(): String? {
        return sharedPreferences.getString(SEARCH_DATA, null)
    }


    fun setPhoneNo(insCode: String?) {
        sharedPreferences.edit().putString(KEY_PHONE, insCode).apply()
    }

    fun getPhoneNo(): String? {
        return sharedPreferences.getString(KEY_PHONE, null)
    }

    fun setName(insCode: String?) {
        sharedPreferences.edit().putString(KEY_NAME, insCode).apply()
    }

    fun getName(): String? {
        return sharedPreferences.getString(KEY_NAME, null)
    }

    fun setAvatarImg(insCode: String?) {
        sharedPreferences.edit().putString(KEY_AVATAR, insCode).apply()
    }

    fun getAvatarImg(): String? {
        return sharedPreferences.getString(KEY_AVATAR, null)
    }

    fun getProfilePic(): String? {
        return sharedPreferences.getString(KEY_PIC, "boy1")
    }

    fun setProfilePic(insCode: String?) {
        sharedPreferences.edit().putString(KEY_PIC, insCode).apply()
    }

    fun setNotificationCount(insCode: String?) {
        sharedPreferences.edit().putString(NOTIFICATION_COUNT, insCode).apply()
    }

    fun getNotificationCount(): String? {
        return sharedPreferences.getString(NOTIFICATION_COUNT, null)
    }

    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun setLoggedIn(isLoggedIn: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_IS_LOGGED_IN, isLoggedIn).apply()
    }

    fun getAccessToken(): String? {
        /*
         * default value is app name, if it is empty then app will crash
         * */
        return sharedPreferences.getString(AUTH_ACCESS_TOKEN, "")
    }

    fun setAccessToken(accessToken: String?) {
        sharedPreferences.edit().putString(AUTH_ACCESS_TOKEN, accessToken).commit()
    }

    fun setAddress(address: String?) {
        sharedPreferences.edit().putString(KEY_ADDRESS, address).apply()
    }

    fun getAddress(): String? {
        return sharedPreferences.getString(KEY_ADDRESS, null)
    }

    fun setWishListCount(count: String?) {
        sharedPreferences.edit().putString(WISHLIST_COUNT, count).apply()
    }

    fun getWishListCount(): String? {
        return sharedPreferences.getString(WISHLIST_COUNT, null)
    }

    fun setMode(nightMode: Boolean?) {
        sharedPreferences.edit().putBoolean(NIGHT_MODE, nightMode!!).apply()
    }

    fun getMode(): Boolean? {
        return sharedPreferences.getBoolean(NIGHT_MODE, false)
    }

    fun setNotificationBlinkCount(count: String?) {
        sharedPreferences.edit().putString(NOTIFICATION_BLINK_COUNT, count).apply()
    }

    fun getNotificationBlinkCount(): String? {
        return sharedPreferences.getString(NOTIFICATION_BLINK_COUNT, "")
    }

    fun setNotificationPreviousCount(count: String?) {
        sharedPreferences.edit().putString(NOTIFICATION_PREVIOUS_COUNT, count).apply()
    }

    fun getNotificationPreviousCount(): String? {
        return sharedPreferences.getString(NOTIFICATION_PREVIOUS_COUNT, "")
    }

    fun setAddressId(pin: String?) {
        sharedPreferences.edit().putString(ADDRESS_ID, pin).apply()
    }

    fun getAddressId(): String? {
        return sharedPreferences.getString(ADDRESS_ID, "")
    }

    fun setPayableAmount(amount: String?) {
        sharedPreferences.edit().putString(PAYMENT_AMOUNT, amount).apply()
    }

    fun getPayableAmount(): String? {
        return sharedPreferences.getString(PAYMENT_AMOUNT, "")
    }

    fun setDiscountPrice(discount: String?) {
        sharedPreferences.edit().putString(DISCOUNT_PRICE, discount!!).apply()
    }

    fun getDiscountPrice(): String? {
        return sharedPreferences.getString(DISCOUNT_PRICE, "0")
    }

    fun setCartPrice(amount: String?) {
        sharedPreferences.edit().putString(CART_AMOUNT, amount).apply()
    }

    fun getCartPrice(): String? {
        return sharedPreferences.getString(CART_AMOUNT, "")
    }

    fun setShipPrice(ship: String?) {
        sharedPreferences.edit().putString(SHIP_PRICE, ship).apply()
    }

    fun getShipPrice(): String? {
        return sharedPreferences.getString(SHIP_PRICE, "")
    }


    fun setTaxPerc(ship: String?) {
        sharedPreferences.edit().putString(TAX_PERC, ship).apply()
    }

    fun getTaxPerc(): String? {
        return sharedPreferences.getString(TAX_PERC, "")
    }


    fun setTaxAmount(ship: String?) {
        sharedPreferences.edit().putString(TAX_AMOUNT, ship).apply()
    }

    fun getTaxAmount(): String? {
        return sharedPreferences.getString(TAX_AMOUNT, "")
    }

    fun setGrandTotal(ship: String?) {
        sharedPreferences.edit().putString(GRAND_TOTAL, ship).apply()
    }

    fun getGrandTotal(): String? {
        return sharedPreferences.getString(GRAND_TOTAL, "")
    }

    fun setCouponName(couponName: String?) {
        sharedPreferences.edit().putString(COUPON_NAME, couponName).apply()
    }

    fun getCouponName(): String? {
        return sharedPreferences.getString(COUPON_NAME, "")
    }

    fun setPromoName(couponName: String?) {
        sharedPreferences.edit().putString(PROMO_CODE, couponName).apply()
    }

    fun getPromName(): String? {
        return sharedPreferences.getString(PROMO_CODE, "")
    }

    fun setPromoDisc(promoDiscount: String?) {
        sharedPreferences.edit().putString(PROMO_DISCOUNT, promoDiscount!!).apply()
    }

    fun getPromDics(): String? {
        return sharedPreferences.getString(PROMO_DISCOUNT, "0")
    }

    fun setSingleProductBuy(singleProduct: Boolean?) {
        sharedPreferences.edit().putBoolean(SINGLE_PRODUCT, singleProduct!!).apply()
    }

    fun getSingleProductBuy(): Boolean? {
        return sharedPreferences.getBoolean(SINGLE_PRODUCT, false)
    }

    fun setProductId(productId: String?) {
        sharedPreferences.edit().putString(PRODUCT_ID, productId).apply()
    }

    fun getProductId(): String? {
        return sharedPreferences.getString(PRODUCT_ID, "")
    }

    fun setQuantity(quantity: String?) {
        sharedPreferences.edit().putString(QUANTITY, quantity).apply()
    }

    fun getQuantity(): String? {
        return sharedPreferences.getString(QUANTITY, "")
    }

    fun setPaymentMethod(quantity: String?) {
        sharedPreferences.edit().putString(PAYMENT_METHOD, quantity).apply()
    }

    fun getPaymentMethod(): String? {
        return sharedPreferences.getString(PAYMENT_METHOD, "")
    }

    fun setAdminToken(quantity: String?) {
        sharedPreferences.edit().putString(ADMIN_TOKEN, quantity).apply()
    }

    fun getAdminToken(): String? {
        return sharedPreferences.getString(ADMIN_TOKEN, "")
    }

    fun setPrice(price: String?) {
        sharedPreferences.edit().putString(PRICE, price).apply()
    }

    fun getPrice(): String? {
        return sharedPreferences.getString(PRICE, "")
    }


    fun setDisPrice(discPrice: String?) {
        sharedPreferences.edit().putString(DISC_PRICE, discPrice).apply()
    }

    fun getDisPrice(): String? {
        return sharedPreferences.getString(DISC_PRICE, "")
    }

    fun setVariationID(variationId: String?) {
        sharedPreferences.edit().putString(variation_id, variationId).apply()
    }

    fun getVariationID(): String? {
        return sharedPreferences.getString(variation_id, "")
    }
}