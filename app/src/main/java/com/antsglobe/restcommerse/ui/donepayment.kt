package com.antsglobe.restcommerse.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.antsglobe.restcommerse.R
import com.antsglobe.restcommerse.Utils.PreferenceManager
import com.antsglobe.restcommerse.adapter.OrderItemAdaptor
import com.antsglobe.restcommerse.databinding.FragmentDonepaymentBinding
import com.antsglobe.restcommerse.network.RetrofitClient
import com.antsglobe.restcommerse.viewmodel.DonePaymentViewModel
import com.antsglobe.restcommerse.viewmodel.ViewModelFactory


class donepayment : Fragment() {

    private var price: Int = 0
    private var totalAmount: Int = 0
    private var binding: FragmentDonepaymentBinding? = null
    private lateinit var sharedPreferences: PreferenceManager
    private lateinit var orderItemAdapter: OrderItemAdaptor
    private lateinit var viewmodel: DonePaymentViewModel
    private var orderId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            orderId = it.getString("orderId")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDonepaymentBinding.inflate(inflater, container, false)
        viewmodel = ViewModelProvider(
            this, ViewModelFactory(RetrofitClient.apiService)
        )[DonePaymentViewModel::class.java]
        sharedPreferences = PreferenceManager(requireContext())
        setOnBackPressed()

        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val email = sharedPreferences.getEmail()
        viewmodel.getOrderDetailsResponse(email!!, orderId.toString())

        viewmodel.getGetOrderDetailsResponse.observe(viewLifecycleOwner) { getOrderDetailsResponse ->
            Log.e("GetOrderListResp", "onCreateView: $getOrderDetailsResponse")
            binding!!.tvPayableAmount.text = "₹ ${getOrderDetailsResponse?.orderMaster?.grandtotal}"
            binding!!.tvDiscountPrice.text = "-₹ ${getOrderDetailsResponse?.orderMaster?.promodisc}"

            binding!!.tvTaxPerc.text = "${getOrderDetailsResponse?.orderMaster?.taxper}.00"
            binding!!.tvTaxPrice.text = "₹ ${getOrderDetailsResponse?.orderMaster?.taxamt}"

            if (getOrderDetailsResponse?.orderMaster?.shipcharge == 0) {
                binding!!.tvDevilery.text = "FREE"
            } else {
                binding!!.tvDevilery.text =
                    "-₹ ${getOrderDetailsResponse?.orderMaster?.shipcharge}.00"
            }
        }

        if (sharedPreferences.getMode() == true) {
            binding?.fullscreen?.setBackgroundColor(resources.getColor(R.color.blackfordark))
            binding?.tvConfirm?.setTextColor(resources.getColor(R.color.whitefordark))
            binding?.orderdetail?.setTextColor(resources.getColor(R.color.whitefordark))
            binding?.tvText?.setTextColor(resources.getColor(R.color.whitefordark))
            binding?.tvTotalAmount?.setTextColor(resources.getColor(R.color.whitefordark))
            binding?.tvPricedetails?.setTextColor(resources.getColor(R.color.whitefordark))
            binding?.tvCartvalue?.setTextColor(resources.getColor(R.color.whitefordark))
            binding?.tvDisc?.setTextColor(resources.getColor(R.color.whitefordark))
            binding?.tvPayableAmount?.setTextColor(resources.getColor(R.color.whitefordark))
            binding?.tvAmount?.setTextColor(resources.getColor(R.color.whitefordark))
            binding?.btnDone?.setTextColor(resources.getColor(R.color.blackfordark))
            binding?.btnShopping?.setTextColor(resources.getColor(R.color.blackfordark))

//            binding?.tvtax?.setTextColor(resources.getColor(R.color.blackfordark))
//            binding?.tvTaxPerc?.setTextColor(resources.getColor(R.color.blackfordark))
//            binding?.Perc?.setTextColor(resources.getColor(R.color.blackfordark))
//            binding?.tvTaxPrice?.setTextColor(resources.getColor(R.color.blackfordark))

            binding?.rlPricedetails?.setBackgroundResource(R.drawable.profile_round_corner_bg_addresses_dark)
        }
        viewmodel.orderDetailsList.observe(viewLifecycleOwner) { OrderListResp ->
            orderItemAdapter = OrderItemAdaptor(OrderListResp!!)
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

            binding!!.tvCartPrice.text = "₹ " + totalAmount.toString() + ".0"
            binding!!.tvTotalAmount.text = "₹ " + totalAmount.toString()
        }

        binding!!.btnShopping.setOnClickListener {
            val intent = Intent(requireContext(), HomeActivity::class.java)
            startActivity(intent)
        }
        binding!!.btnDone.setOnClickListener {

            val intent = Intent(requireContext(), HomeActivity::class.java)
            startActivity(intent)

        }

    }

    private fun setOnBackPressed() {
        // Initialize onBackPressedCallback
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(requireContext(), HomeActivity::class.java)
                startActivity(intent)
            }
        }

        // Add the onBackPressedCallback to the activity's onBackPressedDispatcher
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            onBackPressedCallback
        )
    }
}