package com.example.lab12


import android.app.Activity
import android.app.PendingIntent.getActivity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.lab12.tools.Method
import java.net.URL


class MyListAdapter(private val context: Activity, list: ArrayList<MainActivity5.StoreInfo>)
    : ArrayAdapter<MainActivity5.StoreInfo>(context, R.layout.activity_near_rest, list) {

    private class ViewHolder(v: View) {
        val tv_name: TextView = v.findViewById(R.id.tv_name)
        val tv_address: TextView = v.findViewById(R.id.tv_address)
        val tv_distance: TextView = v.findViewById(R.id.tv_distance)
        val tv_access: TextView = v.findViewById(R.id.tv_access)
        val tv_phone_number: TextView = v.findViewById(R.id.tv_phone_number)
        val img_picture: ImageView = v.findViewById(R.id.img_picture)
    }

    override fun getView(position: Int, convertView: View?, viewGroup: ViewGroup): View {
        val view: View
        val holder: ViewHolder

        if(convertView == null){
            view = View.inflate(context, R.layout.activity_near_rest, null)
            holder = ViewHolder(view)
            view.tag = holder
        } else {
            holder = convertView.tag as ViewHolder
            view = convertView
        }

        val item = getItem(position) ?: return view

        holder.tv_name.text = item.restName
        holder.tv_address.text = item.address
        holder.tv_distance.text = item.distance
        holder.tv_access.text = item.access
        holder.tv_phone_number.text = item.phoneNumber
        Glide.with(context).load(item.picture).into(holder.img_picture)

        return view
    }
}