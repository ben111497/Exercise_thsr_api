package com.example.lab12

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.example.lab12.adapter.StationSearchAdapter
import com.example.lab12.fragment.TestFragment
import com.example.lab12.manager.DialogManager
import com.example.lab12.tools.Method
import com.google.android.gms.maps.*
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_main2.*
import kotlinx.android.synthetic.main.activity_main_homepage.*

class MainActivity_homepage : BaseActivity(), OnMapReadyCallback, OnMarkerClickListener {
    class Station(val name: String, val address: String, val positionLat: String, val positionLon: String)
    private var x_init = 23.583234
    private var y_init = 120.5825975

    private lateinit var adapterStation: StationSearchAdapter
    private lateinit var adapterStation2: StationSearchAdapter

    private lateinit var dbrw : SQLiteDatabase
    private var items = ArrayList<Station>()
    private var originData = ArrayList<Station>()
    private lateinit var map:GoogleMap

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        data?.extras?.let {
            if(requestCode==1 && resultCode== Activity.RESULT_OK) {//畫面2回傳
                x_init = it.getString("PositionLat").toDouble()
                y_init = it.getString("PositionLon").toDouble()
                val latLng = LatLng(x_init, y_init)
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
            }

            if(requestCode==2 && resultCode== Activity.RESULT_OK){//畫面3回傳
            }

            if(requestCode==3 && resultCode== Activity.RESULT_OK){//畫面3回傳
            }

        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_homepage)

        dbrw = MyDBHelper(this).writableDatabase
        //取得資料庫實體
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_PERMISSIONS)
        else {
            val map = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
            map.getMapAsync(this)
        }
        setTitle("老鐵發車")
        setListener()

        val c = dbrw.rawQuery( "SELECT * FROM myTable",null)
        c.moveToFirst()
        items.clear()
        for(i in 0 until c.count){
            originData.add(Station(c.getString(0), c.getString(3), c.getString(1), c.getString(2)))
            c.moveToNext()
        }
        items.addAll(originData)
        c.close()
    }

    override fun onBackPressed() {
        Method.logE("back","back")
    }

    //google map
    private val REQUEST_PERMISSIONS = 1
    override fun onRequestPermissionsResult (requestCode: Int ,permissions: Array<String>, grantResults: IntArray) {
        if (grantResults.isEmpty()) return
        when (requestCode){
            REQUEST_PERMISSIONS ->{
                for (result in grantResults)
                    if (result != PackageManager.PERMISSION_GRANTED){
                        finish()
                    }
                    else{
                        val map =supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
                        map.getMapAsync(this)
                    }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onMapReady(map2:GoogleMap){
        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED)return
        map=map2
        map.isMyLocationEnabled=true
        val c = dbrw.rawQuery( "SELECT * FROM myTable",null)
        c.moveToFirst()
        items.clear()
        for(i in 0 until c.count){
            val marker=MarkerOptions()
            marker.position(LatLng(c.getString(1).toDouble(),c.getString(2).toDouble()))
            marker.title(c.getString(0)+"高鐵站")
            marker.draggable(true)
            map.addMarker(marker)
            c.moveToNext()
        }
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(x_init,y_init), 8f))
        map.setOnMarkerClickListener(this)
    }

    override fun onMarkerClick(p0: Marker?): Boolean {
        val latLng = p0?.position
        latLng?.let {
            x_init = it.latitude
            y_init = it.longitude
        }
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
        showListDialog()
        return false
    }

    private fun setListener() {
        //交換終點起點
        change.setOnClickListener {
            val change_item1 = start.text
            val change_item2 = end.text
            start.setText(change_item2)
            end.setText(change_item1)
            Method.switchTo(this, TestFragment())
        }
        //搜尋站點
        station_search.setOnClickListener {
            val bundle = Bundle()
            val i = Intent(this, MainActivity2::class.java)
            i.putExtras(bundle)  //此無資料
            startActivityForResult(i, 1)
        }
        //時刻表規劃路線規劃
        station_plan.setOnClickListener{
            val startStation = start.text.toString().replace("車站", "")
            val endStation = end.text.toString().replace("車站", "")
            if (startStation.isEmpty() || endStation.isEmpty())
                Toast.makeText(this, "起始站點或終點站點未輸入!", Toast.LENGTH_SHORT).show()
            else {
                if(startStation.contains(endStation))
                    Toast.makeText(this, "終點站和起點站輸入相同!\n請更改!", Toast.LENGTH_SHORT).show()
                else {
                    val bundle = Bundle()
                    val i = Intent(this, MainActivity3::class.java)
                    bundle.putString("StationStart", startStation)
                    bundle.putString("StationEnd", endStation)
                    i.putExtras(bundle)
                    startActivityForResult(i, 2)
                }
            }
        }

        cl_start.setOnClickListener {
            DialogManager.instance.showCustom(this, R.layout.dialog_stationlist, true)?.let {
                val ed_stationName = it.findViewById<EditText>(R.id.ed_stationName)
                val listView = it.findViewById<ListView>(R.id.listView)
                adapterStation = StationSearchAdapter(this, items, object: StationSearchAdapter.MsgListener {
                    override fun onClick(position: Int) {
                        start.text = "${items[position].name}車站"
                        DialogManager.instance.cancelDialog()
                    }
                })
                items.clear()
                items.addAll(originData)
                listView?.adapter = adapterStation
                adapterStation.notifyDataSetChanged()

                ed_stationName.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(p0: Editable?) { }

                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                        items.clear()

                        if (ed_stationName.text.isEmpty())
                            items.addAll(originData)
                        else
                            items.addAll(originData.filter { it.name.contains(p0.toString()) || it.address.contains(p0.toString()) })

                        adapterStation.notifyDataSetChanged()
                    }
                })
            }
        }

        cl_end.setOnClickListener {
            DialogManager.instance.showCustom(this, R.layout.dialog_stationlist, true)?.let {
                val ed_stationName = it.findViewById<EditText>(R.id.ed_stationName)
                val listView = it.findViewById<ListView>(R.id.listView)
                adapterStation2 = StationSearchAdapter(this, items, object: StationSearchAdapter.MsgListener{
                override fun onClick(position: Int) {
                    end.text = "${items[position].name}車站"
                    DialogManager.instance.cancelDialog()
                }
            })
                items.clear()
                items.addAll(originData)
                listView?.adapter = adapterStation2
                adapterStation2.notifyDataSetChanged()

                ed_stationName.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(p0: Editable?) { }

                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                        items.clear()

                        if (ed_stationName.text.isEmpty())
                            items.addAll(originData)
                        else
                            items.addAll(originData.filter { it.name.contains(p0.toString()) || it.address.contains(p0.toString()) })

                        adapterStation2.notifyDataSetChanged()
                    }
                })
            }
        }
    }

    private fun showListDialog() {
        val data = arrayOf("設定成起點", "設定成終點", "附近餐廳", "取消")
        DialogManager.instance.showList(this, data)
            ?.setOnItemClickListener { parent, view, p, id ->
                DialogManager.instance.cancelDialog()
                when (p) {
                    0 -> originData.find { it.positionLat == x_init.toString() || it.positionLon == y_init.toString() }?.name?.let { start.text = "${it}車站" }
                    1 -> originData.find { it.positionLat == x_init.toString() || it.positionLon == y_init.toString() }?.name?.let { end.text = "${it}車站" }
                    2 -> {
                        val bundle2 = Bundle()
                        val i = Intent(this, MainActivity5::class.java)
                        bundle2.putDouble("lat", x_init)
                        bundle2.putDouble("lng", y_init)
                        i.putExtras(bundle2)
                        startActivityForResult(i, 3)
                    }
                    3 -> DialogManager.instance.dismissAll()
                }
            }
    }

    private fun showPopup(view: View) {
        var popup: PopupMenu? = null;
        popup = PopupMenu(this, view)
        popup.inflate(R.menu.header_menu)
        popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item: MenuItem? ->
            when (item!!.itemId) {
                R.id.start_point -> {
                    val c = dbrw.rawQuery( "SELECT * FROM myTable",null)
                    c.moveToFirst()
                    for(i in 0 until c.count){
                        if(String.format("%.2f",c.getString(1).toDouble())==String.format("%.2f",x_init)
                            && String.format("%.2f",c.getString(2).toDouble())==String.format("%.2f",y_init)){
                            start.text = "${c.getString(0)}"
                        }
                        c.moveToNext()
                    }
                }
                R.id.end_point -> {
                    val c = dbrw.rawQuery( "SELECT * FROM myTable",null)
                    c.moveToFirst()
                    for(i in 0 until c.count){
                        if(String.format("%.2f",c.getString(1).toDouble())==String.format("%.2f",x_init)
                            && String.format("%.2f",c.getString(2).toDouble())==String.format("%.2f",y_init)){
                            end.text = "${c.getString(0)}"
                        }
                        c.moveToNext()
                    }
                }
                R.id.rest_near -> {
                    val bundle2 = Bundle()
                    val i = Intent(this, MainActivity5::class.java)
                    bundle2.putDouble("lat",x_init)
                    bundle2.putDouble("lng",y_init)
                    i.putExtras(bundle2)  //此無資料
                    startActivityForResult(i, 3)
                }
                R.id.cancel -> {
                    //無動作
                }
            }
            true
        })
        popup.show()
    }
}
