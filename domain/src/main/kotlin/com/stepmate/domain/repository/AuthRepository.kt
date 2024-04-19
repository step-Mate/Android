package com.stepmate.domain.repository

import com.stepmate.domain.model.BodyData
import com.stepmate.domain.model.ResponseState
import com.stepmate.domain.model.SignUpData
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun checkDuplicationId(id: String): ResponseState<Boolean>
    suspend fun checkDuplicationNickname(nickname: String): ResponseState<Boolean>
    suspend fun signUpAccount(signUpData: SignUpData): Flow<ResponseState<Boolean>>
    suspend fun signInAccount(id: String, password: String): Flow<ResponseState<Boolean>>
    suspend fun resetPasswordAccount(id: String, password: String): ResponseState<Boolean>
    suspend fun findAccountId(email: String, code: String): Flow<ResponseState<String>>
    suspend fun verificationEmailCode(email: String, code: String): ResponseState<Boolean>
    suspend fun requestEmailCode(email: String): ResponseState<Boolean>
    suspend fun verificationUserEmail(
        id: String,
        email: String,
        code: String
    ): Flow<ResponseState<Boolean>>
    fun getBodyData() : Flow<BodyData>
    suspend fun setBodyData(bodyData: BodyData)
    suspend fun logoutAccount()
    fun getAccessToken(): Flow<String>
}