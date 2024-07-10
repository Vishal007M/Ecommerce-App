package com.antsglobe.restcommerse.network

import com.antsglobe.restcommerse.model.Request.SignUpRequest
import com.antsglobe.restcommerse.model.Response.AddAddressResponse
import com.antsglobe.restcommerse.model.Response.AddReviewResponse
import com.antsglobe.restcommerse.model.Response.AddToCartResponse
import com.antsglobe.restcommerse.model.Response.AllProductsResponse
import com.antsglobe.restcommerse.model.Response.BuySingleProductResponse
import com.antsglobe.restcommerse.model.Response.CartListResponse
import com.antsglobe.restcommerse.model.Response.CategoriesResponse
import com.antsglobe.restcommerse.model.Response.CouponListResponse
import com.antsglobe.restcommerse.model.Response.DeleteAddressResponse
import com.antsglobe.restcommerse.model.Response.DeleteCartResponse
import com.antsglobe.restcommerse.model.Response.GetAddressList
import com.antsglobe.restcommerse.model.Response.GetAllPinCodeResponse
import com.antsglobe.restcommerse.model.Response.GetFCMTokenResponse
import com.antsglobe.restcommerse.model.Response.GetOrderDetailsResponse
import com.antsglobe.restcommerse.model.Response.GetOrderTaxResponse
import com.antsglobe.restcommerse.model.Response.GetPaymentApi
import com.antsglobe.restcommerse.model.Response.GetPinCodeResponse
import com.antsglobe.restcommerse.model.Response.GetRecentSearchResponse
import com.antsglobe.restcommerse.model.Response.GetRepeatOrderResponse
import com.antsglobe.restcommerse.model.Response.GetReviewResponse
import com.antsglobe.restcommerse.model.Response.GetReviewedListResponse
import com.antsglobe.restcommerse.model.Response.GetTobeReviewedListResponse
import com.antsglobe.restcommerse.model.Response.GetVersionResponse
import com.antsglobe.restcommerse.model.Response.HomeCategoryResponse
import com.antsglobe.restcommerse.model.Response.LoginResponse
import com.antsglobe.restcommerse.model.Response.MostPopularResponse
import com.antsglobe.restcommerse.model.Response.NotificationResponse
import com.antsglobe.restcommerse.model.Response.OffersBannerResponse
import com.antsglobe.restcommerse.model.Response.OrderListResponse
import com.antsglobe.restcommerse.model.Response.OtpResponse
import com.antsglobe.restcommerse.model.Response.ProductListResponse
import com.antsglobe.restcommerse.model.Response.ProductVariationResponse
import com.antsglobe.restcommerse.model.Response.ProfileResponse
import com.antsglobe.restcommerse.model.Response.RecentSearchResponse
import com.antsglobe.restcommerse.model.Response.ResetPassResponse
import com.antsglobe.restcommerse.model.Response.SetDefaultAddressResponse
import com.antsglobe.restcommerse.model.Response.SignUpResponse
import com.antsglobe.restcommerse.model.Response.TopMostPopularResponse
import com.antsglobe.restcommerse.model.Response.UpdateProfileResponse
import com.antsglobe.restcommerse.model.Response.UpdatedAddressResponse
import com.antsglobe.restcommerse.model.Response.WishlistResponse
import com.antsglobe.restcommerse.model.Response.GetAdminTokenResponse
import com.antsglobe.restcommerse.model.Response.getPopularSearchResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    @POST("Signup")
    suspend fun signUp(@Body signUpRequest: SignUpRequest): Response<SignUpResponse>

    @GET("CustomerLogin")
    fun login(
        @Query("email") email: String?,
        @Query("password") password: String?,
        @Query("rem") rem: String?
    ): Call<List<LoginResponse>>

    @GET("GetOTP")
    fun otpApi(
        @Query("name") name: String?, @Query("email") email: String?
    ): Call<OtpResponse>

    @GET("SetPassword")
    fun resetPassApi(
        @Query("email") email: String?, @Query("password") password: String?
    ): Call<ResetPassResponse>


    @GET("GetProfile")
    fun getProfile(
        @Query("email") email: String?
    ): Call<ProfileResponse>

    @GET("UpdateProfile")
    fun updateProfile(
        @Query("email") email: String?,
        @Query("name") name: String?,
        @Query("dob") dob: String?,
        @Query("address") address: String?,
        @Query("phone") phone: String?,
        @Query("gender") gender: String?,
        @Query("uer_image") uer_image: String?,
    ): Call<UpdateProfileResponse>

    @GET("GetNotifications")
    suspend fun getNotification(): Response<NotificationResponse>

    @GET("GetCategory")
    fun getAllCategories(
        @Query("email") email: String?
    ): Call<CategoriesResponse>

    @GET("GetProductByCategory")
    fun getCategoryProduct(
        @Query("email") email: String?, @Query("cid") categoryId: String?
    ): Call<ProductListResponse>

    @GET("GetMostPopular")
    fun getMostPopular(
        @Query("email") email: String?
    ): Call<MostPopularResponse>

    @GET("GetMostPopular")
    fun getTopMostPopular(
        @Query("email") email: String?
    ): Call<TopMostPopularResponse>


    @GET("GetHomeCategory")
    fun getHomeCategory(
        @Query("email") email: String?
    ): Call<HomeCategoryResponse>

    @GET("ProductDetails")
    fun getProductDetails(
        @Query("email") email: String?, @Query("pid") pid: String?
    ): Call<MostPopularResponse>

    @GET("GetProductVariation")
    fun getproductvariations(
        @Query("product_id") product_id: String?, @Query("email") email: String?
    ): Call<ProductVariationResponse>

    @GET("AddToWishList")
    fun addtowishlist(
        @Query("email") email: String?, @Query("productid") productid: String?
    ): Call<WishlistResponse>

    @GET("DeleteWishProduct")
    fun deletefromwishlist(
        @Query("email") email: String?, @Query("pid") pid: String?
    ): Call<WishlistResponse>

    @GET("GetWishlistProduct")
    fun getwishlist(
        @Query("email") email: String?
    ): Call<MostPopularResponse>

    @GET("GetCustomerReview")
    fun getReviewList(
        @Query("pid") productId: String?
    ): Call<GetReviewResponse>

    @GET("AddCustomerReviews")
    fun addCustomerReviews(
        @Query("email") email: String?,
        @Query("product_id") productId: String?,
        @Query("cust_review") review: String?,
        @Query("cust_rating") rating: String?,
        @Query("img") img: String?,
        @Query("img_url") img_url: String?,
    ): Call<AddReviewResponse>


    @GET("GetCartList")
    fun getCartList(
        @Query("email") email: String?
    ): Call<CartListResponse>

    @GET("DeleteCartProduct")
    fun deleteFromCartList(
        @Query("email") email: String?,
        @Query("pid") pid: String?,
        @Query("variation_id") variation_id: String?
    ): Call<DeleteCartResponse>

    @GET("AddToCart")
    fun addToCartApi(
        @Query("email") email: String?,
        @Query("productid") productid: String?,
        @Query("orignal_price") orignal_price: String?,
        @Query("dis_price") dis_price: String?,
        @Query("quantity") quantity: String?,
        @Query("total") total: String?,
        @Query("variation_id") variation_id: String?,
    ): Call<AddToCartResponse>

    @GET("GetAddressList")
    fun getAddressList(
        @Query("email") email: String?
    ): Call<GetAddressList>

    @GET("AddAddress")
    fun addCustomerAddress(
        @Query("email") email: String?,
        @Query("address_type") addressType: String?,
        @Query("address") address: String?,
        @Query("is_default") default: String?,
        @Query("appartment") appartment: String?,
        @Query("landmark") landmark: String?,
        @Query("city") city: String,
        @Query("state") state: String?,
        @Query("pin") pin: String?,
        @Query("customer_name") customerName: String?,
        @Query("customer_mobno") customerMobno: String?
    ): Call<AddAddressResponse>

    @GET("DeleteCustomerAddress")
    fun deleteAddress(
        @Query("email") email: String?, @Query("id") addressId: String?
    ): Call<DeleteAddressResponse>

    @GET("UpdateAddress")
    fun updateCustomerAddress(
        @Query("email") email: String?,
        @Query("address_id") addressId: String?,
        @Query("address_type") addressType: String?,
        @Query("address") address: String?,
        @Query("is_default") default: String?,
        @Query("appartment") appartment: String?,
        @Query("landmark") landmark: String?,
        @Query("city") city: String,
        @Query("state") state: String?,
        @Query("pin") pin: String?,
        @Query("customer_name") customerName: String?,
        @Query("customer_mobno") customerMobno: String?
    ): Call<UpdatedAddressResponse>

    @GET("SetDefaultAddress")
    fun defaultAddress(
        @Query("email") email: String, @Query("address_id") addressId: String
    ): Call<SetDefaultAddressResponse>

    @GET("GetApp_Version")
    suspend fun getVersionApi(): Response<GetVersionResponse>

    @GET("GetCouponList")
    fun couponlist(
        @Query("email") email: String?,
        @Query("coupon_category") couponCategory: String?
    ): Call<CouponListResponse>

    @GET("GetMainBaner")
    fun getofferbanners(
        @Query("email") email: String?
    ): Call<OffersBannerResponse>

    @GET("GetAllProduct")
    fun allProductApi(
        @Query("email") email: String?
    ): Call<AllProductsResponse>

    @GET("GetOrderList")
    fun orderlist(
        @Query("email") email: String?
    ): Call<OrderListResponse>

    @GET("PaymentAPI")
    fun getPaymentApi(
        @Query("email") email: String,
        @Query("transaction_id") transactionId: String,
        @Query("total_price") totalPrice: String,
        @Query("payment_method") paymentMethod: String,
        @Query("payment_status") paymentStatus: String,
        @Query("address_id") addressId: String,
        @Query("disamnt") discountAmount: String,
        @Query("promocode") promoCode: String,
        @Query("promodisc") promodisc: String,
        @Query("shipcharge") shipCharge: String,
        @Query("other_disc") otherDisc: String,
        @Query("taxper") taxPer: String,
        @Query("taxamt") taxAmount: String,
        @Query("grandtotal") grandTotal: String,
    ): Call<GetPaymentApi>


    @GET("PlaceOrderSingleProduct")
    fun getSinglePaymentApi(
        @Query("email") email: String,
        @Query("transaction_id") transactionId: String,
        @Query("total_price") totalPrice: String,
        @Query("payment_method") paymentMethod: String,
        @Query("payment_status") paymentStatus: String,
        @Query("address_id") addressId: String,
        @Query("disamnt") discountAmount: String,
        @Query("promocode") promoCode: String,
        @Query("promodisc") promodisc: String,
        @Query("shipcharge") shipCharge: String,
        @Query("other_disc") otherDisc: String,
        @Query("product_id") productId: String,
        @Query("quantity") quantity: String,
        @Query("price") price: String,
        @Query("dis_price") discountPrice: String,
        @Query("variation_id") variationId: String,
        @Query("grandtotal") grandTotal: String,
        @Query("taxper") taxPer: String,
        @Query("taxamt") taxAmount: String,
    ): Call<BuySingleProductResponse>

    @GET("AddRecentProducts")
    fun addRecentProductsApi(
        @Query("product_name") product_name: String?, @Query("email") email: String?
    ): Call<RecentSearchResponse>

    @GET("GetRecentProduct")
    fun getRecentProductApi(
        @Query("email") email: String?
    ): Call<GetRecentSearchResponse>

    @GET("GetPopularCalagoryName")
    fun getPopularSearchApi(
        @Query("email") email: String?
    ): Call<getPopularSearchResponse>

    @GET("GetPinCode")
    fun getPinCodeApi(
        @Query("email") email: String?, @Query("pin_code") pin_code: String?
    ): Call<GetPinCodeResponse>

    @GET("GetAllPinCode")
    fun getAllPinCodeApi(
        @Query("email") email: String?
    ): Call<GetAllPinCodeResponse>

    @GET("GetOrderdetails")
    fun getOrderDetailsApi(
        @Query("email") email: String?, @Query("orderId") orderId: String?
    ): Call<GetOrderDetailsResponse>

    @GET("GetRepeatOrder")
    fun getOrderRepeatApi(
        @Query("email") email: String?, @Query("order_no") orderId: String?
    ): Call<GetRepeatOrderResponse>


    @GET("OrderTaxCal")
    fun getOrderTaxCalApi(
        @Query("email") email: String?,
        @Query("total_price") total_price: String?,
        @Query("promodisc") promodisc: String?,
        @Query("shipcharge") shipcharge: String?,
        @Query("other_disc") other_disc: String?,
    ): Call<GetOrderTaxResponse>

    @GET("GetTobeReviewedList")
    fun getTobeReviewedListApi(
        @Query("email") email: String?
    ): Call<GetTobeReviewedListResponse>

    @GET("GetReviewedList")
    fun getReviewedListApi(
        @Query("email") email: String?
    ): Call<GetReviewedListResponse>

    @GET("AddUserFirebaseToken")
    fun AddUserFirebaseTokenApi(
        @Query("email") email: String?,
        @Query("firebase_token") firebase_token: String?,
    ): Call<GetFCMTokenResponse>


    @GET("GetAdminToken")
    fun GetAdminTokenApi(): Call<GetAdminTokenResponse>

}