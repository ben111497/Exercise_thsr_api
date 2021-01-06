package com.example.lab12.activity


import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.lab12.R


class MyListAdapter(private val context: Activity, list: ArrayList<NearRestActivity.StoreInfo>)
    : ArrayAdapter<NearRestActivity.StoreInfo>(context,
    R.layout.activity_near_rest, list) {

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
            view = View.inflate(context,
                R.layout.activity_near_rest, null)
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
        holder.tv_access.text = item.access.toString()
        holder.tv_phone_number.text = item.phoneNumber
        Glide.with(context).load(item.picture).into(holder.img_picture)

        return view
    }
}