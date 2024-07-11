package com.antsglobe.restcommerse.ui.bottomSheet

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.antsglobe.restcommerse.R
import com.antsglobe.restcommerse.Utils.PreferenceManager
import com.antsglobe.restcommerse.databinding.FragmentViewCouponDialogListDialogBinding
import com.antsglobe.restcommerse.databinding.FragmentViewCouponDialogListDialogItemBinding
import com.antsglobe.restcommerse.model.Response.Coupon
import com.antsglobe.restcommerse.network.RetrofitClient
import com.antsglobe.restcommerse.viewmodel.CouponViewModel
import com.antsglobe.restcommerse.viewmodel.ViewModelFactory
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

const val ARG_ITEM_COUNT = "item_count"

class ViewCouponDialogFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentViewCouponDialogListDialogBinding? = null

    private lateinit var viewModel: CouponViewModel
    private lateinit var sharedPreferences: PreferenceManager
    var discountListener: DiscountListener? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var expired = 0
    private val currentdate = getCurrentDateTime()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewModel = ViewModelProvider(
            this, ViewModelFactory(RetrofitClient.apiService)
        )[CouponViewModel::class.java]

        sharedPreferences = PreferenceManager(requireContext())

        _binding = FragmentViewCouponDialogListDialogBinding.inflate(inflater, container, false)

        binding.llLoadingScreen.visibility = View.VISIBLE
        binding.list.visibility = View.GONE

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val currentuseremail = sharedPreferences.getEmail()!!
        if (sharedPreferences.getMode() == true) {
            binding.fullscreen.setBackgroundColor(resources.getColor(R.color.blackfordark))
            binding.emptyimg.setImageResource(R.drawable.emptycoupon_dark)
        }

        viewModel.getcouponlist(currentuseremail, "valid")

        viewModel.couponlist.observe(viewLifecycleOwner) {
            viewModel.couponlist.observe(viewLifecycleOwner) { couponList ->
                if (couponList != null) {
                    couponList.forEach {
                        val result = compareDates(it.valid_to.toString(), currentdate)
                        if (result < 0) {
                            expired += 1
                        }

                    }
                }

                binding.list.apply {
                    visibility = View.VISIBLE
                    binding.llLoadingScreen.visibility = View.GONE
                    if (couponList != null) {
                        if (couponList.isEmpty() && couponList.size >= 0 || couponList.size == expired) {
                            visibility = View.GONE
                            binding.llEmptyScreen.visibility = View.VISIBLE
                        } else {
                            visibility = View.VISIBLE
                            binding.llEmptyScreen.visibility = View.GONE
                        }
                    }
                    layoutManager = LinearLayoutManager(requireContext())
                    adapter = ItemAdapter(couponList!!)
                }
                expired = 0
            }
        }

    }

    private inner class ViewHolder internal constructor(val binding: FragmentViewCouponDialogListDialogItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @RequiresApi(Build.VERSION_CODES.O)
        fun binditem(item: Coupon) {


            if (sharedPreferences.getMode() == true) {
                binding.couponCode.setTextColor(resources.getColor(R.color.whitefordark))
                binding.from.setTextColor(resources.getColor(R.color.whitefordark))
                binding.to.setTextColor(resources.getColor(R.color.whitefordark))
                binding.comment.setTextColor(resources.getColor(R.color.whitefordark))
                binding.background.setBackgroundResource(R.drawable.profile_round_corner_bg_dark_wish)
            }
            binding.couponCode.text = item.coupon_code
            binding.couponName.text = item.coupon_name

            binding.comment.text = item.comment
            val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
            val outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val from = item.valid_from
            val to = item.valid_to
            val dateTime = LocalDateTime.parse(from, inputFormatter)
            val dateTime2 = LocalDateTime.parse(to, inputFormatter)
            val dateOnly = dateTime.format(outputFormatter)
            val dateOnly2 = dateTime2.format(outputFormatter)
            binding.from.text = "from:-" + dateOnly
            binding.to.text = "to:-" + dateOnly2


            val result = compareDates(item.valid_to.toString(), currentdate)
            if (result < 0) {
                binding.background.layoutParams.height = 0
                binding.background.visibility = View.GONE


            }
            binding.tap.setOnClickListener {
                if (result > 0) {
                    val discount = item.disc_percent
                    val couponName = item.coupon_code
                    discountListener?.onDiscountApplied(discount, couponName)
                    dismiss()
                }
            }
        }

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

    interface DiscountListener {
        fun onDiscountApplied(discount: Double, couponName: String?)
    }

    private inner class ItemAdapter internal constructor(private val list: List<Coupon>) :
        RecyclerView.Adapter<ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

            return ViewHolder(
                FragmentViewCouponDialogListDialogItemBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    ), parent, false
                )
            )

        }

        @RequiresApi(Build.VERSION_CODES.O)
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = list[position]
            holder.binditem(item)
        }

        override fun getItemCount(): Int {
            return list.size
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}