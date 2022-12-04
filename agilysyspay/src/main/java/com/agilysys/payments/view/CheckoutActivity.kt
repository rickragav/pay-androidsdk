package com.agilysys.payments.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.agilysys.payments.R
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val apiService: ApiService = ApiService.getInstance();
        val paymentRepository = PaymentRepository(apiService)
        paymentViewModel =
            ViewModelProvider(this, PaymentViewModelRepository(paymentRepository)).get(
                PaymentViewModel::class.java
            )

        val headerMap: MutableMap<String, String> = HashMap()
        headerMap["Api-Key-Token"] = "";
        headerMap["Content-Type"] = "application/json"
        val cardTokenizeRequest = CardTokenizeRequest()
        cardTokenizeRequest.cardholderName = binding.cardName.toString()
        cardTokenizeRequest.cardNumber = "4111111111111111"
        cardTokenizeRequest.expirationMonth = "03"
        cardTokenizeRequest.expirationYear = "30"
        cardTokenizeRequest.cvv = "737"
        cardTokenizeRequest.token = ""
        cardTokenizeRequest.browserInfo = "{\"userAgent\":\"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36 Edg/107.0.1418.62\"}"
        val billingAddress = BillingAddress()
        billingAddress.addressLine1 = "fww"
        billingAddress.addressLine2 = "fwffwf"
        billingAddress.postalCode = "65000"
        billingAddress.city = "chennai"
        billingAddress.country = "IN"
        cardTokenizeRequest.billingAddress = billingAddress
        paymentViewModel.performTransaction("wwtw", headerMap, cardTokenizeRequest)
    }
}