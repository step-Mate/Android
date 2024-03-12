package com.stepmate.data.remote.dto.request

data class SignUpRequest(
    val userId: String,
    val password: String,
    val nickname: String,
    val email: String,
    val age: Int,
    val height: Int,
    val weight: Int,
)
