package com.example.lab12.activity

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import androidx.annotation.RequiresApi
import com.example.lab12.R
import com.example.lab12.adapter.StationTimeSearchAdapter
import com.example.lab12.data.RailPlan
import com.example.lab12.helper.MyDBHelper
import com.example.lab12.manager.DialogManager
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main2.listview
import kotlinx.android.synthetic.main.activity_main3.*
import okhttp3.*
import java.io.IOException
import java.security.SignatureException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ThsrInfoActivity : AppCompatActivity() {
    class StationInfo(val number: String, val startTime: String, val totalTime: String, val arriveTime: String, val direction: String)

    private lateinit var dbrw: SQLiteDatabase
    private var items = ArrayList<StationInfo>()
    private var itemsOrigin = ArrayList<StationInfo>()
    private lateinit var adapter: StationTimeSearchAdapter

    private val APPID = "1d75f843121143c0addc39550ba48b13"
    //申請的APPKey
    private val APPKey = "CiQyJxkYO_UZY2R-0dUGNIPqoII"
    private lateinit var station_start: String
    private lateinit var station_end: String

    private var direction = "南下"
    private var timeHour = 0
    private var timeMinute = 0

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)
        intent?.extras?.let {
            station_start = it.getString("StationStart")
            station_end = it.getString("StationEnd")
            timeHour = it.getInt("TimeHour")
            timeMinute = it.getInt("TimeMinute")
            start_station.setText(station_start)
            end_station.setText(station_end)
        }

        DialogManager.instance.showLoading(this)
        //取得資料庫實體
        dbrw = MyDBHelper(this).writableDatabase

        adapter = StationTimeSearchAdapter(this, items)
        listview.adapter = adapter
        listview.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            val bundle2 = Bundle()
            //parent[position].setBackgroundColor(Color.YELLOW)
            val i = Intent(this@ThsrInfoActivity, ShiftInfoActivity::class.java)
            bundle2.putString("Shift", items[position].number)
            bundle2.putString("StationStart", station_start)
            bundle2.putString("StationEnd", station_end)
            bundle2.putString("Direction", direction)
            i.putExtras(bundle2)
            startActivityForResult(i, 1)
        }
        //宣告 Adapter，使用 simple_list_item_1並連結 listView

//台鐵授權============================================================================================================
        val intentfilter = IntentFilter("Message3")
        registerReceiver(receiver, intentfilter)
        fun getServerTime(): String {
            val calendar = Calendar.getInstance()
            val dateFormat = SimpleDateFormat(
                "EEE, dd MMM yyyy HH:mm:ss z", Locale.US
            )
            dateFormat.timeZone = TimeZone.getTimeZone("GMT")
            return dateFormat.format(calendar.time)
        }
        //取得當下的UTC時間，Java8有提供時間格式DateTimeFormatter.RFC_1123_DATE_TIME
        val xdate = getServerTime()

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
        val currentday= dateFormat.format(calendar.time)

        val s = dbrw.rawQuery( "SELECT * FROM myTable WHERE StationName LIKE '%${station_start}%'",null)
        val e = dbrw.rawQuery( "SELECT * FROM myTable WHERE StationName LIKE '%${station_end}%'",null)

        var originStationID = ""
        var destinationStationID = ""
        s.moveToFirst()
        originStationID = s.getString(4)
        e.moveToFirst()
        destinationStationID = e.getString(4)

        val APIUrl = "http://ptx.transportdata.tw/MOTC/v2/Rail/THSR/DailyTimetable/OD/${originStationID}/to/${destinationStationID}/${currentday}?\$format=JSON"
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

        setListener()
    }

    //廣播=============================================================================================================================
    private val receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            intent.extras?.getString("json")?.let {
                val data = Gson().fromJson(it, RailPlan::class.java)
                //listview===================================================================================
                items.clear()
                itemsOrigin.clear()
                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")
                val date="0000-00-00 "
                var startTime: Date
                var endTime: Date
                var diff :Long
                var days :Long
                var hours :Long
                var minutes:Long
                var arrive_time:String
                var leave_time:String
                direction = if (data[0].DailyTrainInfo.Direction == 0) "南下" else "北上"

                for(i in 0 until data.size) {
                    arrive_time=data[i].OriginStopTime.ArrivalTime  //到站時間
                    leave_time=data[i].DestinationStopTime.DepartureTime //發車時間
                    startTime = dateFormat.parse(date + arrive_time)
                    endTime = dateFormat.parse(date + leave_time)
                    diff = endTime.time - startTime.time
                    days = diff / (1000 * 60 * 60 * 24)
                    hours = (diff - days * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60)
                    minutes = (diff - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60)) / (1000 * 60)
                    if (hours > 0.0)
                        itemsOrigin.add(StationInfo(data[i].DailyTrainInfo.TrainNo, arrive_time, "${hours}時${minutes}分", leave_time, direction))
                    else
                        itemsOrigin.add(StationInfo(data[i].DailyTrainInfo.TrainNo, arrive_time, "${minutes}分", leave_time, direction))

                    itemsOrigin.sortBy { it.startTime }
                }

                items.addAll(itemsOrigin.filter {
                    (it.startTime.split(":")[0].toInt() > timeHour) ||
                            (it.startTime.split(":")[0].toInt() == timeHour &&
                                    it.startTime.split(":")[1].toInt() >= timeMinute) })

                adapter.notifyDataSetChanged()

                DialogManager.instance.dismissAll()
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        data?.extras?.let {
            if(requestCode==1 && resultCode== Activity.RESULT_OK){
                //畫面2回傳
            }
        }
    }

    private fun setListener() {
        tv_change.setOnClickListener {
            setInfoMode()
        }

        back.setOnClickListener {
            setResult(Activity.RESULT_OK, Intent())
            finish()
        }
    }

    private fun setInfoMode() {
        if (items.size < itemsOrigin.size) {
            tv_change.text = "All"
            items.clear()
            items.addAll(itemsOrigin)
        } else {
            tv_change.text = "Now"
            items.clear()
            items.addAll(itemsOrigin.filter {
                (it.startTime.split(":")[0].toInt() > timeHour) ||
                        (it.startTime.split(":")[0].toInt() == timeHour &&
                                it.startTime.split(":")[1].toInt() >= timeMinute) })
        }
        adapter.notifyDataSetChanged()
    }
}