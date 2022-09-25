package com.example.android.politicalpreparedness.election

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.repository.CivicApiRepository
import kotlinx.coroutines.launch

class ElectionInformationViewModel(database: ElectionDatabase) : ViewModel() {
    // TODO: Implement the ViewModel
    private val civicApiRepository = CivicApiRepository(database)

    val isElectionSaved = MutableLiveData<Boolean>()


    private var doesElectionExists:Boolean = false


    private val _error = MutableLiveData<String>()
    val error:LiveData<String> = _error

    lateinit var election:Election




    fun getElectionById(election:Election){
        viewModelScope.launch {
            val savedElection = civicApiRepository.getElectionById(election.id)
            doesElectionExists = savedElection != null
            isElectionSaved.value = doesElectionExists
        }
    }


    fun followOrUnFollowElection(election: Election) {
        viewModelScope.launch {
            try {
                if (isElectionSaved.value!!) {
                    civicApiRepository.deleteElectionById(election.id)
                } else {
                    civicApiRepository.saveElection(election)
                }
                isElectionSaved.value = !isElectionSaved.value!!
            } catch (exception: Exception) {
                if (isElectionSaved.value!!) {
                    _error.value = "Unable to unfollow the election."
                } else {
                    _error.value = "Unable to follow the election."
                }
            }
        }

    }
}