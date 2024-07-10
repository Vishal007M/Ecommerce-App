package com.antsglobe.restcommerse.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavController.OnDestinationChangedListener
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import com.antsglobe.restcommerse.R
import com.antsglobe.restcommerse.Utils.PreferenceManager
import com.antsglobe.restcommerse.databinding.ActivityCheckoutBinding
import com.antsglobe.restcommerse.network.RetrofitClient
import com.antsglobe.restcommerse.viewmodel.PaymentViewModel
import com.antsglobe.restcommerse.viewmodel.ViewModelFactory
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


class CheckoutActivity : AppCompatActivity(), OnDestinationChangedListener, PaymentResultListener {
    private var discount: Double = 0.0
    private lateinit var navController: NavController
    private var binding: ActivityCheckoutBinding? = null
    private lateinit var viewmodel: PaymentViewModel
    private val sharedPreferences = PreferenceManager(this)
    private var email: String? = null
    private var addressId: String? = null

    private var cartValueAmount: String = ""
    private var couponValueAmount: String = ""
    private var strCouponName: String = ""
    private var strProductId: String = ""
    private var strQuantity: String = ""
    private var strPrice: String = ""
    private var strDiscPrice: String = ""
    private var strVariationId: String = ""

    private var shipcharge: String = ""
    private var taxableAmt: String = ""
    private var taxper: String = ""
    private var taxamt: String = ""
    private var grandtotal: String = ""
    private var token: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        viewmodel = ViewModelProvider(
            this, ViewModelFactory(RetrofitClient.apiService)
        )[PaymentViewModel::class.java]
        email = sharedPreferences.getEmail().toString()

        token = sharedPreferences.getAdminToken().toString()

        strCouponName = intent.getStringExtra("CouponName").toString()
        cartValueAmount = intent.getStringExtra("total_price").toString()
        couponValueAmount = intent.getStringExtra("promodisc").toString()
        shipcharge = intent.getStringExtra("shipcharge").toString()

        taxableAmt = intent.getStringExtra("taxableAmt").toString()
        taxper = intent.getStringExtra("taxper").toString()
        taxamt = intent.getStringExtra("taxamt").toString()
        grandtotal = intent.getStringExtra("grandtotal").toString()

        /* single price */
        strProductId = intent.getStringExtra("product_id").toString()
        strVariationId = intent.getStringExtra("variation_id").toString()
        strQuantity = intent.getStringExtra("quantity").toString()
        strDiscPrice = intent.getStringExtra("discount_price").toString()
        strPrice = intent.getStringExtra("orginal_price").toString()
        //Toast.makeText(this, "CouponName : $cartValueAmount", Toast.LENGTH_SHORT).show()

        Log.e(
            "TAG",
            "buy now: $strProductId, $strQuantity, $strPrice, $strDiscPrice, $strVariationId"
        )

        if (sharedPreferences.getMode() == true) {
            binding!!.fullscreen.setBackgroundColor(resources.getColor(R.color.blackfordark))
            binding!!.logoImageHome.setTextColor(resources.getColor(R.color.blackfordark))
            binding!!.backButton.setImageResource(R.drawable.ic_baseline_arrow_back_24_dark)
        }
        sharedPreferences.setDiscountPrice(couponValueAmount)
        sharedPreferences.setCartPrice(cartValueAmount)
        sharedPreferences.setShipPrice(shipcharge)
        sharedPreferences.setTaxPerc(taxper)
        sharedPreferences.setTaxAmount(taxamt)
        sharedPreferences.setGrandTotal(grandtotal)

        sharedPreferences.setCouponName(strCouponName)

        if (strProductId.isNullOrEmpty()) {
            sharedPreferences.setProductId("0")
        } else {
            sharedPreferences.setProductId(strProductId)
        }

        if (strVariationId.isNullOrEmpty()) {
            sharedPreferences.setVariationID("0")
        } else {
            sharedPreferences.setVariationID(strVariationId)
        }

        if (strQuantity.isNullOrEmpty()) {
            sharedPreferences.setQuantity("0")
        } else {
            sharedPreferences.setQuantity(strQuantity)
        }

        if (strDiscPrice.isNullOrEmpty()) {
            sharedPreferences.setDisPrice("0")
        } else {
            sharedPreferences.setDisPrice(strDiscPrice)
        }

        if (strPrice.isNullOrEmpty()) {
            sharedPreferences.setPrice("0")
        } else {
            sharedPreferences.setPrice(strPrice)
        }

        val bundle = Bundle()
        bundle.putString("CouponName", strCouponName) // You can put any data type here
        bundle.putString("total_price", cartValueAmount)
        bundle.putString("promodisc", couponValueAmount)

        navController = findNavController(R.id.fragment)
        navController.addOnDestinationChangedListener(this)

        navController.navigateUp() // to clear previous navigation history
        navController.navigate(R.id.shippingCheckout, bundle)

        binding!!.backButton.setOnClickListener {
            val i = Intent(this@CheckoutActivity, HomeActivity::class.java)
            startActivity(i)
            finish()
        }

    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        when (destination.id) {
            R.id.shippingCheckout -> {
                binding!!.llSlideOut.visibility = View.VISIBLE
                binding!!.BusinessSettingsTB.visibility = View.VISIBLE
                binding!!.card2.setCardBackgroundColor(Color.parseColor("#FFEBC58D"))
                binding!!.card3.setCardBackgroundColor(Color.parseColor("#FFEBC58D"))
                binding!!.payment.setTextColor(Color.parseColor("#FFEBC58D"))
                binding!!.done.setTextColor(Color.parseColor("#FFEBC58D"))
                binding!!.line1.setBackgroundColor(Color.parseColor("#FFEBC58D"))
                binding!!.line2.setBackgroundColor(Color.parseColor("#FFEBC58D"))
            }

            R.id.paymentCheckout -> {
                binding!!.logoImageHome.text = "Proceed to checkout"
                binding!!.card2.setCardBackgroundColor(resources.getColor(R.color.orange))
                binding!!.payment.setTextColor(resources.getColor(R.color.orange))
                binding!!.card3.setCardBackgroundColor(Color.parseColor("#FFEBC58D"))
                binding!!.done.setTextColor(Color.parseColor("#FFEBC58D"))
                binding!!.line1.setBackgroundColor(resources.getColor(R.color.orange))
                binding!!.line2.setBackgroundColor(Color.parseColor("#FFEBC58D"))
            }

            R.id.donepayment -> {
                binding!!.logoImageHome.text = "Order Placed"
                binding!!.card2.setCardBackgroundColor(resources.getColor(R.color.orange))
                binding!!.card3.setCardBackgroundColor(resources.getColor(R.color.orange))
                binding!!.payment.setTextColor(resources.getColor(R.color.orange))
                binding!!.done.setTextColor(resources.getColor(R.color.orange))
                binding!!.line1.setBackgroundColor(resources.getColor(R.color.orange))
                binding!!.line2.setBackgroundColor(resources.getColor(R.color.orange))
            }

            R.id.maps_fragment -> {
                binding!!.llSlideOut.visibility = View.GONE
                binding!!.BusinessSettingsTB.visibility = View.GONE
            }
        }
    }

    fun startPayment(totalPrice: String, address_id: String) {
        val coursePrice = totalPrice
        /* You need to pass current activity in order to let Razorpay create CheckoutActivity*/
        val activity: Activity = this

        Checkout.preload(binding?.root?.context)

        val checkOut = Checkout()
        checkOut.setKeyID("rzp_test_ZIepneClKHvAsB")

        try {
            val options = JSONObject()
            options.put("name", "RestCommerce")
            options.put("description", "Order's Payment")
            // options.put("order_id", orderId)
            options.put("theme.color", "#FF9800")
            options.put("currency", "INR")
            options.put("amount", coursePrice!!.toDouble() * 100)

            val retryObj = JSONObject()
            retryObj.put("enabled", true)
            retryObj.put("max_count", 4)
            options.put("retry", retryObj)

            checkOut.open(activity, options)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("TAG", "Error initiating payment:: ${e.message} ")
            customToast(this, "Error initiating payment: ${e.message}", R.drawable.ic_info)
        }
    }

    override fun onPaymentSuccess(transactionId: String?) {
        var PaymentMethod = "Online"


        if (sharedPreferences.getPaymentMethod() == "COD") {
            PaymentMethod = "Cash On Delivery"
        }


        addressId = sharedPreferences.getAddressId().toString()
        val payableAmount = sharedPreferences.getPayableAmount()
        val cartValueAmount = sharedPreferences.getCartPrice()
        val promoCoupon = sharedPreferences.getPromName().toString()
        val promoDisc = sharedPreferences.getPromDics()
        val productId = sharedPreferences.getProductId()
        val variantId = sharedPreferences.getVariationID()
        val price = sharedPreferences.getPrice()
        val discPrice = sharedPreferences.getDisPrice()
        val quantity = sharedPreferences.getQuantity()


        if (promoDisc != null) {
            discount = cartValueAmount!!.toInt() * (promoDisc!!.toDouble() / 100)
        } else {
            discount = 0.0
        }

        try {

            if (sharedPreferences.getSingleProductBuy() == true) {
                Log.e(
                    "TAG",
                    "onSinglePaymentSuccess: $email, ${transactionId.toString()}, ${payableAmount.toString()}," +
                            "${addressId.toString()}, ${promoDisc.toString()}, ${promoCoupon.toString()}, ${discount.toString()}," +
                            "${productId.toString()}, ${quantity.toString()}, ${price.toString()}, ${variantId.toString()}, ${cartValueAmount.toString()}  ${taxamt.toString()} ${taxper.toString()}"
                )
                viewmodel.GetSinglePaymentResponse(
                    email!!,
                    transactionId.toString(),
                    cartValueAmount.toString(),
                    PaymentMethod,
                    "Succcessful",
                    addressId.toString(),
                    promoDisc.toString(),
                    promoCoupon,
                    discount.toString(),
                    shipcharge.toString(),
                    "0",
                    productId.toString(),
                    quantity.toString(),
                    price.toString(),
                    discPrice.toString(),
                    variantId.toString(),
                    payableAmount.toString(),
                    taxper,
                    taxamt,
                )
                initSinglePaymentObervser()

            } else {
                Log.e(
                    "TAG",
                    "onCartPaymentSuccess: $email, ${transactionId.toString()}, ${payableAmount.toString()}," +
                            "${addressId.toString()}, ${promoDisc.toString()}, ${promoCoupon.toString()}, ${discount.toString()}, ${cartValueAmount.toString()}, $taxper, $taxamt"
                )

                viewmodel.GetPaymentResponse(
                    email!!,
                    transactionId.toString(),
                    cartValueAmount.toString(),
                    PaymentMethod,
                    "Succcessful",
                    addressId.toString(),
                    discount.toString(),
                    promoCoupon,
                    promoDisc.toString(),
                    shipcharge.toString(),
                    "0",
                    taxper,
                    taxamt,
                    payableAmount.toString(),

                    )
                initPaymentObervser()
            }

        } catch (e: Exception) {
            e.printStackTrace()
            customToast(this, "Error!", R.drawable.ic_info)
        }
    }


    private fun initPaymentObervser() {
        viewmodel.getPaymentResponse.observe(this) { paymentApiResp ->
            Log.e("paymentApiResp", "paymentApiResp $paymentApiResp")

//            LoadingDialog.dismissProgressDialog()
            if (paymentApiResp?.is_success == true) {
                customToast(this, "Successful! ", R.drawable.success_toast_icon)

                val orderList = paymentApiResp.order_id
                val bundle = Bundle()
                bundle.putString("orderId", orderList)
                navController.navigateUp() // to clear previous navigation history
                navController.navigate(R.id.donepayment, bundle)

                sendNotification(
                    "Product Purchased",
                    token,
                    "order placed order id ${paymentApiResp.order_id}"
                )

            } else {
                customToast(this, " Unsuccessful - ${paymentApiResp?.message}", R.drawable.ic_info)
            }
        }
    }

    private fun initSinglePaymentObervser() {
        viewmodel.getSinglePaymentResponse.observe(this) { SinglePaymentApiResp ->
//            LoadingDialog.dismissProgressDialog()
            if (SinglePaymentApiResp?.is_success == true) {
                Log.e("paymentApiResp", "SinglepaymentApiResp $SinglePaymentApiResp")
                customToast(this, "isSingle Successful! ", R.drawable.success_toast_icon)

                sharedPreferences.setSingleProductBuy(false)

                val orderList = SinglePaymentApiResp.order_id
                val bundle = Bundle()
                bundle.putString("orderId", orderList)
                navController.navigateUp() // to clear previous navigation history
                navController.navigate(R.id.donepayment, bundle)

                sendNotification(
                    "restcommerse admin",
                    token,
                    "order placed order id ${SinglePaymentApiResp.order_id}"
                )
            } else {
                sharedPreferences.setSingleProductBuy(false)
                customToast(this, "${SinglePaymentApiResp?.message}", R.drawable.ic_info)
            }
        }
    }


    private fun sendNotification(title: String, token: String, body: String) {
        val client = OkHttpClient()
        val mediaType = "application/json".toMediaTypeOrNull()

        val jsonNotif = JSONObject()
        val wholeObj = JSONObject()

        try {
            jsonNotif.put("title", title)
            jsonNotif.put("body", body)
            wholeObj.put("to", token)
            wholeObj.put("notification", jsonNotif)
        } catch (e: JSONException) {
            Log.d("mylog", e.toString())
        }

        val rBody = RequestBody.create(mediaType, wholeObj.toString())

        val request = Request.Builder()
            .url("https://fcm.googleapis.com/fcm/send")
            .post(rBody)
            .addHeader(
                "Authorization",
                "key=AAAAc343NvE:APA91bHGL-jh32gg1ZAZS0oENPsCfOuhyFH4tIklBhCVgCgGvcBNfX4boh9WpJY46bhKgc1pAe1qKB5qCcDFZ92lFjDTG0W8fniyOOWmMimYXZ3O6dExJs1dqZmgx1qmY717QiM04b4m"
            )
//            .addHeader("Content-Type", "application/json")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.d("mylog", e.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                // Handle response here
                // For example, you can log response code
                Log.d("mylog", "Response code: ${response.code}")
                response.body?.close()
            }
        })
    }

    override fun onPaymentError(p0: Int, p1: String?) {
        try {
            val jsonObject = JSONObject(p1)
            val errorObject = jsonObject.getJSONObject("error")
            val description = errorObject.getString("description")
            customToast(this, "Error! $description", R.drawable.ic_info)
        } catch (e: JSONException) {
            e.printStackTrace()
            customToast(this, "Error!", R.drawable.ic_info)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        /*val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)*/
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

    override fun onDestroy() {
        super.onDestroy()
        //unregisterReceiver(yourBroadcastReceiver)
    }
}