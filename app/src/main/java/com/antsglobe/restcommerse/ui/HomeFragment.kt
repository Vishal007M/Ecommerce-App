package com.antsglobe.restcommerse.ui

import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.antsglobe.aeroquiz.CartListAdapter
import com.antsglobe.aeroquiz.GetAllPinCodeAdapter
import com.antsglobe.aeroquiz.HomeCategoryAdapter
import com.antsglobe.aeroquiz.TopMostPopularAdapter
import com.antsglobe.aeroquiz.Utils.NetworkUtils
import com.antsglobe.restcommerse.R
import com.antsglobe.restcommerse.Utils.PreferenceManager
import com.antsglobe.restcommerse.adapter.AddressListAdaptor
import com.antsglobe.restcommerse.databinding.FragmentHomeBinding
import com.antsglobe.restcommerse.model.Response.GetAllPinCodeList
import com.antsglobe.restcommerse.network.RetrofitClient
import com.antsglobe.restcommerse.viewmodel.AddressListViewModel
import com.antsglobe.restcommerse.viewmodel.CartListViewModel
import com.antsglobe.restcommerse.viewmodel.GetAllPinCodeViewModel
import com.antsglobe.restcommerse.viewmodel.HomeCategoryViewModel
import com.antsglobe.restcommerse.viewmodel.TopMostPopularViewModel
import com.antsglobe.restcommerse.viewmodel.ViewModelFactory
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.messaging.FirebaseMessaging
import java.util.Locale


class HomeFragment : Fragment(), TopMostPopularAdapter.OnClickProductListener,
    HomeCategoryAdapter.OnClickCategoryListener, GetAllPinCodeAdapter.OnClickPinCodeListener/*  , PopularSearchAdapter.OnClickPopularProductListener,
   RecentSearchAdapter.OnClickRecentProductListener */ {

    private var isMap: Boolean? = false
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var toolbar: Toolbar
    lateinit var addressPincode: String

    private lateinit var topMostPopularViewModel: TopMostPopularViewModel
    private lateinit var topMostPopularAdapter: TopMostPopularAdapter
    private lateinit var sharedPreferences: PreferenceManager

    private lateinit var viewModelCategory: HomeCategoryViewModel
    private lateinit var homeCategoryAdapter: HomeCategoryAdapter

    private lateinit var cartListViewModel: CartListViewModel
    private lateinit var cartListAdapter: CartListAdapter

    private lateinit var addressViewmodel: AddressListViewModel
    private lateinit var addressListAdapter: AddressListAdaptor

    private lateinit var getAllPinCodeViewmodel: GetAllPinCodeViewModel
    private lateinit var getAllPinCodeAdapter: GetAllPinCodeAdapter

//    private lateinit var recentSearchAdapter: RecentSearchAdapter
//    private lateinit var popularSearchAdapter: PopularSearchAdapter
//    private lateinit var searchListViewModel: SearchListViewModel

    private var mList = ArrayList<GetAllPinCodeList>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        toolbar = binding.toolbar

        sharedPreferences = PreferenceManager(requireContext())
        NetworkUtils.initialize(requireContext())
//        binding.Userprofile.setImageResource(sharedPreferences.getProfilePic())
//        val userEmail = sharedPreferences.getEmail().toString().trim()
        fetchCurrentLocation()

        getAllPinCodeViewmodel = ViewModelProvider(
            this, ViewModelFactory(RetrofitClient.apiService)
        )[GetAllPinCodeViewModel::class.java]

//        searchListViewModel = ViewModelProvider(
//            this, ViewModelFactory(RetrofitClient.apiService)
//        )[SearchListViewModel::class.java]
//
//        searchListViewModel.getRecentSearchListVM(userEmail)
//        getSearchListInitObserver()
//
//        searchListViewModel.getPopularSearchListVM(userEmail)
//        getPopularSearchListInitObserver()


        val profilePicName = sharedPreferences.getProfilePic()

        if (profilePicName != null) {

            val drawableMap = mapOf(
                "boy1" to R.drawable.boy1,
                "boy2" to R.drawable.boy2,
                "boy3" to R.drawable.boy3,
                "boy4" to R.drawable.boy4,
                "girl1" to R.drawable.girl1,
                "girl2" to R.drawable.girl2,
                "girl3" to R.drawable.girl3,
                "girl4" to R.drawable.girl4,
                "girl5" to R.drawable.girl5,
            )

            val drawableName = profilePicName

            //     val imageView = binding.Userprofile
            //  imageView.setImageResource(drawableMap[drawableName] ?: 0)

        } else {
            //  binding.Userprofile.setImageResource(R.drawable.boy1)
        }

        binding.UserNameTV.text = "Hi, ${sharedPreferences.getName()}!"
//        binding.categories.text = "Hi, ${sharedPreferences.getAdminToken()}!"

        binding.ivCart.setOnClickListener {
            findNavController().navigate(R.id.action_HomeFragment_to_bottom_menu_my_cart)
        }

        binding.tvSeeCategory.setOnClickListener {
            findNavController().navigate(R.id.action_HomeFragment_to_AllCategoryFragment)
        }

        binding.seeAllMostPopular.setOnClickListener {
            findNavController().navigate(R.id.action_HomeFragment_to_AllMostPopularFragment)
        }

        binding.searchClick.setOnClickListener {
            findNavController().navigate(R.id.HomeFragment_to_SearchProductsFragment)
//            binding.showingSearchBar.visibility = View.VISIBLE
        }

        binding.selectDelivery.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_AddressListFragment)
        }

        return binding.root
    }

    private lateinit var dialog: BottomSheetDialog
    private lateinit var recyclerView: RecyclerView
    private lateinit var googleLinearLayout: RelativeLayout
    private lateinit var linearLayout: LinearLayout

    private fun pincodeBottomSheet() {
        var dialogView = layoutInflater.inflate(R.layout.pincode_bottom_sheet, null)
        if (sharedPreferences.getMode() == true){
            dialogView = layoutInflater.inflate(R.layout.pincode_bottom_sheet_dark, null)
        }

        dialog = BottomSheetDialog(requireActivity(), R.style.AppBottomSheetDialogTheme)
        dialog.setContentView(dialogView)

        getAllPinCodeViewmodel.getAllPincodeVM(sharedPreferences.getEmail()!!)

        recyclerView = dialogView.findViewById(R.id.pincodeRV)
        linearLayout = dialogView.findViewById(R.id.search_empty)
        googleLinearLayout = dialogView.findViewById(R.id.ll_goggle_map)
        val dialogSearch = dialogView.findViewById<SearchView>(R.id.searchProduct)

        getAllPinCodeAdapter = GetAllPinCodeAdapter(emptyList(), requireContext())
        // Initialize with empty list
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = getAllPinCodeAdapter
        }

        getAllPinCodeViewmodel.getAllPincodeList.observe(viewLifecycleOwner) { response ->
            mList.clear()
//            getAllPinCodeAdapter.updateStudentList(response)
            getAllPinCodeAdapter.setOnClickPincodeListener(this)
            mList.addAll(response)

        }

        dialogSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    filterList(query.trim())
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
//                newText?.let {
//                    productListViewModel.searchProducts(it)
//                }
                if (newText != null) {
                    filterList(newText.trim())
                }
                return true
            }
        })

        googleLinearLayout.setOnClickListener {
            val bundle = Bundle()
            bundle.putBoolean("isHomeFragment", true)
            findNavController().navigate(R.id.action_HomeFragment_to_maps_fragment, bundle)
            dialog.dismiss()
        }
        dialog.show()

    }

    private fun filterList(query: String?) {
        if (query != null) {
            val filteredList = ArrayList<GetAllPinCodeList>()
            val lowerCaseQuery = query.lowercase(Locale.ROOT) // Convert query to lowercase

            for (i in mList) {
                val productNameLowerCase = i.pin_code?.lowercase(Locale.ROOT)
                recyclerView.visibility = View.VISIBLE
                linearLayout.visibility = View.GONE

                if (productNameLowerCase != null) {
                    if (productNameLowerCase.contains(lowerCaseQuery)) {
                        filteredList.add(i)
                    }
                }
            }

            if (filteredList.isEmpty()) {
                recyclerView.visibility = View.GONE
                linearLayout.visibility = View.VISIBLE
                //customToast(requireContext(), "No Data found", R.drawable.ic_info)
                //Toast.makeText(context, "No Data found", Toast.LENGTH_SHORT).show()
            } else {
                getAllPinCodeAdapter.updateStudentList(filteredList)
            }
        }
    }

//    private fun getPopularSearchListInitObserver() {
//        searchListViewModel.getPopularSearchListData.observe(viewLifecycleOwner) { mostPopularResp ->
//            Log.e("mostPopularResp", "onCreateView: $mostPopularResp")
//
//
//            popularSearchAdapter = PopularSearchAdapter(mostPopularResp!!)
//            popularSearchAdapter.setOnPopularClickProductListener(this)
//            binding.popularSearchRecycleView.layoutManager =
//                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
//            binding.popularSearchRecycleView.adapter = popularSearchAdapter
//
//
//        }
//
//
//        searchListViewModel.getPopularSearchList.observe(viewLifecycleOwner) { paper ->
//
//            if (paper?.token == null) {
////                Toast.makeText(context, "access", Toast.LENGTH_SHORT).show()
////                binding.allDetail.text = "All Test | Total - 0   Solved - 0  Unsolved - 0"
//            } else {
////                binding.allDetail.text = "All Test | Total - ${paper?.Total}   Solved - ${paper?.total_solved}  Unsolved - ${paper?.total_unsolved}"
//            }
//
//        }
//    }


//    private fun getSearchListInitObserver() {
//
//        searchListViewModel.getRecentSearchListData.observe(viewLifecycleOwner) { mostPopularResp ->
//            Log.e("mostPopularResp", "onCreateView: $mostPopularResp")
//
//
//            recentSearchAdapter = RecentSearchAdapter(mostPopularResp!!)
//            recentSearchAdapter.setOnRecentClickProductListener(this)
//            binding.recentSearchRecycleView.layoutManager =
//                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
//            binding.recentSearchRecycleView.adapter = recentSearchAdapter
//
//
//        }
//
//
//        searchListViewModel.getRecentSearchList.observe(viewLifecycleOwner) { paper ->
//
//            if (paper?.token == null) {
////                Toast.makeText(context, "access", Toast.LENGTH_SHORT).show()
////                binding.allDetail.text = "All Test | Total - 0   Solved - 0  Unsolved - 0"
//            } else {
////                binding.allDetail.text = "All Test | Total - ${paper?.Total}   Solved - ${paper?.total_solved}  Unsolved - ${paper?.total_unsolved}"
//            }
//
//        }
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val blinkCount = sharedPreferences.getNotificationBlinkCount()
        val previousNotificationCount = sharedPreferences.getNotificationPreviousCount().toString()

        val email = sharedPreferences.getEmail().toString().trim()

        binding.llLoadingScreen.visibility = View.VISIBLE
        binding.llMainScreen.visibility = View.GONE

        topMostPopularViewModel = ViewModelProvider(
            this, ViewModelFactory(RetrofitClient.apiService)
        )[TopMostPopularViewModel::class.java]


//        binding.viewRecentBackground.setOnClickListener {
//
//            binding.showingSearchBar.visibility = View.GONE
//        }
//
//        binding.backButtonsearch.setOnClickListener {
//            binding.showingSearchBar.visibility = View.GONE
//
//        }

        cartListViewModel = ViewModelProvider(
            this, ViewModelFactory(RetrofitClient.apiService)
        )[CartListViewModel::class.java]

        cartListViewModel.getCartListVM(email)
        cartListInitObserver()

        addressViewmodel = ViewModelProvider(
            this, ViewModelFactory(RetrofitClient.apiService)
        )[AddressListViewModel::class.java]

        addressViewmodel.getAddressResponse(sharedPreferences.getEmail()!!)
        addressListInitObserver()

        viewModelCategory = ViewModelProvider(
            this, ViewModelFactory(RetrofitClient.apiService)
        )[HomeCategoryViewModel::class.java]

        topMostPopularViewModel.getTopMostPopularVM(email)

        topMostPopularViewModel.getofferbanners(email)
        initObserver()

        viewModelCategory.getHomeCategoryVM(email)
        initObserverCatogery()

        val pin = arguments?.getString("pin")
        val city = arguments?.getString("city")

        if (pin.isNullOrEmpty() && city.isNullOrEmpty()) {
            binding.addressHome.text = "set delivery address"
        } else {
            addressPincode = "$city, $pin"
            sharedPreferences.setAddress(addressPincode)
            binding.addressHome.text = addressPincode
        }

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task: Task<String?> ->
            if (!task.isSuccessful) {
                Log.e("TokenDetails", "Token Failed to received")
                return@addOnCompleteListener
            }
            val token = task.result
            Log.d("TokenDetails", token!!)
//            binding.searchProduct.setText(token)
        }

        if (sharedPreferences.getMode() == true) {

            topSliderImplementationdark()
            binding.margin.setBackgroundColor(resources.getColor(R.color.blackfordark))
            binding.toolbar.setBackgroundColor(resources.getColor(R.color.blackfordark))
            binding.llMainScreen.setBackgroundColor(resources.getColor(R.color.blackfordark))
            binding.carticon.setImageDrawable(resources.getDrawable(R.drawable.add_to_cart_dark))
            binding.searchClick.setBackgroundResource(R.drawable.profile_round_corner_bg_dark_wish)
            binding.searchProduct.setHintTextColor(Color.GRAY)
            binding.UserNameTV.setTextColor(resources.getColor(R.color.whitefordark))
            binding.addressHome.setTextColor(Color.GRAY)
            binding.categories.setTextColor(resources.getColor(R.color.whitefordark))
            binding.mostpopular.setTextColor(resources.getColor(R.color.whitefordark))
            binding.todaysoffer.setTextColor(resources.getColor(R.color.whitefordark))

        } else {
            topSliderImplementation()
            binding.margin.setBackgroundColor(resources.getColor(R.color.whitefordark))
            binding.toolbar.setBackgroundColor(resources.getColor(R.color.whitefordark))
            binding.llMainScreen.setBackgroundColor(resources.getColor(R.color.whitefordark))
            binding.carticon.setImageDrawable(resources.getDrawable(R.drawable.add_to_cart))
            binding.searchClick.setBackgroundResource(R.drawable.round_corner_bg)
            binding.searchProduct.setHintTextColor(Color.BLACK)
            binding.UserNameTV.setTextColor(resources.getColor(R.color.blackfordark))
            binding.addressHome.setTextColor(resources.getColor(R.color.blackfordark))
            binding.categories.setTextColor(resources.getColor(R.color.blackfordark))
            binding.mostpopular.setTextColor(resources.getColor(R.color.blackfordark))
            binding.todaysoffer.setTextColor(resources.getColor(R.color.blackfordark))
        }

        // Initialize onBackPressedCallback
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finishAffinity(requireActivity())
            }
        }

        // Add the onBackPressedCallback to the activity's onBackPressedDispatcher
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner, onBackPressedCallback
        )

    }

    private fun addressListInitObserver() {
        var defaultStatus = false
        var defaultCity: String
        var defaultPin: String
        addressViewmodel.getAddressListResponse.observe(viewLifecycleOwner) { AddressListResp ->
            Log.e("GetAddressListResp", "onCreateView: $AddressListResp")
            if (!AddressListResp.isNullOrEmpty()) {
                for (countLoop in AddressListResp!!) {
                    defaultStatus = countLoop.is_default!!
                    if (defaultStatus) {
                        defaultCity = countLoop?.city.toString()
                        defaultPin = countLoop?.pin.toString()

                        sharedPreferences.setAddress("$defaultCity, $defaultPin")
                        binding.addressHome.text = "$defaultCity, $defaultPin"
                        sharedPreferences.setFirstTimeOpen(false)

                    } else {
                        binding.addressHome.text = "set delivery address"
                        sharedPreferences.setFirstTimeOpen(false)
                    }
                }
            } else if (sharedPreferences.isFirstTimeOpen()) {
                sharedPreferences.setFirstTimeOpen(false)

                binding.addressHome.text = "set delivery address"
                pincodeBottomSheet()

            } else if (!sharedPreferences.getAddress().isNullOrEmpty()) {
                binding.addressHome.text = sharedPreferences.getAddress().toString()
            }

        }
    }

    var itemCount: Int = 0

    private fun cartListInitObserver() {

        cartListViewModel.getCartListData.observe(viewLifecycleOwner) { mostPopularResp ->
            Log.e("mostPopularResp", "onCreateView: $mostPopularResp")

            cartListAdapter = context?.let { CartListAdapter(mostPopularResp, it) }!!

            itemCount = cartListAdapter.itemCount

            if (itemCount > 0) {
                binding.cartIconCount.visibility = View.VISIBLE
                binding.cartIconCount.text = itemCount.toString()

            } else {
                binding.cartIconCount.visibility = View.GONE
            }
            if (itemCount > 99) {
                binding.cartIconCount.visibility = View.VISIBLE
                binding.cartIconCount.text = itemCount.toString()
                binding.cartIconCount.text = "99+"

            }
        }

        cartListViewModel.getCartList.observe(viewLifecycleOwner) { paper ->

            if (paper?.token == null) {
//                Toast.makeText(context, "access", Toast.LENGTH_SHORT).show()
//                binding.allDetail.text = "All Test | Total - 0   Solved - 0  Unsolved - 0"
            } else {
//                binding.allDetail.text = "All Test | Total - ${paper?.Total}   Solved - ${paper?.total_solved}  Unsolved - ${paper?.total_unsolved}"
            }
        }
    }


    override fun onResume() {
        super.onResume()
        _binding?.UserNameTV!!.text = "Hi, ${sharedPreferences.getName()}!"
        val email = sharedPreferences.getEmail().toString().trim()

        cartListViewModel.getCartListVM(email)
        cartListInitObserver()

        topMostPopularViewModel.getTopMostPopularVM(email)
        initObserver()

        viewModelCategory.getHomeCategoryVM(email)
        initObserverCatogery()

    }

    private fun initObserverCatogery() {
        viewModelCategory.getHomeCategoryItem.observe(viewLifecycleOwner) { homeCategoryResp ->
            Log.e("homeCategoryResp", "onCreateView: $homeCategoryResp")

            var list= homeCategoryResp?.filter { it.is_active }
//            val sortedNotificationList = notificationResp.sortedByDescending { it.create_date }
            homeCategoryAdapter = HomeCategoryAdapter(list!!, requireContext())

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

    override fun onProductIdClick(pId: String) {
        val bundle = Bundle()
        bundle.putString("pid", pId)
        findNavController().navigate(R.id.action_HomeFragment_to_Product, bundle)
    }

    override fun onWishaddclick(pId: String) {
        val curretuseremail = sharedPreferences.getEmail()
        topMostPopularViewModel.addtowishlist(curretuseremail.toString(), pId)
    }

    override fun onWishdeleteclick(pId: String) {
        val curretuseremail = sharedPreferences.getEmail()
        topMostPopularViewModel.deletefromwishlist(curretuseremail.toString(), pId)
    }

    private fun initObserver() {

        topMostPopularViewModel.getTopMostPopularItem.observe(viewLifecycleOwner) { mostPopularResp ->
            binding.llLoadingScreen.visibility = View.GONE
            binding.llMainScreen.visibility = View.VISIBLE
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

    private fun topSliderImplementation() {
        topMostPopularViewModel.banners.observe(viewLifecycleOwner) {
            val imageList = ArrayList<SlideModel>()

            it?.forEach() {
                if (it?.img_type == "Light") imageList.add(
                    SlideModel(
                        it.img_url, ScaleTypes.CENTER_CROP
                    )
                )
            }

            val imageSlider = binding.imageSlider
            imageSlider.setImageList(imageList)
            val imageSlider2 = binding.imageSlider2
            imageSlider2.setImageList(imageList)

            imageSlider.setItemClickListener(object : ItemClickListener {
                override fun doubleClick(position: Int) {
                }

                override fun onItemSelected(position: Int) {
                    it?.get(position)!!.Category_id

                    val bundle = Bundle()
                    bundle.putString("categoryId", it.get(position)!!.Category_id.toString())
                    findNavController().navigate(
                        R.id.action_AllCategoryFragment_to_ProductListFragment,
                        bundle
                    )
                }
            })

            imageSlider2.setItemClickListener(object : ItemClickListener {
                override fun doubleClick(position: Int) {
                }

                override fun onItemSelected(position: Int) {
                    val bundle = Bundle()
                    bundle.putString("categoryId", it?.get(position)!!.Category_id.toString())
                    findNavController().navigate(
                        R.id.action_AllCategoryFragment_to_ProductListFragment,
                        bundle
                    )
                }
            })
        }
    }

    private fun topSliderImplementationdark() {
        topMostPopularViewModel.banners.observe(viewLifecycleOwner) {
            val imageList = ArrayList<SlideModel>()

            it?.forEach() {
                if (it?.img_type == "Dark") imageList.add(
                    SlideModel(
                        it.img_url, ScaleTypes.CENTER_CROP
                    )
                )
            }

            val imageSlider = binding.imageSlider
            imageSlider.setImageList(imageList)
            val imageSlider2 = binding.imageSlider2
            imageSlider2.setImageList(imageList)

            imageSlider.setItemClickListener(object : ItemClickListener {
                override fun doubleClick(position: Int) {
                }

                override fun onItemSelected(position: Int) {
                    it?.get(position)!!.Category_id

                    val bundle = Bundle()
                    bundle.putString("categoryId", it?.get(position)!!.Category_id.toString())
                    findNavController().navigate(
                        R.id.action_AllCategoryFragment_to_ProductListFragment,
                        bundle
                    )
                }
            })

            imageSlider2.setItemClickListener(object : ItemClickListener {
                override fun doubleClick(position: Int) {
                }

                override fun onItemSelected(position: Int) {
                    val bundle = Bundle()
                    bundle.putString("categoryId", it?.get(position)!!.Category_id.toString())
                    findNavController().navigate(
                        R.id.action_AllCategoryFragment_to_ProductListFragment,
                        bundle
                    )
                }
            })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCategoryIdClick(categoryId: String, categoryName : String) {
        val bundle = Bundle()
        bundle.putString("categoryId", categoryId)
        bundle.putString("categoryName", categoryName)
        findNavController().navigate(R.id.action_AllCategoryFragment_to_ProductListFragment, bundle)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
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
                requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 1000
            )
            return
        }
        //  Toast.makeText(context, "permission granted", Toast.LENGTH_SHORT).show()
    }

    override fun onPinCodeIdClick(pinId: String, pin_code: String, city: String, state: String) {

        addressPincode = "$city, $pin_code"
        sharedPreferences.setAddress(addressPincode)
        binding.addressHome.text = addressPincode
        dialog.dismiss()

    }

//    override fun onPopularProductIdClick(pName: String) {
//
////        binding.searchProduct2.setQuery(pName.trim(), true)
////        val bundle = Bundle()
////        bundle.putString("pName", pName)
//        sharedPreferences.setSearch(pName)
//
//        findNavController().navigate(R.id.HomeFragment_to_SearchProductsFragment)
//    }
//
//    override fun onRecentProductIdClick(pName: String) {
////        binding.searchProduct2.setQuery(pName.trim(), true)
////        val bundle = Bundle()
////        bundle.putString("pName", pName)
//
//        sharedPreferences.setSearch(pName)
//        findNavController().navigate(R.id.HomeFragment_to_SearchProductsFragment)
//    }

}