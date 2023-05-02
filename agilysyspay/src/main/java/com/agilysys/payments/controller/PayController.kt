package com.agilysys.payments.controller

import android.app.Activity
import android.content.Intent
import com.agilysys.payments.view.CheckoutActivity

class PayController(private val activity: Activity) {

    fun executePayments(request: String) {
        val intent = Intent(activity, CheckoutActivity::class.java)
        intent.putExtra("payRequest", request)
        activity.startActivityForResult(intent, 200)
    }
}