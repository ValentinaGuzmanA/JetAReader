package com.mobile.jetreaderapp.screens.search

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.mobile.jetreaderapp.model.Item
import com.mobile.jetreaderapp.navigation.ReaderScreens
import com.mobile.jetreaderapp.widgets.ReaderAppTopBar
import com.mobile.jetreaderapp.widgets.ReaderInputField

@ExperimentalComposeUiApi
@Composable
fun ReaderSearchScreen(
    navController: NavController,
    bookSearchViewModel: BookSearchViewModel = hiltViewModel()
) {
    Scaffold(topBar = {
        ReaderAppTopBar(
            title = "Search Books",
            image = Icons.Default.ArrowBack,
            showProfile = false,
            navController = navController
        ) {
            navController.navigate(ReaderScreens.ReaderHomeScreen.name)
        }
    }) {

        Surface {
            Column {
                SearchForm(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    bookSearchViewModel = bookSearchViewModel
                ) { query ->
                    Log.e("Aditi==>","SearchForm :: $query")
                    bookSearchViewModel.searchBooks(query)
                }

                Spacer(modifier = Modifier.height(13.dp))
                BookList(navController, bookSearchViewModel)
            }

        }

    }

}

@Composable
fun BookList(
    navController: NavController,
    bookSearchViewModel: BookSearchViewModel = hiltViewModel()
) {
    if (bookSearchViewModel.isLoading) {
        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
    } else {
        val listOfBooks = bookSearchViewModel.listOfBooks
        LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp)) {
            items(items = listOfBooks) { book ->
                BookRow(book, navController)
            }
        }
    }
}

@Composable
fun BookRow(book: Item, navController: NavController) {
    Card(modifier = Modifier
        .clickable {
            navController.navigate(ReaderScreens.DetailScreen.name + "/${book.id}")
        }
        .fillMaxWidth()
        .padding(3.dp)
        .height(100.dp),
        shape = RectangleShape,
        elevation = 7.dp
    ) {
        Row(modifier = Modifier.padding(5.dp), verticalAlignment = Alignment.Top) {
            Image(
                painter = rememberAsyncImagePainter(model = book.volumeInfo.imageLinks.smallThumbnail),
                contentDescription = "Image URL",
                modifier = Modifier
                    .width(80.dp)
                    .padding(end = 4.dp)
                    .fillMaxHeight()
            )
            Column {
                Text(text = book.volumeInfo.title, overflow = TextOverflow.Ellipsis)
                Text(
                    text = "Author : ${book.volumeInfo.authors}",
                    overflow = TextOverflow.Clip,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    style = MaterialTheme.typography.caption
                )

                Text(
                    text = "Date : ${book.volumeInfo.publishedDate}",
                    overflow = TextOverflow.Clip,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Normal,
                    style = MaterialTheme.typography.caption
                )

                Text(
                    text = book.volumeInfo.categories.toString(),
                    overflow = TextOverflow.Clip,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Normal,
                    style = MaterialTheme.typography.caption
                )


            }
        }

    }
}

@ExperimentalComposeUiApi
@Composable
fun SearchForm(
    modifier: Modifier = Modifier,
    loading: Boolean = false,
    hint: String = "Search",
    bookSearchViewModel: BookSearchViewModel = hiltViewModel(),
    onSearch: (String) -> Unit = {}
) {
    Column {
        val searchQueryState = rememberSaveable {
            mutableStateOf("")
        }
        val keyBoardController = LocalSoftwareKeyboardController.current
        val valid = remember(searchQueryState.value) {
            searchQueryState.value.trim().isNotEmpty()
        }
        ReaderInputField(
            valueState = searchQueryState,
            labelID = "Search",
            enabled = true,
            onActions = KeyboardActions {
                if (!valid) return@KeyboardActions
                onSearch(searchQueryState.value.trim())
                //searchQueryState.value = ""
                keyBoardController?.hide()
            })
    }

}