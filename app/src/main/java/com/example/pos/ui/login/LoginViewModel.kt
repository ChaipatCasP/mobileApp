package com.example.pos.ui.login

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pos.R
import com.example.pos.service.ApiResult
import com.example.pos.service.auth.AuthService
import com.example.pos.service.auth.LoginRequest
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val authService = AuthService()

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    fun login(username: String, password: String) {
        viewModelScope.launch {
            when (val result = authService.login(
                LoginRequest(username = username, password = password)
            )) {
                is ApiResult.Success -> {
                    _loginResult.value = LoginResult(
                        success = LoggedInUserView(displayName = result.data.displayName)
                    )
                }
                is ApiResult.HttpError -> {
                    // message มาจาก API เช่น "Cannot find User..."
                    _loginResult.value = LoginResult(errorMessage = result.message)
                }
                is ApiResult.Exception -> {
                    _loginResult.value = LoginResult(error = R.string.login_failed)
                }
            }
        }
    }

    fun loginDataChanged(username: String, password: String) {
        if (!isUserNameValid(username)) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    private fun isUserNameValid(username: String): Boolean =
        if (username.contains("@")) Patterns.EMAIL_ADDRESS.matcher(username).matches()
        else username.isNotBlank()

    private fun isPasswordValid(password: String): Boolean = password.length > 5
}
