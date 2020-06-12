package com.example.lab12

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteDatabase
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.PopupMenu
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.*
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_main_homepage.*

class MainActivity_homepage : AppCompatActivity() ,OnMapReadyCallback,OnMarkerClickListener {
    var x_init=23.583234
    var y_init=120.5825975
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
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        )
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_PERMISSIONS
            )
        else {
            val map = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
            map.getMapAsync(this)
        }
        //交換終點起點
        change.setOnClickListener {
            var change_item1 = start.text
            var change_item2 = end.text
            start.setText(change_item2)
            end.setText(change_item1)
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
                    val c = dbrw.rawQuery("SELECT * FROM myTable", null)
                    c.moveToFirst()
                    val station_name = arrayOfNulls<String>(c.count)
                    items.clear()
                    for (i in 0 until c.count) {
                        station_name[i] = c.getString(0)
                        c.moveToNext()
                    }
                    if (start.text.toString() == station_name[0] || start.text.toString() == station_name[1]
                        || start.text.toString() == station_name[2] || start.text.toString() == station_name[3]
                        || start.text.toString() == station_name[4] || start.text.toString() == station_name[5]
                        || start.text.toString() == station_name[6] || start.text.toString() == station_name[7]
                        || start.text.toString() == station_name[8] || start.text.toString() == station_name[9]
                        || start.text.toString() == station_name[10] || start.text.toString() == station_name[11]
                    ) {
                        if (end.text.toString() == station_name[0] || end.text.toString() == station_name[1]
                            || end.text.toString() == station_name[2] || end.text.toString() == station_name[3]
                            || end.text.toString() == station_name[4] || end.text.toString() == station_name[5]
                            || end.text.toString() == station_name[6] || end.text.toString() == station_name[7]
                            || end.text.toString() == station_name[8] || end.text.toString() == station_name[9]
                            || end.text.toString() == station_name[10] || end.text.toString() == station_name[11]
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

}
