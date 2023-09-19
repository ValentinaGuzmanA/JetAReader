package com.mobile.jetreaderapp.screens.detail

import androidx.lifecycle.ViewModel
import com.mobile.jetreaderapp.data.Resource
import com.mobile.jetreaderapp.model.Item
import com.mobile.jetreaderapp.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor( private val bookRepository: BookRepository) : ViewModel(){
    suspend fun getBookInfo(bookId : String) : Resource<Item> {
        return bookRepository.getBooksInfo(bookID = bookId)
    }
}