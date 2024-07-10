package com.antsglobe.restcommerse.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.antsglobe.restcommerse.R
import com.antsglobe.restcommerse.Utils.PreferenceManager
import com.antsglobe.restcommerse.adapter.WishlistAdapter
import com.antsglobe.restcommerse.databinding.FragmentWishListBinding
import com.antsglobe.restcommerse.network.RetrofitClient
import com.antsglobe.restcommerse.viewmodel.ViewModelFactory
import com.antsglobe.restcommerse.viewmodel.WishlistViewmodel

class WishListFragment : Fragment(), WishlistAdapter.OnClickDeleteListener {


    private var tcount = 0
    private lateinit var binding: FragmentWishListBinding
    private lateinit var viewmodel: WishlistViewmodel
    private lateinit var sharedpreference: PreferenceManager
    private lateinit var wishlistAdapter: WishlistAdapter
    private var isProfile = false
    private var listener: OnWishlistBadgeChangeListener? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            // param1 = it.getString(ARG_PARAM1)
            isProfile = it.getBoolean("isMyProfile")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentWishListBinding.inflate(inflater, container, false)

        return binding?.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewmodel = ViewModelProvider(
            this, ViewModelFactory(RetrofitClient.apiService)
        )[WishlistViewmodel::class.java]
        sharedpreference = PreferenceManager(requireContext())
        val currentuseremail = sharedpreference.getEmail()!!
        viewmodel.getProductList(currentuseremail)
        viewmodel.getcartlist(currentuseremail)

        binding.llLoadingScreen.visibility = View.VISIBLE
        binding.clMainScreen.visibility = View.GONE

        if (sharedpreference.getMode() == true) {
            binding.logoImageHome.setTextColor(resources.getColor(R.color.blackfordark))
            binding.backButton.setImageResource(R.drawable.ic_baseline_arrow_back_24_dark)
            binding.Product.setBackgroundColor(resources.getColor(R.color.blackfordark))
            binding.clMainScreen.setBackgroundColor(resources.getColor(R.color.blackfordark))
            binding.cartcount.setBackgroundColor(resources.getColor(R.color.blackfordark))
            binding.carticonlayout.setBackgroundColor(resources.getColor(R.color.blackfordark))
            binding.cart.setImageDrawable(resources.getDrawable(R.drawable.add_to_cart_dark))
            binding.emptyicon.setImageResource(R.drawable.emptywishlist_dark)
        } else {
            binding.Product.setBackgroundColor(resources.getColor(R.color.whitefordark))
            binding.clMainScreen.setBackgroundColor(resources.getColor(R.color.whitefordark))
            binding.cartcount.setBackgroundColor(resources.getColor(R.color.whitefordark))
            binding.carticonlayout.setBackgroundColor(resources.getColor(R.color.whitefordark))
            binding.cart.setImageDrawable(resources.getDrawable(R.drawable.add_to_cart))
        }
        /*  if (isProfile == true) {
              binding.backButton.visibility = View.VISIBLE
          } else {
              binding.backButton.visibility = View.GONE
          }*/

        viewmodel.cartListsize.observe(viewLifecycleOwner) { count ->
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


        viewmodel.cartListResponse.observe(viewLifecycleOwner) { cart ->

            viewmodel.apiresponse.observe(viewLifecycleOwner) {
                Log.d("this", it?.size.toString())
                tcount = it!!.size
                binding.clMainScreen.visibility = View.VISIBLE
                binding.llLoadingScreen.visibility = View.GONE
                wishlistAdapter = WishlistAdapter(it, cart, requireContext())
                sharedpreference.setWishListCount(it.size.toString())
                listener?.onWishlistBadgeChanged(it.size.toString())

                wishlistAdapter.notifyDataSetChanged()

                if (it.isEmpty() && it.size <= 0) {
                    binding.clMainScreen.visibility = View.GONE
                    binding.llEmptyScreen.visibility = View.VISIBLE
                } else {
                    binding.clMainScreen.visibility = View.VISIBLE
                    binding.llEmptyScreen.visibility = View.GONE
                }
                val layoutmanager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                binding.wishlistrecyclerview.layoutManager = layoutmanager
                wishlistAdapter.setOnClickProductListener(this)
                binding.wishlistrecyclerview.adapter = wishlistAdapter

            }
            binding.backButton.setOnClickListener {
                findNavController().popBackStack()
            }
            binding.countcard.setOnClickListener {
                findNavController().navigate(R.id.action_MyWishListFragment_to_bottom_menu_my_cart)
            }
        }
    }


    override fun onProductdeleteClick(pId: String) {
        val currentuseremail = sharedpreference.getEmail()!!
        tcount = tcount - 1
        Log.d("that", tcount.toString())

        if (tcount == 0) {
            binding.clMainScreen.visibility = View.GONE
            binding.llEmptyScreen.visibility = View.VISIBLE
        } else {
            binding.clMainScreen.visibility = View.VISIBLE
            binding.llEmptyScreen.visibility = View.GONE
        }

        viewmodel.deletefromwishlist(currentuseremail, pId)

    }

    override fun onProductClick(pId: String) {
        val bundle = Bundle()
        bundle.putString("pid", pId)
        findNavController().navigate(R.id.action_MyWishListFragment_to_Product, bundle)
    }

    override fun onaddtoCartClick(
        pId: String,
        price: String,
        discountPrice: String,
        quantity: String,
        totalPrice: String
    ) {
        val currentuseremail = sharedpreference.getEmail()
        viewmodel.addtocart(currentuseremail, pId, price, discountPrice, "1", price, "0")
    }

    override fun onDeleteCartClick(pId: String, price: String, discountPrice: String, variation: String) {
        val currentuseremail = sharedpreference.getEmail()
        viewmodel.deletefromcart(currentuseremail, pId, "")
    }

    interface OnWishlistBadgeChangeListener {
        fun onWishlistBadgeChanged(itemSize: String)
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnWishlistBadgeChangeListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnWishlistBadgeChangeListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }
}