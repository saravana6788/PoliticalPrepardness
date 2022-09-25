package com.example.android.politicalpreparedness.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.android.politicalpreparedness.network.models.Election

@Dao
@TypeConverters(Converters::class)
interface ElectionDao {

    // Add insert query
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertElection(election:Election)

    //TODO: Add select all election query
    @Query("Select * from election_table")
    fun getSavedElections():LiveData<List<Election>>

    //TODO: Add select single election query
    @Query("Select * from election_table where id = :id")
    fun getElectionById(id:Int): Election

    //TODO: Add delete query
    @Query("Delete from election_table where id = :id")
    fun deleteElectionById(id:Int)

    //TODO: Add clear query
    @Query("Delete from election_table")
    fun clearElections()

}