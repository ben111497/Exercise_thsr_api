package com.example.lab12.activity

import android.app.Activity
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
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.lab12.R
import com.example.lab12.data.NearRest
import com.example.lab12.manager.DialogManager
import com.example.lab12.tools.Method
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main5.*
import okhttp3.*
import java.io.IOException

class NearHotelActivity : AppCompatActivity() {
    class StoreInfo(val restName: String, val address: String, val distance: String, 
                    val access: Double, val picture: String, val star: Int)

    private var storeDataList = ArrayList<StoreInfo>()
    
    private var latInit: Double = 25.04
    private var lngInt: Double = 121.5
    private val hotelLatList = ArrayList<Double>()
    private val hotelLngList = ArrayList<Double>()
    private val hotelUrlList = ArrayList<String>()

    private val APIUrl = "https://api.bluenet-ride.com/v2_0/lineBot/hotel/get"
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main5)

        val intentfilter = IntentFilter("MyMessage5")
        registerReceiver(receiver, intentfilter)
        intent?.extras?.let {
            latInit = it.getDouble("HotelLatList")
            lngInt = it.getDouble("HotelLngList")
        }

        back.setOnClickListener {
            val bundle = Bundle()
            val intent = Intent().putExtras(bundle)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

        DialogManager.instance.showLoading(this)

        Method.logE("latInit", "$latInit")
        Method.logE("lngInt", "$lngInt")

        val json = "{\"lastIndex\":-1,\"type\":[7],\"count\":15,\"lat\": ${latInit},\"lng\": ${lngInt},\"range\":\"10000\",\"mode\":0}"
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
    //廣播
    private val receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            intent.extras?.getString("json")?.let {
                val data = Gson().fromJson(it, NearRest::class.java)

                storeDataList.clear()
                hotelUrlList.clear()
                
                data.results.content.forEachIndexed { index, content ->
                    val result = FloatArray(1)
                    hotelLatList.add(content.lat)
                    hotelLngList.add(content.lng)
                    hotelUrlList.add(content.url)
                    Location.distanceBetween(content.lat, content.lng, latInit, lngInt, result) //經緯度距離計算
                    val str = String.format("%.2f",result[0]/1000)

                    if (content.url.isNotEmpty() && content.name.isNotEmpty())
                        storeDataList.add(StoreInfo(content.name, content.vicinity, "${str}公里", content.rating, content.photo, content.star))
                }

                storeDataList.sortBy { it.distance.replace("公里", "").toDouble() }

                val myListAdapter = MyListAdapter(this@NearHotelActivity, storeDataList)

                listview.adapter = myListAdapter
                listview.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
                    showNearHotelChooseDialog(position)
                }

                DialogManager.instance.dismissAll()
            }
        }
    }

    private fun showNearHotelChooseDialog(position: Int) {
        DialogManager.instance.showCustom(this, R.layout.dialog_choose)?.let {
            val ll_hotel = it.findViewById<LinearLayout>(R.id.ll_hotel)
            val ll_google = it.findViewById<LinearLayout>(R.id.ll_google)

            ll_google.setOnClickListener {
                val uri = Uri.parse("http://maps.google.com/maps?f=d&saddr=${latInit}%20${lngInt}&daddr=${hotelLatList[position]}%20${hotelLngList[position]}&hl=en")
                val it = Intent(Intent.ACTION_VIEW, uri)
                startActivity(it)
                DialogManager.instance.dismissAll()
            }

            ll_hotel.setOnClickListener {
                val uri = Uri.parse(hotelUrlList[position])
                val it = Intent(Intent.ACTION_VIEW, uri)
                startActivity(it)
                DialogManager.instance.dismissAll()
            }
        }
    }
}
