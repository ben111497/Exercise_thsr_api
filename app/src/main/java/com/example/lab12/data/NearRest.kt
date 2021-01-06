package com.example.lab12.data

data class NearRest(
    val AuthProcessTime: Int,
    val DB1: Double,
    val DB2: Double,
    val errCodes: List<Any>,
    val errMsgs: List<Any>,
    val errorInfo: List<Any>,
    val results: Results,
    val status: Int
)

data class Results(
    val content: List<Content>,
    val count: List<Any>,
    val historyAddress: HistoryAddress,
    val loveAddress: LoveAddress
)

data class Content(
    val type: Int,
    val lat: Double,
    val lng: Double,
    val name: String,
    val rating: Double,
    val vicinity:String,
    val photo: String,
    val url: String,
    val star: Int,
    val reviewsNumber: Int,
    val index: Int,
    val mode: Int
)

data class HistoryAddress(
    val address: String,
    val lat: Double,
    val lng: Double,
    val placeID: String
)

data class LoveAddress(
    val address: String,
    val lat: Double,
    val lng: Double,
    val placeID: String
)