package com.example.android.politicalpreparedness.representative

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter
import com.google.android.gms.location.*
import java.util.*


class DetailFragment : Fragment() {

    companion object {
        //TODO: Add Constant for Location request
        private const val REQUEST_LOCATION_PERMISSION = 1
        private const val  RECYCLER_INDEX_KEY = "representative_recycler_index"
        private const val  MOTIONLAYOUT_KEY = "layout_state"
        private const val RECYCLER_DATA = "representative_data"
    }

    //TODO: Declare ViewModel

    private lateinit var binding:FragmentRepresentativeBinding

    private lateinit var viewModel:RepresentativeViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation:Location


    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        //TODO: Establish bindings
        binding = FragmentRepresentativeBinding.inflate(inflater)
        //savedInstanceState?.getInt(MOTIONLAYOUT_KEY)?.let { binding.representativeLayout.transitionToState(it)  }
        val viewModelFactory = RepresentativeViewModelFactory(ElectionDatabase.getInstance(requireContext()))
        viewModel = ViewModelProvider(this,viewModelFactory)[RepresentativeViewModel::class.java]
        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(requireContext())
        binding.lifecycleOwner = this
        binding.viewModel = viewModel



        binding.buttonSearch.setOnClickListener {
            hideKeyboard()
            val addressLine1 = binding.addressLine1.text.toString()
            val addressLine2 = binding.addressLine2.text.toString()
            val city = binding.city.text.toString()
            val state = binding.state.selectedItem.toString()
            val zip = binding.zip.text.toString()
            val searchAddress = Address(addressLine1,addressLine2,city,state,zip).toFormattedString()
            viewModel.getRepresentatives(searchAddress)
        }
        //TODO: Define and assign Representative adapter

        binding.buttonLocation.setOnClickListener {
            checkLocationPermissions()

        }

        viewModel.representative.observe(viewLifecycleOwner){
            val adapter = RepresentativeListAdapter()
            adapter.submitList(it)
            binding.representativeList.adapter = adapter
            try {
                savedInstanceState?.getInt(RECYCLER_INDEX_KEY).let {
                    if (it != null) { binding.representativeList.layoutManager!!.scrollToPosition(it+it)
                    }}
            }catch (e:Exception){
            }
        }

        viewModel.error.observe(viewLifecycleOwner){
            Toast.makeText(requireContext(),it,Toast.LENGTH_LONG).show()
        }


        binding.representativeLayout.addTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionStarted(motionLayout: MotionLayout?, startId: Int, endId: Int) {}
            override fun onTransitionChange(motionLayout: MotionLayout?, startId: Int, endId: Int, progress: Float) {}
            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
                try {
                    savedInstanceState?.getInt(RECYCLER_INDEX_KEY).let {
                        if (it != null) {
                            binding.representativeList.layoutManager!!.scrollToPosition(it + it)
                        }
                    }
                } catch (e: Exception) {
                }
            }
            override fun onTransitionTrigger(
                motionLayout: MotionLayout?,
                triggerId: Int,
                positive: Boolean,
                progress: Float
            ) {
            }
        })
        savedInstanceState?.getInt(MOTIONLAYOUT_KEY)?.let {
            binding.representativeLayout.transitionToState(it)
        }


        //TODO: Populate Representative adapter

        //TODO: Establish button listeners for field and location search
    return binding.root
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        //TODO: Handle location permission result to get location on permission granted
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLocation()
            }
        }
    }

    private fun checkLocationPermissions(): Boolean {
        return if (isPermissionGranted()) {
            getLocation()
            true
        } else {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
            false
        }
    }

    private fun isPermissionGranted() : Boolean {
        //TODO: Check if permission is already granted and return (true = granted, false = denied/other)
        return ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

    }


    private fun getLocation() {
        //TODO: Get location from LocationServices
        try {

            if (isLocationEnabled()) {
                fusedLocationClient.lastLocation.addOnSuccessListener {
                    if (it == null) {
                        requestNewLocationData()
                    } else {
                        lastLocation = it
                    }
                    if(::lastLocation.isInitialized) {
                        val currentAddress = geoCodeLocation(lastLocation)
                        binding.addressLine1.setText(currentAddress.line1)
                        binding.addressLine2.setText(currentAddress.line2)
                        binding.city.setText(currentAddress.city)
                        binding.zip.setText(currentAddress.zip)
                        setSpinnerValue(currentAddress.state)
                    }else{
                        getLocation()
                    }

                }

                fusedLocationClient.lastLocation.addOnFailureListener {
                    Log.e("" , "Unable to fetch the last location.. Retry once!")
                    requestNewLocationData()
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    "Please turn ON Location Services to get the current location",
                    Toast.LENGTH_LONG
                ).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        }catch (exception:SecurityException){
        Log.e("RepresentativeFrag","Permission Issue")
    }
        //TODO: The geoCodeLocation method is a helper function to change the lat/long location to a human readable street address
    }

    private fun geoCodeLocation(location: Location): Address {
        val geocoder = Geocoder(context, Locale.getDefault())
        return geocoder.getFromLocation(location.latitude, location.longitude, 1)
                .map { address ->
                    Address(address.thoroughfare, address.subThoroughfare, address.locality, address.adminArea, address.postalCode)
                }
                .first()
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view!!.windowToken, 0)
    }

    private fun setSpinnerValue(state:String){
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.states,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.state.adapter = adapter

            val spinnerPosition = adapter.getPosition(state)
            binding.state.setSelection(spinnerPosition)

    }

    private fun requestNewLocationData(){
        try {
            val locationRequest = LocationRequest.create()
            locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

            val locationCallback: LocationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    //lastLocation = locationResult.lastLocation
                    //getLocation()

                    if (locationResult == null) {
                        return
                    }
                    for (location in locationResult.locations) {
                        // Update UI with location data
                        lastLocation = location
                        Log.d("TAG", "onLocationResult: " + lastLocation.getLatitude())
                    }
                }
            }

            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.myLooper()
            )
        }catch (exception:SecurityException){
                Log.e("RepresentativeFrag","Permission Issue")
        }

    }

   /* private val locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            //lastLocation = locationResult.lastLocation
            //getLocation()

            if (locationResult == null) {
                return
            }
            for (location in locationResult.locations) {
                // Update UI with location data
                lastLocation = location
                Log.d("TAG", "onLocationResult: " + lastLocation.getLatitude())
            }
        }
    }*/


    private fun isLocationEnabled(): Boolean {
        val locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(MOTIONLAYOUT_KEY, binding.representativeLayout.currentState)
        val index: Int = (binding.representativeList.layoutManager
                as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
        outState.putInt(RECYCLER_INDEX_KEY,index)
       val recycerData = (binding.representativeList.layoutManager as LinearLayoutManager).onSaveInstanceState()
        outState.putParcelable(RECYCLER_DATA,recycerData)
    }


    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if(savedInstanceState != null){
            (binding.representativeList.layoutManager as LinearLayoutManager).onRestoreInstanceState(savedInstanceState.getParcelable(RECYCLER_DATA))
        }
    }








}