package com.example.lab12.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import com.example.lab12.R

class ListAdapter(context: Context, list: ArrayList<String>, private val listAddress: ArrayList<String>):
    ArrayAdapter<String>(context, R.layout.adapter_stationlist, list) {
    private class ViewHolder(v: View) {
        val tv_text: TextView = v.findViewById(R.id.tv_station)
        val tv_address: TextView = v.findViewById(R.id.tv_address)
    }

    override fun getView(position: Int, convertView: View?, viewGroup: ViewGroup): View {
        val view: View
        val holder: ViewHolder

        if(convertView == null){
            view = View.inflate(context, 0, null)
            holder = ViewHolder(view)
            view.tag = holder
        } else {
            holder = convertView.tag as ViewHolder
            view = convertView
        }

        holder.tv_text.text = getItem(position)
        holder.tv_address.text = listAddress[position]

        return view
    }
}