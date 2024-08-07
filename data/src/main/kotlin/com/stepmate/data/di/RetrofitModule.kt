package com.stepmate.data.di

import com.google.gson.GsonBuilder
import com.stepmate.data.BuildConfig.SERVER_IP
import com.stepmate.data.remote.api.AuthApi
import com.stepmate.data.remote.utils.ResultCallAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.create
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object RetrofitModule {

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class RetrofitAuth

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class OkHttpClientAuth

    @OkHttpClientAuth
    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }

    @RetrofitAuth
    @Singleton
    @Provides
    fun provideRetrofit(@OkHttpClientAuth okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .addCallAdapterFactory(ResultCallAdapterFactory())
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .baseUrl(SERVER_IP)
            .client(okHttpClient)
            .build()
    }

    @Singleton
    @Provides
    fun createStepMateService(@RetrofitAuth retrofit: Retrofit): AuthApi = retrofit.create()
}
