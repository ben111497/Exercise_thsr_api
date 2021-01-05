package com.example.lab12.data

class TrainNumberInfo: ArrayList<shiftItem>()

data class shiftItem(
    val EffectiveDate: String,
    val ExpiringDate: String,
    val GeneralTimetable: GeneralTimetable2,
    val UpdateTime: String,
    val VersionID: Int
) {
    data class GeneralTimetable2(
        val GeneralTrainInfo: GeneralTrainInfo,
        val ServiceDay: ServiceDay,
        val SrcUpdateTime: String,
        val StopTimes: List<StopTime>
    )

    data class GeneralTrainInfo(
        val Direction: Int,
        val EndingStationID: String,
        val EndingStationName: EndingStationName,
        val Note: Note,
        val StartingStationID: String,
        val StartingStationName: StartingStationName,
        val TrainNo: String
    )

    data class ServiceDay(
        val Friday: Int,
        val Monday: Int,
        val Saturday: Int,
        val Sunday: Int,
        val Thursday: Int,
        val Tuesday: Int,
        val Wednesday: Int
    )

    data class StopTime(
        val DepartureTime: String?,
        val ArrivalTime: String?,
        val StationID: String,
        val StationName: StationName,
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
}
