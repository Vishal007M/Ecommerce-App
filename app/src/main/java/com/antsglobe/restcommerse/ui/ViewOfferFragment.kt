package com.antsglobe.restcommerse.ui

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.antsglobe.restcommerse.R
import com.antsglobe.restcommerse.Utils.PreferenceManager
import com.antsglobe.restcommerse.adapter.CouponInvalidAdaptor
import com.antsglobe.restcommerse.adapter.CouponListAdaptor
import com.antsglobe.restcommerse.adapter.CouponRedeemAdaptor
import com.antsglobe.restcommerse.adapter.OfferListAdaptor
import com.antsglobe.restcommerse.adapter.PopularSearchAdapter
import com.antsglobe.restcommerse.databinding.FragmentViewOfferBinding
import com.antsglobe.restcommerse.model.OfferItemList
import com.antsglobe.restcommerse.model.Response.AddressList
import com.antsglobe.restcommerse.model.Response.Coupon
import com.antsglobe.restcommerse.network.RetrofitClient
import com.antsglobe.restcommerse.viewmodel.CouponViewModel
import com.antsglobe.restcommerse.viewmodel.ViewModelFactory
import com.antsglobe.restcommerse.viewmodel.AddressListViewModel
import com.antsglobe.restcommerse.viewmodel.AllProductListViewModel
import com.antsglobe.restcommerse.viewmodel.TopMostPopularViewModel
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class ViewOfferFragment : Fragment(), OfferListAdaptor.OnClickProductListener {
    private lateinit var currentuseremail: String
    private var binding: FragmentViewOfferBinding? = null
    private var list = ArrayList<OfferItemList>()
    private lateinit var offerListAdaptor: OfferListAdaptor
    private lateinit var couponListAdaptor: CouponListAdaptor
    private lateinit var couponRedeemAdaptor: CouponRedeemAdaptor
    private lateinit var couponInvalidAdaptor: CouponInvalidAdaptor
    private lateinit var viewModel: CouponViewModel

    private var mList = ArrayList<Coupon>()
    private var mList2 = ArrayList<Coupon>()

    private lateinit var couponlist:List<Coupon>

    private lateinit var topMostPopularViewModel: TopMostPopularViewModel
    private lateinit var sharedPreferences: PreferenceManager

    private lateinit var productListViewModel: AllProductListViewModel
//    private lateinit var productAdapter: AllProductListAdaptor

    private var expired=0
    private val currentdate=getCurrentDateTime()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,

        savedInstanceState: Bundle?
    ): View? {
        //return inflater.inflate(R.layout.fragment_view_offer, container, false)
        binding = FragmentViewOfferBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(
            this, ViewModelFactory(RetrofitClient.apiService)
        )[CouponViewModel::class.java]
        sharedPreferences = PreferenceManager(requireContext())
        currentuseremail = sharedPreferences.getEmail()!!
        binding?.backButton?.setOnClickListener {
            findNavController().popBackStack()
        }

        topMostPopularViewModel = ViewModelProvider(
            this, ViewModelFactory(RetrofitClient.apiService)
        )[TopMostPopularViewModel::class.java]

        topMostPopularViewModel.getofferbanners(currentuseremail)
        SlideimageinitObserver()

// Initialize ViewModel
        productListViewModel = ViewModelProvider(
            this, ViewModelFactory(RetrofitClient.apiService)
        )[AllProductListViewModel::class.java]
        productListViewModel.allProductListVM(currentuseremail)
        getproductListInitObserver()


        if (sharedPreferences.getMode() == true){
            binding!!.fullscreen.setBackgroundColor(Color.BLACK)
            binding!!.tvOffer.setBackgroundColor(Color.parseColor("#1F201D"))
            binding!!.tvCoupon.setBackgroundColor(resources.getColor(R.color.dark_grey))
            binding!!.backButton.setImageResource(R.drawable.ic_baseline_arrow_back_24_dark)
            binding!!.logoImageHome.setTextColor(Color.BLACK)
            binding!!.todaysoffer.setTextColor(Color.WHITE)
            binding!!.todaysoffer.setTextColor(Color.WHITE)
            binding!!.ucoffers.setTextColor(Color.WHITE)
            binding!!.emptyicon.setImageResource(R.drawable.emptycoupon_dark)
            binding!!.llValid.setBackgroundResource(R.drawable.round_corner_border2_coupon_dark)
            binding!!.tvRedeem.setTextColor(resources.getColor(R.color.white))
            binding!!.tvInvalid.setTextColor(resources.getColor(R.color.white))
            binding!!.tvRedeem.setBackgroundResource(R.color.blackfordark)
            binding!!.tvInvalid.setBackgroundResource(R.color.blackfordark)
            binding!!.tvValid.setTextColor(resources.getColor(R.color.blackfordark))

        }


        if (binding!!.rbOffer.isChecked){
            binding!!.llOffer.visibility = View.VISIBLE
            setOfferData()
        }

        binding!!.rbOffer.setOnClickListener(View.OnClickListener {
            binding!!.rbOffer.setChecked(true)
            binding!!.rbCoupon.setChecked(false)
            // Set background color from resource
            binding!!.tvCoupon.setBackgroundColor(resources.getColor(R.color.light_grey))
            binding!!.tvOffer.setBackgroundColor(resources.getColor(R.color.dark_grey))
            if (sharedPreferences.getMode() == true){
                binding!!.tvCoupon.setBackgroundColor(resources.getColor(R.color.dark_grey))
                binding!!.tvOffer.setBackgroundColor(Color.parseColor("#1F201D"))
            }
            binding!!.llEmptyScreen.visibility=View.GONE
            binding!!.llOffer.visibility = View.VISIBLE
            binding!!.llCoupon.visibility = View.GONE
            setOfferData()
        })
        binding!!.rbCoupon.setOnClickListener(View.OnClickListener {
            binding!!.rbCoupon.setChecked(true)
            binding!!.rbOffer.setChecked(false)
            // Set background color from resource
            binding!!.tvCoupon.setBackgroundColor(resources.getColor(R.color.dark_grey))
            binding!!.tvOffer.setBackgroundColor(resources.getColor(R.color.light_grey))
            binding!!.llEmptyScreen.visibility = View.GONE
            if (sharedPreferences.getMode() == true){
                binding!!.tvCoupon.setBackgroundColor(Color.parseColor("#1F201D"))
                binding!!.tvOffer.setBackgroundColor(resources.getColor(R.color.dark_grey))
            }
            binding!!.llCoupon.visibility = View.VISIBLE
            binding!!.llOffer.visibility = View.GONE
            setCouponData()
        })

        return binding?.root
    }


    private fun getproductListInitObserver() {

    }

    private fun SlideimageinitObserver() {
        topMostPopularViewModel.banners.observe(viewLifecycleOwner) {
            val imageList = ArrayList<SlideModel>()


            it?.forEach() {
                if (sharedPreferences.getMode() == true){
                    if (it?.img_type == "Dark") imageList.add(
                        SlideModel(
                            it.img_url, ScaleTypes.CENTER_CROP
                        )
                    )
                }
                else{
                    if (it?.img_type == "Light") imageList.add(
                        SlideModel(
                            it.img_url, ScaleTypes.CENTER_CROP
                        )
                    )
                }

            }

            val imageSlider2 = binding?.imageSlider2
            imageSlider2?.setImageList(imageList)

            imageSlider2?.setItemClickListener(object : ItemClickListener {
                override fun doubleClick(position: Int) {
                }

                override fun onItemSelected(position: Int) {
                    val bundle = Bundle()
                    bundle.putString("categoryId", it?.get(position)!!.Category_id.toString())
                    findNavController().navigate(R.id.action_ViewOfferFragment_to_ProductListFragment, bundle)
                }
            })

        }    }

    private fun setOfferData() {

        productListViewModel.allProductItems.observe(viewLifecycleOwner) { productList ->
            Log.e("TAG", "setOfferData: $productList", )
            offerListAdaptor = OfferListAdaptor(productList,requireContext())
            offerListAdaptor.setOnClickProductViewListener(this)
            binding!!.rvOfferList.layoutManager = GridLayoutManager(context, 2)
            binding!!.rvOfferList.adapter = offerListAdaptor


        }


    }

//    private fun topSliderImplementation() {
//        val imageList = ArrayList<SlideModel>()
//        imageList.add(
//            SlideModel(
//                R.drawable.offers1, ScaleTypes.FIT
//            )
//        )
//        imageList.add(
//            SlideModel(
//                R.drawable.offers1, ScaleTypes.FIT
//            )
//        )
//
//        val imageSlider = binding!!.imageSlider2
//        imageSlider.setImageList(imageList)
//    }

//    private fun offerList() {
//        list.clear()
//        list.add(OfferItemList("Chicken hamburger", "YOU SAVE ₹40"))
//        list.add(OfferItemList("Chicken hamburger", "YOU SAVE ₹40"))
//        list.add(OfferItemList("Chicken hamburger", "YOU SAVE ₹40"))
//        list.add(OfferItemList("Chicken hamburger", "YOU SAVE ₹40"))
//        list.add(OfferItemList("Chicken hamburger", "YOU SAVE ₹40"))
//        list.add(OfferItemList("Chicken hamburger", "YOU SAVE ₹40"))
//    }

    private fun setCouponData() {

        if (binding!!.rbValid.isChecked){
            viewModel.getcouponlist(currentuseremail, "valid")

            viewModel.couponlist.observe(viewLifecycleOwner) {
                viewModel.couponlist.observe(viewLifecycleOwner) { couponList ->
                    if (couponList != null) {
                        couponlist=couponList
                        couponList.forEach {
                            val result=compareDates(it.valid_to.toString(),currentdate)
                            if (result<0) {
                                expired+=1
                                Log.d("expired",expired.toString())
                            }
                        }
                    }
                    binding?.rvCouponList!!.apply {
                        visibility = View.VISIBLE
                        // binding.llLoadingScreen.visibility = View.GONE
                        if (couponList != null) {
                            if (couponList.isEmpty() && couponList.size >= 0||couponList.size==expired) {
                                visibility = View.GONE
                                binding?.llEmptyScreen!!.visibility = View.VISIBLE
                            } else {
                                visibility = View.VISIBLE
                                binding?.llEmptyScreen!!.visibility = View.GONE
                            }
                        }
                        layoutManager = LinearLayoutManager(requireContext())
                        couponListAdaptor = CouponListAdaptor(couponList!!)
                        adapter = couponListAdaptor

                    }
                    expired=0
                }
            }
//            couponListAdaptor = CouponListAdaptor(list!!)
//            binding!!.rvCouponList.layoutManager = LinearLayoutManager(context)
//            binding!!.rvCouponList.adapter = couponListAdaptor
        }

        binding!!.rbValid.setOnClickListener(View.OnClickListener {
            binding!!.rbValid.setChecked(true)
            binding!!.rbRedeem.setChecked(false)
            binding!!.rbInvalid.setChecked(false)
            // Set background color from resource
            binding!!.tvValid.setBackgroundResource(R.drawable.round_corner_border2_selected_coupon)
            binding!!.tvRedeem.setBackgroundResource(R.color.white)
            binding!!.tvInvalid.setBackgroundResource(R.color.white)
            binding!!.tvValid.setTextColor(resources.getColor(R.color.white))
            binding!!.tvRedeem.setTextColor(resources.getColor(R.color.hint))
            binding!!.tvInvalid.setTextColor(resources.getColor(R.color.hint))
            if (sharedPreferences.getMode() == true){
                binding!!.tvRedeem.setTextColor(resources.getColor(R.color.white))
                binding!!.tvInvalid.setTextColor(resources.getColor(R.color.white))
                binding!!.tvRedeem.setBackgroundResource(R.color.blackfordark)
                binding!!.tvInvalid.setBackgroundResource(R.color.blackfordark)
                binding!!.tvValid.setTextColor(resources.getColor(R.color.black))
            }

            /*Added the valid adaptor*/
            viewModel.getcouponlist(currentuseremail, "valid")

            viewModel.couponlist.observe(viewLifecycleOwner) {
                viewModel.couponlist.observe(viewLifecycleOwner) {
                    if (couponlist != null) {
                        couponlist.forEach {
                            val result=compareDates(it.valid_to.toString(),currentdate)
                            if (result<0) {
                                expired+=1
                                Log.d("expired",expired.toString())
                            }
                        }
                    }
                    binding?.rvCouponList!!.apply {
                        visibility = View.VISIBLE
                        // binding.llLoadingScreen.visibility = View.GONE
                        if (couponlist != null) {
                            if (couponlist.isEmpty() && couponlist.size >= 0||couponlist.size==expired) {

                                visibility = View.GONE
                                binding?.llEmptyScreen!!.visibility = View.VISIBLE
                            } else {
                                visibility = View.VISIBLE
                                binding?.llEmptyScreen!!.visibility = View.GONE
                            }
                        }
                        layoutManager = LinearLayoutManager(requireContext())
                        couponListAdaptor = CouponListAdaptor(couponlist!!)
                        adapter = couponListAdaptor

                    }
                    expired=0
                }
            }
        })

        binding!!.rbRedeem.setOnClickListener(View.OnClickListener  {
            binding!!.rbValid.setChecked(false)
            binding!!.rbRedeem.setChecked(true)
            binding!!.rbInvalid.setChecked(false)
            // Set background color from resource
            binding!!.tvRedeem.setBackgroundResource(R.drawable.round_corner_border2_selected_coupon)
            binding!!.tvValid.setBackgroundResource(R.color.white)
            binding!!.tvInvalid.setBackgroundResource(R.color.white)
            binding!!.tvRedeem.setTextColor(resources.getColor(R.color.white))
            binding!!.tvValid.setTextColor(resources.getColor(R.color.hint))
            binding!!.tvInvalid.setTextColor(resources.getColor(R.color.hint))
            if (sharedPreferences.getMode() == true){
                binding!!.tvValid.setTextColor(resources.getColor(R.color.white))
                binding!!.tvInvalid.setTextColor(resources.getColor(R.color.white))
                binding!!.tvValid.setBackgroundResource(R.color.blackfordark)
                binding!!.tvInvalid.setBackgroundResource(R.color.blackfordark)
                binding!!.tvRedeem.setTextColor(resources.getColor(R.color.blackfordark))
            }

            /*adaptor of the redeem */
            viewModel.getcouponlist(currentuseremail, "redeemed")

            viewModel.couponlist.observe(viewLifecycleOwner) {
                viewModel.couponlist.observe(viewLifecycleOwner) { couponList ->
                    binding?.rvCouponList!!.apply {
                        visibility = View.VISIBLE
                        // binding.llLoadingScreen.visibility = View.GONE
                        if (couponList != null) {
                            if (couponList.isEmpty() && couponList.size >= 0) {
                                visibility = View.GONE
                                binding?.llEmptyScreen!!.visibility = View.VISIBLE
                            } else {
                                visibility = View.VISIBLE
                                binding?.llEmptyScreen!!.visibility = View.GONE
                            }
                        }
                        layoutManager = LinearLayoutManager(requireContext())
                        couponRedeemAdaptor = CouponRedeemAdaptor(couponList!!)
                        adapter = couponRedeemAdaptor

                    }
                }
            }
//            couponRedeemAdaptor = CouponRedeemAdaptor(list!!)
//            binding!!.rvCouponList.layoutManager = LinearLayoutManager(context)
//            binding!!.rvCouponList.adapter = couponRedeemAdaptor
        })

        binding!!.rbInvalid.setOnClickListener(View.OnClickListener {
            binding!!.rbValid.setChecked(false)
            binding!!.rbRedeem.setChecked(false)
            binding!!.rbInvalid.setChecked(true)
            // Set background color from resource
            binding!!.tvInvalid.setBackgroundResource(R.drawable.round_corner_border2_selected_coupon)
            binding!!.tvRedeem.setBackgroundResource(R.color.white)
            binding!!.tvValid.setBackgroundResource(R.color.white)
            binding!!.tvInvalid.setTextColor(resources.getColor(R.color.white))
            binding!!.tvValid.setTextColor(resources.getColor(R.color.hint))
            binding!!.tvRedeem.setTextColor(resources.getColor(R.color.hint))
            if (sharedPreferences.getMode() == true){
                binding!!.tvValid.setTextColor(resources.getColor(R.color.white))
                binding!!.tvRedeem.setTextColor(resources.getColor(R.color.white))
                binding!!.tvValid.setBackgroundResource(R.color.blackfordark)
                binding!!.tvRedeem.setBackgroundResource(R.color.blackfordark)
                binding!!.tvInvalid.setTextColor(resources.getColor(R.color.blackfordark))
            }

            /*adaptor of the Invalid */
            viewModel.getcouponlist(currentuseremail, "expired")

            viewModel.couponlist.observe(viewLifecycleOwner) {
                viewModel.couponlist.observe(viewLifecycleOwner) { couponList ->
                    binding?.rvCouponList!!.apply {
                        visibility = View.VISIBLE
                        // binding.llLoadingScreen.visibility = View.GONE
                        if (couponList != null) {
                            if (couponList.isEmpty() && couponList.size >= 0) {
                                visibility = View.GONE
                                binding?.llEmptyScreen!!.visibility = View.VISIBLE
                            } else {
                                visibility = View.VISIBLE
                                binding?.llEmptyScreen!!.visibility = View.GONE
                            }
                        }
                        for (items in couponList!!){
                            filterList(items.valid_to)
                        }


                        mList.clear()
                        couponInvalidAdaptor = CouponInvalidAdaptor(couponList!!)
                        couponInvalidAdaptor.updateStudentList(couponList!!)
                        mList.addAll(couponList!!)
                        layoutManager = LinearLayoutManager(requireContext())
                        adapter = couponInvalidAdaptor

                    }
                }
            }

            //            couponInvalidAdaptor = CouponInvalidAdaptor(list!!)
//            binding!!.rvCouponList.layoutManager = LinearLayoutManager(context)
//            binding!!.rvCouponList.adapter = couponInvalidAdaptor
        })


    }
    fun compareDates(date1: String, date2: String): Int {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val dateTime1 = format.parse(date1)
        val dateTime2 = format.parse(date2)

        return dateTime1.compareTo(dateTime2)
    }

    fun getCurrentDateTime(): String {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        return format.format(Date())
    }

    private fun filterList(query: String?) {
        if (query != null) {
            val filteredList = ArrayList<Coupon>()
            val lowerCaseQuery = query.toString()

            for (i in mList) {
//                i.is_default? = null
                val currentdate=getCurrentDateTime()
                val result=compareDates(lowerCaseQuery,currentdate)
                if (result<0) {
                    Log.e("TAG", "filterList: $i", )
                    filteredList.add(i)

                }

                if (filteredList.isEmpty()) {
                    binding?.rvCouponList?.visibility = View.GONE
                    binding?.llEmptyScreen!!.visibility = View.VISIBLE
                } else {
                    couponInvalidAdaptor.updateStudentList(filteredList)
                }

            }
        }
    }

    override fun onProductIdClick(pId: String) {
        val bundle = Bundle()
        bundle.putString("pid", pId)
        findNavController().navigate(R.id.action_bottom_menu_my_cart_to_Product, bundle)
    }
}