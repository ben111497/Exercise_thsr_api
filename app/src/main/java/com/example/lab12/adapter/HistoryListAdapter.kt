package com.example.lab12.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.lab12.activity.HomepageActivity
import com.example.lab12.R

class HistoryListAdapter(context: Context, list: ArrayList<HomepageActivity.HistoryData>, private val listener: MsgListener):
    ArrayAdapter<HomepageActivity.HistoryData>(context, R.layout.adapter_history_list, list) {
    private class ViewHolder(v: View) {
        val tv_start: TextView = v.findViewById(R.id.tv_start)
        val tv_end: TextView = v.findViewById(R.id.tv_end)
        val img_cancel: ImageView = v.findViewById(R.id.img_cancel)
        val cl_all: ConstraintLayout = v.findViewById(R.id.cl_all)
    }

    interface MsgListener {
        fun onClick(position: Int)
        fun onCancel(position: Int)
    }

    override fun getView(position: Int, convertView: View?, viewGroup: ViewGroup): View {
        val view: View
        val holder: ViewHolder

        if(convertView == null){
            view = View.inflate(context, R.layout.adapter_history_list, null)
            holder = ViewHolder(view)
            view.tag = holder
        } else {
            holder = convertView.tag as ViewHolder
            view = convertView
        }

        val item =  getItem(position) ?: return view

        holder.cl_all.background = context.getDrawable(if (position % 2 == 0) R.drawable.bg_blue_green else R.drawable.bg_white3)

        holder.tv_start.text = item.startStation
        holder.tv_end.text = item.endStation

        holder.cl_all.setOnClickListener {
            listener.onClick(position)
        }

        holder.img_cancel.setOnClickListener {
            listener.onCancel(position)
        }
        return view
    }
}