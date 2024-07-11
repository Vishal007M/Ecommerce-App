package com.antsglobe.restcommerse.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.recyclerview.widget.RecyclerView
import com.antsglobe.restcommerse.R
import com.antsglobe.restcommerse.Utils.PreferenceManager
import com.antsglobe.restcommerse.databinding.AddressRecyclerViewBinding
import com.antsglobe.restcommerse.model.Response.AddressList

class AddressListAdaptor(private val context: Context, var items: List<AddressList>) :
    RecyclerView.Adapter<AddressListAdaptor.MainViewHolder>() {
    private var onClickDeleteAddressListener: OnClickDeleteAddressListener? = null
    private var onClickEditAddressListener: OnClickEditAddressListener? = null
    private var onClickDefaultAddressListener: OnClickDefaultAddressListener? = null
    private lateinit var sharedPreferences: PreferenceManager
    var lastCheckedRB: RadioButton? = null
    private var selectedPosition = 0

    inner class MainViewHolder(val itemsBinding: AddressRecyclerViewBinding) :
        RecyclerView.ViewHolder(itemsBinding.root) {
        fun bindItem(list: AddressList) {
            sharedPreferences = PreferenceManager(context)


            if (sharedPreferences.getMode() == true) {
                itemsBinding.fullscreen.setBackgroundResource(R.drawable.profile_round_corner_bg_addresses_dark)
                itemsBinding.tvAddressType.setTextColor(context.resources.getColor(R.color.whitefordark))
                itemsBinding.name.setTextColor(context.resources.getColor(R.color.whitefordark))
                itemsBinding.phoneNo.setTextColor(Color.GRAY)
                itemsBinding.address.setTextColor(Color.GRAY)
                itemsBinding.ivDeleteAddress.setImageResource(R.drawable.deleteicon)
                itemsBinding.ivEditAddress.setImageResource(R.drawable.edit)
                itemsBinding.ivDeleteAddress.setBackgroundColor(context.resources.getColor(R.color.blackfordark))
                itemsBinding.addressImage.setBackgroundColor(context.resources.getColor(R.color.blackfordark))
            }

            itemsBinding.tvAddressType.text = list.address_type.toString()
            itemsBinding.name.text = "${list.customer_name} - "
            itemsBinding.phoneNo.text = list.customer_mobno

            val address =
                "${list.appartment}, ${list.landmark}, ${list.city}, ${list.state}, ${list.pin}"
            itemsBinding.address.text = address

            itemsBinding.ivDeleteAddress.setOnClickListener {
                val addressId = list.id
                val isDefault = list.is_default
                onClickDeleteAddressListener?.onDeleteAddressClick(
                    addressId.toString(),
                    isDefault!!
                )
            }

            when (list.address_type) {
                "Home" -> {
                    itemsBinding.addressImage.setImageResource(R.drawable.home)
                }

                "Office" -> {
                    itemsBinding.addressImage.setImageResource(R.drawable.address_office)
                }

                "Other" -> {
                    itemsBinding.addressImage.setImageResource(R.drawable.address_office)
                }

                else -> {
                    itemsBinding.addressImage.setImageResource(R.drawable.home)
                }
            }

            itemsBinding.ivEditAddress.setOnClickListener {
                val addressId = list.id
                val customerName = list.customer_name
                val customerPhone = list.customer_mobno
                val isDefault = list.is_default
                val addressType = list.address_type
                val address = list.address
                val appartment = list.appartment
                val landmark = list.landmark
                val city = list.city
                val state = list.state
                val pin = list.pin
                onClickEditAddressListener?.onEditAddressClick(
                    addressId.toString(),
                    customerName.toString(),
                    customerPhone.toString(),
                    addressType.toString(),
                    address.toString(),
                    isDefault!!,
                    appartment.toString(),
                    landmark.toString(),
                    city.toString(),
                    state.toString(),
                    pin.toString()
                )
            }
            if (list.is_default != null && list.is_default == true) {
                itemsBinding.rbDefault.setClickable(false)
                itemsBinding.rbDefault.setChecked(true)

                // sharedPreferences.setAddress("${list.city}, ${list.pin}")
                /* if (items.size == 1) {
                || items.size == 1 /// upside it else
                     if (list.is_default == false) {
                         if (list.id != null) {
                             val addressId = list.id
                             val address = " ${list.city}, ${list.pin}"
                             onClickDefaultAddressListener?. (
                                 addressId.toString(),
                                 address
                             )
                         }
                     }
                 }*/
            } else {
                itemsBinding.rbDefault.setClickable(true)
                itemsBinding.rbDefault.setChecked(false)
            }

            itemsBinding.rbDefault.setOnClickListener {
                if (list.id != null) {
                    val addressId = list.id
                    val address = " ${list.city}, ${list.pin}"
                    onClickDefaultAddressListener?.onDefaultAddressClick(
                        addressId.toString(),
                        address
                    )
                }
            }

            // Set checked state based on selected position
            //    itemsBinding.rbDefault.isChecked = selectedPosition == position

        }
    }

    interface OnClickDefaultAddressListener {
        fun onDefaultAddressClick(addressId: String, address: String)
    }

    fun setOnDefaultAddressListener(listener: OnClickDefaultAddressListener) {
        onClickDefaultAddressListener = listener
    }

    interface OnClickDeleteAddressListener {
        fun onDeleteAddressClick(addressId: String, isDefault: Boolean)
    }

    fun setOnDeleteAddressListener(listener: OnClickDeleteAddressListener) {
        onClickDeleteAddressListener = listener
    }

    interface OnClickEditAddressListener {
        fun onEditAddressClick(
            addressId: String, name: String, phone: String, addressType: String,
            address: String, isDefault: Boolean, appartment: String, landmark: String,
            city: String, state: String, pin: String
        )
    }

    fun setOnEditAddressListener(listener: OnClickEditAddressListener) {
        onClickEditAddressListener = listener
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val item = items[position]
        holder.bindItem(item!!)


        holder.setIsRecyclable(false) // prevent repeating items
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        return MainViewHolder(
            AddressRecyclerViewBinding.inflate
                (LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun updateStudentList(data: List<AddressList>) {
        this.items = data
        notifyDataSetChanged()
    }
}