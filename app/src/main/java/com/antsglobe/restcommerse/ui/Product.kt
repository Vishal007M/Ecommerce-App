package com.antsglobe.restcommerse.ui

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.antsglobe.restcommerse.R
import com.antsglobe.restcommerse.Utils.PreferenceManager
import com.antsglobe.restcommerse.adapter.SimilarProductAdapter
import com.antsglobe.restcommerse.adapter.VariantsAdapter
import com.antsglobe.restcommerse.databinding.FragmentProductBinding
import com.antsglobe.restcommerse.network.RetrofitClient
import com.antsglobe.restcommerse.viewmodel.CartListViewModel
import com.antsglobe.restcommerse.viewmodel.ProductListViewModel
import com.antsglobe.restcommerse.viewmodel.ProductViewModel
import com.antsglobe.restcommerse.viewmodel.TaxAmountViewModel
import com.antsglobe.restcommerse.viewmodel.ViewModelFactory
import com.antsglobe.restcommerse.viewmodel.WishlistViewmodel
import com.bumptech.glide.Glide
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.net.URL
import java.util.Objects

class Product : Fragment(), VariantsAdapter.OnVariantClickListner,
    SimilarProductAdapter.OnClickProductListListener {

    private lateinit var productId: String
    private lateinit var binding: FragmentProductBinding
    private lateinit var viewmodel: ProductViewModel
    private lateinit var wlviewmodel: WishlistViewmodel
    private lateinit var variantsadapter: VariantsAdapter

    private lateinit var sharedPreferences: PreferenceManager
    private lateinit var taxAmountViewModel: TaxAmountViewModel

    private lateinit var productnameview: TextView
    private lateinit var productrating: TextView
    private lateinit var productreviews: TextView
    private lateinit var wishlisticon: ImageView
    private lateinit var backbutton: ImageView
    private lateinit var price: TextView
    private lateinit var dprice: TextView
    private lateinit var description: TextView
    private lateinit var producttypecard: CardView
    private lateinit var producttype: ImageView
    private lateinit var wishlistcard: CardView
    private var pId: String? = null
    private var vId: String? = null
    private var Pquantity: String? = null
    private var productname: String? = null
    private var producturl: String? = null
    private var product_price: String? = null
    private var disc_price: String? = null
    private var productItemSize: String? = null

    private lateinit var cartListViewModel: CartListViewModel
    var ItemInCart: Int = 1
    var totalPrice: Int = 0

    private lateinit var productListViewModel: ProductListViewModel
    private lateinit var productAdapter: SimilarProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProductBinding.inflate(inflater, container, false)

        arguments?.let {
            pId = it.getString("pid")
            vId = it.getString("vid")
            Pquantity = it.getString("quantity")
        }

        if (!Pquantity.isNullOrEmpty()) {
            binding.itemCount.text = Pquantity
        }

        sharedPreferences = PreferenceManager(requireContext())
        binding.llLoadingScreen.visibility = View.VISIBLE
        binding.clMainScreen.visibility = View.GONE

        viewmodel = ViewModelProvider(
            this, ViewModelFactory(RetrofitClient.apiService)
        )[ProductViewModel::class.java]

        taxAmountViewModel = ViewModelProvider(
            this, ViewModelFactory(RetrofitClient.apiService)
        )[TaxAmountViewModel::class.java]

        Log.d("pid", pId.toString())
        viewmodel.productListResponse(sharedPreferences.getEmail().toString(), pId.toString())
        viewmodel.getproductvariations(pId.toString(), sharedPreferences.getEmail().toString())

        productListViewModel = ViewModelProvider(
            this, ViewModelFactory(RetrofitClient.apiService)
        )[ProductListViewModel::class.java]

        return binding?.root

    }

    private fun productlistInitobserver() {
        productListViewModel.getProductListResponse.observe(viewLifecycleOwner) { ProductListResp ->
            Log.e("ProductListResp", "onCreateView: $ProductListResp")
//            binding.clProductList.visibility = View.VISIBLE
//            binding.llLoadingScreen.visibility = View.GONE
//
//            if (ProductListResp!!.isEmpty() && ProductListResp!!.size >= 0) {
//                binding.clProductList.visibility = View.GONE
//                binding.llEmptyScreen.visibility = View.VISIBLE
//            } else {
//                binding.clProductList.visibility = View.VISIBLE
//                binding.llEmptyScreen.visibility = View.GONE
//            }
//            productAdapter = SimilarProductAdapter(ProductListResp!!, requireContext())
//
//            val layoutmanager =
//                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
//            binding.similarProductRecyclerView.layoutManager = layoutmanager
//            productAdapter.setOnClickCategoryListener(this)
//            binding.similarProductRecyclerView.adapter = productAdapter

            val shuffledList = ProductListResp!!.shuffled()
            val randomItems = shuffledList.take(5)

            productAdapter = SimilarProductAdapter(randomItems, requireContext())

            val layoutmanager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            binding.similarProductRecyclerView.layoutManager = layoutmanager
            productAdapter.setOnClickCategoryListener(this)
            binding.similarProductRecyclerView.adapter = productAdapter

        }
    }

    var variationIdData: Int = 0

    private var longDiscr = false

    lateinit var productDialogName: String
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        productnameview = binding.productname
        productrating = binding.productrating
        productreviews = binding.productreviews
        wishlisticon = binding.isaddedtowishlist
        backbutton = binding.backButton
        price = binding.price
        dprice = binding.discPrice
        description = binding.description
        producttype = binding.foodtype
        wishlistcard = binding.view2

        val currentuseremail = sharedPreferences.getEmail().toString()

        if (sharedPreferences.getMode() == true) {
            binding.logoImageHome.setTextColor(resources.getColor(R.color.blackfordark))
            binding.productname.setTextColor(resources.getColor(R.color.whitefordark))
            binding.options.setTextColor(resources.getColor(R.color.whitefordark))
            binding.discPrice.setTextColor(resources.getColor(R.color.whitefordark))
            binding.descheading.setTextColor(resources.getColor(R.color.whitefordark))
            binding.description.setTextColor(resources.getColor(R.color.dark_grey))
            binding.btnAddToCart2.setTextColor(resources.getColor(R.color.blackfordark))
            binding.tvBuyNow.setTextColor(resources.getColor(R.color.blackfordark))
            binding.backButton.setImageResource(R.drawable.ic_baseline_arrow_back_24_dark)
            binding.cart.setImageResource(R.drawable.add_to_cart_dark)
            binding.Product.setBackgroundColor(resources.getColor(R.color.blackfordark))
            binding.view2.setCardBackgroundColor(Color.parseColor("#2C2C2C"))
            binding.itemCount.setTextColor(resources.getColor(R.color.whitefordark))
            binding.minus.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#2C2C2C"))
            binding.add.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#2C2C2C"))
            binding.imageSlider.setBackgroundResource(R.drawable.profile_round_corner_bg_dark_wish)
            binding.tvprice.setTextColor(Color.WHITE)
            binding.similar.setTextColor(Color.WHITE)
        }
        cartListViewModel = ViewModelProvider(
            this, ViewModelFactory(RetrofitClient.apiService)
        )[CartListViewModel::class.java]

        wlviewmodel = ViewModelProvider(
            this, ViewModelFactory(RetrofitClient.apiService)
        )[WishlistViewmodel::class.java]

        wlviewmodel.getcartlist(currentuseremail)
        countCart()

        binding.add.setOnClickListener {

            if (ItemInCart < 29) {
                ItemInCart++

                totalPrice = Integer.parseInt(disc_price.toString()) * ItemInCart
//            Toast.makeText(context, totalPrice.toString(), Toast.LENGTH_SHORT).show()
                binding.totalPP.text = totalPrice.toString()

                binding.itemCount.text = ItemInCart.toString()

                Log.d("increase", ItemInCart.toString())
            }
        }

        binding.more.setOnClickListener {

            if (!longDiscr) {
                longDiscr = true
                binding.longDescription.visibility = View.VISIBLE
                binding.more.text = "- Less"
            } else {
                longDiscr = false
                binding.longDescription.visibility = View.GONE
                binding.more.text = "+ More"

            }
        }

        binding.minus.setOnClickListener {
            if (ItemInCart > 1) {
                ItemInCart--

                totalPrice = Integer.parseInt(disc_price.toString()) * ItemInCart
//                Toast.makeText(context, totalPrice.toString(), Toast.LENGTH_SHORT).show()
                binding.totalPP.text = totalPrice.toString()
                binding.itemCount.text = ItemInCart.toString()
            }
        }

        binding.countcard.setOnClickListener {
            findNavController().navigate(R.id.action_ProductDetailsFragment_to_bottom_menu_my_cart)
        }


        binding.btnAddToCart2.setOnClickListener {

            cartListViewModel.addToCartVM(
                currentuseremail,
                productId,
                priseData,
                discData,
                ItemInCart.toString(),
                totalPrice.toString(),
                variationIdData.toString()
            )
            cartListInitObserver()
//            Toast.makeText(context, variationIdData.toString(), Toast.LENGTH_SHORT).show()
            wlviewmodel.getcartlist(currentuseremail)
            countCart()
            showPopUpDialog()

        }

//        added.observe(viewLifecycleOwner) { newvalue ->
//        }

        var wishlist = false
        wishlistcard.setOnClickListener {
            if (!wishlist) {
                viewmodel.addtowishlist(currentuseremail, pId.toString())
                wishlisticon.setImageResource(R.drawable.heart_plus)
                wishlist = true
            } else {
                viewmodel.deletefromwishlist(currentuseremail, pId.toString())
                wishlisticon.setImageResource(R.drawable.heart_plus_tapped)
                wishlist = false
            }
        }


        binding.shareProduct.setOnClickListener {
            val imageUrl = producturl
            lifecycleScope.launch {
                val bitmap = imageUrl?.let { it1 -> getBitmapFromUrl(it1) }

                var uri: Uri? = null
                try {
                    val imagesFolder = File(requireContext().cacheDir, "images")
                    imagesFolder.mkdirs()
                    val file = File(imagesFolder, "shared_image.png")

                    val stream: OutputStream = FileOutputStream(file)
                    bitmap?.compress(Bitmap.CompressFormat.PNG, 100, stream)
                    stream.flush()
                    stream.close()
                    uri = FileProvider.getUriForFile(
                        requireContext(),
                        "${requireContext().packageName}.fileprovider",
                        file
                    )
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                if (uri != null) {
                    val shareIntent = Intent(Intent.ACTION_SEND)
                    shareIntent.type = "image/png"
                    shareIntent.putExtra(Intent.EXTRA_STREAM, uri)

                    // Additional text to share
                    val shareAppLink =
                        "https://play.google.com/store/apps/details?id=${context?.packageName}"
                    shareIntent.putExtra(
                        Intent.EXTRA_TEXT,
                        "Product Id = $productId \nProduct Name = ${productnameview.text} \nPrice = ${disc_price} \n\n $shareAppLink"
                    )

                    startActivity(Intent.createChooser(shareIntent, "Share Using"))
                } else {
                    // Handle error here
                }
            }
        }

        backbutton.setOnClickListener {
            findNavController().popBackStack()
        }

        viewmodel.getProductResponse?.observe(viewLifecycleOwner) { productdata ->
            if (productdata != null) {
                productId = productdata.PID.toString()

                binding.clMainScreen.visibility = View.VISIBLE
                binding.llLoadingScreen.visibility = View.GONE

                if (!productdata.is_variant) {
                    binding.options.visibility = View.GONE
                    binding.variantsRecycleview.visibility = View.GONE
                    variationIdData = 0
                } else {
                    Log.d("afsbdg", "before")
                    viewmodel.variationData.observe(viewLifecycleOwner) { variationDatab ->
                        variantsadapter =
                            VariantsAdapter(variationDatab, productdata.disc_price, vId)
                        val layoutmanager = LinearLayoutManager(
                            requireContext(), LinearLayoutManager.HORIZONTAL, false
                        )

                        for (loop in variationDatab) {
                            if (productdata.disc_price.toDouble() == loop.discount_price) {
                                variationIdData = loop.Variation_id
                            }
                        }

                        binding.variantsRecycleview.layoutManager = layoutmanager
                        variantsadapter.setonvariantclick(this)
                        binding.variantsRecycleview.adapter = variantsadapter
                    }
                }

                productnameview.text = productdata.productname
                productItemSize = productnameview.text.toString()
                productDialogName = productdata.productname.toString()
                if (productdata.prod_type == "Non-Veg") {
                    producttype.setImageResource(R.drawable.non_veg_icon)
                } else if (productdata.prod_type == "Veg") {
                    producttype.setImageResource(R.drawable.veg_icon)
                }
                val rating = String.format("%.1f", productdata.rating)
                binding.ratingBarIndicator.rating = rating.toFloat()
                productrating.text = rating
                productreviews.text = "(${productdata.totalreview.toString()})"
                description.text = productdata.short_descrip

                discData = productdata.product_price.toString()
                priseData = productdata.disc_price.toString()

                price.text = "₹" + productdata.product_price.toString()
                price.setPaintFlags(price.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)
                dprice.text = "₹" + productdata.disc_price.toString()
                binding.totalPP.text = productdata.disc_price.toString()
                producturl = productdata.product_url

                if (productdata.long_descrip.isNullOrEmpty()) {
                    binding.more.visibility = View.GONE
                    binding.longDescription.visibility = View.GONE
                    longDiscr = false

                } else {
                    binding.more.visibility = View.VISIBLE
                    binding.longDescription.text = productdata.long_descrip
                }

                Glide.with(binding.showMainImg)
                    .load(productdata.product_url)
                    .into(binding.showMainImg)

                topSliderImplementation(
                    producturl,
                    productdata.img1_url,
                    productdata.img2_url,
                    productdata.img3_url,
                    productdata.img4_url
                )
                var wishlist = productdata.wishlist_status
                if (!wishlist) {
                    wishlisticon.setImageResource(R.drawable.heart_plus)
                } else {
                    wishlisticon.setImageResource(R.drawable.heart_plus_tapped)
                }
                wishlisticon.setOnClickListener {
                    if (!wishlist) {
                        viewmodel.addtowishlist(currentuseremail, pId.toString())
                        wishlisticon.setImageResource(R.drawable.heart_plus_tapped)
                        wishlist = true
                    } else {
                        viewmodel.deletefromwishlist(currentuseremail, pId.toString())
                        wishlisticon.setImageResource(R.drawable.heart_plus)
                        wishlist = false
                    }
                }

                if (productdata.prod_availability.toString() == "Out of Stock") {
//                itemsBinding.stockChecking.visibility = View.VISIBLE
//                    itemsBinding.foodItemImg.setColorFilter(ContextCompat.getColor(context, R.color.transparent_blur))

                    binding.buyingButton.visibility = View.GONE
                    binding.PDOutOfStock.visibility = View.VISIBLE
                    binding.OOS.visibility = View.VISIBLE
                } else {

                    binding.buyingButton.visibility = View.VISIBLE
                    binding.PDOutOfStock.visibility = View.GONE
                    binding.OOS.visibility = View.GONE
                }

                disc_price = productdata.disc_price.toString()
                totalPrice = Integer.parseInt(disc_price)

                binding.llReview.setOnClickListener {
//                    if (productdata.totalreview == 0) {
////                        val bundle = Bundle()
////                        bundle.putString("productId", productId)
////                        findNavController().navigate(
////                            R.id.action_product_details_to_AddReview, bundle
////                        )
//                        customToast(requireContext(), "No Reviews Available", R.drawable.ic_info)
//
//                    } else {
                    val bundle = Bundle()
                    bundle.putString("productId", productId)
                    findNavController().navigate(
                        R.id.action_product_details_to_ViewReviewDetails, bundle
                    )
//                    }
                }

                binding!!.tvBuyNow.setOnClickListener {

                    val quantity = binding.itemCount.text.toString()
                    val price = productdata.disc_price.toString()
                    val total_price = quantity.toInt() * price.toInt()

                    taxAmountViewModel.TaxResponseVM(
                        currentuseremail,
                        total_price.toString(),
                        "0".toString(),
                        "0",
                        "0"
                    )
                    cartTaxListInitObserver()

                }
            }

            var cstegoryid = productdata?.cat_id
            productListViewModel.productListResponse(
                sharedPreferences.getEmail().toString(), cstegoryid.toString()
            )
            productlistInitobserver()
        }
    }

    private suspend fun getBitmapFromUrl(url: String): Bitmap? {
        return withContext(Dispatchers.IO) {
            try {
                val connection = URL(url).openConnection()
                connection.connect()
                val input = connection.getInputStream()
                BitmapFactory.decodeStream(input)
            } catch (e: IOException) {
                e.printStackTrace()
                null
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

    private fun cartTaxListInitObserver() {
        taxAmountViewModel.getTaxResponse.observe(viewLifecycleOwner) { taxResp ->

            val total_price = taxResp?.total_price
            val promodisc = taxResp?.promodisc.toString()
            val shipcharge = taxResp?.shipcharge.toString()
            val other_disc = taxResp?.other_disc
            val taxableAmt = taxResp?.taxableAmt
            val taxper = taxResp?.taxper
            val taxamt = taxResp?.taxamt
            val grandtotal = taxResp?.grandtotal

//            val intent = Intent(context, CheckoutActivity::class.java)
//            //  Log.e("TAG", "CheckoutActivity: $cartValueAmount, $couponValueAmount, $strCouponName",)
//            intent.putExtra("single_product", true)
//            intent.putExtra("CouponName", "")
//            intent.putExtra("total_price", "${total_price.toString()}")
//            intent.putExtra("Coupon_Discount", "")
//            Log.e("Product", "onViewValue:  $productId, $variationIdData, ${binding?.discPrice?.text.toString()}", )
//            startActivity(intent)
//            requireActivity().finish()

            val intent = Intent(context, CheckoutActivity::class.java)
//            Log.e("TAG", "CheckoutActivity: $cartValueAmount, $couponValueAmount, $strCouponName")
            intent.putExtra("CouponName", "")
            intent.putExtra("single_product", true)
            intent.putExtra("CouponName", "")
            intent.putExtra("total_price", total_price)
            intent.putExtra("product_id", productId)

            intent.putExtra("variation_id", variationIdData.toString())
            intent.putExtra("quantity", binding.itemCount.text.toString())
            intent.putExtra("discount_price", binding.discPrice?.text.toString().replace("₹", ""))
            intent.putExtra("orginal_price", binding.price?.text.toString().replace("₹", ""))

            intent.putExtra("promodisc", promodisc)
            intent.putExtra("other_disc", other_disc)
            intent.putExtra("shipcharge", shipcharge)
            intent.putExtra("taxableAmt", taxableAmt)
            intent.putExtra("taxper", taxper)
            intent.putExtra("taxamt", taxamt)
            sharedPreferences.setSingleProductBuy(true)

            intent.putExtra("grandtotal", grandtotal)
            startActivity(intent)
            requireActivity().finish()

        }
    }

    lateinit var priseData: String
    lateinit var discData: String

    private fun countCart() {
        wlviewmodel.cartListsize.observe(viewLifecycleOwner) { count ->
            Log.d("count", count.toString())
            if (count != null) {
                if (count.compareTo(1) >= 0) {
                    binding.cartcount.visibility = View.VISIBLE
                    binding.cartcount.text = count.toString()
                    if (sharedPreferences.getMode() == true) {
                        binding.countbackground.setBackgroundColor(Color.BLACK)
                    }
                } else {
                    binding.cartcount.visibility = View.GONE
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        arguments?.let {
            pId = it.getString("pid")
        }

        sharedPreferences = PreferenceManager(requireContext())
        binding.llLoadingScreen.visibility = View.VISIBLE
        binding.clMainScreen.visibility = View.GONE

        viewmodel = ViewModelProvider(
            this, ViewModelFactory(RetrofitClient.apiService)
        )[ProductViewModel::class.java]
        Log.d("pid", pId.toString())
        viewmodel.productListResponse(sharedPreferences.getEmail().toString(), pId.toString())
    }

    private fun cartListInitObserver() {
        cartListViewModel.addCartData.observe(viewLifecycleOwner) { productdata ->
            if (productdata.is_success) {
            }
        }
    }

    private var popUpDialog: Dialog? = null

    private fun showPopUpDialog() {
        popUpDialog = Dialog(requireContext(), R.style.popup_dialog)
        if (sharedPreferences.getMode() == true) {
            popUpDialog?.setContentView(R.layout.popup_dialogbox_added_to_cart_dark)
        } else {
            popUpDialog?.setContentView(R.layout.popup_dialogbox_added_to_cart)
        }

        val window: Window? = popUpDialog!!.getWindow()
        if (window != null) {
            val params = window.attributes
            params.gravity = Gravity.BOTTOM
            params.dimAmount = 0.2f
            window.attributes = params
        }
        popUpDialog!!.show()
        Objects.requireNonNull<Window>(popUpDialog!!.getWindow())
            .setBackgroundDrawableResource(R.drawable.border_popup_bg)

        val productNameText: TextView = popUpDialog!!.findViewById(R.id.productName)
        val laterBtn: TextView = popUpDialog!!.findViewById(R.id.doneCart)
        val updateNowBtn: TextView = popUpDialog!!.findViewById(R.id.checkoutCart)
        productNameText.text = "${productDialogName}"

        laterBtn.setOnClickListener { popUpDialog!!.dismiss() }

        updateNowBtn.setOnClickListener {
            findNavController().navigate(R.id.action_ProductDetailsFragment_to_bottom_menu_my_cart)
            popUpDialog!!.dismiss()
        }
    }

    private fun topSliderImplementation(
        producturl: String?,
        img1Url: String?,
        img2Url: String?,
        img3Url: String?,
        img4Url: String?
    ) {

        val imageList = ArrayList<SlideModel>()
        producturl?.let {
            imageList.add(SlideModel(it, ScaleTypes.CENTER_INSIDE))
        }
        img1Url?.let {
            imageList.add(SlideModel(it, ScaleTypes.CENTER_INSIDE))
        }
        img2Url?.let {
            imageList.add(SlideModel(it, ScaleTypes.CENTER_INSIDE))
        }
        img3Url?.let {
            imageList.add(SlideModel(it, ScaleTypes.CENTER_INSIDE))
        }
        img4Url?.let {
            imageList.add(SlideModel(it, ScaleTypes.CENTER_INSIDE))
        }

        val imageSlider = binding.imageSlider
        imageSlider.setImageList(imageList)

//        val imageList = ArrayList<SlideModel>()
//        imageList.add(
//            SlideModel(
//                this.producturl, ScaleTypes.FIT
//            )
//        )
//        imageList.add(
//            SlideModel(
//                this.producturl, ScaleTypes.FIT
//            )
//        )
//        val imageSlider = binding.imageSlider
//        imageSlider.setImageList(imageList)
    }

    override fun onvariantclick(vid: Int, price: Int, dprice: Int, itemSize: String) {
        Log.d("reached", "reached")
        binding.price.text = "₹" + price.toString()
        binding.discPrice.text = "₹" + dprice.toString()
        val currentuseremail = sharedPreferences.getEmail()

        productnameview.text = "$productItemSize - $itemSize"

        variationIdData = vid
        binding.btnAddToCart2.setOnClickListener {

            cartListViewModel.addToCartVM(
                currentuseremail,
                productId,
                price.toString(),
                dprice.toString(),
                ItemInCart.toString(),
                totalPrice.toString(),
                variationIdData.toString(),

                )
            cartListInitObserver()
//            Toast.makeText(context, variationIdData.toString(), Toast.LENGTH_SHORT).show()
            if (currentuseremail != null) {
                wlviewmodel.getcartlist(currentuseremail)
            }
            countCart()
            showPopUpDialog()
        }
    }

    override fun onProductListClick(nPId: String) {
        val bundle = Bundle()
        bundle.putString("pid", nPId)
        findNavController().navigate(R.id.action_Product_to_Product, bundle)
    }

}