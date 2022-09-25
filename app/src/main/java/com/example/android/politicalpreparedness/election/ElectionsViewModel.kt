package com.example.android.politicalpreparedness.election

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.repository.CivicApiRepository
import kotlinx.coroutines.launch

//TODO: Construct ViewModel and provide election datasource
class ElectionsViewModel(electionDatabase:ElectionDatabase): ViewModel() {

    private val civicApiRepository = CivicApiRepository(electionDatabase)
    val showProgress = MutableLiveData<Boolean>()


    // Create live data val for upcoming elections
    private val _upcomingElections = MutableLiveData<List<Election>>()
    val upcomingElections:LiveData<List<Election>> = _upcomingElections

    // Create live data val for saved elections
    var savedElections:LiveData<List<Election>> = civicApiRepository.getSavedElections()

    private val _error = MutableLiveData<String>()
    val error:LiveData<String> = _error

    //TODO: Create val and functions to populate live data for upcoming elections from the API and saved elections from local database



    //TODO: Create functions to navigate to saved or upcoming election voter info

    fun getUpcomingElections(){
        viewModelScope.launch {
            showProgress.postValue(true)
            try{
                val upcomingElectionsList =  civicApiRepository.getUpcomingElections()
                _upcomingElections.value = upcomingElectionsList
                showProgress.postValue(false)
            }catch (exception:Exception){
                showProgress.postValue(false)
                _error.value = "Unable to fetch the Upcoming elections"
            }


        }
    }

    fun  getSavedElections(){
        showProgress.postValue(true)
        try{
            savedElections = civicApiRepository.getSavedElections()
            showProgress.postValue(false)
        }catch (exception:Exception){
            showProgress.postValue(false)
            _error.value = "Unable to fetch the Saved elections"
        }
    }



}