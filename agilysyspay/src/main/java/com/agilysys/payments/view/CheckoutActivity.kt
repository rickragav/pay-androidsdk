package com.agilysys.payments.view

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.agilysys.payments.R
import com.agilysys.payments.controller.Callback
import com.agilysys.payments.creditcardformatter.OtherCardTextWatcher
import com.agilysys.payments.databinding.ActivityCheckoutBinding
import com.agilysys.payments.model.BillingAddress
import com.agilysys.payments.model.CardTokenizeRequest
import com.agilysys.payments.model.PayRequest
import com.agilysys.payments.model.repository.PaymentRepository
import com.agilysys.payments.model.response.Response
import com.agilysys.payments.model.services.ApiService
import com.agilysys.payments.payview.Payview
import com.agilysys.payments.payview.data.PayModel
import com.agilysys.payments.viewmodel.PaymentViewModel
import com.agilysys.payments.viewmodel.PaymentViewModelRepository
import com.github.razir.progressbutton.attachTextChangeAnimator
import com.github.razir.progressbutton.bindProgressButton
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import com.google.android.recaptcha.Recaptcha
import com.google.android.recaptcha.RecaptchaAction
import com.google.android.recaptcha.RecaptchaClient
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
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
    private var getURL = ""
    private var iframeToken = ""
    var isToken: Boolean = false
    private var getIframeURL = ""
    private lateinit var recaptchaClient: RecaptchaClient
    private var isGetIframe = false


    private var payRequest:String? = ""
    private var payToken:String? = ""
    private var cardNumber:String? = ""
    private var cardCvv:String? = ""
    private var cardMonth:String? = ""
    private var cardYear:String? = ""
    private var cardHolderName:String? = ""
    lateinit var payRequestRes:PayRequest


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.title = "Payments"

        val apiService: ApiService = ApiService.getInstance()
        val paymentRepository = PaymentRepository(apiService)

        paymentViewModel =
            ViewModelProvider(
                this,
                PaymentViewModelRepository(paymentRepository)
            )[PaymentViewModel::class.java]


        val intent = intent
        isToken = intent.getBooleanExtra("isToken",false)
        payRequest = intent.getStringExtra("payRequest")

        payRequestRes = Gson().fromJson(payRequest,PayRequest::class.java)

        getQueryParameters(payRequestRes.uri.toString())

        init()
    }

    private fun init() {
        val payButton = binding.payview.findViewById<Button>(R.id.btn_pay)

        bindProgressButton(payButton)

        payButton.attachTextChangeAnimator {
            fadeInMills = 200
            fadeOutMills = 200
        }

        binding.payview.setOnDataChangedListener(object:Payview.OnChangelistener{
            override fun onChangelistener(payModel: PayModel?, isFillAllComponents: Boolean) {
                cardHolderName = payModel?.cardOwnerName
                cardNumber = payModel?.cardNo
                cardMonth = payModel?.cardMonth
                cardYear = payModel?.cardYear
                cardCvv = payModel?.cardCv
            }
        })

        binding.payview.setPayOnclickListener{
            cardHolderName = "Rakesh"
            cardNumber = "4111111111111111"
            cardMonth = "03"
            cardYear = "2030"
            cardCvv = "737"
            if (validateCardDetails()){
                showProgressCenter(payButton)
                getIframeTokenCreation()
                Log.d("cardDetails" ,"${cardHolderName}-${cardNumber}-${cardMonth}-${cardYear}-${cardCvv}")
            }
        }


        paymentViewModel.successResponse.observe(this){
            val response = Gson().fromJson(it,Response::class.java)
            if (!response?.issuerUrl.isNullOrEmpty()){

                val webviewIntent = Intent(this,WebviewActivity::class.java)
                webviewIntent.putExtra("3dsRequest",it)
                startActivityForResult(webviewIntent,205)

            }else{
                val data = Intent()
                data.putExtra("response", it)
                setResult(RESULT_OK, data)
                finish()
            }

        }
        paymentViewModel.errorMessage.observe(this){_message->
            //handle data here
            Log.d("@@@@@@finalRes" , _message)
        }

        paymentViewModel.errorResponse.observe(this){
            //handle data here
            Log.d("@@@@@@finalRes" , it)
        }

    }

    fun validateCardDetails():Boolean{
        if (cardHolderName.isNullOrEmpty()){
            showToast("CardHolderName is empty")
            return false
        }
        if (cardNumber.isNullOrEmpty()){
            showToast("CardNumber is empty")
            return false
        }
        if (cardMonth.isNullOrEmpty()){
            showToast("CardMonth is empty")
            return false
        }
        if (cardYear.isNullOrEmpty()){
            showToast("CardYear is empty")
            return false
        }
        if (cardCvv.isNullOrEmpty()){
            showToast("CardCvv is empty")
            return false
        }

        return true

    }

    fun showToast(msg:String){
        Toast.makeText(this,msg,Toast.LENGTH_LONG).show()
    }

    private fun showProgressCenter(button: Button) {
        button.showProgress {
            buttonTextRes = R.string.empty
            progressColor = this@CheckoutActivity.resources.getColor(R.color.colorPrimary)
        }
        button.isEnabled = false
        Handler().postDelayed({
            button.isEnabled = true
            button.hideProgress("Completed")
        }, 5000)
    }

    private fun testGoogleCAptcha() {
        binding.cardll.visibility = View.VISIBLE
//        binding.buttonOk.setOnClickListener {view->
//            SafetyNet.getClient(view.context).verifyWithRecaptcha("6Lff43ojAAAAAJw_AquMGwTa2bNxyZQKyW_-xv3F")
//                .addOnSuccessListener {
//                    Toast.makeText(view.context,it.tokenResult,Toast.LENGTH_LONG).show()
//                    println("captcahkey " + it.tokenResult)
//                }
//                .addOnFailureListener {
//                    Toast.makeText(view.context,it.localizedMessage,Toast.LENGTH_LONG).show()
//                }
//        }
    }

    suspend fun enterpriseCaptcha(){
        Recaptcha.getClient(application,"")
                .onSuccess {
                    recaptchaClient = it
                    System.out.println("success")
                    binding.cardll.visibility = View.VISIBLE
                }
                .onFailure {
                    System.out.println("fail " + it.localizedMessage)
                }
        binding.buttonOk.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                executeLoginAction()
            }
        }
    }

    private suspend fun executeLoginAction() {
        recaptchaClient
                .execute(RecaptchaAction.LOGIN)
                .onSuccess { token ->
                    // Handle success ...
                    // See "What's next" section for instructions
                    // about handling tokens.
                    System.out.println("token $token")


                }
                .onFailure { exception ->
                    // Handle communication errors ...
                    // See "Handle communication errors" section
                }

    }

    fun initializeIframeAPI(){
        val apiService: ApiService = ApiService.getInstance()
        val paymentRepository = PaymentRepository(apiService)
        paymentViewModel =
            ViewModelProvider(this, PaymentViewModelRepository(paymentRepository)).get(
                PaymentViewModel::class.java
            )

        binding.cardNumberformatter.addTextChangedListener(OtherCardTextWatcher(binding.cardNumberformatter))
        binding.cardNumberformatter.setText("4111111111111111")
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

        paymentViewModel.successResponse.observe(this) { response ->
            payResponse = response
            val jsonObject = JsonParser().parse(payResponse) as JsonObject
            val isTokenResponse = jsonObject["token"]
            if (isTokenResponse == null){
                val intent = Intent(this, WebviewActivity::class.java)
                intent.putExtra("cardresponse", payResponse)
                this.startActivityForResult(intent, 205)
            }else{
                //handle data here
                val datass = Intent()
                datass.putExtra("response", payResponse)
                setResult(RESULT_OK, datass)
                finish()
            }

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
            if (isGetIframe){
                val intent = Intent(this, IframeActivity::class.java)
                println("getiframeurl " + getIframeURL)
                intent.putExtra("getIframeURL", getIframeURL)
                this.startActivityForResult(intent, 205)
            }else{
                binding.cardll.visibility = View.VISIBLE
            }


        }

        binding.buttonOk.setOnClickListener {
            binding.progressDialog.visibility = View.VISIBLE
            hideKeybaord(binding.root)
            getIframeTokenCreation()

        }
    }

    fun getIframeTokenCreation(){
        val headerMap: MutableMap<String, String> = HashMap()
        headerMap["Api-Key-Token"] = apiToken
        headerMap["Content-Type"] = "application/json"
        val cardTokenizeRequest = CardTokenizeRequest()
        cardTokenizeRequest.cardholderName = cardHolderName
        cardTokenizeRequest.cardNumber = cardNumber?.trim()
        cardTokenizeRequest.expirationMonth = cardMonth
        cardTokenizeRequest.expirationYear = cardYear
        cardTokenizeRequest.cvv = cardCvv
        cardTokenizeRequest.token = getIframeToken()
        cardTokenizeRequest.browserInfo = "{\"userAgent\":\"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36 Edg/107.0.1418.62\"}"
        val billingAddress = BillingAddress()
        billingAddress.addressLine1 = "fww"
        billingAddress.addressLine2 = "fwffwf"
        billingAddress.postalCode = "65000"
        billingAddress.city = "chennai"
        billingAddress.country = "IN"
        cardTokenizeRequest.billingAddress = billingAddress

        val queryMap = HashMap<String,String>()
        queryMap["withToken"] = "true"
        queryMap["transactionType"] = "auth"

//        if (isToken){
//            paymentViewModel.performTokenCreation(headerMap,cardTokenizeRequest)
//        }else{
            paymentViewModel.performTransaction(payToken!!, headerMap,queryMap, cardTokenizeRequest)
        //}
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
                    getIframeURL = jsonResponse["uri"].toString()
                    getQueryParameters(jsonResponse["uri"].toString())
                }
            }
        }catch (e: Exception){
            payTokenLD.value  =  "NotValid"
        }
    }
    fun getIframeToken():String {
        var issuccess = true;
        var token = "";
        try {
            CoroutineScope(Dispatchers.IO).launch {
                val urlConnection = URL(payRequestRes.uri)
                val httpURLConnection = urlConnection.openConnection() as HttpURLConnection
                httpURLConnection.requestMethod = "GET"
                httpURLConnection.setRequestProperty("User-Agent", "Mozilla/5.0")
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

                    Log.d("responseiframeToken" , response.toString())

                    val tkn = response.split("name=\"token\" value=")[1].split(" />")[0]
                    token = tkn.substring(1,tkn.length-1)

                    issuccess = false

                } else {
                    println("GET request not worked")
                }
            }
            while (issuccess){
                Thread.sleep(1000)
            }
            return token;
        }catch (e: Exception){
            return ""
        }
    }

    fun getQueryParameters(url: String){
        Log.d("query", url)
        try {
            getURL = url.substring(1,url.length)
            val queryParams = getURL.split("?")[1].split("&")
            queryParams.forEach { value ->
                Log.d("query", value)
                if (value.contains("payToken=")){
                    payToken = value.split("payToken=")[1]
                }
                if (value.contains("apiToken=")){
                    apiToken = value.split("apiToken=")[1]
                }
            }

        }catch (e:Exception){
            Log.d("payToken", e.message.toString())
        }
    }

    private fun hideKeybaord(v: View) {
        val inputMethodManager: InputMethodManager =
            getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(v.applicationWindowToken, 0)
    }
}