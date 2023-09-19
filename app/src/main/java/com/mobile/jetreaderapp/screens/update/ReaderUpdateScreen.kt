package com.mobile.jetreaderapp.screens.update

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.mobile.jetreaderapp.R
import com.mobile.jetreaderapp.data.DataOrException
import com.mobile.jetreaderapp.model.MBook
import com.mobile.jetreaderapp.navigation.ReaderScreens
import com.mobile.jetreaderapp.screens.home.HomeScreenViewModel
import com.mobile.jetreaderapp.utils.formatDate
import com.mobile.jetreaderapp.utils.showToast
import com.mobile.jetreaderapp.widgets.RatingBar
import com.mobile.jetreaderapp.widgets.ReaderAppTopBar
import com.mobile.jetreaderapp.widgets.ReaderInputField
import com.mobile.jetreaderapp.widgets.RoundedButton

@ExperimentalComposeUiApi
@Composable
fun ReaderUpdateScreen(
    navController: NavController,
    bookId: String?,
    homeScreenViewModel: HomeScreenViewModel = hiltViewModel()
) {
    Scaffold(topBar = {
        ReaderAppTopBar(
            title = "Update Book",
            navController = navController,
            showProfile = false,
            image = Icons.Default.ArrowBack
        ) {
            navController.popBackStack()
        }
    }) {
        val bookInfo = produceState<DataOrException<List<MBook>, Boolean, Exception>>(
            initialValue = DataOrException(
                data = emptyList(),
                true,
                Exception("")
            )
        ) {
            value = homeScreenViewModel.data.value
        }.value


        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(3.dp)
        ) {
            Column(
                modifier = Modifier.padding(top = 3.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (bookInfo.loading == true) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    bookInfo.loading = false
                } else {
                    Surface(
                        modifier = Modifier
                            .padding(2.dp)
                            .fillMaxWidth(),
                        shape = CircleShape,
                        elevation = 4.dp
                    ) {
                        ShowBookUpdate(bookInfo = homeScreenViewModel.data.value, bookId = bookId)
                    }

                    ShowSimpleForm(book = homeScreenViewModel.data.value.data?.first { mBook ->
                        mBook.googleBookId == bookId.toString()
                    }, navController)

                }
            }

        }

    }

}

@ExperimentalComposeUiApi
@Composable
fun ShowSimpleForm(book: MBook?, navController: NavController) {

    val context = LocalContext.current
    val noteText = remember {
        mutableStateOf("")
    }

    val isStaredReading = remember {
        mutableStateOf(false)
    }

    val isFinishedReading = remember {
        mutableStateOf(false)
    }

    val ratingVal = remember {
        mutableStateOf(0)
    }

    SimpleForm(
        defaultValue = book?.notes.toString().ifEmpty { "No thoughts available" }
    ) { note ->
        noteText.value = note
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier.padding(4.dp)
    ) {
        TextButton(
            onClick = { isStaredReading.value = true },
            enabled = book?.startedReading == null
        ) {
            if (book?.startedReading == null) {
                if (!isStaredReading.value) {
                    Text(text = "Start Reading")
                } else {
                    Text(
                        text = "Started Reading",
                        modifier = Modifier.alpha(0.6f),
                        color = Color.Red.copy(alpha = 0.5f)
                    )
                }
            } else {
                Text(text = "Started On : ${formatDate(book.startedReading!!)}")
            }
        }

        Spacer(modifier = Modifier.width(50.dp))

        TextButton(
            onClick = { isFinishedReading.value = true },
            enabled = book?.finishedReading == null
        ) {
            if (book?.finishedReading == null) {
                if (!isFinishedReading.value) {
                    Text(text = "Mark as Read")
                } else {
                    Text(
                        text = "Finished Reading",
                        modifier = Modifier.alpha(0.6f),
                        color = Color.Red.copy(alpha = 0.5f)
                    )
                }
            } else {
                Text(text = "Finished On : ${formatDate(book.finishedReading!!)}")
            }
        }
    }

    Text(text = "Rating", modifier = Modifier.padding(3.dp))

    book?.rating?.toInt().let {
        RatingBar(rating = it!!) { rating ->
            ratingVal.value = rating
        }
    }

    Spacer(
        modifier = Modifier
            .padding(bottom = 8.dp)
            .height(50.dp)
    )

    Row(horizontalArrangement = Arrangement.SpaceBetween) {

        val changedNotes = book?.notes != noteText.value
        val changedRating = book?.rating?.toInt() != ratingVal.value
        val isFinishedTimeStamp =
            if (isFinishedReading.value) Timestamp.now() else book?.finishedReading
        val isStartedTimeStamp =
            if (isStaredReading.value) Timestamp.now() else book?.startedReading

        val bookUpdate =
            changedNotes || changedRating || isFinishedReading.value || isStaredReading.value

        val bookToUpdate = hashMapOf(
            "started_reading_at" to isStartedTimeStamp,
            "finished_reading_at" to isFinishedTimeStamp,
            "rating" to ratingVal.value.toDouble(),
            "notes" to noteText.value
        ).toMap()


        RoundedButton(label = "Update") {
            if (bookUpdate) {
                FirebaseFirestore.getInstance().collection("books").document(book?.id!!)
                    .update(bookToUpdate).addOnSuccessListener {
                        showToast(context = context, msg = "Book Updated Successfully !!!")
                        navController.navigate(ReaderScreens.ReaderHomeScreen.name)
                    }
                    .addOnFailureListener {

                    }
            }
        }
        Spacer(modifier = Modifier.width(100.dp))

        val openDialog = remember {
            mutableStateOf(false)
        }

        if (openDialog.value) {
            ShowAlertDialog(
                message = stringResource(id = R.string.message) + "\n" + stringResource(
                    id = R.string.actions
                ), openDialog = openDialog
            ) {
                FirebaseFirestore.getInstance().collection("books").document(book?.id!!).delete()
                    .addOnSuccessListener {
                        openDialog.value = false
                        navController.navigate(ReaderScreens.ReaderHomeScreen.name)
                    }
            }
        }
        RoundedButton(label = "Delete") {
            openDialog.value = true

        }
    }
}

@Composable
fun ShowAlertDialog(message: String, openDialog: MutableState<Boolean>, onYesPressed: () -> Unit) {
    if (openDialog.value) {
        AlertDialog(onDismissRequest = { /*TODO*/ },
            title = { Text(text = "Delete Book") },
            buttons = {
                Row(
                    modifier = Modifier.padding(8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    TextButton(onClick = { onYesPressed.invoke() }) {
                        Text(text = "Yes")
                    }

                    TextButton(onClick = { openDialog.value = false }) {
                        Text(text = "No")
                    }
                }
            },
            text = { Text(text = message) }
        )
    }

}

@ExperimentalComposeUiApi
@Composable
fun SimpleForm(
    modifier: Modifier = Modifier,
    loading: Boolean = false,
    defaultValue: String = "Book",
    onSearch: (String) -> Unit = {}
) {
    Column {
        val textFieldValue = rememberSaveable { mutableStateOf(defaultValue) }
        val keyBoardController = LocalSoftwareKeyboardController.current
        val valid = remember(textFieldValue.value, textFieldValue.value.trim()::isNotEmpty)

        ReaderInputField(
            modifier = modifier
                .fillMaxWidth()
                .height(140.dp)
                .padding(3.dp)
                .background(Color.White)
                .padding(horizontal = 20.dp, vertical = 12.dp),
            valueState = textFieldValue,
            labelID = "Enter your thoughts",
            enabled = true,
            onActions = KeyboardActions {
                if (!valid) return@KeyboardActions
                onSearch(textFieldValue.value.trim())
                keyBoardController?.hide()
            })
    }

}

@Composable
fun ShowBookUpdate(bookInfo: DataOrException<List<MBook>, Boolean, Exception>, bookId: String?) {
    Row(horizontalArrangement = Arrangement.Center) {
        if (bookInfo.data != null) {
            Column(verticalArrangement = Arrangement.Center, modifier = Modifier.padding(4.dp)) {
                CardListItem(book = bookInfo.data!!.first { mBook ->
                    mBook.googleBookId == bookId.toString()
                }, onPressed = {})
            }
        }

    }
}

@Composable
fun CardListItem(book: MBook, onPressed: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(start = 4.dp, top = 4.dp, bottom = 8.dp, end = 4.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable { onPressed.invoke() },
        elevation = 8.dp,
    ) {
        Row(horizontalArrangement = Arrangement.Start) {
            Image(
                painter = rememberAsyncImagePainter(model = book.photoUri.toString()),
                contentDescription = " Book Image",
                modifier = Modifier
                    .height(100.dp)
                    .width(120.dp)
                    .padding(4.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 70.dp,
                            topEnd = 20.dp,
                            bottomStart = 0.dp,
                            bottomEnd = 0.dp
                        )
                    )
            )
            Column {
                Text(
                    text = book.title.toString(),
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier
                        .padding(start = 8.dp, end = 8.dp)
                        .width(120.dp),
                    fontWeight = FontWeight.SemiBold,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2
                )

                Text(
                    text = book.categories.toString(),
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier
                        .padding(start = 8.dp, end = 8.dp, top = 2.dp, bottom = 0.dp)
                )

                Text(
                    text = book.publishedDate.toString(),
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier
                        .padding(start = 8.dp, end = 8.dp, top = 2.dp, bottom = 8.dp)
                )
            }
        }
    }
}
