package com.mobile.jetreaderapp.widgets

import android.view.MotionEvent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.mobile.jetreaderapp.R
import com.mobile.jetreaderapp.model.MBook
import com.mobile.jetreaderapp.navigation.ReaderScreens


@Composable
fun ReaderLogo(modifier: Modifier = Modifier) {
    Text(
        text = "Jet Reader App",
        modifier = modifier.padding(top = 3.dp),
        style = MaterialTheme.typography.h4,
        color = Color.Red
            .copy(alpha = 0.5f)
    )
}

@Composable
fun EmailInput(
    modifier: Modifier = Modifier,
    emailState: MutableState<String>,
    labelID: String = "Email",
    enabled: Boolean = true,
    imeAction: ImeAction = ImeAction.Next,
    onActions: KeyboardActions = KeyboardActions.Default
) {
    ReaderInputField(
        valueState = emailState,
        labelID = labelID,
        modifier = modifier,
        enabled = enabled,
        keyboardType = KeyboardType.Email,
        imeAction = imeAction,
        onActions = onActions
    )

}

@Composable
fun ReaderInputField(
    modifier: Modifier = Modifier,
    valueState: MutableState<String>,
    labelID: String,
    enabled: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    isSingleLine: Boolean = true,
    onActions: KeyboardActions = KeyboardActions.Default
) {
    OutlinedTextField(
        value = valueState.value,
        onValueChange = { valueState.value = it },
        label = {
            Text(
                text = labelID
            )
        }, singleLine = isSingleLine,
        enabled = enabled,
        modifier = modifier
            .padding(10.dp)
            .fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
        keyboardActions = onActions,
        maxLines = 1,
        textStyle = TextStyle(fontSize = 18.sp, color = MaterialTheme.colors.onBackground)
    )
}

@Composable
fun PasswordInput(
    passwordState: MutableState<String>,
    modifier: Modifier,
    enabled: Boolean,
    labelID: String,
    passwordVisibility: MutableState<Boolean>,
    imeAction: ImeAction = ImeAction.Done,
    onActions: KeyboardActions = KeyboardActions.Default
) {

    val visualTransformation =
        if (passwordVisibility.value) VisualTransformation.None else PasswordVisualTransformation()
    OutlinedTextField(
        value = passwordState.value,
        onValueChange = { passwordState.value = it },
        label = { Text(text = labelID) },
        modifier = modifier
            .fillMaxWidth()
            .padding(10.dp),
        singleLine = true,
        enabled = enabled,
        textStyle = TextStyle(fontSize = 18.sp, color = MaterialTheme.colors.onBackground),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = imeAction
        ),
        visualTransformation = visualTransformation,
        trailingIcon = { PasswordVisibility(passwordVisibility = passwordVisibility) },
        keyboardActions = onActions

    )
}

@Composable
fun PasswordVisibility(passwordVisibility: MutableState<Boolean>) {

    val visible = passwordVisibility.value
    IconButton(onClick = { passwordVisibility.value = !visible }) {
        Icons.Default.Close

    }
}

@Composable
fun SubmitButton(loading: Boolean, textID: String, validInputs: Boolean, onClick: () -> Unit = {}) {
    Button(
        onClick = {
            onClick.invoke()
        },
        modifier = Modifier
            .padding(3.dp)
            .fillMaxWidth(),
        enabled = !loading && validInputs,
        shape = CircleShape
    ) {
        if (loading) CircularProgressIndicator(modifier = Modifier.size(25.dp)) else Text(
            text = textID,
            modifier = Modifier.padding(5.dp)
        )

    }
}

@Composable
fun ReaderAppTopBar(
    title: String,
    image: ImageVector? = null,
    showProfile: Boolean = true,
    navController: NavController,
    onBackPress: () -> Unit = {}
) {
    TopAppBar(title = {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (showProfile) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Person",
                    modifier = Modifier
                        .clip(shape = RoundedCornerShape(12.dp))
                        .scale(0.9f)
                )
            }

            if (image != null) {
                Icon(
                    imageVector = image,
                    contentDescription = "Arrow Icon",
                    tint = Color.Red.copy(0.7f),
                    modifier = Modifier.clickable { onBackPress.invoke() })
            }

            Spacer(modifier = Modifier.width(40.dp))


            Text(
                text = title,
                color = Color.Red.copy(alpha = 0.6f),
                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 25.sp)
            )
        }
    }, actions = {
        IconButton(onClick = {
            FirebaseAuth.getInstance().signOut().run {
                navController.navigate(ReaderScreens.LoginScreen.name)
            }
        }) {
            if (showProfile) {
                Row {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_logout),
                        contentDescription = "Log out",
                        tint = Color.Gray
                    )
                }
            } else Box {}


        }
    }, backgroundColor = Color.Transparent, elevation = 0.dp)
}

@Composable
fun FabContent(onTap: () -> Unit) {
    FloatingActionButton(
        onClick = { onTap() },
        shape = RoundedCornerShape(50.dp),
        backgroundColor = Color(0xFF92CBDF)
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add",
            tint = Color.White
        )
    }
}

@Composable
fun TitleSection(modifier: Modifier = Modifier, label: String) {
    Surface(modifier = modifier.padding(start = 5.dp, top = 1.dp)) {
        Column {
            Text(
                text = label,
                fontSize = 18.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Left
            )
        }

    }
}

@Composable
fun RoundedButton(
    label: String = "Reading",
    radius: Int = 29,
    onPress: () -> Unit = {}
) {
    Surface(
        modifier = Modifier.clip(
            RoundedCornerShape(
                bottomEndPercent = radius,
                topStartPercent = radius
            )
        ),
        color = Color(0xFF92CBDF)
    ) {
        Column(
            modifier = Modifier
                .width(90.dp)
                .heightIn(40.dp)
                .clickable { onPress.invoke() },
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(text = label, style = TextStyle(color = Color.White, fontSize = 15.sp))

        }

    }
}


@Composable
fun BookRating(score: Double = 4.5) {
    Surface(
        modifier = Modifier
            .height(70.dp)
            .padding(1.dp), shape = RoundedCornerShape(56.dp), elevation = 6.dp, color = Color.White
    ) {
        Column(modifier = Modifier.padding(4.dp)) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = "Star",
                modifier = Modifier.padding(3.dp)
            )
            Text(text = score.toString(), style = MaterialTheme.typography.subtitle1)
        }

    }
}

@Composable
fun ListCard(
    book: MBook, onPressDetail: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val resources = context.resources
    val displayMetrics = resources.displayMetrics
    val screenWidth = displayMetrics.widthPixels / displayMetrics.density
    val spacing = 10.dp
    val isStartedReading = remember {
        mutableStateOf(false)
    }

    Card(
        shape = RoundedCornerShape(29.dp),
        backgroundColor = Color.White,
        elevation = 6.dp,
        modifier = Modifier
            .padding(16.dp)
            .height(250.dp)
            .width(200.dp)
            .clickable { book.title?.let { onPressDetail(it) } }) {
        Column(
            modifier = Modifier.width(screenWidth.dp - (spacing * 2)),
            horizontalAlignment = Alignment.Start
        ) {
            Row(horizontalArrangement = Arrangement.Center) {
                Image(
                    painter = rememberAsyncImagePainter(model = book.photoUri),
                    contentDescription = "Book",
                    modifier = Modifier
                        .height(140.dp)
                        .padding(4.dp)
                        .width(100.dp)
                )
                Spacer(modifier = Modifier.width(50.dp))
                Column(
                    modifier = Modifier.padding(top = 25.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        modifier = Modifier.padding(1.dp)
                    )

                    BookRating(score = book.rating!!.toDouble())
                }
            }
            Text(
                text = book.title.toString(),
                modifier = Modifier.padding(4.dp),
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = book.author.toString(),
                modifier = Modifier.padding(4.dp),
                style = MaterialTheme.typography.caption
            )
        }
        Row(
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.Bottom
        ) {
            isStartedReading.value = book.startedReading != null
            RoundedButton(label = if(isStartedReading.value) "Reading" else "Pending", radius = 70)
        }
    }
}

@ExperimentalComposeUiApi
@Composable
fun RatingBar(
    modifier: Modifier = Modifier,
    rating: Int,
    onPressRating: (Int) -> Unit
) {

    var ratingState by remember {
        mutableStateOf(rating)
    }


    var selected by remember {
        mutableStateOf(false)
    }

    val size by animateDpAsState(
        targetValue = if (selected) 42.dp else 34.dp,
        spring(Spring.DampingRatioMediumBouncy)
    )

    Row(
        modifier = Modifier.width(280.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        for (i in 1..5) {
            Icon(
                painter = painterResource(id = R.drawable.ic_star),
                contentDescription = "star",
                modifier = modifier
                    .width(size)
                    .height(size)
                    .pointerInteropFilter {
                        when (it.action) {
                            MotionEvent.ACTION_DOWN -> {
                                selected = true
                                onPressRating(i)
                                ratingState = i
                            }
                            MotionEvent.ACTION_UP -> {
                                selected = false
                            }
                        }
                        true
                    },
                tint = if (i <= ratingState) Color(0xFFFFD700) else Color(0xFFA2ADB1)
            )
        }
    }
}



