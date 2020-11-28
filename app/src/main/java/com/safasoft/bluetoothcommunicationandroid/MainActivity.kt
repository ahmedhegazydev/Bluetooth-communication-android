package com.safasoft.bluetoothcommunicationandroid

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


class MainActivity : AppCompatActivity() {


    companion object {
        const val REQUEST_ENABLE_BT = 42
        const val REQUEST_DEVICE_DISCOVERABLE = 33
//        val REQUEST_QUERY_DEVICES = 142
    }

    val mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    private var listView: ListView? = null
    private val mDeviceList = ArrayList<String>()
    lateinit var arrayAdapter: ArrayAdapter<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        listView = findViewById(R.id.listView)

//        startDiscovering()
        arrayAdapter = ArrayAdapter(
            this@MainActivity,
            android.R.layout.simple_list_item_1,
            mDeviceList
        )
        listView?.adapter = arrayAdapter


        if (mBluetoothAdapter == null) {
            // Device doesn't support Bluetooth
            Toast.makeText(
                applicationContext,
                "Device doesn't support Bluetooth",
                Toast.LENGTH_SHORT
            ).show()
        } else if (!mBluetoothAdapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        }

        val permission1 = ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH)
        val permission2 =
            ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN)
//        val permission3 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
//        val permission4 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        if (permission1 != PackageManager.PERMISSION_GRANTED
            || permission2 != PackageManager.PERMISSION_GRANTED
//            || permission3 != PackageManager.PERMISSION_GRANTED
//            || permission4 != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),
                642
            )
        } else {
            Log.d("DISCOVERING-PERMISSIONS", "Permissions Granted")
        }


    }


    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
        } else if (!mBluetoothAdapter.isEnabled) {
            // Bluetooth is not enabled :)

            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, 1)

        } else {
            // Bluetooth is enabled
//            mBluetoothAdapter.disable()
//            startDiscovering()

        }


        val filter = IntentFilter(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)
        registerReceiver(receiverDiscovarable, filter)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Bluetooth enabled", Toast.LENGTH_LONG).show()
//                bluetoothEnabled()

//                val pairedDevices = mBluetoothAdapter.bondedDevices
//                val s: MutableList<String> = ArrayList()
//                for (bt in pairedDevices) s.add(bt.name)
//                setListAdapter(ArrayAdapter(this, R.layout.list, s))


//                startDiscovering()

            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "User canceled", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun startDiscovering() {
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
//        val filter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        registerReceiver(mReceiver, filter)
    }


    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mReceiver)
        unregisterReceiver(receiverDiscovarable)

    }

    private val mReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val action = intent.action
            if (action == BluetoothDevice.ACTION_FOUND) {
                val device: BluetoothDevice? =
                    intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                mDeviceList.add(device?.name ?: "null")
                arrayAdapter.notifyDataSetChanged()

            }

//            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
//                when (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)) {
//                    BluetoothAdapter.STATE_OFF -> {
//                        Log.e("TAG", "onReceive: ")
//                    }
//                    BluetoothAdapter.STATE_TURNING_OFF -> {
//                        Log.e("TAG", "onReceive: ")
//
//                    }
//                    BluetoothAdapter.STATE_ON -> {
//                        Log.e("TAG", "onReceive: ")
//                        val device = intent
//                            .getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
//                        mDeviceList.add("""${device?.name}${device?.address}""".trimIndent())
////                        Log.i("BT", """${device.name} ${device.address}""".trimIndent())
//                        listView?.adapter = ArrayAdapter(
//                            this@MainActivity,
//                            android.R.layout.simple_list_item_1, mDeviceList
//                        )
//
//                    }
//                    BluetoothAdapter.STATE_TURNING_ON -> {
//                        Log.e("TAG", "onReceive: ")
//
//                    }
//                }
//            }


        }

    }

    fun onBlueTooth(view: View) {
        if (mBluetoothAdapter?.isDiscovering == true) {
            mBluetoothAdapter.cancelDiscovery()
        }
        var filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        this.registerReceiver(receiver, filter)
        filter = IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
        this.registerReceiver(receiver, filter)
        filter = IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        this.registerReceiver(receiver, filter)
//        filter = IntentFilter(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)
//        this.registerReceiver(receiver, filter)
        mBluetoothAdapter?.startDiscovery()
    }


    private val receiverDiscovarable = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {

                BluetoothAdapter.ACTION_SCAN_MODE_CHANGED -> {
                    Log.d("DISCOVERING-FINISHED", "ACTION_SCAN_MODE_CHANGED")
                    val mode = intent.getIntExtra(
                        BluetoothAdapter.EXTRA_SCAN_MODE,
                        BluetoothAdapter.ERROR
                    )
                    when (mode) {
                        BluetoothAdapter.SCAN_MODE_CONNECTABLE -> {
                            Log.e("TAG", "onReceive: ")
                        }
                        BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE -> {
                            Log.e("TAG", "onReceive: ")

                        }
                        BluetoothAdapter.SCAN_MODE_NONE -> {
                            Log.e("TAG", "onReceive: ")

                        }
                    }

                }
            }
        }
    }


    // Create a BroadcastReceiver for ACTION_FOUND.
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    // Discovery has found a device. Get the BluetoothDevice
                    // object and its info from the Intent.
                    val device: BluetoothDevice? =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    val deviceName = device?.name
                    val deviceHardwareAddress = device?.address // MAC address
                    var msg = ""
                    if (deviceName.isNullOrBlank()) {
                        msg = deviceHardwareAddress.toString()
                    } else {
                        msg = "$deviceName $deviceHardwareAddress"
                    }
                    Log.d("DISCOVERING-DEVICE", msg)

//                    mDeviceList.add(msg)
                    mDeviceList.add(deviceName.toString())
                    arrayAdapter.notifyDataSetChanged()
                }
                BluetoothAdapter.ACTION_DISCOVERY_STARTED -> {
                    Log.d("DISCOVERING-STARTED", "isDiscovering")
                }
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    Log.d("DISCOVERING-FINISHED", "FinishedDiscovering")
                }
                BluetoothAdapter.ACTION_SCAN_MODE_CHANGED -> {
                    Log.d("DISCOVERING-FINISHED", "ACTION_SCAN_MODE_CHANGED")
                    val mode = intent.getIntExtra(
                        BluetoothAdapter.EXTRA_SCAN_MODE,
                        BluetoothAdapter.ERROR
                    )
                    when (mode) {
                        BluetoothAdapter.SCAN_MODE_CONNECTABLE -> {
                            Log.e("TAG", "onReceive: ")
                        }
                        BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE -> {
                            Log.e("TAG", "onReceive: ")

                        }
                        BluetoothAdapter.SCAN_MODE_NONE -> {
                            Log.e("TAG", "onReceive: ")

                        }
                    }

                }
            }
        }
    }

    fun enableDiscoverability(view: View) {

        val intent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)
        intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 20)///10 sec
//        startActivityForResult(intent, REQUEST_DEVICE_DISCOVERABLE)
        startActivity(intent)
    }

}