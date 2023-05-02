package com.agilysys.payments.model.services

import com.agilysys.payments.model.CardTokenizeRequest
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.Body
import retrofit2.http.HeaderMap
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.QueryMap

interface ApiService {
//62957de619491e2d499f305a - adyen
    // 62678ea396992f0de7c768ed - fp
    @POST("pay-iframe-service/v1/iFrame/tenants/0/token/62678ea396992f0de7c768ed/{payToken}")
    suspend fun createPostTokenizeCardForCompleteTransaction(
        @Path("payToken") payToken: String?, @HeaderMap headers: Map<String, String>?,@QueryMap queryMap: Map<String,String>,
        @Body dataModal: CardTokenizeRequest?
    ): Response<String>

    @POST("pay-iframe-service/v1/iFrame/tenants/0/token/62957de619491e2d499f305a/")
    suspend fun createPostTokenizeCard(@HeaderMap headers: Map<String, String>?,
        @Body dataModal: CardTokenizeRequest?
    ): Response<String>


    @POST("https://aks-pay-qa.hospitalityrevolution.com/pay-iframe-service/v1/iFrame/tenants/0/62678ea396992f0de7c768ed/")
    suspend fun postIframe(@HeaderMap headers: Map<String, String>?, @QueryMap queryMap: Map<String,String>,
                           @Body dataModal: String?
    ): Response<String>


    companion object{
        var apiService: ApiService? = null

        fun getInstance(): ApiService {
            val gson = GsonBuilder()
                .setLenient()
                .create()
            val interceptor = HttpLoggingInterceptor()
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            val client: OkHttpClient = OkHttpClient.Builder().addInterceptor(interceptor).build()
            if (apiService == null) {
                val retrofit = Retrofit.Builder()
                    .baseUrl("https://aks-pay-qa.hospitalityrevolution.com/")
                    .client(client)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()
                apiService = retrofit.create(ApiService::class.java)
            }
            return apiService!!
        }
    }
}