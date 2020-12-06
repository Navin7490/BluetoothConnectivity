package com.example.bluetoothconnectivity.fragment

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.bluetoothconnectivity.R
import com.example.bluetoothconnectivity.adapers.DeviceListAdapter
import com.example.bluetoothconnectivity.databinding.FragmentHomeBinding
import com.example.bluetoothconnectivity.services.BluetoothService

class HomeFragment : Fragment(),SwipeRefreshLayout.OnRefreshListener {

    // Initialise variable
    private lateinit var listView:ListView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    var bluetoothDeviceList: ArrayList<BluetoothDevice> =ArrayList()
    var deviceListAdapter:DeviceListAdapter?=null
    var bluetoothAdapter:BluetoothAdapter?=null
    var bluetoothService:BluetoothService?=null
    var device:BluetoothDevice?=null
    var deviceName:String?=null
    var deviceaddress:String?=null
    var scanning:Boolean=false
    var scanTime:Long=10000
    var handler=Handler()
    var requestCode=7490

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("HomeFragment","onCreate")


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.e("HomeFragment","onCreateView")

        // Inflate the layout for this fragment
        val viewBinding:FragmentHomeBinding = FragmentHomeBinding.inflate(inflater,container,false)
        val view=viewBinding.root

        // Assign variable
        listView=view.findViewById(R.id.listView_deviceList)
        swipeRefreshLayout=view.findViewById(R.id.SwipeRefresh_Layout)

        swipeRefreshLayout.setOnRefreshListener(this)

        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!requireActivity().packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
           Toast.makeText(requireContext(),"BLE Not Supported",Toast.LENGTH_SHORT).show()
            requireActivity().finish()
        }

        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        val bluetoothManager=requireActivity().getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter=bluetoothManager.adapter

        // Checks if Bluetooth is supported on the device.
        if(bluetoothAdapter==null) {
            Toast.makeText(requireContext(),"Bluetooth Not Supported",Toast.LENGTH_SHORT).show()
            requireActivity().finish()

        }

        return view
    }

    override fun onResume() {
        super.onResume()
        Log.e("HomeFragment","onResume")

        grantedPermission()
        checkLocationIsEnabledOrNot()
        // fire an intent to display a dialog asking the user to grant permission to enable it.
        if(!bluetoothAdapter!!.isEnabled) {

            var enabledIntent=Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enabledIntent,requestCode)


        } else {
            deviceListAdapter= DeviceListAdapter(requireContext(),bluetoothDeviceList)
            listView.adapter=deviceListAdapter
            deviceListAdapter!!.notifyDataSetChanged()
            scanLeDevice(true)
        }
    }

    private fun scanLeDevice(enable:Boolean) {

        if(enable) {
            handler.postDelayed({
                scanning=false
                bluetoothAdapter!!.stopLeScan(LeScanCallBack)
            }, scanTime)
            scanning=true
            bluetoothAdapter!!.startLeScan(LeScanCallBack)

        } else {
            scanning=false
            bluetoothAdapter!!.stopLeScan(LeScanCallBack)

        }
    }

    // LeScanCallBack
    private val LeScanCallBack = BluetoothAdapter.LeScanCallback { device, rssi, scanRecord ->

        deviceListAdapter!!.addDevice(device)
        deviceListAdapter!!.notifyDataSetChanged()

    }

    // permission
    private fun grantedPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireContext() as Activity, arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ), 100
            )
        }
    }


    // check location enabled or not

    private fun checkLocationIsEnabledOrNot() {


        val lm =
           requireActivity(). getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var gpsEnabled = false
        var networkEnabled = false
        try {

            gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            networkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (e: Exception) {
            e.printStackTrace()
        }


        if (!gpsEnabled && !networkEnabled) {
            AlertDialog.Builder(requireContext())
                .setTitle("Enable GPS Service")
                .setCancelable(false)
                .setPositiveButton("Enable",
                    DialogInterface.OnClickListener { dialog, which -> // this intent redirect us to the location setting , if GPS is disabled this dialog will be show
                        startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                    })
                .show()
        }
    }

    // Swipe Refresh Data
    override fun onRefresh() {
        swipeRefreshLayout.isRefreshing=true
        handler.postDelayed({
            swipeRefreshLayout.isRefreshing=false
        },scanTime)

        if (!bluetoothAdapter!!.isEnabled) {
            val enabledIntent=Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enabledIntent,requestCode)
            deviceListAdapter!!.clearData()
            deviceListAdapter!!.notifyDataSetChanged()
            checkLocationIsEnabledOrNot()
        } else if (listView!=null) {
            deviceListAdapter!!.clearData()
            deviceListAdapter!!.notifyDataSetChanged()
            checkLocationIsEnabledOrNot()
            scanLeDevice(true)

        }

    }

    private fun removeItemList(infoData:BluetoothDevice) {
        deviceListAdapter!!.removeItem(infoData)
    }

}