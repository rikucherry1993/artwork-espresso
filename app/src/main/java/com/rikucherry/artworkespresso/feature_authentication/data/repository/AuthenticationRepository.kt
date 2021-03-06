package com.rikucherry.artworkespresso.feature_authentication.data.repository

import com.rikucherry.artworkespresso.feature_authentication.data.local.data_source.LoginInfoItem
import com.rikucherry.artworkespresso.feature_authentication.data.remote.data_source.ClientTokenResponseDto
import com.rikucherry.artworkespresso.feature_authentication.data.remote.data_source.UserTokenResponseDto
import com.skydoves.sandwich.ApiResponse

interface AuthenticationRepository {

    suspend fun getLoginInfo(): LoginInfoItem?

    suspend fun insertLoginInfo(loginInfoItem: LoginInfoItem)

    suspend fun truncateLoginInfo()

    suspend fun getUserAccessToken(clientId: Int, clientSecret: String, grantType: String,
                                   code: String, redirectUri: String, ): ApiResponse<UserTokenResponseDto>

    suspend fun getClientAccessToken(clientId: Int, clientSecret: String,
                                     grantType: String) : ApiResponse<ClientTokenResponseDto>

    suspend fun refreshUserAccessToken(clientId: Int, clientSecret: String,
                                       refreshToken: String): ApiResponse<UserTokenResponseDto>

}