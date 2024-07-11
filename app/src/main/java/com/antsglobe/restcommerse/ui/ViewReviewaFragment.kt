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
import androidx.recyclerview.widget.LinearLayoutManager
import com.antsglobe.restcommerse.R
import com.antsglobe.restcommerse.Utils.PreferenceManager
import com.antsglobe.restcommerse.adapter.ReviewListAdaptor
import com.antsglobe.restcommerse.databinding.FragmentViewReviewaBinding
import com.antsglobe.restcommerse.network.RetrofitClient
import com.antsglobe.restcommerse.viewmodel.ReviewViewModel
import com.antsglobe.restcommerse.viewmodel.ToBeReviewedViewModel
import com.antsglobe.restcommerse.viewmodel.ViewModelFactory


class ViewReviewaFragment : Fragment() {
    private var productId: String? = null
    private lateinit var binding: FragmentViewReviewaBinding
    private lateinit var viewmodel: ReviewViewModel
    private lateinit var sharedPreferences: PreferenceManager
    private lateinit var reviewListAdapter: ReviewListAdaptor

    private lateinit var ToBeReviewedVM: ToBeReviewedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            // param1 = it.getString(ARG_PARAM1)
            productId = it.getString("productId")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //return inflater.inflate(R.layout.fragment_view_reviewa, container, false)
        binding = FragmentViewReviewaBinding.inflate(inflater, container, false)
        viewmodel = ViewModelProvider(
            this, ViewModelFactory(RetrofitClient.apiService)
        )[ReviewViewModel::class.java]
        sharedPreferences = PreferenceManager(requireContext())
        viewmodel.getReviewResponse(productId!!)

        binding.llLoadingScreen.visibility = View.VISIBLE
        binding.llMainScreen.visibility = View.GONE
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        ToBeReviewedVM = ViewModelProvider(
            this,
            ViewModelFactory(RetrofitClient.apiService)
        )[ToBeReviewedViewModel::class.java]
        ToBeReviewedVM.getTobeReviewedListVM(sharedPreferences.getEmail().toString())
        tobeReviewedInitObserver()

        if (sharedPreferences.getMode() == true) {
            binding.logoImageHome.setTextColor(resources.getColor(R.color.blackfordark))
            binding.backButton.setImageResource(R.drawable.ic_baseline_arrow_back_24_dark)
            binding.ivReviewImage.setImageResource(R.drawable.add_review_dark)
            binding.summary.setBackgroundColor(Color.parseColor("#1F201D"))
            binding.fullscreen.setBackgroundColor(resources.getColor(R.color.blackfordark))
            binding.tvRating.setTextColor(resources.getColor(R.color.whitefordark))
            binding.tvRatingCount.setTextColor(resources.getColor(R.color.whitefordark))
            binding.tvOneStar.setTextColor(resources.getColor(R.color.whitefordark))
            binding.tvTwoStar.setTextColor(resources.getColor(R.color.whitefordark))
            binding.tvThreeStar.setTextColor(resources.getColor(R.color.whitefordark))
            binding.tvFourStar.setTextColor(resources.getColor(R.color.whitefordark))
            binding.tvFiveStar.setTextColor(resources.getColor(R.color.whitefordark))
            binding.tvAddReview.setTextColor(resources.getColor(R.color.whitefordark))
            binding.addreview.setTextColor(resources.getColor(R.color.whitefordark))
        }

        binding.addreviews.visibility = View.GONE


        binding.rlAddReview.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("productId", productId)
            findNavController().navigate(R.id.action_ViewReviewDetails_to_AddReview, bundle)
        }

//        viewmodel.getGetReviewResponse.observe(viewLifecycleOwner) { getReviewResponse ->
//            val txtRating = String.format("%.1f", getReviewResponse?.rating?.toDouble())
//            binding.tvRatingCount.text = "${getReviewResponse?.totalreview} ratings"
//            binding.tvRating.text = "${txtRating} / 5.0"
//            binding.ratingBarIndicator.setIsIndicator(true)
//            binding.ratingBarIndicator.rating = txtRating.toFloat()
//            binding.progressBarFive.progress = getReviewResponse?.star5!!.toInt()
//            binding.progressBarFour.progress = getReviewResponse?.star4!!.toInt()
//            binding.progressBarThree.progress = getReviewResponse?.star3!!.toInt()
//            binding.progressBartwo.progress = getReviewResponse?.star3!!.toInt()
//            binding.progressBarOne.progress = getReviewResponse?.star1!!.toInt()
//        }

        viewmodel.getGetReviewResponse.observe(viewLifecycleOwner) { getReviewResponse ->
            getReviewResponse?.let {
                val rating = getReviewResponse.rating
                if (!rating.isNullOrEmpty()) {
                    val txtRating = String.format("%.1f", rating.toDouble())
                    binding.tvRatingCount.text = "${getReviewResponse.totalreview} ratings"
                    binding.tvRating.text = "$txtRating / 5.0"
                    binding.ratingBarIndicator.setIsIndicator(true)
                    binding.ratingBarIndicator.rating = txtRating.toFloat()
                    binding.progressBarFive.progress = getReviewResponse.star5!!.toInt()
                    binding.progressBarFour.progress = getReviewResponse.star4!!.toInt()
                    binding.progressBarThree.progress = getReviewResponse.star3!!.toInt()
                    binding.progressBartwo.progress = getReviewResponse.star2!!.toInt()
                    binding.progressBarOne.progress = getReviewResponse.star1!!.toInt()
                } else {
                }
            }
        }

        viewmodel.getReviewListResponse.observe(viewLifecycleOwner) { ReviewListResp ->
            Log.e("GetReviewListResp", "onCreateView: $ReviewListResp")

            binding.llMainScreen.visibility = View.VISIBLE
            binding.llLoadingScreen.visibility = View.GONE

            if (ReviewListResp!!.isEmpty() && ReviewListResp.size >= 0) {
                binding.rvReviewList.visibility = View.GONE
                binding.llEmptyScreen.visibility = View.VISIBLE
            } else {
                binding.rvReviewList.visibility = View.VISIBLE
                binding.llEmptyScreen.visibility = View.GONE
            }

            reviewListAdapter = ReviewListAdaptor(ReviewListResp!!, requireContext())
            binding.rvReviewList.layoutManager = LinearLayoutManager(context)
            binding.rvReviewList.adapter = reviewListAdapter
        }
    }

    private fun tobeReviewedInitObserver() {
        ToBeReviewedVM.getToBeReviewItem.observe(viewLifecycleOwner) { toBeReviewResp ->
            Log.e("toBeReviewResp", "onCreateView: $toBeReviewResp")

            for (loop in toBeReviewResp) {
                if (loop.product_id == Integer.parseInt(productId)) {
                    binding.addreviews.visibility = View.VISIBLE
                }
            }
//
//            if (toBeReviewResp.isEmpty() && toBeReviewResp.size <= 0) {
//                binding!!.ReviewsRecycleViewList.visibility = View.GONE
//                binding!!.llEmptyScreen.visibility = View.VISIBLE
//            } else {
//                binding!!.ReviewsRecycleViewList.visibility = View.VISIBLE
//                binding!!.llEmptyScreen.visibility = View.GONE
//            }
//
//            val sortedNotificationList = toBeReviewResp.reversed()
//            toBeReviewedAdapter = ToBeReviewedAdapter(sortedNotificationList, sharedPreferences.getMode() == true, requireContext())
//
//            binding!!.tv1.text = "To be reviewed (${toBeReviewResp.size.toString()})"
//            binding!!.ReviewsRecycleViewList.layoutManager = LinearLayoutManager(requireContext())
//            binding!!.ReviewsRecycleViewList.adapter = toBeReviewedAdapter
//            toBeReviewedAdapter.setOnProductClickListener(this)
//
//            sharedPreferences.setNotificationBlinkCount(toBeReviewResp.size.toString())


        }
    }
}