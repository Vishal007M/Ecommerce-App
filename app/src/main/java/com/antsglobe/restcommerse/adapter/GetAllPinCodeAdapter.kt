package com.antsglobe.aeroquiz

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.antsglobe.restcommerse.Utils.PreferenceManager
import com.antsglobe.restcommerse.databinding.PincodeListRecycleViewBinding
import com.antsglobe.restcommerse.model.Response.GetAllPinCodeList

class GetAllPinCodeAdapter(
    var items: List<GetAllPinCodeList>,
    private val context: Context/*, val context:Context*/
) :
    RecyclerView.Adapter<GetAllPinCodeAdapter.MainViewHolder>() {

    private var pinCodeClickListener: OnClickPinCodeListener? = null


    private lateinit var sharedPreferences: PreferenceManager


    inner class MainViewHolder(val itemsBinding: PincodeListRecycleViewBinding) :
        RecyclerView.ViewHolder(itemsBinding.root) {
        fun bindItem(list: GetAllPinCodeList) {

            sharedPreferences = PreferenceManager(context)

            itemsBinding.pincode.text = list.pin_code.toString()
            itemsBinding.pincodeCity.text = list.city.toString()
            itemsBinding.pincodeState.text = list.state.toString()


            itemView.setOnClickListener {
                val id = list.ID.toString()
                val pin_code = list.pin_code.toString()
                val city = list.city.toString()
                val state = list.state.toString()
                Log.e("TAG", "pinId: $id")
                pinCodeClickListener?.onPinCodeIdClick(id, pin_code, city, state)
                sharedPreferences.setAddress("$city, $pin_code")

            }
        }
    }

    interface OnClickPinCodeListener {
        fun onPinCodeIdClick(pinId: String, pin_code: String, city: String, state: String)
    }

    fun setOnClickPincodeListener(listener: OnClickPinCodeListener) {
        pinCodeClickListener = listener
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val item = items[position]
        holder.bindItem(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        return MainViewHolder(
            PincodeListRecycleViewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )

        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun updateStudentList(data: List<GetAllPinCodeList>) {
        this.items = data
        notifyDataSetChanged()
    }


}