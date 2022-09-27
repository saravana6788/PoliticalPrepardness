package com.example.android.politicalpreparedness.network.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
data class Official (
        val name: String,
        val address: @RawValue List<Address>? = null,
        val party: String? = null,
        val phones: @RawValue List<String>? = null,
        val urls: @RawValue List<String>? = null,
        val photoUrl: String? = null,
        val channels: @RawValue List<Channel>? = null
): Parcelable