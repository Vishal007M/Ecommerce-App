package com.antsglobe.aeroquiz

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.antsglobe.restcommerse.R
import com.antsglobe.restcommerse.Utils.PreferenceManager
import com.antsglobe.restcommerse.databinding.NotificationRecyclerViewBinding
import com.antsglobe.restcommerse.model.Response.NotificationList
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

class NotificationAdapter(
    val items: List<NotificationList>,
    val dark: Boolean,
    private val context: Context
) :
    RecyclerView.Adapter<NotificationAdapter.MainViewHolder>() {

    private lateinit var sharedPreferences: PreferenceManager

    inner class MainViewHolder(val itemsBinding: NotificationRecyclerViewBinding) :
        RecyclerView.ViewHolder(itemsBinding.root) {

        fun bindItem(list: NotificationList) {
            sharedPreferences = PreferenceManager(context)

//            val profilePicName = sharedPreferences.getProfilePic()
//
//            if (profilePicName != null) {
//
//                val drawableMap = mapOf(
//                    "boy1" to R.drawable.boy1,
//                    "boy2" to R.drawable.boy2,
//                    "boy3" to R.drawable.boy3,
//                    "boy4" to R.drawable.boy4,
//                    "girl1" to R.drawable.girl1,
//                    "girl2" to R.drawable.girl2,
//                    "girl3" to R.drawable.girl3,
//                    "girl4" to R.drawable.girl4,
//                    "girl5" to R.drawable.girl5,
//                ) n
//                val drawableName = profilePicName
//
//                val imageView = itemsBinding.profileImage
//
//                imageView.setImageResource(drawableMap[drawableName] ?: 0)
//
//            } else {
//                itemsBinding.profileImage.setImageResource(R.drawable.boy1)
//            }

            itemsBinding.notificationHeading.text = list.title

            try {
                val f: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")

                val d = f.parse(list.create_date)
                val date: DateFormat = SimpleDateFormat("dd-MMM-yy")
                val time: DateFormat = SimpleDateFormat("hh:mm a")
                val dayOfWeek: DateFormat = SimpleDateFormat("EEE", Locale.getDefault())

                val formattedTime = time.format(d).uppercase(Locale.ROOT)
                val formattedDayOfWeek = dayOfWeek.format(d)

                itemsBinding.notificationTime.text =
                    "$formattedDayOfWeek ${date.format(d)}"

            } catch (e: ParseException) {
                e.printStackTrace()
            }

            if (dark) {
                itemsBinding.fullscreen.setBackgroundColor(Color.parseColor("#1F201D"))
                itemsBinding.NotificationText.setTextColor(Color.GRAY)
                itemsBinding.notificationHeading.setTextColor(Color.WHITE)
            }

            itemsBinding.NotificationText.text = list.description

        }
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val item = items[position]
        holder.bindItem(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        return MainViewHolder(
            NotificationRecyclerViewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }


}