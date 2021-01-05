package com.example.lab12.other

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.AbsSpinner
import androidx.fragment.app.Fragment
import com.example.lab12.BaseActivity

abstract class BaseFragment: Fragment() {
    lateinit var mActivity: BaseActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        mActivity = activity as BaseActivity
        super.onCreate(savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()

        /*if(mActivity is MainActivity){
            val refWatcher = (mActivity as MainActivity).refWatcher
            refWatcher.watch(this)
        }*/
    }

    fun unbindDrawables(view: View?) {
        view?.let {
            if (it.background != null)
                it.background.callback = null

            if (it is ViewGroup) {
                for (i in 0 until it.childCount)
                    unbindDrawables(it.getChildAt(i))
                if(it !is AbsListView && it !is AbsSpinner)
                    it.removeAllViews()
            }
        }
    }
}