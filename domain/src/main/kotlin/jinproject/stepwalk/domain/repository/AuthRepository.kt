package jinproject.stepwalk.domain.repository

import jinproject.stepwalk.domain.model.ResponseState
import jinproject.stepwalk.domain.model.UserData

interface AuthRepository {
    suspend fun checkDuplicationId(id : String) : ResponseState<Boolean>
    suspend fun signUpAccount(userData: UserData) : ResponseState<String>
    suspend fun signInAccount(id: String, password : String) : ResponseState<String>
    suspend fun resetPasswordAccount(id: String, password : String) : ResponseState<Boolean>
    suspend fun findAccountId(email : String, code : Int) : ResponseState<String>
    suspend fun verificationEmailCode(email : String, code : Int) : ResponseState<Boolean>
    suspend fun requestEmailCode(email: String) : ResponseState<Boolean>
}