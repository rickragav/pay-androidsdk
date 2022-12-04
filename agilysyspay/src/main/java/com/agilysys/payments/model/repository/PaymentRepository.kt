package com.agilysys.payments.model.repository


import com.agilysys.payments.model.CardTokenizeRequest
import com.agilysys.payments.model.NetworkState
import com.agilysys.payments.model.services.ApiService

class PaymentRepository constructor(private val apiService: ApiService) {

    suspend fun performTransaction(payToken:String, headers:Map<String,String>?, cardTokenizeRequest: CardTokenizeRequest?): NetworkState<String> {
        val response = apiService.createPostTokenizeCard(payToken,headers,cardTokenizeRequest)
        return if (response.isSuccessful) {
            val responseBody = response.body()
            if (responseBody != null && response.code() == 200) {
                NetworkState.Success(responseBody)
            } else {
                NetworkState.Error(response)
            }
        } else {
            NetworkState.Error(response)
        }

    }
}