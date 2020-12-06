package com.example.bluetoothconnectivity.adapers

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.navigation.findNavController
import com.example.bluetoothconnectivity.R

class DeviceListAdapter(context: Context, var bluetoothList: ArrayList<BluetoothDevice>) :
    ArrayAdapter<Any>(context, -1, bluetoothList as List<Any>) {

    @SuppressLint("ViewHolder", "SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val layoutInflater = LayoutInflater.from(context)
        val view = layoutInflater.inflate(R.layout.list_device_item, parent, false)
        val deviceName = view.findViewById(R.id.textview_devicename) as TextView
        val deviceAddress = view.findViewById(R.id.textview_deviceaddress) as TextView
        val device = bluetoothList[position]

        val deviceN = device.name
        if (deviceN != null && deviceN.isNotEmpty()) {
            deviceName.text = deviceN
        } else

            deviceName.text = "unknown device"
            deviceAddress.text = device.address

        view.setOnClickListener {
          //  removeItem(device)
            view.findNavController().navigate(R.id.serviceFragment)
        }

        return view

    }

    // for addDevice
    fun addDevice(device: BluetoothDevice) {
        if (!bluetoothList.contains(device)) {
            bluetoothList.add(device)
        }
    }

    // for getDevice
    fun getDevice(position: Int): BluetoothDevice? {
        return bluetoothList[position]
    }

    // for clear listView
    fun clearData() {
        bluetoothList.clear()
    }

    // for removeItem
    fun removeItem(infoData: BluetoothDevice?) {
        val currentPosition = bluetoothList.indexOf(infoData)
        bluetoothList.removeAt(currentPosition)
        notifyDataSetChanged()
    }

}