package com.safasoft.bluetoothcommunicationandroid

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    val mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    private var listView: ListView? = null
    private val mDeviceList = ArrayList<String>()
    lateinit var arrayAdapter: ArrayAdapter<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        listView = findViewById(R.id.listView)


        arrayAdapter = ArrayAdapter(
            this@MainActivity,
            android.R.layout.simple_list_item_1,
            mDeviceList
        )
        listView?.adapter = arrayAdapter


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
            startDiscovering()

        }
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


                startDiscovering()


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

    }

    private val mReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val action = intent.action

            if (action == BluetoothDevice.ACTION_FOUND){
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
        mBluetoothAdapter.startDiscovery();
    }


}