package jinproject.stepwalk.home.screen.home.state

import androidx.compose.runtime.Stable

@Stable
internal data class User(
    val name: String,
    val level: Int,
    val age: Int,
    val weight: Int,
    val height: Int,
) {
    companion object {
        fun getInitValues() = User(
            name = "",
            level = 0,
            age = 0,
            weight = 55,
            height = 0,
        )
    }
}

internal fun jinproject.stepwalk.domain.model.user.User.toHomeUserState() = User(
    name = this.name,
    level = this.level,
    age = 0,
    weight = 0,
    height = 0,
)