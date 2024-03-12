package com.stepmate.core

import java.util.regex.Pattern

const val MIN_ID_LENGTH = 4
const val MAX_ID_LENGTH = 12
const val MIN_PASS_LENGTH = 8
const val MAX_PASS_LENGTH = 16
const val MIN_NICKNAME_LENGTH = 2
const val MAX_NICKNAME_LENGTH = 10
const val MIN_AGE_LENGTH = 1
const val MAX_AGE_LENGTH = 3
const val MIN_BODY_LENGTH = 2
const val MAX_BODY_LENGTH = 3
const val MAX_EMAIL_LENGTH = 50
const val MAX_EMAIL_CODE_LENGTH = 6

private const val idPattern = "^[A-Za-z[0-9]_]{$MIN_ID_LENGTH,$MAX_ID_LENGTH}$"
private const val passwordPattern = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[!@#$%^&*])[A-Za-z[0-9][!@#$%^&*]]{$MIN_PASS_LENGTH,$MAX_PASS_LENGTH}$"
private const val nicknamePattern = "^[ㄱ-ㅎ가-힣a-zA-Z0-9]{$MIN_NICKNAME_LENGTH,$MAX_NICKNAME_LENGTH}$"
private const val intPattern = "^[0-9]{$MIN_AGE_LENGTH,$MAX_AGE_LENGTH}$"
private const val bodyPattern = "^[0-9]{$MIN_BODY_LENGTH,$MAX_BODY_LENGTH}$"
private const val emailPattern = "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$"
private const val emailCodePattern = "^[0-9]{$MAX_EMAIL_CODE_LENGTH,$MAX_EMAIL_CODE_LENGTH}$"

fun String.isValidID(): Boolean = Pattern.matches(idPattern,this)

fun String.isValidEmail() : Boolean = Pattern.matches(emailPattern,this)

fun String.isValidPassword(): Boolean = Pattern.matches(passwordPattern,this)

fun String.isValidNickname() : Boolean = Pattern.matches(nicknamePattern,this)

fun String.isValidAge() : Boolean = (Pattern.matches(intPattern,this)) and (this.toInt() in 1..120)

fun String.isValidEmailCode() : Boolean = Pattern.matches(emailCodePattern,this)

fun String.isValidHeight() : Boolean = Pattern.matches(bodyPattern,this) and (this.toInt() in 50 .. 300)

fun String.isValidWeight() : Boolean = Pattern.matches(bodyPattern,this) and (this.toInt() in 10 .. 300)
fun String.passwordMatches(repeated: String): Boolean =
    this == repeated
