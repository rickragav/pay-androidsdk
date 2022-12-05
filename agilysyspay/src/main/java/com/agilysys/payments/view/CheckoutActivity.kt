package com.agilysys.payments.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.agilysys.payments.databinding.ActivityCheckoutBinding
import com.agilysys.payments.model.BillingAddress
import com.agilysys.payments.model.CardTokenizeRequest
import com.agilysys.payments.model.repository.PaymentRepository
import com.agilysys.payments.model.services.ApiService
import com.agilysys.payments.viewmodel.PaymentViewModel
import com.agilysys.payments.viewmodel.PaymentViewModelRepository
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL


class CheckoutActivity : AppCompatActivity() {

    private lateinit var paymentViewModel: PaymentViewModel
    private lateinit var binding: ActivityCheckoutBinding
    private lateinit var payResponse: String
    private var apiTokenLD = MutableLiveData<String>()
    private var payTokenLD = MutableLiveData<String>()
    private var iframeTokenLD = MutableLiveData<String>()
    private var apiToken = ""
    private var payToken = ""
    private var getURL = ""
    private var iframeToken = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.title = "Payments"
        setSupportActionBar(binding.toolbar)
        val apiService: ApiService = ApiService.getInstance()
        val paymentRepository = PaymentRepository(apiService)
        paymentViewModel =
            ViewModelProvider(this, PaymentViewModelRepository(paymentRepository)).get(
                PaymentViewModel::class.java
            )

        binding.cardNumber.setText("4111111111111111")
        binding.progressDialog.visibility = View.VISIBLE

        getApiToken()

        paymentViewModel.loading.observe(this) { isLoading ->
            if (isLoading) {
                binding.progressDialog.visibility = View.VISIBLE
            } else {
                binding.progressDialog.visibility = View.GONE
            }
        }

        paymentViewModel.errorMessage.observe(this){_message->
            payResponse = _message
            val data = Intent()
            data.putExtra("response", payResponse)
            setResult(RESULT_OK, data)
            finish()
        }

        paymentViewModel.error.observe(this){ response->
            if (response.errorBody() != null) {
                payResponse = try {
                    response.errorBody()!!.string()
                } catch (e: Exception) {
                    e.localizedMessage as String
                }
                val data = Intent()
                data.putExtra("response", payResponse)
                setResult(RESULT_OK, data)
                finish()
            }
        }

        paymentViewModel.responseString.observe(this) { response ->
            payResponse = response
            val intent = Intent(this, WebviewActivity::class.java)
            intent.putExtra("cardresponse", payResponse)
            this.startActivityForResult(intent, 205)
        }

        apiTokenLD.observe(this){ token->
            System.out.println("apiToken $token")
            apiToken = token
            getPayToken()
        }

        payTokenLD.observe(this){ token->
            System.out.println("payToken $token")
            payToken = token
            getIframeToken()
        }

        iframeTokenLD.observe(this){ token->
            System.out.println("iframeToken $token")
            iframeToken = token

            if (iframeToken == "NotValid" || payToken == "NotValid" || apiToken == "NotValid"){
                val data = Intent()
                data.putExtra("response", "Please try again")
                setResult(RESULT_OK, data)
                finish()
            }
            binding.progressDialog.visibility  = View.GONE
            binding.cardll.visibility = View.VISIBLE

        }

        binding.buttonOk.setOnClickListener {
            binding.progressDialog.visibility = View.VISIBLE
            getIframeTokenCreation()

        }


    }

    fun getIframeTokenCreation(){
        val headerMap: MutableMap<String, String> = HashMap()
        headerMap["Api-Key-Token"] = apiToken
        headerMap["Content-Type"] = "application/json"
        val cardTokenizeRequest = CardTokenizeRequest()
        cardTokenizeRequest.cardholderName = binding.cardName.toString()
        cardTokenizeRequest.cardNumber = binding.cardNumber.text.toString()
        cardTokenizeRequest.expirationMonth = "03"
        cardTokenizeRequest.expirationYear = "2030"
        cardTokenizeRequest.cvv = "737"
        cardTokenizeRequest.token = iframeToken
        cardTokenizeRequest.browserInfo = "{\"userAgent\":\"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36 Edg/107.0.1418.62\"}"
        val billingAddress = BillingAddress()
        billingAddress.addressLine1 = "fww"
        billingAddress.addressLine2 = "fwffwf"
        billingAddress.postalCode = "65000"
        billingAddress.city = "chennai"
        billingAddress.country = "IN"
        cardTokenizeRequest.billingAddress = billingAddress

        paymentViewModel.performTransaction(payToken, headerMap, cardTokenizeRequest)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        try {
            super.onActivityResult(requestCode, resultCode, data)
            if (requestCode == 205 && resultCode == RESULT_OK) {
                setResult(RESULT_OK, data)
                finish()
            }
        } catch (_: java.lang.Exception) {

        }
    }



    fun getApiToken() {
        val environment = "aks-pay-qa"
        var payHost = ""
        if (environment.equals("Production", ignoreCase = true)) {
            payHost = "https://core.rguest.com"
        } else if (environment.equals("aks-pay-qa", ignoreCase = true)) {
            payHost = "https://core-qa.hospitalityrevolution.com"
        } else if (environment.equals("aks-pay-qaint", ignoreCase = true)) {
            payHost = "https://core-qaint.hospitalityrevolution.com"
        } else if (environment.equals("aks-pay-perf", ignoreCase = true)) {
            payHost = "https://core-qaint.hospitalityrevolution.com"
        } else if (environment.equals("aks-pay-prod-emea", ignoreCase = true)) {
            payHost = "https://core.rguest.eu"
        } else if (environment.equals("aks-pay-prod-us", ignoreCase = true)) {
            payHost = "https://core.rguest.com"
        } else if (environment.equals("aks-pay-prod-apac", ignoreCase = true)) {
            payHost = "http://aks-core-pci-prod-seasia.rguest.internal"
        } else if (environment.equals("aks-pay-sales", ignoreCase = true)) {
            payHost = "https://aks-core-sales.hospitalityrevolution.com"
        } else if (environment.equals("aks-pay-dev", ignoreCase = true)) {
            payHost = "https://core-qaint.hospitalityrevolution.com"
        } else if (environment.equals("aks-pay-devint", ignoreCase = true)) {
            payHost = "https://aks-core-devint.hospitalityrevolution.com"
        } else if (environment.equals("production-dr", ignoreCase = true)) {
            payHost = "https://core.rguest.com"
        } else if (environment.equals("aks-pay-uat", ignoreCase = true)) {
            payHost = "https://aks-core-uat.hospitalityrevolution.com"
        } else if (environment.equals("aks-pay-release", ignoreCase = true)) {
            payHost = "https://aks-core-release.hospitalityrevolution.com"
        }
        var token = ""
        try {
            CoroutineScope(Dispatchers.IO).launch {
                val url =
                    String.format("$payHost/user-service/apiUser/tenants/0/authenticateApiUser")
                val urlConnection = URL(url)
                val httpURLConnection = urlConnection.openConnection() as HttpURLConnection
                httpURLConnection.requestMethod = "POST"
                httpURLConnection.setRequestProperty("Content-Type","application/json")
                val jsonRequestString = "{\"clientId\" : \"" + "62957de619491e2d499f305a" + "\" , " + "\"nonce\" : \"" + "330cbaaf59cb87536c80bafd06d6ab6b450a2db7e880a62f4b16ab702e189a7e3d9d963696d4720e8e549acbfe87f19d2fad76e0613dcf3d4b5e693a93c0b758" + "\", \"apiUserName\" : \"" + "44d96af1-a135-419a-b8a8-d81caf5a7ed9" + "\"}"
                httpURLConnection.doOutput = true
                val os: OutputStream = httpURLConnection.outputStream
                val osw = OutputStreamWriter(os, "UTF-8")
                osw.write(jsonRequestString)
                osw.flush()
                osw.close()
                os.close()
                httpURLConnection.connect()
                // For POST only - END

                val `in` = BufferedReader(InputStreamReader(httpURLConnection.inputStream))
                var inputLine: String?
                val response = StringBuffer()

                while (withContext(Dispatchers.IO) {
                        `in`.readLine()
                    }.also { inputLine = it } != null) {
                    response.append(inputLine)
                }
                withContext(Dispatchers.IO) {
                    `in`.close()
                }
                println(response.toString())

                withContext(Dispatchers.Main){
                    token = response.toString()
                    val jsonResponse = Gson().fromJson(token, JsonObject::class.java)
                    apiTokenLD.value  = jsonResponse["token"].toString().replace("\"", "")
                }
            }
        }catch (e: Exception){
            apiTokenLD.value  =  "NotValid"
        }
    }
    fun getPayToken() {
        val payHost = "https://aks-pay-qa.hospitalityrevolution.com"
        try {
            CoroutineScope(Dispatchers.IO).launch {
                val url =
                    String.format("$payHost/pay-iframe-service/v1/iFrame/tenants/0/62957de619491e2d499f305a" +
                            "?apiToken=$apiToken&submit=Pay&style=https://authorize.rguest.com" +
                            "/AuthorizeStyles/Authorize_Style.css&doVerify=true&version=3" +
                            "&transactionType=sale&withToken=true")
                val urlConnection = URL(url)
                val httpURLConnection = urlConnection.openConnection() as HttpURLConnection
                httpURLConnection.requestMethod = "POST"
                httpURLConnection.setRequestProperty("Content-Type","application/json")
                val jsonRequestString = "{\n" +
                        "    \"requestId\": \"5ef05c68-3803-4ae4-84a8-bbc050fe8564\",\n" +
                        "    \"gatewayId\": \"adyen\",\n" +
                        "    \"industryType\": \"eCommerce\",\n" +
                        "    \"invoiceData\": {\n" +
                        "        \"invoiceId\": \"Testvoid123\",\n" +
                        "        \"invoiceDate\": \"2022-04-19T11:17:11.288+05:30\"\n" +
                        "    },\n" +
                        "    \"apiOptions\": [\n" +
                        "        \"ALLOWPARTIALAUTH\"\n" +
                        "    ],\n" +
                        "    \"billingAddress\": {\n" +
                        "        \"addressLine1\": \"89 Main Street\",\n" +
                        "        \"addressLine2\": \"xxxxxxx\",\n" +
                        "        \"city\": \"xxxxxxx\",\n" +
                        "        \"country\": \"xxx\",\n" +
                        "        \"firstName\": \"xxxx\",\n" +
                        "        \"lastName\": \"xxxxx\",\n" +
                        "        \"middleName\": \"x\",\n" +
                        "        \"phoneNumber\": \"xxxxxxxxxxxx\",\n" +
                        "        \"postalCode\": \"89000\",\n" +
                        "        \"state\": \"xx\"\n" +
                        "    },\n" +
                        "    \"transactionData\": {\n" +
                        "        \"registerId\": \"1876\",\n" +
                        "        \"clerkId\": \"12\",\n" +
                        "        \"transactionDate\": \"2022-04-19T11:17:11.288+05:30\",\n" +
                        "        \"referenceCode\": \"\",\n" +
                        "        \"transactionAmount\": 10.00,\n" +
                        "        \"tipAmount\": 1.00,\n" +
                        "        \"taxAmount\": 0.00,\n" +
                        "        \"allowPartialTransactionAmount\": true\n" +
                        "    },\n" +
                        "    \"currencyCode\":\"usd\",\n" +
                        "    \"description\": \"Test\"\n" +
                        "}"
                httpURLConnection.doOutput = true
                val os: OutputStream = httpURLConnection.outputStream
                val osw = OutputStreamWriter(os, "UTF-8")
                osw.write(jsonRequestString)
                osw.flush()
                osw.close()
                os.close()
                httpURLConnection.connect()
                // For POST only - END

                val `in` = BufferedReader(InputStreamReader(httpURLConnection.inputStream))
                var inputLine: String?
                val response = StringBuffer()

                while (withContext(Dispatchers.IO) {
                        `in`.readLine()
                    }.also { inputLine = it } != null) {
                    response.append(inputLine)
                }
                withContext(Dispatchers.IO) {
                    `in`.close()
                }
                println(response.toString())

                withContext(Dispatchers.Main){

                   val jsonResponse = Gson().fromJson(response.toString(), JsonObject::class.java)
                    getQueryParameters(jsonResponse["uri"].toString())
                }
            }
        }catch (e: Exception){
            payTokenLD.value  =  "NotValid"
        }
    }
    fun getIframeToken() {
        try {
            CoroutineScope(Dispatchers.IO).launch {
                val urlConnection = URL(getURL)
                val httpURLConnection = urlConnection.openConnection() as HttpURLConnection
                httpURLConnection.requestMethod = "GET"
                httpURLConnection.setRequestProperty("User-Agent", "Mozilla/5.0");
                httpURLConnection.connect()
                val responseCode = httpURLConnection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val `in` = BufferedReader(InputStreamReader(httpURLConnection.inputStream))
                    var inputLine: String?
                    val response = StringBuffer()

                    while (withContext(Dispatchers.IO) {
                            `in`.readLine()
                        }.also { inputLine = it } != null) {
                        response.append(inputLine)
                    }
                    withContext(Dispatchers.IO) {
                        `in`.close()
                    }
                    println(response.toString())

                    withContext(Dispatchers.Main) {
                        val token = response.split("name=\"token\" value=")[1].split(" />").get(0)
                        iframeTokenLD.value = token.substring(1,token.length-1)
                    }
                }else {
                    System.out.println("GET request not worked");
                }
            }
        }catch (e: Exception){
            iframeTokenLD.value  =  "NotValid"
        }
    }

    fun getQueryParameters(url: String){
        try {
            getURL = url.substring(1,url.length-1)
            val queryParams = getURL.split("?")[1].split("&")
            queryParams.forEach { value ->
                if (value.contains("payToken=")){
                    payTokenLD.value = value.split("payToken=")[1]
                }
            }

        }catch (e:Exception){
            payTokenLD.value  =  "NotValid"
        }
    }
}