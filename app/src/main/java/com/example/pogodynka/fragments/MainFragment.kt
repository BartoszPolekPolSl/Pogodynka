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
import androidx.appcompat.widget.ViewUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pogodynka.adapters.FavoriteWeatherListAdapter
import com.example.pogodynka.data.model.WeatherApiResponse
import com.example.pogodynka.data.viewmodels.WeatherViewModel
import com.example.pogodynka.data.viewmodels.WeatherViewModelFactory
import com.example.pogodynka.databinding.MainFragmentBinding
import com.example.pogodynka.other.Constants
import com.example.pogodynka.util.Permissions
import com.example.pogodynka.util.WeatherApiResponseHelper
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.util.*
import com.example.pogodynka.util.toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Response


class MainFragment : Fragment(), EasyPermissions.PermissionCallbacks, KodeinAware {

    override val kodein by closestKodein()
    private val factory: WeatherViewModelFactory by instance()
    private val viewModel: WeatherViewModel by activityViewModels { factory }
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
        val adapter = FavoriteWeatherListAdapter(
            viewModel.favoriteWeatherList,
            { navigateToWeatherFragment(it) },
            { viewModel.removeFavoriteWeather(it) })
        binding.recyclerviewFavorite.layoutManager = LinearLayoutManager(this.context)
        binding.recyclerviewFavorite.adapter = adapter
        viewModel.favoriteWeatherList.observe(
            viewLifecycleOwner
        ) { adapter.notifyDataSetChanged() }
        viewModel.weatherResponse.observe(viewLifecycleOwner) {
            if (it != null) {
                navigateToWeatherFragment(it.body()!!)
            }
        }
        viewModel.failureMessage.observe(
            viewLifecycleOwner
        ) { if (it != null) toast(it) }
    }


    private fun setAutocompleteFragment() {
        val autocompleteFragment =
            binding.autocompleteFragment.getFragment<AutocompleteSupportFragment>()
        autocompleteFragment.setTypeFilter(TypeFilter.CITIES)
        autocompleteFragment.setPlaceFields(
            listOf(Place.Field.LAT_LNG)
        )
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                viewModel.getWeather(place.latLng.latitude, place.latLng.longitude)
            }

            override fun onError(status: Status) {
                // TODO: Handle the error.
                Log.i("LOL", "An error occurred: $status")
            }
        })
    }

    private fun navigateToWeatherFragment(weatherApiResponse: WeatherApiResponse) {
        runBlocking {
            if (viewModel.favoriteWeatherList.value.isNullOrEmpty()) {
                viewModel.loadFavoriteWeather().await()
            }
        }
        val locationName = WeatherApiResponseHelper.getLocationName(
            weatherApiResponse.coord.lat,
            weatherApiResponse.coord.lon,
            requireContext()
        )
        weatherApiResponse.coord.name = locationName
        findNavController()
            .navigate(
                MainFragmentDirections.actionMainFragmentToWeatherFragment(weatherApiResponse)
            )
    }

    @SuppressLint("MissingPermission")
    private fun getGPSWeather() {
        if (Permissions.hasLocationPermissions(requireContext())) {
            if (isLocationEnabled()) {
                //TODO check interent
                fusedLocationClient.getCurrentLocation(
                    PRIORITY_HIGH_ACCURACY,
                    CancellationTokenSource().token
                ).addOnSuccessListener { location ->
                    viewModel.getWeather(location.latitude, location.longitude)
                }
            } else {
                toast("Location cannot be obtained. Check that the location is turned on.")
            }
        } else {
            requestPermissions()
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            requireContext().getSystemService(LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun requestPermissions() {
        if (Permissions.hasLocationPermissions(requireContext())) {
            return
        } else {
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
        if (!Permissions.hasLocationPermissions(requireContext())) {
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
        viewModel.clearWeatherResponse()
        _binding = null
    }
}