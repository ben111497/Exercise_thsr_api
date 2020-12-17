package com.example.lab12.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.lab12.R

class StationAdapter(context: Context, list: ArrayList<String>, private val listAddress: ArrayList<String>):
    ArrayAdapter<String>(context, R.layout.adapter_stationlist, list) {

    private class ViewHolder(v: View) {
        val tv_station: TextView = v.findViewById(R.id.tv_station)
        val tv_address: TextView = v.findViewById(R.id.tv_address)
        val cl_station: ConstraintLayout = v.findViewById(R.id.cl_station)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val p = (parent as Spinner).selectedItemPosition
        return createViewFromResource(p, convertView, true)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createViewFromResource(position, convertView, false)
    }

    private fun createViewFromResource(position: Int, convertView: View?, showArrow: Boolean): View {
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

        holder.tv_station.text = getItem(position)
        holder.tv_address.text = listAddress[position]

        return view
    }
}