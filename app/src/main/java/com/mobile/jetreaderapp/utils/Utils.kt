package com.mobile.jetreaderapp.utils

import android.content.Context
import android.widget.Toast
import com.google.firebase.Timestamp
import java.text.DateFormat

fun showToast(context: Context, msg:String){
    Toast.makeText(context,msg, Toast.LENGTH_LONG).show()
}
fun formatDate(timestamp: Timestamp): String {
    return DateFormat.getDateInstance().format(timestamp.toDate()).toString().split(",")[0]
}