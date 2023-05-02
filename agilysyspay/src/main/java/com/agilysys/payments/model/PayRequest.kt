package com.agilysys.payments.model

import com.google.gson.annotations.SerializedName

data class PayRequest(
    @SerializedName("uri" ) var uri : String? = null
)