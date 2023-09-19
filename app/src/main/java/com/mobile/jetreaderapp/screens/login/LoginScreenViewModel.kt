package com.mobile.jetreaderapp.screens.login

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.mobile.jetreaderapp.model.MUser
import kotlinx.coroutines.launch

class LoginScreenViewModel : ViewModel() {
    //val loadingState = MutableStateFlow(LoadingState.IDLE)
    private val auth: FirebaseAuth = Firebase.auth

    private val _loading = MutableLiveData(false)
    val loading: MutableLiveData<Boolean> = _loading

    fun createUserWithEmailPassword(email: String, password: String, home: () -> Unit) {
        viewModelScope.launch {
            try {
                if (_loading.value == false) {
                    _loading.value = true

                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.e(
                                    "TAG",
                                    "createUserWithEmailPassword: ${task.result.user}"
                                )
                                val displayName = task.result.user?.email?.split('@')?.get(0)
                                createUser(displayName)
                                home()

                            } else {
                                Log.e(
                                    "TAG",
                                    "createUserWithEmailPassword: ${task.exception?.message}"
                                )
                            }
                        }
                    _loading.value = false
                }
            } catch (ex: Exception) {
                Log.e("TAG", "createUserWithEmailPassword: ${ex.message}")
            }
        }
    }

    private fun createUser(displayName: String?) {
        val userID = auth.currentUser?.uid
        val user = MUser(
            userID = userID.toString(),
            displayName = displayName.toString(),
            avatarUrl = "",
            quote = "Life is hell",
            profession = "Android Developer", id = null
        )
        FirebaseFirestore.getInstance().collection("users").add(user)

    }

    fun signInWithEmailPassword(email: String, password: String, home: () -> Unit) {
        viewModelScope.launch {
            try {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            home()
                        } else {
                            Log.e("TAG", "signInWithEmailAndPassword: ${task.exception?.message}")
                        }
                    }

            } catch (ex: Exception) {
                Log.e("TAG", "signInWithEmailAndPassword: ${ex.message}")
            }
        }
    }
}