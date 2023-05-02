package com.agilysys.payments.viewmodel

import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.agilysys.payments.model.CardTokenizeRequest
import com.agilysys.payments.model.NetworkState
import com.agilysys.payments.model.repository.PaymentRepository
import com.google.gson.Gson
import kotlinx.coroutines.*
import retrofit2.Response

class PaymentViewModel constructor(private val paymentRepository: PaymentRepository) : ViewModel() {
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String>
        get() = _errorMessage


    val loading = MutableLiveData<Boolean>()
    var job: Job? = null

    private val _error = MutableLiveData<Response<String>>()
    val error: LiveData<Response<String>>
        get() = _error

    var successResponse = MutableLiveData<String>()

    var errorResponse = MutableLiveData<String>()

    private val exceptionHandler = CoroutineExceptionHandler { context, throwable ->
        CoroutineScope(context).launch {
            withContext(Dispatchers.Main){
                onError("Exception handled: ${throwable.localizedMessage}")
            }
        }
    }

    fun performTransaction(payToken:String, headers:Map<String,String>?,queryMap:Map<String,String>, cardTokenizeRequest: CardTokenizeRequest?){
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = paymentRepository.performTransaction(payToken, headers, queryMap,cardTokenizeRequest)
            withContext(Dispatchers.Main){
                when(response){
                    is NetworkState.Success -> {
                        successResponse.postValue(response.data)
                    }
                    is NetworkState.Error -> {
                        if (response.response.errorBody() != null) {
                            val payResponse = try {
                                response.response.errorBody()!!.string()
                            } catch (e: Exception) {
                                e.localizedMessage as String
                            }
                            errorResponse.postValue(payResponse)
                        }
                    }
                }
            }

        }
    }

    fun performTokenCreation(headers:Map<String,String>?, cardTokenizeRequest: CardTokenizeRequest?){
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = paymentRepository.performTokenCreation(headers, cardTokenizeRequest)
            withContext(Dispatchers.Main){
                when(response){
                    is NetworkState.Success -> {
                        successResponse.postValue(response.data)
                    }
                    is NetworkState.Error -> {
                        if (response.response.code() == 401) {
                            //movieList.postValue(NetworkState.Error())
                            //  _errorMessage.value = response.response.errorBody().toString()
                            _error.value = response.response;
                        } else {
                            //movieList.postValue(NetworkState.Error)
                            // _errorMessage.value = response.response.errorBody().toString()
                            _error.value = response.response;
                        }
                    }
                }
            }

        }
    }

    fun performPostIframe(headers:Map<String,String>?,queryMap:Map<String,String>, request: String?) {
        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = paymentRepository.performPostIframe(headers,queryMap, request)
            Log.d("httpResponse" , Gson().toJson(response))
            withContext(Dispatchers.Main){
                when(response){
                    is NetworkState.Success -> {
                        successResponse.postValue(response.data)
                    }
                    is NetworkState.Error -> {
                        if (response.response.errorBody() != null) {
                            val payResponse = try {
                                response.response.errorBody()!!.string()
                            } catch (e: Exception) {
                                e.localizedMessage as String
                            }
                            errorResponse.postValue(payResponse)
                        }
                    }
                }
            }

        }
    }



    fun onError(message: String) {
        _errorMessage.value = message
        loading.value = false
    }
}