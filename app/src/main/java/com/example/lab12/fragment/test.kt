package com.example.lab12.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.lab12.R
import com.example.lab12.fragment.other.ObserverFragment
import java.util.*

class TestFragment: ObserverFragment()  {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_regist, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Toast.makeText(mActivity, "成功", Toast.LENGTH_LONG).show()
    }

    override fun update(p0: Observable?, p1: Any?) {}
}