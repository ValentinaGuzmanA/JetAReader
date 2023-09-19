package com.mobile.jetreaderapp.model


import com.google.gson.annotations.SerializedName

data class ListPriceX(
    @SerializedName("amountInMicros")
    val amountInMicros: Double,
    @SerializedName("currencyCode")
    val currencyCode: String
)