package com.mobile.jetreaderapp.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mobile.jetreaderapp.network.BooksApi
import com.mobile.jetreaderapp.repository.BookRepository
import com.mobile.jetreaderapp.repository.FireRepository
import com.mobile.jetreaderapp.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideBookApi(): BooksApi {
        return Retrofit.Builder().baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()).build().create(BooksApi::class.java)
    }

    @Provides
    @Singleton
    fun provideBookRepository(booksApi: BooksApi) = BookRepository(booksApi)

    @Provides
    @Singleton
    fun provideFireRepository() = FireRepository(query = FirebaseFirestore.getInstance().collection("books"))

}