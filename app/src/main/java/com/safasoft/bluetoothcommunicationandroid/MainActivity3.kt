package com.safasoft.bluetoothcommunicationandroid

import android.Manifest
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions


class MainActivity3 : AppCompatActivity(), EasyPermissions.PermissionCallbacks {

    companion object {
        const val RC_CAMERA_AND_LOCATION = 42
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        methodRequiresTwoPermission()


    }


    @AfterPermissionGranted(RC_CAMERA_AND_LOCATION)
    private fun methodRequiresTwoPermission() {
        val perms =
            arrayOf<String>(
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.ACCESS_FINE_LOCATION,

                )
        if (EasyPermissions.hasPermissions(this, *perms)) {
            // Already have permission, do the thing

        } else {
            EasyPermissions.requestPermissions(
                this@MainActivity3,
                getString(R.string.camera_and_location_rationale),
                RC_CAMERA_AND_LOCATION,
                *perms
            )
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)

    }


    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {

    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {

    }


}