package com.example.lab12

import android.app.Activity
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.widget.*
import androidx.core.view.get
import com.example.lab12.adapter.StationListAdapter
import com.example.lab12.adapter.StationSearchAdapter
import com.example.lab12.manager.DialogManager
import kotlinx.android.synthetic.main.activity_main2.*
import kotlinx.android.synthetic.main.activity_main_homepage.*
import androidx.core.widget.addTextChangedListener as addTextChangedListener1

class MainActivity2 : AppCompatActivity() {
    class Station(val name: String, val address: String, val positionLat: String, val positionLon: String)

    private lateinit var dbrw : SQLiteDatabase
    private var items = ArrayList<Station>()
    private var originData = ArrayList<Station>()

    private lateinit var adapter : StationSearchAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        dbrw=MyDBHelper(this).writableDatabase
        //取得資料庫實體

        adapter = StationSearchAdapter(this, items, object: StationListAdapter.MsgListener{
            override fun onClick(position: Int) {
                val bundle = Bundle()
                bundle.putString("PositionLat", items[position].positionLat)
                bundle.putString("PositionLon", items[position].positionLon)
                val intent = Intent().putExtras(bundle)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        })

        listview.adapter = adapter

        //宣告 Adapter，使用 simple_list_item_1並連結 listView
        val c = dbrw.rawQuery( "SELECT * FROM myTable",null)
        c.moveToFirst()
        items.clear()
        for(i in 0 until c.count){
            originData.add(Station(c.getString(0),  c.getString(3), c.getString(1), c.getString(2)))
            c.moveToNext()
        }
        items.addAll(originData)
        c.close()
        
        name.addTextChangedListener(object:TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                items.clear()
                items.addAll(originData.filter { it.name.contains(p0.toString()) || it.address.contains(p0.toString()) })
                adapter.notifyDataSetChanged()
            }
        })
    }
}


