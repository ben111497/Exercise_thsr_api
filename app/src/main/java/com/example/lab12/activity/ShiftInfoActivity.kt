package com.example.lab12.activity

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.lab12.R
import com.example.lab12.adapter.StationTimeAdapter
import com.example.lab12.data.TrainNumberInfo
import com.example.lab12.manager.DialogManager
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main3.back
import kotlinx.android.synthetic.main.activity_main3.listview
import kotlinx.android.synthetic.main.activity_main4.*
import okhttp3.*
import java.io.IOException
import java.security.SignatureException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ShiftInfoActivity : AppCompatActivity() {
    class TrainInfo(val name: String, val startTime: String, val arriveTime: String)

    private val APPID = "1d75f843121143c0addc39550ba48b13"
    //申請的APPKey
    private val APPKey = "CiQyJxkYO_UZY2R-0dUGNIPqoII"
    private var shift = ""
    private lateinit var station_start: String
    private lateinit var station_end: String
    private var direction = "南下"

    private var trainData = ArrayList<TrainInfo>()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main4)
        intent?.extras?.let {
            shift = it.getString("Shift")
            station_start = it.getString("StationStart")
            station_end = it.getString("StationEnd")
            direction = it.getString("Direction")
        }

        DialogManager.instance.showLoading(this)
        setListener()
        tv_number.text = "${shift} ${direction}"

        //台鐵授權
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
    //廣播
    private val receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            intent.extras?.getString("json")?.let {
                val data = Gson().fromJson(it, TrainNumberInfo::class.java)

                trainData.clear()
                data[0].GeneralTimetable.StopTimes.forEachIndexed { _, stopTime ->
                    trainData.add(
                        TrainInfo(
                            stopTime.StationName.Zh_tw,
                            stopTime.DepartureTime ?: "終點站",
                            stopTime.ArrivalTime ?: "起點站"
                        )
                    )
                }

                val myListAdapter = StationTimeAdapter(
                    this@ShiftInfoActivity,
                    trainData,
                    station_start,
                    station_end
                )
                listview.adapter = myListAdapter

                DialogManager.instance.dismissAll()
            }
        }
    }

    private fun setListener() {
        img_purchase.setOnClickListener {
            val uri = Uri.parse("https://irs.thsrc.com.tw/IMINT/?locale=tw")
            val it = Intent(Intent.ACTION_VIEW, uri)
            startActivity(it)
        }
    }
}
