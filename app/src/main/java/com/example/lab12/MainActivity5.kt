package com.example.lab12

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Location
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main5.*
import okhttp3.*
import java.io.IOException
import java.security.SignatureException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity5 : AppCompatActivity() {
    data class rest(
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

    var lat_init: Double = 25.04
    var lng_init: Double = 121.5
    var range:Int=2000

    val APIUrl = "https://api.bluenet-ride.com/v2_0/lineBot/restaurant/get"
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main5)
        val intentfilter = IntentFilter("MyMessage5")
        registerReceiver(receiver, intentfilter)
        intent?.extras?.let {
            lat_init=it.getDouble("lat")
            lng_init=it.getDouble("lng")
        }
        back.setOnClickListener {
            val bundle = Bundle()
            val intent = Intent().putExtras(bundle)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

        val json = "{\"lastIndex\":-1,\"count\":15,\"type\":[7],\"lat\":${lat_init},\"lng\":${lng_init},\"range\":${range}}"
        val body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),json)

        val req = Request.Builder()
            .header("Content-Type","application/json")
            .url(APIUrl)
            .post(body)
            .build()

        OkHttpClient().newCall(req).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                sendBroadcast(
                    Intent("MyMessage5")
                        .putExtra("json", response.body()?.string())
                )
            }
            override fun onFailure(call: Call, e: IOException?) {
                Log.e("查詢失敗", "$e")
            }
        })

    }
    //廣播=============================================================================================================================
    private val receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            intent.extras?.getString("json")?.let {
                val data = Gson().fromJson(it, rest::class.java)
                // 店名 地址 距離 評價 評論數
                var rest_name= arrayOfNulls<String>(data.results.content.size)
                var address= arrayOfNulls<String>(data.results.content.size)
                var distance= arrayOfNulls<String>(data.results.content.size)
                var access= arrayOfNulls<String>(data.results.content.size)
                var lat= arrayOfNulls<Double>(data.results.content.size)
                var lng= arrayOfNulls<Double>(data.results.content.size)

                for(i in 0 until data.results.content.size){
                    rest_name[i]=data.results.content[i].name
                    address[i]=data.results.content[i].vicinity
                    access[i]="${data.results.content[i].priceLevel}(${data.results.content[i].reviewsNumber}則評論)"
                    lat[i]=data.results.content[i].lat
                    lng[i]=data.results.content[i].lng
                    var result:FloatArray=FloatArray(1)
                    Location.distanceBetween(lat[i]!!,lng[i]!!,lat_init,lng_init,result) //經緯度距離計算
                    var str:String=String.format("%.2f",result[0]/1000)
                    distance[i]="${str}公里"
                }
                val myListAdapter = MyListAdapter(this@MainActivity5, rest_name, address, distance, access)
                listview.adapter = myListAdapter

            }
        }
    }
}
