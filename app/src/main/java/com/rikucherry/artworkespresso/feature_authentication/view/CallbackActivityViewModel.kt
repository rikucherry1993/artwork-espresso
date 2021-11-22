package com.rikucherry.artworkespresso.feature_authentication.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rikucherry.artworkespresso.common.Constants
import com.rikucherry.artworkespresso.common.tool.ResponseHandler
import com.rikucherry.artworkespresso.common.tool.SharedPreferenceHelper
import com.rikucherry.artworkespresso.feature_authentication.domain.use_case.UserLoginUseCase
import com.rikucherry.artworkespresso.feature_authentication.domain.util.AuthenticationUtil
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class CallbackActivityViewModel @AssistedInject constructor(
    private val userLoginUseCase: UserLoginUseCase,
    private val prefs: SharedPreferenceHelper,
    @Assisted private val args: Bundle?
) : ViewModel() {

    private val _state = mutableStateOf("")
    val state: State<String> = _state

    init {
        val intent = args?.getParcelable<Intent>(Constants.AUTH_INTENT)
        val state = args?.getString(Constants.AUTH_STATE)
        getAccessToken(intent, state ?: "")
    }

    private fun getAccessToken(intent: Intent?, state: String) {
        val authCode = AuthenticationUtil.retrieveAuthorizeCode(intent, state)

        userLoginUseCase(authCode ?: "").onEach { result ->
            when (result) {
                is ResponseHandler.Success -> {
                    _state.value = result.data.toString()
                    prefs.saveUserAccessToken(result.data!!.accessToken)
                    Log.d("Auth state:", prefs.getUserAccessToken() ?: "")
                }

                is ResponseHandler.Loading -> {
                    _state.value = result.message ?: ""
                    Log.d("Auth state:", _state.value)
                }

                is ResponseHandler.Error -> {
                    _state.value = result.message!!
                    Log.d("Auth state:", _state.value)
                }
            }
        }.launchIn(viewModelScope)
    }

}