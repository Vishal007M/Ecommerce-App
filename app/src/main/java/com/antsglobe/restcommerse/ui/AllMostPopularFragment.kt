package com.antsglobe.restcommerse.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.antsglobe.aeroquiz.AllMostPopularAdapter
import com.antsglobe.restcommerse.R
import com.antsglobe.restcommerse.Utils.PreferenceManager
import com.antsglobe.restcommerse.databinding.FragmentAllMostPopularBinding
import com.antsglobe.restcommerse.network.RetrofitClient
import com.antsglobe.restcommerse.viewmodel.AllMostPopularViewModel
import com.antsglobe.restcommerse.viewmodel.ViewModelFactory
import com.antsglobe.restcommerse.viewmodel.WishlistViewmodel


class AllMostPopularFragment : Fragment(), AllMostPopularAdapter.OnClickProductListener {

    private lateinit var email: String
    private var _binding: FragmentAllMostPopularBinding? = null
    private val binding get() = _binding!!
    private var swipeRefreshLayout: SwipeRefreshLayout? = null
    private lateinit var viewModel: AllMostPopularViewModel
    private lateinit var allMostPopularAdapter: AllMostPopularAdapter
    private lateinit var sharedPreferences: PreferenceManager

    private lateinit var wishViewmodel: WishlistViewmodel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentAllMostPopularBinding.inflate(inflater, container, false)
        sharedPreferences = PreferenceManager(requireContext())
        binding.llLoadingScreen.visibility = View.VISIBLE
        binding.llMainScreen.visibility = View.GONE

        swipeRefreshLayout = binding.spRefreshlayout
        swipeRefreshLayout?.setOnRefreshListener {
            fetchData()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        email = sharedPreferences.getEmail().toString().trim()

        wishViewmodel = ViewModelProvider(
            this, ViewModelFactory(RetrofitClient.apiService)
        )[WishlistViewmodel::class.java]
        wishViewmodel.getcartlist(email)

        wishViewmodel.cartListsize.observe(viewLifecycleOwner) { count ->
            Log.d("count", count.toString())
            if (count != null) {
                if (count.compareTo(1) >= 0) {
                    binding.cartcount.visibility = View.VISIBLE
                    binding.cartcount.text = count.toString()
                } else {
                    binding.cartcount.visibility = View.GONE
                }
            }
        }


        if (sharedPreferences.getMode() == true) {
            binding.fullscreen.setBackgroundColor(resources.getColor(R.color.blackfordark))
            binding.logoImageHome.setTextColor(resources.getColor(R.color.blackfordark))
            binding.backButton.setImageResource(R.drawable.ic_baseline_arrow_back_24_dark)
            binding.carticonlayout.setBackgroundColor(resources.getColor(R.color.blackfordark))
            binding.cartcount.setBackgroundColor(resources.getColor(R.color.blackfordark))
            binding.cart.setImageDrawable(resources.getDrawable(R.drawable.add_to_cart_dark))

        } else {
            binding.carticonlayout.setBackgroundColor(resources.getColor(R.color.whitefordark))
            binding.cartcount.setBackgroundColor(resources.getColor(R.color.blackfordark))
            binding.cart.setImageDrawable(resources.getDrawable(R.drawable.add_to_cart))

        }

        binding.countcard.setOnClickListener {
            findNavController().navigate(R.id.action_HomeFragment_to_bottom_menu_my_cart)
        }

        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(RetrofitClient.apiService)
        )[AllMostPopularViewModel::class.java]

        viewModel.getAllMostPopularVM(email)
        initObserver()

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

    }

    override fun onProductIdClick(pId: String) {
        val bundle = Bundle()
        bundle.putString("pid", pId)
        findNavController().navigate(R.id.action_MostPopular_to_Product, bundle)
    }

    override fun onWishaddclick(pId: String) {
        val curretuseremail = sharedPreferences.getEmail()
        viewModel.addtowishlist(curretuseremail.toString(), pId)
    }

    override fun onWishdeleteclick(pId: String) {
        val curretuseremail = sharedPreferences.getEmail()
        viewModel.deletefromwishlist(curretuseremail.toString(), pId)
    }

    private fun initObserver() {


        viewModel.getMostPopularItem.observe(viewLifecycleOwner) { mostPopularResp ->
            Log.e("mostPopularResp", "onCreateView: $mostPopularResp")
            swipeRefreshLayout?.isRefreshing = false
            binding.llMainScreen.visibility = View.VISIBLE
            binding.llLoadingScreen.visibility = View.GONE
//            val sortedNotificationList = notificationResp.sortedByDescending { it.create_date }
            allMostPopularAdapter = AllMostPopularAdapter(mostPopularResp!!, requireContext())

            val gridLayoutManager = GridLayoutManager(
                requireContext(),
                2
            ) // Change 2 to the number of columns you want in your grid
            binding.mostPopularRecyclerView.layoutManager = gridLayoutManager
            allMostPopularAdapter.setOnClickProductListener(this)
            binding.mostPopularRecyclerView.adapter = allMostPopularAdapter
            //            val itemCount: Int = notificationAdapter.getItemCount()
//            Toast.makeText(this@Notification, "Item Count: $itemCount", Toast.LENGTH_SHORT).show()
        }

        viewModel.getMostPopular.observe(viewLifecycleOwner) { paper ->
            if (paper?.token == null) {
//                Toast.makeText(context, "access", Toast.LENGTH_SHORT).show()
//                binding.allDetail.text = "All Test | Total - 0   Solved - 0  Unsolved - 0"
            } else {
//                binding.allDetail.text = "All Test | Total - ${paper?.Total}   Solved - ${paper?.total_solved}  Unsolved - ${paper?.total_unsolved}"
            }
        }
    }

    private fun fetchData() {
        Handler(Looper.getMainLooper()).postDelayed({
            viewModel.getAllMostPopularVM(email)
            initObserver()
        }, 2000)
    }
}