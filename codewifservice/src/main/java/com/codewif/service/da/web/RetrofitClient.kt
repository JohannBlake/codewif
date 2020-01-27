package com.codewif.service.da.web

import com.codewif.service.da.web.URLs.Companion.CODEWIF_BASE_URL
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClient {
    companion object {

        var requestHeaders = mutableMapOf<String, MutableMap<String, String>>()

        fun createRetrofitClient(): CodewifWebAPI {
            return Retrofit.Builder()
                .baseUrl(CODEWIF_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
                .client(initializeRetrofit())
                .build().create(CodewifWebAPI::class.java)
        }


        private fun initializeRetrofit(): OkHttpClient {

            val httpClient = OkHttpClient.Builder()

            httpClient.addInterceptor { chain ->
                val originalRequest = chain.request()
                val requestBuilder = originalRequest.newBuilder()

                if (requestHeaders.containsKey(originalRequest.url().toString())) {
                    val headers = requestHeaders.getValue(originalRequest.url().toString())

                    if (headers.isNotEmpty()) {
                        headers.forEach { entry ->
                            requestBuilder.header(entry.key, entry.value)
                        }
                    }
                }

                val request = requestBuilder.build()
                chain.proceed(request)
            }

            return httpClient.build()
        }
    }
}