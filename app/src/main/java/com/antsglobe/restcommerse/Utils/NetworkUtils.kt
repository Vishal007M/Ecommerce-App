package com.antsglobe.aeroquiz.Utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.antsglobe.restcommerse.R


object NetworkUtils {
    private var appContext: Context? = null

    fun initialize(context: Context) {
        appContext = context.applicationContext
    }

    fun isNetworkConnected(): Boolean {
        val connectivityManager =
            appContext?.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities = connectivityManager?.activeNetwork
            val actNw = connectivityManager?.getNetworkCapabilities(networkCapabilities)
            actNw?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true ||
                    actNw?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true
        } else {
            val activeNetworkInfo = connectivityManager?.activeNetworkInfo
            activeNetworkInfo?.isConnected == true
        }
    }

    fun showToast() {
        appContext?.let {
            customToast(it, "Please, connect to the internet!", R.drawable.ic_info)
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
}
