package com.safasoft.chat

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.safasoft.bluetoothcommunicationandroid.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main2.*
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*
import java.util.UUID.fromString
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

    private val mDeviceList: ArrayList<String> = ArrayList()
    val mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    val UUID: UUID = fromString("5cc1e271-664e-4679-a6c9-fd31692e3499")
    var btDevices: Set<BluetoothDevice> = setOf()

    //    var btDevices: MutableList<BluetoothDevice> = mutableListOf()
    lateinit var sendReceive: SendReceive

    inner class ServiceClass : Thread() {
        var bluetoothServerSocket: BluetoothServerSocket? = null

        init {
//            bluetoothServerSocket = BluetoothAdapter.getDefaultAdapter().listenUsingInsecureRfcommWithServiceRecord()
            bluetoothServerSocket = mBluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(
                APP_NAME, UUID
            )
        }


        override fun run() {
            super.run()
            var socket: BluetoothSocket? = null
            while (socket == null) {
                try {
                    val message = Message.obtain()
                    message.what = STATE_CONNECTING
                    handler.sendMessage(message)
                    socket = bluetoothServerSocket?.accept()
                } catch (ex: Exception) {
                    ex.printStackTrace()
                    val message = Message.obtain()
                    message.what = STATE_CONNECTION_FAILED
                    handler.sendMessage(message)
                }

                if (socket != null) {
                    val message = Message.obtain()
                    message.what = STATE_CONNECTED
                    handler.sendMessage(message)

                    ///write some code for sending/receiving mesages

                    sendReceive = SendReceive(socket)
                    sendReceive.start()



                    break
                }

            }

        }


    }

    inner class SendReceive(private val bluetoothSocket: BluetoothSocket) : Thread() {
        var outputStream: OutputStream? = null
        var inputStream: InputStream? = null

        init {
            var outputStreamTemp: OutputStream? = null
            var inputStreamTemp: InputStream? = null

            try {
                outputStreamTemp = bluetoothSocket.outputStream
                inputStreamTemp = bluetoothSocket.inputStream
            } catch (e: IOException) {
                e.printStackTrace()
            }

            outputStream = outputStreamTemp
            inputStream = inputStreamTemp


        }


        override fun run() {
            super.run()

            val buffer = byteArrayOfInts(1024)
            var bytes = 0


            while (true) {
                try {
                    bytes = inputStream!!.read(buffer)
                    handler.obtainMessage(STATE_MESSAGE_RECEIVED, bytes, -1, buffer)


                } catch (e: IOException) {
                    e.printStackTrace()

                }
            }


        }

        fun byteArrayOfInts(vararg ints: Int) = ByteArray(ints.size) { pos -> ints[pos].toByte() }

        fun write(arrayByte: ByteArray) {
            try {
                outputStream?.write(arrayByte)
            } catch (e: IOException) {
                e.printStackTrace()

            }
        }
    }


    inner class ClientClass(val bluetoothDevice: BluetoothDevice) : Thread() {
        var bluetoothSocket: BluetoothSocket? = null

        init {
            try {
                bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(UUID)

            } catch (e: Exception) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }


        override fun run() {
            super.run()

            try {
                bluetoothSocket?.connect()
                val message = Message.obtain()
                message.what = STATE_CONNECTED
                handler.sendMessage(message)

                sendReceive = bluetoothSocket?.let { SendReceive(it) }!!
                sendReceive.start()


            } catch (e: Exception) {
                e.printStackTrace()
                val message = Message.obtain()
                message.what = STATE_CONNECTION_FAILED
                handler.sendMessage(message)
            }

        }


    }


    companion object {
        const val REQUEST_ENABLE_BT = 42

        const val STATE_LISTENING = 1
        const val STATE_CONNECTING = 2
        const val STATE_CONNECTED = 3
        const val STATE_CONNECTION_FAILED = 5
        const val STATE_MESSAGE_RECEIVED = 6

        const val APP_NAME = "HEGZO_CHAT"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)



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


    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        listView.setOnItemClickListener { parent, view, position, id ->
            val bluetoothDevice: List<BluetoothDevice> = ArrayList(btDevices)
            val clienClass = ClientClass(bluetoothDevice = bluetoothDevice[position])
            clienClass.start()

            tv_status.text = "Connecting"


        }
    }

    fun listDevices(view: View) {
        mDeviceList.clear()
        btDevices = mBluetoothAdapter.bondedDevices

        if (btDevices.isNotEmpty()) {
            for (currentDevice in btDevices) {
//                log.i("Device Name " + currentDevice.name)
                mDeviceList.add(
                    """
         Device Name: ${currentDevice.name}
         Device Address: ${currentDevice.address}
         """.trimIndent()
                )
                findViewById<ListView>(R.id.listView).adapter = ArrayAdapter<Any?>(
                    application,
                    android.R.layout.simple_list_item_1, mDeviceList.toList()
                )
            }
        }

    }

    fun listen(view: View) {
        val serviceClass = ServiceClass()
        serviceClass.start()

    }

    fun sendMessage(view: View) {

        val message = etMessage.text.toString()
        sendReceive.write(message.toByteArray())


    }

    @SuppressLint("HandlerLeak")
    var handler: Handler = object : Handler(Looper.myLooper()!!) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                STATE_CONNECTED -> {
                    tv_status.text = "STATE_CONNECTED"
                }
                STATE_CONNECTING -> {
                    tv_status.text = "STATE_CONNECTING"

                }
                STATE_CONNECTION_FAILED -> {
                    tv_status.text = "STATE_CONNECTION_FAILED"

                }
                STATE_MESSAGE_RECEIVED -> {
                    tv_status.text = "STATE_MESSAGE_RECEIVED"


                    val readBuffer = msg.obj as ByteArray
                    val tempMessage = String(readBuffer, 0, msg.arg1)
                    tvMessage.text = tempMessage


                }
                STATE_LISTENING -> {
                    tv_status.text = "STATE_LISTENING"

                }
            }
        }
    }

}