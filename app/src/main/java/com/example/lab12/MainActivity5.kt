package com.example.lab12

import android.app.Activity
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.lab12.data.NearRest
import com.example.lab12.manager.DialogManager
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main5.*
import okhttp3.*
import java.io.IOException

class MainActivity5 : AppCompatActivity() {
    class StoreInfo(val restName: String, val address: String, val distance: String, 
                    val access: Double, val picture: String, val phoneNumber: String)

    private var storeDataList = ArrayList<StoreInfo>()
    
    private var latInit: Double = 25.04
    private var lngInt: Double = 121.5
    private var range = 2000

    private val APIUrl = "https://api.bluenet-ride.com/v2_0/lineBot/restaurant/get"
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main5)

        val intentfilter = IntentFilter("MyMessage5")
        registerReceiver(receiver, intentfilter)
        intent?.extras?.let {
            latInit = it.getDouble("lat")
            lngInt = it.getDouble("lng")
        }

        back.setOnClickListener {
            val bundle = Bundle()
            val intent = Intent().putExtras(bundle)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

        DialogManager.instance.showLoading(this)

        val json = "{\"lastIndex\":-1,\"count\":15,\"type\":[7],\"lat\":${latInit},\"lng\":${lngInt},\"range\":${range}}"
        val body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),json)

        val req = Request.Builder()
            .header("Content-Type","application/json")
            .url(APIUrl)
            .post(body)
            .build()

        OkHttpClient().newCall(req).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                sendBroadcast(Intent("MyMessage5").putExtra("json", response.body()?.string()))
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
                val data = Gson().fromJson(it, NearRest::class.java)
                val lat = ArrayList<Double>()
                val lng = ArrayList<Double>()

                storeDataList.clear()
                data.results.content.forEachIndexed { index, content ->
                    val result = FloatArray(1)
                    lat.add(content.lat)
                    lng.add(content.lng)
                    Location.distanceBetween(content.lat, content.lng, latInit, lngInt, result) //經緯度距離計算
                    val str = String.format("%.2f",result[0]/1000)

                    storeDataList.add(StoreInfo(content.name, content.vicinity, "${str}公里", content.rating, content.photo, content.phone))
                }

                val myListAdapter = MyListAdapter(this@MainActivity5, storeDataList)
                listview.adapter = myListAdapter
                listview.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
                    val uri = Uri.parse("http://maps.google.com/maps?f=d&saddr=${latInit}%20${lngInt}&daddr=${lat[position]}%20${lng[position]}&hl=en")
                    val it = Intent(Intent.ACTION_VIEW, uri)
                    startActivity(it)
                }

                DialogManager.instance.dismissAll()
            }
        }
    }
}
