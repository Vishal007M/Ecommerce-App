package com.antsglobe.restcommerse.ui

import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.antsglobe.restcommerse.R
import com.antsglobe.restcommerse.Utils.PreferenceManager
import com.antsglobe.restcommerse.databinding.FragmentAddReviewBinding
import com.antsglobe.restcommerse.network.RetrofitClient
import com.antsglobe.restcommerse.viewmodel.AddReviewViewModel
import com.antsglobe.restcommerse.viewmodel.ViewModelFactory

class AddReviewFragment : Fragment() {

    private lateinit var binding: FragmentAddReviewBinding
    private lateinit var viewmodel: AddReviewViewModel
    private lateinit var sharedPreferences: PreferenceManager
    private var productId: String? = null
    private var charCounts = 0

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
        //return inflater.inflate(R.layout.fragment_add_review, container, false)
        binding = FragmentAddReviewBinding.inflate(inflater, container, false)
        viewmodel = ViewModelProvider(
            this, ViewModelFactory(RetrofitClient.apiService)
        )[AddReviewViewModel::class.java]
        sharedPreferences = PreferenceManager(requireContext())


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        if (sharedPreferences.getMode() == true) {
            binding.logoImageHome.setTextColor(resources.getColor(R.color.blackfordark))
            binding.btnAddReview.setTextColor(resources.getColor(R.color.blackfordark))
            binding.add.setTextColor(resources.getColor(R.color.whitefordark))
            binding.tvCount.setTextColor(resources.getColor(R.color.whitefordark))
            binding.tvRating.setTextColor(resources.getColor(R.color.whitefordark))
            binding.write.setTextColor(resources.getColor(R.color.whitefordark))
            binding.backButton.setImageResource(R.drawable.ic_baseline_arrow_back_24_dark)
            binding.tvAddReview.setBackgroundColor(Color.parseColor("#1F201D"))
            binding.addratingBar.setBackgroundResource(R.drawable.profile_round_corner_bg)
            binding.tvAddReview.setHintTextColor(resources.getColor(R.color.whitefordark))
            binding.tvAddReview.setTextColor(resources.getColor(R.color.whitefordark))
            binding.fullscreen.setBackgroundColor(resources.getColor(R.color.blackfordark))
            binding.reviewtv.setBackgroundResource(R.drawable.profile_round_corner_bg_dark_wish)
        }
        binding.btnAddReview.setOnClickListener {
            val rating = binding.addratingBar.rating
            Log.e("TAG", "rating: $rating")
            if (rating == 0f) {
                customToast(requireContext(), "Add the review", R.drawable.success_toast_icon)
            } else if (validateFields()) {
                val email = sharedPreferences.getEmail()
                val rating = binding.addratingBar.rating
                val review = binding.tvAddReview.text.toString().trim()

                viewmodel.AddReviewResponse(
                    email.toString(),
                    productId.toString(),
                    review,
                    rating.toString(),
                    "a",
                    "a",
                )
            }

            initObserver()
        }

        binding.tvAddReview.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                charCounts = s?.length ?: 0
                binding.tvCount.text = "${charCounts}  / 350"
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        binding.deleteImageReview.setOnClickListener {
            binding.reviewImagesLL.visibility = View.GONE
        }

        binding.btnAddReviewImage.setOnClickListener {
            openGalleryForImage()
        }
    }


    private fun openGalleryForImage() {
        galleryLauncher.launch("image/*")
    }


    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                binding.reviewImages.setImageURI(it)
                binding.reviewImagesLL.visibility = View.VISIBLE
            }
        }

    private fun initObserver() {
        viewmodel.addReviewsResponse.observe(viewLifecycleOwner) { addReviewResp ->
//            LoadingDialog.dismissProgressDialog()
            if (addReviewResp?.is_success == true) {
                Log.e("AddReviewResp", "AddReviewResp $addReviewResp")
                customToast(requireContext(), "Review Submitted", R.drawable.success_toast_icon)
                findNavController().popBackStack()
            } else {
                customToast(requireContext(), "${addReviewResp?.message}", R.drawable.ic_info)
            }
        }
    }

    private fun validateFields(): Boolean {
        val fullNameText = binding.tvAddReview.text.toString().trim()
        if (fullNameText.toString().isNullOrEmpty()) {
            customToast(requireContext(), "Enter Your Review", R.drawable.ic_info)
            return false
        }
        return true
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
}