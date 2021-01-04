package com.example.lab12.manager

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.graphics.drawable.AnimationDrawable
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.lab12.R
import com.example.lab12.adapter.ListDialogAdapter
import com.example.lab12.tools.Method

class DialogManager private constructor(){
    companion object {
        val instance : DialogManager by lazy { DialogManager() }
    }

    private var loadingDialog: Dialog? = null
    private var dialog: Dialog? = null

    fun dismissAll(){
        loadingDialog?.dismiss()
        dialog?.dismiss()
    }

    fun showMessage(activity: Activity, message: String, flag: Boolean = false): TextView?{
        if(!activity.isDestroyed){
            try {
                dialog?.dismiss()

                dialog = AlertDialog.Builder(activity).create()
                dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
                dialog?.setCancelable(false)
                dialog?.show()

                val view = View.inflate(activity, R.layout.dialog_message, null)
                val tv_message = view.findViewById<TextView>(R.id.tv_message)
                tv_message.text = message
                view.findViewById<TextView>(R.id.tv_cancel).visibility = if(flag) View.VISIBLE else View.GONE
                view.findViewById<TextView>(R.id.tv_cancel).setOnClickListener { dialog?.dismiss() }
                val tv_ok = view.findViewById<TextView>(R.id.tv_ok)
                tv_ok.setOnClickListener { dialog?.dismiss() }
                dialog?.setContentView(view)
                return tv_ok
            } catch (e: Exception) {
                return null
            }
        }
        return null
    }

    fun showMessage2(activity: Activity, message: String): Pair<TextView?, TextView?>{
        if(!activity.isDestroyed){
            dialog?.dismiss()

            dialog = AlertDialog.Builder(activity).create()
            dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
            dialog?.setCancelable(true)
            dialog?.show()

            val view = View.inflate(activity, R.layout.dialog_message, null)
            val tv_message = view.findViewById<TextView>(R.id.tv_message)
            val tv_cancel = view.findViewById<TextView>(R.id.tv_cancel)
            val tv_ok = view.findViewById<TextView>(R.id.tv_ok)
            tv_message.text = message
            tv_cancel.visibility = View.VISIBLE
            tv_cancel.setOnClickListener { dialog?.dismiss() }
            tv_ok.setOnClickListener { dialog?.dismiss() }
            dialog?.setContentView(view)
            return Pair(tv_ok, tv_cancel)
        }
        return Pair(null, null)
    }

    fun showList(activity: Activity, strList: Array<String>): ListView?{
        if(!activity.isDestroyed){
            dialog?.dismiss()

            dialog = AlertDialog.Builder(activity).create()
            dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
            dialog?.show()

            val view = View.inflate(activity, R.layout.dialog_listview, null)
            dialog?.setContentView(view)

            val arrayAdapter = ListDialogAdapter(activity, R.layout.item_textview, strList)
            val listView = view.findViewById<ListView>(R.id.listView)
            listView.adapter = arrayAdapter

            listView.setOnItemClickListener { parent, view, position, id ->
                dialog?.dismiss()
            }
            return listView
        }
        return null
    }

    fun showList2(activity: Activity, strList: Array<String>, title: String): ListView?{
        if(!activity.isDestroyed){
            dialog?.dismiss()

            dialog = AlertDialog.Builder(activity).create()
            dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
            dialog?.show()

            val view = View.inflate(activity, R.layout.dialog_sort, null)
            view.findViewById<TextView>(R.id.tv_title).text = title
            dialog?.setContentView(view)

            val arrayAdapter = ListDialogAdapter(activity, R.layout.item_textview2, strList)
            val listView = view.findViewById<ListView>(R.id.listView)
            listView.adapter = arrayAdapter

            listView.setOnItemClickListener { parent, view, position, id ->
                dialog?.dismiss()
            }
            return listView
        }
        return null
    }

    fun showCustom(activity: Activity, layout: Int, keyboard: Boolean = false, gravityPosition: Int = -1): View?{
        if(!activity.isDestroyed){
            try {
                dialog?.dismiss()

                dialog = AlertDialog.Builder(activity, R.style.Theme_Dialog).create()
                dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)

                if (gravityPosition != -1) {
                    val wlp = dialog?.window?.attributes
                    wlp?.gravity = gravityPosition
                    wlp?.flags = wlp?.flags?.and(WindowManager.LayoutParams.FLAG_DIM_BEHIND.inv())
                    dialog?.window?.attributes = wlp
                }

                dialog?.show()
                if(keyboard) {
                    dialog?.window?.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)

                    dialog?.setOnDismissListener {
                        dialog?.window?.addFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)

                        (activity as? AppCompatActivity)?.let {
                            Method.hideKeyBoard(it)
                        }
                    }
                }

                val view = View.inflate(activity, layout, null)
                dialog?.setContentView(view)
                return view
            } catch (e: Exception) {
                return null
            }
        }
        return null
    }

    fun cancelDialog() = dialog?.dismiss()

    fun getDialog() = dialog
}