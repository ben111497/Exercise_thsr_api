package com.example.lab12.helper

import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.AbsListView
import android.widget.ListView

fun View?.alphaAnimation(show: Boolean, alphaStart: Float = 1.0f,
                         alphaEnd: Float = 0.1f, duration: Long = 1000) {
    this?.clearAnimation()
    if (show) {
        val alpha = AlphaAnimation(alphaStart, alphaEnd)
        alpha.duration = duration
        alpha.repeatCount = Animation.INFINITE
        alpha.repeatMode = Animation.REVERSE
        this?.animation = alpha
        alpha.start()
    }
}

fun ListView?.setListViewHeightBasedOnChildren(maxCount: Int) {
    val listAdapter = this?.adapter ?: return

    var totalHeight = getPaddingTop() + getPaddingBottom()
    for (i in 0 until listAdapter.getCount()) {
        if (maxCount == i) break
        val listItem = listAdapter.getView(i, null, this)
        if (listItem is ViewGroup) {
            listItem.setLayoutParams(
                AbsListView.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    AbsListView.LayoutParams.WRAP_CONTENT
                )
            )
        }
        listItem.measure(0, 0)
        totalHeight += listItem.getMeasuredHeight()
    }

    val params = getLayoutParams()
    params.height = totalHeight + getDividerHeight() * (listAdapter.getCount() - 1)
    setLayoutParams(params)
}