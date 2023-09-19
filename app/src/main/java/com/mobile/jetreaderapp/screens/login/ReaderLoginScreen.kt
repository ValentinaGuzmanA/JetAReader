package com.mobile.jetreaderapp.screens.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mobile.jetreaderapp.R
import com.mobile.jetreaderapp.navigation.ReaderScreens
import com.mobile.jetreaderapp.widgets.EmailInput
import com.mobile.jetreaderapp.widgets.PasswordInput
import com.mobile.jetreaderapp.widgets.ReaderLogo
import com.mobile.jetreaderapp.widgets.SubmitButton

@ExperimentalComposeUiApi
@Composable
fun ReaderLoginScreen(navController: NavController,loginScreenViewModel: LoginScreenViewModel = viewModel()) {

    val showLogin = rememberSaveable {
        mutableStateOf(true)
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(50.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ReaderLogo()

            if (showLogin.value) {
                UserForm(loading = false, isCreateAccount = false) { email, password ->
                    loginScreenViewModel.signInWithEmailPassword(email,password){
                        navController.navigate(ReaderScreens.ReaderHomeScreen.name)
                    }
                }
            } else {
                UserForm(loading = false, isCreateAccount = true) { email, password ->
                    loginScreenViewModel.createUserWithEmailPassword(email,password){
                        navController.navigate(ReaderScreens.ReaderHomeScreen.name)
                    }
                }
            }


            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier.padding(15.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                val text = if (showLogin.value) "Sign up" else "Login"
                Text(text = "New User?")
                Text(
                    text = text,
                    modifier = Modifier
                        .clickable { showLogin.value = !showLogin.value }
                        .padding(start = 5.dp),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colors.secondaryVariant
                )
            }
        }
    }
}

@Preview
@ExperimentalComposeUiApi
@Composable
fun UserForm(
    loading: Boolean = false,
    isCreateAccount: Boolean = false,
    onDone: (String, String) -> Unit = { email, password -> }
) {
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val passwordVisibility = remember { mutableStateOf(false) }
    val passwordFocus = FocusRequester.Default
    val keyboardController = LocalSoftwareKeyboardController.current
    val valid = remember(email.value, password.value) {
        email.value.trim().isNotEmpty() && password.value.trim().isNotEmpty()
    }

    Column(
        modifier = Modifier
            .height(250.dp)
            .fillMaxWidth()
            .background(color = MaterialTheme.colors.background)
            .verticalScroll(
                rememberScrollState()
            ), horizontalAlignment = Alignment.CenterHorizontally
    ) {

        if(isCreateAccount) Text(text = stringResource(id = R.string.create_acc))

        EmailInput(
            emailState = email,
            enabled = !loading,
            labelID = "Email",
            onActions = KeyboardActions {
                passwordFocus.requestFocus()
            })

        PasswordInput(
            passwordState = password,
            modifier = Modifier.focusRequester(passwordFocus),
            enabled = !loading,
            labelID = "Password",
            passwordVisibility = passwordVisibility,
            onActions = KeyboardActions {
                if (!valid) return@KeyboardActions
                onDone(email.value.trim(), password.value.trim())
                keyboardController?.hide()
            })

        SubmitButton(
            loading = loading,
            textID = if (isCreateAccount) "CREATE ACCOUNT" else "LOGIN",
            validInputs = valid
        ) {
            onDone(email.value.trim(), password.value.trim())
            keyboardController?.hide()
        }


    }
}


