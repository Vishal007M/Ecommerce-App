package com.antsglobe.restcommerse.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.antsglobe.restcommerse.R
import com.antsglobe.restcommerse.Utils.PreferenceManager
import com.antsglobe.restcommerse.databinding.FragmentPaymentCheckoutBinding
import com.antsglobe.restcommerse.network.RetrofitClient
import com.antsglobe.restcommerse.viewmodel.PaymentViewModel
import com.antsglobe.restcommerse.viewmodel.ViewModelFactory


class PaymentCheckout : Fragment() {

    private var binding: FragmentPaymentCheckoutBinding? = null
    private lateinit var viewmodel: PaymentViewModel
    private lateinit var sharedPreferences: PreferenceManager

    private var defaultPin: String? = null
    private var defaultID: String? = null
    private var cartValueAmount: String = ""
    private var shippingChanger: String = ""
    private var taxPerc: String = ""
    private var taxAmpunt: String = ""
    private var grandTotal: String = ""
    private var couponValueAmount: String = ""
    private var strCouponName: String = ""

    private var email: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            // param1 = it.getString(ARG_PARAM1)
            defaultID = it.getString("DefaultId")
            defaultPin = it.getString("DefaultPin")
//            strCouponName = it.getString("CouponName").toString()
//            cartValueAmount = it.getString("Cart_value").toString()
//            couponValueAmount = it.getDouble("Coupon_Discount") ?: 0.0

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPaymentCheckoutBinding.inflate(inflater, container, false)
        sharedPreferences = PreferenceManager(requireContext())
        setOnBackPressed()

        sharedPreferences.setAddressId(defaultID)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewmodel = ViewModelProvider(
            this, ViewModelFactory(RetrofitClient.apiService)
        )[PaymentViewModel::class.java]

        email = sharedPreferences.getEmail().toString()


        strCouponName = sharedPreferences.getCouponName().toString()
        cartValueAmount = sharedPreferences.getCartPrice().toString()
        couponValueAmount = sharedPreferences.getDiscountPrice()!!.toString()
        shippingChanger = sharedPreferences.getShipPrice().toString()
        taxPerc = sharedPreferences.getTaxPerc().toString()
        taxAmpunt = sharedPreferences.getTaxAmount().toString()
        grandTotal = sharedPreferences.getGrandTotal().toString()

        binding!!.tvDeliverPin.text = defaultPin

        Log.e("TAG", "shippingChanger: $shippingChanger")
        if (shippingChanger == "0") {
            binding?.tvDevilery?.text = "FREE"
        } else {
            binding?.tvDevilery?.text = shippingChanger
        }

        binding?.tvTotalPrice?.text = cartValueAmount + ".00"
        binding?.tvDiscountPrice?.text = "-" + couponValueAmount.toString() + ".00"
        binding?.tvTaxPerc?.text = taxPerc
        binding?.tvTaxPrice?.text = taxAmpunt
        binding?.tvPayableAmount?.text = grandTotal

        binding!!.ivCross.setOnClickListener {
            binding!!.rlAppliedCoupon.visibility = View.GONE
        }
        if (sharedPreferences.getMode() == true) {
            binding!!.llPriceView.setBackgroundResource(R.drawable.profile_round_corner_bg_addresses_dark)
            binding!!.tvcartvalue.setTextColor(resources.getColor(R.color.whitefordark))
            binding!!.tvcoupondisc.setTextColor(resources.getColor(R.color.whitefordark))
            binding!!.tvtax.setTextColor(resources.getColor(R.color.whitefordark))
            binding!!.tvTaxPerc.setTextColor(resources.getColor(R.color.whitefordark))
            binding!!.Perc.setTextColor(resources.getColor(R.color.whitefordark))
            binding!!.tvPayableAmount.setTextColor(resources.getColor(R.color.whitefordark))
            binding!!.tvamountpayable.setTextColor(resources.getColor(R.color.whitefordark))
            binding!!.next.setTextColor(resources.getColor(R.color.blackfordark))
        }
        binding!!.changeAddress.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("CouponName", strCouponName)
            bundle.putString("Cart_value", cartValueAmount)
            bundle.putString("Coupon_Discount", couponValueAmount)

            findNavController().navigate(R.id.action_paymentCheckout_to_shippingCheckout, bundle)
        }
//        binding!!.tvClear.setOnClickListener {
//            binding!!.rlAppliedCoupon.visibility = View.GONE
//            binding!!.llCouponCode.visibility = View.GONE
//            binding!!.tvDiscountPrice.text = " - ₹0"
//            binding!!.tvCouponName.text = ""
////            binding!!.tvPayableAmount.text = "₹ ${cartValueAmount}"
//        }

        if (strCouponName.isNullOrEmpty()) {
            binding!!.llCouponCode.visibility = View.GONE
            binding!!.rlAppliedCoupon.visibility = View.GONE
        } else {
            binding!!.llCouponCode.visibility = View.VISIBLE
            binding!!.rlAppliedCoupon.visibility = View.VISIBLE
            binding!!.tvCouponName.text = strCouponName
        }

//        binding!!.tvTotalPrice.text = "₹ ${cartValueAmount}"
//
//        if (couponValueAmount == 0.0) {
//            binding!!.tvDiscountPrice.text = " - ₹0"
////            binding!!.tvPayableAmount.text = "₹ ${cartValueAmount}"
//        } else {
//            val Discount = cartValueAmount.toInt() * (couponValueAmount / 100)
//            binding!!.tvDiscountPrice.text = " - ₹${Discount.toString()}"
////            binding!!.tvPayableAmount.text = "₹ ${cartValueAmount.toInt() - Discount}"
//        }

        updateApiData()


    }

    private val PREFS_NAME = "MyPrefs"
    private val PREF_SELECTED_RADIO_BUTTON = "SelectedRadioButton"

    private fun updateApiData() {
        val totalPrice = binding!!.tvPayableAmount.text.toString().replace("₹", "")
        val promoCode = binding!!.tvCouponName.text.toString()
        val promodisc = binding!!.tvDiscountPrice.text.toString().replace(" - ₹", "")
        sharedPreferences.setPayableAmount(totalPrice)

        if (promoCode.isNullOrEmpty()) {
            sharedPreferences.setPromoName("")
        } else {
            sharedPreferences.setPromoName(strCouponName)
        }

        if (promodisc.isNullOrEmpty()) {
            sharedPreferences.setPromoDisc("0")
        } else {
            sharedPreferences.setPromoDisc(couponValueAmount)
        }


        val genderRadioGroup = view?.findViewById<RadioGroup>(R.id.genderRB)
        val prefs = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        val selectedRadioButtonId = prefs.getInt(PREF_SELECTED_RADIO_BUTTON, -1)
        if (selectedRadioButtonId != -1) {
            genderRadioGroup?.check(selectedRadioButtonId)
        }

        genderRadioGroup?.setOnCheckedChangeListener { _, checkedId ->
            val radioButton = view?.findViewById<RadioButton>(checkedId)
            val editor = prefs.edit()
            editor.putInt(PREF_SELECTED_RADIO_BUTTON, checkedId)
            editor.apply()

            when (radioButton?.id) {
                R.id.online -> {

//                    Toast.makeText(requireContext(), "online", Toast.LENGTH_SHORT).show()
                }

                R.id.cashOnDelivery -> {
//                    Toast.makeText(requireContext(), "cashOnDelivery", Toast.LENGTH_SHORT).show()
                }

                R.id.nonVegOnly -> {

                }
            }
        }

        binding!!.next.setOnClickListener {

            if (binding!!.online.isChecked) {
                val checkoutActivity = activity as? CheckoutActivity
                checkoutActivity?.startPayment(totalPrice, "1024")
            }
            if (binding!!.cashOnDelivery.isChecked) {
                val checkoutActivity = activity as? CheckoutActivity
                checkoutActivity?.onPaymentSuccess("")
                sharedPreferences.setPaymentMethod("COD")
//                onCODPurchase()
            }


        }
    }

    private lateinit var navController: NavController


    private fun initSinglePaymentObervser() {
        viewmodel.getSinglePaymentResponse.observe(viewLifecycleOwner) { SinglePaymentApiResp ->
//            LoadingDialog.dismissProgressDialog()
            if (SinglePaymentApiResp?.is_success == true) {
                Log.e("paymentApiResp", "SinglepaymentApiResp $SinglePaymentApiResp")
                customToast(
                    requireContext(),
                    "isSingle Successful! ",
                    R.drawable.success_toast_icon
                )

                sharedPreferences.setSingleProductBuy(false)

                val orderList = SinglePaymentApiResp.order_id
                val bundle = Bundle()
                bundle.putString("orderId", orderList)
                navController.navigateUp() // to clear previous navigation history
                navController.navigate(R.id.donepayment, bundle)

//                sendNotification(
//                    "restcommerse admin",
//                    "en7z_LDtStmTBiku7Unq9J:APA91bGvNeLzvztzuUDFFLglYyWlA1xz6iFXkS9DL6XfoNRBiBx8smF0dQM0LoFtL--9QQjS4gZ2GjtukQhd0mRTsVt5FeofrWfPrdC8DS_3mdBBAJfuoJdKTXsrkcOpcLttA8bOyoHs",
//                    "order placed order id ${SinglePaymentApiResp.order_id}"
//                )
            } else {
                sharedPreferences.setSingleProductBuy(false)
                customToast(
                    requireContext(),
                    "${SinglePaymentApiResp?.message}",
                    R.drawable.ic_info
                )
            }
        }
    }

    private fun initPaymentObervser() {
        viewmodel.getPaymentResponse.observe(viewLifecycleOwner) { paymentApiResp ->
            Log.e("paymentApiResp", "paymentApiResp $paymentApiResp")

//            LoadingDialog.dismissProgressDialog()
            if (paymentApiResp?.is_success == true) {
                customToast(requireContext(), "Successful! ", R.drawable.success_toast_icon)

                val orderList = paymentApiResp.order_id
                val bundle = Bundle()
                bundle.putString("orderId", orderList)
                navController.navigateUp() // to clear previous navigation history
                navController.navigate(R.id.donepayment, bundle)

//                sendNotification(
//                    "restcommerse admin",
//                    "en7z_LDtStmTBiku7Unq9J:APA91bGvNeLzvztzuUDFFLglYyWlA1xz6iFXkS9DL6XfoNRBiBx8smF0dQM0LoFtL--9QQjS4gZ2GjtukQhd0mRTsVt5FeofrWfPrdC8DS_3mdBBAJfuoJdKTXsrkcOpcLttA8bOyoHs",
//                    "order placed order id ${paymentApiResp.order_id}"
//                )

            } else {
                customToast(
                    requireContext(),
                    " Unsuccessful - ${paymentApiResp?.message}",
                    R.drawable.ic_info
                )
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

    private fun setOnBackPressed() {
        // Initialize onBackPressedCallback
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
            }
        }

        // Add the onBackPressedCallback to the activity's onBackPressedDispatcher
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            onBackPressedCallback
        )
    }
}