package com.example.android.politicalpreparedness.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.models.Election
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CivicApiRepository(private val database:ElectionDatabase) {
    private val civicsApiService = CivicsApi.retrofitService

    suspend fun getUpcomingElections() = withContext(Dispatchers.IO){
        civicsApiService.getElections().elections

    }

     fun getSavedElections():LiveData<List<Election>> =
        database.electionDao.getSavedElections()



    suspend fun saveElection(election:Election) = withContext(Dispatchers.IO){
        database.electionDao.insertElection(election)
    }

    fun getRepresentatives(searchAddress:String) =
        civicsApiService.getRepresentativesDeferred(searchAddress)

    suspend fun getElectionById(id:Int):Election? =withContext(Dispatchers.IO) {
        database.electionDao.getElectionById(id)
    }

    suspend fun deleteElectionById(id:Int) = withContext(Dispatchers.IO) {
        database.electionDao.deleteElectionById(id)
    }
}