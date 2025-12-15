package com.krishnajeena.anonymous.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krishnajeena.anonymous.data.auth.FirebaseAuthRepository
import com.krishnajeena.anonymous.domain.AuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val authRepository: FirebaseAuthRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState

  init{
      observeAuthState()
  }

    private fun observeAuthState(){
        viewModelScope.launch{
            authRepository.authState().collect{
                state ->
                _authState.value = state
            }
        }
    }

    fun onLoginSuccess(userId: String) {
        _authState.value = AuthState.Authenticated(userId)
    }

    fun logout() {
        _authState.value = AuthState.Unauthenticated
    }
}