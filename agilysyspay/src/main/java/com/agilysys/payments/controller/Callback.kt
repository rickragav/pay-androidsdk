package com.agilysys.payments.controller

interface Callback  {

    fun onSuccess(message: String);
    fun onFailure(message: String);
}