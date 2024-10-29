package com.intecular.invis.data.interceptor


import com.intecular.invis.data.datastore.ProtoDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.RequestBody
import okhttp3.Response
import timber.log.Timber
import javax.inject.Inject

class HeaderInterceptor @Inject constructor(
    private val protoDataStore: ProtoDataStore
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val builder = original.newBuilder()
        val apiHeader = runBlocking { protoDataStore.getApiHeader().first() }
        builder.header("Content-Type", "application/x-amz-json-1.1")
        if (apiHeader.isNotEmpty()) {
            builder.header("X-Amz-Target", apiHeader)
        }
       Timber.d("RequestHeader:${ builder.build().headers}")
        return chain.proceed(builder.build())
    }
}