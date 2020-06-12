package com.example.lab12

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.core.view.get
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main2.*
import kotlinx.android.synthetic.main.activity_main2.listview
import kotlinx.android.synthetic.main.activity_main3.*
import kotlinx.android.synthetic.main.activity_main_homepage.*
import okhttp3.*
import java.io.IOException
import java.security.SignatureException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivity3 : AppCompatActivity() {
    private lateinit var dbrw : SQLiteDatabase
    private var items : ArrayList<String> = ArrayList(0)
    private lateinit var adapter : ArrayAdapter<String>

    val APPID = "1d75f843121143c0addc39550ba48b13"
    //申請的APPKey
    val APPKey = "CiQyJxkYO_UZY2R-0dUGNIPqoII"
    lateinit var station_start:String
    lateinit var station_end:String

    class rail_plan : ArrayList<rail_planItem>()
    data class rail_planItem(
        val DailyTrainInfo: DailyTrainInfo,
        val DestinationStopTime: DestinationStopTime,
        val OriginStopTime: OriginStopTime,
        val TrainDate: String,
        val UpdateTime: String,
        val VersionID: Int
    )

    data class DailyTrainInfo(
        val Direction: Int,
        val EndingStationID: String,
        val EndingStationName: EndingStationName,
        val Note: Note,
        val StartingStationID: String,
        val StartingStationName: StartingStationName,
        val TrainNo: String
    )

    data class DestinationStopTime(
        val ArrivalTime: String,
        val DepartureTime: String,
        val StationID: String,
        val StationName: StationName,
        val StopSequence: Int
    )

    data class OriginStopTime(
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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)
        intent?.extras?.let {
            station_start=it.getString("station_start")
            station_end=it.getString("station_end")
            start_station.setText(station_start)
            end_station.setText(station_end)
        }
        dbrw=MyDBHelper(this).writableDatabase
        //取得資料庫實體
        adapter = ArrayAdapter(this,android.R.layout.simple_list_item_1 , items)
        listview.adapter = adapter
        //宣告 Adapter，使用 simple_list_item_1並連結 listView

//台鐵授權============================================================================================================
        val intentfilter = IntentFilter("Message3")
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

        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat(
            "yyyy-MM-dd", Locale.US
        )
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"))
        var currentday= dateFormat.format(calendar.time)

        val s = dbrw.rawQuery( "SELECT * FROM myTable WHERE StationName LIKE '%${station_start}%'",null)
        val e = dbrw.rawQuery( "SELECT * FROM myTable WHERE StationName LIKE '%${station_end}%'",null)
        // items.clear()
        var OriginStationID:String
        var DestinationStationID:String
        s.moveToFirst()
        OriginStationID=s.getString(4)
        e.moveToFirst()
        DestinationStationID=e.getString(4)

        val APIUrl = "http://ptx.transportdata.tw/MOTC/v2/Rail/THSR/DailyTimetable/OD/${OriginStationID}/to/${DestinationStationID}/${currentday}?\$format=JSON"
        val req = Request.Builder()
            .header("Authorization", sAuth)
            .header("x-date", xdate)
            // .header("Accept-Encoding","gzip")
            .url(APIUrl)
            .build()

        OkHttpClient().newCall(req).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                sendBroadcast(
                    Intent("Message3")
                        .putExtra("json", response.body()?.string())
                )
                //val data = Gson().fromJson(response.body()?.string(), rail::class.java)
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
                val data = Gson().fromJson(it, rail_plan::class.java)
                //listview===================================================================================
                items.clear()
                items.add("車次\t\t\t\t\t\t\t發車時間\t\t\t\t\t車程\t\t\t\t\t\t\t到站時間")
                var dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
                var date="0000-00-00 "
                var startTime: Date
                var endTime: Date
                var diff :Long
                var days :Long
                var hours :Long
                var minutes:Long
                var arrive_time:String
                var leave_time:String
                var direction:String
                if(data[0].DailyTrainInfo.Direction==0)direction="南下"
                else direction="北上"
                for(i in 0 until data.size) {
                    arrive_time=data[i].OriginStopTime.ArrivalTime  //到站時間
                    leave_time=data[i].DestinationStopTime.DepartureTime //發車時間
                    startTime = dateFormat.parse(date + arrive_time)
                    endTime = dateFormat.parse(date + leave_time)
                    diff = endTime.time - startTime.time
                    days = diff / (1000 * 60 * 60 * 24)
                    hours = (diff - days * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60)
                    minutes = (diff - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60)) / (1000 * 60)
                    if(hours>0.0) {
                        items.add("${data[i].DailyTrainInfo.TrainNo}\t\t\t\t\t\t\t${arrive_time}\t\t\t\t\t\t\t${hours}小時${minutes}分鐘\t\t\t${leave_time}")
                    }
                    else {
                        items.add("${data[i].DailyTrainInfo.TrainNo}\t\t\t\t\t\t\t${arrive_time}\t\t\t\t\t\t\t${minutes}分鐘\t\t\t\t\t\t\t${leave_time}")
                    }
                }
                adapter.notifyDataSetChanged()

               // listview[0].setBackgroundColor(Color.YELLOW)
                listview.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->
                    val bundle2 = Bundle()
                    //parent[position].setBackgroundColor(Color.YELLOW)
                    val i = Intent(this@MainActivity3, MainActivity4::class.java)
                    bundle2.putString("shift", data[position].DailyTrainInfo.TrainNo)
                    bundle2.putString("station_start",station_start)
                    bundle2.putString("station_end",station_end)
                    i.putExtras(bundle2)
                   startActivityForResult(i, 1)
                })
            }
        }

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        data?.extras?.let {
            if(requestCode==1 && resultCode== Activity.RESULT_OK){//畫面2回傳
            }

        }
    }
}