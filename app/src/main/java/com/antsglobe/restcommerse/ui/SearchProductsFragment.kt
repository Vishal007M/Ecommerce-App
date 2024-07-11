package com.antsglobe.restcommerse.ui

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.antsglobe.aeroquiz.HomeCategoryAdapter
import com.antsglobe.aeroquiz.TopMostPopularAdapter
import com.antsglobe.restcommerse.R
import com.antsglobe.restcommerse.Utils.PreferenceManager
import com.antsglobe.restcommerse.adapter.AllProductListAdaptor
import com.antsglobe.restcommerse.adapter.PopularSearchAdapter
import com.antsglobe.restcommerse.adapter.RecentSearchAdapter
import com.antsglobe.restcommerse.databinding.FragmentSearchProductsBinding
import com.antsglobe.restcommerse.model.Response.AllProductsList
import com.antsglobe.restcommerse.network.RetrofitClient
import com.antsglobe.restcommerse.viewmodel.AllProductListViewModel
import com.antsglobe.restcommerse.viewmodel.HomeCategoryViewModel
import com.antsglobe.restcommerse.viewmodel.SearchListViewModel
import com.antsglobe.restcommerse.viewmodel.TopMostPopularViewModel
import com.antsglobe.restcommerse.viewmodel.ViewModelFactory
import java.util.Locale


class SearchProductsFragment : Fragment(), AllProductListAdaptor.OnClickProductListListener,
    PopularSearchAdapter.OnClickPopularProductListener, HomeCategoryAdapter.OnClickCategoryListener,
    RecentSearchAdapter.OnClickRecentProductListener, TopMostPopularAdapter.OnClickProductListener {

    private var _binding: FragmentSearchProductsBinding? = null
    private val binding get() = _binding!!

    private lateinit var productListViewModel: AllProductListViewModel
    private lateinit var sharedPreferences: PreferenceManager
    private lateinit var productAdapter: AllProductListAdaptor
    private lateinit var recentSearchAdapter: RecentSearchAdapter
    private lateinit var popularSearchAdapter: PopularSearchAdapter
    private lateinit var searchListViewModel: SearchListViewModel

    private lateinit var topMostPopularViewModel: TopMostPopularViewModel
    private lateinit var topMostPopularAdapter: TopMostPopularAdapter

    private lateinit var viewModelCategory: HomeCategoryViewModel
    private lateinit var homeCategoryAdapter: HomeCategoryAdapter

    private val REQUEST_CODE_SPEECH_INPUT = 1
    private var mList = ArrayList<AllProductsList>()

    var userEmail: String = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchProductsBinding.inflate(inflater, container, false)

        sharedPreferences = PreferenceManager(requireContext())
        userEmail = sharedPreferences.getEmail().toString()

        topMostPopularViewModel = ViewModelProvider(
            this,
            ViewModelFactory(RetrofitClient.apiService)
        )[TopMostPopularViewModel::class.java]

        // Initialize ViewModel
        productListViewModel = ViewModelProvider(
            this, ViewModelFactory(RetrofitClient.apiService)
        )[AllProductListViewModel::class.java]

        productListViewModel = ViewModelProvider(
            this, ViewModelFactory(RetrofitClient.apiService)
        )[AllProductListViewModel::class.java]

        searchListViewModel = ViewModelProvider(
            this, ViewModelFactory(RetrofitClient.apiService)
        )[SearchListViewModel::class.java]

        viewModelCategory = ViewModelProvider(
            this,
            ViewModelFactory(RetrofitClient.apiService)
        )[HomeCategoryViewModel::class.java]

        searchListViewModel.getRecentSearchListVM(userEmail)
        getSearchListInitObserver()

        searchListViewModel.getPopularSearchListVM(userEmail)
        getPopularSearchListInitObserver()

        productListViewModel.allProductListVM(userEmail)
        getproductListInitObserver()

        topMostPopularViewModel.getTopMostPopularVM(userEmail)
        initObserver()

        viewModelCategory.getHomeCategoryVM(userEmail)
        initObserverCatogery()


        return binding.root
    }

    private fun initObserver() {

        topMostPopularViewModel.getTopMostPopularItem.observe(viewLifecycleOwner) { mostPopularResp ->
//            binding.llLoadingScreen.visibility = View.GONE
//            binding.llMainScreen.visibility = View.VISIBLE
            Log.e("mostPopularResp", "onCreateView: $mostPopularResp")
//            val sortedNotificationList = notificationResp.sortedByDescending { it.create_date }
            topMostPopularAdapter = TopMostPopularAdapter(mostPopularResp, requireContext())
            val layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            binding.mostPopularRecyclerView.layoutManager = layoutManager
            topMostPopularAdapter.setOnClickProductListener(this)
            binding.mostPopularRecyclerView.adapter = topMostPopularAdapter

//            val itemCount: Int = notificationAdapter.getItemCount()
//            Toast.makeText(this@Notification, "Item Count: $itemCount", Toast.LENGTH_SHORT).show()

        }

        topMostPopularViewModel.getTopMostPopular.observe(viewLifecycleOwner) { paper ->

            if (paper?.token == null) {
//                Toast.makeText(context, "access", Toast.LENGTH_SHORT).show()
//                binding.allDetail.text = "All Test | Total - 0   Solved - 0  Unsolved - 0"
            } else {
//                binding.allDetail.text = "All Test | Total - ${paper?.Total}   Solved - ${paper?.total_solved}  Unsolved - ${paper?.total_unsolved}"
            }

        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (sharedPreferences.getMode() == true) {
            binding.mostpopular.setTextColor(Color.BLACK)
            binding.categories.setTextColor(Color.BLACK)
            binding.linearLayout5.setBackgroundColor(Color.BLACK)
            binding.fullscreen.setBackgroundColor(Color.BLACK)
            binding.linearLayout6.setBackgroundColor(Color.BLACK)
            binding.linearLayout7.setBackgroundColor(Color.BLACK)
            binding.recentSearchLayout.setBackgroundColor(Color.GRAY)
            binding.recentsearch.setTextColor(Color.WHITE)
            binding.popularSearch.setTextColor(Color.WHITE)
            binding.popularimg.setImageResource(R.drawable.popular)
            binding.recentimg.setImageResource(R.drawable.history)
            binding.backButton.setImageResource(R.drawable.ic_baseline_arrow_back_24_dark)
            binding.logoImageHome.setTextColor(Color.BLACK)
        }
        productAdapter = AllProductListAdaptor(emptyList(), requireContext())
        // Initialize with empty list
        binding.rvProductList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = productAdapter
        }

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.searchProduct.isFocusable = true
        binding.ivSearch.setOnClickListener {
            fetchCurrentLocation()
        }

        ///-----------------------------------------------------------------
        binding.recentSearchLayout.visibility = View.VISIBLE
        binding.viewRecentBackground.visibility = View.VISIBLE

        binding.viewRecentBackground.setOnClickListener {
//            binding.recentSearchLayout.visibility = View.GONE
//            binding.viewRecentBackground.visibility = View.GONE
            findNavController().popBackStack()

        }

        binding.searchProduct.setOnClickListener {
            binding.recentSearchLayout.visibility = View.VISIBLE
            binding.viewRecentBackground.visibility = View.VISIBLE

        }

        binding.searchProduct.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {


                query?.let {
                    val capitalizedQuery = capitalizeFirstWord(it)
                    searchListViewModel.addRecentSearchVM(capitalizedQuery, userEmail)
                    addSearchListInitObserver()
                    binding.recentSearchLayout.visibility = View.GONE
                    binding.viewRecentBackground.visibility = View.GONE
                    binding.linearLayout5.visibility = View.GONE
                }

                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
//                newText?.let {
//                    productListViewModel.searchProducts(it)
//                }
                binding.recentSearchLayout.visibility = View.VISIBLE
                binding.viewRecentBackground.visibility = View.VISIBLE
                binding.linearLayout5.visibility = View.VISIBLE

                searchListViewModel.getRecentSearchListVM(userEmail)
                getSearchListInitObserver()

                searchListViewModel.getPopularSearchListVM(userEmail)
                getPopularSearchListInitObserver()

                if (newText != null) {
                    filterList(newText.trim())
                }
                return true
            }
        })



        binding.searchProduct.setOnSearchClickListener {
            binding.recentSearchLayout.visibility = View.VISIBLE
            binding.viewRecentBackground.visibility = View.VISIBLE
        }

        binding.searchProduct.setOnCloseListener {
            binding.recentSearchLayout.visibility = View.VISIBLE
            binding.viewRecentBackground.visibility = View.VISIBLE
            binding.linearLayout5.visibility = View.VISIBLE

            searchListViewModel.getRecentSearchListVM(userEmail)
            getSearchListInitObserver()

            searchListViewModel.getPopularSearchListVM(userEmail)
            getPopularSearchListInitObserver()
            true

        }

    }

    private fun capitalizeFirstWord(query: String): String {
        if (query.isEmpty()) return query

        return query.substring(0, 1).toUpperCase() + query.substring(1).toLowerCase()
    }

    private fun initObserverCatogery() {
        viewModelCategory.getHomeCategoryItem.observe(viewLifecycleOwner) { homeCategoryResp ->
            Log.e("homeCategoryResp", "onCreateView: $homeCategoryResp")

//            val sortedNotificationList = notificationResp.sortedByDescending { it.create_date }
            homeCategoryAdapter = HomeCategoryAdapter(homeCategoryResp, requireContext())

            val layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            binding.homeCategoriesRecyclerView.layoutManager = layoutManager
            binding.homeCategoriesRecyclerView.adapter = homeCategoryAdapter
            homeCategoryAdapter.setOnClickCategoryListener(this)

            //            val itemCount: Int = notificationAdapter.getItemCount()
//            Toast.makeText(this@Notification, "Item Count: $itemCount", Toast.LENGTH_SHORT).show()

        }

        viewModelCategory.getHomeCategory.observe(viewLifecycleOwner) { paper ->

            if (paper?.token == null) {
//                Toast.makeText(context, "access", Toast.LENGTH_SHORT).show()
//                binding.allDetail.text = "All Test | Total - 0   Solved - 0  Unsolved - 0"
            } else {
//                binding.allDetail.text = "All Test | Total - ${paper?.Total}   Solved - ${paper?.total_solved}  Unsolved - ${paper?.total_unsolved}"
            }

        }
    }

    private fun getproductListInitObserver() {
        productListViewModel.allProductItems.observe(viewLifecycleOwner) { productList ->
            mList.clear()
            productAdapter.updateStudentList(productList)
            productAdapter.setOnClickListener(this)
            mList.addAll(productList)
        }
    }


    private fun getPopularSearchListInitObserver() {
        searchListViewModel.getPopularSearchListData.observe(viewLifecycleOwner) { mostPopularResp ->
            Log.e("mostPopularResp", "onCreateView: $mostPopularResp")


            popularSearchAdapter = PopularSearchAdapter(mostPopularResp!!, requireContext())
            popularSearchAdapter.setOnPopularClickProductListener(this)
            binding.popularSearchRecycleView.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            binding.popularSearchRecycleView.adapter = popularSearchAdapter


        }

    }

    private fun getSearchListInitObserver() {

        searchListViewModel.getRecentSearchListData.observe(viewLifecycleOwner) { mostPopularResp ->
            Log.e("mostPopularResp", "onCreateView: $mostPopularResp")


            recentSearchAdapter = RecentSearchAdapter(mostPopularResp!!, requireContext())
            recentSearchAdapter.setOnRecentClickProductListener(this)
            binding.recentSearchRecycleView.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            binding.recentSearchRecycleView.adapter = recentSearchAdapter


        }

    }

    private fun addSearchListInitObserver() {
        searchListViewModel.addSearchData.observe(viewLifecycleOwner) { productdata ->
            if (productdata.is_success) {
            }
        }
    }


    private fun filterList(query: String?) {
        if (query != null) {
            val filteredList = ArrayList<AllProductsList>()
            val lowerCaseQuery = query.lowercase(Locale.ROOT) // Convert query to lowercase

            for (i in mList) {
                val productNameLowerCase = i.productname.lowercase(Locale.ROOT)
                binding.rvProductList.visibility = View.VISIBLE
                binding.searchEmpty.visibility = View.GONE
                binding.linearLayout5.visibility = View.VISIBLE

                if (productNameLowerCase.contains(lowerCaseQuery)) {
                    filteredList.add(i)
                }

            }

            if (filteredList.isEmpty()) {
                binding.rvProductList.visibility = View.GONE
                binding.searchEmpty.visibility = View.VISIBLE
                binding.linearLayout5.visibility = View.GONE

                //customToast(requireContext(), "No Data found", R.drawable.ic_info)
                //Toast.makeText(context, "No Data found", Toast.LENGTH_SHORT).show()
            } else {
                productAdapter.updateStudentList(filteredList)
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1000 -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchCurrentLocation()
            }
        }
    }

    private fun fetchCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.RECORD_AUDIO
            )
            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(android.Manifest.permission.RECORD_AUDIO),
                1000
            )
            return
        }


        try {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text")

            result.launch(intent)

        } catch (e: Exception) {
            Toast.makeText(requireContext(), " " + e.message, Toast.LENGTH_SHORT).show()

        }

    }

    private val result =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            if (result.resultCode == RESULT_OK) {


                val results = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                Log.d("SpeechRecognition", "Recognized text: $results")

//               binding.searchProduct.setQuery(results?.get(0).toString().trim(), true)
                val recognizedText = results?.get(0)

                binding.searchProduct.setQuery(recognizedText.toString().trim(), true)

                binding.recentSearchLayout.visibility = View.GONE
                binding.viewRecentBackground.visibility = View.GONE
            }
        }

    override fun onResume() {
        super.onResume()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onProductListClick(pId: String) {
        val bundle = Bundle()
        bundle.putString("pid", pId)
        findNavController().navigate(R.id.action_SearchProductsFragment_to_Product, bundle)
    }

    override fun onaddtoCartClick(pId: String, price: String) {
        TODO("Not yet implemented")
    }

    override fun onDeleteCartClick(pId: String) {
        TODO("Not yet implemented")
    }

    override fun onPopularProductIdClick(pName: String) {
        binding.searchProduct.setQuery(pName.trim(), true)
    }

    override fun onRecentProductIdClick(pName: String) {
        binding.searchProduct.setQuery(pName.trim(), true)
    }

    override fun onProductIdClick(pId: String) {
        val bundle = Bundle()
        bundle.putString("pid", pId)
        findNavController().navigate(R.id.action_SearchProductsFragment_to_Product, bundle)
    }

    override fun onWishaddclick(pId: String) {
        val curretuseremail = sharedPreferences.getEmail()
        topMostPopularViewModel.addtowishlist(curretuseremail.toString(), pId)
    }

    override fun onWishdeleteclick(pId: String) {
        val curretuseremail = sharedPreferences.getEmail()
        topMostPopularViewModel.deletefromwishlist(curretuseremail.toString(), pId)
    }

    override fun onCategoryIdClick(categoryId: String, CategroyName: String) {
        val bundle = Bundle()
        bundle.putString("categoryId", categoryId)
        bundle.putString("categoryName", CategroyName)
        findNavController().navigate(
            R.id.action_SearchProductsFragment_to_ProductListFragment,
            bundle
        )
    }

}