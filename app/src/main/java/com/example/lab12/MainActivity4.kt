package com.example.lab12

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main2.*
import kotlinx.android.synthetic.main.activity_main3.*
import kotlinx.android.synthetic.main.activity_main3.back
import kotlinx.android.synthetic.main.activity_main3.listview
import kotlinx.android.synthetic.main.activity_main4.*
import kotlinx.android.synthetic.main.activity_main5.*
import okhttp3.*
import java.io.IOException
import java.security.SignatureException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity4 : AppCompatActivity() {

    class station_shift : ArrayList<shiftItem>()

    data class shiftItem(
        val EffectiveDate: String,
        val ExpiringDate: String,
        val GeneralTimetable: GeneralTimetable,
        val UpdateTime: String,
        val VersionID: Int
    )

    data class GeneralTimetable(
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
        val DepartureTime: String,
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
    private val APPID = "1d75f843121143c0addc39550ba48b13"
    //申請的APPKey
    private val APPKey = "CiQyJxkYO_UZY2R-0dUGNIPqoII"
    private var shift: String= ""
    private lateinit var station_start:String
    private lateinit var station_end:String
//==============================================
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main4)
        intent?.extras?.let {
            shift=it.getString("shift")
            station_start=it.getString("station_start")
            station_end=it.getString("station_end")
        }

    tv_start_station.text = station_start
    tv_end_station.text = station_end
//台鐵授權============================================================================================================
        val intentfilter = IntentFilter("Message4")
        registerReceiver(receiver, intentfilter)
        fun getServerTime(): String {
            val calendar = Calendar.getInstance()
            val dateFormat = SimpleDateFormat(
                "EEE, dd MMM yyyy HH:mm:ss z", Locale.US
            )
            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"))
            return dateFormat.format(calendar.time)
        }
        //取得當下的UTC時間，Java8有提供時間格式DateTimeFormatter.RFC_1123_DATE_TIME
        var xdate = getServerTime()

        val SignDate = "x-date: $xdate"

        var Signature = ""
        try {
            //取得加密簽章
            Signature = HMAC_SHA1.Signature(SignDate, APPKey)
        } catch (e1: SignatureException) {
            e1.printStackTrace()
        }
        val sAuth ="hmac username=\"$APPID\", algorithm=\"hmac-sha1\", headers=\"x-date\", signature=\"$Signature\""

        val APIUrl ="https://ptx.transportdata.tw/MOTC/v2/Rail/THSR/GeneralTimetable/TrainNo/${shift}?\$format=JSON"
        val req = Request.Builder()
            .header("Authorization", sAuth)
            .header("x-date", xdate)
            .url(APIUrl)
            .build()

        OkHttpClient().newCall(req).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                sendBroadcast(Intent("Message4").putExtra("json", response.body()?.string()))
            }
            override fun onFailure(call: Call, e: IOException?) {
                Log.e("查詢失敗", "$e")
            }
        })

        back.setOnClickListener {
            val bundle = Bundle()
            val intent = Intent().putExtras(bundle)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }
    //廣播=============================================================================================================================
    private val receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            intent.extras?.getString("json")?.let {
                val data = Gson().fromJson(it, station_shift::class.java)
                //listview===================================================================================
                //items.add("0${data[0].GeneralTimetable.StopTimes[0].StopSequence}.${data[0].GeneralTimetable.StopTimes[0].StationName}\t\t\t\t\t\t\t\t\t\t\t\t\t\t${data[0].GeneralTimetable.StopTimes[0].DepartureTime}")
                var name1= arrayOfNulls<String>(data[0].GeneralTimetable.StopTimes.size)
                var station1= arrayOfNulls<String>(data[0].GeneralTimetable.StopTimes.size)
                var time1= arrayOfNulls<String>(data[0].GeneralTimetable.StopTimes.size)
                for(i in 0 until data[0].GeneralTimetable.StopTimes.size) {
                    if(data[0].GeneralTimetable.StopTimes[i].StopSequence<10){
                        name1[i]="0${data[0].GeneralTimetable.StopTimes[i].StopSequence}"
                        station1[i]="${data[0].GeneralTimetable.StopTimes[i].StationName.Zh_tw}"
                        time1[i]="${data[0].GeneralTimetable.StopTimes[i].DepartureTime}"
                    }
                    else{
                        name1[i]="${data[0].GeneralTimetable.StopTimes[i].StopSequence}"
                        station1[i]="${data[0].GeneralTimetable.StopTimes[i].StationName.Zh_tw}"
                        time1[i]="${data[0].GeneralTimetable.StopTimes[i].DepartureTime}"
                    }
                }
                val myListAdapter = MyListAdapter_station_time(this@MainActivity4, name1, station1, time1, station_start , station_end)
                listview.adapter = myListAdapter
            }
        }

    }
}
