package com.example.lab12

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import java.util.*
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.lab12.manager.DataManager
import com.example.lab12.manager.DialogManager

abstract class BaseActivity: AppCompatActivity(), Observer {
    //MARK: LifeCycle
    // Set application language
    override fun attachBaseContext(newBase: Context) {
        val config = newBase.resources.configuration
        super.attachBaseContext(newBase.createConfigurationContext(config))
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Init ActionBar
        supportActionBar?.let {
            it.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
            it.setCustomView(R.layout.actionbar)

            val parent = it.customView?.parent as Toolbar
            parent.setPadding(0, 0, 0, 0)
            parent.setContentInsetsAbsolute(0, 0)
            it.customView?.findViewById<View>(R.id.btn_back)?.setOnClickListener { onBackPressed() }
        }

        window.statusBarColor = getColor(R.color.status_bar_color)
        DataManager.instance.addObserver(this)
    }

    override fun onResume() {
        super.onResume()
        DataManager.instance.addObserver(this)
    }

    override fun onStop() {
        super.onStop()
        DataManager.instance.deleteObserver(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        DialogManager.instance.dismissAll()
    }

    @SuppressLint("MissingSuperCall")
    override fun onSaveInstanceState(outState: Bundle) {
    }

    //MARK: Others Override
    override fun update(o: Observable?, arg: Any?) {
    }

    //MARk: Others Function
    // Restart application

    fun resetActionBar() {
        supportActionBar?.customView?.findViewById<View>(R.id.btn_back)?.visibility = View.GONE
        supportActionBar?.customView?.findViewById<View>(R.id.tv_btn_right)?.visibility = View.GONE
        supportActionBar?.customView?.findViewById<View>(R.id.img_next0)?.visibility = View.GONE
        supportActionBar?.customView?.findViewById<View>(R.id.img_next)?.visibility = View.GONE
        supportActionBar?.customView?.findViewById<View>(R.id.img_next2)?.visibility = View.GONE
        supportActionBar?.customView?.findViewById<View>(R.id.img_next3)?.visibility = View.GONE
    }

    fun showActionBar (isDisplay: Boolean , position: Int) { //Mode true:開啟 false:關閉
        val imageView = when(position){
            0 -> supportActionBar?.customView?.findViewById<ImageView>(R.id.img_next)
            1 -> supportActionBar?.customView?.findViewById<ImageView>(R.id.img_next2)
            2 -> supportActionBar?.customView?.findViewById<ImageView>(R.id.img_next3)
            else -> supportActionBar?.customView?.findViewById<ImageView>(R.id.img_next0)
        }
         imageView?.visibility = if (isDisplay) View.VISIBLE else  View.GONE
    }

    fun setTitle(title: String, reset: Boolean = true): TextView? {
        val tv_title = supportActionBar?.customView?.findViewById<TextView>(R.id.tv_title)
        tv_title?.text = title
        tv_title?.setOnClickListener {}
        tv_title?.setCompoundDrawables(null, null, null, null)
        return tv_title
    }

    fun setTitleGone(): TextView? {
        val tv_title = supportActionBar?.customView?.findViewById<TextView>(R.id.tv_title)
        tv_title?.visibility = View.GONE
        return tv_title
    }

    fun setBack(visble: Boolean) {
        val view = supportActionBar?.customView?.findViewById<View>(R.id.btn_back)
        (view as? ImageView)?.setImageResource(R.drawable.back)
        view?.setOnClickListener { onBackPressed() }
        view?.visibility = if (visble) View.VISIBLE else View.GONE
    }

    fun setButton(position: Int, resource: Int): ImageView? {
        val imageView = when(position) {
            0 -> supportActionBar?.customView?.findViewById<ImageView>(R.id.img_next)
            1 -> supportActionBar?.customView?.findViewById<ImageView>(R.id.img_next2)
            2 -> supportActionBar?.customView?.findViewById<ImageView>(R.id.img_next3)
            else -> supportActionBar?.customView?.findViewById<ImageView>(R.id.img_next0)
        }
        if (resource != 0) {
            imageView?.setImageResource(resource)
            imageView?.visibility = View.VISIBLE
        } else
            imageView?.visibility = View.GONE
        return imageView
    }

    fun setButtonText(text: String, isRight: Boolean = true): TextView? {
        val tv_btn =  supportActionBar?.customView?.findViewById<TextView>(R.id.tv_btn_right)

        if (text.isNotEmpty()) {
            tv_btn?.text = text
            tv_btn?.visibility = View.VISIBLE
        } else
            tv_btn?.visibility = View.GONE

        tv_btn?.setCompoundDrawables(null, null, null, null)
        tv_btn?.setBackgroundResource(android.R.color.transparent)
        tv_btn?.setOnClickListener {}

        return tv_btn
    }

    fun cleanNotification(){
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.cancelAll()
    }
}