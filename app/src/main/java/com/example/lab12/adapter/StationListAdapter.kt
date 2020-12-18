package com.example.lab12.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.lab12.R

class StationListAdapter(context: Context, list: ArrayList<String>, private val listAddress: ArrayList<String>
                        , private val listener: MsgListener):
    ArrayAdapter<String>(context, R.layout.adapter_stationlist, list) {
    private class ViewHolder(v: View) {
        val tv_text: TextView = v.findViewById(R.id.tv_station)
        val tv_address: TextView = v.findViewById(R.id.tv_address)
        val cl_station: ConstraintLayout = v.findViewById(R.id.cl_station)
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

        holder.tv_text.text = getItem(position)
        holder.tv_address.text = listAddress[position]
        holder.cl_station.setOnClickListener {
            listener.onClick(position)
        }

        return view
    }
}