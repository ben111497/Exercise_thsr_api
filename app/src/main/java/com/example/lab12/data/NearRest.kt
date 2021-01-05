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
    val index: Int,
    val lat: Double,
    val lng: Double,
    val name: String,
    val `open`: List<String>,
    val periods: List<Period>,
    val phone: String,
    val photo: String,
    val placeID: String,
    val priceLevel: Int,
    val rating: Double,
    val reviews: List<Review>,
    val reviewsNumber: Int,
    val type: Int,
    val vicinity: String
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

data class Period(
    val close: Close,
    val `open`: Open
)

data class Review(
    val name: String,
    val photo: String,
    val rating: Int,
    val text: String,
    val time: Int
)

data class Close(
    val day: Int,
    val time: String
)

data class Open(
    val day: Int,
    val time: String
)