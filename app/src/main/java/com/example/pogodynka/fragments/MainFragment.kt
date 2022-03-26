package com.example.pogodynka.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context.LOCATION_SERVICE
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.pogodynka.databinding.MainFragmentBinding
import com.example.pogodynka.other.Constants
import com.example.pogodynka.other.Permissions
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest.PRIORITY_LOW_POWER
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.util.*


class MainFragment : Fragment(), EasyPermissions.PermissionCallbacks {



    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var _binding: MainFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = MainFragmentBinding.inflate(inflater, container, false)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        requireContext().packageManager.getApplicationInfo(
            requireContext().packageName,
            PackageManager.GET_META_DATA
        )
            .apply {
                val mapsApiKey = metaData.getString("com.google.android.geo.API_KEY")
                Places.initialize(requireContext(), mapsApiKey!!)
            }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAutocompleteFragment()
        binding.fabGpsLocation.setOnClickListener {
            getGPSWeather()
        }
    }

    private fun setAutocompleteFragment(){
        val autocompleteFragment =
            binding.autocompleteFragment.getFragment<AutocompleteSupportFragment>()
        autocompleteFragment.setTypeFilter(TypeFilter.CITIES)
        autocompleteFragment.setPlaceFields(
            listOf(Place.Field.LAT_LNG)
        )
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                Log.i("LOL", " ${place.latLng.longitude},${place.latLng.latitude}")
                findNavController()
                    .navigate(MainFragmentDirections.actionMainFragmentToWeatherFragment(place.latLng.latitude, place.latLng.longitude))
            }

            override fun onError(status: Status) {
                // TODO: Handle the error.
                Log.i("LOL", "An error occurred: $status")
            }
        })
    }

    @SuppressLint("MissingPermission")
    private fun getGPSWeather(){
        if(Permissions.hasLocationPermissions(requireContext())){
            if(isLocationEnabled()){
                fusedLocationClient.getCurrentLocation(PRIORITY_LOW_POWER, CancellationTokenSource().token).addOnSuccessListener { location ->

                    Log.i("LOL", " ${location.latitude},${location.longitude}")
                    findNavController()
                        .navigate(MainFragmentDirections.actionMainFragmentToWeatherFragment(location.latitude, location.longitude))
                }
            }
            else{
                Toast.makeText(requireContext(),"Location cannot be obtained. Check that you have access to the Internet and that the location is turned on.", Toast.LENGTH_LONG).show()
            }
        }
        else {
            requestPermissions()
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager : LocationManager = requireContext().getSystemService(LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)&&locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun requestPermissions(){
        if(Permissions.hasLocationPermissions(requireContext())){
            return
        }
        else{
            EasyPermissions.requestPermissions(
                this,
                "You need to accept location permissions to use this feature",
                Constants.REQUEST_CODE_LOCATION_PERMISSIONS,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        getGPSWeather()

    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
       if(!Permissions.hasLocationPermissions(requireContext())) {
           AppSettingsDialog.Builder(this).build().show()
       }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}