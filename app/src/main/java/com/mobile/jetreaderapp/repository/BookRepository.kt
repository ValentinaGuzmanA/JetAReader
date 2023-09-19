package com.mobile.jetreaderapp.repository

import com.mobile.jetreaderapp.data.DataOrException
import com.mobile.jetreaderapp.data.Resource
import com.mobile.jetreaderapp.model.Item
import com.mobile.jetreaderapp.network.BooksApi
import javax.inject.Inject

class BookRepository @Inject constructor(private val booksApi: BooksApi) {

    private val dataOrException = DataOrException<List<Item>, Boolean, Exception>()
    private val bookInfoDataOrException = DataOrException<Item, Boolean, Exception>()

    private suspend fun getAllBooks(searchQuery: String): DataOrException<List<Item>, Boolean, Exception> {
        try {
            dataOrException.loading = true
            dataOrException.data = booksApi.getAllBooks(searchQuery).items
            if (dataOrException.data!!.isNotEmpty()) dataOrException.loading = false

        } catch (e: Exception) {
            dataOrException.error = e
        }
        return dataOrException
    }
    private suspend fun getBookInfo(bookID: String): DataOrException<Item, Boolean, Exception> {
        try {
            bookInfoDataOrException.loading = true
            bookInfoDataOrException.data = booksApi.getBookInfo(bookID)
            if (bookInfoDataOrException.data!!.toString()
                    .isNotEmpty()
            ) bookInfoDataOrException.loading = false

        } catch (e: Exception) {
            bookInfoDataOrException.error = e
        }
        return bookInfoDataOrException
    }

    suspend fun getBooks(searchQuery: String) : Resource<List<Item>>{
        return try {
            Resource.Loading(data = true)
            val itemList = booksApi.getAllBooks(searchQuery).items
            if(itemList.isNotEmpty()) Resource.Loading(data = false)
            Resource.Success(data = itemList)
        }catch (e : Exception){
            Resource.Error(message = e.message)
        }
    }

    suspend fun getBooksInfo(bookID: String) : Resource<Item>{
        val response = try {
            Resource.Loading(data = true)
            booksApi.getBookInfo(bookID)
        }catch (e : Exception){
            return Resource.Error(message = e.message, data = null)
        }
        Resource.Loading(data = false)
        return Resource.Success(data = response)
    }
}