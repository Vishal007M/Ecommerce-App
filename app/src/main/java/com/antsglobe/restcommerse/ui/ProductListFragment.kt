package com.antsglobe.restcommerse.ui

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.antsglobe.restcommerse.R
import com.antsglobe.restcommerse.Utils.PreferenceManager
import com.antsglobe.restcommerse.adapter.ProductListAdaptor
import com.antsglobe.restcommerse.databinding.FragmentProductListBinding
import com.antsglobe.restcommerse.model.Response.ProductList
import com.antsglobe.restcommerse.network.RetrofitClient
import com.antsglobe.restcommerse.viewmodel.ProductListViewModel
import com.antsglobe.restcommerse.viewmodel.ViewModelFactory
import com.antsglobe.restcommerse.viewmodel.WishlistViewmodel
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.lang.Integer.parseInt


class ProductListFragment : Fragment(), ProductListAdaptor.OnClickProductListListener {
    private lateinit var binding: FragmentProductListBinding
    private lateinit var viewmodel: ProductListViewModel
    private lateinit var sharedPreferences: PreferenceManager
    private lateinit var productAdapter: ProductListAdaptor
    private var categoryId: String? = null
    private var categoryName: String? = null

    private lateinit var wishViewmodel: WishlistViewmodel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            categoryId = it.getString("categoryId")
            categoryName = it.getString("categoryName")
            // param2 = it.getString(ARG_PARAM2)
        }
    }

    private var mList = ArrayList<ProductList?>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProductListBinding.inflate(inflater, container, false)

        sharedPreferences = PreferenceManager(requireContext())

        val prefs = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putInt(PREF_SELECTED_RADIO_BUTTON, R.id.relevant)
        editor.apply()

        viewmodel = ViewModelProvider(
            this, ViewModelFactory(RetrofitClient.apiService)
        )[ProductListViewModel::class.java]

        viewmodel.productListResponse(
            sharedPreferences.getEmail().toString(),
            categoryId.toString()
        )

        viewmodel.getcartlist(sharedPreferences.getEmail().toString())
        binding.llLoadingScreen.visibility = View.VISIBLE
        binding.clProductList.visibility = View.GONE

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        productAdapter = ProductListAdaptor(emptyList(), emptyList(), requireContext())
        // Initialize with empty list
        binding.rvProductList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = productAdapter
        }

        wishViewmodel = ViewModelProvider(
            this, ViewModelFactory(RetrofitClient.apiService)
        )[WishlistViewmodel::class.java]
        wishViewmodel.getcartlist(sharedPreferences.getEmail().toString())

        if (categoryName.isNullOrEmpty()){
            binding!!.logoImageHome.text = "All Product"
        }else{
            binding!!.logoImageHome.text = categoryName

        }

        if (sharedPreferences.getMode() == true) {
            binding.fullscreen.setBackgroundColor(resources.getColor(R.color.blackfordark))
            binding.backButton.setImageResource(R.drawable.ic_baseline_arrow_back_24_dark)
            binding.logoImageHome.setTextColor(resources.getColor(R.color.blackfordark))
            binding.emptytext.setTextColor(resources.getColor(R.color.whitefordark))
            binding.sorticon.imageTintList= ColorStateList.valueOf(Color.WHITE)
            binding.filtericon.imageTintList= ColorStateList.valueOf(Color.WHITE)
            binding.filterByData.setTextColor(Color.WHITE)
            binding.shortByData.setTextColor(Color.WHITE)
            binding.cartcount.setBackgroundColor(resources.getColor(R.color.blackfordark))
            binding.carticonlayout.setBackgroundColor(resources.getColor(R.color.blackfordark))
            binding.cart.setImageDrawable(resources.getDrawable(R.drawable.add_to_cart_dark))


        }else{
            binding.cartcount.setBackgroundColor(resources.getColor(R.color.whitefordark))
            binding.carticonlayout.setBackgroundColor(resources.getColor(R.color.whitefordark))

            binding.cart.setImageDrawable(resources.getDrawable(R.drawable.add_to_cart))

        }

        binding.countcard.setOnClickListener {
            findNavController().navigate(R.id.action_HomeFragment_to_bottom_menu_my_cart)
        }

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


        viewmodel.cartListResponse.observe(viewLifecycleOwner) {
            viewmodel.getProductListResponse.observe(viewLifecycleOwner) { ProductListResp ->
                Log.e("ProductListResp", "onCreateView: $ProductListResp")
                binding.clProductList.visibility = View.VISIBLE
                binding.llLoadingScreen.visibility = View.GONE

                if (ProductListResp!!.isEmpty() && ProductListResp!!.size >= 0) {
                    binding.clProductList.visibility = View.GONE
                    binding.llEmptyScreen.visibility = View.VISIBLE
                } else {
                    binding.clProductList.visibility = View.VISIBLE
                    binding.llEmptyScreen.visibility = View.GONE
                }

                productAdapter.setOnClickCategoryListener(this)
                mList.clear()
                productAdapter.updateStudentList(ProductListResp)
                productAdapter.updateCartList(it)
                mList.addAll(ProductListResp)

            }
        }
        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.sortProduct.setOnClickListener {

            shortingBottomSheet()

        }

        binding.filterProduct.setOnClickListener {

            filterBottomSheet()

        }


    }

    private fun filterBottomSheet() {

        var dialogView = layoutInflater.inflate(R.layout.filter_product_bottom_sheet, null)
        if (sharedPreferences.getMode() == true){
            dialogView = layoutInflater.inflate(R.layout.filter_product_bottom_sheet_dark, null)
        }

        shortBydialog = BottomSheetDialog(requireActivity(), R.style.AppBottomSheetDialogTheme)
        shortBydialog.setContentView(dialogView)

        val genderRadioGroup = dialogView.findViewById<RadioGroup>(R.id.genderRB)
        val prefs = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        val selectedRadioButtonId = prefs.getInt(PREF_SELECTED_RADIO_BUTTON, -1)
        if (selectedRadioButtonId != -1) {
            genderRadioGroup.check(selectedRadioButtonId)
        }

        genderRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            val radioButton = dialogView.findViewById<RadioButton>(checkedId)
            val editor = prefs.edit()
            editor.putInt(PREF_SELECTED_RADIO_BUTTON, checkedId)
            editor.apply()

            when (radioButton.id) {
                R.id.inStock -> {
                    filterStockList("In Stock")
                    binding.filterByData.text = "Filter by In Stock"
                    binding.shortByData.text = "Sort by"
                    shortBydialog.dismiss()
                }

                R.id.vegOnly -> {
                    filterVegOnlyList("Veg")
                    binding.filterByData.text = "Filter by Veg Only"
                    binding.shortByData.text = "Sort by"
                    shortBydialog.dismiss()
                }

                R.id.nonVegOnly -> {
                    filterNonVegOnlyList("Non-Veg")
                    binding.filterByData.text = "Filter by Non Veg Only"
                    binding.shortByData.text = "Sort by"
                    shortBydialog.dismiss()
                }
            }
        }

        shortBydialog.show()
    }

    private fun filterStockList(query: String?) {
        val filteredList = mList.filter { it?.prod_availability == query }
        if (filteredList.isEmpty()) {
            productAdapter.updateStudentList(filteredList)
            binding.filterEmptyScreen.visibility = View.VISIBLE
            binding.rvProductList.visibility = View.GONE
        } else {
            binding.filterEmptyScreen.visibility = View.GONE
            binding.rvProductList.visibility = View.VISIBLE
            productAdapter.updateStudentList(filteredList)
        }
    }

    private fun filterVegOnlyList(query: String?) {
        val filteredList = mList.filter { it?.prod_type == query }
        if (filteredList.isEmpty()) {
            productAdapter.updateStudentList(filteredList)
            binding.filterEmptyScreen.visibility = View.VISIBLE
            binding.rvProductList.visibility = View.GONE
        } else {
            binding.filterEmptyScreen.visibility = View.GONE
            binding.rvProductList.visibility = View.VISIBLE
            productAdapter.updateStudentList(filteredList)
        }
    }
    private fun filterNonVegOnlyList(query: String?) {
        val filteredList = mList.filter { it?.prod_type == query }
        if (filteredList.isEmpty()) {
            productAdapter.updateStudentList(filteredList)
            binding.filterEmptyScreen.visibility = View.VISIBLE
            binding.rvProductList.visibility = View.GONE
        } else {
            binding.filterEmptyScreen.visibility = View.GONE
            binding.rvProductList.visibility = View.VISIBLE
            productAdapter.updateStudentList(filteredList)
        }
    }

    private lateinit var shortBydialog: BottomSheetDialog
    private val PREFS_NAME = "MyPrefs"
    private val PREF_SELECTED_RADIO_BUTTON = "SelectedRadioButton"


    private fun shortingBottomSheet() {

        var dialogView = layoutInflater.inflate(R.layout.short_product_bottom_sheet, null)
        if (sharedPreferences.getMode() == true){
            dialogView = layoutInflater.inflate(R.layout.short_product_bottom_sheet_dark, null)
        }
        shortBydialog = BottomSheetDialog(requireActivity(), R.style.AppBottomSheetDialogTheme)
        shortBydialog.setContentView(dialogView)

        val genderRadioGroup = dialogView.findViewById<RadioGroup>(R.id.genderRB)
        val prefs = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        val selectedRadioButtonId = prefs.getInt(PREF_SELECTED_RADIO_BUTTON, -1)
        if (selectedRadioButtonId != -1) {
            genderRadioGroup.check(selectedRadioButtonId)
        }

        genderRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            val radioButton = dialogView.findViewById<RadioButton>(checkedId)
            val editor = prefs.edit()
            editor.putInt(PREF_SELECTED_RADIO_BUTTON, checkedId)
            editor.apply()

            when (radioButton.id) {
                R.id.relevant -> {
                    productAdapter.updateStudentList(mList)
                    binding.shortByData.text = "Sort by"
                    binding.filterByData.text = "Filter"
                    shortBydialog.dismiss()
                }

                R.id.priceHToL -> {
                    sortProductList(false)
                    binding.shortByData.text = "Sort by Price (high to low)"
                    binding.filterByData.text = "Filter"
                    shortBydialog.dismiss()
                }

                R.id.priceLToH -> {
                    sortProductList(true)
                    binding.shortByData.text = "Sort by Price (low to high)"
                    binding.filterByData.text = "Filter"
                    shortBydialog.dismiss()
                }

                R.id.popularity -> {
                    mList.sortBy { it?.rating }
                    mList.reverse()
                    productAdapter.updateStudentList(mList)
                    binding.shortByData.text = "Sort by Popularity"
                    binding.filterByData.text = "Filter"
                    shortBydialog.dismiss()
                }

                R.id.AtoZ -> {
                    mList.sortBy { it?.productname }
                    productAdapter.updateStudentList(mList)
                    binding.shortByData.text = "Sort by A to Z"
                    binding.filterByData.text = "Filter"
                    shortBydialog.dismiss()
                }

                R.id.ZtoA -> {
                    mList.sortBy { it?.productname }
                    mList.reverse()
                    productAdapter.updateStudentList(mList)
                    binding.shortByData.text = "Sort by Z to A"
                    binding.filterByData.text = "Filter"
                    shortBydialog.dismiss()
                }
            }
        }

        shortBydialog.show()
    }


    private fun sortProductList(ascending: Boolean) {
        mList.sortBy { it?.product_price }
        if (!ascending) {
            mList.reverse()
        }
        productAdapter.updateStudentList(mList)
    }


    override fun onProductListClick(pId: String) {
        val bundle = Bundle()
        bundle.putString("pid", pId)
        findNavController().navigate(R.id.action_ProductListFragment_to_Product, bundle)
    }

    override fun onaddtoCartClick(
        pId: String,
        price: String,
        discountPrice: String,
        quantity: String,
        totalPrice: String
    ) {
        val currentuseremail = sharedPreferences.getEmail()
        viewmodel.addtocart(currentuseremail, pId, price, discountPrice, "1", price, "0")

        var countingCartItem = binding.cartcount.text.toString()

        binding.cartcount.text = (Integer.parseInt(countingCartItem) + 1).toString()

    }

    override fun onDeleteCartClick(pId: String, price: String) {
        val currentuseremail = sharedPreferences.getEmail()
        viewmodel.deletefromcart(currentuseremail, pId, "")

        var countingCartItem = binding.cartcount.text.toString()

        binding.cartcount.text = (Integer.parseInt(countingCartItem) - 1).toString()


    }
}