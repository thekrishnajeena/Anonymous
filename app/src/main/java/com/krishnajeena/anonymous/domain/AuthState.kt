package com.krishnajeena.anonymous.domain

sealed interface AuthState{
    object Loading : AuthState
    object Unauthenticated : AuthState
    data class Authenticated(val userId: String) : AuthState
}