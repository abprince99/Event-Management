package com.example.eventmanagement.auth.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventmanagement.auth.data.repository.AuthRepository
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val repository = AuthRepository()

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    fun register(
        email: String,
        password: String
    ) {

        if (email.isBlank()) {
            _authState.value =
                AuthState.Error("Please enter your email")
            return
        }

        if (password.isBlank()) {
            _authState.value =
                AuthState.Error("Please enter your password")
            return
        }

        if (password.length < 6) {
            _authState.value =
                AuthState.Error("Password must be at least 6 characters")
            return
        }

        viewModelScope.launch {

            _authState.value = AuthState.Loading

            val result = repository.register(
                email,
                password
            )

            result
                .onSuccess {
                    _authState.value = AuthState.RegisterSuccess
                }
                .onFailure { exception ->

                    _authState.value =
                        AuthState.Error(
                            getFirebaseErrorMessage(exception)
                        )
                }
        }
    }

    fun login(
        email: String,
        password: String
    ) {

        if (email.isBlank()) {
            _authState.value =
                AuthState.Error("Please enter your email")
            return
        }

        if (password.isBlank()) {
            _authState.value =
                AuthState.Error("Please enter your password")
            return
        }

        viewModelScope.launch {

            _authState.value = AuthState.Loading

            val result = repository.login(
                email,
                password
            )

            result
                .onSuccess {
                    _authState.value = AuthState.LoginSuccess
                }
                .onFailure { exception ->

                    _authState.value =
                        AuthState.Error(
                            getFirebaseErrorMessage(exception)
                        )
                }
        }
    }

    fun resetPassword(email: String) {
        if (email.isBlank()) {
            _authState.value = AuthState.Error("Please enter your email")
            return
        }
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = repository.resetPassword(email)
            result
                .onSuccess {
                    _authState.value = AuthState.PasswordResetSuccess
                }.onFailure { exception ->
                    _authState.value = AuthState.Error(getFirebaseErrorMessage(exception))
                }
        }
    }

    private fun getFirebaseErrorMessage(
        exception: Throwable
    ): String {

        return when {

            exception.message?.contains(
                "email address is already in use",
                ignoreCase = true
            ) == true ->
                "This email is already registered"

            exception.message?.contains(
                "badly formatted",
                ignoreCase = true
            ) == true ->
                "Please enter a valid email address"

            exception.message?.contains(
                "password is invalid",
                ignoreCase = true
            ) == true ->
                "Invalid email or password"

            exception.message?.contains(
                "no user record",
                ignoreCase = true
            ) == true ->
                "Invalid email or password"

            exception.message?.contains(
                "network",
                ignoreCase = true
            ) == true ->
                "Please check your internet connection"

            else ->
                exception.message
                    ?: "Something went wrong"
        }
    }
}

sealed class AuthState {

    data object Loading : AuthState()

    data object RegisterSuccess : AuthState()

    data object LoginSuccess : AuthState()

    data object PasswordResetSuccess : AuthState()

    data class Error(
        val message: String
    ) : AuthState()
}