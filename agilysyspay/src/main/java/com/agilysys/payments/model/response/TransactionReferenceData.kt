package com.agilysys.payments.model.response

import com.google.gson.annotations.SerializedName


data class TransactionReferenceData (

  @SerializedName("transactionId" ) var transactionId : String? = null

)