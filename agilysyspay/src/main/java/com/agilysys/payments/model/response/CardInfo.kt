package com.agilysys.payments.model.response

import com.google.gson.annotations.SerializedName


data class CardInfo (

  @SerializedName("cardIssuer"          ) var cardIssuer          : String? = null,
  @SerializedName("accountNumberMasked" ) var accountNumberMasked : String? = null,
  @SerializedName("expirationYearMonth" ) var expirationYearMonth : String? = null,
  @SerializedName("cardHolderName"      ) var cardHolderName      : String? = null,
  @SerializedName("entryMode"           ) var entryMode           : String? = null,
  @SerializedName("cardType"            ) var cardType            : String? = null,
  @SerializedName("correlationId"       ) var correlationId       : String? = null

)