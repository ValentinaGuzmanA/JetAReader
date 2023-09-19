package com.mobile.jetreaderapp.screens.stats

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.sharp.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.mobile.jetreaderapp.model.MBook
import com.mobile.jetreaderapp.screens.home.HomeScreenViewModel
import com.mobile.jetreaderapp.utils.formatDate
import com.mobile.jetreaderapp.widgets.ReaderAppTopBar
import java.util.*

@Composable
fun ReaderStatsScreen(
    navController: NavController,
    homeScreenViewModel: HomeScreenViewModel = hiltViewModel()
) {
    var bookList: List<MBook>
    val currentUser = FirebaseAuth.getInstance().currentUser

    Scaffold(topBar = {
        ReaderAppTopBar(
            title = "Book Stats",
            navController = navController,
            image = Icons.Default.ArrowBack,
            showProfile = false
        ) {
            navController.popBackStack()
        }
    }) {
        Surface {
            bookList = if (!homeScreenViewModel.data.value.data.isNullOrEmpty()) {
                homeScreenViewModel.data.value.data!!.filter { mBook ->
                    (mBook.userId == currentUser?.uid)
                }
            } else {
                emptyList()
            }
            Column {
                Row {
                    Box(
                        modifier = Modifier
                            .size(45.dp)
                            .padding(2.dp)
                    ) {
                        Icon(imageVector = Icons.Sharp.Person, contentDescription = "Person")
                    }
                    Text(
                        text = "Hi ${
                            currentUser?.email.toString()
                                .split("@")[0].uppercase(Locale.getDefault())
                        }"
                    )
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    shape = CircleShape,
                    elevation = 4.dp
                ) {
                    val readBooks = if (!homeScreenViewModel.data.value.data.isNullOrEmpty()) {
                        bookList.filter { mBook ->
                            (mBook.userId == currentUser?.uid) && (mBook.finishedReading != null)
                        }
                    } else {
                        emptyList()
                    }

                    val readingBooks = if (!homeScreenViewModel.data.value.data.isNullOrEmpty()) {
                        bookList.filter { mBook ->
                            (mBook.userId == currentUser?.uid) && (mBook.startedReading != null) && (mBook.finishedReading == null)
                        }
                    } else {
                        emptyList()
                    }

                    Column(
                        modifier = Modifier.padding(start = 25.dp, top = 4.dp, bottom = 4.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "Your Stats : ",
                            style = MaterialTheme.typography.h5,
                            modifier = Modifier.padding(2.dp)
                        )
                        Divider()
                        Text(
                            text = "You are reading : ${readingBooks.size} books",
                            modifier = Modifier.padding(1.dp)
                        )
                        Text(
                            text = "You have read : ${readBooks.size} books",
                            modifier = Modifier.padding(1.dp)
                        )

                    }

                }

                if (homeScreenViewModel.data.value.loading == true) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                } else {
                    Divider(modifier = Modifier.padding(5.dp))
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        val readBooks = if (!homeScreenViewModel.data.value.data.isNullOrEmpty()) {
                            bookList.filter { mBook ->
                                (mBook.userId == currentUser?.uid) && (mBook.finishedReading != null)
                            }
                        } else {
                            emptyList()
                        }

                        items(items = readBooks) { book ->
                            BookRowStats(book = book)
                        }

                    }
                }

            }

        }


    }

}

@Composable
fun BookRowStats(book: MBook) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(3.dp)
            .height(100.dp),
        shape = RectangleShape,
        elevation = 7.dp
    ) {
        Row(modifier = Modifier.padding(5.dp), verticalAlignment = Alignment.Top) {
            Image(
                painter = rememberAsyncImagePainter(model = book.photoUri),
                contentDescription = "Image URL",
                modifier = Modifier
                    .width(80.dp)
                    .padding(end = 4.dp)
                    .fillMaxHeight()
            )
            Column {

                Row(horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = book.title.toString(), overflow = TextOverflow.Ellipsis)
                    Spacer(modifier = Modifier.fillMaxWidth(0.8f))
                    if (book.rating!! >= 4) {
                        Icon(
                            imageVector = Icons.Default.ThumbUp,
                            contentDescription = "ThumbUp",
                            tint = Color.Green.copy(alpha = 0.5f)
                        )
                    } else {
                        Box {}
                    }

                }

                Text(
                    text = "Author : ${book.author}",
                    overflow = TextOverflow.Clip,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    style = MaterialTheme.typography.caption
                )

                Text(
                    text = "Started : ${formatDate(book.startedReading!!)}",
                    softWrap = true,
                    overflow = TextOverflow.Clip,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Normal,
                    style = MaterialTheme.typography.caption
                )

                Text(
                    text = "Finished : ${formatDate(book.startedReading!!)}",
                    softWrap = true,
                    overflow = TextOverflow.Clip,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Normal,
                    style = MaterialTheme.typography.caption
                )


            }
        }

    }
}

