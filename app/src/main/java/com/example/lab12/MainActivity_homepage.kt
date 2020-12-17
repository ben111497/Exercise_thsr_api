package com.example.lab12

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.example.lab12.fragment.TestFragment
import com.example.lab12.manager.DialogManager
import com.example.lab12.tools.Method
import com.google.android.gms.maps.*
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_main_homepage.*

class MainActivity_homepage : BaseActivity() ,OnMapReadyCallback,OnMarkerClickListener {
    private var x_init=23.583234
    private var y_init=120.5825975

    private var startStation = ""
    private var endStation = ""

    private lateinit var dbrw : SQLiteDatabase
    private var items : ArrayList<String> = ArrayList(0)
    private lateinit var adapter : ArrayAdapter<String>
    private lateinit var map:GoogleMap
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

        val c = dbrw.rawQuery("SELECT * FROM myTable", null)
        c.moveToFirst()
        val stationName = ArrayList<String>()
        val stationAddress = ArrayList<String>()
        items.clear()
        for (i in 0 until c.count) {
            stationName.add(c.getString(0))
            stationAddress.add(c.getString(3))
            c.moveToNext()
        }
        //設定起始站終點站spinner
//        val adapter = StationAdapter(this, stationName, stationAddress)
//        spinner.adapter = adapter
//        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(p0: AdapterVSiew<*>?, v: View?, p: Int, id: Long) {
//                startStation = stationName[p]
//            }
//            override fun onNothingSelected(p0: AdapterView<*>?) {}
//        }

        //交換終點起點
        change.setOnClickListener {
            var change_item1 = start.text
            var change_item2 = end.text
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
            if(start.length()<1 || end.length()<1){
                Toast.makeText(
                    this,
                    "起始站點或終點站點未輸入!",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else{
                if(start.text.toString()==end.text.toString()){
                    Toast.makeText(
                        this,
                        "終點站和起點站輸入相同!\n請更改!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else {
                    if (start.text.toString() == stationName[0] || start.text.toString() == stationName[1]
                        || start.text.toString() == stationName[2] || start.text.toString() == stationName[3]
                        || start.text.toString() == stationName[4] || start.text.toString() == stationName[5]
                        || start.text.toString() == stationName[6] || start.text.toString() == stationName[7]
                        || start.text.toString() == stationName[8] || start.text.toString() == stationName[9]
                        || start.text.toString() == stationName[10] || start.text.toString() == stationName[11]
                    ) {
                        if (end.text.toString() == stationName[0] || end.text.toString() == stationName[1]
                            || end.text.toString() == stationName[2] || end.text.toString() == stationName[3]
                            || end.text.toString() == stationName[4] || end.text.toString() == stationName[5]
                            || end.text.toString() == stationName[6] || end.text.toString() == stationName[7]
                            || end.text.toString() == stationName[8] || end.text.toString() == stationName[9]
                            || end.text.toString() == stationName[10] || end.text.toString() == stationName[11]
                        ) {
                            val bundle = Bundle()
                            val i = Intent(this, MainActivity3::class.java)
                            bundle.putString("station_start", start.text.toString())
                            bundle.putString("station_end", end.text.toString())
                            i.putExtras(bundle)  //此無資料
                            startActivityForResult(i, 2)
                        } else {
                            Toast.makeText(this, "輸入錯誤或站名不存在!!", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this, "輸入錯誤或站名不存在!!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    }
    //google map================================================================================================================================
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
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(x_init,y_init),8f))
        map.setOnMarkerClickListener(this)
    }

    override fun onMarkerClick(p0: Marker?): Boolean {
        var latLng: com.google.android.gms.maps.model.LatLng? =p0?.getPosition()
        x_init=latLng!!.latitude
        y_init=latLng!!.longitude
        val map = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        map.getMapAsync(this)
        showPopup(map.view!!)   //menu
        return false
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
                            start.setText("${c.getString(0)}")
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
                            end.setText("${c.getString(0)}")
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
//頁面2回傳值==============================================================================================================================================
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        data?.extras?.let {
            if(requestCode==1 && resultCode== Activity.RESULT_OK) {//畫面2回傳
                x_init = it.getString("PositionLat").toDouble()
                y_init = it.getString("PositionLon").toDouble()
                var latLng=LatLng(x_init,y_init)
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
            }

            if(requestCode==2 && resultCode== Activity.RESULT_OK){//畫面3回傳
            }

            if(requestCode==3 && resultCode== Activity.RESULT_OK){//畫面3回傳
            }

        }
    }
//==========================================================================================
    private fun setListener() {
        start.setOnClickListener {
            DialogManager.instance.showCustom(this, R.layout.dialog_stationlist, true).let {
                val tv_stationName = it?.findViewById<EditText>(R.id.ed_stationName)
                val listView = it?.findViewById<ListView>(R.id.listView)

            }
        }
        end.setOnClickListener {

        }
    }

}
