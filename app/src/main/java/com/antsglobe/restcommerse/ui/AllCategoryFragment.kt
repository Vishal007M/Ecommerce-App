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
import com.antsglobe.restcommerse.R
import com.antsglobe.restcommerse.Utils.PreferenceManager
import com.antsglobe.restcommerse.adapter.CategoryAdapter
import com.antsglobe.restcommerse.databinding.FragmentAllCategoryBinding
import com.antsglobe.restcommerse.network.RetrofitClient
import com.antsglobe.restcommerse.viewmodel.AllCategoryViewModel
import com.antsglobe.restcommerse.viewmodel.ViewModelFactory

class AllCategoryFragment : Fragment(), CategoryAdapter.OnClickCategoryListener {
    private var swipeRefreshLayout: SwipeRefreshLayout? = null
    private lateinit var binding: FragmentAllCategoryBinding
    private lateinit var viewmodel: AllCategoryViewModel
    private lateinit var sharedPreferences: PreferenceManager
    private lateinit var categoryAdapter: CategoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // return inflater.inflate(R.layout.fragment_all_category, container, false)
        binding = FragmentAllCategoryBinding.inflate(inflater, container, false)
        viewmodel = ViewModelProvider(
            this, ViewModelFactory(RetrofitClient.apiService)
        )[AllCategoryViewModel::class.java]
        sharedPreferences = PreferenceManager(requireContext())

        viewmodel.allCategory(sharedPreferences.getEmail().toString())

        binding.llLoadingScreen.visibility = View.VISIBLE
        binding.llMainScreen.visibility = View.GONE

        swipeRefreshLayout = binding?.spRefreshlayout
        swipeRefreshLayout?.setOnRefreshListener {
            fetchData()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (sharedPreferences.getMode() == true) {
            binding.llMainScreen.setBackgroundColor(resources.getColor(R.color.blackfordark))
            binding.llLoadingScreen.setBackgroundColor(resources.getColor(R.color.blackfordark))
            binding.logoImageHome.setTextColor(resources.getColor(R.color.blackfordark))
            binding.backButton.setImageResource(R.drawable.ic_baseline_arrow_back_24_dark)
        }

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
        initObervserResponse()
    }


    override fun onCategoryIdClick(categoryId: String, catergroyNAme: String) {
        val bundle = Bundle()
        bundle.putString("categoryId", categoryId)
        bundle.putString("categoryName", catergroyNAme)
        findNavController().navigate(R.id.action_AllCategoryFragment_to_ProductListFragment, bundle)
    }

    private fun initObervserResponse() {
        viewmodel.getAllCategoryListResponse.observe(viewLifecycleOwner) { CategoryResp ->
            Log.e("CategoryResp", "onCreateView: $CategoryResp")
            swipeRefreshLayout?.isRefreshing = false
            binding.llMainScreen.visibility = View.VISIBLE
            binding.llLoadingScreen.visibility = View.GONE
            var list = CategoryResp?.filter { it.is_active == "true" }
            categoryAdapter = CategoryAdapter(list!!, requireContext())
            binding.rvCategoryList.layoutManager = GridLayoutManager(context, 3)
            categoryAdapter.setOnClickCategoryListener(this)
            binding.rvCategoryList.adapter = categoryAdapter
        }

//        binding.backButton.setOnClickListener {
//            findNavController().popBackStack()
//        }
    }

    private fun fetchData() {

        Handler(Looper.getMainLooper()).postDelayed({
            viewmodel.allCategory(sharedPreferences.getEmail().toString())
            initObervserResponse()
        }, 2000)
    }
}