package com.mobile.jetreaderapp.screens.search

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobile.jetreaderapp.data.Resource
import com.mobile.jetreaderapp.model.Item
import com.mobile.jetreaderapp.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookSearchViewModel @Inject constructor(private val bookRepository: BookRepository) :
    ViewModel() {
    var listOfBooks: List<Item> by
    mutableStateOf(listOf())

    var isLoading: Boolean by mutableStateOf(true)

    init {
        loadBooks()
    }

    private fun loadBooks() {
        searchBooks("Android")
    }

    fun searchBooks(query: String) {
        viewModelScope.launch(Dispatchers.Default) {
            if (query.isEmpty())
                return@launch
            try {

                when (val response = bookRepository.getBooks(searchQuery = query)) {
                    is Resource.Success -> {
                        listOfBooks = response.data!!
                        if (listOfBooks.isNotEmpty()) isLoading = false

                    }
                    is Resource.Error -> {
                        isLoading = false
                    }
                    else -> {
                        isLoading = false

                    }
                }

            } catch (e: Exception) {
                isLoading = false
            }
        }
    }

}