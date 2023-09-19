package com.mobile.jetreaderapp.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.mobile.jetreaderapp.model.MBook
import com.mobile.jetreaderapp.navigation.ReaderScreens
import com.mobile.jetreaderapp.widgets.FabContent
import com.mobile.jetreaderapp.widgets.ListCard
import com.mobile.jetreaderapp.widgets.ReaderAppTopBar
import com.mobile.jetreaderapp.widgets.TitleSection

@Composable
fun ReaderHomeScreen(
    navController: NavController,
    homeScreenViewModel: HomeScreenViewModel = hiltViewModel()
) {
    Scaffold(topBar = {
        ReaderAppTopBar("Jet Reader App", navController = navController)
    },
        floatingActionButton = {
            FabContent {
                navController.navigate(ReaderScreens.SearchScreen.name)
            }
        }) {
        Surface(modifier = Modifier.fillMaxSize()) {
            HomeContent(navController, homeScreenViewModel)
        }

    }

}

@Composable
fun HomeContent(navController: NavController, homeScreenViewModel: HomeScreenViewModel) {
    val currentUserName = if (!FirebaseAuth.getInstance().currentUser?.email.isNullOrEmpty()) {
        FirebaseAuth.getInstance().currentUser?.email?.split("@")?.get(0)
    } else {
        "N/A"
    }

    LazyColumn(modifier = Modifier.padding(2.dp), verticalArrangement = Arrangement.Top) {
        item {
            Row {
                TitleSection(label = "Your reading activity right now ...")
                Spacer(modifier = Modifier.fillMaxWidth(0.4f))
                Column {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Account",
                        modifier = Modifier
                            .clickable {
                                navController.navigate(ReaderScreens.ReaderStatsScreen.name)
                            }
                            .size(45.dp),
                        tint = MaterialTheme.colors.secondaryVariant
                    )
                    Text(
                        text = currentUserName.toString(),
                        modifier = Modifier.padding(2.dp),
                        style = MaterialTheme.typography.overline,
                        color = Color.Red,
                        fontSize = 15.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Clip
                    )
                    Divider()
                }
            }

            val currentUser = FirebaseAuth.getInstance().currentUser
            var listOfBooks = emptyList<MBook>()
            if (!homeScreenViewModel.data.value.data.isNullOrEmpty()) {
                listOfBooks = homeScreenViewModel.data.value.data!!.toList().filter { mBook ->
                    mBook.userId == currentUser?.uid.toString()
                }
            }
            ReadingNow(bookList = listOfBooks, navController = navController)
            TitleSection(label = "Reading List")
            BookListArea(listOfBooks = listOfBooks, navController = navController)
        }
    }
}

@Composable
fun BookListArea(listOfBooks: List<MBook>, navController: NavController) {

    val addedBooks = listOfBooks.filter { mBook ->
        mBook.startedReading == null && mBook.finishedReading == null
    }

    HorizontalScrollableComponent(addedBooks) {
        navController.navigate(ReaderScreens.UpdateScreen.name + "/$it")
    }
}

@Composable
fun HorizontalScrollableComponent(
    listOfBooks: List<MBook>,
    homeScreenViewModel: HomeScreenViewModel = hiltViewModel(),
    onCardPressed: (String) -> Unit
) {
    val scrollState = rememberScrollState()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(280.dp)
            .horizontalScroll(scrollState)
    ) {
        if (homeScreenViewModel.data.value.loading == true) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        } else {
            if (listOfBooks.isEmpty()) {
                Surface(modifier = Modifier.padding(23.dp)) {
                    Text(
                        text = "No Books found. Add books to read.",
                        style = TextStyle(
                            color = Color.Red.copy(alpha = 0.5f),
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            } else {
                for (book in listOfBooks) {
                    ListCard(book = book) {
                        onCardPressed(book.googleBookId.toString())
                    }
                }
            }
        }

    }
}


@Composable
fun ReadingNow(bookList: List<MBook>, navController: NavController) {

    val readingNowList = bookList.filter { mBook ->
        mBook.startedReading != null && mBook.finishedReading == null
    }

    HorizontalScrollableComponent(readingNowList) {
        navController.navigate(ReaderScreens.UpdateScreen.name + "/$it")
    }
}

