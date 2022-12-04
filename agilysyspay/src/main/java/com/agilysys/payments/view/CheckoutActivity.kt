package com.agilysys.payments.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.agilysys.payments.databinding.ActivityCheckoutBinding
import com.agilysys.payments.model.BillingAddress
import com.agilysys.payments.model.CardTokenizeRequest
import com.agilysys.payments.model.repository.PaymentRepository
import com.agilysys.payments.model.services.ApiService
import com.agilysys.payments.viewmodel.PaymentViewModel
import com.agilysys.payments.viewmodel.PaymentViewModelRepository

class CheckoutActivity : AppCompatActivity() {

    private lateinit var paymentViewModel: PaymentViewModel
    private lateinit var binding: ActivityCheckoutBinding
    private lateinit var payResponse: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.title = "Enter card details"
        setSupportActionBar(binding.toolbar)
        val apiService: ApiService = ApiService.getInstance()
        val paymentRepository = PaymentRepository(apiService)
        paymentViewModel =
            ViewModelProvider(this, PaymentViewModelRepository(paymentRepository)).get(
                PaymentViewModel::class.java
            )

        val headerMap: MutableMap<String, String> = HashMap()
        headerMap["Api-Key-Token"] = "352981da-8df7-4e2d-8c28-5edbe9197fc9";
        headerMap["Content-Type"] = "application/json"
        val cardTokenizeRequest = CardTokenizeRequest()
        cardTokenizeRequest.cardholderName = binding.cardName.toString()
        cardTokenizeRequest.cardNumber = "4111111111111111"
        cardTokenizeRequest.expirationMonth = "03"
        cardTokenizeRequest.expirationYear = "2030"
        cardTokenizeRequest.cvv = "737"
        cardTokenizeRequest.token = "5af345bd-b4ec-460c-a5e3-746aebcdf2a1"
        cardTokenizeRequest.browserInfo = "{\"userAgent\":\"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36 Edg/107.0.1418.62\"}"
        val billingAddress = BillingAddress()
        billingAddress.addressLine1 = "fww"
        billingAddress.addressLine2 = "fwffwf"
        billingAddress.postalCode = "65000"
        billingAddress.city = "chennai"
        billingAddress.country = "IN"
        cardTokenizeRequest.billingAddress = billingAddress

        paymentViewModel.loading.observe(this) { isLoading ->
            if (isLoading) {
                binding.progressDialog.visibility = View.VISIBLE
            } else {
                binding.progressDialog.visibility = View.GONE
            }
        }

        paymentViewModel.errorMessage.observe(this){_message->
            payResponse = _message;
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

        binding.buttonOk.setOnClickListener {
            binding.progressDialog.visibility = View.VISIBLE
            paymentViewModel.performTransaction("746ecdeb263148d5992fb8a23d7992d9", headerMap, cardTokenizeRequest)
        }


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
}