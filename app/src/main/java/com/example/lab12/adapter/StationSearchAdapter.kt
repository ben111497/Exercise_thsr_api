package com.example.lab12.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.lab12.activity.HomepageActivity
import com.example.lab12.R

class StationSearchAdapter(context: Context, list: ArrayList<HomepageActivity.Station>, private val listener: MsgListener):
    ArrayAdapter<HomepageActivity.Station>(context, R.layout.adapter_stationlist, list) {
    private class ViewHolder(v: View) {
        val tv_station: TextView = v.findViewById(R.id.tv_station)
        val tv_address: TextView = v.findViewById(R.id.tv_address)
        val cl_station: ConstraintLayout = v.findViewById(R.id.cl_station)
        val cl_all: ConstraintLayout = v.findViewById(R.id.cl_all)
    }

    interface MsgListener {
        fun onClick(position: Int)
    }

    override fun getView(position: Int, convertView: View?, viewGroup: ViewGroup): View {
        val view: View
        val holder: ViewHolder

        if(convertView == null){
            view = View.inflate(context, R.layout.adapter_stationlist, null)
            holder = ViewHolder(view)
            view.tag = holder
        } else {
            holder = convertView.tag as ViewHolder
            view = convertView
        }

        holder.cl_station.visibility = View.GONE
        holder.cl_station.visibility = View.GONE

        val item = getItem(position) ?: return view

        holder.cl_station.visibility = View.VISIBLE
        holder.tv_station.text = "${item.name}高鐵站"
        holder.tv_address.text = item.address
        holder.cl_station.setOnClickListener {
            listener.onClick(position)
        }
        return view
    }
}