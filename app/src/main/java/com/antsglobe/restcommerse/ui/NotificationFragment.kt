package com.antsglobe.restcommerse.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.antsglobe.aeroquiz.NotificationAdapter
import com.antsglobe.restcommerse.R
import com.antsglobe.restcommerse.Utils.PreferenceManager
import com.antsglobe.restcommerse.databinding.FragmentNotificationBinding
import com.antsglobe.restcommerse.network.RetrofitClient
import com.antsglobe.restcommerse.viewmodel.NotificationViewModel
import com.antsglobe.restcommerse.viewmodel.ViewModelFactory


class NotificationFragment : Fragment() {

    private var _binding: FragmentNotificationBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: NotificationViewModel
    private lateinit var notificationAdapter: NotificationAdapter
    private lateinit var sharedPreferences: PreferenceManager


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNotificationBinding.inflate(inflater, container, false)
        sharedPreferences = PreferenceManager(requireContext())
        binding.llLoadingScreen.visibility = View.VISIBLE
        binding.notificationRecyclerView.visibility = View.GONE

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(RetrofitClient.apiService)
        )[NotificationViewModel::class.java]

        viewModel.getNotificationItem.observe(viewLifecycleOwner) { notificationResp ->
            Log.e("notificationResp", "onCreateView: $notificationResp")
            binding.notificationRecyclerView.visibility = View.VISIBLE
            binding.llLoadingScreen.visibility = View.GONE

            if (sharedPreferences.getMode() == true) {
                binding.logoImageHome.setTextColor(resources.getColor(R.color.blackfordark))
                binding.backButton.setImageResource(R.drawable.ic_baseline_arrow_back_24_dark)
                binding.notificationRecyclerView.setBackgroundColor(resources.getColor(R.color.blackfordark))
                binding.fullscreen.setBackgroundColor(resources.getColor(R.color.blackfordark))
                binding.llEmptyScreen.setBackgroundColor(resources.getColor(R.color.blackfordark))
                binding.llLoadingScreen.setBackgroundColor(resources.getColor(R.color.blackfordark))
                binding.emptyicon.setImageResource(R.drawable.emptynotifications_dark)
            }

            if (notificationResp.isEmpty() && notificationResp.size >= 0) {
                binding.notificationRecyclerView.visibility = View.GONE
                binding.llEmptyScreen.visibility = View.VISIBLE
            } else {
                binding.notificationRecyclerView.visibility = View.VISIBLE
                binding.llEmptyScreen.visibility = View.GONE
            }

            val sortedNotificationList = notificationResp.reversed()
            notificationAdapter =
                NotificationAdapter(sortedNotificationList, sharedPreferences.getMode() == true, requireContext())

            binding.notificationRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            binding.notificationRecyclerView.adapter = notificationAdapter
            sharedPreferences.setNotificationBlinkCount(notificationResp.size.toString())

            //            val itemCount: Int = notificationAdapter.getItemCount()
//            Toast.makeText(this@Notification, "Item Count: $itemCount", Toast.LENGTH_SHORT).show()

        }
        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        viewModel.getAllNotifications()


        viewModel.getNotification.observe(viewLifecycleOwner) { paper ->

            if (paper?.Total == null && paper?.read == null && paper?.unread == null) {
                //Toast.makeText(context, "access", Toast.LENGTH_SHORT).show()
//                binding.allDetail.text = "All Test | Total - 0   Solved - 0  Unsolved - 0"
            } else {
//                binding.allDetail.text = "All Test | Total - ${paper?.Total}   Solved - ${paper?.total_solved}  Unsolved - ${paper?.total_unsolved}"
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}