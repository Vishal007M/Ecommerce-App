package com.antsglobe.restcommerse.ui

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.antsglobe.restcommerse.R
import com.antsglobe.restcommerse.Utils.PreferenceManager
import com.antsglobe.restcommerse.adapter.MyOrdersAdapter
import com.antsglobe.restcommerse.databinding.FragmentMyOrderBinding
import com.antsglobe.restcommerse.network.RetrofitClient
import com.antsglobe.restcommerse.viewmodel.MyOrderViewModel
import com.antsglobe.restcommerse.viewmodel.ViewModelFactory

class MyOrderFragment : Fragment(), MyOrdersAdapter.OnClickOrderListener {

    private var binding: FragmentMyOrderBinding? = null
    private lateinit var viewmodel: MyOrderViewModel
    private lateinit var sharedPreferences: PreferenceManager
    private lateinit var adapter: MyOrdersAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        sharedPreferences = PreferenceManager(requireContext())
        binding = FragmentMyOrderBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val currentuser = sharedPreferences.getEmail().toString().trim()
        viewmodel = ViewModelProvider(
            this, ViewModelFactory(RetrofitClient.apiService)
        )[MyOrderViewModel::class.java]

        if (sharedPreferences.getMode() == true) {
            binding?.ShippingPolicy?.setBackgroundResource(R.color.blackfordark)
            binding?.heading?.setTextColor(resources.getColor(R.color.blackfordark))
            binding?.backButton?.setImageResource(R.drawable.ic_baseline_arrow_back_24_dark)
            binding?.tvemptyorders?.setTextColor(Color.WHITE)
        }

        binding?.rvProductList?.visibility = View.GONE
        binding?.llLoadingScreen?.visibility = View.VISIBLE
        viewmodel.getorderlist(currentuser)
        binding?.backButton?.setOnClickListener {
            findNavController().popBackStack()
        }

        viewmodel.orderresponse.observe(viewLifecycleOwner) {
            binding?.rvProductList?.visibility = View.VISIBLE
            binding?.llLoadingScreen?.visibility = View.GONE
            val sortedList = it.reversed()
//            adapter.notifyDataSetChanged()
            adapter = MyOrdersAdapter(sortedList, requireContext())
            adapter.setOnClickOrderListener(this)

            if (it.isEmpty() && it.size >= 0) {
                binding?.clMainScreen?.visibility = View.GONE
                binding?.llEmptyScreen?.visibility = View.VISIBLE
                customToast(requireContext(), "Order data not found", R.drawable.ic_info)
            } else {
                binding?.clMainScreen?.visibility = View.VISIBLE
                binding?.llEmptyScreen?.visibility = View.GONE
            }
            val layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            binding?.rvProductList?.layoutManager = layoutManager
            binding?.rvProductList?.adapter = adapter
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

    override fun onOrderIdClick(orderId: String, transactionId: String) {
        val bundle = Bundle()
        bundle.putString("orderId", orderId)
        bundle.putString("transactionId", transactionId)
        findNavController().navigate(R.id.action_my_order_to_order_fragment, bundle)
    }
}