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
import kotlinx.android.synthetic.main.activity_main2.*
import androidx.core.widget.addTextChangedListener as addTextChangedListener1

class MainActivity2 : AppCompatActivity() {
    private lateinit var dbrw : SQLiteDatabase
    private var items : ArrayList<String> = ArrayList(0)

    private lateinit var adapter : ArrayAdapter<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        dbrw=MyDBHelper(this).writableDatabase
        //取得資料庫實體
        adapter = ArrayAdapter(this,android.R.layout.simple_list_item_1 , items)
        listview.adapter = adapter

        //宣告 Adapter，使用 simple_list_item_1並連結 listView
        val c = dbrw.rawQuery( "SELECT * FROM myTable",null)
        var PositionLat = arrayOfNulls<String>(c.count)
        var PositionLon = arrayOfNulls<String>(c.count)
        c.moveToFirst()
        items.clear()
        for(i in 0 until c.count){
            items.add("${c.getString(0)}高鐵站\n ${c.getString(3)}")
            PositionLat[i]=c.getString(1)
            PositionLon[i]=c.getString(2)
            c.moveToNext()
        }
        adapter.notifyDataSetChanged()
        listview.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->
            val bundle = Bundle()
            bundle.putString("PositionLat", PositionLat[position].toString())
            bundle.putString("PositionLon", PositionLon[position].toString())
            val intent = Intent().putExtras(bundle)
            setResult(Activity.RESULT_OK, intent)
            finish()
        })
        c.close()
        name.addTextChangedListener(object:TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val c = dbrw.rawQuery( if(name.length()<1 ){ "SELECT * FROM myTable"}
                else {"SELECT * FROM myTable WHERE StationName LIKE '%${name.text}%' OR StationAddress LIKE '%${name.text}%'"},null)
                c.moveToFirst()
                items.clear()
                for(i in 0 until c.count){
                    items.add("${c.getString(0)}高鐵站\n${c.getString(3)}")
                    PositionLat[i]=c.getString(1)
                    PositionLon[i]=c.getString(2)
                    c.moveToNext()
                }
                adapter.notifyDataSetChanged()
                listview.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->
                    val bundle1 = Bundle()
                    bundle1.putString("PositionLat", PositionLat[position].toString())
                    bundle1.putString("PositionLon", PositionLon[position].toString())
                    val intent = Intent().putExtras(bundle1)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                })
                c.close()
            }
        })

    }
}


