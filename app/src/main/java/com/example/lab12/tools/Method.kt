package com.example.lab12.tools

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.lab12.BuildConfig
import com.example.lab12.R
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.regex.Pattern
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

object Method {
    fun logE(tag: String, message: String, tr: Throwable? = null){
        if(BuildConfig.DEBUG)
            android.util.Log.e(tag, if(message.length>500) message.substring(0, 500) else message, tr)
    }

    @JvmStatic
    fun requestPermission(activity: Activity, vararg permissions: String): Boolean {
        return if (!hasPermissions(activity, *permissions)) {
            ActivityCompat.requestPermissions(activity, permissions, 0)
            false
        } else
            true
    }

    private fun hasPermissions(context: Context, vararg permissions: String): Boolean {
        for (permission in permissions)
            if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED)
                return false
        return true
    }

    fun gzip(content: String): ByteArray {
        val bos = ByteArrayOutputStream()
        GZIPOutputStream(bos).bufferedWriter(StandardCharsets.UTF_8).use { it.write(content) }
        return bos.toByteArray()
    }

    fun ungzip(content: ByteArray): String =
        GZIPInputStream(content.inputStream()).bufferedReader(StandardCharsets.UTF_8).use { it.readText() }

    fun switchTo(activity: AppCompatActivity, fragment : Fragment, bundle: Bundle? = null, broken: Boolean = false){
        val fm = activity.supportFragmentManager
        if(broken || fm.findFragmentByTag(fragment.javaClass.simpleName)==null){
            if(broken && fm.backStackEntryCount>0)
                fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)

            logE("switchTo", fragment.javaClass.simpleName)
            fragment.arguments = bundle
            val ft = fm.beginTransaction()
            ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out,
                R.anim.grow_fade_in_from_bottom, R.anim.shrink_fade_out_from_bottom)
            ft.replace(R.id.fl_fragment, fragment, fragment.javaClass.simpleName)
            ft.addToBackStack(fragment.javaClass.simpleName)
            ft.commit()
        }
    }
    //解決java.lang.IllegalStateException: Can not perform this action after onSaveInstanceState
    fun switchToAllowingStateLoss(activity: AppCompatActivity, fragment : Fragment, bundle: Bundle? = null){
        val fm = activity.supportFragmentManager

        if(fm.findFragmentByTag(fragment.javaClass.simpleName)==null){
            logE(javaClass.simpleName,fragment.javaClass.simpleName)
            fragment.arguments = bundle
            val ft = fm.beginTransaction()
            ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out,
                R.anim.grow_fade_in_from_bottom, R.anim.shrink_fade_out_from_bottom)
            ft.replace(R.id.fl_fragment, fragment, fragment.javaClass.simpleName)
            ft.addToBackStack(fragment.javaClass.simpleName)
            ft.commitAllowingStateLoss()
        }
    }

    // Back to last fragment
    fun popBack(activity: AppCompatActivity){
        val fm = activity.supportFragmentManager

        if(fm.backStackEntryCount>0)
            fm.popBackStack(fm.getBackStackEntryAt(fm.backStackEntryCount-1).id, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

    // Back to specific fragment
    fun popBack(activity: AppCompatActivity, count: Int){
        val fm = activity.supportFragmentManager

        if(fm.backStackEntryCount>count)
            fm.popBackStack(fm.getBackStackEntryAt(fm.backStackEntryCount-count).id, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

    // Exit all fragment
    fun popBackAll(activity: AppCompatActivity){
        val fm = activity.supportFragmentManager

        if(fm.backStackEntryCount>0)
            fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

    // Back to first fragment
    fun popBackFirst(activity: AppCompatActivity){
        val fm = activity.supportFragmentManager

        if(fm.backStackEntryCount>0)
            fm.popBackStack(fm.getBackStackEntryAt(1).id, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

    fun checkPassword(str: String): Boolean{
        if(str.length<6) return false
        val pattern = Pattern.compile("[0-9]")
        val matcher = pattern.matcher(str)

        return if(matcher.find()){
            val pattern2 = Pattern.compile("[a-zA-Z]")
            val matcher2 = pattern2.matcher(str)
            matcher2.find()
        }else
            false
    }

    fun isEmailValid(email: CharSequence) = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()

    fun showKeyBoard(activity: AppCompatActivity, ed: EditText){
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(ed, 0)
    }

    fun hideKeyBoard(activity: AppCompatActivity) {
        activity.currentFocus?.let {
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken,0)
        }
    }

    fun hideKeyBoard(context: Context, view: View) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken,0)
    }

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        val activeNetworkInfo = connectivityManager?.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }
}