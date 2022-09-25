package com.example.android.politicalpreparedness.representative

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.database.ElectionDatabase

//TODO: Create Factory to generate ElectionViewModel with provided election datasource
class RepresentativeViewModelFactory(private val database: ElectionDatabase): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(ElectionDatabase::class.java).newInstance(database)
    }

}