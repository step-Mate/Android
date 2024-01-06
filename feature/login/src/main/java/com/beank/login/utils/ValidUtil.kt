package com.beank.login.utils

import java.util.regex.Pattern

const val MIN_ID_LENGTH = 4
const val MAX_ID_LENGTH = 12
const val MIN_PASS_LENGTH = 8
const val MAX_PASS_LENGTH = 16

private val idPattern = "^[a-zA-Z0-9_@.]{$MIN_ID_LENGTH,$MAX_ID_LENGTH}*$"
private val passwordPattern = "^.*(?=^.{$MIN_PASS_LENGTH,$MAX_PASS_LENGTH}$)(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[!@#$%^&*]).*$"

fun String.isValidID(): Boolean = Pattern.compile(idPattern).matcher(this).matches()

fun String.isValidPassword(): Boolean = Pattern.compile(passwordPattern).matcher(this).matches()

fun String.passwordMatches(repeated: String): Boolean =
    this == repeated
