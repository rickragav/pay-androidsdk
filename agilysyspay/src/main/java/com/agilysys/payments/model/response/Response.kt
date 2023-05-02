package com.agilysys.payments.model.response

import com.google.gson.annotations.SerializedName


data class Response (

  @SerializedName("transactionReferenceData" ) var transactionReferenceData : TransactionReferenceData? = TransactionReferenceData(),
  @SerializedName("transactionResponseData"  ) var transactionResponseData  : TransactionResponseData?  = TransactionResponseData(),
  @SerializedName("cardInfo"                 ) var cardInfo                 : CardInfo?                 = CardInfo(),
  @SerializedName("issuerUrl"                ) var issuerUrl                : String?                   = null,
  @SerializedName("md"                       ) var md                       : String?                   = null,
  @SerializedName("paRequest"                ) var paRequest                : String?                   = null,
  @SerializedName("termUrl"                  ) var termUrl                  : String?                   = null

)