package com.example.android.politicalpreparedness.representative

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.repository.CivicApiRepository
import com.example.android.politicalpreparedness.representative.model.Representative
import kotlinx.coroutines.launch

class RepresentativeViewModel(val database:ElectionDatabase): ViewModel() {
    private val civicApiRepository = CivicApiRepository(database)
    //TODO: Establish live data for representatives and address
    private val _representatives = MutableLiveData<List<Representative>>()
    val representative:LiveData<List<Representative>> = _representatives

    val showProgress = MutableLiveData<Boolean>()

    private val _error = MutableLiveData<String>()
    val error:LiveData<String> =  _error

    //TODO: Create function to fetch representatives from API from a provided address

    /**
     *  The following code will prove helpful in constructing a representative from the API. This code combines the two nodes of the RepresentativeResponse into a single official :

    val (offices, officials) = getRepresentativesDeferred.await()
    _representatives.value = offices.flatMap { office -> office.getRepresentatives(officials) }

    Note: getRepresentatives in the above code represents the method used to fetch data from the API
    Note: _representatives in the above code represents the established mutable live data housing representatives

     */


    //TODO: Create function get address from geo location

    //TODO: Create function to get address from individual fields


    fun getRepresentatives(searchAddress:String){
        showProgress.value = true
        viewModelScope.launch {
            try {
                val (offices, officials) = civicApiRepository.getRepresentatives(searchAddress)
                    .await()
                _representatives.value =
                    offices.flatMap { office -> office.getRepresentatives(officials) }

                showProgress.value = false

            }catch (exception:Exception){
                showProgress.value = false
                _error.value = "Oops! Unable to fetch the representatives. Please try later"
            }
        }

    }



    fun getRepresentativeFromSaveInstanceState(repList:List<Representative>){
        if(repList!=null){
            _representatives.value = repList
        }
    }
}
