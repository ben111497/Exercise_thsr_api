package com.example.lab12


import android.app.Activity
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout

class MyListAdapter_station_time(private val context: Activity, private val number: Array<String?>, private val station: Array<String?>, private val time: Array<String?>
                                 , private val station_start: String, private val station_end: String)
    : ArrayAdapter<String>(context, R.layout.custom_list_station_time,number) {

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val rowView = inflater.inflate(R.layout.custom_list_station_time, null, true)
        val number2 = rowView.findViewById(R.id.number) as TextView
        val station2 = rowView.findViewById(R.id.station) as TextView
        val time2 = rowView.findViewById(R.id.time) as TextView
        val cl_all=rowView.findViewById(R.id.cl_all) as ConstraintLayout
        for (i in 0 until position+1) {
            if (station[i] == station_start||station[i] == station_end) {
                number2.text = "${number[i]}:"
                station2.text = "${station[i]}高鐵站"
                time2.text = time[i]
                cl_all.setBackgroundColor(Color.YELLOW)
            } else {
                number2.text = "${number[i]}:"
                station2.text = "${station[i]}高鐵站"
                time2.text = time[i]
                cl_all.setBackgroundColor(Color.alpha(255))
            }
        }
        return rowView
    }
}