package com.agilysys.payments.model.response

import com.google.gson.annotations.SerializedName


data class TransactionResponseData (

  @SerializedName("subTotalAmount" ) var subTotalAmount : Int? = null,
  @SerializedName("tipAmount"      ) var tipAmount      : Int? = null

)