package com.krishnajeena.anonymous.feature_auth

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krishnajeena.anonymous.data.FirestoreUserRepository
import com.krishnajeena.anonymous.data.auth.FirebaseAuthRepository
import com.krishnajeena.anonymous.data.auth.GoogleSignInManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface AuthUiState {
    object Idle : AuthUiState
    object Loading : AuthUiState
    data class Error(val msg: String) : AuthUiState
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val googleSignInManager: GoogleSignInManager,
    private val authRepository: FirebaseAuthRepository,
    private val userRepository: FirestoreUserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState

    fun signInWithGoogle(
        activity: ComponentActivity,
        onSuccess: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                _uiState.value = AuthUiState.Loading

                val googleCred = googleSignInManager.signIn(activity)
                val uid = authRepository.signInWithGoogle(
                    googleCred.idToken
                )

                val firebaseUser = authRepository.currentUser()
                    ?: error("user null")

                userRepository.createUserIfNotExists(
                    uid = uid,
                    email = firebaseUser.email,
                    photoUrl = firebaseUser.photoUrl?.toString(),
                    displayName = firebaseUser.displayName ?: "Anonymous"
                )

                onSuccess(uid)
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(e.message ?: "Sign-in failed")
            }
        }
    }
}