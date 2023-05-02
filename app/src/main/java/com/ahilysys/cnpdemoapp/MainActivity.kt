package com.ahilysys.cnpdemoapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.agilysys.payments.controller.Callback
import com.agilysys.payments.controller.PayController
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
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity(){

    private lateinit var paymentViewModel: PaymentViewModel
    private lateinit var responseTxt: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val button = findViewById<Button>(R.id.checkout)
        responseTxt = findViewById(R.id.responseText)
        val payController = PayController(this)

        button.setOnClickListener {
            getPostIframeResponse()
        }

        val apiService: ApiService = ApiService.getInstance()
        val paymentRepository = PaymentRepository(apiService)

        paymentViewModel =
            ViewModelProvider(
                this,
                PaymentViewModelRepository(paymentRepository)
            )[PaymentViewModel::class.java]



        paymentViewModel.successResponse.observe(this){
            Log.d("@@@@@@successResponse" , it)
            responseTxt.visibility = View.GONE
            payController.executePayments(it)
        }
        paymentViewModel.errorMessage.observe(this){_message->
            Log.d("@@@@@@errorMessage" , _message)
        }

        paymentViewModel.errorResponse.observe(this){
            Log.d("@@@@@@errorResponse" , it)
        }

    }

    fun getPostIframeResponse() {
        val headerMap = HashMap<String,String>();
        headerMap["Content-Type"] = "application/json"

        val queryMap = HashMap<String,String>();
        queryMap["apiToken"] = getApiToken().toString()
        queryMap["transactionType"] = "auth"

        val body = "{\n" +
                "    \"requestId\": \"3490059b-3cc1-4dfd-b835-0404e49a31ac\",\n" +
                "    \"industryType\": \"eCommerce\",\n" +
                "     \n" +
                "    \"invoiceData\": {\n" +
                "        \"invoiceId\": \"3211212\",\n" +
                "        \"invoiceDate\": \"2018-04-06T03:43:43.068Z\"\n" +
                "    },\n" +
                "    \"transactionData\": {\n" +
                "        \"registerId\": \"1234\",\n" +
                "        \"clerkId\": \"12\",\n" +
                "        \"transactionDate\": \"2018-04-06T03:43:43.068Z\",\n" +
                "        \"referenceCode\": \"3421672\",\n" +
                "        \"transactionAmount\": 10,\n" +
                "        \"tipAmount\": 2,\n" +
                "        \"taxAmount\": 10,\n" +
                "        \"allowPartialTransactionAmount\": true\n" +
                "    },\n" +
                "    \"BillingAddress\": {\n" +
                "        \"Country\": \"US\",\n" +
                "        \"City\": \"city\",\n" +
                "        \"Street\": \"12\",\n" +
                "        \"HouseNumberOrName\": \"12\",\n" +
                "        \"StateOrProvince\": \"State\"\n" +
                "    },\n" +
                "    \"cardPresent\": true,\n" +
                "    \"cardholderPresent\": true,\n" +
                "    \"storedCredential\":{\n" +
                "        \"transactionType\":\"recurringInitial\",\n" +
                "        \"instalmentNumber\":\"1\",\n" +
                "        \"numberOfInstalments\":\"3\"\n" +
                "    }\n" +
                "}\n"

        paymentViewModel.performPostIframe(headerMap,queryMap,body)
    }

    fun getApiToken():String? {
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
        var issuccess = true;
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

                token = response.toString()
                val jsonResponse = Gson().fromJson(token, JsonObject::class.java)
                token = jsonResponse["token"].toString().replace("\"", "")
                issuccess = false;

            }
            while (issuccess){
                Thread.sleep(1000)
            }
            return token;
        }catch (e: Exception){
            return null
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 200 && resultCode == RESULT_OK) {
            val response = data?.getStringExtra("response")
            if (response != null) {
                responseTxt.visibility = View.VISIBLE
                responseTxt.text = response
                Log.d("parentResponse text", response)
            }
        }

    }

}