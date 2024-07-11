package com.antsglobe.restcommerse.ui

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.antsglobe.restcommerse.R
import com.antsglobe.restcommerse.Utils.PreferenceManager
import com.antsglobe.restcommerse.adapter.OrderDetailsItemAdaptor
import com.antsglobe.restcommerse.databinding.FragmentOrderDetailsBinding
import com.antsglobe.restcommerse.model.OrderItemsList
import com.antsglobe.restcommerse.network.RetrofitClient
import com.antsglobe.restcommerse.viewmodel.DonePaymentViewModel
import com.antsglobe.restcommerse.viewmodel.ViewModelFactory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

class OrderDetailsFragment : Fragment() {

    private var email: String? = null
    private var totalAmount: Int = 0
    private var price: Int = 0
    private var binding: FragmentOrderDetailsBinding? = null
    private lateinit var sharedPreferences: PreferenceManager
    private lateinit var orderItemAdapter: OrderDetailsItemAdaptor
    private lateinit var viewmodel: DonePaymentViewModel
    private var orderId: String? = null
    private var transactionId: String? = null
    private var list = ArrayList<OrderItemsList>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            // param1 = it.getString
            orderId = it.getString("orderId")
//            transactionId = it.getString("transactionId")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // return inflater.inflate(R.layout.fragment_order_details, container, false)
        binding = FragmentOrderDetailsBinding.inflate(inflater, container, false)
        viewmodel = ViewModelProvider(
            this, ViewModelFactory(RetrofitClient.apiService)
        )[DonePaymentViewModel::class.java]
        sharedPreferences = PreferenceManager(requireContext())
        email = sharedPreferences.getEmail()

        return binding!!.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewmodel.getOrderDetailsResponse(email!!, orderId.toString())

        viewmodel.getGetOrderDetailsResponse.observe(viewLifecycleOwner) { getOrderDetailsResponse ->
            Log.e("GetOrderViewResp", "OrderView: $getOrderDetailsResponse")


            if (sharedPreferences.getMode() == true) {
                binding?.heading?.setTextColor(resources.getColor(R.color.blackfordark))
                binding?.btnReorder?.setTextColor(resources.getColor(R.color.blackfordark))
                binding?.tvPayment?.setTextColor(resources.getColor(R.color.whitefordark))
                binding?.tvTransactionId?.setTextColor(resources.getColor(R.color.whitefordark))
                binding?.expected?.setTextColor(resources.getColor(R.color.whitefordark))
                binding?.tvpd?.setTextColor(resources.getColor(R.color.whitefordark))
                binding?.tvtotal?.setTextColor(resources.getColor(R.color.whitefordark))
                binding?.tvTotalAmount?.setTextColor(resources.getColor(R.color.whitefordark))
                binding?.tvpricedetails?.setTextColor(resources.getColor(R.color.whitefordark))
                binding?.tvOrderId?.setTextColor(resources.getColor(R.color.whitefordark))
                binding?.tvStatus?.setTextColor(resources.getColor(R.color.whitefordark))
                binding?.tvadress?.setTextColor(resources.getColor(R.color.whitefordark))
                binding?.tvcartvalue?.setTextColor(resources.getColor(R.color.whitefordark))
                binding?.tvamountpayable?.setTextColor(resources.getColor(R.color.whitefordark))
                binding?.tvPayableAmount?.setTextColor(resources.getColor(R.color.whitefordark))
                binding!!.discount.setTextColor(resources.getColor(R.color.whitefordark))
                binding!!.status.setTextColor(Color.WHITE)
                binding?.backButton?.setImageResource(R.drawable.ic_baseline_arrow_back_24_dark)
                binding?.fullscreen?.setBackgroundColor(Color.BLACK)
                binding?.clPriceView?.setCardBackgroundColor(resources.getColor(R.color.wish))
                binding?.cvCustomerDetails?.setCardBackgroundColor(resources.getColor(R.color.wish))
                binding?.cvOrder?.setCardBackgroundColor(resources.getColor(R.color.wish))

            }
            /*Address Details*/
            binding!!.tvCustomerName.text = getOrderDetailsResponse?.orderMaster?.shipname
            binding!!.tvCustomerNumber.text = getOrderDetailsResponse?.orderMaster?.mobno
            binding!!.tvCustomerAddress.text = getOrderDetailsResponse?.orderMaster?.shipaddress

            /*Payment details*/
            binding!!.tvPayment.text =
                "Payment : ${getOrderDetailsResponse?.orderMaster?.payment_method}"
            binding!!.tvPayableAmount.text = "₹ ${getOrderDetailsResponse?.orderMaster?.grandtotal}"


//            binding!!.EditI.text = "₹ ${getOrderDetailsResponse?.orderMaster?.address_type}"

            val profilePicName = getOrderDetailsResponse?.orderMaster?.address_type

            if (profilePicName != null) {

                val drawableMap = mapOf(
                    "Home" to R.drawable.home,
                    "Office" to R.drawable.address_office,
                    "Other" to R.drawable.news,
                )
                val drawableName = profilePicName

                val imageView = binding!!.EditI

                imageView.setImageResource(drawableMap[drawableName] ?: 0)

            } else {
                binding!!.EditI.setImageResource(R.drawable.boy1)
            }


            binding!!.tvTaxPerc.text = "${getOrderDetailsResponse?.orderMaster?.taxper}.00"
            binding!!.tvTaxPrice.text = "₹ ${getOrderDetailsResponse?.orderMaster?.taxamt}"

            if (getOrderDetailsResponse?.orderMaster?.shipcharge == 0) {
                binding!!.tvDevilery.text = "FREE"
            } else {
                binding!!.tvDevilery.text =
                    "-₹ ${getOrderDetailsResponse?.orderMaster?.shipcharge}.00"
            }

            val inputExpectedDateTime = getOrderDetailsResponse?.orderMaster?.delivary_date
            val formatterExpectedInput1 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")
            val formatterExpectedInput2 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SS")
            var dateExpectedTime: LocalDateTime? = null
            try {
                dateExpectedTime =
                    LocalDateTime.parse(inputExpectedDateTime, formatterExpectedInput1)
            } catch (e: DateTimeParseException) {
                // Try the alternative format
                try {
                    dateExpectedTime =
                        LocalDateTime.parse(inputExpectedDateTime, formatterExpectedInput2)
                } catch (e: DateTimeParseException) {
                    // Handle the case when both formats fail to parse
                    println("Failed to parse input date: $inputExpectedDateTime")
                }
            }
            val formatterExpectedOutput =
                DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm a", Locale.ENGLISH)
            val formattedExpectedDateTime = dateExpectedTime?.format(formatterExpectedOutput)
            binding!!.tvExpectedDate.text = formattedExpectedDateTime
            binding!!.tvDiscountPrice.text = "-₹ ${getOrderDetailsResponse?.orderMaster?.promodisc}"


            /*Order status*/
            val inputDateTime = getOrderDetailsResponse?.orderMaster?.orderdate
            val formatterInput1 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")
            val formatterInput2 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SS")
            var dateTime: LocalDateTime? = null
            try {
                dateTime = LocalDateTime.parse(inputDateTime, formatterInput1)
            } catch (e: DateTimeParseException) {
                // Try the alternative format
                try {
                    dateTime = LocalDateTime.parse(inputDateTime, formatterInput2)
                } catch (e: DateTimeParseException) {
                    // Handle the case when both formats fail to parse
                    println("Failed to parse input date: $inputDateTime")
                }
            }
            val formatterOutput =
                DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm a", Locale.ENGLISH)
            val formattedDateTime = dateTime?.format(formatterOutput)
            binding!!.tvOrderDate.text = formattedDateTime

            binding!!.tvOrderId.text = "Order Id - $orderId"
            binding!!.tvTransactionId.text =
                "Transcation Id: ${getOrderDetailsResponse?.orderMaster?.transaction_id}"

            when (getOrderDetailsResponse?.orderMaster?.delivery_status) {
                "Pending" -> {
                    binding!!.tvStatus.apply {
                        text = getOrderDetailsResponse?.orderMaster?.delivery_status
                        setTextColor(ContextCompat.getColor(context, R.color.progress_two_star))
                    }

                }

                else -> {
                    binding!!.tvStatus.apply {
                        text = getOrderDetailsResponse?.orderMaster?.delivery_status
                        setTextColor(android.graphics.Color.GREEN)
                    }
                }
            }

        }

        viewmodel.orderDetailsList.observe(viewLifecycleOwner) { OrderListResp ->

            orderItemAdapter = OrderDetailsItemAdaptor(OrderListResp!!)
            binding!!.rvItemRecyclerView.layoutManager = LinearLayoutManager(context)
            binding!!.rvItemRecyclerView.adapter = orderItemAdapter

            for (countLoop in OrderListResp) {
                try {
                    if (countLoop!!.dis_price == null) {
                        price = countLoop!!.original_price!!.toInt()
                    } else {
                        price = countLoop!!.dis_price!!.toInt()
                    }
                    totalAmount += price * countLoop.quantity!!.toInt()
                } catch (e: Exception) {
                    println("catch the error" + e.message)
                }
//                totalAmount += countLoop!!.dis_price!!.toInt() * countLoop.quantity!!.toInt()
            }

            binding!!.tvOrginalPrice.text = totalAmount.toString()
            binding!!.tvTotalAmount.text = "₹" + totalAmount.toString()
        }


        binding!!.btnInvoice.setOnClickListener {
            Toast.makeText(context, "soon update", Toast.LENGTH_SHORT).show()
        }

        binding?.backButton?.setOnClickListener {
            findNavController().popBackStack()
        }

        binding?.btnReorder?.setOnClickListener {
            viewmodel.getOrderRepeatResponse(email!!, orderId!!)
            initGetRepeatOrderOsbervser()
        }

    }

    private fun initGetRepeatOrderOsbervser() {
        viewmodel.getOrderRepeatResponse.observe(viewLifecycleOwner) { getOrderRepeatResponse ->
            Log.e("GetOrderListResp", "getOrderRepeatResponse: $getOrderRepeatResponse")

            if (getOrderRepeatResponse?.is_success == true) {
                findNavController().navigate(R.id.action_order_fragment_to_bottom_menu_my_cart)
            } else {
                Toast.makeText(
                    requireContext(),
                    "${getOrderRepeatResponse?.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}