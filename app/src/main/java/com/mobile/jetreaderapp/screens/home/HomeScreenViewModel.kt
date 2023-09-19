package com.mobile.jetreaderapp.screens.home

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobile.jetreaderapp.data.DataOrException
import com.mobile.jetreaderapp.model.MBook
import com.mobile.jetreaderapp.repository.FireRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(private var fireRepository : FireRepository): ViewModel() {

    val data : MutableState<DataOrException<List<MBook>,Boolean,Exception>> = mutableStateOf(
        DataOrException(listOf(),true,Exception(""))
    )

    init {
        getAllBooksFromDatabase()
    }

    private fun getAllBooksFromDatabase() {
        viewModelScope.launch {
            try {
                data.value.loading=true
                data.value = fireRepository.getAllBooksFromDatabase()
                if(!data.value.data.isNullOrEmpty()) data.value.loading=false
            }catch (e : Exception){
                data.value.loading=false
                data.value.error = e
            }
        }
    }
}