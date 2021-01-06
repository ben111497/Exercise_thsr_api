package com.example.lab12.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.lab12.activity.ThsrInfoActivity
import com.example.lab12.R

class StationTimeSearchAdapter(context: Context, list: ArrayList<ThsrInfoActivity.StationInfo>):
    ArrayAdapter<ThsrInfoActivity.StationInfo>(context, R.layout.adapter_stationinfo, list) {
    private class ViewHolder(v: View) {
        val tv_number: TextView = v.findViewById(R.id.tv_number)
        val tv_start: TextView = v.findViewById(R.id.tv_start)
        val tv_time: TextView = v.findViewById(R.id.tv_time)
        val tv_arrive: TextView = v.findViewById(R.id.tv_arrive)
    }

    override fun getView(position: Int, convertView: View?, viewGroup: ViewGroup): View {
        val view: View
        val holder: ViewHolder

        if(convertView == null){
            view = View.inflate(context, R.layout.adapter_stationinfo, null)
            holder = ViewHolder(view)
            view.tag = holder
        } else {
            holder = convertView.tag as ViewHolder
            view = convertView
        }

        val item = getItem(position) ?: return view

        holder.tv_number.text = "${item.number}\n${item.direction}"
        holder.tv_start.text = item.startTime
        holder.tv_time.text = item.totalTime
        holder.tv_arrive.text = item.arriveTime

        return view
    }
}