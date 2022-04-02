package com.example.pogodynka.util

import android.Manifest
import android.content.Context
import pub.devrel.easypermissions.EasyPermissions

object Permissions {

    fun hasLocationPermissions(context : Context): Boolean {
        return EasyPermissions.hasPermissions(context, Manifest.permission.ACCESS_COARSE_LOCATION)||EasyPermissions.hasPermissions(context, Manifest.permission.ACCESS_FINE_LOCATION)
    }

}