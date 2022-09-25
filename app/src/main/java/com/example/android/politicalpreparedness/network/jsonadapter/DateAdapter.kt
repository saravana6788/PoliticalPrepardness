package com.example.android.politicalpreparedness.network.jsonadapter

import com.squareup.moshi.*
import java.text.SimpleDateFormat
import java.util.*

class DateAdapter: JsonAdapter<Date>() {


    override fun fromJson(reader: JsonReader): Date? {
        return null
    }

    override fun toJson(writer: JsonWriter, value: Date?) {
        TODO("Not yet implemented")
    }
}