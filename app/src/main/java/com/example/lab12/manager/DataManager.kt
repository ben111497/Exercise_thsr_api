package com.example.lab12.manager

import com.example.lab12.tools.Method
import okhttp3.*
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit

class DataManager private constructor() : Observable() {
    companion object {
        val instance : DataManager = DataManager()
    }

    private var client = OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS).readTimeout(10, TimeUnit.SECONDS)
        .addInterceptor(LoggingInterceptor()).build()
    private class LoggingInterceptor : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            val t1 = System.nanoTime()
            val response = chain.proceed(request)

            response.body()?.let {
                val t2 = System.nanoTime()
                val contentType = it.contentType()
                val content = it.string()
                Method.logE("${response.request().url()}","${String.format("%.1f", (t2 - t1) / 1e6)}ms $content")

                val wrappedBody = ResponseBody.create(contentType, content)
                return response.newBuilder().body(wrappedBody).build()
            }
            return chain.proceed(request)
        }
    }

    var notifyID = ""

    fun notifyChanged(res: Any) {
        setChanged()
        notifyObservers(res)
    }
}