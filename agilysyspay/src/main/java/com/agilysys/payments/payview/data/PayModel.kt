package com.agilysys.payments.payview.data

import com.agilysys.payments.payview.commons.CardType


data class PayModel(
    var cardOwnerName:String="name surname",
    var cardMonth:String="03",
    var cardYear:String="30",
    var cardNo:String="4111111111111111",
    var cardCv:String="123",
    var cardType: CardType = CardType.UNDEFINED
)