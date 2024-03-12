package com.stepmate.login.screen.signup


import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.stepmate.login.screen.state.Account
import com.stepmate.login.screen.state.SignValid

internal class SignUpStatePreviewParameters : PreviewParameterProvider<Account> {
    override val values: Sequence<Account> = sequenceOf(
        Account(
            time = 0,
            initText = "test",
            initValid = SignValid.blank,
        ),
        Account(
            time = 0,
            initText = "test123",
            initValid = SignValid.notValid,
        ),
        Account(
            time = 0,
            initText = "test123",
            initValid = SignValid.success,
        ),
    )
}

internal class PasswordStatePreviewParameters : PreviewParameterProvider<Account> {
    override val values: Sequence<Account> = sequenceOf(
        Account(
            time = 0,
            initText = "test",
            initValid = SignValid.blank,
        ),
        Account(
            time = 0,
            initText = "test123",
            initValid = SignValid.notValid,
        ),
        Account(
            time = 0,
            initText = "test123",
            initValid = SignValid.notMatch,
        ),
    )
}

internal class EmailStatePreviewParameters : PreviewParameterProvider<List<Account>> {
    override val values: Sequence<List<Account>> = sequenceOf(
        listOf(
            Account(
                time = 0,
                initText = "",
                initValid = SignValid.blank,
            ),
            Account(
                time = 0,
                initText = "",
                initValid = SignValid.blank,
            )
        ),
        listOf(
            Account(
                time = 0,
                initText = "",
                initValid = SignValid.notValid,
            ),
            Account(
                time = 0,
                initText = "",
                initValid = SignValid.blank,
            )
        ),
        listOf(
            Account(
                time = 0,
                initText = "",
                initValid = SignValid.success,
            ),
            Account(
                time = 0,
                initText = "",
                initValid = SignValid.blank,
            )
        ),
        listOf(
            Account(
                time = 0,
                initText = "",
                initValid = SignValid.verifying,
            ),
            Account(
                time = 0,
                initText = "",
                initValid = SignValid.blank,
            )
        ),
        listOf(
            Account(
                time = 0,
                initText = "",
                initValid = SignValid.verifying,
            ),
            Account(
                time = 0,
                initText = "",
                initValid = SignValid.notValid,
            )
        ),
        listOf(
            Account(
                time = 0,
                initText = "",
                initValid = SignValid.verifying,
            ),
            Account(
                time = 0,
                initText = "",
                initValid = SignValid.success,
            )
        ),
    )
}