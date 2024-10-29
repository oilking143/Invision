package com.intecular.invis.data.di

import com.intecular.invis.data.interceptor.HeaderInterceptor
import com.slack.eithernet.ApiResultCallAdapterFactory
import com.slack.eithernet.ApiResultConverterFactory
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetWorkModule {

    @Singleton
    @Provides
    fun provideMoshi(): Moshi = Moshi.Builder().build()


    @Singleton
    @Provides
    fun provideOkHttpClient(interceptor: HeaderInterceptor) = OkHttpClient.Builder().addNetworkInterceptor(interceptor).build()


    @Singleton
    @Provides
    fun provideRetrofit(client: OkHttpClient, moshi: Moshi): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://cognito-idp.us-east-1.amazonaws.com")
            .addCallAdapterFactory(ApiResultCallAdapterFactory)
            .addConverterFactory(ApiResultConverterFactory)
            .addConverterFactory(MoshiConverterFactory.create())
            .client(client)
            .build()
    }
}