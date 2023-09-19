package com.mobile.jetreaderapp.screens.detail

import android.widget.Space
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.text.HtmlCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mobile.jetreaderapp.data.Resource
import com.mobile.jetreaderapp.model.Item
import com.mobile.jetreaderapp.model.MBook
import com.mobile.jetreaderapp.widgets.ReaderAppTopBar
import com.mobile.jetreaderapp.widgets.RoundedButton

@Composable
fun ReaderBookDetailScreen(
    navController: NavController,
    bookId: String,
    detailsViewModel: DetailsViewModel = hiltViewModel()
) {
    Scaffold(topBar = {
        ReaderAppTopBar(
            title = "Detail",
            image = Icons.Default.ArrowBack,
            showProfile = false,
            navController = navController
        ) {
            navController.popBackStack()
        }
    }) {
        Surface(
            modifier = Modifier
                .padding(3.dp)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier.padding(top = 12.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                val bookInfo = produceState<Resource<Item>>(initialValue = Resource.Loading()) {
                    value = detailsViewModel.getBookInfo(bookId)
                }.value

                if (bookInfo.data == null) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                } else {
                    ShowDetailsScreen(bookInfo, navController)
                }
            }

        }
    }

}

@Composable
fun ShowDetailsScreen(bookInfo: Resource<Item>, navController: NavController) {

    val bookData = bookInfo.data?.volumeInfo
    val bookGoogleId = bookInfo.data?.id

    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(modifier = Modifier.padding(34.dp), shape = CircleShape, elevation = 4.dp) {
            Image(
                painter = rememberAsyncImagePainter(model = bookData?.imageLinks?.smallThumbnail),
                contentDescription = "Image",
                modifier = Modifier
                    .width(90.dp)
                    .padding(1.dp)
                    .height(90.dp)
            )
        }
        Text(
            text = bookData!!.title,
            style = MaterialTheme.typography.h6,
            overflow = TextOverflow.Ellipsis,
            maxLines = 19
        )

        Text(
            text = "Authors : ${bookData.authors}",
            modifier = Modifier.padding(start = 10.dp, top = 2.dp, end = 10.dp)
        )
        Text(
            text = "Page Count : ${bookData.pageCount}",
            modifier = Modifier.padding(start = 10.dp, top = 2.dp, end = 10.dp)
        )
        Text(
            text = "Categories : ${bookData.authors}",
            style = MaterialTheme.typography.subtitle1,
            modifier = Modifier.padding(start = 10.dp, top = 2.dp, end = 10.dp),
            overflow = TextOverflow.Ellipsis,
            maxLines = 2
        )
        Text(
            text = "Published : ${bookData.publishedDate}",
            style = MaterialTheme.typography.subtitle2,
            modifier = Modifier.padding(start = 10.dp, top = 2.dp, end = 10.dp)
        )

        Spacer(modifier = Modifier.height(5.dp))

        val localDimensions = LocalContext.current.resources.displayMetrics
        Surface(
            modifier = Modifier
                .height(localDimensions.heightPixels.dp.times(0.09f))
                .padding(4.dp),
            shape = RectangleShape,
            border = BorderStroke(
                1.dp,
                Color.Gray
            )
        ) {
            val cleanDescription =
                HtmlCompat.fromHtml(bookData.description, HtmlCompat.FROM_HTML_MODE_LEGACY)
                    .toString()
            LazyColumn(modifier = Modifier.padding(5.dp)) {
                item {
                    Text(text = cleanDescription)
                }
            }
        }

        Row(
            modifier = Modifier.padding(top = 20.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            RoundedButton(label = "Cancel") {
                navController.popBackStack()
            }
            Spacer(modifier = Modifier.width(25.dp))
            RoundedButton(label = "Save") {

                val book = MBook(
                    title = bookData.title,
                    description = bookData.description,
                    author = bookData.authors.toString(),
                    notes = "",
                    pageCount = bookData.pageCount.toString(),
                    photoUri = bookData.imageLinks.smallThumbnail,
                    publishedDate = bookData.publishedDate,
                    rating = 0.0,
                    googleBookId = bookGoogleId,
                    userId = FirebaseAuth.getInstance().currentUser?.uid.toString()
                )
                saveBookToFirebase(book,navController)
            }
        }
    }
}

fun saveBookToFirebase(book: MBook, navController: NavController) {
    FirebaseFirestore.getInstance().collection("books").add(book).addOnSuccessListener { dataRef ->
        val docId = dataRef.id
        FirebaseFirestore.getInstance().collection("books").document(docId)
            .update(hashMapOf("id" to docId) as Map<String, Any>).addOnSuccessListener {
                navController.popBackStack()
            }
    }
}
