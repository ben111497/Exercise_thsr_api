package com.example.lab12.fragment.other

import android.os.Bundle
import com.example.lab12.fragment.other.BaseFragment
import com.example.lab12.manager.DataManager
import java.util.*

abstract class ObserverFragment: BaseFragment(), Observer {
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
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
}