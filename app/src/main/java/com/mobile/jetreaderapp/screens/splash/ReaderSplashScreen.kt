package com.mobile.jetreaderapp.screens.splash

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.mobile.jetreaderapp.navigation.ReaderScreens
import com.mobile.jetreaderapp.widgets.ReaderLogo
import kotlinx.coroutines.delay

@Composable
fun ReaderSplashScreen(navController: NavController) {

    val scale = remember {
        Animatable(0f)
    }

    LaunchedEffect(key1 = true, block = {
        scale.animateTo(
            targetValue = 0.9f,
            animationSpec = tween(
                durationMillis = 800,
                easing = { OvershootInterpolator(8f).getInterpolation(it) }
            )
        )
        delay(2000L)
        if(FirebaseAuth.getInstance().currentUser?.email.isNullOrEmpty()){
            navController.navigate(ReaderScreens.LoginScreen.name)

        }else{
            navController.navigate(ReaderScreens.ReaderHomeScreen.name)
        }
    })

    Surface(
        modifier = Modifier
            .padding(10.dp)
            .size(300.dp)
            .scale(scale.value)
            .background(color = Color.White),
        shape = CircleShape,
        border = BorderStroke(width = 2.dp, color = Color.LightGray)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            ReaderLogo()

            Spacer(modifier = Modifier.height(15.dp))

            Text(
                text = "Read . Change. Yourselves",
                modifier = Modifier.padding(top = 3.dp),
                style = MaterialTheme.typography.subtitle1,
                color = Color.LightGray
            )
        }

    }

}
