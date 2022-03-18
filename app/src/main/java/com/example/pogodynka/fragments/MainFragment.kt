package com.example.pogodynka.fragments

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.pogodynka.data.viewmodels.MainFragmentViewModel
import com.example.pogodynka.databinding.MainFragmentBinding
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener


class MainFragment : Fragment() {

    private val viewModel : MainFragmentViewModel by viewModels()

    private var _binding: MainFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = MainFragmentBinding.inflate(inflater, container, false)
        requireContext().packageManager.getApplicationInfo(requireContext().packageName, PackageManager.GET_META_DATA)
            .apply {
                val mapsApiKey = metaData.getString("com.google.android.geo.API_KEY")
                Places.initialize(requireContext(), mapsApiKey!!)
            }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val autocompleteFragment = binding.autocompleteFragment.getFragment<AutocompleteSupportFragment>()
        autocompleteFragment.setTypeFilter(TypeFilter.CITIES)
        autocompleteFragment.setPlaceFields(listOf(Place.Field.ADDRESS, Place.Field.LAT_LNG))
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                // TODO: Get info about the selected place.
                Log.i("LOL", "Place: ${place.latLng},${place.address}")
                viewModel.getWeather()
                viewModel.weatherResponse.observe(viewLifecycleOwner,{
                    Log.i("LOL", "An error occurred: ${it.body().toString()}")
                })

            }

            override fun onError(status: Status) {
                // TODO: Handle the error.
                Log.i("LOL", "An error occurred: $status")
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}