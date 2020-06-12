package com.example.lab12


import android.app.Activity
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.graphics.red

class MyListAdapter(private val context: Activity, private val rest_name: Array<String?>, private val address: Array<String?>, private val distance: Array<String?>,private val access: Array<String?>)
    : ArrayAdapter<String>(context, R.layout.custom_list,rest_name) {

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val rowView = inflater.inflate(R.layout.custom_list, null, true)

        val rest_name1 = rowView.findViewById(R.id.rest_name) as TextView
        val address1 = rowView.findViewById(R.id.address) as TextView
        val distance1 = rowView.findViewById(R.id.distance) as TextView
        val access1 = rowView.findViewById(R.id.access) as TextView

        rest_name1.text = rest_name[position]
        address1.text = address[position]
        distance1.text=distance[position]
        access1.text=access[position]

        return rowView
    }
}