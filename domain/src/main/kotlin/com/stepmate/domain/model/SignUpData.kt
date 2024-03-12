package com.stepmate.domain.model

data class SignUpData(
    val id: String = "",
    val password: String = "",
    val nickname: String = "",
    val email: String = "",
    val age: Int = 0,
    val height: Int = 0,
    val weight: Int = 0
)

