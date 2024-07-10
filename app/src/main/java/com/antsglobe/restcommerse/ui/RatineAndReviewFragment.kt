package com.antsglobe.restcommerse.ui

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.antsglobe.aeroquiz.NotificationAdapter
import com.antsglobe.aeroquiz.ReviewedAdapter
import com.antsglobe.aeroquiz.ToBeReviewedAdapter
import com.antsglobe.restcommerse.R
import com.antsglobe.restcommerse.Utils.PreferenceManager
import com.antsglobe.restcommerse.databinding.FragmentRatineAndReviewBinding
import com.antsglobe.restcommerse.network.RetrofitClient
import com.antsglobe.restcommerse.viewmodel.ReviewedViewModel
import com.antsglobe.restcommerse.viewmodel.ToBeReviewedViewModel
import com.antsglobe.restcommerse.viewmodel.ViewModelFactory

class RatineAndReviewFragment : Fragment(), ToBeReviewedAdapter.OnClickProductListener {
    private var binding: FragmentRatineAndReviewBinding? = null


    private lateinit var ToBeReviewedVM: ToBeReviewedViewModel
    private lateinit var toBeReviewedAdapter: ToBeReviewedAdapter
    private lateinit var sharedPreferences: PreferenceManager

    private lateinit var reviewedVM: ReviewedViewModel
    private lateinit var reviewedAdapter: ReviewedAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            //  param1 = it.getString(ARG_PARAM1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //return inflater.inflate(R.layout.fragment_ratine_and_review, container, false)
        binding = FragmentRatineAndReviewBinding.inflate(inflater, container, false)
        sharedPreferences = PreferenceManager(requireContext())

        binding?.backButton?.setOnClickListener {
            findNavController().popBackStack()
        }

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val email = sharedPreferences.getEmail().toString().trim()

        ToBeReviewedVM = ViewModelProvider(
            this,
            ViewModelFactory(RetrofitClient.apiService)
        )[ToBeReviewedViewModel::class.java]


        reviewedVM = ViewModelProvider(
            this,
            ViewModelFactory(RetrofitClient.apiService)
        )[ReviewedViewModel::class.java]

        ToBeReviewedVM.getTobeReviewedListVM(email)
        tobeReviewedInitObserver()


        reviewedVM.getReviewedListVM(email)
        reviewedInitObserver()

        binding?.tobe?.setOnClickListener {
            binding!!.dashline.visibility=View.VISIBLE
            binding!!.dashline2.visibility=View.INVISIBLE
//            binding!!.emptyscreen.visibility=View.GONE
            binding!!.tv2.setTextColor(resources.getColor(R.color.dark_grey))
            binding!!.tv1.setTextColor(resources.getColor(R.color.black))

            binding!!.llReviewList.visibility=View.VISIBLE
            binding!!.llRatingList.visibility=View.GONE




        }

        binding?.reviewed?.setOnClickListener {
            binding!!.dashline.visibility=View.INVISIBLE
            binding!!.dashline2.visibility=View.VISIBLE
//            binding!!.emptyscreen.visibility=View.VISIBLE
            binding!!.tv2.setTextColor(Color.BLACK)
            binding!!.tv1.setTextColor(resources.getColor(R.color.dark_grey))

            binding!!.llReviewList.visibility=View.GONE
            binding!!.llRatingList.visibility=View.VISIBLE
        }
    }

    private fun reviewedInitObserver() {
        reviewedVM.getReviewItem.observe(viewLifecycleOwner) { reviewResp ->
            Log.e("toBeReviewResp", "onCreateView: $reviewResp")


            if (reviewResp.isEmpty() && reviewResp.size <= 0) {
                binding!!.ratingNReviewsRecycleViewList.visibility = View.GONE
                binding!!.emptyscreen.visibility = View.VISIBLE
            } else {
                binding!!.ratingNReviewsRecycleViewList.visibility = View.VISIBLE
                binding!!.emptyscreen.visibility = View.GONE
            }

            val sortedNotificationList = reviewResp.reversed()
            reviewedAdapter = ReviewedAdapter(sortedNotificationList, sharedPreferences.getMode() == true, requireContext())

            binding!!.tv2.text = "Reviewed (${reviewResp.size.toString()})"
            binding!!.ratingNReviewsRecycleViewList.layoutManager = LinearLayoutManager(requireContext())
            binding!!.ratingNReviewsRecycleViewList.adapter = reviewedAdapter

            sharedPreferences.setNotificationBlinkCount(reviewResp.size.toString())


        }    }

    private fun tobeReviewedInitObserver() {
        ToBeReviewedVM.getToBeReviewItem.observe(viewLifecycleOwner) { toBeReviewResp ->
            Log.e("toBeReviewResp", "onCreateView: $toBeReviewResp")


            if (toBeReviewResp.isEmpty() && toBeReviewResp.size <= 0) {
                binding!!.ReviewsRecycleViewList.visibility = View.GONE
                binding!!.llEmptyScreen.visibility = View.VISIBLE
            } else {
                binding!!.ReviewsRecycleViewList.visibility = View.VISIBLE
                binding!!.llEmptyScreen.visibility = View.GONE
            }

            val sortedNotificationList = toBeReviewResp.reversed()
            toBeReviewedAdapter = ToBeReviewedAdapter(sortedNotificationList, sharedPreferences.getMode() == true, requireContext())

            binding!!.tv1.text = "To be reviewed (${toBeReviewResp.size.toString()})"
            binding!!.ReviewsRecycleViewList.layoutManager = LinearLayoutManager(requireContext())
            binding!!.ReviewsRecycleViewList.adapter = toBeReviewedAdapter
            toBeReviewedAdapter.setOnProductClickListener(this)

            sharedPreferences.setNotificationBlinkCount(toBeReviewResp.size.toString())


        }
    }

    override fun onProductIdClick(pId: String) {
//        Toast.makeText(context, pId, Toast.LENGTH_SHORT).show()

        val bundle = Bundle()
        bundle.putString("productId", pId)
        findNavController().navigate(R.id.action_ViewReviewDetails_to_AddReview, bundle)
    }

}