package com.antsglobe.restcommerse.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
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
import com.antsglobe.aeroquiz.CartListAdapter
import com.antsglobe.restcommerse.R
import com.antsglobe.restcommerse.Utils.PreferenceManager
import com.antsglobe.restcommerse.databinding.FragmentMyCartBinding
import com.antsglobe.restcommerse.network.RetrofitClient
import com.antsglobe.restcommerse.ui.bottomSheet.ViewCouponDialogFragment
import com.antsglobe.restcommerse.viewmodel.AllMostPopularViewModel
import com.antsglobe.restcommerse.viewmodel.CartListViewModel
import com.antsglobe.restcommerse.viewmodel.TaxAmountViewModel
import com.antsglobe.restcommerse.viewmodel.ViewModelFactory

class MyCartFragment : Fragment(), CartListAdapter.OnClickDeleteListener,
    CartListAdapter.OnClickProductAddListener, CartListAdapter.OnClickProductMinusListener,
    CartListAdapter.OnClickProductListener, ViewCouponDialogFragment.DiscountListener,
    CartListAdapter.OnClickWishAddClickListener {

    private var _binding: FragmentMyCartBinding? = null
    private val binding get() = _binding!!

    private lateinit var cartListViewModel: CartListViewModel
    private lateinit var taxAmountViewModel: TaxAmountViewModel
    private lateinit var cartListAdapter: CartListAdapter
    private lateinit var sharedPreferences: PreferenceManager
    val bottomSheetFragment = ViewCouponDialogFragment()
    private var TotalItemAmount: Int = 0
    private var cartValueAmount: String = ""
    private var couponValueAmount: Double = 0.0
    private var strCouponName: String = ""
    private lateinit var viewModel: AllMostPopularViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyCartBinding.inflate(inflater, container, false)
        sharedPreferences = PreferenceManager(requireContext())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val email = sharedPreferences.getEmail().toString().trim()
//        cartListAdapter = CartListAdapter(ArrayList<CartListData>())

        if (sharedPreferences.getMode() == true) {

            binding.logoImageHome.setTextColor(resources.getColor(R.color.blackfordark))
            binding.coupon.setTextColor(resources.getColor(R.color.whitefordark))
            binding.cartTotal.setTextColor(resources.getColor(R.color.whitefordark))
            binding.totalAmountOfCart.setTextColor(resources.getColor(R.color.whitefordark))
            binding.checkout.setTextColor(resources.getColor(R.color.blackfordark))
            binding.tohomepage.setTextColor(resources.getColor(R.color.blackfordark))
            binding.backButton.setImageResource(R.drawable.ic_baseline_arrow_back_24_dark)
            binding.fullscreen.setBackgroundColor(resources.getColor(R.color.blackfordark))
            binding.llLoadingScreen.setBackgroundColor(resources.getColor(R.color.blackfordark))
            binding.llEmptyScreen.setBackgroundColor(resources.getColor(R.color.blackfordark))
            binding.linearLayout3.setBackgroundColor(resources.getColor(R.color.blackfordark))
            binding.emptyicon.setImageResource(R.drawable.emptycart_dark)
        }

        cartListViewModel = ViewModelProvider(
            this, ViewModelFactory(RetrofitClient.apiService)
        )[CartListViewModel::class.java]
        viewModel = ViewModelProvider(
            this, ViewModelFactory(RetrofitClient.apiService)
        )[AllMostPopularViewModel::class.java]

        taxAmountViewModel = ViewModelProvider(
            this, ViewModelFactory(RetrofitClient.apiService)
        )[TaxAmountViewModel::class.java]

        binding.llLoadingScreen.visibility = View.VISIBLE
        binding.cartListRecyclerView.visibility = View.GONE

        cartListViewModel.getCartListVM(email)
        cartListInitObserver()

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.couponlayout.setOnClickListener {

            bottomSheetFragment.discountListener = this
            bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.tag)
        }

        binding.checkout.setOnClickListener {

            val Discount = cartValueAmount.toInt() * (couponValueAmount / 100)

            taxAmountViewModel.TaxResponseVM(email,cartValueAmount,Discount.toString(),"0", "0")
            cartTaxListInitObserver()
        }
        binding.tohomepage.setOnClickListener {
            findNavController().navigate(R.id.action_bottom_menu_my_cart_to_HomeFragment)
        }
    }

    private fun cartTaxListInitObserver() {
        taxAmountViewModel.getTaxResponse.observe(viewLifecycleOwner){ taxResp ->

            val total_price = taxResp?.total_price
            val promodisc = taxResp?.promodisc.toString()
            val shipcharge = taxResp?.shipcharge.toString()
            val other_disc = taxResp?.other_disc
            val taxableAmt = taxResp?.taxableAmt
            val taxper = taxResp?.taxper
            val taxamt = taxResp?.taxamt
            val grandtotal = taxResp?.grandtotal


            val intent = Intent(context, CheckoutActivity::class.java)
            Log.e("TAG", "CheckoutActivity: $cartValueAmount, $couponValueAmount, $strCouponName")
            intent.putExtra("CouponName", strCouponName)
            intent.putExtra("total_price", total_price)
            intent.putExtra("promodisc", promodisc)
            intent.putExtra("other_disc", other_disc)
            intent.putExtra("shipcharge", shipcharge)
            intent.putExtra("taxableAmt", taxableAmt)
            intent.putExtra("taxper", taxper)
            intent.putExtra("taxamt", taxamt)
            intent.putExtra("grandtotal", grandtotal)
            startActivity(intent)
            requireActivity().finish()

        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun cartListInitObserver() {

        cartListViewModel.getCartListData.observe(viewLifecycleOwner) { mostPopularResp ->
            Log.e("mostPopularResp", "onCreateView: $mostPopularResp")

            cartListAdapter = context?.let { CartListAdapter(mostPopularResp, it) }!!
            binding.cartListRecyclerView.visibility = View.VISIBLE
            binding.llLoadingScreen.visibility = View.GONE

            cartListAdapter.notifyItemInserted(id)

            if (mostPopularResp.isEmpty() && mostPopularResp.size >= 0) {
                binding.cartListRecyclerView.visibility = View.GONE
                binding.llEmptyScreen.visibility = View.VISIBLE
            } else {
                binding.cartListRecyclerView.visibility = View.VISIBLE
                binding.llEmptyScreen.visibility = View.GONE
            }

//            cartListAdapter.updateItems(mostPopularResp)
            cartListAdapter.setOnClickProductDeleteListener(this)
            cartListAdapter.setOnClickProductViewListener(this)
            cartListAdapter.setOnClickProductAddListener(this)
            cartListAdapter.setOnClickProductMinusListener(this)
            cartListAdapter.setOnClickWishlistAddListener(this)

            binding.cartListRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            binding.cartListRecyclerView.adapter = cartListAdapter
            TotalItemAmount = 0

            for (countLoop in mostPopularResp) {
                TotalItemAmount += countLoop.dis_price * countLoop.quantity
            }
            cartValueAmount = TotalItemAmount.toString()
            binding.totalAmountOfCart.text = "₹ ${TotalItemAmount}"
            binding.coupon.text = "View all payment coupons"
            if (sharedPreferences.getMode() == true) {
                binding.coupon.setTextColor(Color.WHITE)
            } else {
                binding.coupon.setTextColor(Color.BLACK)
            }
            binding.couponicon.setImageResource(R.drawable.arrow_back)
            binding.couponicon.setOnClickListener {
                bottomSheetFragment.discountListener = this
                bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.tag)
                binding.totalAmountOfCart.text = "₹ ${TotalItemAmount}"
            }
            binding.couponlayout.visibility = View.VISIBLE
            binding.couponlayout.setOnClickListener {
                bottomSheetFragment.discountListener = this
                bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.tag)
            }
            cartListAdapter.notifyDataSetChanged()
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onProductIdClick(pId: String, vId: String, quantity: String) {
        val bundle = Bundle()
        bundle.putString("pid", pId)
        bundle.putString("vid", vId)
        bundle.putString("quantity", quantity)
        findNavController().navigate(R.id.action_bottom_menu_my_cart_to_Product, bundle)
    }

    override fun onProductdeleteClick(
        pId: String, pPrice: String, toString: String, variation: String
    ) {
        val currentuseremail = sharedPreferences.getEmail()!!
        cartListViewModel.deleteFromCartVM(currentuseremail, pId, variation)
        cartListViewModel.getCartListVM(currentuseremail)
        cartListInitObserver()

    }

    override fun onResume() {
        super.onResume()
        val email = sharedPreferences.getEmail().toString().trim()
        cartListViewModel.getCartListVM(email)
        cartListInitObserver()
    }

    override fun onProductAddClick(
        pId: String,
        pPrice: String,
        discPrice: String,
        variation: String,
    ) {
        val currentuseremail = sharedPreferences.getEmail()!!
        cartListViewModel.addToCartVM(currentuseremail, pId, pPrice, discPrice, "1", "0", variation)
        cartListViewModel.getCartListVM(currentuseremail)
//        cartListAddInitObserver()
        cartListInitObserver()

    }

    override fun onProductMinusClick(
        pId: String, pPrice: String, discPrice: String, variation: String
    ) {
        val currentuseremail = sharedPreferences.getEmail()!!

        cartListViewModel.addToCartVM(
            currentuseremail, pId, pPrice, discPrice, "-1", "0", variation
        )
        cartListViewModel.getCartListVM(currentuseremail)
//        cartListMinusInitObserver()
        cartListInitObserver()

    }

    private fun cartListAddInitObserver() {
        cartListViewModel.addCartData.observe(viewLifecycleOwner) { productdata ->
            if (productdata.is_success) {
                customToast(requireContext(), "added in cart", R.drawable.success_toast_icon)

//                cartListAdapter.notifyDataSetChanged()

            }
        }
    }

    private fun cartListMinusInitObserver() {
        cartListViewModel.addCartData.observe(viewLifecycleOwner) { minusProductData ->
            if (minusProductData.is_success) {
                customToast(requireContext(), "minus in cart", R.drawable.success_toast_icon)

//                cartListAdapter.notifyDataSetChanged()

            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cartListViewModel.getCartListData.removeObservers(viewLifecycleOwner)

    }

    override fun onDiscountApplied(discount: Double, couponName: String?) {
        this.couponValueAmount = discount
        this.strCouponName = couponName.toString()
        Log.d("DiscountApplied", "Discount applied: $discount")
        val Discount = TotalItemAmount * (discount / 100)
        binding.couponlayout.visibility = View.GONE
        binding.totalAmountOfCart.text = "₹ ${TotalItemAmount - Discount.toInt()}"
        binding.coupon.text = discount.toInt().toString() + "% Discount coupon is Applied"
        binding.coupon.setTextColor(resources.getColor(R.color.orange))
        binding.couponicon.setImageResource(R.drawable.delete)


        binding.couponicon.setOnClickListener {
            binding.couponlayout.visibility = View.VISIBLE

            binding.couponicon.setImageResource(R.drawable.arrow_back)
            binding.totalAmountOfCart.text = "₹ ${TotalItemAmount}"
            if (sharedPreferences.getMode() == true) {
                binding.coupon.setTextColor(Color.WHITE)
            } else {
                binding.coupon.setTextColor(Color.BLACK)
            }
            binding.coupon.text = "View all payment coupons"
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

    override fun onWishAddClick(pId: String) {
        val curretuseremail = sharedPreferences.getEmail()
        viewModel.addtowishlist(curretuseremail.toString(), pId)
    }
}