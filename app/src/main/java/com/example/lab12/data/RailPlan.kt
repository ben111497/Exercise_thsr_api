package com.example.lab12.data


class RailPlan : ArrayList<rail_planItem>() {}
data class rail_planItem(
    val DailyTrainInfo: DailyTrainInfo2,
    val DestinationStopTime: DestinationStopTime2,
    val OriginStopTime: OriginStopTime2,
    val TrainDate: String,
    val UpdateTime: String,
    val VersionID: Int
) {

    data class DailyTrainInfo2(
        val Direction: Int,
        val EndingStationID: String,
        val EndingStationName: EndingStationName,
        val Note: Note,
        val StartingStationID: String,
        val StartingStationName: StartingStationName,
        val TrainNo: String
    )

    data class DestinationStopTime2(
        val ArrivalTime: String,
        val DepartureTime: String,
        val StationID: String,
        val StationName: StationName,
        val StopSequence: Int
    )

    data class OriginStopTime2(
        val ArrivalTime: String,
        val DepartureTime: String,
        val StationID: String,
        val StationName: StationNameX,
        val StopSequence: Int
    )

    data class EndingStationName(
        val En: String,
        val Zh_tw: String
    )

    class Note(
    )

    data class StartingStationName(
        val En: String,
        val Zh_tw: String
    )

    data class StationName(
        val En: String,
        val Zh_tw: String
    )

    data class StationNameX(
        val En: String,
        val Zh_tw: String
    )
}