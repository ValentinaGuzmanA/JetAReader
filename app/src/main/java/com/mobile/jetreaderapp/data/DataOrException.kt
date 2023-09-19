package com.mobile.jetreaderapp.data

class DataOrException<T,Boolean,E : Exception> (
    var data : T? =null,
    var loading : Boolean? = null,
    var error : E? = null
        )