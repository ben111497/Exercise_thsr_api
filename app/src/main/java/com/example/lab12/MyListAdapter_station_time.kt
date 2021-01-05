package com.example.lab12

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.activity_main4.view.*

class MyListAdapter_station_time(context: Context, list: ArrayList<MainActivity4.TrainInfo>,
                                 private val station_start: String, private val station_end: String)
    : ArrayAdapter<MainActivity4.TrainInfo>(context, R.layout.custom_list_station_time, list) {

    private class ViewHolder(v: View) {
        val tv_station: TextView = v.findViewById(R.id.tv_station)
        val tv_start_time: TextView = v.findViewById(R.id.tv_start_time)
        val tv_arrive_time: TextView = v.findViewById(R.id.tv_arrive_time)
        val ll_all: LinearLayout = v.findViewById(R.id.ll_all)
    }

    override fun getView(position: Int, convertView: View?, viewGroup: ViewGroup): View {
        val view: View
        val holder: ViewHolder

        if(convertView == null){
            view = View.inflate(context, R.layout.custom_list_station_time, null)
            holder = ViewHolder(view)
            view.tag = holder
        } else {
            holder = convertView.tag as ViewHolder
            view = convertView
        }

        val item = getItem(position) ?: return view

        holder.tv_station.text = item.name
        holder.tv_start_time.text = item.startTime
        holder.tv_arrive_time.text = item.arriveTime

        if (item.name == station_start || item.name == station_end) holder.ll_all.background = context.getDrawable(R.drawable.bg_orange_all2)

        return view
    }
}