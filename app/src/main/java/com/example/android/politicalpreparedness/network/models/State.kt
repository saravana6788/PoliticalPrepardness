package com.example.android.politicalpreparedness.network.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class State (
    val name: String,
    val electionAdministrationBody: AdministrationBody
):Parcelable