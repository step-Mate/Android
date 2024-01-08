package com.beank.login.utils

import java.util.regex.Pattern

const val MIN_ID_LENGTH = 4
const val MAX_ID_LENGTH = 12
const val MIN_PASS_LENGTH = 8
const val MAX_PASS_LENGTH = 16
const val MIN_NICKNAME_LENGTH = 2
const val MAX_NICKNAME_LENGTH = 10
const val MIN_AGE_LENGTH = 1
const val MAX_AGE_LENGTH = 3
const val MIN_HEIGHT_LENGTH = 2
const val MAX_HEIGHT_LENGTH = 5
const val MIN_WEIGHT_LENGTH = 2
const val MAX_WEIGHT_LENGTH = 5

private const val idPattern = "^[A-Za-z[0-9]_]{$MIN_ID_LENGTH,$MAX_ID_LENGTH}$"
private const val passwordPattern = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[!@#$%^&*])[A-Za-z[0-9][!@#$%^&*]]{$MIN_PASS_LENGTH,$MAX_PASS_LENGTH}$"
private const val nicknamePattern = "^[ㄱ-ㅎ가-힣a-zA-Z0-9]{$MIN_NICKNAME_LENGTH,$MAX_NICKNAME_LENGTH}$"
private const val intPattern = "^[0-9]{$MIN_AGE_LENGTH,$MAX_AGE_LENGTH}$"
private const val doublePattern = "^[0-9[.]]{$MIN_HEIGHT_LENGTH,$MAX_WEIGHT_LENGTH}$"
private const val emailPattern = "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$"

fun String.isValidID(): Boolean = Pattern.matches(idPattern,this)

fun String.isValidEmail() : Boolean = Pattern.matches(emailPattern,this)

fun String.isValidPassword(): Boolean = Pattern.matches(passwordPattern,this)

fun String.isValidNickname() : Boolean = Pattern.matches(nicknamePattern,this)

fun String.isValidInt() : Boolean = (Pattern.matches(intPattern,this)) and (this.toInt() <= 101)

fun String.isValidDouble() : Boolean {
    if((Pattern.matches(doublePattern,this)) and (this.first() != '.') and (this.last() != '.')){
        if (this.toDouble() <= 300.0)
            return true
    }
    return false
}

fun String.passwordMatches(repeated: String): Boolean =
    this == repeated
