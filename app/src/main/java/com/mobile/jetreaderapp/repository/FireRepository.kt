package com.mobile.jetreaderapp.repository

import android.util.Log
import com.google.firebase.firestore.Query
import com.mobile.jetreaderapp.data.DataOrException
import com.mobile.jetreaderapp.model.MBook
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FireRepository @Inject constructor(private var query : Query) {

    suspend fun getAllBooksFromDatabase() : DataOrException<List<MBook>,Boolean,Exception>{
        val dataOrException = DataOrException<List<MBook>,Boolean,Exception>()
        try {
            dataOrException.loading=true
            dataOrException.data = query.get().await().documents.map { documentSnapshot ->
                documentSnapshot.toObject(MBook::class.java)!!
            }
            if(!dataOrException.data.isNullOrEmpty()) dataOrException.loading=false

        }catch (e : Exception){
            dataOrException.error = e
        }
        return dataOrException
    }
}